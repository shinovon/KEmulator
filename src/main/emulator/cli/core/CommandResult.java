package emulator.cli.core;

import mjson.Json;

public final class CommandResult {
	public final String commandName;
	public final String text;
	public final Json payload;
	public final boolean json;

	public CommandResult(String commandName, String text, Json payload, boolean json) {
		this.commandName = commandName;
		this.text = text;
		this.payload = payload;
		this.json = json;
	}
}
