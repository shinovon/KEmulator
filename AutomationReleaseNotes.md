# Automation API release notes

## Unreleased

- Added schema 3 observations with monotonic revisions, structured LCDUI trees,
  frame revisions, cursor-addressable events, and one canonical nested
  `displayable` representation.
- Added event-driven waits for displays, worker readiness/exit, LCDUI idle,
  frames, permissions, and worker log regular expressions. Timeouts return
  structured last-state diagnostics and elapsed time.
- Added LCDUI-thread-safe native `List`, `ChoiceGroup`, `Gauge`, and `TextField`
  mutation commands with optimistic revision checks.
- Added atomic command lookup by id or label, `STALE_REVISION` protection,
  callback completion acknowledgement, and optional next-display waiting,
  including applications that reuse one `Displayable` instance while replacing
  its title or contents.
- Command callbacks that suspend on a permission request now return a structured
  `permission-pending` result. The command later emits `command-finished` after
  the request is answered instead of deadlocking the CLI until timeout.
- Added acknowledged key and pointer delivery with delivery-kind metadata.
- Added session IDs, writable data/RMS/file roots, read-only bundle support,
  RMS/state archives, configurable worker JVM options, and actual controller
  and worker PID/status reporting.
- Preserved legacy `file/root` bundle fixtures by copying them once into an
  implicit session-local file root, rejected explicit writable roots that
  overlap the runtime bundle, and made RMS index replacement atomic.
- Kept repaint traffic out of display revision checks, coalesced frame events
  by rendered state revision, delivered native item-state callbacks before
  control mutations return, and initialized an empty writable `midlets.ini`
  when a session starts from a read-only bundle.
- Added cursor-based worker log reads/waits and JSONL event reads.
- Removed fixed `wait <ms>`, legacy key/tap forms, line-tail log commands,
  command snapshots, positional command ids, and schema 1-style observation
  duplicates. Current commands require revision guards and acknowledged input.

This automation runtime is a functional MIDP/LCDUI/RMS/JSR-75/MMAPI test
environment. Its wall time, audio timing, and rendered output are not evidence
of physical Java ME device performance or fidelity.
