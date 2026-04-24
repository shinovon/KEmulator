package emulator.automation.controller;

import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationException;
import emulator.automation.shared.AutomationLimits;
import java.util.HashMap;
import java.util.Map;
import mjson.Json;

final class ControllerOperationRegistry {
	interface ShutdownHandler {
		void requestShutdown();
	}

	private interface ControllerAction {
		Json run(Json request) throws Exception;
	}

	private final ControllerWorkerSession workerSession;
	private final ShutdownHandler shutdownHandler;
	private final Object requestQueueLock = new Object();
	private final Map<String, ControllerOperation> commands = new HashMap<String, ControllerOperation>();

	ControllerOperationRegistry(ControllerWorkerSession workerSession, ShutdownHandler shutdownHandler) {
		this.workerSession = workerSession;
		this.shutdownHandler = shutdownHandler;
		registerCommands();
	}

	private static Json sleepTool(Json arguments) throws InterruptedException {
		int durationMs = Math.max(0, arguments.at("durationMs", 250).asInteger());
		if (durationMs > AutomationLimits.MAX_WAIT_MS) {
			throw new AutomationException(
				AutomationErrorCodes.INVALID_REQUEST,
				"wait duration must be between 0 and " + AutomationLimits.MAX_WAIT_MS + " ms");
		}

		Thread.sleep(durationMs);

		return Json.object().set("sleptMs", durationMs);
	}

	private void registerCommand(final String op, final DispatchMode dispatchMode, final ControllerAction action) {
		commands.put(op, new ControllerOperation() {
			public String op() {
				return op;
			}

			public DispatchMode dispatchMode() {
				return dispatchMode;
			}

			public Json execute(Json args) throws Exception {
				return action.run(args == null ? Json.object() : args);
			}
		});
	}

	Json dispatch(String op, Json request) throws Exception {
		ControllerOperation command = commands.get(op);
		if (command == null) {
			throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "Unknown controller operation: " + op);
		}

		if (command.dispatchMode() == DispatchMode.PRIORITY) {
			return command.execute(request);
		}

		synchronized (requestQueueLock) {
			return command.execute(request);
		}
	}

	private void registerCommands() {
		registerCommand("health", DispatchMode.PRIORITY, new ControllerAction() {
			public Json run(Json request) {
				return Json.object().set("ok", true);
			}
		});
		registerCommand("shutdown", DispatchMode.PRIORITY, new ControllerAction() {
			public Json run(Json request) {
				shutdownHandler.requestShutdown();

				return Json.object().set("ok", true);
			}
		});
		registerCommand("app.current", DispatchMode.QUEUED, new ControllerAction() {
			public Json run(Json request) {
				return workerSession.currentGame();
			}
		});
		registerCommand("app.open-path", DispatchMode.QUEUED, new ControllerAction() {
			public Json run(Json request) throws Exception {
				String path = request.at("path") == null ? null : request.at("path").asString();
				Integer midlet = request.at("midlet") == null
					? null
					: Integer.valueOf(request.at("midlet").asInteger());

				return workerSession.openPath(path, midlet);
			}
		});
		registerCommand("app.close", DispatchMode.PRIORITY, new ControllerAction() {
			public Json run(Json request) throws Exception {
				return workerSession.closeGame();
			}
		});
		registerCommand("app.session", DispatchMode.QUEUED, new ControllerAction() {
			public Json run(Json request) throws Exception {
				return workerSession.sessionInfo();
			}
		});
		registerCommand("app.observe", DispatchMode.QUEUED, new ControllerAction() {
			public Json run(Json request) throws Exception {
				return workerSession.observe(request == null ? Json.object().set("includeImage", false) : request);
			}
		});
		registerCommand("app.key", DispatchMode.QUEUED, new ControllerAction() {
			public Json run(Json request) throws Exception {
				return workerSession.proxyWorker("press-key", request);
			}
		});
		registerCommand("app.tap", DispatchMode.QUEUED, new ControllerAction() {
			public Json run(Json request) throws Exception {
				return workerSession.proxyWorker("tap", request);
			}
		});
		registerCommand("app.drag", DispatchMode.QUEUED, new ControllerAction() {
			public Json run(Json request) throws Exception {
				return workerSession.proxyWorker("drag", request);
			}
		});
		registerCommand("app.command.run", DispatchMode.QUEUED, new ControllerAction() {
			public Json run(Json request) throws Exception {
				return workerSession.proxyWorker("select-command", request);
			}
		});
		registerCommand("app.permission", DispatchMode.PRIORITY, new ControllerAction() {
			public Json run(Json request) throws Exception {
				return workerSession.proxyWorkerControl("answer-permission", request);
			}
		});
		registerCommand("app.screenshot", DispatchMode.QUEUED, new ControllerAction() {
			public Json run(Json request) throws Exception {
				return workerSession.captureSnapshot(request);
			}
		});
		registerCommand("app.wait", DispatchMode.QUEUED, new ControllerAction() {
			public Json run(Json request) throws Exception {
				return sleepTool(request);
			}
		});
		registerCommand("logs.worker", DispatchMode.QUEUED, new ControllerAction() {
			public Json run(Json request) throws Exception {
				return workerSession.workerLogTail(request);
			}
		});
	}
}
