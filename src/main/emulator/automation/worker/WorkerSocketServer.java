package emulator.automation.worker;

import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationErrorPayloads;
import emulator.automation.shared.AutomationException;
import emulator.automation.shared.TextValues;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import mjson.Json;

final class WorkerSocketServer {
	private static final int CLIENT_READ_TIMEOUT_MS = 30000;
	private static final WorkerRuntimeLifecycle.ServerLoop SERVER_LOOP =
		new WorkerRuntimeLifecycle.ServerLoop() {
			public void run(ServerSocket serverSocket) {
				runServerLoop(serverSocket);
			}
		};
	private static final WorkerOperationDispatcher.ShutdownRequester SHUTDOWN_REQUESTER =
		new WorkerOperationDispatcher.ShutdownRequester() {
			public void requestRuntimeShutdown(String reason) {
				WorkerRuntimeLifecycle.requestShutdown(reason);
			}
		};

	private WorkerSocketServer() {
	}

	static WorkerRuntimeLifecycle.ServerLoop serverLoop() {
		return SERVER_LOOP;
	}

	private static void writeResponse(OutputStream out, Json response) throws IOException {
		byte[] bytes = (response.toString() + '\n').getBytes(StandardCharsets.UTF_8);
		out.write(bytes);
		out.flush();
	}

	private static Json errorResponse(Json id, Exception error) {
		return Json.object()
			.set("id", id)
			.set("ok", false)
			.set(
				"error",
				AutomationErrorPayloads.toErrorJson(
					error, AutomationErrorCodes.WORKER_FAILURE, "Worker error"));
	}

	private static Json handleProtocolLine(String line) {
		Json request;
		Json id = Json.nil();
		try {
			request = Json.read(line);
			if (!request.isObject()) {
				return errorResponse(
					id,
					new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "Expected JSON object request"));
			}

			if (request.has("id")) {
				id = request.at("id");
			}

			String op = request.at("op", "").asString();
			Json args = request.at("args", Json.object());
			if (args == null || !args.isObject()) {
				args = Json.object();
			}

			Json result = WorkerOperationDispatcher.dispatch(op, args, SHUTDOWN_REQUESTER);

			return Json.object().set("id", id).set("ok", true).set("result", result);
		} catch (Exception e) {
			return errorResponse(id, e);
		}
	}

	private static void handleClient(Socket socket) throws IOException {
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
		OutputStream out = socket.getOutputStream();
		String line = reader.readLine();
		if (TextValues.isBlank(line)) {
			writeResponse(
				out,
				errorResponse(
					Json.nil(),
					new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "Empty request")));

			return;
		}

		writeResponse(out, handleProtocolLine(line));
	}

	private static void handleClientConnection(Socket socket, ServerSocket serverSocket) {
		try {
			handleClient(socket);
		} catch (SocketTimeoutException ignored) {
		} catch (IOException e) {
			if (serverSocket.isClosed()) {
				return;
			}

			System.err.println("Automation worker client error: " + e);
		} finally {
			try {
				socket.close();
			} catch (IOException ignored) {
			}
		}
	}

	private static void runServerLoop(final ServerSocket serverSocket) {
		while (true) {
			try {
				final Socket socket = serverSocket.accept();
				socket.setSoTimeout(CLIENT_READ_TIMEOUT_MS);
				Thread handler = new Thread(
					new Runnable() {
						public void run() {
							handleClientConnection(socket, serverSocket);
						}
					},
					"KEmulator-Automation-Worker-Client");
				handler.setDaemon(true);
				handler.start();
			} catch (IOException e) {
				if (serverSocket.isClosed()) {
					return;
				}

				System.err.println("Automation worker socket error: " + e);
			}
		}
	}
}
