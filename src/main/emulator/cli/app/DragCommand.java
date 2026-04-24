package emulator.cli.app;

import emulator.automation.shared.AutomationLimits;
import emulator.cli.controller.*;
import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import emulator.cli.output.CliTextRenderer;
import emulator.cli.parse.CliParsing;
import java.util.ArrayList;
import java.util.List;
import mjson.Json;

public final class DragCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("drag");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		if (invocation.tokens().size() < 5) {
			throw new KemuCliException(
				"USAGE_ERROR", CliTextRenderer.usageText("drag"), CliExitCodes.USAGE, "drag", invocation.json());
		}

		Integer delay = null;
		boolean sawDelay = false;
		List<Integer> coords = new ArrayList<Integer>();
		for (int i = 1; i < invocation.tokens().size(); i++) {
			String token = invocation.tokens().get(i);
			if ("--delay".equals(token)) {
				if (sawDelay) {
					throw new KemuCliException(
						"USAGE_ERROR", "Duplicate option: --delay.", CliExitCodes.USAGE, "drag", invocation.json());
				}

				if (i + 1 >= invocation.tokens().size()) {
					throw new KemuCliException(
						"USAGE_ERROR",
						CliTextRenderer.usageText("drag"),
						CliExitCodes.USAGE,
						"drag",
						invocation.json());
				}

				sawDelay = true;
				delay = Integer.valueOf(CliParsing.parseIntegerArgument(
					invocation.tokens().get(++i), "--delay", "drag", invocation.json()));
				delay = Integer.valueOf(CliParsing.requireInclusiveRange(
					delay.intValue(), 5, AutomationLimits.MAX_DRAG_DELAY_MS, "--delay", "drag", invocation.json()));
			} else {
				coords.add(Integer.valueOf(
					CliParsing.parseIntegerArgument(token, "coordinate", "drag", invocation.json())));
			}
		}

		if (coords.size() < 4 || coords.size() % 2 != 0) {
			throw new KemuCliException(
				"USAGE_ERROR", CliTextRenderer.usageText("drag"), CliExitCodes.USAGE, "drag", invocation.json());
		}

		Json points = Json.array();
		for (int i = 0; i < coords.size(); i += 2) {
			points.add(Json.object()
				.set("x", coords.get(i).intValue())
				.set("y", coords.get(i + 1).intValue()));
		}

		ControllerStatus status = ControllerLifecycle.requireRunningController("drag", invocation.json());
		Json args = Json.object().set("points", points);
		if (delay != null) {
			args.set("delayMs", delay.intValue());
		}

		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status), "app.drag", args, "drag", invocation.json()));

		return new CommandResult(
			"drag", "Dragged across " + payload.at("points").asInteger() + " points.", payload, invocation.json());
	}
}
