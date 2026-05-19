package emulator.automation.worker;

import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationException;
import emulator.automation.shared.AutomationLimits;
import mjson.Json;

final class WorkerOperationDispatcher {
	interface ShutdownRequester {
		void requestRuntimeShutdown(String reason);
	}

	private WorkerOperationDispatcher() {
	}

	static Json dispatch(String op, Json request, ShutdownRequester shutdownRequester) {
		if ("health".equals(op) || "session".equals(op)) {
			return WorkerSessionSnapshot.build(false);
		}

		if ("observe".equals(op)) {
			return WorkerSessionSnapshot.build(request.at("includeImage", false).asBoolean());
		}

		if ("press-key".equals(op)) {
			String key = request.at("key") == null ? null : request.at("key").asString();
			int code = WorkerInputActions.resolveKeyCode(key, request.at("code"));
			int durationMs = Math.max(10, request.at("durationMs", 80).asInteger());
			if (durationMs > AutomationLimits.MAX_KEY_DURATION_MS) {
				throw new AutomationException(
					AutomationErrorCodes.INVALID_REQUEST,
					"press-key duration must be between 10 and " + AutomationLimits.MAX_KEY_DURATION_MS + " ms");
			}

			WorkerInputActions.pressKey(code, durationMs);
			WorkerCommandSnapshots.invalidate();

			return Json.object().set("ok", true).set("key", key).set("code", code);
		}

		if ("tap".equals(op)) {
			int x = request.at("x", -1).asInteger();
			int y = request.at("y", -1).asInteger();
			if (x < 0 || y < 0) {
				throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "tap requires x and y");
			}

			WorkerInputActions.tap(x, y);
			WorkerCommandSnapshots.invalidate();

			return Json.object().set("ok", true).set("x", x).set("y", y);
		}

		if ("drag".equals(op)) {
			Json points = request.at("points");
			if (points == null || !points.isArray()) {
				throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "drag requires points");
			}

			int delayMs = Math.max(5, request.at("delayMs", 20).asInteger());
			if (delayMs > AutomationLimits.MAX_DRAG_DELAY_MS) {
				throw new AutomationException(
					AutomationErrorCodes.INVALID_REQUEST,
					"drag delay must be between 5 and " + AutomationLimits.MAX_DRAG_DELAY_MS + " ms");
			}

			WorkerInputActions.drag(points, delayMs);
			WorkerCommandSnapshots.invalidate();

			return Json.object()
				.set("ok", true)
				.set("points", points.asJsonList().size());
		}

		if ("select-command".equals(op)) {
			return WorkerCommandSnapshots.select(request);
		}

		if ("answer-permission".equals(op)) {
			int id = request.at("id", -1).asInteger();
			boolean allow = request.at("allow", false).asBoolean();
			if (id < 0) {
				throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "answer-permission requires id");
			}

			WorkerPermissions.resolve(id, allow);
			WorkerCommandSnapshots.invalidate();

			return Json.object().set("ok", true).set("id", id).set("allow", allow);
		}

		if ("shutdown".equals(op)) {
			shutdownRequester.requestRuntimeShutdown("shutdown");

			return Json.object().set("ok", true);
		}

		throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "Unknown worker operation: " + op);
	}
}
