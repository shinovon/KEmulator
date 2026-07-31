package emulator.automation.controller;

import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationException;
import emulator.automation.shared.TextValues;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
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

	Json openPath(String inputPath, Integer midletIndex, Json request) throws Exception {
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
		WorkerProcess worker = workerSupervisor.launchWorker(entry, midletClassName, request);
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

	private static String logCursor(WorkerProcess worker, long offset) {
		return worker.startedAt + ":" + offset;
	}

	private static long parseLogCursor(WorkerProcess worker, String cursor) {
		if (cursor == null || cursor.length() == 0) {
			return 0L;
		}
		int separator = cursor.indexOf(':');
		if (separator <= 0) {
			throw new AutomationException(
				AutomationErrorCodes.INVALID_REQUEST,
				"Invalid log cursor: " + cursor);
		}
		try {
			long startedAt = Long.parseLong(cursor.substring(0, separator));
			long offset = Long.parseLong(cursor.substring(separator + 1));
			if (startedAt != worker.startedAt || offset < 0L) {
				throw new NumberFormatException();
			}
			return offset;
		} catch (NumberFormatException e) {
			throw new AutomationException(
				AutomationErrorCodes.INVALID_REQUEST,
				"Log cursor does not belong to the active worker: " + cursor,
				Json.object().set("cursor", cursor).set("workerStartedAt", worker.startedAt));
		}
	}

	private static Json readWorkerLog(WorkerProcess worker, long offset) throws IOException {
		if (worker.logPath == null || !Files.isRegularFile(worker.logPath)) {
			return Json.object()
				.set("cursor", logCursor(worker, 0L))
				.set("fromOffset", 0L)
				.set("toOffset", 0L)
				.set("text", "");
		}
		long size = Files.size(worker.logPath);
		long from = Math.min(offset, size);
		if (size - from > Integer.MAX_VALUE) {
			from = size - Integer.MAX_VALUE;
		}
		byte[] bytes = new byte[(int) (size - from)];
		RandomAccessFile file = new RandomAccessFile(worker.logPath.toFile(), "r");
		try {
			file.seek(from);
			file.readFully(bytes);
		} finally {
			file.close();
		}
		String text = new String(bytes, StandardCharsets.UTF_8);
		Json lines = Json.array();
		String[] split = text.split("\\r?\\n", -1);
		for (int i = 0; i < split.length; i++) {
			if (i == split.length - 1 && split[i].length() == 0) {
				continue;
			}
			lines.add(Json.object().set("offset", from).set("line", split[i]));
		}
		return Json.object()
			.set("cursor", logCursor(worker, size))
			.set("fromOffset", from)
			.set("toOffset", size)
			.set("text", text)
			.set("lines", lines);
	}

	synchronized Json workerLogCursor() throws Exception {
		cleanupDeadWorkerLocked();
		WorkerProcess worker = activeWorker != null ? activeWorker : lastWorkerForLogs;
		if (worker == null) {
			throw new AutomationException(AutomationErrorCodes.NO_ACTIVE_APP, "No active app");
		}
		long size = worker.logPath != null && Files.isRegularFile(worker.logPath)
			? Files.size(worker.logPath)
			: 0L;
		return Json.object()
			.set("cursor", logCursor(worker, size))
			.set("offset", size)
			.set("worker", worker.toJson());
	}

	synchronized Json workerLogsRead(Json arguments) throws Exception {
		cleanupDeadWorkerLocked();
		WorkerProcess worker = activeWorker != null ? activeWorker : lastWorkerForLogs;
		if (worker == null) {
			throw new AutomationException(AutomationErrorCodes.NO_ACTIVE_APP, "No active app");
		}
		long offset = parseLogCursor(
			worker,
			arguments.has("since") && !arguments.at("since").isNull()
				? arguments.at("since").asString()
				: null);
		Json result = readWorkerLog(worker, offset);
		result.set("worker", worker.toJson());
		return result;
	}

	Json waitWorkerLog(Json arguments) throws Exception {
		WorkerProcess worker = requireActiveWorker();
		String regex = arguments.at("regex", "").asString();
		if (regex.length() == 0) {
			throw new AutomationException(
				AutomationErrorCodes.INVALID_REQUEST,
				"wait log requires regex");
		}
		final Pattern pattern;
		try {
			pattern = Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			throw new AutomationException(
				AutomationErrorCodes.INVALID_REQUEST,
				"Invalid log regex: " + e.getMessage());
		}
		long offset = parseLogCursor(
			worker,
			arguments.has("since") && !arguments.at("since").isNull()
				? arguments.at("since").asString()
				: null);
		long timeoutMs = arguments.at("timeoutMs", 5000L).asLong();
		long start = System.nanoTime();
		long deadline = start + TimeUnit.MILLISECONDS.toNanos(timeoutMs);
		Json last = Json.object();
		WatchService watchService = worker.logPath.getFileSystem().newWatchService();
		worker.logPath.getParent().register(
			watchService,
			StandardWatchEventKinds.ENTRY_CREATE,
			StandardWatchEventKinds.ENTRY_MODIFY);
		try {
			while (true) {
				last = readWorkerLog(worker, offset);
				String text = last.at("text", "").asString();
				if (pattern.matcher(text).find()) {
					return Json.object()
						.set("condition", "log")
						.set("regex", regex)
						.set("matched", true)
						.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))
						.set("cursor", last.at("cursor"))
						.set("text", text)
						.set("worker", worker.toJson());
				}
				offset = last.at("toOffset", offset).asLong();
				if (!worker.process.isAlive()) {
					throw WorkerDiagnostics.workerFailure(
						AutomationErrorCodes.WORKER_FAILURE,
						"Worker exited while waiting for log",
						worker,
						Json.object().set("lastLog", last));
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
				key.reset();
			}
		} finally {
			watchService.close();
		}
		throw new AutomationException(
			AutomationErrorCodes.TIMEOUT,
			"Timed out waiting for log regex: " + regex,
			Json.object()
				.set("condition", "log")
				.set("regex", regex)
				.set("timeoutMs", timeoutMs)
				.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))
				.set("lastState", last)
				.set("worker", worker.toJson()));
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

	Json waitWorkerExit(Json arguments) throws Exception {
		long timeoutMs = arguments.at("timeoutMs", 5000L).asLong();
		long start = System.nanoTime();
		WorkerProcess worker;
		synchronized (this) {
			cleanupDeadWorkerLocked();
			worker = activeWorker != null ? activeWorker : lastWorkerForLogs;
		}
		if (worker == null || worker.process == null || !worker.process.isAlive()) {
			return Json.object()
				.set("condition", "worker-exit")
				.set("exited", true)
				.set("elapsedMs", 0);
		}
		boolean exited;
		try {
			exited = worker.process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AutomationException(
				AutomationErrorCodes.WORKER_FAILURE,
				"Interrupted while waiting for worker exit",
				null,
				e);
		}
		if (!exited) {
			throw new AutomationException(
				AutomationErrorCodes.TIMEOUT,
				"Timed out waiting for worker exit",
				Json.object()
					.set("condition", "worker-exit")
					.set("timeoutMs", timeoutMs)
					.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))
					.set("lastState", worker.toJson()));
		}
		synchronized (this) {
			cleanupDeadWorkerLocked();
		}
		return Json.object()
			.set("condition", "worker-exit")
			.set("exited", true)
			.set("exitCode", worker.process.exitValue())
			.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
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
