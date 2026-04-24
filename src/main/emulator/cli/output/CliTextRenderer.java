package emulator.cli.output;

import emulator.automation.shared.TextValues;
import emulator.cli.controller.ControllerStatus;
import emulator.cli.library.*;
import emulator.cli.support.KemuPaths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import mjson.Json;

public final class CliTextRenderer {
	private CliTextRenderer() {
	}

	private static String trimTrailingNewline(StringBuilder value) {
		if (value.length() > 0 && value.charAt(value.length() - 1) == '\n') {
			value.setLength(value.length() - 1);
		}

		return value.toString();
	}

	private static boolean hasDevRuntime(Path root) {
		return Files.isRegularFile(root.resolve("out/classes-linux/emulator/cli/KEmulator.class"))
			&& Files.isRegularFile(root.resolve("out/classes-linux/emulator/cli/KemuMain.class"))
			&& Files.isRegularFile(
				root.resolve("out/classes-linux/emulator/automation/controller/AutomationControllerMain.class"))
			&& Files.isRegularFile(
				root.resolve("out/classes-linux/emulator/automation/worker/AutomationWorkerMain.class"));
	}

	private static boolean hasReleaseRuntime(Path root) {
		return Files.isRegularFile(root.resolve("KEmulator.jar"))
			|| Files.isRegularFile(root.resolve("dist/release-linux/KEmulator.jar"));
	}

	private static String joinRuntimeChoices(ArrayList<String> values) {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) {
				out.append('|');
			}

			out.append(values.get(i));
		}

		return out.toString();
	}

	private static String runtimeUsageChoices() {
		String configured = TextValues.trimToNull(System.getProperty("kemu.bootstrap.availableRuntimes"));
		if (configured != null) {
			return configured;
		}

		ArrayList<String> available = new ArrayList<String>();
		Path root = KemuPaths.rootDir();
		if (hasDevRuntime(root)) {
			available.add("dev-linux");
		}

		if (hasReleaseRuntime(root)) {
			available.add("release");
		}

		if (!available.isEmpty()) {
			return joinRuntimeChoices(available);
		}

		String bootstrapRuntime = TextValues.trimToNull(System.getProperty("kemu.bootstrap.runtime"));
		if (bootstrapRuntime != null) {
			return bootstrapRuntime;
		}

		return "dev-linux|release";
	}

	public static String usageText() {
		String runtimeUsage = runtimeUsageChoices();

		return "Usage:\n"
			+ "  kemu help [command...] [--json]\n"
			+ "  kemu start [--headless|--visible] [--runtime " + runtimeUsage + "] [--size WxH] [--json]\n"
			+ "  kemu status [--json]\n"
			+ "  kemu stop [--force] [--json]\n"
			+ "  kemu logs <controller|worker> [--lines N] [--json]\n"
			+ "  kemu inspect <path> [--json]\n"
			+ "  kemu open <path> [--midlet N] [--headless|--visible] [--runtime " + runtimeUsage
			+ "] [--size WxH] [--json]\n"
			+ "  kemu close [--json]\n"
			+ "  kemu state [--json]\n"
			+ "  kemu observe [--json]\n"
			+ "  kemu screenshot --out FILE [--json]\n"
			+ "  kemu wait <ms> [--json]\n"
			+ "  kemu key <key> [--duration MS] [--json]\n"
			+ "  kemu tap <x> <y> [--json]\n"
			+ "  kemu drag <x1> <y1> <x2> <y2> [<x3> <y3> ...] [--delay MS] [--json]\n"
			+ "  kemu command run <id> --snapshot <snapshotId> [--json]\n"
			+ "  kemu permission <allow|deny> <id> [--json]\n"
			+ "\n"
			+ "Notes:\n"
			+ "  CLI automation contract is currently Linux-only.\n"
			+ "  Use `kemu <command> --help` or `kemu help <command...>` for command-specific usage.\n"
			+ "  Path-first workflow is canonical: inspect/open <path>.\n";
	}

	private static String usageLine(String topic) {
		if ("help".equals(topic))
			return "kemu help [command...] [--json]";
		if ("start".equals(topic))
			return "kemu start [--headless|--visible] [--runtime " + runtimeUsageChoices() + "] [--size WxH] [--json]";
		if ("status".equals(topic))
			return "kemu status [--json]";
		if ("stop".equals(topic))
			return "kemu stop [--force] [--json]";
		if ("logs".equals(topic))
			return "kemu logs <controller|worker> [--lines N] [--json]";
		if ("inspect".equals(topic))
			return "kemu inspect <path> [--json]";
		if ("open".equals(topic))
			return "kemu open <path> [--midlet N] [--headless|--visible] [--runtime " + runtimeUsageChoices()
				+ "] [--size WxH] [--json]";
		if ("close".equals(topic))
			return "kemu close [--json]";
		if ("state".equals(topic))
			return "kemu state [--json]";
		if ("observe".equals(topic))
			return "kemu observe [--json]";
		if ("screenshot".equals(topic))
			return "kemu screenshot --out FILE [--json]";
		if ("wait".equals(topic))
			return "kemu wait <ms> [--json]";
		if ("key".equals(topic))
			return "kemu key <key> [--duration MS] [--json]";
		if ("tap".equals(topic))
			return "kemu tap <x> <y> [--json]";
		if ("drag".equals(topic))
			return "kemu drag <x1> <y1> <x2> <y2> [<x3> <y3> ...] [--delay MS] [--json]";
		if ("command".equals(topic))
			return "kemu command run <id> --snapshot <snapshotId> [--json]";
		if ("command run".equals(topic))
			return "kemu command run <id> --snapshot <snapshotId> [--json]";
		if ("permission".equals(topic))
			return "kemu permission <allow|deny> <id> [--json]";
		return null;
	}

	public static String usageText(String topic) {
		if (topic == null || topic.length() == 0) {
			return usageText();
		}

		String usage = usageLine(topic);
		if (usage == null) {
			return usageText();
		}

		return "Usage: " + usage + "\nNote: CLI automation contract is currently Linux-only.";
	}

	public static String renderStatus(ControllerStatus status) {
		if (!status.exists) {
			return "No controller state file found.";
		}

		StringBuilder out = new StringBuilder();
		out.append("Controller state: ").append(status.source).append('\n');
		out.append("State file: ").append(status.stateFile).append('\n');
		out.append("Running: ").append(status.running).append('\n');
		out.append("Reachable: ").append(status.reachable).append('\n');
		if (status.pidAlive != null) {
			out.append("PID alive: ").append(status.pidAlive.booleanValue()).append('\n');
		}

		if (status.degraded) {
			out.append("Degraded: true").append('\n');
		}

		if (status.pid != null) {
			out.append("PID: ").append(status.pid).append('\n');
		}

		if (status.endpoint() != null) {
			out.append("Endpoint: ").append(status.endpoint()).append('\n');
		}

		if (status.mode != null) {
			out.append("Mode: ").append(status.mode).append('\n');
		}

		if (status.runtime != null) {
			out.append("Runtime: ").append(status.runtime).append('\n');
		}

		if (status.screen != null) {
			out.append("Screen: ").append(status.screen).append('\n');
		}

		if (status.logFile != null) {
			out.append("Log file: ").append(status.logFile).append('\n');
		}

		if (status.loadError != null) {
			out.append("Load error: ").append(status.loadError).append('\n');
		}

		return trimTrailingNewline(out);
	}

	public static String renderInspection(InspectionResult result) {
		StringBuilder out = new StringBuilder();
		out.append("Path: ").append(result.inputPath).append('\n');
		out.append("Kind: ").append(result.sourceKind).append('\n');
		out.append("Display name: ").append(result.displayName).append('\n');
		if (result.jarPath != null) {
			out.append("Jar path: ").append(result.jarPath).append('\n');
		}

		if (result.jadPath != null) {
			out.append("Descriptor path: ").append(result.jadPath).append('\n');
		}

		if (result.vendor != null) {
			out.append("Vendor: ").append(result.vendor).append('\n');
		}

		if (result.version != null) {
			out.append("Version: ").append(result.version).append('\n');
		}

		if (result.midlets.isEmpty()) {
			out.append("Midlets: none");
		} else {
			out.append("Midlets:").append('\n');
			for (MidletEntry midlet : result.midlets) {
				out.append("  ")
					.append(midlet.index)
					.append(". ")
					.append(midlet.name)
					.append(" -> ")
					.append(midlet.className)
					.append('\n');
			}

			if (result.selectedMidletClass != null) {
				out.append("Selected MIDlet class: ")
					.append(result.selectedMidletClass)
					.append('\n');
			}
		}

		return trimTrailingNewline(out);
	}

	public static String renderOpen(Json payload) {
		StringBuilder out = new StringBuilder();
		Json app = payload.at("app");
		if (app != null && app.isObject()) {
			out.append("Opened: ")
				.append(
					app.at("displayName", Json.nil()).isNull()
						? "(unknown)"
						: app.at("displayName").asString())
				.append('\n');
		}

		out.append("Ready: ").append(payload.at("ready", false).asBoolean()).append('\n');
		out.append("Title: ")
			.append(
				payload.at("title", Json.nil()).isNull()
					? ""
					: payload.at("title").asString())
			.append('\n');

		return trimTrailingNewline(out);
	}

	public static String renderState(Json payload) {
		if (!payload.at("active", false).asBoolean()) {
			return "No active app.";
		}

		StringBuilder out = new StringBuilder();
		Json app = payload.at("app");
		if (app != null && app.isObject()) {
			out.append("App: ")
				.append(
					app.at("displayName", Json.nil()).isNull()
						? "(unknown)"
						: app.at("displayName").asString())
				.append('\n');
		}

		out.append("Ready: ").append(payload.at("ready", false).asBoolean()).append('\n');
		out.append("Midlet started: ")
			.append(payload.at("midletStarted", false).asBoolean())
			.append('\n');
		if (payload.has("title") && !payload.at("title").isNull()) {
			out.append("Title: ").append(payload.at("title").asString()).append('\n');
		}

		if (payload.has("displayableKind") && !payload.at("displayableKind").isNull()) {
			out.append("Displayable: ")
				.append(payload.at("displayableKind").asString())
				.append('\n');
		}

		if (payload.has("permissionRequest") && !payload.at("permissionRequest").isNull()) {
			Json permission = payload.at("permissionRequest");
			out.append("Permission pending: id=").append(permission.at("id").asInteger());
			if (permission.has("message") && !permission.at("message").isNull()) {
				out.append(" message=").append(permission.at("message").asString());
			}

			out.append('\n');
		}

		return trimTrailingNewline(out);
	}

	public static String renderObserve(Json payload) {
		if (payload.has("active") && !payload.at("active", false).asBoolean()) {
			return "No active app.";
		}

		StringBuilder out = new StringBuilder();
		out.append("Ready: ").append(payload.at("ready", false).asBoolean()).append('\n');
		out.append("Midlet started: ")
			.append(payload.at("midletStarted", false).asBoolean())
			.append('\n');
		if (payload.has("displayableKind") && !payload.at("displayableKind").isNull()) {
			out.append("Displayable: ")
				.append(payload.at("displayableKind").asString())
				.append('\n');
		}

		if (payload.has("title") && !payload.at("title").isNull()) {
			out.append("Title: ").append(payload.at("title").asString()).append('\n');
		}

		Json commands = payload.at("commands", Json.array());
		out.append("Commands: ").append(commands.asJsonList().size()).append('\n');
		if (payload.has("commandSnapshotId") && !payload.at("commandSnapshotId").isNull()) {
			out.append("Command snapshot: ")
				.append(payload.at("commandSnapshotId").asInteger())
				.append('\n');
		}

		for (Json command : commands.asJsonList()) {
			out.append("  [").append(command.at("id").asInteger()).append("] ");
			String text = command.at("text", Json.nil()).isNull()
				? ""
				: command.at("text").asString();
			if (text.length() == 0
				&& command.has("label")
				&& !command.at("label").isNull()) {
				text = command.at("label").asString();
			}

			if (text.length() == 0) {
				text = "(unnamed command)";
			}

			out.append(text).append('\n');
		}

		if (payload.has("permissionRequest") && !payload.at("permissionRequest").isNull()) {
			Json permission = payload.at("permissionRequest");
			out.append("Permission pending: id=").append(permission.at("id").asInteger());
			if (permission.has("message") && !permission.at("message").isNull()) {
				out.append(" message=").append(permission.at("message").asString());
			}

			out.append('\n');
		}

		return trimTrailingNewline(out);
	}

	public static String renderCommandRun(Json payload) {
		String text = payload.at("text", "").asString();
		if (text != null && text.length() > 0) {
			return "Command invoked: " + text;
		}

		return "Command invoked.";
	}

	public static String renderPermission(Json payload) {
		return payload.at("allow", false).asBoolean() ? "Permission allowed." : "Permission denied.";
	}
}
