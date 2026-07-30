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
			|| "--size".equals(token)
			|| "--data-dir".equals(token)
			|| "--rms-dir".equals(token)
			|| "--file-root".equals(token)
			|| "--reset-state".equals(token)
			|| "--wait-ready".equals(token)
			|| "--worker-xmx".equals(token);
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

	static OpenOptions parse(List<String> tokens, boolean json) {
		int inputPathIndex = parseInputPathIndex(tokens, json);
		Path inputPath = CliParsing.resolveUserPath(tokens.get(inputPathIndex));
		Integer midlet = null;
		Path dataDir = null;
		Path rmsDir = null;
		Path fileRoot = null;
		String workerXmx = null;
		boolean resetState = false;
		boolean waitReady = false;
		ArrayList<String> startTokens = new ArrayList<String>();
		for (int i = inputPathIndex + 1; i < tokens.size(); i++) {
			String token = tokens.get(i);
			if ("--midlet".equals(token)) {
				if (i + 1 >= tokens.size()) {
					throw usageError(json);
				}

				requireSingleAssignment(midlet, "--midlet", json);
				midlet = Integer.valueOf(CliParsing.parseIntegerArgument(tokens.get(++i), "--midlet", "open", json));
			} else if ("--data-dir".equals(token)
				|| "--rms-dir".equals(token)
				|| "--file-root".equals(token)) {
				if (i + 1 >= tokens.size()) {
					throw usageError(json);
				}
				Path value = CliParsing.resolveUserPath(tokens.get(++i));
				if ("--data-dir".equals(token)) {
					requireSingleAssignment(dataDir, token, json);
					dataDir = value;
				} else if ("--rms-dir".equals(token)) {
					requireSingleAssignment(rmsDir, token, json);
					rmsDir = value;
				} else {
					requireSingleAssignment(fileRoot, token, json);
					fileRoot = value;
				}
			} else if ("--worker-xmx".equals(token)) {
				if (i + 1 >= tokens.size()) {
					throw usageError(json);
				}
				requireSingleAssignment(workerXmx, token, json);
				workerXmx = tokens.get(++i);
			} else if ("--reset-state".equals(token)) {
				if (resetState) {
					throw duplicateOption(token, json);
				}
				resetState = true;
			} else if ("--wait-ready".equals(token)) {
				if (waitReady) {
					throw duplicateOption(token, json);
				}
				waitReady = true;
			} else if ("--headless".equals(token) || "--visible".equals(token)) {
				startTokens.add(token);
			} else if ("--runtime".equals(token) || "--size".equals(token)) {
				if (i + 1 >= tokens.size()) {
					throw usageError(json);
				}
				startTokens.add(token);
				startTokens.add(tokens.get(++i));
			} else {
				throw usageError(json);
			}
		}

		return new OpenOptions(
			inputPath,
			midlet,
			ControllerLifecycle.parseStartOptions(
				startTokens, 0, "open", json, true, false),
			dataDir,
			rmsDir,
			fileRoot,
			resetState,
			waitReady,
			workerXmx);
	}
}
