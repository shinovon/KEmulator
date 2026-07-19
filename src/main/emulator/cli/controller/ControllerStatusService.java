package emulator.cli.controller;

import emulator.automation.shared.ProcessIdentity;
import emulator.automation.shared.TextValues;
import emulator.cli.core.CliExitCodes;
import emulator.cli.core.KemuCliException;
import emulator.cli.support.KemuPaths;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ControllerStatusService {
	private ControllerStatusService() {
	}

	public static final class LogTail {
		public final boolean exists;
		public final String tail;

		LogTail(boolean exists, String tail) {
			this.exists = exists;
			this.tail = tail;
		}
	}

	public static ControllerClient controllerClient(ControllerStatus status) {
		return new ControllerClient(status.host, status.port == null ? -1 : status.port.intValue());
	}

	public static String controllerLogPath(ControllerStatus status) {
		if (status != null && status.logFile != null) {
			return status.logFile;
		}

		return KemuPaths.automationControllerLog().toAbsolutePath().normalize().toString();
	}

	public static void deleteStateFiles() throws IOException {
		Files.deleteIfExists(KemuPaths.automationControllerState());
	}

	private static String trimTrailingLineBreaks(String value) {
		int start = 0;
		int end = value.length();
		while (start < end && (value.charAt(start) == '\n' || value.charAt(start) == '\r')) {
			start++;
		}

		while (end > start && (value.charAt(end - 1) == '\n' || value.charAt(end - 1) == '\r')) {
			end--;
		}

		return value.substring(start, end);
	}

	public static LogTail readLogTail(Path path, int maxLines) throws IOException {
		if (path == null || !Files.isRegularFile(path)) {
			return new LogTail(false, "");
		}

		if (maxLines <= 0) {
			return new LogTail(true, "");
		}

		long fileSize = Files.size(path);
		if (fileSize <= 0L) {
			return new LogTail(true, "");
		}

		RandomAccessFile reader = new RandomAccessFile(path.toFile(), "r");
		try {
			long start = 0L;
			long position = fileSize - 1L;
			int seenLines = 0;
			while (position >= 0L) {
				reader.seek(position);
				if (reader.read() == '\n' && position < fileSize - 1L) {
					seenLines++;
					if (seenLines >= maxLines) {
						start = position + 1L;
						break;
					}
				}

				position--;
			}

			long length = fileSize - start;
			if (length > Integer.MAX_VALUE) {
				start = fileSize - Integer.MAX_VALUE;
				length = Integer.MAX_VALUE;
			}

			byte[] bytes = new byte[(int) length];
			reader.seek(start);
			reader.readFully(bytes);

			return new LogTail(true, trimTrailingLineBreaks(new String(bytes, StandardCharsets.UTF_8)));
		} finally {
			reader.close();
		}
	}

	public static String readLastLines(Path path, int maxLines) throws IOException {
		return readLogTail(path, maxLines).tail;
	}

	public static boolean isLinux() {
		return System.getProperty("os.name", "")
			.toLowerCase(java.util.Locale.US)
			.contains("linux");
	}

	public static boolean isWindows() {
		return System.getProperty("os.name", "")
			.toLowerCase(java.util.Locale.US)
			.contains("win");
	}

	public static boolean isPidAlive(String pid) {
		return Boolean.TRUE.equals(ProcessIdentity.isAlive(pid));
	}

	public static boolean isForeignPidState(ControllerStatus status) {
		return status != null
			&& Boolean.TRUE.equals(status.pidAlive)
			&& Boolean.FALSE.equals(status.pidIdentityMatches);
	}

	private static boolean probeController(ControllerStatus status) {
		if (status == null || status.host == null || status.port == null) {
			return false;
		}

		try {
			return controllerClient(status).ping();
		} catch (Exception ignored) {
			return false;
		}
	}

	private static void requireSafePidMatch(ControllerStatus status, String commandName, boolean json) {
		if (status == null || TextValues.isBlank(status.pid)) {
			return;
		}

		if (Boolean.TRUE.equals(status.pidIdentityMatches)) {
			return;
		}

		throw new KemuCliException(
			"CONTROLLER_UNREACHABLE",
			"Controller PID identity cannot be verified safely. Refusing to kill PID " + status.pid + '.',
			CliExitCodes.RUNTIME,
			commandName,
			json);
	}

	public static void forceStopPid(ControllerStatus status, String commandName, boolean json) throws Exception {
		requireSafePidMatch(status, commandName, json);
		String pid = status == null ? null : TextValues.trimToNull(status.pid);
		if (pid == null) {
			return;
		}

		java.util.List<String> command = new java.util.ArrayList<String>();
		if (isWindows()) {
			command.add("taskkill");
			command.add("/PID");
			command.add(pid);
			command.add("/F");
		} else {
			command.add("kill");
			command.add("-KILL");
			command.add(pid);
		}

		Process process = new ProcessBuilder(command).start();
		try {
			process.waitFor();
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}
	}

	public static ControllerStatus readControllerStatus() throws Exception {
		Path automation = KemuPaths.automationControllerState();
		if (!Files.exists(automation)) {
			return ControllerStatus.empty();
		}

		ControllerStatus status;
		try {
			status = ControllerStatus.fromStateFile("automation", automation);
		} catch (IOException e) {
			status = ControllerStatus.corrupt("automation", automation, e.getMessage());
		}

		status.pidAlive = ProcessIdentity.isAlive(status.pid);
		status.pidIdentityMatches = ProcessIdentity.matches(status.pid, status.pidStartTimeMs, status.pidStartTicks);
		status.reachable = probeController(status);
		status.running = status.reachable;
		status.degraded = !status.reachable && Boolean.TRUE.equals(status.pidAlive) && !isForeignPidState(status);

		return status;
	}

	public static boolean waitForControllerStop(ControllerStatus status, long timeoutMs) throws Exception {
		String originalPid = status == null ? null : status.pid;
		long deadline = System.currentTimeMillis() + timeoutMs;
		while (System.currentTimeMillis() < deadline) {
			ControllerStatus current = readControllerStatus();
			Boolean pidAlive = current.pid != null ? current.pidAlive : ProcessIdentity.isAlive(originalPid);
			if (!current.reachable && Boolean.FALSE.equals(pidAlive)) {
				return true;
			}

			if (originalPid == null && !current.exists && !current.reachable) {
				return true;
			}

			Thread.sleep(250L);
		}

		return false;
	}

	public static void cleanupUnreachableController(ControllerStatus status, String commandName, boolean json)
		throws Exception {
		if (status == null || !status.exists) {
			return;
		}

		if (isForeignPidState(status)) {
			deleteStateFiles();

			return;
		}

		if (Boolean.TRUE.equals(status.pidAlive)) {
			requireSafePidMatch(status, commandName, json);
			forceStopPid(status, commandName, json);
			waitForControllerStop(status, 5000L);
		}

		deleteStateFiles();
	}
}
