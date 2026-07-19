package emulator.cli.app;

import emulator.cli.controller.*;
import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import emulator.cli.output.CliTextRenderer;
import emulator.cli.parse.CliParsing;
import mjson.Json;

public final class PermissionCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("permission");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		if (invocation.tokens().size() != 3) {
			throw new KemuCliException(
				"USAGE_ERROR",
				CliTextRenderer.usageText("permission"),
				CliExitCodes.USAGE,
				"permission",
				invocation.json());
		}

		boolean allow;
		if ("allow".equals(invocation.tokens().get(1))) {
			allow = true;
		} else if ("deny".equals(invocation.tokens().get(1))) {
			allow = false;
		} else {
			throw new KemuCliException(
				"USAGE_ERROR",
				CliTextRenderer.usageText("permission"),
				CliExitCodes.USAGE,
				"permission",
				invocation.json());
		}

		int id = CliParsing.parseIntegerArgument(invocation.tokens().get(2), "<id>", "permission", invocation.json());
		ControllerStatus status = ControllerLifecycle.requireRunningController("permission", invocation.json());
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			"app.permission",
			Json.object().set("id", id).set("allow", allow),
			"permission",
			invocation.json()));

		return new CommandResult("permission", CliTextRenderer.renderPermission(payload), payload, invocation.json());
	}
}
