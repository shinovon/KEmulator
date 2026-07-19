package emulator.automation.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import mjson.Json;

public final class ControllerServer {
	private static final int CLIENT_READ_TIMEOUT_MS = 30000;
	private final String bindHost;
	private final int port;
	private final Path logsRoot;
	private final ControllerWorkerSession workerSession;
	private final ControllerOperationRegistry operationRegistry;
	private JsonSocketServer transport;

	private void requestShutdown() {
		new Thread(
			new Runnable() {
				public void run() {
					try {
						workerSession.closeActiveForShutdown();
					} catch (Exception ignored) {
					}

					try {
						if (transport != null) {
							transport.close();
						}
					} catch (Exception ignored) {
					}

					System.exit(0);
				}
			},
			"KEmulator-Controller-Shutdown")
			.start();
	}

	public ControllerServer(String bindHost, int port, int screenWidth, int screenHeight, Path runtimeRoot)
		throws IOException {
		this.bindHost = bindHost;
		this.port = port;
		Path normalizedRuntimeRoot = runtimeRoot.toAbsolutePath().normalize();
		this.logsRoot = normalizedRuntimeRoot.resolve("logs");
		AppTargetResolver entryResolver = new AppTargetResolver();
		WorkerSupervisor workerSupervisor = new WorkerSupervisor(this.logsRoot, screenWidth, screenHeight);
		this.workerSession = new ControllerWorkerSession(entryResolver, workerSupervisor);
		this.operationRegistry = new ControllerOperationRegistry(
			this.workerSession,
			new ControllerOperationRegistry.ShutdownHandler() {
				public void requestShutdown() {
					ControllerServer.this.requestShutdown();
				}
			});
		Files.createDirectories(this.logsRoot);
	}

	public ControllerServer(String bindHost, int port, int screenWidth, int screenHeight) throws IOException {
		this(
			bindHost,
			port,
			screenWidth,
			screenHeight,
			Paths.get("automation").toAbsolutePath().normalize());
	}

	public void start() throws IOException {
		transport = new JsonSocketServer(bindHost, port, CLIENT_READ_TIMEOUT_MS, new JsonSocketServer.RequestHandler() {
			public Json handle(String op, Json args) throws Exception {
				return operationRegistry.dispatch(op, args);
			}
		});
		transport.start();
	}
}
