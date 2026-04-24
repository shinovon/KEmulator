package emulator.cli.controller;

import emulator.cli.core.CliExitCodes;
import emulator.cli.core.KemuCliException;
import emulator.cli.support.CliDefaults;
import emulator.cli.support.KemuPaths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

final class ControllerProcessLauncher {
	private ControllerProcessLauncher() {
	}

	private static void validateControllerCompatibility(
		ControllerStatus status, StartOptions options, String commandName, boolean json) {
		if (options.mode != null && status.mode != null && !options.mode.equals(status.mode)) {
			throw new KemuCliException(
				"CONFLICTING_CONTROLLER_DEFAULTS",
				"Controller mode is " + status.mode + ", not " + options.mode + '.',
				CliExitCodes.RUNTIME,
				commandName,
				json);
		}

		if (options.runtime != null && status.runtime != null && !options.runtime.equals(status.runtime)) {
			throw new KemuCliException(
				"CONFLICTING_CONTROLLER_DEFAULTS",
				"Controller runtime is " + status.runtime + ", not " + options.runtime + '.',
				CliExitCodes.RUNTIME,
				commandName,
				json);
		}

		if (options.width != null && options.height != null && status.screen != null) {
			String expected = options.width + "x" + options.height;
			if (!expected.equals(status.screen)) {
				throw new KemuCliException(
					"CONFLICTING_CONTROLLER_DEFAULTS",
					"Controller screen is " + status.screen + ", not " + expected + '.',
					CliExitCodes.RUNTIME,
					commandName,
					json);
			}
		}
	}

	private static boolean sameReadyController(ControllerStatus left, ControllerStatus right) {
		if (left == null || right == null) {
			return false;
		}

		if (left.port == null || right.port == null || left.port.intValue() != right.port.intValue()) {
			return false;
		}

		if (left.pid == null || right.pid == null || !left.pid.equals(right.pid)) {
			return false;
		}

		if (left.stateFile == null || right.stateFile == null) {
			return false;
		}

		return left.stateFile.equals(right.stateFile);
	}

	private static void waitForControllerReady(
		Process process, int port, Path logFile, String commandName, boolean json) throws Exception {
		long deadline = System.currentTimeMillis() + CliDefaults.START_TIMEOUT_MS;
		ControllerStatus stableStatus = null;
		int stableMatches = 0;
		while (System.currentTimeMillis() < deadline) {
			if (!process.isAlive()) {
				throw new KemuCliException(
					"START_FAILED",
					"Controller exited before becoming ready.\n"
						+ ControllerStatusService.readLastLines(logFile, 40),
					CliExitCodes.RUNTIME,
					commandName,
					json);
			}

			try {
				ControllerStatus status = ControllerStatusService.readControllerStatus();
				if (status.running && status.port != null && status.port.intValue() == port) {
					if (sameReadyController(stableStatus, status)) {
						stableMatches++;
					} else {
						stableStatus = status;
						stableMatches = 1;
					}

					if (stableMatches >= 2) {
						return;
					}
				} else {
					stableStatus = null;
					stableMatches = 0;
				}
			} catch (Exception ignored) {
				stableStatus = null;
				stableMatches = 0;
			}

			Thread.sleep(250L);
		}

		throw new KemuCliException(
			"START_TIMEOUT",
			"Timed out waiting for controller readiness.\n" + ControllerStatusService.readLastLines(logFile, 40),
			CliExitCodes.RUNTIME,
			commandName,
			json);
	}

	private static ControllerStatus startController(StartOptions options, String commandName, boolean json)
		throws Exception {
		ControllerStatusService.deleteStateFiles();
		Files.createDirectories(KemuPaths.automationRunDir());
		Files.createDirectories(KemuPaths.automationLogsDir());
		Files.createDirectories(KemuPaths.automationCapturesDir());

		ResolvedRuntime runtime = ControllerRuntimeResolver.resolveRuntime(options.runtime, commandName, json);
		Path logFile = KemuPaths.automationControllerLog();
		Files.deleteIfExists(logFile);

		String actualMode = ControllerRuntimeResolver.resolveControllerMode(options, commandName, json);
		int actualWidth = options.width == null ? CliDefaults.DEFAULT_WIDTH : options.width.intValue();
		int actualHeight = options.height == null ? CliDefaults.DEFAULT_HEIGHT : options.height.intValue();
		int port = ControllerRuntimeResolver.findFreePort();
		ArrayList<String> command = new ArrayList<String>();
		if ("headless".equals(actualMode)) {
			command.add("xvfb-run");
			command.add("-a");
			command.add("-s");
			command.add("-screen 0 1280x720x24");
		}

		command.add(ControllerRuntimeResolver.javaBinary());
		command.add("-Dkemu.root=" + KemuPaths.rootDir().toString());
		command.add("-Dkemu.runtime.root=" + KemuPaths.runtimeRootDir().toString());
		command.add("-cp");
		command.add(runtime.classpath);
		command.add("emulator.automation.controller.AutomationControllerMain");
		command.add("--runtime-root");
		command.add(KemuPaths.automationDir().toString());
		command.add("--host");
		command.add(CliDefaults.DEFAULT_HOST);
		command.add("--port");
		command.add(String.valueOf(port));
		command.add("--screen");
		command.add(actualWidth + "x" + actualHeight);
		command.add("--state-file");
		command.add(KemuPaths.automationControllerState().toString());
		command.add("--mode");
		command.add(actualMode);
		command.add("--runtime");
		command.add(runtime.kind);
		command.add("--log-file");
		command.add(logFile.toString());

		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(KemuPaths.rootDir().toFile());
		builder.redirectErrorStream(true);
		builder.redirectOutput(logFile.toFile());
		builder.environment().put("SWT_GTK4", "0");
		Process process = builder.start();
		waitForControllerReady(process, port, logFile, commandName, json);
		ControllerStatus status = ControllerStatusService.readControllerStatus();
		if (!status.running) {
			throw new KemuCliException(
				"START_FAILED", "Controller did not become ready.", CliExitCodes.RUNTIME, commandName, json);
		}

		return status;
	}

	static ControllerStatus ensureController(StartOptions options, boolean autoStart, String commandName, boolean json)
		throws Exception {
		ControllerStatus status = ControllerStatusService.readControllerStatus();
		if (status.running) {
			validateControllerCompatibility(status, options, commandName, json);

			return status;
		}

		if (status.degraded) {
			ControllerStatusService.cleanupUnreachableController(status, commandName, json);
			status = ControllerStatusService.readControllerStatus();
			if (status.running) {
				validateControllerCompatibility(status, options, commandName, json);

				return status;
			}
		}

		if (!autoStart) {
			if (status.degraded) {
				throw new KemuCliException(
					"CONTROLLER_UNREACHABLE",
					"Controller process exists but is unreachable.",
					CliExitCodes.RUNTIME,
					commandName,
					json);
			}

			throw new KemuCliException(
				"CONTROLLER_NOT_RUNNING", "Controller is not running.", CliExitCodes.RUNTIME, commandName, json);
		}

		return startController(options, commandName, json);
	}
}
