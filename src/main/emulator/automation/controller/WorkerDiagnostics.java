package emulator.automation.controller;

import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationErrorPayloads;
import emulator.automation.shared.AutomationException;
import emulator.automation.shared.AutomationRemoteException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import mjson.Json;

final class WorkerDiagnostics {
	private WorkerDiagnostics() {
	}

	private static void mergeDetails(Json target, Json extraDetails) {
		if (extraDetails == null || extraDetails.isNull()) {
			return;
		}

		if (!extraDetails.isObject()) {
			target.set("context", extraDetails);

			return;
		}

		for (String key : extraDetails.asJsonMap().keySet()) {
			target.set(key, extraDetails.at(key));
		}
	}

	static String truncateResponse(String response) {
		if (response == null) {
			return "(null)";
		}

		String normalized = response.trim();

		return normalized.length() > 160 ? normalized.substring(0, 160) + "..." : normalized;
	}

	static String readLastLines(Path path, int maxLines) throws IOException {
		if (path == null || !Files.isRegularFile(path)) {
			return "";
		}

		ArrayDeque<String> tail = new ArrayDeque<String>();
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				tail.addLast(line);
				while (tail.size() > maxLines) {
					tail.removeFirst();
				}
			}
		} finally {
			reader.close();
		}

		StringBuilder builder = new StringBuilder();
		for (String line : tail) {
			if (builder.length() > 0) {
				builder.append('\n');
			}

			builder.append(line);
		}

		return builder.toString();
	}

	static Json stripImage(Json source) {
		Json copy = source.dup();
		copy.delAt("imageBase64");

		return copy;
	}

	static Json buildWorkerDetails(WorkerProcess worker, Json extraDetails) {
		Json details = Json.object();
		if (worker != null) {
			if (worker.entry != null) {
				details.set("app", worker.entry.toJson());
			}

			details.set("worker", worker.toJson());
			try {
				String logTail = readLastLines(worker.logPath, 30).trim();
				if (logTail.length() > 0) {
					details.set("logTail", logTail);
				}
			} catch (IOException ignored) {
			}
		}

		mergeDetails(details, extraDetails);

		return details.asJsonMap().isEmpty() ? null : details;
	}

	static AutomationException workerFailure(
		String code, String message, WorkerProcess worker, Json extraDetails) {
		return new AutomationException(code, message, buildWorkerDetails(worker, extraDetails));
	}

	static AutomationRemoteException workerRemoteFailure(
		WorkerProcess worker, String code, String message, Json extraDetails) {
		return new AutomationRemoteException(code, message, buildWorkerDetails(worker, extraDetails));
	}

	static AutomationRemoteException workerRemoteFailure(WorkerProcess worker, Json error, String fallbackMessage) {
		AutomationRemoteException remote = AutomationErrorPayloads.remoteException(error,
			AutomationErrorCodes.WORKER_FAILURE, fallbackMessage);

		return new AutomationRemoteException(
			remote.code, remote.getMessage(), buildWorkerDetails(worker, remote.details));
	}
}
