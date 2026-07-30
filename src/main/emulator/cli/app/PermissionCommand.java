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
		if (invocation.tokens().size() < 2 || invocation.tokens().size() > 4) {
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

		int id = -1;
		String mode = "once";
		boolean modeSet = false;
		for (int i = 2; i < invocation.tokens().size(); i++) {
			String token = invocation.tokens().get(i);
			if (allow && ("--once".equals(token) || "--always".equals(token))) {
				if (modeSet) {
					throw usage(invocation);
				}
				mode = "--always".equals(token) ? "always" : "once";
				modeSet = true;
			} else if (id < 0) {
				id = CliParsing.parseIntegerArgument(token, "<id>", "permission", invocation.json());
			} else {
				throw usage(invocation);
			}
		}
		ControllerStatus status = ControllerLifecycle.requireRunningController("permission", invocation.json());
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			"app.permission",
			Json.object().set("id", id).set("allow", allow).set("mode", mode),
			"permission",
			invocation.json()));

		return new CommandResult("permission", CliTextRenderer.renderPermission(payload), payload, invocation.json());
	}

	private KemuCliException usage(CliInvocation invocation) {
		return new KemuCliException(
			"USAGE_ERROR",
			CliTextRenderer.usageText("permission"),
			CliExitCodes.USAGE,
			"permission",
			invocation.json());
	}
}
