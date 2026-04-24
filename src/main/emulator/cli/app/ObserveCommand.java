package emulator.cli.app;

import emulator.cli.controller.*;
import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import emulator.cli.output.CliTextRenderer;
import mjson.Json;

public final class ObserveCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("observe");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		ControllerLifecycle.requireTokenCount(invocation.tokens(), 1, "observe", invocation.json());
		ControllerStatus status = ControllerLifecycle.requireRunningController("observe", invocation.json());
		ControllerClient client = ControllerStatusService.controllerClient(status);
		Json current = ControllerCalls.currentAppResult(client, "observe", invocation.json());
		ControllerCalls.throwIfWorkerFailure(current, "observe", invocation.json());
		Json session = current.at("active", false).asBoolean()
			? ControllerCalls.callController(
				client, "app.observe", Json.object().set("includeImage", false), "observe", invocation.json())
			: Json.object();
		Json payload = CliResponses.buildObservePayload(current, session);

		return new CommandResult("observe", CliTextRenderer.renderObserve(payload), payload, invocation.json());
	}
}
