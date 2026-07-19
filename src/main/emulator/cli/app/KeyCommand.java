package emulator.cli.app;

import emulator.automation.shared.AutomationLimits;
import emulator.cli.controller.*;
import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import emulator.cli.output.CliTextRenderer;
import emulator.cli.parse.CliParsing;
import mjson.Json;

public final class KeyCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("key");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		if (invocation.tokens().size() < 2) {
			throw new KemuCliException(
				"USAGE_ERROR", CliTextRenderer.usageText("key"), CliExitCodes.USAGE, "key", invocation.json());
		}

		String key = invocation.tokens().get(1);
		Integer duration = null;
		boolean sawDuration = false;
		for (int i = 2; i < invocation.tokens().size(); i++) {
			String token = invocation.tokens().get(i);
			if ("--duration".equals(token)) {
				if (sawDuration) {
					throw new KemuCliException(
						"USAGE_ERROR",
						"Duplicate option: --duration.",
						CliExitCodes.USAGE,
						"key",
						invocation.json());
				}

				if (i + 1 >= invocation.tokens().size()) {
					throw new KemuCliException(
						"USAGE_ERROR",
						CliTextRenderer.usageText("key"),
						CliExitCodes.USAGE,
						"key",
						invocation.json());
				}

				sawDuration = true;
				duration = Integer.valueOf(CliParsing.parseIntegerArgument(
					invocation.tokens().get(++i), "--duration", "key", invocation.json()));
				duration = Integer.valueOf(CliParsing.requireInclusiveRange(
					duration.intValue(),
					10,
					AutomationLimits.MAX_KEY_DURATION_MS,
					"--duration",
					"key",
					invocation.json()));
			} else {
				throw new KemuCliException(
					"USAGE_ERROR", CliTextRenderer.usageText("key"), CliExitCodes.USAGE, "key", invocation.json());
			}
		}

		ControllerStatus status = ControllerLifecycle.requireRunningController("key", invocation.json());
		Json args = Json.object().set("key", key);
		if (duration != null) {
			args.set("durationMs", duration.intValue());
		}

		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status), "app.key", args, "key", invocation.json()));

		return new CommandResult("key", "Pressed " + key + ".", payload, invocation.json());
	}
}
