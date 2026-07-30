package emulator.cli.controller;

import emulator.cli.core.CliCommand;
import emulator.cli.core.CliInvocation;
import emulator.cli.core.CommandPath;
import emulator.cli.core.CommandResult;
import emulator.cli.output.CliResponses;
import mjson.Json;

public final class LogsCursorCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("logs", "cursor");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		ControllerLifecycle.requireTokenCount(invocation.tokens(), 2, "logs cursor", invocation.json());
		ControllerStatus status = ControllerLifecycle.requireRunningController("logs cursor", invocation.json());
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			"logs.cursor",
			Json.object(),
			"logs cursor",
			invocation.json()));
		return new CommandResult(
			"logs cursor",
			payload.at("cursor").asString(),
			payload,
			invocation.json());
	}
}
