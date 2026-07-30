package emulator.cli.app;

import emulator.cli.controller.ControllerCalls;
import emulator.cli.controller.ControllerLifecycle;
import emulator.cli.controller.ControllerStatus;
import emulator.cli.controller.ControllerStatusService;
import emulator.cli.core.CliCommand;
import emulator.cli.core.CliExitCodes;
import emulator.cli.core.CliInvocation;
import emulator.cli.core.CommandPath;
import emulator.cli.core.CommandResult;
import emulator.cli.core.KemuCliException;
import emulator.cli.output.CliResponses;
import emulator.cli.parse.CliParsing;
import mjson.Json;

public final class PointerTapCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("pointer", "tap");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		boolean json = invocation.json();
		if (invocation.tokens().size() < 4 || invocation.tokens().size() > 5) {
			throw new KemuCliException(
				"USAGE_ERROR",
				"Usage: kemu pointer tap <x> <y> [--wait-dispatched]",
				CliExitCodes.USAGE,
				"pointer tap",
				json);
		}
		int x = CliParsing.parseIntegerArgument(invocation.tokens().get(2), "<x>", "pointer tap", json);
		int y = CliParsing.parseIntegerArgument(invocation.tokens().get(3), "<y>", "pointer tap", json);
		boolean wait = invocation.tokens().size() == 5
			&& "--wait-dispatched".equals(invocation.tokens().get(4));
		if (invocation.tokens().size() == 5 && !wait) {
			throw new KemuCliException(
				"USAGE_ERROR",
				"Usage: kemu pointer tap <x> <y> [--wait-dispatched]",
				CliExitCodes.USAGE,
				"pointer tap",
				json);
		}
		ControllerStatus status = ControllerLifecycle.requireRunningController("pointer tap", json);
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			"app.tap",
			Json.object().set("x", x).set("y", y).set("waitDispatched", wait),
			"pointer tap",
			json));
		return new CommandResult("pointer tap", "Tapped " + x + "," + y + ".", payload, json);
	}
}
