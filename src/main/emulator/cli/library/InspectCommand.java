package emulator.cli.library;

import emulator.cli.core.*;
import emulator.cli.output.CliTextRenderer;

public final class InspectCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("inspect");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		if (invocation.tokens().size() != 2) {
			throw new KemuCliException(
				"USAGE_ERROR",
				CliTextRenderer.usageText("inspect"),
				CliExitCodes.USAGE,
				"inspect",
				invocation.json());
		}

		java.nio.file.Path input = emulator.cli.parse.CliParsing.resolveUserPath(
			invocation.tokens().get(1));
		InspectionResult result = CliAppInspector.inspect(input, "inspect", invocation.json());

		return new CommandResult(
			"inspect", CliTextRenderer.renderInspection(result), result.toJson(), invocation.json());
	}
}
