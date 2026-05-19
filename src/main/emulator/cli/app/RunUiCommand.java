package emulator.cli.app;

import emulator.cli.controller.*;
import emulator.cli.core.CliCommand;
import emulator.cli.core.CliExitCodes;
import emulator.cli.core.CliInvocation;
import emulator.cli.core.CommandPath;
import emulator.cli.core.CommandResult;
import emulator.cli.core.KemuCliException;
import emulator.cli.output.CliResponses;
import emulator.cli.output.CliTextRenderer;
import emulator.cli.parse.CliParsing;
import mjson.Json;

public final class RunUiCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("command", "run");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		if (invocation.tokens().size() < 3) {
			throw new KemuCliException(
				"USAGE_ERROR",
				CliTextRenderer.usageText("command run"),
				CliExitCodes.USAGE,
				"command run",
				invocation.json());
		}

		String idToken = invocation.tokens().get(2);
		if ("--help".equals(idToken) || "-h".equals(idToken)) {
			throw new KemuCliException(
				"USAGE_ERROR",
				CliTextRenderer.usageText("command run"),
				CliExitCodes.USAGE,
				"command run",
				invocation.json());
		}

		int id = CliParsing.parseIntegerArgument(idToken, "<id>", "command run", invocation.json());
		Integer snapshot = null;
		boolean sawSnapshot = false;
		for (int i = 3; i < invocation.tokens().size(); i++) {
			String token = invocation.tokens().get(i);
			if ("--snapshot".equals(token)) {
				if (sawSnapshot) {
					throw new KemuCliException(
						"USAGE_ERROR",
						"Duplicate option: --snapshot.",
						CliExitCodes.USAGE,
						"command run",
						invocation.json());
				}

				if (i + 1 >= invocation.tokens().size()) {
					throw new KemuCliException(
						"USAGE_ERROR",
						CliTextRenderer.usageText("command run"),
						CliExitCodes.USAGE,
						"command run",
						invocation.json());
				}

				sawSnapshot = true;
				snapshot = Integer.valueOf(CliParsing.parseIntegerArgument(
					invocation.tokens().get(++i), "--snapshot", "command run", invocation.json()));
			} else {
				throw new KemuCliException(
					"USAGE_ERROR",
					CliTextRenderer.usageText("command run"),
					CliExitCodes.USAGE,
					"command run",
					invocation.json());
			}
		}

		if (snapshot == null) {
			throw new KemuCliException(
				"USAGE_ERROR",
				CliTextRenderer.usageText("command run"),
				CliExitCodes.USAGE,
				"command run",
				invocation.json());
		}

		ControllerStatus status = ControllerLifecycle.requireRunningController("command run", invocation.json());
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			"app.command.run",
			Json.object().set("id", id).set("snapshotId", snapshot.intValue()),
			"command run",
			invocation.json()));

		return new CommandResult("command run", CliTextRenderer.renderCommandRun(payload), payload, invocation.json());
	}
}
