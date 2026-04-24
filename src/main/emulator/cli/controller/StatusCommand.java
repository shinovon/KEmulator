package emulator.cli.controller;

import emulator.cli.core.*;

public final class StatusCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("status");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		ControllerLifecycle.requireTokenCount(invocation.tokens(), 1, "status", invocation.json());
		ControllerStatus status = ControllerStatusService.readControllerStatus();

		return new CommandResult(
			"status", emulator.cli.output.CliTextRenderer.renderStatus(status), status.toJson(), invocation.json());
	}
}
