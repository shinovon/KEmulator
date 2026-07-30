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

			Json delivery = WorkerInputActions.pressKey(
				code,
				durationMs,
				request.at("waitDispatched", false).asBoolean(),
				request.at("waitRelease", false).asBoolean());
			WorkerCommandSnapshots.invalidate();

			return Json.object()
				.set("ok", true)
				.set("key", key)
				.set("code", code)
				.set("delivery", delivery);
		}

		if ("tap".equals(op)) {
			int x = request.at("x", -1).asInteger();
			int y = request.at("y", -1).asInteger();
			if (x < 0 || y < 0) {
				throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "tap requires x and y");
			}

			Json delivery = WorkerInputActions.tap(
				x,
				y,
				request.at("waitDispatched", false).asBoolean());
			WorkerCommandSnapshots.invalidate();

			return Json.object().set("ok", true).set("x", x).set("y", y).set("delivery", delivery);
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

			Json delivery = WorkerInputActions.drag(
				points,
				delayMs,
				request.at("waitDispatched", false).asBoolean());
			WorkerCommandSnapshots.invalidate();

			return Json.object()
				.set("ok", true)
				.set("points", points.asJsonList().size())
				.set("delivery", delivery);
		}

		if ("select-command".equals(op)) {
			return WorkerCommandSnapshots.select(request);
		}

		if ("wait".equals(op)) {
			return WorkerWaits.waitFor(request);
		}

		if ("events-read".equals(op)) {
			return WorkerWaits.readEvents(request);
		}

		if ("list-select".equals(op)) {
			return WorkerLcduiActions.listSelect(request);
		}

		if ("list-move".equals(op)) {
			return WorkerLcduiActions.listMove(request);
		}

		if ("choice-set".equals(op)) {
			return WorkerLcduiActions.choiceSet(request);
		}

		if ("gauge-set".equals(op)) {
			return WorkerLcduiActions.gaugeSet(request);
		}

		if ("text-field-set".equals(op)) {
			return WorkerLcduiActions.textFieldSet(request);
		}

		if ("answer-permission".equals(op)) {
			int id = request.at("id", -1).asInteger();
			boolean allow = request.at("allow", false).asBoolean();
			String mode = request.at("mode", "once").asString();

			Json result = WorkerPermissions.resolve(id, allow, mode);
			WorkerCommandSnapshots.invalidate();
			WorkerEventModel.stateChanged(
				"permission-resolved",
				Json.object().set("id", result.at("id")).set("allow", allow).set("mode", mode));

			return result.set("ok", true);
		}

		if ("shutdown".equals(op)) {
			shutdownRequester.requestRuntimeShutdown("shutdown");

			return Json.object().set("ok", true);
		}

		throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "Unknown worker operation: " + op);
	}
}
