package emulator.cli.controller;

import emulator.cli.core.*;
import emulator.cli.output.CliTextRenderer;
import emulator.cli.support.KemuPaths;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.Callable;

public final class ControllerLifecycle {
	private ControllerLifecycle() {
	}

	public static ControllerStatus ensureController(
		StartOptions options, boolean autoStart, String commandName, boolean json) throws Exception {
		return ControllerProcessLauncher.ensureController(options, autoStart, commandName, json);
	}

	public static ControllerStatus requireRunningController(String commandName, boolean json) throws Exception {
		ControllerStatus status = ControllerStatusService.readControllerStatus();
		if (ControllerStatusService.isForeignPidState(status)) {
			ControllerStatusService.cleanupUnreachableController(status, commandName, json);
			status = ControllerStatusService.readControllerStatus();
		}

		if (status.degraded) {
			throw new KemuCliException(
				"CONTROLLER_UNREACHABLE",
				"Controller process exists but is unreachable. Retry with 'kemu stop --force'.",
				CliExitCodes.RUNTIME,
				commandName,
				json);
		}

		if (!status.running) {
			throw new KemuCliException(
				"CONTROLLER_NOT_RUNNING", "Controller is not running.", CliExitCodes.RUNTIME, commandName, json);
		}

		return status;
	}

	public static <T> T withLifecycleLock(Callable<T> action) throws Exception {
		Files.createDirectories(KemuPaths.automationRunDir());
		FileChannel channel = FileChannel.open(
			KemuPaths.automationControllerLock(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		try {
			FileLock lock = channel.lock();
			try {
				return action.call();
			} finally {
				lock.release();
			}
		} finally {
			channel.close();
		}
	}

	public static StartOptions parseStartOptions(
		List<String> tokens,
		int startIndex,
		String commandName,
		boolean json,
		boolean requirePathAlreadyConsumed,
		boolean applyDefaults) {
		return ControllerOptionParsers.parseStartOptions(
			tokens, startIndex, commandName, json, requirePathAlreadyConsumed, applyDefaults);
	}

	public static void requireTokenCount(List<String> tokens, int count, String commandName, boolean json) {
		if (tokens.size() != count) {
			throw new KemuCliException(
				"USAGE_ERROR", CliTextRenderer.usageText(commandName), CliExitCodes.USAGE, commandName, json);
		}
	}
}
