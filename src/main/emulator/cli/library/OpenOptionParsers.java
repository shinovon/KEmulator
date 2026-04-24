package emulator.cli.library;

import emulator.cli.controller.ControllerLifecycle;
import emulator.cli.core.CliExitCodes;
import emulator.cli.core.KemuCliException;
import emulator.cli.output.CliTextRenderer;
import emulator.cli.parse.CliParsing;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

final class OpenOptionParsers {
	private OpenOptionParsers() {
	}

	private static KemuCliException usageError(boolean json) {
		return new KemuCliException("USAGE_ERROR", CliTextRenderer.usageText("open"), CliExitCodes.USAGE, "open", json);
	}

	private static KemuCliException duplicateOption(String option, boolean json) {
		return new KemuCliException(
			"USAGE_ERROR", "Duplicate option: " + option + '.', CliExitCodes.USAGE, "open", json);
	}

	private static void requireSingleAssignment(Object value, String option, boolean json) {
		if (value != null) {
			throw duplicateOption(option, json);
		}
	}

	private static boolean isOpenOptionToken(String token) {
		return "--midlet".equals(token)
			|| "--headless".equals(token)
			|| "--visible".equals(token)
			|| "--runtime".equals(token)
			|| "--size".equals(token);
	}

	private static int parseInputPathIndex(List<String> tokens, boolean json) {
		if (tokens.size() < 2) {
			throw usageError(json);
		}

		String token = tokens.get(1);
		if ("--".equals(token)) {
			if (tokens.size() < 3) {
				throw usageError(json);
			}

			return 2;
		}

		if (isOpenOptionToken(token)) {
			throw usageError(json);
		}

		return 1;
	}

	private static List<String> stripMidlet(List<String> tokens, int startIndex, boolean json) {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = startIndex; i < tokens.size(); i++) {
			String token = tokens.get(i);
			if ("--midlet".equals(token)) {
				i++;
				if (i >= tokens.size()) {
					throw usageError(json);
				}

				continue;
			}

			result.add(token);
		}

		return result;
	}

	static OpenOptions parse(List<String> tokens, boolean json) {
		int inputPathIndex = parseInputPathIndex(tokens, json);
		Path inputPath = CliParsing.resolveUserPath(tokens.get(inputPathIndex));
		Integer midlet = null;
		for (int i = inputPathIndex + 1; i < tokens.size(); i++) {
			String token = tokens.get(i);
			if ("--midlet".equals(token)) {
				if (i + 1 >= tokens.size()) {
					throw usageError(json);
				}

				requireSingleAssignment(midlet, "--midlet", json);
				midlet = Integer.valueOf(CliParsing.parseIntegerArgument(tokens.get(++i), "--midlet", "open", json));
			}
		}

		return new OpenOptions(
			inputPath,
			midlet,
			ControllerLifecycle.parseStartOptions(
				stripMidlet(tokens, inputPathIndex + 1, json), 0, "open", json, true, false));
	}
}
