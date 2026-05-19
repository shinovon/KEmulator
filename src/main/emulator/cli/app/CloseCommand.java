package emulator.cli.app;

import emulator.cli.controller.*;
import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import mjson.Json;

public final class CloseCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("close");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		ControllerLifecycle.requireTokenCount(invocation.tokens(), 1, "close", invocation.json());
		final boolean json = invocation.json();

		return ControllerLifecycle.withLifecycleLock(new java.util.concurrent.Callable<CommandResult>() {
			public CommandResult call() throws Exception {
				ControllerStatus status = ControllerStatusService.readControllerStatus();
				if (ControllerStatusService.isForeignPidState(status)) {
					ControllerStatusService.deleteStateFiles();
					Json payload = Json.object().set("closed", false).set("reason", "not_running");

					return new CommandResult("close", "No active app.", payload, json);
				}

				boolean actionable = status.running || status.degraded || Boolean.TRUE.equals(status.pidAlive);
				if (!status.exists || !actionable) {
					ControllerStatusService.deleteStateFiles();
					Json payload = Json.object().set("closed", false).set("reason", "not_running");

					return new CommandResult("close", "No active app.", payload, json);
				}

				if (status.degraded || (Boolean.TRUE.equals(status.pidAlive) && !status.running)) {
					throw new KemuCliException(
						"CONTROLLER_UNREACHABLE",
						"Controller is unreachable. Retry with kemu stop --force.",
						CliExitCodes.RUNTIME,
						"close",
						json);
				}

				Json result = ControllerCalls.callController(
					ControllerStatusService.controllerClient(status), "app.close", Json.object(), "close", json);
				Json payload = CliResponses.normalizePublicJson(result);

				return new CommandResult(
					"close",
					payload.at("closed", false).asBoolean() ? "App closed." : "No active app.",
					payload,
					json);
			}
		});
	}
}
