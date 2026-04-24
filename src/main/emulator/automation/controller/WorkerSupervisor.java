package emulator.automation.controller;

import java.io.IOException;
import java.nio.file.Path;
import mjson.Json;

final class WorkerSupervisor {
	private final WorkerLauncher launcher;

	WorkerSupervisor(Path logsRoot, int screenWidth, int screenHeight) {
		this.launcher = new WorkerLauncher(logsRoot, screenWidth, screenHeight);
	}

	Json call(WorkerProcess worker, String operation, Json request) throws IOException {
		return WorkerProtocolClient.call(worker, operation, request, WorkerProcessTerminator.timeoutHandler());
	}

	Json callControl(WorkerProcess worker, String operation, Json request) throws IOException {
		return WorkerProtocolClient.callControl(
			worker, operation, request, WorkerProcessTerminator.timeoutHandler());
	}

	void close(WorkerProcess worker) throws Exception {
		WorkerProcessTerminator.close(worker);
	}

	String readLogTail(WorkerProcess worker, int maxLines) throws IOException {
		return WorkerDiagnostics.readLastLines(worker == null ? null : worker.logPath, maxLines);
	}

	WorkerProcess launchWorker(AppTarget entry, String midletClassName) throws IOException {
		return launcher.launch(entry, midletClassName);
	}

	Json waitUntilReady(WorkerProcess worker, long timeoutMs) throws Exception {
		return WorkerReadinessProbe.waitUntilReady(worker, timeoutMs);
	}
}
