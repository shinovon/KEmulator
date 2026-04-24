package emulator.cli.app;

import emulator.cli.core.*;
import emulator.cli.output.CliTextRenderer;

public final class CommandRootCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("command");
	}

	public CommandResult run(CliInvocation invocation) {
		if (invocation.tokens().size() < 2) {
			throw new KemuCliException(
				"USAGE_ERROR",
				CliTextRenderer.usageText("command"),
				CliExitCodes.USAGE,
				"command",
				invocation.json());
		}

		throw new KemuCliException(
			"USAGE_ERROR", CliTextRenderer.usageText("command"), CliExitCodes.USAGE, "command", invocation.json());
	}
}
