package emulator.cli.controller;

import emulator.cli.core.*;

public final class StartCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("start");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		final boolean json = invocation.json();
		final StartOptions options = ControllerLifecycle.parseStartOptions(invocation.tokens(), 1, "start", json, false,
			false);

		return ControllerLifecycle.withLifecycleLock(new java.util.concurrent.Callable<CommandResult>() {
			public CommandResult call() throws Exception {
				ControllerStatus status = ControllerLifecycle.ensureController(options, true, "start", json);

				return new CommandResult(
					"start", emulator.cli.output.CliTextRenderer.renderStatus(status), status.toJson(), json);
			}
		});
	}
}
