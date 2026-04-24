package emulator.automation.controller;

import emulator.automation.shared.ProcessIdentity;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class AutomationControllerMain {
	private AutomationControllerMain() {
	}

	private static void writeStateFile(
		Path stateFile,
		String bindHost,
		int port,
		int width,
		int height,
		Path logFile,
		String mode,
		String runtime,
		String currentPid,
		Long currentStartTimeMs,
		String currentStartTicks,
		String currentStateId)
		throws IOException {
		Files.createDirectories(stateFile.getParent());
		StringBuilder content = new StringBuilder();
		if (currentStateId != null && currentStateId.length() > 0) {
			content.append("STATE_ID=").append(currentStateId).append('\n');
		}

		content.append("PID=").append(currentPid).append('\n');
		if (currentStartTimeMs != null) {
			content.append("PID_START_TIME_MS=")
				.append(currentStartTimeMs.longValue())
				.append('\n');
		}

		if (currentStartTicks != null && currentStartTicks.length() > 0) {
			content.append("PID_START_TICKS=").append(currentStartTicks).append('\n');
		}

		content.append("HOST=").append(bindHost).append('\n');
		content.append("PORT=").append(port).append('\n');
		content.append("SCREEN=").append(width).append('x').append(height).append('\n');
		if (logFile != null) {
			content.append("LOG_FILE=").append(logFile.toString()).append('\n');
		}

		if (mode != null) {
			content.append("MODE=").append(mode).append('\n');
		}

		if (runtime != null) {
			content.append("RUNTIME=").append(runtime).append('\n');
		}

		Path tempFile = stateFile.resolveSibling(stateFile.getFileName().toString() + ".tmp");
		Files.write(tempFile, content.toString().getBytes(StandardCharsets.UTF_8));
		try {
			Files.move(tempFile, stateFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (AtomicMoveNotSupportedException ignored) {
			Files.move(tempFile, stateFile, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private static void deleteOwnedStateFile(
		Path stateFile,
		String expectedPid,
		Long expectedStartTimeMs,
		String expectedStartTicks,
		String expectedStateId)
		throws IOException {
		if (stateFile == null || expectedPid == null || !Files.isRegularFile(stateFile)) {
			return;
		}

		Map properties = readKeyValueFile(stateFile);
		if (expectedStateId != null && expectedStateId.length() > 0) {
			Object currentStateId = properties.get("STATE_ID");
			if (currentStateId == null || !expectedStateId.equals(currentStateId.toString())) {
				return;
			}
		}

		Object currentPid = properties.get("PID");
		if (!expectedPid.equals(currentPid)) {
			return;
		}

		if (expectedStartTimeMs != null) {
			Object currentStartTimeMs = properties.get("PID_START_TIME_MS");
			if (currentStartTimeMs != null
				&& currentStartTimeMs.toString().length() > 0
				&& !String.valueOf(expectedStartTimeMs.longValue()).equals(currentStartTimeMs.toString())) {
				return;
			}
		}

		if (expectedStartTicks != null && expectedStartTicks.length() > 0) {
			Object currentStartTicks = properties.get("PID_START_TICKS");
			if (currentStartTicks != null
				&& currentStartTicks.toString().length() > 0
				&& !expectedStartTicks.equals(currentStartTicks.toString())) {
				return;
			}
		}

		Files.deleteIfExists(stateFile);
	}

	private static Map readKeyValueFile(Path path) throws IOException {
		Map result = new LinkedHashMap();
		BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				int idx = line.indexOf('=');
				if (idx <= 0) {
					continue;
				}

				result.put(line.substring(0, idx), line.substring(idx + 1));
			}
		} finally {
			reader.close();
		}

		return result;
	}

	public static void main(String[] args) throws Exception {
		Path runtimeRoot = Paths.get("automation").toAbsolutePath().normalize();
		String bindHost = "127.0.0.1";
		int port = 8765;
		int width = 240;
		int height = 320;
		Path stateFile = null;
		String mode = null;
		String runtime = null;
		Path logFile = null;

		for (int i = 0; i < args.length; i++) {
			String key = args[i];
			String value = i + 1 < args.length ? args[i + 1] : null;
			if ("--runtime-root".equals(key) && value != null) {
				runtimeRoot = Paths.get(value).toAbsolutePath().normalize();
				i++;
			} else if ("--host".equals(key) && value != null) {
				bindHost = value;
				i++;
			} else if ("--port".equals(key) && value != null) {
				port = Integer.parseInt(value);
				i++;
			} else if ("--screen".equals(key) && value != null) {
				String[] split = value.toLowerCase().split("x");
				if (split.length == 2) {
					width = Integer.parseInt(split[0]);
					height = Integer.parseInt(split[1]);
				}

				i++;
			} else if ("--state-file".equals(key) && value != null) {
				stateFile = Paths.get(value).toAbsolutePath().normalize();
				i++;
			} else if ("--mode".equals(key) && value != null) {
				mode = value;
				i++;
			} else if ("--runtime".equals(key) && value != null) {
				runtime = value;
				i++;
			} else if ("--log-file".equals(key) && value != null) {
				logFile = Paths.get(value).toAbsolutePath().normalize();
				i++;
			}
		}

		final Path finalStateFile = stateFile;
		final String currentPid = ProcessIdentity.currentPid();
		final Long currentStartTimeMs = ProcessIdentity.currentStartTimeMillis();
		final String currentStartTicks = ProcessIdentity.currentStartTicks();
		final String currentStateId = UUID.randomUUID().toString();
		ControllerServer server = new ControllerServer(bindHost, port, width, height, runtimeRoot);
		server.start();
		if (finalStateFile != null) {
			writeStateFile(
				finalStateFile,
				bindHost,
				port,
				width,
				height,
				logFile,
				mode,
				runtime,
				currentPid,
				currentStartTimeMs,
				currentStartTicks,
				currentStateId);
			Runtime.getRuntime()
				.addShutdownHook(new Thread(
					new Runnable() {
						public void run() {
							try {
								deleteOwnedStateFile(
									finalStateFile,
									currentPid,
									currentStartTimeMs,
									currentStartTicks,
									currentStateId);
							} catch (IOException ignored) {
							}
						}
					},
					"KEmulator-Controller-State-Cleanup"));
		}

		System.out.println("KEmulator automation controller listening on " + bindHost + ':' + port);
		synchronized (AutomationControllerMain.class) {
			AutomationControllerMain.class.wait();
		}
	}
}
