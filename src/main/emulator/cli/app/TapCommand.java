package emulator.cli.app;

import emulator.cli.controller.*;
import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import emulator.cli.output.CliTextRenderer;
import emulator.cli.parse.CliParsing;
import mjson.Json;

public final class TapCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("tap");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		if (invocation.tokens().size() != 3) {
			throw new KemuCliException(
				"USAGE_ERROR", CliTextRenderer.usageText("tap"), CliExitCodes.USAGE, "tap", invocation.json());
		}

		int x = CliParsing.parseIntegerArgument(invocation.tokens().get(1), "<x>", "tap", invocation.json());
		int y = CliParsing.parseIntegerArgument(invocation.tokens().get(2), "<y>", "tap", invocation.json());
		ControllerStatus status = ControllerLifecycle.requireRunningController("tap", invocation.json());
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			"app.tap",
			Json.object().set("x", x).set("y", y),
			"tap",
			invocation.json()));

		return new CommandResult("tap", "Tapped " + x + "," + y + ".", payload, invocation.json());
	}
}
