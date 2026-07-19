package emulator.automation.controller;

import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationErrorPayloads;
import emulator.automation.shared.AutomationException;
import emulator.automation.shared.TextValues;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import mjson.Json;

final class JsonSocketServer {
	interface RequestHandler {
		Json handle(String op, Json args) throws Exception;
	}

	private final String bindHost;
	private final int port;
	private final int clientReadTimeoutMs;
	private final RequestHandler handler;
	private ServerSocket serverSocket;
	private Thread acceptThread;

	JsonSocketServer(String bindHost, int port, int clientReadTimeoutMs, RequestHandler handler) {
		this.bindHost = bindHost;
		this.port = port;
		this.clientReadTimeoutMs = clientReadTimeoutMs;
		this.handler = handler;
	}

	void close() throws IOException {
		if (serverSocket != null) {
			serverSocket.close();
		}
	}

	private void processConnection(Socket socket) throws IOException {
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
		OutputStream out = socket.getOutputStream();
		String line = reader.readLine();
		Json id = Json.nil();
		Json response;
		try {
			if (TextValues.isBlank(line)) {
				throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "Empty request");
			}

			Json request;
			try {
				request = Json.read(line);
			} catch (RuntimeException e) {
				throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "Invalid JSON request", null, e);
			}

			if (!request.isObject()) {
				throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "Expected JSON object request");
			}

			if (request.has("id")) {
				id = request.at("id");
			}

			String op = request.at("op", "").asString();
			Json args = request.at("args", Json.object());
			if (args == null || !args.isObject()) {
				args = Json.object();
			}

			Json result = handler.handle(op, args);
			response = Json.object().set("id", id).set("ok", true).set("result", result);
		} catch (Exception error) {
			response = Json.object()
				.set("id", id)
				.set("ok", false)
				.set(
					"error",
					AutomationErrorPayloads.toErrorJson(
						error, AutomationErrorCodes.CONTROLLER_ERROR, "Controller error"));
		}

		out.write((response.toString() + '\n').getBytes(StandardCharsets.UTF_8));
		out.flush();
	}

	private void handleConnection(Socket socket) {
		try {
			processConnection(socket);
		} catch (SocketTimeoutException ignored) {
		} catch (IOException e) {
			if (serverSocket == null || serverSocket.isClosed()) {
				return;
			}

			System.err.println("Automation controller client error: " + e);
		} finally {
			try {
				socket.close();
			} catch (IOException ignored) {
			}
		}
	}

	private void runAcceptLoop() {
		while (true) {
			try {
				final Socket socket = serverSocket.accept();
				socket.setSoTimeout(clientReadTimeoutMs);
				Thread connectionThread = new Thread(
					new Runnable() {
						public void run() {
							handleConnection(socket);
						}
					},
					"KEmulator-Automation-Controller-Client");
				connectionThread.setDaemon(true);
				connectionThread.start();
			} catch (IOException e) {
				if (serverSocket == null || serverSocket.isClosed()) {
					return;
				}

				System.err.println("Automation controller socket error: " + e);
			}
		}
	}

	void start() throws IOException {
		serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress(bindHost, port));
		acceptThread = new Thread(
			new Runnable() {
				public void run() {
					runAcceptLoop();
				}
			},
			"KEmulator-Automation-Controller-Socket");
		acceptThread.setDaemon(true);
		acceptThread.start();
	}
}
