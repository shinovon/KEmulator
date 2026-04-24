package emulator.cli.app;

import emulator.cli.controller.*;
import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import emulator.cli.output.CliTextRenderer;
import mjson.Json;

public final class StateCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("state");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		ControllerLifecycle.requireTokenCount(invocation.tokens(), 1, "state", invocation.json());
		ControllerStatus status = ControllerLifecycle.requireRunningController("state", invocation.json());
		ControllerClient client = ControllerStatusService.controllerClient(status);
		Json current = ControllerCalls.currentAppResult(client, "state", invocation.json());
		ControllerCalls.throwIfWorkerFailure(current, "state", invocation.json());
		Json session = current.at("active", false).asBoolean()
			? CliResponses.normalizePublicJson(ControllerCalls.callController(
				client, "app.session", Json.object(), "state", invocation.json()))
			: Json.object();
		Json payload = CliResponses.buildStatePayload(current, session);

		return new CommandResult("state", CliTextRenderer.renderState(payload), payload, invocation.json());
	}
}
