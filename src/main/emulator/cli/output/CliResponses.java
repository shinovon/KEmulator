package emulator.cli.output;

import emulator.cli.core.*;
import mjson.Json;

public final class CliResponses {
	private CliResponses() {
	}

	public static Json normalizePublicJson(Json value) {
		if (value == null || value.isNull()) {
			return Json.nil();
		}

		if (value.isObject()) {
			Json result = Json.object();
			for (String key : value.asJsonMap().keySet()) {
				result.set(key, normalizePublicJson(value.at(key)));
			}

			return result;
		}

		if (value.isArray()) {
			Json array = Json.array();
			for (Json item : value.asJsonList()) {
				array.add(normalizePublicJson(item));
			}

			return array;
		}

		return value.dup();
	}

	public static Json publicizeOpenResult(Json input) {
		Json payload = normalizePublicJson(input);
		Json result = Json.object();
		if (payload.has("app")) {
			result.set("app", payload.at("app"));
		}

		if (payload.has("inputPath")) {
			result.set("inputPath", payload.at("inputPath"));
		}

		Json session = payload.at("session");
		if (session != null && session.isObject()) {
			for (String key : session.asJsonMap().keySet()) {
				result.set(key, session.at(key));
			}
		}

		return result;
	}

	public static Json successEnvelope(String commandName, Json payload) {
		return Json.object().set("ok", true).set("command", commandName).set("result", payload);
	}

	public static Json errorEnvelope(String commandName, String code, String message, Json payload) {
		Json error = Json.object().set("code", code).set("message", message);
		if (payload != null && !payload.isNull()) {
			error.set("details", payload);
		}

		return Json.object().set("ok", false).set("command", commandName).set("error", error);
	}

	public static Json buildStatePayload(Json current, Json session) {
		if (!current.at("active", false).asBoolean()) {
			return Json.object().set("active", false);
		}

		return Json.object()
			.set("active", true)
			.set("app", current.at("app"))
			.set("ready", session.at("session").at("ready", false).asBoolean())
			.set(
				"midletStarted",
				session.at("session").at("midletStarted", false).asBoolean())
			.set("title", session.at("session").at("title"))
			.set("displayableKind", session.at("session").at("displayableKind"))
			.set("permissionRequest", session.at("session").at("permissionRequest"));
	}

	public static Json buildObservePayload(Json current, Json session) {
		if (!current.at("active", false).asBoolean()) {
			return Json.object().set("active", false);
		}

		Json payload = normalizePublicJson(session);
		payload.set("active", true);
		if (current.has("app")) {
			payload.set("app", current.at("app"));
		}

		return payload;
	}
}
