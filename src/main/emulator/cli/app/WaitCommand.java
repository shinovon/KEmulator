package emulator.cli.app;

import emulator.automation.shared.AutomationLimits;
import emulator.cli.controller.*;
import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import emulator.cli.output.CliTextRenderer;
import emulator.cli.parse.CliParsing;
import mjson.Json;

public final class WaitCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("wait");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		if (invocation.tokens().size() != 2) {
			throw new KemuCliException(
				"USAGE_ERROR", CliTextRenderer.usageText("wait"), CliExitCodes.USAGE, "wait", invocation.json());
		}

		int durationMs = CliParsing.parseIntegerArgument(invocation.tokens().get(1), "<ms>", "wait", invocation.json());
		durationMs = CliParsing.requireInclusiveRange(
			durationMs, 0, AutomationLimits.MAX_WAIT_MS, "<ms>", "wait", invocation.json());
		ControllerStatus status = ControllerLifecycle.requireRunningController("wait", invocation.json());
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			"app.wait",
			Json.object().set("durationMs", durationMs),
			"wait",
			invocation.json()));

		return new CommandResult(
			"wait", "Waited " + payload.at("sleptMs").asInteger() + " ms.", payload, invocation.json());
	}
}
