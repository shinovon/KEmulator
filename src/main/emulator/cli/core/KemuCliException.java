package emulator.cli.core;

import mjson.Json;

public final class KemuCliException extends RuntimeException {
	public final String code;
	public final int exitCode;
	public final String commandName;
	public final boolean json;
	public final Json payload;

	public KemuCliException(String code, String message, int exitCode, String commandName, boolean json, Json payload) {
		super(message);
		this.code = code;
		this.exitCode = exitCode;
		this.commandName = commandName;
		this.json = json;
		this.payload = payload;
	}

	public KemuCliException(String code, String message, int exitCode, String commandName, boolean json) {
		this(code, message, exitCode, commandName, json, null);
	}
}
