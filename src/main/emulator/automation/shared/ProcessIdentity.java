package emulator.automation.shared;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public final class ProcessIdentity {
	private static final long START_TIME_TOLERANCE_MS = 2000L;

	private ProcessIdentity() {
	}

	public static String currentPid() {
		String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
		int separator = runtimeName.indexOf('@');

		return separator <= 0 ? runtimeName : runtimeName.substring(0, separator);
	}

	public static Long currentStartTimeMillis() {
		try {
			return Long.valueOf(ManagementFactory.getRuntimeMXBean().getStartTime());
		} catch (Throwable ignored) {
			return null;
		}
	}

	private static boolean isLinux() {
		return System.getProperty("os.name", "")
			.toLowerCase(java.util.Locale.US)
			.contains("linux");
	}

	private static boolean isWindows() {
		return System.getProperty("os.name", "")
			.toLowerCase(java.util.Locale.US)
			.contains("win");
	}

	private static String readFully(InputStream in) throws IOException {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}

			return new String(out.toByteArray(), StandardCharsets.UTF_8);
		} finally {
			in.close();
		}
	}

	private static String readLinuxProcStartTicks(String pid) {
		if (!isLinux()) {
			return null;
		}

		Path statPath = "self".equals(pid) ? Paths.get("/proc/self/stat") : Paths.get("/proc", pid, "stat");
		if (!Files.isRegularFile(statPath)) {
			return null;
		}

		try {
			String line = new String(Files.readAllBytes(statPath), StandardCharsets.UTF_8);
			int split = line.lastIndexOf(") ");
			if (split < 0 || split + 2 >= line.length()) {
				return null;
			}

			String[] fields = line.substring(split + 2).trim().split("\\s+");

			return fields.length > 19 ? fields[19] : null;
		} catch (IOException ignored) {
			return null;
		}
	}

	public static String currentStartTicks() {
		return readLinuxProcStartTicks("self");
	}

	private static Boolean matchesProcessHandleStart(String pid, Long expectedStartTimeMs) {
		if (expectedStartTimeMs == null) {
			return null;
		}

		try {
			long value = Long.parseLong(TextValues.trimToNull(pid));
			Class<?> handleClass = Class.forName("java.lang.ProcessHandle");
			Object optional = handleClass.getMethod("of", long.class).invoke(null, value);
			Class<?> optionalClass = Class.forName("java.util.Optional");
			Boolean present = (Boolean) optionalClass.getMethod("isPresent").invoke(optional);
			if (!present.booleanValue()) {
				return Boolean.FALSE;
			}

			Object handle = optionalClass.getMethod("get").invoke(optional);
			Object info = handleClass.getMethod("info").invoke(handle);
			Class<?> infoClass = Class.forName("java.lang.ProcessHandle$Info");
			Object startOptional = infoClass.getMethod("startInstant").invoke(info);
			Boolean hasStart = (Boolean) optionalClass.getMethod("isPresent").invoke(startOptional);
			if (!hasStart.booleanValue()) {
				return null;
			}

			Instant start = (Instant) optionalClass.getMethod("get").invoke(startOptional);

			return Boolean.valueOf(
				Math.abs(start.toEpochMilli() - expectedStartTimeMs.longValue()) <= START_TIME_TOLERANCE_MS);
		} catch (Throwable ignored) {
			return null;
		}
	}

	private static Boolean matchesLinuxProcStartTicks(String pid, String expectedStartTicks) {
		String normalizedExpectedStartTicks = TextValues.trimToNull(expectedStartTicks);
		if (normalizedExpectedStartTicks == null) {
			return null;
		}

		String actualStartTicks = readLinuxProcStartTicks(TextValues.trimToNull(pid));
		if (actualStartTicks == null) {
			return null;
		}

		return Boolean.valueOf(normalizedExpectedStartTicks.equals(actualStartTicks));
	}

	public static Boolean isAlive(String pid) {
		String normalized = TextValues.trimToNull(pid);
		if (normalized == null) {
			return Boolean.FALSE;
		}

		try {
			long value = Long.parseLong(normalized);
			Class<?> handleClass = Class.forName("java.lang.ProcessHandle");
			Object optional = handleClass.getMethod("of", long.class).invoke(null, value);
			Class<?> optionalClass = Class.forName("java.util.Optional");
			Boolean present = (Boolean) optionalClass.getMethod("isPresent").invoke(optional);
			if (!present.booleanValue()) {
				return Boolean.FALSE;
			}

			Object handle = optionalClass.getMethod("get").invoke(optional);

			return (Boolean) handleClass.getMethod("isAlive").invoke(handle);
		} catch (Throwable ignored) {
		}

		try {
			Process probe;
			if (isWindows()) {
				probe = new ProcessBuilder("cmd", "/c", "tasklist", "/FI", "PID eq " + normalized).start();
			} else {
				probe = new ProcessBuilder("kill", "-0", normalized).start();
			}

			try {
				int exit = probe.waitFor();
				if (isWindows()) {
					String output = readFully(probe.getInputStream());

					return Boolean.valueOf(exit == 0 && output != null && output.indexOf(normalized) >= 0);
				}

				return Boolean.valueOf(exit == 0);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();

				return null;
			}
		} catch (Throwable ignored) {
			return null;
		}
	}

	public static Boolean matches(String pid, Long expectedStartTimeMs, String expectedStartTicks) {
		Boolean alive = isAlive(pid);
		if (!Boolean.TRUE.equals(alive)) {
			return alive == null ? null : Boolean.FALSE;
		}

		Boolean byHandle = matchesProcessHandleStart(pid, expectedStartTimeMs);
		if (byHandle != null) {
			return byHandle;
		}

		Boolean byProc = matchesLinuxProcStartTicks(pid, expectedStartTicks);
		if (byProc != null) {
			return byProc;
		}

		return null;
	}
}
