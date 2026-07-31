#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DEFAULT_RELEASE_PREFIX="$ROOT_DIR/dist/release-linux-cli-gauntlet"
TEMP_RELEASE_DIR_CREATED=0
PYTHON_BIN="${PYTHON_BIN:-python3}"
LAST_OUTPUT=""

usage() {
  cat <<'EOF'
Usage:
  ./automation/test-cli-gauntlet.sh [RELEASE_DIR]

Builds an isolated Linux release bundle, prepares the expanded automation-only
fixture pack, and runs a broad CLI gauntlet over parser, runtime, worker,
permission, stale-revision, screenshot, logs, and recovery paths.
EOF
}

if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
  usage
  exit 0
fi

if [[ "$#" -gt 1 ]]; then
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

allocate_release_dir() {
  local temp_dir
  temp_dir="$(mktemp -d "$DEFAULT_RELEASE_PREFIX.XXXXXX")"
  rmdir -- "$temp_dir"
  printf '%s\n' "$temp_dir"
}

print_section() {
  printf '\n[%s]\n' "$1"
}

if [[ "$#" -eq 0 ]]; then
  RELEASE_DIR="$(allocate_release_dir)"
  TEMP_RELEASE_DIR_CREATED=1
else
  RELEASE_DIR="$(resolve_path "$1")"
fi

TEST_DIR="$RELEASE_DIR/automation/cli-gauntlet"
FIXTURE_ROOT="$TEST_DIR/fixtures"
CAPTURE_FILE="$TEST_DIR/mega-capture.png"
CAPTURE_DEAD_FILE="$TEST_DIR/dead-worker.png"

cleanup() {
  if [[ -x "$RELEASE_DIR/kemu.sh" ]]; then
    (
      cd "$RELEASE_DIR"
      ./kemu.sh stop --force --json >/dev/null 2>&1 || true
    )
  fi
  if [[ "$TEMP_RELEASE_DIR_CREATED" -eq 1 ]]; then
    rm -rf -- "$RELEASE_DIR"
  fi
}

trap cleanup EXIT

source "$ROOT_DIR/automation/test-cli-common.sh"
require_python

expect_ok() {
  local expected_command="$1"
  local status
  shift
  set +e
  LAST_OUTPUT="$(run_kemu_json "$@")"
  status=$?
  set -e
  if [[ "$status" -ne 0 ]]; then
    test_fail "Expected $* to succeed, got exit $status: $LAST_OUTPUT"
  fi
  json_assert_ok "$LAST_OUTPUT" "$expected_command"
  mark_command_covered "$expected_command"
}

expect_error() {
  local expected_code="$1"
  shift
  if LAST_OUTPUT="$(run_kemu_json_allow_failure "$@")"; then
    test_fail "Expected $* to fail with $expected_code, got success: $LAST_OUTPUT"
  fi
  json_assert_error_code "$LAST_OUTPUT" "$expected_code"
  json_assert_error_shape "$LAST_OUTPUT"
}

expect_error_message() {
  local expected_code="$1"
  local expected_message="$2"
  shift 2
  expect_error "$expected_code" "$@"
  json_expect_field "$LAST_OUTPUT" "error.message" "$expected_message"
}

expect_command_help() {
  local topic="$1"
  shift
  expect_ok "help" help "$@"
  json_expect_field "$LAST_OUTPUT" "result.topic" "$topic"
  json_expect_contains "$LAST_OUTPUT" "result.usage" "Usage: kemu $topic"
}

open_mega() {
  expect_ok "open" open "$MEGA_CLI_FIXTURE_JAR" --headless
  json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Mega menu"
  json_expect_field "$LAST_OUTPUT" "result.ready" "true"
  json_expect_field "$LAST_OUTPUT" "result.displayable.kind" "list"
  json_expect_missing_field "$LAST_OUTPUT" "result.gamePath"
  json_expect_missing_field "$LAST_OUTPUT" "result.gameName"
}

close_if_open() {
  run_kemu_json close >/dev/null 2>&1 || true
}

controller_call_json() {
  local operation="$1"
  local status_payload
  local host
  local port
  status_payload="$(run_kemu_json status)"
  json_assert_ok "$status_payload" "status"
  host="$(json_get "$status_payload" "result.host")"
  port="$(json_get "$status_payload" "result.port")"
  "$PYTHON_BIN" - "$host" "$port" "$operation" <<'PY'
import json
import socket
import sys

host = sys.argv[1]
port = int(sys.argv[2])
operation = sys.argv[3]
request = {"id": 1, "op": operation, "args": {}}
with socket.create_connection((host, port), timeout=5.0) as sock:
    sock.sendall((json.dumps(request) + "\n").encode("utf-8"))
    data = b""
    while not data.endswith(b"\n"):
        chunk = sock.recv(65536)
        if not chunk:
            break
        data += chunk
print(data.decode("utf-8").strip())
PY
}

print_section "Build release bundle"
"$ROOT_DIR/build-release.sh" "$RELEASE_DIR"
mkdir -p -- "$TEST_DIR"

print_section "Prepare expanded fixture pack"
"$ROOT_DIR/automation/test-fixtures/prepare-cli-fixtures.sh" "$RELEASE_DIR/KEmulator.jar" "$FIXTURE_ROOT"
source "$FIXTURE_ROOT/fixtures.env"

print_section "Help and parser surface"
expect_ok "help" help
json_expect_contains "$LAST_OUTPUT" "result.usage" "Path-first workflow is canonical: inspect/open <path>."
expect_command_help "start" start
expect_command_help "status" status
expect_command_help "stop" stop
expect_command_help "logs" logs
expect_command_help "inspect" inspect
expect_command_help "open" open
expect_command_help "close" close
expect_command_help "state" state
expect_command_help "observe" observe
expect_command_help "screenshot" screenshot
expect_command_help "wait" wait
expect_command_help "key" key
expect_command_help "pointer" pointer
expect_command_help "drag" drag
expect_command_help "command" command
expect_command_help "command run" command run
expect_command_help "permission" permission

expect_error "UNKNOWN_COMMAND" nope
expect_error "USAGE_ERROR" command
mark_command_covered "command"
expect_error "USAGE_ERROR" command nope
expect_error_message "USAGE_ERROR" "Duplicate option: --headless." start --headless --headless
expect_error_message "USAGE_ERROR" "Conflicting options: --headless and --visible." start --headless --visible
expect_error_message "USAGE_ERROR" "Duplicate option: --size." start --size 240x320 --size 176x208
expect_error "USAGE_ERROR" start --size widex320
expect_error "UNKNOWN_RUNTIME" start --runtime nope

print_section "Removed legacy surface"
expect_error "UNKNOWN_COMMAND" wait 1
expect_error "UNKNOWN_COMMAND" key FIRE
expect_error "UNKNOWN_COMMAND" tap 10 20
expect_error "UNKNOWN_COMMAND" logs worker
expect_error "USAGE_ERROR" command run 1 --snapshot 1

print_section "Controller lifecycle"
expect_ok "start" start --headless --runtime release --size 240x320
json_expect_field "$LAST_OUTPUT" "result.runtime" "release"
expect_ok "status" status
json_expect_field "$LAST_OUTPUT" "result.running" "true"
expect_ok "state" state
json_expect_field "$LAST_OUTPUT" "result.active" "false"
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.active" "false"
expect_error "NO_ACTIVE_APP" logs cursor

print_section "Inspection fixtures"
expect_ok "inspect" inspect "$MEGA_CLI_FIXTURE_JAR"
json_expect_field "$LAST_OUTPUT" "result.displayName" "Mega CLI Fixture"
json_expect_list_length "$LAST_OUTPUT" "result.midlets" "1"
expect_ok "inspect" inspect "$MEGA_SPACE_JAD"
json_expect_field "$LAST_OUTPUT" "result.displayName" "Mega Space Fixture"
expect_ok "inspect" inspect "$MEGA_PARENT_RELATIVE_JAD"
json_expect_field "$LAST_OUTPUT" "result.displayName" "Mega Parent Fixture"
expect_ok "inspect" inspect "$MEGA_MULTI_MIDLET_JAR"
json_expect_field "$LAST_OUTPUT" "result.displayName" "Mega Multi Midlet Fixture"
json_expect_list_length "$LAST_OUTPUT" "result.midlets" "2"
expect_error "UNSUPPORTED_INPUT" inspect "$PLAIN_TEXT_JAR"
expect_error "UNSUPPORTED_INPUT" inspect "$EMPTY_JAR"
expect_error "UNSUPPORTED_INPUT" inspect "$NO_MANIFEST_JAR"
expect_ok "inspect" inspect "$MISSING_CLASS_JAR"
json_expect_field "$LAST_OUTPUT" "result.displayName" "Missing Class Fixture"

print_section "Open variants and input path edges"
expect_error "USAGE_ERROR" open --headless
expect_error "USAGE_ERROR" open "$MEGA_CLI_FIXTURE_JAR" --midlet
expect_error_message "USAGE_ERROR" "Duplicate option: --midlet." open "$MEGA_CLI_FIXTURE_JAR" --midlet 1 --midlet 1
expect_error_message "USAGE_ERROR" "Conflicting options: --headless and --visible." open "$MEGA_CLI_FIXTURE_JAR" --headless --visible
expect_error "MIDLET_SELECTION_REQUIRED" open "$MEGA_MULTI_MIDLET_JAR" --headless
expect_error "UNKNOWN_MIDLET" open "$MEGA_MULTI_MIDLET_JAR" --midlet 3 --headless

expect_ok "open" open -- "$DASH_PREFIXED_MEGA_JAR" --headless
json_expect_field "$LAST_OUTPUT" "result.app.displayName" "Mega CLI Fixture"
expect_ok "close" close
json_expect_field "$LAST_OUTPUT" "result.closed" "true"

expect_ok "open" open "$MEGA_MULTI_MIDLET_JAR" --midlet 2 --headless
json_expect_field "$LAST_OUTPUT" "result.app.midletName" "Mega CLI Fixture"
expect_ok "close" close

expect_ok "open" open "$MEGA_SPACE_JAD" --headless
json_expect_field "$LAST_OUTPUT" "result.app.displayName" "Mega Space Fixture"
expect_ok "close" close

expect_ok "open" open "$MEGA_PARENT_RELATIVE_JAD" --headless
json_expect_field "$LAST_OUTPUT" "result.app.displayName" "Mega Parent Fixture"
expect_error "APP_ALREADY_OPEN" open "$MEGA_CLI_FIXTURE_JAR" --headless
expect_ok "close" close

print_section "Mega runtime state and revision-gated commands"
open_mega
expect_ok "state" state
json_expect_field "$LAST_OUTPUT" "result.active" "true"
json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Mega menu"
json_expect_field "$LAST_OUTPUT" "result.displayable.kind" "list"
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.active" "true"
json_expect_field "$LAST_OUTPUT" "result.schemaVersion" "3"
json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Mega menu"
json_expect_field "$LAST_OUTPUT" "result.displayable.kind" "list"
json_expect_type "$LAST_OUTPUT" "result.displayable.commands" "array"
json_expect_type "$LAST_OUTPUT" "result.revision" "number"
json_expect_missing_field "$LAST_OUTPUT" "result.title"
json_expect_missing_field "$LAST_OUTPUT" "result.displayableKind"
json_expect_missing_field "$LAST_OUTPUT" "result.commands"
json_expect_missing_field "$LAST_OUTPUT" "result.imageBase64"

menu_observe="$LAST_OUTPUT"
menu_revision="$(json_get "$menu_observe" "result.revision")"
open_editor_id="$(find_command_id "$menu_observe" "Open editor")"
open_canvas_id="$(find_command_id "$menu_observe" "Open canvas")"
ask_imei_id="$(find_command_id "$menu_observe" "Ask IMEI")"
ask_camera_id="$(find_command_id "$menu_observe" "Ask camera")"
ask_race_id="$(find_command_id "$menu_observe" "Ask permission race")"
auto_mutate_id="$(find_command_id "$menu_observe" "Auto mutate")"

expect_ok "command run" command run --id "$open_editor_id" \
  --expect-revision "$menu_revision" --wait-next-display
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.displayable.kind" "text_box"
json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Mega editor"
json_expect_field "$LAST_OUTPUT" "result.displayable.text" "alpha"
expect_ok "key press" key press SOFT_RIGHT --wait-dispatched
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Mega menu"

expect_ok "command run" command run --id "$open_canvas_id" \
  --expect-revision "$(json_get "$LAST_OUTPUT" "result.revision")" --wait-next-display
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.displayable.kind" "canvas"
json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Canvas ready"
expect_ok "key press" key press FIRE --wait-dispatched
expect_ok "observe" observe
json_expect_prefix "$LAST_OUTPUT" "result.displayable.title" "Key "
expect_ok "pointer tap" pointer tap 33 44 --wait-dispatched
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Tap 33,44"
expect_ok "drag" drag 10 10 50 60 80 90 --delay 10
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Drag 10,10 -> 80,90"
expect_ok "key press" key press SOFT_RIGHT --wait-dispatched
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Mega menu"

print_section "Permission and stale revision edges"
current_revision="$(json_get "$LAST_OUTPUT" "result.revision")"
expect_error "UNKNOWN_KEY" key press NOT_A_KEY --wait-dispatched
expect_error "UNKNOWN_COMMAND_ID" command run --id 9999 --expect-revision "$current_revision"
expect_ok "observe" observe

pre_mutate_revision="$(json_get "$LAST_OUTPUT" "result.revision")"
expect_ok "command run" command run --id "$auto_mutate_id" --expect-revision "$pre_mutate_revision"
expect_ok "wait display" wait display --title "Auto mutate done" --timeout 3000
expect_error "STALE_REVISION" command run --id "$open_editor_id" --expect-revision "$pre_mutate_revision"
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Auto mutate done"
late_id="$(find_command_id "$LAST_OUTPUT" "Late command")"
expect_ok "command run" command run --id "$late_id" --expect-revision "$(json_get "$LAST_OUTPUT" "result.revision")"
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Late command selected"

expect_ok "command run" command run --id "$ask_camera_id" --expect-revision "$(json_get "$LAST_OUTPUT" "result.revision")"
json_expect_field "$LAST_OUTPUT" "result.pending" "true"
json_expect_field "$LAST_OUTPUT" "result.status" "permission-pending"
json_expect_type "$LAST_OUTPUT" "result.permissionRequest" "object"
camera_permission_id="$(json_get "$LAST_OUTPUT" "result.permissionRequest.id")"
expect_ok "observe" observe
json_expect_type "$LAST_OUTPUT" "result.permissionRequest" "object"
json_expect_field "$LAST_OUTPUT" "result.permissionRequest.id" "$camera_permission_id"
expect_ok "permission" permission allow "$camera_permission_id"
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Camera allowed"

expect_ok "command run" command run --id "$ask_imei_id" --expect-revision "$(json_get "$LAST_OUTPUT" "result.revision")"
expect_ok "observe" observe
imei_permission_id="$(json_get "$LAST_OUTPUT" "result.permissionRequest.id")"
expect_ok "permission" permission allow "$imei_permission_id"
expect_ok "observe" observe
json_expect_prefix "$LAST_OUTPUT" "result.displayable.title" "IMEI allowed "

expect_ok "command run" command run --id "$ask_race_id" --expect-revision "$(json_get "$LAST_OUTPUT" "result.revision")"
expect_ok "wait permission" wait permission --timeout 3000
expect_ok "observe" observe
race_head_id="$(json_get "$LAST_OUTPUT" "result.permissionRequest.id")"
race_second_id=$((race_head_id + 1))
expect_error "PERMISSION_ORDER_VIOLATION" permission deny "$race_second_id"
expect_ok "permission" permission allow "$race_head_id"
expect_ok "wait permission" wait permission --timeout 3000
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.permissionRequest.id" "$race_second_id"
pre_race_finish_revision="$(json_get "$LAST_OUTPUT" "result.revision")"
expect_ok "permission" permission deny "$race_second_id"
expect_ok "wait display" wait display --after-revision "$pre_race_finish_revision" --timeout 3000
expect_ok "observe" observe
json_expect_prefix "$LAST_OUTPUT" "result.displayable.title" "Permission race "
expect_error "UNKNOWN_PERMISSION_ID" permission allow "$race_second_id"

print_section "Screenshot and logs"
expect_ok "screenshot" screenshot --out "$CAPTURE_FILE"
json_expect_field "$LAST_OUTPUT" "result.saved" "true"
json_expect_field "$LAST_OUTPUT" "result.path" "$CAPTURE_FILE"
json_expect_missing_field "$LAST_OUTPUT" "result.imageBase64"
png_expect_size "$CAPTURE_FILE" 240 320
expect_error "USAGE_ERROR" screenshot --out "$TEST_DIR/not-png.jpg"
mkdir -p -- "$TEST_DIR/as-dir.png"
expect_error "SCREENSHOT_WRITE_FAILED" screenshot --out "$TEST_DIR/as-dir.png"
expect_ok "logs cursor" logs cursor
json_expect_type "$LAST_OUTPUT" "result.cursor" "string"
expect_ok "logs read" logs read
json_expect_type "$LAST_OUTPUT" "result.lines" "array"

print_section "Worker death, timeout, and recovery"
expect_ok "observe" observe
json_expect_prefix "$LAST_OUTPUT" "result.displayable.title" "Permission race "

current_before_kill="$(controller_call_json app.current)"
worker_pid="$(json_get "$current_before_kill" "result.worker.pid")"
if [[ -z "$worker_pid" || "$worker_pid" == "null" ]]; then
  test_fail "Expected worker pid before kill."
fi
kill -9 "$worker_pid" 2>/dev/null || true
sleep 0.5
expect_error "WORKER_FAILURE" screenshot --out "$CAPTURE_DEAD_FILE"
if [[ -e "$CAPTURE_DEAD_FILE" ]]; then
  test_fail "Expected killed-worker screenshot to fail before writing $CAPTURE_DEAD_FILE"
fi
expect_ok "logs read" logs read
json_expect_type "$LAST_OUTPUT" "result.lines" "array"

open_mega
stopped_worker="$(controller_call_json app.current)"
stopped_worker_pid="$(json_get "$stopped_worker" "result.worker.pid")"
if [[ -z "$stopped_worker_pid" || "$stopped_worker_pid" == "null" ]]; then
  test_fail "Expected worker pid before SIGSTOP."
fi
kill -STOP "$stopped_worker_pid" 2>/dev/null || true
expect_error "WORKER_FAILURE" observe
kill -CONT "$stopped_worker_pid" 2>/dev/null || true
expect_ok "open" open "$MEGA_CLI_FIXTURE_JAR" --headless
json_expect_field "$LAST_OUTPUT" "result.displayable.title" "Mega menu"
expect_ok "close" close
json_expect_field "$LAST_OUTPUT" "result.closed" "true"

print_section "Final controller cleanup"
expect_ok "state" state
json_expect_field "$LAST_OUTPUT" "result.active" "false"
expect_ok "observe" observe
json_expect_field "$LAST_OUTPUT" "result.active" "false"
expect_ok "stop" stop --force
expect_ok "status" status
json_expect_field "$LAST_OUTPUT" "result.running" "false"

require_command_coverage \
  "help" \
  "start" \
  "status" \
  "stop" \
  "logs cursor" \
  "logs read" \
  "inspect" \
  "open" \
  "close" \
  "state" \
  "observe" \
  "screenshot" \
  "wait display" \
  "wait permission" \
  "key press" \
  "pointer tap" \
  "drag" \
  "command" \
  "command run" \
  "permission"

echo "CLI gauntlet test passed."
