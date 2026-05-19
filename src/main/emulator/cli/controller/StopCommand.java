package emulator.cli.controller;

import emulator.cli.core.*;
import mjson.Json;

public final class StopCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("stop");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		boolean force = false;
		final boolean json = invocation.json();
		for (int i = 1; i < invocation.tokens().size(); i++) {
			String token = invocation.tokens().get(i);
			if ("--force".equals(token)) {
				if (force) {
					throw ControllerOptionParsers.duplicateOption("--force", "stop", json);
				}

				force = true;
			} else {
				throw ControllerOptionParsers.usageError("stop", json);
			}
		}

		final boolean finalForce = force;

		return ControllerLifecycle.withLifecycleLock(new java.util.concurrent.Callable<CommandResult>() {
			public CommandResult call() throws Exception {
				ControllerStatus status = ControllerStatusService.readControllerStatus();
				if (ControllerStatusService.isForeignPidState(status)) {
					ControllerStatusService.deleteStateFiles();
					Json payload = Json.object().set("stopped", false).set("reason", "not_running");

					return new CommandResult("stop", "Controller is not running.", payload, json);
				}

				boolean actionable = status.running || status.degraded || Boolean.TRUE.equals(status.pidAlive);
				if (!status.exists || !actionable) {
					ControllerStatusService.deleteStateFiles();
					Json payload = Json.object().set("stopped", false).set("reason", "not_running");

					return new CommandResult("stop", "Controller is not running.", payload, json);
				}

				boolean stopped = false;
				if (status.running) {
					ControllerClient client = ControllerStatusService.controllerClient(status);
					try {
						client.shutdown();
						stopped = ControllerStatusService.waitForControllerStop(status, 8000L);
					} catch (Exception ignored) {
					}
				} else if (status.degraded && !finalForce) {
					throw new KemuCliException(
						"STOP_FAILED",
						"Controller is unreachable. Retry with --force.",
						CliExitCodes.RUNTIME,
						"stop",
						json);
				}

				if (!stopped && finalForce && status.pid != null && ControllerStatusService.isPidAlive(status.pid)) {
					ControllerStatusService.forceStopPid(status, "stop", json);
					stopped = ControllerStatusService.waitForControllerStop(status, 5000L);
				}

				if (!stopped) {
					throw new KemuCliException(
						"STOP_FAILED", "Controller did not stop cleanly.", CliExitCodes.RUNTIME, "stop", json);
				}

				ControllerStatusService.deleteStateFiles();
				Json payload = Json.object().set("stopped", true);

				return new CommandResult("stop", "Controller stopped.", payload, json);
			}
		});
	}
}
