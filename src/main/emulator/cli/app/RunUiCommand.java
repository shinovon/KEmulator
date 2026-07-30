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

		Integer id = null;
		String label = null;
		Integer snapshot = null;
		Long expectRevision = null;
		boolean waitNextDisplay = false;
		int timeoutMs = 5000;
		int start = 2;
		String first = invocation.tokens().get(2);
		if (!first.startsWith("--")) {
			id = Integer.valueOf(CliParsing.parseIntegerArgument(first, "<id>", "command run", invocation.json()));
			start = 3;
		}
		for (int i = start; i < invocation.tokens().size(); i++) {
			String token = invocation.tokens().get(i);
			if ("--snapshot".equals(token)) {
				if (snapshot != null || i + 1 >= invocation.tokens().size()) {
					throw new KemuCliException(
						"USAGE_ERROR",
						CliTextRenderer.usageText("command run"),
						CliExitCodes.USAGE,
						"command run",
						invocation.json());
				}
				snapshot = Integer.valueOf(CliParsing.parseIntegerArgument(
					invocation.tokens().get(++i), "--snapshot", "command run", invocation.json()));
			} else if ("--id".equals(token)) {
				if (id != null || label != null || i + 1 >= invocation.tokens().size()) {
					throw new KemuCliException(
						"USAGE_ERROR",
						CliTextRenderer.usageText("command run"),
						CliExitCodes.USAGE,
						"command run",
						invocation.json());
				}
				id = Integer.valueOf(CliParsing.parseIntegerArgument(
					invocation.tokens().get(++i), "--id", "command run", invocation.json()));
			} else if ("--label".equals(token)) {
				if (label != null || id != null || i + 1 >= invocation.tokens().size()) {
					throw new KemuCliException(
						"USAGE_ERROR",
						CliTextRenderer.usageText("command run"),
						CliExitCodes.USAGE,
						"command run",
						invocation.json());
				}
				label = invocation.tokens().get(++i);
			} else if ("--expect-revision".equals(token)) {
				if (expectRevision != null || i + 1 >= invocation.tokens().size()) {
					throw new KemuCliException(
						"USAGE_ERROR",
						CliTextRenderer.usageText("command run"),
						CliExitCodes.USAGE,
						"command run",
						invocation.json());
				}
				try {
					expectRevision = Long.valueOf(Long.parseLong(invocation.tokens().get(++i)));
				} catch (NumberFormatException e) {
					throw new KemuCliException(
						"USAGE_ERROR",
						CliTextRenderer.usageText("command run"),
						CliExitCodes.USAGE,
						"command run",
						invocation.json());
				}
			} else if ("--wait-next-display".equals(token)) {
				if (waitNextDisplay) {
					throw new KemuCliException(
						"USAGE_ERROR",
						"Duplicate option: --wait-next-display.",
						CliExitCodes.USAGE,
						"command run",
						invocation.json());
				}
				waitNextDisplay = true;
			} else if ("--timeout".equals(token)) {
				if (i + 1 >= invocation.tokens().size()) {
					throw new KemuCliException(
						"USAGE_ERROR",
						CliTextRenderer.usageText("command run"),
						CliExitCodes.USAGE,
						"command run",
						invocation.json());
				}
				timeoutMs = CliParsing.parseIntegerArgument(
					invocation.tokens().get(++i), "--timeout", "command run", invocation.json());
				timeoutMs = CliParsing.requireInclusiveRange(
					timeoutMs,
					0,
					emulator.automation.shared.AutomationLimits.MAX_WAIT_MS,
					"--timeout",
					"command run",
					invocation.json());
			} else {
				throw new KemuCliException(
					"USAGE_ERROR",
					CliTextRenderer.usageText("command run"),
					CliExitCodes.USAGE,
					"command run",
					invocation.json());
			}
		}

		if (id == null && label == null) {
			throw new KemuCliException(
				"USAGE_ERROR",
				CliTextRenderer.usageText("command run"),
				CliExitCodes.USAGE,
				"command run",
				invocation.json());
		}

		ControllerStatus status = ControllerLifecycle.requireRunningController("command run", invocation.json());
		Json request = Json.object()
			.set("waitNextDisplay", waitNextDisplay)
			.set("timeoutMs", timeoutMs);
		if (id != null) {
			request.set("id", id.intValue());
		}
		if (label != null) {
			request.set("label", label);
		}
		if (snapshot != null) {
			request.set("snapshotId", snapshot.intValue());
		}
		if (expectRevision != null) {
			request.set("expectRevision", expectRevision.longValue());
		}
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			"app.command.run",
			request,
			"command run",
			invocation.json()));

		return new CommandResult("command run", CliTextRenderer.renderCommandRun(payload), payload, invocation.json());
	}
}
