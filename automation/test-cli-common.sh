#!/usr/bin/env bash

PYTHON_BIN="${PYTHON_BIN:-python3}"

if ! declare -p CLI_COMMAND_COVERAGE >/dev/null 2>&1; then
  declare -gA CLI_COMMAND_COVERAGE=()
fi

resolve_path() {
  local value="$1"
  case "$value" in
    /*) printf '%s\n' "$value" ;;
    *) printf '%s\n' "$PWD/$value" ;;
  esac
}

test_fail() {
  echo "$1" >&2
  exit 1
}

require_python() {
  if ! command -v "$PYTHON_BIN" >/dev/null 2>&1; then
    test_fail "$PYTHON_BIN not found"
  fi
}

mark_command_covered() {
  CLI_COMMAND_COVERAGE["$1"]=1
}

require_command_coverage() {
  local missing=()
  local command_name
  for command_name in "$@"; do
    if [[ -z "${CLI_COMMAND_COVERAGE[$command_name]:-}" ]]; then
      missing+=("$command_name")
    fi
  done
  if [[ "${#missing[@]}" -gt 0 ]]; then
    test_fail "Missing CLI command coverage: ${missing[*]}"
  fi
}

json_assert_ok() {
  local payload="$1"
  local expected_command="${2:-}"
  PAYLOAD="$payload" "$PYTHON_BIN" - "$expected_command" <<'PY'
import json
import os
import sys

expected = sys.argv[1]
data = json.loads(os.environ["PAYLOAD"])

if not data.get("ok"):
    error = data.get("error") or {}
    raise SystemExit("expected ok response, got %s: %s" % (error.get("code"), error.get("message")))

if expected and data.get("command") != expected:
    raise SystemExit("expected command %r, got %r" % (expected, data.get("command")))
PY
}

json_assert_error_code() {
  local payload="$1"
  local expected_code="$2"
  PAYLOAD="$payload" "$PYTHON_BIN" - "$expected_code" <<'PY'
import json
import os
import sys

expected = sys.argv[1]
data = json.loads(os.environ["PAYLOAD"])
error = data.get("error") or {}

if data.get("ok"):
    raise SystemExit("expected failure response, got ok")
if error.get("code") != expected:
    raise SystemExit("expected error code %r, got %r" % (expected, error.get("code")))
PY
}

json_expect_field() {
  local payload="$1"
  local path="$2"
  local expected="$3"
  PAYLOAD="$payload" "$PYTHON_BIN" - "$path" "$expected" <<'PY'
import json
import os
import sys

path = [segment for segment in sys.argv[1].split(".") if segment]
expected = sys.argv[2]
current = json.loads(os.environ["PAYLOAD"])

for segment in path:
    if segment.isdigit():
        current = current[int(segment)]
    else:
        current = current[segment]

if isinstance(current, bool):
    current_value = "true" if current else "false"
elif current is None:
    current_value = "null"
else:
    current_value = str(current)

if current_value != expected:
    raise SystemExit("expected %s=%r, got %r" % (".".join(path), expected, current_value))
PY
}

json_expect_prefix() {
  local payload="$1"
  local path="$2"
  local expected_prefix="$3"
  PAYLOAD="$payload" "$PYTHON_BIN" - "$path" "$expected_prefix" <<'PY'
import json
import os
import sys

path = [segment for segment in sys.argv[1].split(".") if segment]
expected_prefix = sys.argv[2]
current = json.loads(os.environ["PAYLOAD"])

for segment in path:
    if segment.isdigit():
        current = current[int(segment)]
    else:
        current = current[segment]

if current is None:
    raise SystemExit("expected %s to start with %r, got null" % (".".join(path), expected_prefix))

current_value = str(current)
if not current_value.startswith(expected_prefix):
    raise SystemExit("expected %s to start with %r, got %r" % (".".join(path), expected_prefix, current_value))
PY
}

json_expect_contains() {
  local payload="$1"
  local path="$2"
  local expected_fragment="$3"
  PAYLOAD="$payload" "$PYTHON_BIN" - "$path" "$expected_fragment" <<'PY'
import json
import os
import sys

path = [segment for segment in sys.argv[1].split(".") if segment]
expected = sys.argv[2]
current = json.loads(os.environ["PAYLOAD"])

for segment in path:
    if segment.isdigit():
        current = current[int(segment)]
    else:
        current = current[segment]

current_value = str(current)
if expected not in current_value:
    raise SystemExit("expected %s to contain %r, got %r" % (".".join(path), expected, current_value))
PY
}

json_expect_missing_field() {
  local payload="$1"
  local path="$2"
  PAYLOAD="$payload" "$PYTHON_BIN" - "$path" <<'PY'
import json
import os
import sys

path = [segment for segment in sys.argv[1].split(".") if segment]
current = json.loads(os.environ["PAYLOAD"])

for segment in path:
    if segment.isdigit():
        index = int(segment)
        if not isinstance(current, list) or index >= len(current):
            sys.exit(0)
        current = current[index]
    else:
        if not isinstance(current, dict) or segment not in current:
            sys.exit(0)
        current = current[segment]

raise SystemExit("expected %s to be absent, but it was present" % ".".join(path))
PY
}

json_expect_type() {
  local payload="$1"
  local path="$2"
  local expected_type="$3"
  PAYLOAD="$payload" "$PYTHON_BIN" - "$path" "$expected_type" <<'PY'
import json
import os
import sys

path = [segment for segment in sys.argv[1].split(".") if segment]
expected = sys.argv[2]
current = json.loads(os.environ["PAYLOAD"])

for segment in path:
    if segment.isdigit():
        current = current[int(segment)]
    else:
        current = current[segment]

if current is None:
    actual = "null"
elif isinstance(current, bool):
    actual = "boolean"
elif isinstance(current, int) or isinstance(current, float):
    actual = "number"
elif isinstance(current, str):
    actual = "string"
elif isinstance(current, list):
    actual = "array"
elif isinstance(current, dict):
    actual = "object"
else:
    actual = type(current).__name__

if actual != expected:
    raise SystemExit("expected %s to be %s, got %s" % (".".join(path), expected, actual))
PY
}

json_expect_number_between() {
  local payload="$1"
  local path="$2"
  local min_value="$3"
  local max_value="$4"
  PAYLOAD="$payload" "$PYTHON_BIN" - "$path" "$min_value" "$max_value" <<'PY'
import json
import os
import sys

path = [segment for segment in sys.argv[1].split(".") if segment]
minimum = float(sys.argv[2])
maximum = float(sys.argv[3])
current = json.loads(os.environ["PAYLOAD"])

for segment in path:
    if segment.isdigit():
        current = current[int(segment)]
    else:
        current = current[segment]

if not isinstance(current, (int, float)):
    raise SystemExit("expected %s to be numeric, got %r" % (".".join(path), current))
if current < minimum or current > maximum:
    raise SystemExit("expected %s between %s and %s, got %s" % (".".join(path), minimum, maximum, current))
PY
}

json_assert_error_shape() {
  local payload="$1"
  PAYLOAD="$payload" "$PYTHON_BIN" - <<'PY'
import json
import os

data = json.loads(os.environ["PAYLOAD"])
if data.get("ok"):
    raise SystemExit("expected error payload, got ok")
if not isinstance(data.get("command"), str) or not data.get("command"):
    raise SystemExit("expected non-empty command string in error payload")
error = data.get("error")
if not isinstance(error, dict):
    raise SystemExit("expected error object")
if not isinstance(error.get("code"), str) or not error.get("code"):
    raise SystemExit("expected non-empty error.code")
if not isinstance(error.get("message"), str) or not error.get("message"):
    raise SystemExit("expected non-empty error.message")
if "details" in error and error["details"] is None:
    raise SystemExit("error.details must be omitted or non-null")
PY
}

json_expect_list_length() {
  local payload="$1"
  local path="$2"
  local expected="$3"
  PAYLOAD="$payload" "$PYTHON_BIN" - "$path" "$expected" <<'PY'
import json
import os
import sys

path = [segment for segment in sys.argv[1].split(".") if segment]
expected = int(sys.argv[2])
current = json.loads(os.environ["PAYLOAD"])

for segment in path:
    if segment.isdigit():
        current = current[int(segment)]
    else:
        current = current[segment]

if not isinstance(current, list):
    raise SystemExit("expected %s to be a list, got %r" % (".".join(path), type(current).__name__))
if len(current) != expected:
    raise SystemExit("expected %s length %s, got %s" % (".".join(path), expected, len(current)))
PY
}

json_get() {
  local payload="$1"
  local path="$2"
  PAYLOAD="$payload" "$PYTHON_BIN" - "$path" <<'PY'
import json
import os
import sys

path = [segment for segment in sys.argv[1].split(".") if segment]
current = json.loads(os.environ["PAYLOAD"])

for segment in path:
    if segment.isdigit():
        current = current[int(segment)]
    else:
        current = current[segment]

if isinstance(current, bool):
    sys.stdout.write("true" if current else "false")
elif current is None:
    sys.stdout.write("null")
elif isinstance(current, (list, dict)):
    sys.stdout.write(json.dumps(current, ensure_ascii=False))
else:
    sys.stdout.write(str(current))
PY
}

png_expect_size() {
  local path="$1"
  local expected_width="$2"
  local expected_height="$3"
  "$PYTHON_BIN" - "$path" "$expected_width" "$expected_height" <<'PY'
import struct
import sys

path = sys.argv[1]
expected_width = int(sys.argv[2])
expected_height = int(sys.argv[3])

with open(path, "rb") as fh:
    data = fh.read(24)

if len(data) < 24:
    raise SystemExit("png too small: %s" % path)
if data[:8] != b"\x89PNG\r\n\x1a\n":
    raise SystemExit("png signature mismatch: %s" % path)
width, height = struct.unpack(">II", data[16:24])
if width != expected_width or height != expected_height:
    raise SystemExit("expected png %sx%s, got %sx%s" % (expected_width, expected_height, width, height))
PY
}

find_command_id() {
  local payload="$1"
  local wanted_text="$2"
  PAYLOAD="$payload" "$PYTHON_BIN" - "$wanted_text" <<'PY'
import json
import os
import sys

wanted = sys.argv[1]
commands = json.loads(os.environ["PAYLOAD"])["result"]["displayable"]["commands"]

for command in commands:
    text = command.get("text") or command.get("label") or ""
    if text == wanted:
        sys.stdout.write(str(command["id"]))
        break
else:
    raise SystemExit("command not found: %s" % wanted)
PY
}

run_kemu_json() {
  (
    cd "$RELEASE_DIR"
    ./kemu.sh "$@" --json
  )
}

run_kemu_json_allow_failure() {
  local output
  local status
  set +e
  output="$(
    (
      cd "$RELEASE_DIR"
      ./kemu.sh "$@" --json
    )
  )"
  status=$?
  set -e
  printf '%s' "$output"
  return "$status"
}

wait_for_status_field() {
  local path="$1"
  local expected="$2"
  local attempts="${3:-40}"
  local delay="${4:-0.25}"
  local last_payload=""
  local actual=""

  for ((i = 0; i < attempts; i++)); do
    last_payload="$(run_kemu_json status)"
    json_assert_ok "$last_payload" "status"
    actual="$(json_get "$last_payload" "$path")"
    if [[ "$actual" == "$expected" ]]; then
      printf '%s' "$last_payload"
      return 0
    fi
    sleep "$delay"
  done

  echo "Timed out waiting for $path=$expected; last value was ${actual:-<empty>}" >&2
  if [[ -n "$last_payload" ]]; then
    echo "$last_payload" >&2
  fi
  return 1
}
