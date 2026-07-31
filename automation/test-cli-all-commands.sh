#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DEFAULT_RELEASE_PREFIX="$ROOT_DIR/dist/release-linux-cli-all"
TEMP_RELEASE_DIR_CREATED=0
PYTHON_BIN="${PYTHON_BIN:-python3}"

usage() {
  cat <<'EOF'
Usage:
  ./automation/test-cli-all-commands.sh [RELEASE_DIR]

Builds an isolated Linux release bundle, prepares a strict CLI fixture pack,
and explicitly exercises every public CLI command surface:
  - help
  - start / status / stop / logs cursor/read
  - inspect / open / close / state / observe / screenshot
  - condition waits / key press / pointer tap / drag
  - command / command run / permission
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

assert_tail_line_count_at_most() {
  local payload="$1"
  local path="$2"
  local max_lines="$3"
  local tail_text
  local line_count
  tail_text="$(json_get "$payload" "$path")"
  line_count="$(printf '%s\n' "$tail_text" | awk 'NF { count++ } END { print count + 0 }')"
  if [[ "$line_count" -gt "$max_lines" ]]; then
    test_fail "Expected $path to contain at most $max_lines non-empty lines, got $line_count."
  fi
}

if [[ "$#" -eq 0 ]]; then
  RELEASE_DIR="$(allocate_release_dir)"
  TEMP_RELEASE_DIR_CREATED=1
else
  RELEASE_DIR="$(resolve_path "$1")"
fi

TEST_DIR="$RELEASE_DIR/automation/cli-all-commands"
FIXTURE_ROOT="$TEST_DIR/fixtures"
CAPTURE_FILE="$TEST_DIR/capture.png"

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

print_section "Build release bundle"
"$ROOT_DIR/build-release.sh" "$RELEASE_DIR"
mkdir -p -- "$TEST_DIR"

print_section "Prepare strict fixture pack"
"$ROOT_DIR/automation/test-fixtures/prepare-cli-fixtures.sh" "$RELEASE_DIR/KEmulator.jar" "$FIXTURE_ROOT"
source "$FIXTURE_ROOT/fixtures.env"

print_section "Help surface"
help_root_output="$(run_kemu_json help)"
json_assert_ok "$help_root_output" "help"
mark_command_covered "help"
json_expect_contains "$help_root_output" "result.usage" "kemu help [command...] [--json]"
json_expect_contains "$help_root_output" "result.usage" "CLI automation contract is currently Linux-only."

help_command_run_output="$(run_kemu_json help command run)"
json_assert_ok "$help_command_run_output" "help"
json_expect_field "$help_command_run_output" "result.topic" "command run"
json_expect_field "$help_command_run_output" "result.usage" $'Usage: kemu command run <--id ID|--label LABEL> --expect-revision REV [--wait-next-display] [--timeout MS] [--json]\nNote: CLI automation contract is currently Linux-only.'

if bare_command_output="$(run_kemu_json_allow_failure command)"; then
  test_fail "Expected bare command to fail with usage."
fi
json_assert_error_code "$bare_command_output" "USAGE_ERROR"
mark_command_covered "command"
json_expect_field "$bare_command_output" "command" "command"

print_section "Controller lifecycle"
start_output="$(run_kemu_json start --headless --runtime release)"
json_assert_ok "$start_output" "start"
mark_command_covered "start"
json_expect_field "$start_output" "result.runtime" "release"

status_output="$(run_kemu_json status)"
json_assert_ok "$status_output" "status"
mark_command_covered "status"
json_expect_field "$status_output" "result.running" "true"

state_idle="$(run_kemu_json state)"
json_assert_ok "$state_idle" "state"
mark_command_covered "state"
json_expect_field "$state_idle" "result.active" "false"

observe_idle="$(run_kemu_json observe)"
json_assert_ok "$observe_idle" "observe"
mark_command_covered "observe"
json_expect_field "$observe_idle" "result.active" "false"

if idle_logs_cursor="$(run_kemu_json_allow_failure logs cursor)"; then
  test_fail "Expected logs cursor without an active app to fail."
fi
json_assert_error_code "$idle_logs_cursor" "NO_ACTIVE_APP"

print_section "Inspect fixtures"
inspect_command_fixture="$(run_kemu_json inspect "$COMMAND_FIXTURE_JAR")"
json_assert_ok "$inspect_command_fixture" "inspect"
mark_command_covered "inspect"
json_expect_field "$inspect_command_fixture" "result.displayName" "Command Fixture"

inspect_dash_prefixed="$(run_kemu_json inspect "$DASH_PREFIXED_JAR")"
json_assert_ok "$inspect_dash_prefixed" "inspect"
json_expect_field "$inspect_dash_prefixed" "result.displayName" "Command Fixture"

inspect_good_jad="$(run_kemu_json inspect "$GOOD_JAD")"
json_assert_ok "$inspect_good_jad" "inspect"
json_expect_field "$inspect_good_jad" "result.displayName" "Descriptor Fixture"

inspect_multi_midlet="$(run_kemu_json inspect "$MULTI_MIDLET_JAR")"
json_assert_ok "$inspect_multi_midlet" "inspect"
json_expect_field "$inspect_multi_midlet" "result.displayName" "Multi Midlet Fixture"
json_expect_list_length "$inspect_multi_midlet" "result.midlets" "2"

if inspect_invalid_output="$(run_kemu_json_allow_failure inspect "$PLAIN_TEXT_JAR")"; then
  test_fail "Expected inspect on plain-text fixture to fail."
fi
json_assert_error_code "$inspect_invalid_output" "UNSUPPORTED_INPUT"

print_section "Open variants"
if open_missing_path_output="$(run_kemu_json_allow_failure open --headless)"; then
  test_fail "Expected open without <path> to fail."
fi
json_assert_error_code "$open_missing_path_output" "USAGE_ERROR"

open_dash_prefixed="$(run_kemu_json open "$DASH_PREFIXED_JAR" --headless)"
json_assert_ok "$open_dash_prefixed" "open"
mark_command_covered "open"
json_expect_field "$open_dash_prefixed" "result.app.displayName" "Command Fixture"

close_dash_prefixed="$(run_kemu_json close)"
json_assert_ok "$close_dash_prefixed" "close"
mark_command_covered "close"
json_expect_field "$close_dash_prefixed" "result.closed" "true"

open_multi_midlet="$(run_kemu_json open "$MULTI_MIDLET_JAR" --midlet 2 --headless)"
json_assert_ok "$open_multi_midlet" "open"
json_expect_field "$open_multi_midlet" "result.displayable.title" "Mutable menu"
json_expect_field "$open_multi_midlet" "result.app.midletName" "Mutable Title Fixture"

close_multi_midlet="$(run_kemu_json close)"
json_assert_ok "$close_multi_midlet" "close"

open_bom_manifest="$(run_kemu_json open "$BOM_MANIFEST_JAR" --headless)"
json_assert_ok "$open_bom_manifest" "open"
json_expect_field "$open_bom_manifest" "result.app.displayName" "Command Fixture"

close_bom_manifest="$(run_kemu_json close)"
json_assert_ok "$close_bom_manifest" "close"

open_parent_relative="$(run_kemu_json open "$PARENT_RELATIVE_JAD" --headless)"
json_assert_ok "$open_parent_relative" "open"
json_expect_field "$open_parent_relative" "result.displayable.title" "Parent Relative Target Menu"

if open_while_active_output="$(run_kemu_json_allow_failure open "$COMMAND_FIXTURE_JAR" --headless)"; then
  test_fail "Expected open while another app is active to fail."
fi
json_assert_error_code "$open_while_active_output" "APP_ALREADY_OPEN"

logs_cursor_output="$(run_kemu_json logs cursor)"
json_assert_ok "$logs_cursor_output" "logs cursor"
mark_command_covered "logs cursor"
json_expect_type "$logs_cursor_output" "result.cursor" "string"
logs_read_output="$(run_kemu_json logs read)"
json_assert_ok "$logs_read_output" "logs read"
mark_command_covered "logs read"
json_expect_type "$logs_read_output" "result.lines" "array"

print_section "State and observe"
wait_output="$(run_kemu_json wait display --kind list --title "Parent Relative Target Menu" --timeout 3000)"
json_assert_ok "$wait_output" "wait display"
mark_command_covered "wait display"

state_active="$(run_kemu_json state)"
json_assert_ok "$state_active" "state"
json_expect_field "$state_active" "result.active" "true"
json_expect_field "$state_active" "result.displayable.title" "Parent Relative Target Menu"

observe_menu="$(run_kemu_json observe)"
json_assert_ok "$observe_menu" "observe"
json_expect_field "$observe_menu" "result.schemaVersion" "3"
json_expect_field "$observe_menu" "result.displayable.title" "Parent Relative Target Menu"
json_expect_missing_field "$observe_menu" "result.title"
menu_revision="$(json_get "$observe_menu" "result.revision")"

print_section "Command run and key"
open_editor_id="$(find_command_id "$observe_menu" "Open editor")"
command_open_editor="$(run_kemu_json command run --id "$open_editor_id" --expect-revision "$menu_revision" --wait-next-display)"
json_assert_ok "$command_open_editor" "command run"
mark_command_covered "command run"

observe_editor="$(run_kemu_json observe)"
json_assert_ok "$observe_editor" "observe"
json_expect_field "$observe_editor" "result.displayable.title" "Editor"
json_expect_field "$observe_editor" "result.displayable.text" "hello"

key_back_output="$(run_kemu_json key press SOFT_RIGHT --wait-dispatched)"
json_assert_ok "$key_back_output" "key press"
mark_command_covered "key press"

observe_menu_again="$(run_kemu_json observe)"
json_assert_ok "$observe_menu_again" "observe"
json_expect_field "$observe_menu_again" "result.displayable.title" "Parent Relative Target Menu"

print_section "Pointer tap and drag"
touch_revision="$(json_get "$observe_menu_again" "result.revision")"
open_touch_id="$(find_command_id "$observe_menu_again" "Open touch")"
command_open_touch="$(run_kemu_json command run --id "$open_touch_id" --expect-revision "$touch_revision" --wait-next-display)"
json_assert_ok "$command_open_touch" "command run"

observe_touch="$(run_kemu_json observe)"
json_assert_ok "$observe_touch" "observe"
json_expect_field "$observe_touch" "result.displayable.title" "Touch canvas"

tap_output="$(run_kemu_json pointer tap 20 20 --wait-dispatched)"
json_assert_ok "$tap_output" "pointer tap"
mark_command_covered "pointer tap"

observe_tapped="$(run_kemu_json observe)"
json_assert_ok "$observe_tapped" "observe"
json_expect_field "$observe_tapped" "result.displayable.title" "Tap 20,20"

drag_output="$(run_kemu_json drag 20 20 120 120 --delay 10)"
json_assert_ok "$drag_output" "drag"
mark_command_covered "drag"

observe_dragged="$(run_kemu_json observe)"
json_assert_ok "$observe_dragged" "observe"
json_expect_field "$observe_dragged" "result.displayable.title" "Drag 20,20 -> 120,120"
screen_width="$(json_get "$observe_dragged" "result.width")"
screen_height="$(json_get "$observe_dragged" "result.height")"

key_back_from_touch="$(run_kemu_json key press SOFT_RIGHT --wait-dispatched)"
json_assert_ok "$key_back_from_touch" "key press"

print_section "Permission flows"
observe_permissions_menu="$(run_kemu_json observe)"
json_assert_ok "$observe_permissions_menu" "observe"
permission_revision_allow="$(json_get "$observe_permissions_menu" "result.revision")"
ask_imei_id="$(find_command_id "$observe_permissions_menu" "Ask IMEI")"
command_ask_imei="$(run_kemu_json command run --id "$ask_imei_id" --expect-revision "$permission_revision_allow")"
json_assert_ok "$command_ask_imei" "command run"

observe_imei_permission="$(run_kemu_json observe)"
json_assert_ok "$observe_imei_permission" "observe"
imei_permission_id="$(json_get "$observe_imei_permission" "result.permissionRequest.id")"

permission_allow_output="$(run_kemu_json permission allow "$imei_permission_id")"
json_assert_ok "$permission_allow_output" "permission"
mark_command_covered "permission"

observe_imei_allowed="$(run_kemu_json observe)"
json_assert_ok "$observe_imei_allowed" "observe"
json_expect_prefix "$observe_imei_allowed" "result.displayable.title" "IMEI "

camera_revision="$(json_get "$observe_imei_allowed" "result.revision")"
ask_camera_id="$(find_command_id "$observe_imei_allowed" "Ask camera")"
command_ask_camera="$(run_kemu_json command run --id "$ask_camera_id" --expect-revision "$camera_revision")"
json_assert_ok "$command_ask_camera" "command run"

observe_camera_permission="$(run_kemu_json observe)"
json_assert_ok "$observe_camera_permission" "observe"
camera_permission_id="$(json_get "$observe_camera_permission" "result.permissionRequest.id")"

permission_deny_output="$(run_kemu_json permission deny "$camera_permission_id")"
json_assert_ok "$permission_deny_output" "permission"

observe_camera_denied="$(run_kemu_json observe)"
json_assert_ok "$observe_camera_denied" "observe"
json_expect_field "$observe_camera_denied" "result.displayable.title" "Camera denied"

print_section "Screenshot and close"
screenshot_output="$(run_kemu_json screenshot --out "$CAPTURE_FILE")"
json_assert_ok "$screenshot_output" "screenshot"
mark_command_covered "screenshot"
if [[ ! -s "$CAPTURE_FILE" ]]; then
  test_fail "Expected screenshot file at $CAPTURE_FILE"
fi
png_expect_size "$CAPTURE_FILE" "$screen_width" "$screen_height"

mkdir -p -- "$TEST_DIR/as-dir.png"
if screenshot_dir_output="$(run_kemu_json_allow_failure screenshot --out "$TEST_DIR/as-dir.png")"; then
  test_fail "Expected screenshot to a directory path to fail."
fi
json_assert_error_code "$screenshot_dir_output" "SCREENSHOT_WRITE_FAILED"

close_output="$(run_kemu_json close)"
json_assert_ok "$close_output" "close"
json_expect_field "$close_output" "result.closed" "true"

state_after_close="$(run_kemu_json state)"
json_assert_ok "$state_after_close" "state"
json_expect_field "$state_after_close" "result.active" "false"

observe_after_close="$(run_kemu_json observe)"
json_assert_ok "$observe_after_close" "observe"
json_expect_field "$observe_after_close" "result.active" "false"

if logs_after_close="$(run_kemu_json_allow_failure logs read)"; then
  test_fail "Expected logs read without an active or failed worker to fail."
fi
json_assert_error_code "$logs_after_close" "NO_ACTIVE_APP"

print_section "Hard stale-revision fixtures"
open_auto_snapshot="$(run_kemu_json open "$AUTO_SNAPSHOT_FIXTURE_JAR" --headless)"
json_assert_ok "$open_auto_snapshot" "open"

observe_auto_menu="$(run_kemu_json observe)"
json_assert_ok "$observe_auto_menu" "observe"
json_expect_field "$observe_auto_menu" "result.displayable.title" "Auto menu"
auto_revision="$(json_get "$observe_auto_menu" "result.revision")"
auto_open_editor_id="$(find_command_id "$observe_auto_menu" "Open editor")"

auto_wait_output="$(run_kemu_json wait display --title "Auto editor" --timeout 3000)"
json_assert_ok "$auto_wait_output" "wait display"

if auto_stale_revision_output="$(run_kemu_json_allow_failure command run --id "$auto_open_editor_id" --expect-revision "$auto_revision")"; then
  test_fail "Expected stale autonomous revision to fail."
fi
json_assert_error_code "$auto_stale_revision_output" "STALE_REVISION"

close_auto_snapshot="$(run_kemu_json close)"
json_assert_ok "$close_auto_snapshot" "close"

open_mutable_title="$(run_kemu_json open "$MUTABLE_TITLE_FIXTURE_JAR" --headless)"
json_assert_ok "$open_mutable_title" "open"

observe_mutable_menu="$(run_kemu_json observe)"
json_assert_ok "$observe_mutable_menu" "observe"
json_expect_field "$observe_mutable_menu" "result.displayable.title" "Mutable menu"
mutable_revision="$(json_get "$observe_mutable_menu" "result.revision")"
mutable_open_editor_id="$(find_command_id "$observe_mutable_menu" "Open editor")"

mutable_wait_output="$(run_kemu_json wait display --title "Mutable menu updated" --timeout 3000)"
json_assert_ok "$mutable_wait_output" "wait display"

if mutable_stale_revision_output="$(run_kemu_json_allow_failure command run --id "$mutable_open_editor_id" --expect-revision "$mutable_revision")"; then
  test_fail "Expected stale mutable-title revision to fail."
fi
json_assert_error_code "$mutable_stale_revision_output" "STALE_REVISION"

close_mutable_title="$(run_kemu_json close)"
json_assert_ok "$close_mutable_title" "close"

print_section "Representative parser failures"
if wait_too_long_output="$(run_kemu_json_allow_failure wait idle --timeout 120001)"; then
  test_fail "Expected wait idle --timeout 120001 to fail."
fi
json_assert_error_code "$wait_too_long_output" "USAGE_ERROR"

if key_duration_zero_output="$(run_kemu_json_allow_failure key hold FIRE --duration 0 --wait-release)"; then
  test_fail "Expected key hold FIRE --duration 0 to fail."
fi
json_assert_error_code "$key_duration_zero_output" "USAGE_ERROR"

if drag_delay_short_output="$(run_kemu_json_allow_failure drag 20 20 120 120 --delay 4)"; then
  test_fail "Expected drag --delay 4 to fail."
fi
json_assert_error_code "$drag_delay_short_output" "USAGE_ERROR"

if command_run_missing_revision_output="$(run_kemu_json_allow_failure command run --id 1 --expect-revision)"; then
  test_fail "Expected command run --id 1 --expect-revision to fail."
fi
json_assert_error_code "$command_run_missing_revision_output" "USAGE_ERROR"

print_section "Stop controller"
stop_output="$(run_kemu_json stop --force)"
json_assert_ok "$stop_output" "stop"
mark_command_covered "stop"
json_expect_field "$stop_output" "result.stopped" "true"

status_after_stop="$(run_kemu_json status)"
json_assert_ok "$status_after_stop" "status"
json_expect_field "$status_after_stop" "result.exists" "false"

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
  "key press" \
  "pointer tap" \
  "drag" \
  "command" \
  "command run" \
  "permission"

echo "CLI all-commands test passed."
