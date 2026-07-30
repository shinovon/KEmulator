package emulator.automation.controller;

import emulator.automation.shared.AutomationErrorCodes;
import java.nio.file.Files;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;
import mjson.Json;

final class WorkerReadinessProbe {
	private WorkerReadinessProbe() {
	}

	static Json waitUntilReady(WorkerProcess worker, long timeoutMs) throws Exception {
		long start = System.nanoTime();
		long deadline = start + TimeUnit.MILLISECONDS.toNanos(timeoutMs);
		WatchService watchService = worker.readyPath.getFileSystem().newWatchService();
		worker.readyPath.getParent().register(
			watchService,
			StandardWatchEventKinds.ENTRY_CREATE,
			StandardWatchEventKinds.ENTRY_MODIFY);
		try {
			while (true) {
			if (!worker.process.isAlive()) {
				throw WorkerDiagnostics.workerFailure(
					AutomationErrorCodes.WORKER_FAILURE, "Worker exited before becoming ready", worker, null);
			}

			if (Files.isRegularFile(worker.readyPath)) {
				Json session = WorkerProtocolClient.call(
					worker, "session", Json.object(), WorkerProcessTerminator.timeoutHandler());
				if (session.at("ready", false).asBoolean()) {
					return session;
				}
				throw WorkerDiagnostics.workerFailure(
					AutomationErrorCodes.OPEN_TIMEOUT,
					"Worker ready marker was written before the MIDlet display became ready",
					worker,
					Json.object().set("lastSession", WorkerDiagnostics.stripImage(session)));
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

		throw WorkerDiagnostics.workerFailure(
			AutomationErrorCodes.OPEN_TIMEOUT,
			"Timed out waiting for worker readiness",
			worker,
			Json.object()
				.set("timeoutMs", timeoutMs)
				.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))
				.set("readyPath", worker.readyPath.toString()));
	}
}
