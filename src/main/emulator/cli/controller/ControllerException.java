package emulator.cli.controller;

import java.io.IOException;
import mjson.Json;

public final class ControllerException extends IOException {
	public final String code;
	public final Json payload;

	public ControllerException(String code, String message, Json payload, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.payload = payload;
	}

	public ControllerException(String code, String message, Json payload) {
		this(code, message, payload, null);
	}
}
