package emulator.automation.controller;

import java.util.concurrent.TimeUnit;
import mjson.Json;

final class WorkerProcessTerminator {
	private static final long WORKER_TERMINATE_WAIT_MS = 1500L;
	private static final WorkerProtocolClient.TimeoutHandler TIMEOUT_HANDLER =
		new WorkerProtocolClient.TimeoutHandler() {
			public void terminate(WorkerProcess worker) {
				WorkerProcessTerminator.terminate(worker);
			}
		};

	private WorkerProcessTerminator() {
	}

	static WorkerProtocolClient.TimeoutHandler timeoutHandler() {
		return TIMEOUT_HANDLER;
	}

	static void terminate(WorkerProcess worker) {
		if (worker == null || worker.process == null) {
			return;
		}

		if (worker.process.isAlive()) {
			worker.process.destroy();
			try {
				worker.process.waitFor(WORKER_TERMINATE_WAIT_MS, TimeUnit.MILLISECONDS);
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}
		}

		if (worker.process.isAlive()) {
			worker.process.destroyForcibly();
			try {
				worker.process.waitFor(WORKER_TERMINATE_WAIT_MS, TimeUnit.MILLISECONDS);
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}
		}
	}

	static void close(WorkerProcess worker) throws Exception {
		if (worker == null) {
			return;
		}

		try {
			WorkerProtocolClient.callControl(worker, "shutdown", Json.object(), TIMEOUT_HANDLER);
		} catch (Exception ignored) {
		}

		try {
			worker.process.waitFor(WORKER_TERMINATE_WAIT_MS, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ignored) {
		}

		terminate(worker);
	}
}
