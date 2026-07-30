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
import mjson.Json;

public final class EventsReadCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("events", "read");
	}

	private KemuCliException usage(boolean json) {
		return new KemuCliException(
			"USAGE_ERROR",
			"Usage: kemu events read [--since CURSOR] [--jsonl] [--json]",
			CliExitCodes.USAGE,
			"events read",
			json);
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		long since = 0L;
		boolean sinceSet = false;
		boolean jsonl = false;
		for (int i = 2; i < invocation.tokens().size(); i++) {
			String token = invocation.tokens().get(i);
			if ("--since".equals(token)) {
				if (sinceSet || i + 1 >= invocation.tokens().size()) {
					throw usage(invocation.json());
				}
				sinceSet = true;
				try {
					since = Long.parseLong(invocation.tokens().get(++i));
				} catch (NumberFormatException error) {
					throw usage(invocation.json());
				}
				if (since < 0L) {
					throw usage(invocation.json());
				}
			} else if ("--jsonl".equals(token) && !jsonl) {
				jsonl = true;
			} else {
				throw usage(invocation.json());
			}
		}
		ControllerStatus status = ControllerLifecycle.requireRunningController(
			"events read",
			invocation.json());
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			"app.events.read",
			Json.object().set("since", since),
			"events read",
			invocation.json()));
		StringBuilder text = new StringBuilder();
		if (jsonl) {
			for (Json event : payload.at("events", Json.array()).asJsonList()) {
				if (text.length() > 0) {
					text.append('\n');
				}
				text.append(event.toString());
			}
		} else {
			text.append("Events: ")
				.append(payload.at("events", Json.array()).asJsonList().size())
				.append(", cursor: ")
				.append(payload.at("cursor", since).asLong());
		}
		return new CommandResult("events read", text.toString(), payload, invocation.json());
	}
}
