package emulator.automation.shared;

import mjson.Json;

public final class AutomationErrorPayloads {
	private AutomationErrorPayloads() {
	}

	private static String normalizeCode(String code, String fallbackCode) {
		if (!TextValues.isBlank(code)) {
			return code;
		}

		if (!TextValues.isBlank(fallbackCode)) {
			return fallbackCode;
		}

		return AutomationErrorCodes.CONTROLLER_ERROR;
	}

	private static String normalizeMessage(String message, String fallbackMessage) {
		if (!TextValues.isBlank(message)) {
			return message;
		}

		if (!TextValues.isBlank(fallbackMessage)) {
			return fallbackMessage;
		}

		return "Unknown error";
	}

	public static Json toErrorJson(Throwable error, String fallbackCode, String fallbackMessage) {
		String code = fallbackCode;
		String message = fallbackMessage;
		Json details = null;

		if (error instanceof AutomationException) {
			AutomationException automationError = (AutomationException) error;
			code = automationError.code;
			message = automationError.getMessage();
			details = automationError.details;
		} else if (error instanceof AutomationRemoteException) {
			AutomationRemoteException remoteError = (AutomationRemoteException) error;
			code = remoteError.code;
			message = remoteError.getMessage();
			details = remoteError.details;
		} else if (error != null
			&& error.getMessage() != null
			&& !TextValues.isBlank(error.getMessage())) {
			message = error.getMessage();
		}

		Json result = Json.object()
			.set("code", normalizeCode(code, fallbackCode))
			.set("message", normalizeMessage(message, fallbackMessage));
		if (details != null && !details.isNull()) {
			result.set("details", details);
		}

		return result;
	}

	public static AutomationRemoteException remoteException(Json error, String fallbackCode, String fallbackMessage) {
		if (error == null || !error.isObject()) {
			return new AutomationRemoteException(
				normalizeCode(fallbackCode, AutomationErrorCodes.CONTROLLER_ERROR),
				normalizeMessage(fallbackMessage, "Remote error"),
				null);
		}

		String code = error.has("code") ? error.at("code").asString() : fallbackCode;
		String message = error.has("message") ? error.at("message").asString() : fallbackMessage;
		Json details = error.has("details") ? error.at("details") : null;

		return new AutomationRemoteException(
			normalizeCode(code, fallbackCode), normalizeMessage(message, fallbackMessage), details);
	}
}
