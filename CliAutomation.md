# CLI Automation

KEmulator exposes a local automation CLI through `kemu.sh`. The CLI is intended
for scripts and AI agents that need to inspect, launch, observe, and control
J2ME MIDlets without manually driving the emulator UI.

## Status

- The current automation contract is Linux-only.
- The public launcher is `kemu.sh`.
- Automation uses a local controller/worker runtime and loopback transport.
- The transport is a local convenience channel, not a security boundary.
- The primary workflow is path-first: inspect an archive, then open that exact
  path.

## Build

Build a release bundle before using the launcher from a fresh checkout. The
build requires `java`, `javac`, and `jar`:

```bash
./build-release.sh [OUTPUT_DIR]
```

When `OUTPUT_DIR` is omitted, the default bundle is written to:

```text
dist/release-linux/
```

The bundle contains `KEmulator.jar`, `kemu.sh`, `lib/`, and `home/`.
The build refuses to overwrite a live release bundle while matching controller
or worker processes are still running.

## Basic Flow

Use `--json` for automation. Successful and expected failure responses are
printed as a single JSON envelope on stdout.

Run examples from the repository root with `./kemu.sh`. From a packaged bundle,
run that bundle's `./kemu.sh`.

```bash
./kemu.sh inspect ./app.jar --json
./kemu.sh --session-id test-1 open ./app.jar --headless \
  --data-dir /tmp/kemu-test-1 --reset-state --worker-xmx 64M \
  --wait-ready --json
./kemu.sh --session-id test-1 wait display --kind list --timeout 5000 --json
./kemu.sh observe --json
./kemu.sh key press FIRE --wait-dispatched --json
./kemu.sh screenshot --out ./screen.png --json
./kemu.sh close --json
./kemu.sh stop --force --json
```

For `.jad` files, `MIDlet-Jar-URL` should resolve to a local relative JAR path.
If it is missing, the inspector tries a sibling `.jar` with the same base name.

## Lifecycle

`open` starts the controller automatically when needed, then opens the requested
MIDlet. `close` closes only the active MIDlet. `stop --force` stops the
controller runtime and should be reserved for cleanup, recovery, or changing
controller defaults.

## Commands

- `help [command...]`
- `start [--headless|--visible] [--runtime <advertised-runtime>] [--size WxH]`
- `status`
- `stop [--force]`
- `logs <controller|worker> [--lines N]`, where `N` must be positive and
  defaults to `100`
- `logs cursor`
- `logs read [--since CURSOR] [--jsonl]`
- `logs wait --regex REGEX [--since CURSOR] [--timeout MS]`
- `inspect <path>`
- `open <path> [--midlet N] [--headless|--visible] [--runtime <advertised-runtime>] [--size WxH] [--data-dir DIR] [--rms-dir DIR] [--file-root DIR] [--reset-state] [--worker-xmx SIZE] [--wait-ready]`
- `close`
- `state`
- `state snapshot FILE`
- `state restore FILE`
- `rms reset`
- `rms export FILE`
- `rms import FILE`
- `observe`
- `events read [--since CURSOR] [--jsonl]`
- `screenshot --out FILE`
- `wait <ms>`
- `wait display [--kind KIND] [--title TITLE] [--selected-index N] [--after-revision REV] [--timeout MS]`
- `wait worker-ready [--timeout MS]`
- `wait worker-exit [--timeout MS]`
- `wait idle [--timeout MS]`
- `wait frame --after-revision REV [--timeout MS]`
- `wait permission [--name NAME] [--timeout MS]`
- `wait log --regex REGEX [--since CURSOR] [--timeout MS]`
- `key <key> [--duration MS]`
- `key press <key> [--wait-dispatched]`
- `key hold <key> [--duration MS] [--wait-release]`
- `tap <x> <y>`
- `pointer tap <x> <y> [--wait-dispatched]`
- `drag <x1> <y1> <x2> <y2> [<x3> <y3> ...] [--delay MS]`
- `list select INDEX [--expect-revision REV]`
- `list move <up|down> [--count N] [--expect-revision REV]`
- `choice set INDEX [--item-index INDEX] [--expect-revision REV]`
- `gauge set VALUE [--item-index INDEX] [--expect-revision REV]`
- `text-field set TEXT [--item-index INDEX] [--expect-revision REV]`
- `command run [ID|--id ID|--label LABEL] [--snapshot ID] [--expect-revision REV] [--wait-next-display] [--timeout MS]`
- `permission allow [id] [--once|--always]`
- `permission deny [id]`

`--session-id ID` is a global option and may be added to every command.

Run command-specific help with:

```bash
./kemu.sh help open --json
```

The launcher also accepts `--help`, `-h`, and command help such as:

```bash
./kemu.sh open --help
```

Use `open -- <path>` when an archive path begins with `--`.

Runtime names are launcher-dependent. A packaged bundle normally advertises
`release`; a repository checkout may advertise values such as `dev-linux` and
`release`. Query the active launcher with `help start` or `help open` and pass
one of the advertised values.

## JSON Contract

`--json` is a global flag. In JSON mode, parse stdout as the single response
envelope even when the process exits nonzero. Do not scrape stderr or human text
for automation control flow.

`--json` may appear anywhere in the command. Documentation examples place it at
the end for consistency. In shell scripts that use `set -e`, capture stdout and
the exit code explicitly so a JSON failure response is still available to parse.
Missing or invalid JSON usually means launcher bootstrap failure, transport
failure, or a process-level crash.

Success responses use this shape:

```json
{
  "ok": true,
  "command": "observe",
  "result": {}
}
```

Expected failures use this shape:

```json
{
  "ok": false,
  "command": "open",
  "error": {
    "code": "PATH_NOT_FOUND",
    "message": "Path not found: ./missing.jar",
    "details": {}
  }
}
```

`error.details` is optional and contains operation-specific context when
available, such as snapshot ids, permission ids, controller endpoint data, or
failed paths.

Automation callers should branch on `ok` and `error.code`, not on localized or
human-readable text.

Common error codes include:

- `USAGE_ERROR`
- `UNKNOWN_COMMAND`
- `JAVA_NOT_FOUND`
- `PATH_NOT_FOUND`
- `UNSUPPORTED_INPUT`
- `NO_RUNTIME`
- `UNKNOWN_RUNTIME`
- `AMBIGUOUS_RUNTIME`
- `DISPLAY_REQUIRED`
- `HEADLESS_DEPENDENCY_MISSING`
- `HEADLESS_UNSUPPORTED`
- `MISSING_SWT`
- `CONTROLLER_NOT_RUNNING`
- `CONTROLLER_UNREACHABLE`
- `CONTROLLER_ERROR`
- `CONFLICTING_CONTROLLER_DEFAULTS`
- `INVALID_REQUEST`
- `START_FAILED`
- `START_TIMEOUT`
- `STOP_FAILED`
- `APP_ALREADY_OPEN`
- `NO_ACTIVE_APP`
- `APP_INPUT_UNAVAILABLE`
- `MIDLET_SELECTION_REQUIRED`
- `UNKNOWN_MIDLET`
- `STALE_SNAPSHOT`
- `STALE_REVISION`
- `TIMEOUT`
- `LCDUI_CONTROL_UNAVAILABLE`
- `UNKNOWN_KEY`
- `UNKNOWN_COMMAND_ID`
- `UNKNOWN_PERMISSION_ID`
- `PERMISSION_ORDER_VIOLATION`
- `OPEN_TIMEOUT`
- `SCREENSHOT_FAILED`
- `SCREENSHOT_WRITE_FAILED`
- `WORKER_FAILURE`
- `INTERNAL_ERROR`

## Exit Codes

- `0`: success
- `2`: usage or request contract error
- `3`: path not found
- `4`: runtime, controller, or internal failure

JSON failures still exit nonzero. Use both the process exit code and
`error.code` when deciding whether to retry.

## Observing UI State

Use `status` for controller health. Use `state` for a lightweight active-app
snapshot. `state` still requires a running controller and can return
`WORKER_FAILURE` if the worker process has died.

`state` returns:

- active app metadata
- `active`
- `ready`
- `midletStarted`
- `title`
- `displayableKind`
- `permissionRequest`

`observe` returns the richer current MIDlet screen snapshot:

- `schemaVersion` (currently `2`), monotonic `revision`, `frameRevision`, and
  `eventCursor`
- readiness and active app metadata
- screen size
- displayable kind
- title
- softkey labels
- available LCDUI commands
- pending permission request
- `TextBox` text/caret metadata
- `List` items and selected index
- structured `displayable` data for `List`, `Form`, `StringItem`,
  `ChoiceGroup`, `Gauge`, `TextField`, `Alert`, and `Canvas`

`observe` is the preferred command for agents because it returns the current
controller/app state in one call.

Schema 2 retains the legacy top-level fields (`displayableKind`, `title`,
`list`, `commands`, and `commandSnapshotId`) so existing callers keep working.
New callers should use `schemaVersion`, `revision`, and `displayable`.

After `open`, call `observe --json` before the first input even if the open
response already contains session-like fields. Check `result.active` before
reading deeper UI fields. Use `observe`, not `state`, when choosing LCDUI
commands.

## Revisions And Atomic Commands

The preferred command flow is atomic and does not require a separate command
snapshot:

```bash
state="$(./kemu.sh observe --json)"
revision="$(printf '%s\n' "$state" | jq -r '.result.revision')"
./kemu.sh command run --label Exit \
  --expect-revision "$revision" \
  --wait-next-display --timeout 5000 --json
```

If the display state changed after `observe`, the operation returns
`STALE_REVISION`. A successful result includes `oldRevision`, `newRevision`,
`elapsedMs`, the resulting state, and (with `--wait-next-display`) transition
details. Rendering a frame does not advance the display `revision`;
`frameRevision` records the newest display revision that has actually been
painted, so repaint traffic cannot invalidate an otherwise current command.
LCDUI commands and native control mutations run on the LCDUI event
thread and return only after their model mutation or callback completes.
`--wait-next-display` also recognizes a structured display transition when an
application reuses one `Displayable` object but replaces its title or contents.

### Legacy Command Snapshots

LCDUI commands are invoked with snapshot protection:

1. Call `observe --json`.
2. Read `result.commandSnapshotId`.
3. Choose a command from `result.commands`.
4. Call `command run <id> --snapshot <snapshotId> --json`.

If the UI changes before the command is invoked, the CLI returns
`STALE_SNAPSHOT` instead of running a command from an old screen.

Command entries can include `id`, `text`, `choice`, `selected`, `label`, `type`,
and `priority`. Command ids are valid only for the snapshot that produced them.
After any UI-changing action, discard old ids and call `observe --json` again.

If `command run` returns `STALE_SNAPSHOT` or `UNKNOWN_COMMAND_ID`, call
`observe --json`, reselect the command by stable fields such as `label`, `text`,
`type`, or `priority`, and retry with the new snapshot id.

## Permissions

When `result.permissionRequest` is non-null, the MIDlet may be blocked waiting
for an answer. The request includes its stable `name` where KEmulator can
identify one, for example `javax.microedition.io.Connector.file.read`. Read
`result.permissionRequest.id` and answer it with:

```bash
./kemu.sh permission allow <id> --json
./kemu.sh permission deny <id> --json
./kemu.sh permission allow --once --json
./kemu.sh permission allow --always --json
```

Only the head pending permission can be answered. If the CLI returns
`UNKNOWN_PERMISSION_ID` or `PERMISSION_ORDER_VIOLATION`, call `observe --json`
again and use the current `permissionRequest.id`.

After answering a permission request, call `observe --json` before issuing the
next UI command because the screen and command snapshot may have changed.
When the id is omitted, the head pending permission is answered atomically.
`--always` changes the permission policy for the remaining lifetime of that
worker; it does not claim device-level or OS-level persistence. If a permission
request disappears while an agent is deciding, call `observe --json` and
continue from the new state.

## Input

Useful key names:

- `UP`, `DOWN`, `LEFT`, `RIGHT`
- `FIRE`, `OK`, `MIDDLE`
- `LSK`, `SOFT_LEFT`, `S1`
- `RSK`, `SOFT_RIGHT`, `S2`
- `NUM0` through `NUM9`
- `0` through `9`
- `STAR`
- `*`
- `POUND`
- `#`

Limits and defaults:

- condition waits accept `0..120000` ms and use condition variables or file
  events rather than polling sleeps
- legacy `wait <ms>` remains for compatibility
- `key --duration MS` accepts `10..5000` and defaults to `80`.
- `drag --delay MS` accepts `5..1000` and defaults to `20`.
- `drag` requires at least two points and an even coordinate count.
- `tap` and `drag` coordinates must be non-negative integers.

For Canvas games, numeric keypad input is often more reliable than directional
aliases because many J2ME games document movement as `1` through `9`.

## Screenshots And Canvas Text

The CLI can expose structured text for LCDUI widgets such as `List` and
`TextBox`. Canvas games are different: the game draws pixels directly, so text
rendered inside the Canvas is not available as structured state.

For Canvas games, use screenshots and an OCR or vision layer when an agent needs
to understand on-screen text.

```bash
./kemu.sh screenshot --out ./screen.png --json
```

`--out` must end in `.png`. Parent directories are created automatically. The
JSON response returns metadata such as `app`, nested `state`, `saved: true`,
and `path`; it does not embed screenshot bytes. Non-`.png` output paths return
`USAGE_ERROR`. File creation and write failures return
`SCREENSHOT_WRITE_FAILED`.

Use unique screenshot paths for each observation step so an agent does not read
stale image files. Treat `observe.width` and `observe.height` as the coordinate
space for `tap` and `drag`.

## Headless And Visible Modes

Headless mode currently requires Linux and `xvfb-run`:

```bash
./kemu.sh open ./app.jar --headless --json
```

Visible mode requires an X11 `DISPLAY`:

```bash
./kemu.sh open ./app.jar --visible --json
```

If neither a display nor `xvfb-run` is available, the CLI returns a runtime
error such as `DISPLAY_REQUIRED` or `HEADLESS_DEPENDENCY_MISSING`.

When no mode is specified, the controller chooses visible mode if `DISPLAY`
exists, otherwise headless mode on Linux if `xvfb-run` exists. The default screen
size is `240x320`. Controller startup waits up to `30000` ms before returning
`START_TIMEOUT`.

If a controller is already running, explicit `--runtime`, mode, or `--size`
values must match that controller. Conflicts return
`CONFLICTING_CONTROLLER_DEFAULTS`.

Each `--session-id` owns its controller state, loopback port, worker, logs,
captures, and default writable data tree. Headless controllers own a private
`xvfb-run` lifecycle; `stop` is idempotent and shuts down the worker before the
controller exits.

## Writable Session Storage

The release bundle may be mounted read-only. Controller state automatically
falls back to a writable temporary automation root when the bundle directory is
not writable; `KEMU_AUTOMATION_DIR` can select an explicit root. Worker writes
go to `--data-dir`, `--rms-dir`, and `--file-root`.

For compatibility with existing bundles, an implicit file root is seeded once
from the bundle's legacy `file/root` directory when the session file root does
not yet exist. Later reads and writes use only the session copy. Explicit data,
RMS, and file roots that overlap the runtime bundle are rejected.

```bash
./kemu.sh --session-id a open app.jar \
  --data-dir /tmp/kemu-a \
  --rms-dir /tmp/kemu-a/rms \
  --file-root /tmp/kemu-a/files \
  --reset-state --worker-xmx 64M --wait-ready

./kemu.sh --session-id a rms export /tmp/a-rms.zip
./kemu.sh --session-id a state snapshot /tmp/a-state.zip
```

`rms import`, `rms reset`, `state snapshot`, and `state restore` require the
session app to be closed so the archive is consistent. Archives validate their
schema and paths before replacing session data. RMS index updates use an atomic
replace so another MIDlet thread cannot observe a truncated index.

`KEMU_WORKER_JAVA_OPTS` supplies additional worker JVM options. An explicit
`--worker-xmx` replaces any `-Xmx` in that variable. `status --json` reports
controller and worker JVM options, both PIDs, data paths, and the configured
emulated heap when one exists.

## Event And Log Cursors

`events read --jsonl` returns structured JSONL events such as
`display-changed`, `selection-changed`, `command-finished`,
`input-dispatched`, and `frame-rendered`. Use `eventCursor` or an event
`cursor` as the next `--since` value.

Worker log cursors are opaque:

```bash
cursor="$(./kemu.sh logs cursor --json | jq -r '.result.cursor')"
./kemu.sh logs wait --since "$cursor" --regex 'STATUS=PASS' --timeout 10000
./kemu.sh logs read --since "$cursor" --jsonl
```

## Troubleshooting

Useful first checks:

```bash
./kemu.sh status --json
./kemu.sh logs controller --lines 120 --json
./kemu.sh logs worker --lines 120 --json
```

Reset a stuck run with:

```bash
./kemu.sh stop --force --json
```

- `DISPLAY_REQUIRED`: set X11 `DISPLAY` or use `--headless`.
- `HEADLESS_DEPENDENCY_MISSING`: install `xvfb-run` or use visible mode with
  `DISPLAY`.
- `UNKNOWN_RUNTIME`: run `./kemu.sh help start --json` and use an advertised
  runtime.
- `CONTROLLER_UNREACHABLE`: run `./kemu.sh stop --force --json`, then start
  again.
- `NO_ACTIVE_APP` from `logs worker` after `close`: expected when no MIDlet is
  active.
- `STALE_SNAPSHOT`: run `observe --json` again and retry with the new
  `commandSnapshotId`.
- `SCREENSHOT_WRITE_FAILED`: check the parent directory, permissions, and that
  `--out` is a file path. A non-`.png` extension is a `USAGE_ERROR`.

## Agent Guidance

- Always pass `--json`.
- Start with `inspect <path>` before `open <path>`.
- Prefer absolute or clearly rooted paths in automation scripts.
- Use condition waits after actions instead of fixed sleeps and repeated
  observations.
- Use `revision` guards for LCDUI controls and atomic commands.
- Keep command snapshots only for compatibility with older callers.
- Answer pending permissions before normal UI input.
- Use screenshots with unique output paths for Canvas games and visual
  verification.
- Clean up with `close --json`, then `stop --force --json` at the end of a run.
- Treat `close` responses such as `closed: false` and `stop` responses such as
  `stopped: false, reason: "not_running"` as successful cleanup.
- Do not kill controller PIDs manually from an agent script; use
  `status --json` and `stop --force --json`.
- If `status` reports `pidIdentityMatches: false`, do not kill the PID manually;
  recover with `stop --force --json`.
- Do not retry unchanged requests for usage errors, missing paths, unsupported
  input, unknown keys, or unknown MIDlet indexes.
- Treat the loopback controller as local process automation, not as a remote API.
