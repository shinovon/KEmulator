package emulator.automation.controller;

import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationException;
import emulator.automation.shared.TextValues;
import java.io.IOException;
import java.nio.file.Paths;
import mjson.Json;

final class ControllerWorkerSession {
	private final AppTargetResolver entryResolver;
	private final WorkerSupervisor workerSupervisor;
	private WorkerProcess activeWorker;
	private WorkerProcess lastWorkerForLogs;
	private Json lastWorkerFailure;

	ControllerWorkerSession(AppTargetResolver entryResolver, WorkerSupervisor workerSupervisor) {
		this.entryResolver = entryResolver;
		this.workerSupervisor = workerSupervisor;
	}

	private Json buildWorkerFailure(WorkerProcess worker, String message) {
		Json failure = Json.object().set("code", AutomationErrorCodes.WORKER_FAILURE).set("message", message);
		Json details = Json.object();
		if (worker != null) {
			if (worker.entry != null) {
				details.set("app", worker.entry.toJson());
			}

			details.set("worker", worker.toJson());
			try {
				String tail = workerSupervisor.readLogTail(worker, 30).trim();
				if (tail.length() > 0) {
					details.set("logTail", tail);
				}
			} catch (IOException ignored) {
			}
		}

		if (!details.asJsonMap().isEmpty()) {
			failure.set("details", details);
		}

		return failure;
	}

	private void recordUnexpectedWorkerDeathLocked(WorkerProcess worker, String message) {
		if (worker == null) {
			return;
		}

		lastWorkerForLogs = worker;
		lastWorkerFailure = buildWorkerFailure(worker, message);
		activeWorker = null;
	}

	private void clearWorkerFailureLocked() {
		lastWorkerFailure = null;
	}

	private AutomationException lastWorkerFailureExceptionLocked() {
		if (lastWorkerFailure == null || !lastWorkerFailure.isObject()) {
			return null;
		}

		String code = lastWorkerFailure.has("code")
			? lastWorkerFailure.at("code").asString()
			: AutomationErrorCodes.WORKER_FAILURE;
		String message = lastWorkerFailure.has("message")
			? lastWorkerFailure.at("message").asString()
			: "Worker exited unexpectedly";
		Json details = lastWorkerFailure.has("details") ? lastWorkerFailure.at("details") : null;

		return new AutomationException(code, message, details);
	}

	private void cleanupDeadWorkerLocked() {
		if (activeWorker != null && (activeWorker.process == null || !activeWorker.process.isAlive())) {
			recordUnexpectedWorkerDeathLocked(activeWorker, "Worker exited unexpectedly");
		}
	}

	private WorkerProcess detachActiveWorkerLocked() {
		WorkerProcess worker = activeWorker;
		activeWorker = null;

		return worker;
	}

	private synchronized WorkerProcess requireActiveWorker() {
		cleanupDeadWorkerLocked();
		if (activeWorker == null) {
			AutomationException failure = lastWorkerFailureExceptionLocked();
			if (failure != null) {
				throw failure;
			}

			throw new AutomationException(AutomationErrorCodes.NO_ACTIVE_APP, "No active app");
		}

		return activeWorker;
	}

	synchronized Json currentGame() {
		cleanupDeadWorkerLocked();
		Json result = Json.object();
		if (activeWorker == null) {
			result.set("active", false);
			if (lastWorkerFailure != null) {
				result.set("failure", lastWorkerFailure.dup());
			}

			return result;
		}

		result.set("active", true);
		result.set("app", activeWorker.entry.toJson());
		result.set("worker", activeWorker.toJson());

		return result;
	}

	Json openPath(String inputPath, Integer midletIndex) throws Exception {
		String normalizedInputPath = TextValues.trimToNull(inputPath);
		if (normalizedInputPath == null) {
			throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "open path requires a path");
		}

		AppTarget entry;
		synchronized (this) {
			cleanupDeadWorkerLocked();
			if (activeWorker != null) {
				throw new AutomationException(AutomationErrorCodes.APP_ALREADY_OPEN, "Another app is already active");
			}
		}

		entry = entryResolver.inspect(
			Paths.get(normalizedInputPath).toAbsolutePath().normalize());
		String midletClassName = entryResolver.resolveMidletClass(entry, midletIndex);
		WorkerProcess worker = workerSupervisor.launchWorker(entry, midletClassName);
		try {
			Json session = workerSupervisor.waitUntilReady(worker, 30000L);
			synchronized (this) {
				lastWorkerForLogs = null;
				clearWorkerFailureLocked();
				activeWorker = worker;
			}

			return Json.object()
				.set("app", entry.toJson())
				.set("worker", worker.toJson())
				.set("session", session);
		} catch (Exception e) {
			workerSupervisor.close(worker);
			throw e;
		}
	}

	Json closeGame() throws Exception {
		WorkerProcess worker;
		AppTarget entry;
		synchronized (this) {
			cleanupDeadWorkerLocked();
			worker = detachActiveWorkerLocked();
			entry = worker == null ? null : worker.entry;
			if (worker == null) {
				lastWorkerForLogs = null;
				clearWorkerFailureLocked();
			}
		}

		Json result = Json.object();
		if (worker == null) {
			result.set("closed", false);
			result.set("reason", "not_running");

			return result;
		}

		workerSupervisor.close(worker);
		synchronized (this) {
			lastWorkerForLogs = null;
			clearWorkerFailureLocked();
		}

		result.set("closed", true);
		result.set("app", entry.toJson());

		return result;
	}

	Json sessionInfo() throws Exception {
		WorkerProcess worker = requireActiveWorker();

		return Json.object()
			.set("app", worker.entry.toJson())
			.set("worker", worker.toJson())
			.set("session", workerSupervisor.call(worker, "session", Json.object()));
	}

	synchronized Json workerLogTail(Json arguments) throws Exception {
		cleanupDeadWorkerLocked();
		WorkerProcess worker = activeWorker != null ? activeWorker : lastWorkerForLogs;
		if (worker == null) {
			throw new AutomationException(AutomationErrorCodes.NO_ACTIVE_APP, "No active app");
		}

		int maxLines = arguments.at("maxLines", 80).asInteger();
		if (maxLines <= 0) {
			throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "maxLines must be positive");
		}

		return Json.object()
			.set("app", worker.entry.toJson())
			.set("worker", worker.toJson())
			.set("tail", workerSupervisor.readLogTail(worker, maxLines));
	}

	Json observe(Json arguments) throws Exception {
		WorkerProcess worker = requireActiveWorker();

		return workerSupervisor.call(
			worker,
			"observe",
			Json.object()
				.set("includeImage", arguments.at("includeImage", true).asBoolean()));
	}

	Json proxyWorker(String operation, Json arguments) throws Exception {
		return workerSupervisor.call(requireActiveWorker(), operation, arguments);
	}

	Json proxyWorkerControl(String operation, Json arguments) throws Exception {
		return workerSupervisor.callControl(requireActiveWorker(), operation, arguments);
	}

	Json captureSnapshot(Json arguments) throws Exception {
		WorkerProcess worker = requireActiveWorker();
		Json observe = workerSupervisor.call(worker, "observe", Json.object().set("includeImage", true));
		String imageBase64 = observe.at("imageBase64") == null
			? null
			: observe.at("imageBase64").asString();
		if (imageBase64 == null || imageBase64.length() == 0) {
			throw new AutomationException(AutomationErrorCodes.SCREENSHOT_FAILED, "Worker did not return image data");
		}

		return Json.object()
			.set("imageBase64", imageBase64)
			.set("app", worker.entry.toJson())
			.set("state", WorkerDiagnostics.stripImage(observe));
	}

	void closeActiveForShutdown() throws Exception {
		WorkerProcess worker;
		synchronized (this) {
			worker = detachActiveWorkerLocked();
			lastWorkerForLogs = null;
			clearWorkerFailureLocked();
		}

		workerSupervisor.close(worker);
	}
}
