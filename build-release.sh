#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_OUTPUT_DIR="$ROOT_DIR/dist/release-linux"
BUILD_DIR="$ROOT_DIR/.build-release"
CLASSES_DIR="$BUILD_DIR/classes"
SOURCES_FILE="$BUILD_DIR/sources.txt"
MANIFEST_FILE="$ROOT_DIR/src/main/META-INF/MANIFEST.MF"
OUTPUT_MARKER_NAME=".kemu-release-bundle"

usage() {
  cat <<'EOF'
Usage:
  ./build-release.sh [OUTPUT_DIR]

Builds the current Linux release bundle for the Java-first `kemu` CLI:
  - compiles KEmulator from sources
  - creates KEmulator.jar
  - copies kemu.sh
  - copies runtime jars into lib/
  - copies Linux native helpers and support assets into home/

Defaults:
  OUTPUT_DIR = ./dist/release-linux
EOF
}

looks_like_release_dir() {
  local dir="$1"
  [[ -f "$dir/KEmulator.jar" && -f "$dir/kemu.sh" && -d "$dir/lib" && -d "$dir/home" ]]
}

canonical_existing_dir() {
  local dir="$1"
  if [[ ! -d "$dir" ]]; then
    return 1
  fi
  (
    cd -- "$dir"
    pwd -P
  )
}

release_dir_live_processes() {
  local dir="$1"
  local dir_real=""
  local line
  local padded
  local token
  local value
  local value_real
  local matched
  local found=1
  if [[ -d "$dir" ]]; then
    dir_real="$(canonical_existing_dir "$dir" || true)"
  fi
  while IFS= read -r line; do
    padded=" $line "
    case "$padded" in
      *" emulator.automation.controller.AutomationControllerMain "*|*" emulator.automation.worker.AutomationWorkerMain "*)
        matched=1
        for token in $line; do
          case "$token" in
            -Dkemu.root=*|-Dkemu.runtime.root=*)
              value="${token#*=}"
              if [[ "$value" == "$dir" ]]; then
                matched=0
                break
              fi
              if [[ -n "$dir_real" && -d "$value" ]]; then
                value_real="$(canonical_existing_dir "$value" || true)"
                if [[ -n "$value_real" && "$value_real" == "$dir_real" ]]; then
                  matched=0
                  break
                fi
              fi
              ;;
          esac
        done
        if [[ "$matched" -eq 0 ]]; then
          printf '%s\n' "$line"
          found=0
        fi
        ;;
    esac
  done < <(ps -eo pid=,args=)
  return "$found"
}

if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
  usage
  exit 0
fi

if [[ "$#" -gt 1 ]]; then
  usage >&2
  exit 1
fi

OUTPUT_DIR="${1:-$DEFAULT_OUTPUT_DIR}"
case "$OUTPUT_DIR" in
  /*) ;;
  *) OUTPUT_DIR="$PWD/$OUTPUT_DIR" ;;
esac
OUTPUT_PARENT="$(dirname "$OUTPUT_DIR")"
mkdir -p -- "$OUTPUT_PARENT"
OUTPUT_DIR="$(cd "$OUTPUT_PARENT" && pwd)/$(basename "$OUTPUT_DIR")"
OUTPUT_MARKER="$OUTPUT_DIR/$OUTPUT_MARKER_NAME"

if [[ -e "$OUTPUT_DIR" && ! -d "$OUTPUT_DIR" ]]; then
  echo "Output path exists and is not a directory: $OUTPUT_DIR" >&2
  exit 1
fi

if [[ -d "$OUTPUT_DIR" ]]; then
  OUTPUT_DIR_REAL="$(canonical_existing_dir "$OUTPUT_DIR" || true)"
  if [[ -n "${OUTPUT_DIR_REAL:-}" && "$OUTPUT_DIR_REAL" != "$OUTPUT_DIR" ]]; then
    if live_processes="$(release_dir_live_processes "$OUTPUT_DIR_REAL")"; then
      echo "Refusing to rebuild a live release bundle: $OUTPUT_DIR" >&2
      echo "Stop the running kemu controller or worker first." >&2
      printf '%s\n' "$live_processes" >&2
      exit 1
    fi
  fi
  if live_processes="$(release_dir_live_processes "$OUTPUT_DIR")"; then
    echo "Refusing to rebuild a live release bundle: $OUTPUT_DIR" >&2
    echo "Stop the running kemu controller or worker first." >&2
    printf '%s\n' "$live_processes" >&2
    exit 1
  fi
fi

if [[ -d "$OUTPUT_DIR" && ! -L "$OUTPUT_DIR" && -z "$(find -- "$OUTPUT_DIR" -mindepth 1 -maxdepth 1 -print -quit)" ]]; then
  rm -rf -- "$OUTPUT_DIR"
fi

if [[ -d "$OUTPUT_DIR" && ! -f "$OUTPUT_MARKER" ]] && ! looks_like_release_dir "$OUTPUT_DIR"; then
  echo "Refusing to overwrite a non-release directory: $OUTPUT_DIR" >&2
  echo "Remove it manually or choose a different OUTPUT_DIR." >&2
  exit 1
fi

STAGING_DIR="$(mktemp -d "$OUTPUT_PARENT/.kemu-release-staging.XXXXXX")"
cleanup() {
  if [[ -n "${STAGING_DIR:-}" && -d "$STAGING_DIR" ]]; then
    rm -rf -- "$STAGING_DIR"
  fi
}
trap cleanup EXIT

if ! command -v javac >/dev/null 2>&1; then
  echo "javac not found" >&2
  exit 1
fi

if ! command -v jar >/dev/null 2>&1; then
  echo "jar not found" >&2
  exit 1
fi

if [[ ! -f "$MANIFEST_FILE" ]]; then
  echo "Manifest file not found: $MANIFEST_FILE" >&2
  exit 1
fi

SOURCE_DIRS=(
  "$ROOT_DIR/src/main"
  "$ROOT_DIR/src/media"
  "$ROOT_DIR/src/midp"
  "$ROOT_DIR/src/nnapi"
  "$ROOT_DIR/src/nokia"
  "$ROOT_DIR/src/oem"
  "$ROOT_DIR/src/3d"
  "$ROOT_DIR/x64/src/main"
  "$ROOT_DIR/m3g_lwjgl/src"
  "$ROOT_DIR/micro3d_gl/src"
)

rm -rf -- "$BUILD_DIR"
if [[ -d "$OUTPUT_DIR" ]]; then
  rm -rf -- "$OUTPUT_DIR"
fi
mkdir -p -- "$CLASSES_DIR" "$STAGING_DIR/lib" "$STAGING_DIR/home" "$CLASSES_DIR/META-INF"

{
  for dir in "${SOURCE_DIRS[@]}"; do
    find -- "$dir" -type f -name '*.java' -print
  done
} | sort > "$SOURCES_FILE"

if ! [[ -s "$SOURCES_FILE" ]]; then
  echo "No Java sources found for release build" >&2
  exit 1
fi

GIT_REVISION="$(git -C "$ROOT_DIR" describe --tags --always HEAD 2>/dev/null || echo unknown)"
printf 'Manifest-Version: 1.0\nGit-Revision: %s\n\n' "$GIT_REVISION" > "$CLASSES_DIR/META-INF/version.mf"

javac \
  -encoding UTF-8 \
  -source 8 \
  -target 8 \
  -cp "$ROOT_DIR/lib/*:$ROOT_DIR/home/*" \
  -d "$CLASSES_DIR" \
  @"$SOURCES_FILE"

cp -R -- "$ROOT_DIR/src/res/." "$CLASSES_DIR/"
jar cfm "$STAGING_DIR/KEmulator.jar" "$MANIFEST_FILE" -C "$CLASSES_DIR" .

REQUIRED_RELEASE_CLASSES=(
  "javax/microedition/lcdui/Command.class"
  "javax/microedition/lcdui/CommandListener.class"
  "javax/microedition/lcdui/Displayable.class"
  "javax/microedition/lcdui/Screen.class"
)

JAR_CONTENTS_FILE="$BUILD_DIR/jar-contents.txt"
jar tf "$STAGING_DIR/KEmulator.jar" > "$JAR_CONTENTS_FILE"
missing_release_classes=()
for required_class in "${REQUIRED_RELEASE_CLASSES[@]}"; do
  if ! grep -Fxq "$required_class" "$JAR_CONTENTS_FILE"; then
    missing_release_classes+=("$required_class")
  fi
done

if [[ "${#missing_release_classes[@]}" -gt 0 ]]; then
  printf 'Release bundle is missing required runtime classes:\n' >&2
  printf '  %s\n' "${missing_release_classes[@]}" >&2
  exit 1
fi

cp -- "$ROOT_DIR/kemu.sh" "$STAGING_DIR/kemu.sh"
chmod +x -- "$STAGING_DIR/kemu.sh"

shopt -s nullglob

for jar_file in "$ROOT_DIR"/lib/*.jar; do
  cp -- "$jar_file" "$STAGING_DIR/lib/"
done

for jar_file in \
  "$ROOT_DIR"/home/swt-gtk-linux-*.jar \
  "$ROOT_DIR"/home/lwjgl3-swt-linux*.jar \
  "$ROOT_DIR"/home/lwjgl-natives-linux*.jar \
  "$ROOT_DIR"/home/lwjgl-opengl-natives-linux*.jar \
  "$ROOT_DIR"/home/lwjgl-glfw-natives-linux*.jar
do
  cp -- "$jar_file" "$STAGING_DIR/lib/"
done

for native_file in "$ROOT_DIR"/home/*.so "$ROOT_DIR"/home/sensorsimulator.jar; do
  if [[ -e "$native_file" ]]; then
    cp -- "$native_file" "$STAGING_DIR/home/"
  fi
done

if [[ -d "$ROOT_DIR/home/lang" ]]; then
  cp -R -- "$ROOT_DIR/home/lang" "$STAGING_DIR/home/"
fi

if [[ -d "$ROOT_DIR/home/.package" ]]; then
  cp -R -- "$ROOT_DIR/home/.package" "$STAGING_DIR/home/"
fi

shopt -u nullglob

printf 'kemu-release-bundle=1\n' > "$STAGING_DIR/$OUTPUT_MARKER_NAME"
mv -- "$STAGING_DIR" "$OUTPUT_DIR"
STAGING_DIR=""

cat <<EOF
Release bundle built:
  $OUTPUT_DIR

Contents:
  $OUTPUT_DIR/KEmulator.jar
  $OUTPUT_DIR/kemu.sh
  $OUTPUT_DIR/lib/
  $OUTPUT_DIR/home/
EOF
