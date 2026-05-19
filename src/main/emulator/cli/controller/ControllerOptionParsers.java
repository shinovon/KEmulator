package emulator.cli.controller;

import emulator.cli.core.CliExitCodes;
import emulator.cli.core.KemuCliException;
import emulator.cli.output.CliTextRenderer;
import emulator.cli.parse.CliParsing;
import emulator.cli.support.CliDefaults;
import java.util.List;
import java.util.Locale;

final class ControllerOptionParsers {
	private ControllerOptionParsers() {
	}

	static KemuCliException duplicateOption(String option, String commandName, boolean json) {
		return new KemuCliException(
			"USAGE_ERROR", "Duplicate option: " + option + '.', CliExitCodes.USAGE, commandName, json);
	}

	static KemuCliException usageError(String commandName, boolean json) {
		return new KemuCliException(
			"USAGE_ERROR", CliTextRenderer.usageText(commandName), CliExitCodes.USAGE, commandName, json);
	}

	private static void requireSingleAssignment(Object value, String option, String commandName, boolean json) {
		if (value != null) {
			throw duplicateOption(option, commandName, json);
		}
	}

	private static void requireModeOptionAvailable(String currentMode, String token, String commandName, boolean json) {
		if (currentMode == null) {
			return;
		}

		String requestedMode = "--headless".equals(token) ? "headless" : "visible";
		if (currentMode.equals(requestedMode)) {
			throw duplicateOption(token, commandName, json);
		}

		throw new KemuCliException(
			"USAGE_ERROR", "Conflicting options: --headless and --visible.", CliExitCodes.USAGE, commandName, json);
	}

	private static int[] parseSize(String value, String commandName, boolean json) {
		String[] parts = value.toLowerCase(Locale.US).split("x");
		if (parts.length != 2) {
			throw usageError(commandName, json);
		}

		return new int[]{
			CliParsing.parseIntegerArgument(parts[0], "width", commandName, json),
			CliParsing.parseIntegerArgument(parts[1], "height", commandName, json)
		};
	}

	static StartOptions parseStartOptions(
		List<String> tokens,
		int startIndex,
		String commandName,
		boolean json,
		boolean requirePathAlreadyConsumed,
		boolean applyDefaults) {
		String mode = null;
		String runtime = null;
		Integer width = null;
		Integer height = null;
		for (int i = startIndex; i < tokens.size(); i++) {
			String token = tokens.get(i);
			if ("--headless".equals(token)) {
				requireModeOptionAvailable(mode, token, commandName, json);
				mode = "headless";
			} else if ("--visible".equals(token)) {
				requireModeOptionAvailable(mode, token, commandName, json);
				mode = "visible";
			} else if ("--runtime".equals(token)) {
				if (i + 1 >= tokens.size()) {
					throw usageError(commandName, json);
				}

				requireSingleAssignment(runtime, "--runtime", commandName, json);
				runtime = tokens.get(++i);
			} else if ("--size".equals(token)) {
				if (i + 1 >= tokens.size()) {
					throw usageError(commandName, json);
				}

				if (width != null || height != null) {
					throw duplicateOption("--size", commandName, json);
				}

				int[] size = parseSize(tokens.get(++i), commandName, json);
				width = Integer.valueOf(size[0]);
				height = Integer.valueOf(size[1]);
			} else {
				throw usageError(commandName, json);
			}
		}

		return new StartOptions(
			mode,
			runtime,
			applyDefaults && width == null ? Integer.valueOf(CliDefaults.DEFAULT_WIDTH) : width,
			applyDefaults && height == null ? Integer.valueOf(CliDefaults.DEFAULT_HEIGHT) : height);
	}
}
