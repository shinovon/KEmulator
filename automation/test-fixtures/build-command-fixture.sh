#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SOURCE_DIR="$ROOT_DIR/automation/test-fixtures/src"
BUILD_DIR="$ROOT_DIR/automation/test-fixtures/build"
CLASSES_DIR="$BUILD_DIR/classes"
SOURCES_FILE="$BUILD_DIR/sources.txt"
MANIFEST_FILE="$ROOT_DIR/automation/test-fixtures/command-fixture.mf"
DEFAULT_OUTPUT_JAR="$BUILD_DIR/command-fixture.jar"

usage() {
  cat <<'EOF'
Usage:
  ./automation/test-fixtures/build-command-fixture.sh [RUNTIME_CLASSPATH] [OUTPUT_JAR]

Builds the automation command fixture MIDlet JAR.

Arguments:
  RUNTIME_CLASSPATH  Optional KEmulator compile classpath entry. Auto-detected when omitted.
  OUTPUT_JAR         Optional destination jar path. Defaults to ./automation/test-fixtures/build/command-fixture.jar
EOF
}

if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
  usage
  exit 0
fi

if [[ "$#" -gt 2 ]]; then
  usage >&2
  exit 1
fi

resolve_path() {
  local value="$1"
  case "$value" in
    /*) printf '%s\n' "$value" ;;
    *) printf '%s\n' "$PWD/$value" ;;
  esac
}

find_runtime_classpath() {
  local candidate
  for candidate in \
    "$ROOT_DIR/dist/release-linux/KEmulator.jar" \
    "$ROOT_DIR/KEmulator.jar"
  do
    if [[ -f "$candidate" ]]; then
      printf '%s\n' "$candidate"
      return
    fi
  done

  if [[ -d "$ROOT_DIR/out/classes-linux" ]]; then
    printf '%s\n' "$ROOT_DIR/out/classes-linux"
    return
  fi

  echo "Could not find a runtime classpath. Build a release bundle first or pass KEmulator.jar/out/classes-linux explicitly." >&2
  exit 1
}

if ! command -v javac >/dev/null 2>&1; then
  echo "javac not found" >&2
  exit 1
fi

if ! command -v jar >/dev/null 2>&1; then
  echo "jar not found" >&2
  exit 1
fi

RUNTIME_CLASSPATH="${1:-$(find_runtime_classpath)}"
OUTPUT_JAR="${2:-$DEFAULT_OUTPUT_JAR}"

RUNTIME_CLASSPATH="$(resolve_path "$RUNTIME_CLASSPATH")"
OUTPUT_JAR="$(resolve_path "$OUTPUT_JAR")"

if [[ ! -e "$RUNTIME_CLASSPATH" ]]; then
  echo "Runtime classpath not found: $RUNTIME_CLASSPATH" >&2
  exit 1
fi

if [[ ! -f "$MANIFEST_FILE" ]]; then
  echo "Manifest file not found: $MANIFEST_FILE" >&2
  exit 1
fi

rm -rf -- "$CLASSES_DIR"
mkdir -p -- "$CLASSES_DIR" "$(dirname "$OUTPUT_JAR")"

find -- "$SOURCE_DIR" -type f -name '*.java' | sort > "$SOURCES_FILE"

if ! [[ -s "$SOURCES_FILE" ]]; then
  echo "No fixture sources found under $SOURCE_DIR" >&2
  exit 1
fi

javac \
  -encoding UTF-8 \
  -source 1.4 \
  -target 1.4 \
  -cp "$RUNTIME_CLASSPATH" \
  -d "$CLASSES_DIR" \
  @"$SOURCES_FILE"

jar cfm "$OUTPUT_JAR" "$MANIFEST_FILE" -C "$CLASSES_DIR" .

cat <<EOF
Fixture jar built:
  $OUTPUT_JAR

Classpath used:
  $RUNTIME_CLASSPATH
EOF
