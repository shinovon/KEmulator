package emulator.cli.library;

import emulator.cli.controller.*;
import emulator.cli.core.*;
import emulator.cli.output.CliResponses;
import emulator.cli.output.CliTextRenderer;
import mjson.Json;

public final class OpenCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("open");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		final boolean json = invocation.json();
		final OpenOptions options = OpenOptionParsers.parse(invocation.tokens(), json);

		return ControllerLifecycle.withLifecycleLock(new java.util.concurrent.Callable<CommandResult>() {
			public CommandResult call() throws Exception {
				InspectionResult inspection = CliAppInspector.inspect(options.inputPath, "open", json);
				CliAppInspector.validateOpenTarget(inspection, options.midletIndex, "open", json);
				ControllerStatus status = ControllerLifecycle.ensureController(options.startOptions, true, "open",
					json);
				ControllerClient client = ControllerStatusService.controllerClient(status);
				ControllerCalls.rejectIfAppAlreadyActive(client, "open", json);
				Json args = Json.object().set("path", options.inputPath.toString());
				if (options.midletIndex != null) {
					args.set("midlet", options.midletIndex.intValue());
				}

				Json result = ControllerCalls.callController(client, "app.open-path", args, "open", json);
				Json payload = CliResponses.publicizeOpenResult(result);
				payload.set("inputPath", options.inputPath.toString());

				return new CommandResult("open", CliTextRenderer.renderOpen(payload), payload, json);
			}
		});
	}
}
