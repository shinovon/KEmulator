package emulator.cli.controller;

import emulator.cli.core.CliExitCodes;
import emulator.cli.core.KemuCliException;
import emulator.cli.support.CliDefaults;
import emulator.cli.support.KemuPaths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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

	private static void waitForControllerReady(
		Process process, int port, Path logFile, String commandName, boolean json) throws Exception {
		long deadline = System.nanoTime()
			+ TimeUnit.MILLISECONDS.toNanos(CliDefaults.START_TIMEOUT_MS);
		WatchService watchService = KemuPaths.automationRunDir().getFileSystem().newWatchService();
		KemuPaths.automationRunDir().register(
			watchService,
			StandardWatchEventKinds.ENTRY_CREATE,
			StandardWatchEventKinds.ENTRY_MODIFY);
		try {
			while (true) {
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
						return;
					}
				} catch (Exception ignored) {
				}

				long remaining = deadline - System.nanoTime();
				if (remaining <= 0L) {
					break;
				}
				WatchKey key = watchService.poll(remaining, TimeUnit.NANOSECONDS);
				if (key == null) {
					break;
				}
				key.pollEvents();
				if (!key.reset()) {
					break;
				}
			}
		} finally {
			watchService.close();
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
		command.add("-Dkemu.session.id=" + KemuPaths.sessionId());
		command.add("-cp");
		command.add(runtime.classpath);
		command.add("emulator.automation.controller.AutomationControllerMain");
		command.add("--runtime-root");
		command.add(KemuPaths.automationSessionDir().toString());
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
		command.add("--session-id");
		command.add(KemuPaths.sessionId());
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
