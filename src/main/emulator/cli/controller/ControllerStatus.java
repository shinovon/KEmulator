package emulator.cli.controller;

import emulator.automation.shared.TextValues;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import mjson.Json;

public final class ControllerStatus {
	public boolean exists;
	public String source;
	public Path stateFile;
	public Map<String, String> properties;
	public String pid;
	public Long pidStartTimeMs;
	public String pidStartTicks;
	public Boolean pidAlive;
	public Boolean pidIdentityMatches;
	public boolean reachable;
	public boolean degraded;
	public String host;
	public Integer port;
	public String mode;
	public String runtime;
	public String screen;
	public String logFile;
	public String loadError;
	public boolean running;

	public static ControllerStatus empty() {
		ControllerStatus status = new ControllerStatus();
		status.exists = false;
		status.source = "none";
		status.properties = new LinkedHashMap<String, String>();
		status.running = false;

		return status;
	}

	public static ControllerStatus corrupt(String source, Path stateFile, String loadError) {
		ControllerStatus status = new ControllerStatus();
		status.exists = true;
		status.source = source;
		status.stateFile = stateFile;
		status.properties = new LinkedHashMap<String, String>();
		status.loadError = loadError;

		return status;
	}

	private static Map<String, String> readKeyValueFile(Path path) throws IOException {
		Map<String, String> result = new LinkedHashMap<String, String>();
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

	private static String firstValue(Map<String, String> map, String... keys) {
		for (String key : keys) {
			String value = map.get(key);
			if (value != null && value.length() > 0) {
				return value;
			}
		}

		return null;
	}

	private static Integer safeParseInteger(String value) {
		String normalized = TextValues.trimToNull(value);
		if (normalized == null) {
			return null;
		}

		try {
			return Integer.valueOf(Integer.parseInt(normalized));
		} catch (NumberFormatException ignored) {
			return null;
		}
	}

	private static Long safeParseLong(String value) {
		String normalized = TextValues.trimToNull(value);
		if (normalized == null) {
			return null;
		}

		try {
			return Long.valueOf(Long.parseLong(normalized));
		} catch (NumberFormatException ignored) {
			return null;
		}
	}

	public static ControllerStatus fromStateFile(String source, Path stateFile) throws IOException {
		ControllerStatus status = new ControllerStatus();
		status.exists = true;
		status.source = source;
		status.stateFile = stateFile;
		status.properties = readKeyValueFile(stateFile);
		status.pid = firstValue(status.properties, "PID");
		status.pidStartTimeMs = safeParseLong(firstValue(status.properties, "PID_START_TIME_MS"));
		status.pidStartTicks = firstValue(status.properties, "PID_START_TICKS");
		status.host = firstValue(status.properties, "HOST");
		status.port = safeParseInteger(firstValue(status.properties, "PORT"));
		status.mode = firstValue(status.properties, "MODE");
		status.runtime = firstValue(status.properties, "RUNTIME");
		status.screen = firstValue(status.properties, "SCREEN");
		status.logFile = firstValue(status.properties, "LOG_FILE");

		return status;
	}

	public String endpoint() {
		if (host == null || port == null) {
			return null;
		}

		return host + ':' + port;
	}

	public Json toJson() {
		Json props = Json.object();
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			props.set(entry.getKey(), entry.getValue());
		}

		return Json.object()
			.set("exists", exists)
			.set("source", source)
			.set("stateFile", stateFile == null ? null : stateFile.toString())
			.set("running", running)
			.set("reachable", reachable)
			.set("degraded", degraded)
			.set("pid", pid)
			.set("pidIdentityMatches", pidIdentityMatches)
			.set("pidAlive", pidAlive)
			.set("host", host)
			.set("port", port)
			.set("endpoint", endpoint())
			.set("mode", mode)
			.set("runtime", runtime)
			.set("screen", screen)
			.set("logFile", logFile)
			.set("loadError", loadError)
			.set("properties", props);
	}
}
