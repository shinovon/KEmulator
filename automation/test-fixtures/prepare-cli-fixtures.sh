#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PYTHON_BIN="${PYTHON_BIN:-python3}"
DEFAULT_OUTPUT_DIR="$ROOT_DIR/automation/test-fixtures/cli-pack"
COMMAND_BUILD_SCRIPT="$ROOT_DIR/automation/test-fixtures/build-command-fixture.sh"
AUTO_BUILD_SCRIPT="$ROOT_DIR/automation/test-fixtures/build-auto-snapshot-fixture.sh"
MUTABLE_BUILD_SCRIPT="$ROOT_DIR/automation/test-fixtures/build-mutable-title-fixture.sh"
MEGA_BUILD_SCRIPT="$ROOT_DIR/automation/test-fixtures/build-mega-cli-fixture.sh"

usage() {
  cat <<'EOF'
Usage:
  ./automation/test-fixtures/prepare-cli-fixtures.sh [RUNTIME_CLASSPATH] [OUTPUT_DIR]

Builds the strict CLI fixture pack used by shell test suites.

Arguments:
  RUNTIME_CLASSPATH  Optional KEmulator compile classpath entry. Auto-detected when omitted.
  OUTPUT_DIR         Optional destination directory. Defaults to ./automation/test-fixtures/cli-pack
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

write_env_var() {
  local env_file="$1"
  local key="$2"
  local value="$3"
  printf '%s=%q\n' "$key" "$value" >> "$env_file"
}

if ! command -v "$PYTHON_BIN" >/dev/null 2>&1; then
  echo "$PYTHON_BIN not found" >&2
  exit 1
fi

if ! command -v javac >/dev/null 2>&1; then
  echo "javac not found" >&2
  exit 1
fi

if ! command -v jar >/dev/null 2>&1; then
  echo "jar not found" >&2
  exit 1
fi

RUNTIME_CLASSPATH="${1:-$(find_runtime_classpath)}"
OUTPUT_DIR="${2:-$DEFAULT_OUTPUT_DIR}"

RUNTIME_CLASSPATH="$(resolve_path "$RUNTIME_CLASSPATH")"
OUTPUT_DIR="$(resolve_path "$OUTPUT_DIR")"

FIXTURE_JARS_DIR="$OUTPUT_DIR/jars"
DESCRIPTOR_DIR="$OUTPUT_DIR/descriptors"
INVALID_DIR="$OUTPUT_DIR/invalid-archives"
ENV_FILE="$OUTPUT_DIR/fixtures.env"
COMMAND_FIXTURE_JAR="$FIXTURE_JARS_DIR/command-fixture.jar"
AUTO_SNAPSHOT_FIXTURE_JAR="$FIXTURE_JARS_DIR/auto-snapshot-fixture.jar"
MUTABLE_TITLE_FIXTURE_JAR="$FIXTURE_JARS_DIR/mutable-title-fixture.jar"
MEGA_CLI_FIXTURE_JAR="$FIXTURE_JARS_DIR/mega-cli-fixture.jar"

if [[ ! -e "$RUNTIME_CLASSPATH" ]]; then
  echo "Runtime classpath not found: $RUNTIME_CLASSPATH" >&2
  exit 1
fi

rm -rf -- "$OUTPUT_DIR"
mkdir -p -- "$FIXTURE_JARS_DIR" "$DESCRIPTOR_DIR" "$INVALID_DIR/empty-dir"

"$COMMAND_BUILD_SCRIPT" "$RUNTIME_CLASSPATH" "$COMMAND_FIXTURE_JAR"
"$AUTO_BUILD_SCRIPT" "$RUNTIME_CLASSPATH" "$AUTO_SNAPSHOT_FIXTURE_JAR"
"$MUTABLE_BUILD_SCRIPT" "$RUNTIME_CLASSPATH" "$MUTABLE_TITLE_FIXTURE_JAR"
"$MEGA_BUILD_SCRIPT" "$RUNTIME_CLASSPATH" "$MEGA_CLI_FIXTURE_JAR"

"$PYTHON_BIN" - "$COMMAND_FIXTURE_JAR" "$MEGA_CLI_FIXTURE_JAR" "$DESCRIPTOR_DIR" "$INVALID_DIR" <<'PY'
from pathlib import Path
import sys
import zipfile

fixture_jar = Path(sys.argv[1])
mega_jar = Path(sys.argv[2])
descriptor_dir = Path(sys.argv[3])
invalid_dir = Path(sys.argv[4])
nested_descriptor_dir = descriptor_dir / "nested"
parent_jars_dir = descriptor_dir / "jars"
bom_manifest_jar = descriptor_dir / "bom-manifest.jar"
multi_midlet_jar = descriptor_dir / "multi-midlet.jar"
manifest_extra_jar = descriptor_dir / "manifest-extra.jar"
mega_space_jar = descriptor_dir / "mega space.jar"
mega_parent_jar = parent_jars_dir / "mega-parent.jar"
mega_multi_midlet_jar = descriptor_dir / "mega-multi-midlet.jar"
no_manifest_jar = invalid_dir / "no-manifest.jar"
missing_class_jar = invalid_dir / "missing-class.jar"

nested_descriptor_dir.mkdir(parents=True, exist_ok=True)
parent_jars_dir.mkdir(parents=True, exist_ok=True)

def with_identity_title(source_path, target_path, title):
    with zipfile.ZipFile(source_path, "r") as source, zipfile.ZipFile(target_path, "w") as target:
        for entry in source.infolist():
            data = source.read(entry.filename)
            target.writestr(entry, data)
        target.writestr("fixtures/menu-title.txt", (title + "\n").encode("utf-8"))

encoded_jar = descriptor_dir / "encoded.jar"
with_identity_title(fixture_jar, encoded_jar, "Descriptor Target Menu")
encoded_space_jar = descriptor_dir / "encoded space.jar"
with_identity_title(fixture_jar, encoded_space_jar, "Encoded Space Target Menu")
parent_jar = parent_jars_dir / "parent.jar"
with_identity_title(fixture_jar, parent_jar, "Parent Relative Target Menu")
with_identity_title(mega_jar, mega_space_jar, "Mega Space Target Menu")
with_identity_title(mega_jar, mega_parent_jar, "Mega Parent Target Menu")

with zipfile.ZipFile(fixture_jar, "r") as source, zipfile.ZipFile(manifest_extra_jar, "w") as target:
    for entry in source.infolist():
        if entry.filename.upper() == "META-INF/MANIFEST.MF":
            continue
        target.writestr(entry, source.read(entry.filename))
    target.writestr(
        "META-INF/MANIFEST.MF",
        (
            "Manifest-Version: 1.0\r\n"
            "MIDlet-1: Descriptor Fixture,,fixtures.CommandFixtureMidlet\r\n"
            "MIDlet-2: Hidden Manifest Fixture,,fixtures.CommandFixtureMidlet\r\n"
            "MIDlet-Name: Descriptor Fixture\r\n"
            "MIDlet-Vendor: KEmulator\r\n"
            "MIDlet-Version: 1.0.0\r\n"
            "MicroEdition-Configuration: CLDC-1.1\r\n"
            "MicroEdition-Profile: MIDP-2.0\r\n"
            "\r\n"
        ).encode("utf-8"),
    )

with zipfile.ZipFile(fixture_jar, "r") as source, zipfile.ZipFile(bom_manifest_jar, "w") as target:
    for entry in source.infolist():
        data = source.read(entry.filename)
        if entry.filename.upper() == "META-INF/MANIFEST.MF":
            data = b"\xef\xbb\xbf" + data
        target.writestr(entry, data)

with zipfile.ZipFile(fixture_jar, "r") as source, zipfile.ZipFile(multi_midlet_jar, "w") as target:
    for entry in source.infolist():
        if entry.filename.upper() == "META-INF/MANIFEST.MF":
            continue
        target.writestr(entry, source.read(entry.filename))
    target.writestr(
        "META-INF/MANIFEST.MF",
        (
            "Manifest-Version: 1.0\r\n"
            "MIDlet-1: Command Fixture,,fixtures.CommandFixtureMidlet\r\n"
            "MIDlet-2: Mutable Title Fixture,,fixtures.MutableTitleFixtureMidlet\r\n"
            "MIDlet-Name: Multi Midlet Fixture\r\n"
            "MIDlet-Vendor: KEmulator\r\n"
            "MIDlet-Version: 1.0.0\r\n"
            "MicroEdition-Configuration: CLDC-1.1\r\n"
            "MicroEdition-Profile: MIDP-2.0\r\n"
            "\r\n"
        ).encode("utf-8"),
    )

with zipfile.ZipFile(mega_jar, "r") as source, zipfile.ZipFile(mega_multi_midlet_jar, "w") as target:
    for entry in source.infolist():
        if entry.filename.upper() == "META-INF/MANIFEST.MF":
            continue
        target.writestr(entry, source.read(entry.filename))
    target.writestr(
        "META-INF/MANIFEST.MF",
        (
            "Manifest-Version: 1.0\r\n"
            "MIDlet-1: Command Fixture,,fixtures.CommandFixtureMidlet\r\n"
            "MIDlet-2: Mega CLI Fixture,,fixtures.MegaCliFixtureMidlet\r\n"
            "MIDlet-Name: Mega Multi Midlet Fixture\r\n"
            "MIDlet-Vendor: KEmulator\r\n"
            "MIDlet-Version: 1.0.0\r\n"
            "MicroEdition-Configuration: CLDC-1.1\r\n"
            "MicroEdition-Profile: MIDP-2.0\r\n"
            "\r\n"
        ).encode("utf-8"),
    )

with zipfile.ZipFile(no_manifest_jar, "w") as target:
    target.writestr("fixtures/empty.txt", b"empty\n")

with zipfile.ZipFile(missing_class_jar, "w") as target:
    target.writestr(
        "META-INF/MANIFEST.MF",
        (
            "Manifest-Version: 1.0\r\n"
            "MIDlet-1: Missing Class Fixture,,fixtures.DoesNotExistMidlet\r\n"
            "MIDlet-Name: Missing Class Fixture\r\n"
            "MIDlet-Vendor: KEmulator\r\n"
            "MIDlet-Version: 1.0.0\r\n"
            "MicroEdition-Configuration: CLDC-1.1\r\n"
            "MicroEdition-Profile: MIDP-2.0\r\n"
            "\r\n"
        ).encode("utf-8"),
    )

(descriptor_dir / "good.jad").write_text(
    "MIDlet-1: Descriptor Fixture,,fixtures.CommandFixtureMidlet\n"
    "MIDlet-Name: Descriptor Fixture\n"
    "MIDlet-Jar-URL: encoded.jar\n",
    encoding="utf-8",
)

(descriptor_dir / "bad.jad").write_text(
    "MIDlet-1: Missing Fixture,,fixtures.CommandFixtureMidlet\n"
    "MIDlet-Name: Missing Fixture\n"
    "MIDlet-Jar-URL: missing.jar\n",
    encoding="utf-8",
)

(descriptor_dir / "encoded-space.jad").write_text(
    "MIDlet-1: Encoded Descriptor Fixture,,fixtures.CommandFixtureMidlet\n"
    "MIDlet-Name: Encoded Descriptor Fixture\n"
    "MIDlet-Jar-URL: encoded%20space.jar\n",
    encoding="utf-8",
)

(nested_descriptor_dir / "parent-relative.jad").write_text(
    "MIDlet-1: Parent Relative Fixture,,fixtures.CommandFixtureMidlet\n"
    "MIDlet-Name: Parent Relative Fixture\n"
    "MIDlet-Jar-URL: ../jars/parent.jar\n",
    encoding="utf-8",
)

(nested_descriptor_dir / "parent-relative-encoded.jad").write_text(
    "MIDlet-1: Parent Relative Encoded Fixture,,fixtures.CommandFixtureMidlet\n"
    "MIDlet-Name: Parent Relative Encoded Fixture\n"
    "MIDlet-Jar-URL: ..%2Fjars%2Fparent.jar\n",
    encoding="utf-8",
)

(descriptor_dir / "mega-space.jad").write_text(
    "MIDlet-1: Mega Space Fixture,,fixtures.MegaCliFixtureMidlet\n"
    "MIDlet-Name: Mega Space Fixture\n"
    "MIDlet-Jar-URL: mega%20space.jar\n",
    encoding="utf-8",
)

(nested_descriptor_dir / "mega-parent-relative.jad").write_text(
    "MIDlet-1: Mega Parent Fixture,,fixtures.MegaCliFixtureMidlet\n"
    "MIDlet-Name: Mega Parent Fixture\n"
    "MIDlet-Jar-URL: ../jars/mega-parent.jar\n",
    encoding="utf-8",
)

(descriptor_dir / "manifest-extra.jad").write_text(
    "MIDlet-1: Descriptor Fixture,,fixtures.CommandFixtureMidlet\n"
    "MIDlet-Name: Descriptor Fixture\n"
    "MIDlet-Jar-URL: manifest-extra.jar\n",
    encoding="utf-8",
)
PY

cp -- "$COMMAND_FIXTURE_JAR" "$DESCRIPTOR_DIR/--fixture.jar"
cp -- "$MEGA_CLI_FIXTURE_JAR" "$DESCRIPTOR_DIR/--mega.jar"
printf 'not a jar\n' > "$INVALID_DIR/plain-text.jar"
jar cf "$INVALID_DIR/empty.jar" -C "$INVALID_DIR/empty-dir" .

: > "$ENV_FILE"
write_env_var "$ENV_FILE" "CLI_FIXTURE_ROOT" "$OUTPUT_DIR"
write_env_var "$ENV_FILE" "FIXTURES_ENV" "$ENV_FILE"
write_env_var "$ENV_FILE" "COMMAND_FIXTURE_JAR" "$COMMAND_FIXTURE_JAR"
write_env_var "$ENV_FILE" "AUTO_SNAPSHOT_FIXTURE_JAR" "$AUTO_SNAPSHOT_FIXTURE_JAR"
write_env_var "$ENV_FILE" "MUTABLE_TITLE_FIXTURE_JAR" "$MUTABLE_TITLE_FIXTURE_JAR"
write_env_var "$ENV_FILE" "MEGA_CLI_FIXTURE_JAR" "$MEGA_CLI_FIXTURE_JAR"
write_env_var "$ENV_FILE" "DESCRIPTOR_DIR" "$DESCRIPTOR_DIR"
write_env_var "$ENV_FILE" "INVALID_DIR" "$INVALID_DIR"
write_env_var "$ENV_FILE" "DASH_PREFIXED_JAR" "$DESCRIPTOR_DIR/--fixture.jar"
write_env_var "$ENV_FILE" "DASH_PREFIXED_MEGA_JAR" "$DESCRIPTOR_DIR/--mega.jar"
write_env_var "$ENV_FILE" "GOOD_JAD" "$DESCRIPTOR_DIR/good.jad"
write_env_var "$ENV_FILE" "BAD_JAD" "$DESCRIPTOR_DIR/bad.jad"
write_env_var "$ENV_FILE" "ENCODED_SPACE_JAD" "$DESCRIPTOR_DIR/encoded-space.jad"
write_env_var "$ENV_FILE" "PARENT_RELATIVE_JAD" "$DESCRIPTOR_DIR/nested/parent-relative.jad"
write_env_var "$ENV_FILE" "PARENT_RELATIVE_ENCODED_JAD" "$DESCRIPTOR_DIR/nested/parent-relative-encoded.jad"
write_env_var "$ENV_FILE" "MEGA_SPACE_JAD" "$DESCRIPTOR_DIR/mega-space.jad"
write_env_var "$ENV_FILE" "MEGA_PARENT_RELATIVE_JAD" "$DESCRIPTOR_DIR/nested/mega-parent-relative.jad"
write_env_var "$ENV_FILE" "MANIFEST_EXTRA_JAR" "$DESCRIPTOR_DIR/manifest-extra.jar"
write_env_var "$ENV_FILE" "MANIFEST_EXTRA_JAD" "$DESCRIPTOR_DIR/manifest-extra.jad"
write_env_var "$ENV_FILE" "BOM_MANIFEST_JAR" "$DESCRIPTOR_DIR/bom-manifest.jar"
write_env_var "$ENV_FILE" "MULTI_MIDLET_JAR" "$DESCRIPTOR_DIR/multi-midlet.jar"
write_env_var "$ENV_FILE" "MEGA_MULTI_MIDLET_JAR" "$DESCRIPTOR_DIR/mega-multi-midlet.jar"
write_env_var "$ENV_FILE" "PLAIN_TEXT_JAR" "$INVALID_DIR/plain-text.jar"
write_env_var "$ENV_FILE" "EMPTY_JAR" "$INVALID_DIR/empty.jar"
write_env_var "$ENV_FILE" "NO_MANIFEST_JAR" "$INVALID_DIR/no-manifest.jar"
write_env_var "$ENV_FILE" "MISSING_CLASS_JAR" "$INVALID_DIR/missing-class.jar"

cat <<EOF
CLI fixture pack prepared:
  $OUTPUT_DIR

Fixture env:
  $ENV_FILE

Runtime classpath:
  $RUNTIME_CLASSPATH
EOF
