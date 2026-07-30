package emulator.cli.controller;

import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import mjson.Json;

public final class StatusCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("status");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		ControllerLifecycle.requireTokenCount(invocation.tokens(), 1, "status", invocation.json());
		ControllerStatus status = ControllerStatusService.readControllerStatus();
		Json payload = status.toJson();
		payload.set("active", false);
		if (status.running) {
			Json current = CliResponses.normalizePublicJson(ControllerCalls.currentAppResult(
				ControllerStatusService.controllerClient(status),
				"status",
				invocation.json()));
			payload.set("active", current.at("active", false).asBoolean());
			if (current.has("app")) {
				payload.set("app", current.at("app"));
			}
			if (current.has("worker")) {
				payload.set("worker", current.at("worker"));
			}
			if (current.has("failure")) {
				payload.set("workerFailure", current.at("failure"));
			}
		}

		return new CommandResult(
			"status",
			emulator.cli.output.CliTextRenderer.renderStatus(status, payload),
			payload,
			invocation.json());
	}
}
