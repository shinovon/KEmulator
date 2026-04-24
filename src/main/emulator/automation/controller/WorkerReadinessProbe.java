package emulator.automation.controller;

import emulator.automation.shared.AutomationErrorCodes;
import java.io.IOException;
import mjson.Json;

final class WorkerReadinessProbe {
	private WorkerReadinessProbe() {
	}

	static Json waitUntilReady(WorkerProcess worker, long timeoutMs) throws Exception {
		long deadline = System.currentTimeMillis() + timeoutMs;
		Json last = null;
		while (System.currentTimeMillis() < deadline) {
			if (!worker.process.isAlive()) {
				throw WorkerDiagnostics.workerFailure(
					AutomationErrorCodes.WORKER_FAILURE, "Worker exited before becoming ready", worker, null);
			}

			try {
				last = WorkerProtocolClient.call(
					worker, "session", Json.object(), WorkerProcessTerminator.timeoutHandler());
				if (last.at("ready", false).asBoolean()) {
					return last;
				}
			} catch (IOException ignored) {
			}

			Thread.sleep(500L);
		}

		if (last != null) {
			throw WorkerDiagnostics.workerFailure(
				AutomationErrorCodes.OPEN_TIMEOUT,
				"Timed out waiting for worker readiness",
				worker,
				Json.object().set("lastSession", WorkerDiagnostics.stripImage(last)));
		}

		throw WorkerDiagnostics.workerFailure(
			AutomationErrorCodes.OPEN_TIMEOUT, "Timed out waiting for worker startup", worker, null);
	}
}
