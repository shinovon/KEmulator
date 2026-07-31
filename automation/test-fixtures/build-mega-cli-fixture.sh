#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SOURCE_FILE="$ROOT_DIR/automation/test-fixtures/src/fixtures/MegaCliFixtureMidlet.java"
BUILD_DIR="$ROOT_DIR/automation/test-fixtures/build-mega-cli"
CLASSES_DIR="$BUILD_DIR/classes"
MANIFEST_FILE="$ROOT_DIR/automation/test-fixtures/mega-cli-fixture.mf"
DEFAULT_OUTPUT_JAR="$BUILD_DIR/mega-cli-fixture.jar"

usage() {
  cat <<'EOF'
Usage:
  ./automation/test-fixtures/build-mega-cli-fixture.sh [RUNTIME_CLASSPATH] [OUTPUT_JAR]

Builds the automation-only mega CLI fixture MIDlet JAR.

Arguments:
  RUNTIME_CLASSPATH  Optional KEmulator compile classpath entry. Auto-detected when omitted.
  OUTPUT_JAR         Optional destination jar path. Defaults to ./automation/test-fixtures/build-mega-cli/mega-cli-fixture.jar
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

if [[ ! -f "$SOURCE_FILE" ]]; then
  echo "Fixture source not found: $SOURCE_FILE" >&2
  exit 1
fi

rm -rf -- "$CLASSES_DIR"
mkdir -p -- "$CLASSES_DIR" "$(dirname "$OUTPUT_JAR")"

javac \
  -encoding UTF-8 \
  -source 1.4 \
  -target 1.4 \
  -cp "$RUNTIME_CLASSPATH" \
  -d "$CLASSES_DIR" \
  "$SOURCE_FILE"

jar cfm "$OUTPUT_JAR" "$MANIFEST_FILE" -C "$CLASSES_DIR" .

cat <<EOF
Fixture jar built:
  $OUTPUT_JAR

Classpath used:
  $RUNTIME_CLASSPATH
EOF
