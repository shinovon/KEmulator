package emulator.cli.controller;

import emulator.automation.shared.AutomationErrorCodes;
import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import java.io.IOException;
import mjson.Json;

public final class ControllerCalls {
	private ControllerCalls() {
	}

	public static Json callController(
		ControllerClient client, String name, Json arguments, String commandName, boolean json) throws Exception {
		try {
			return client.callTool(name, arguments);
		} catch (ControllerException e) {
			throw new KemuCliException(
				e.code,
				e.getMessage(),
				CliErrorMapping.exitCodeFor(e.code),
				commandName,
				json,
				CliResponses.normalizePublicJson(e.payload));
		} catch (IOException e) {
			throw new KemuCliException(
				AutomationErrorCodes.CONTROLLER_UNREACHABLE,
				e.getMessage(),
				CliErrorMapping.exitCodeFor(AutomationErrorCodes.CONTROLLER_UNREACHABLE),
				commandName,
				json);
		}
	}

	public static Json currentAppResult(ControllerClient client, String commandName, boolean json) throws Exception {
		return CliResponses.normalizePublicJson(
			callController(client, "app.current", Json.object(), commandName, json));
	}

	public static void throwIfWorkerFailure(Json current, String commandName, boolean json) {
		if (current == null || current.isNull() || !current.isObject() || !current.has("failure")) {
			return;
		}

		Json failure = current.at("failure");
		if (failure == null || failure.isNull() || !failure.isObject()) {
			return;
		}

		String code = failure.has("code") ? failure.at("code").asString() : AutomationErrorCodes.WORKER_FAILURE;
		String message = failure.has("message") ? failure.at("message").asString() : "Worker exited unexpectedly";
		Json details = failure.has("details") ? CliResponses.normalizePublicJson(failure.at("details")) : null;
		throw new KemuCliException(code, message, CliErrorMapping.exitCodeFor(code), commandName, json, details);
	}

	private static boolean canRecoverFromBrokenActiveApp(KemuCliException e) {
		if (e == null) {
			return false;
		}

		if (AutomationErrorCodes.WORKER_FAILURE.equals(e.code)) {
			return true;
		}

		if (!AutomationErrorCodes.CONTROLLER_ERROR.equals(e.code) || e.payload == null || !e.payload.isObject()) {
			return false;
		}

		Json operation = e.payload.at("operation");

		return operation != null && !operation.isNull() && "app.session".equals(operation.asString());
	}

	public static void rejectIfAppAlreadyActive(ControllerClient client, String commandName, boolean json)
		throws Exception {
		Json current = currentAppResult(client, commandName, json);
		if (current.at("active", false).asBoolean()) {
			Json session;
			try {
				session = CliResponses.normalizePublicJson(
					callController(client, "app.session", Json.object(), commandName, json));
			} catch (KemuCliException e) {
				if (canRecoverFromBrokenActiveApp(e)) {
					Json refreshed = currentAppResult(client, commandName, json);
					if (!refreshed.at("active", false).asBoolean()) {
						return;
					}
				}

				throw e;
			}

			throw new KemuCliException(
				"APP_ALREADY_OPEN",
				"Another app is already active.",
				CliExitCodes.RUNTIME,
				commandName,
				json,
				CliResponses.buildStatePayload(current, session));
		}
	}
}
