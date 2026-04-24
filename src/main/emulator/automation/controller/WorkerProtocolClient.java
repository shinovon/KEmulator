package emulator.automation.controller;

import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.TextValues;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;
import mjson.Json;

final class WorkerProtocolClient {
	private static final int WORKER_CONNECT_TIMEOUT_MS = 1500;
	private static final int WORKER_READ_TIMEOUT_MS = 5000;
	private static final int WORKER_CONTROL_READ_TIMEOUT_MS = 1500;

	interface TimeoutHandler {
		void terminate(WorkerProcess worker);
	}

	private WorkerProtocolClient() {
	}

	private static Json callInternal(
		WorkerProcess worker,
		String operation,
		Json request,
		boolean controlPath,
		TimeoutHandler timeoutHandler) throws IOException {
		if (worker == null || worker.process == null || !worker.process.isAlive()) {
			throw WorkerDiagnostics.workerRemoteFailure(
				worker, AutomationErrorCodes.WORKER_FAILURE, "Worker is not running", null);
		}

		ReentrantLock lock = worker.protocolLock;
		if (!controlPath) {
			lock.lock();
		}

		try {
			Socket socket = new Socket();
			try {
				socket.connect(new InetSocketAddress("127.0.0.1", worker.port), WORKER_CONNECT_TIMEOUT_MS);
				socket.setSoTimeout(controlPath ? WORKER_CONTROL_READ_TIMEOUT_MS : WORKER_READ_TIMEOUT_MS);
				Json envelope = Json.object()
					.set("id", worker.nextRequestId++)
					.set("op", operation)
					.set("args", request == null ? Json.object() : request);
				OutputStream out = socket.getOutputStream();
				out.write((envelope.toString() + '\n').getBytes(StandardCharsets.UTF_8));
				out.flush();
				BufferedReader reader = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
				String response = reader.readLine();
				if (TextValues.isBlank(response)) {
					throw WorkerDiagnostics.workerRemoteFailure(
						worker,
						AutomationErrorCodes.WORKER_FAILURE,
						"Worker returned empty protocol response",
						null);
				}

				Json parsed;
				try {
					parsed = Json.read(response);
				} catch (RuntimeException e) {
					throw WorkerDiagnostics.workerRemoteFailure(
						worker,
						AutomationErrorCodes.WORKER_FAILURE,
						"Worker returned invalid JSON response",
						Json.object().set("response", WorkerDiagnostics.truncateResponse(response)));
				}

				if (!parsed.isObject()) {
					throw WorkerDiagnostics.workerRemoteFailure(
						worker,
						AutomationErrorCodes.WORKER_FAILURE,
						"Worker returned non-object JSON",
						Json.object().set("response", WorkerDiagnostics.truncateResponse(response)));
				}

				if (!parsed.at("ok", false).asBoolean()) {
					throw WorkerDiagnostics.workerRemoteFailure(worker, parsed.at("error"), "Worker error");
				}

				return parsed.at("result", Json.object());
			} catch (java.net.SocketTimeoutException e) {
				timeoutHandler.terminate(worker);
				throw WorkerDiagnostics.workerRemoteFailure(
					worker,
					AutomationErrorCodes.WORKER_FAILURE,
					"Worker timed out while handling " + operation,
					Json.object().set("operation", operation));
			} finally {
				try {
					socket.close();
				} catch (IOException ignored) {
				}
			}
		} finally {
			if (!controlPath) {
				lock.unlock();
			}
		}
	}

	static Json call(
		WorkerProcess worker,
		String operation,
		Json request,
		TimeoutHandler timeoutHandler) throws IOException {
		return callInternal(worker, operation, request, false, timeoutHandler);
	}

	static Json callControl(
		WorkerProcess worker,
		String operation,
		Json request,
		TimeoutHandler timeoutHandler) throws IOException {
		return callInternal(worker, operation, request, true, timeoutHandler);
	}
}
