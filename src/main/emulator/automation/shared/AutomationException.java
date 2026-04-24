package emulator.automation.shared;

import mjson.Json;

public final class AutomationException extends RuntimeException {
	public final String code;
	public final Json details;

	public AutomationException(String code, String message, Json details, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.details = details;
	}

	public AutomationException(String code, String message, Json details) {
		this(code, message, details, null);
	}

	public AutomationException(String code, String message) {
		this(code, message, null, null);
	}
}
