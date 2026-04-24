package emulator.cli.controller;

import emulator.cli.core.*;
import emulator.cli.output.CliTextRenderer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import mjson.Json;

public final class LogsCommand implements CliCommand {
	public CommandPath path() {
		return CommandPath.of("logs");
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		boolean json = invocation.json();
		if (invocation.tokens().size() < 2) {
			throw new KemuCliException(
				"USAGE_ERROR", CliTextRenderer.usageText("logs"), CliExitCodes.USAGE, "logs", json);
		}

		String target = invocation.tokens().get(1);
		String commandName = "controller".equals(target) || "worker".equals(target) ? "logs " + target : "logs";
		int lines = 100;
		boolean sawLines = false;
		if (!"controller".equals(target) && !"worker".equals(target)) {
			throw new KemuCliException(
				"USAGE_ERROR", CliTextRenderer.usageText("logs"), CliExitCodes.USAGE, "logs", json);
		}

		for (int i = 2; i < invocation.tokens().size(); i++) {
			String token = invocation.tokens().get(i);
			if ("--lines".equals(token)) {
				if (sawLines) {
					throw ControllerOptionParsers.duplicateOption("--lines", commandName, json);
				}

				if (i + 1 >= invocation.tokens().size()) {
					throw new KemuCliException(
						"USAGE_ERROR", CliTextRenderer.usageText("logs"), CliExitCodes.USAGE, commandName, json);
				}

				sawLines = true;
				lines = emulator.cli.parse.CliParsing.parseIntegerArgument(
					invocation.tokens().get(++i), "--lines", commandName, json);
			} else {
				throw new KemuCliException(
					"USAGE_ERROR", CliTextRenderer.usageText("logs"), CliExitCodes.USAGE, commandName, json);
			}
		}

		if (lines <= 0) {
			throw new KemuCliException(
				"USAGE_ERROR",
				"Expected positive integer for --lines: " + lines,
				CliExitCodes.USAGE,
				commandName,
				json);
		}

		if ("controller".equals(target)) {
			Path logFile = Paths.get(
				ControllerStatusService.controllerLogPath(ControllerStatusService.readControllerStatus()))
				.toAbsolutePath()
				.normalize();
			ControllerStatusService.LogTail tail = ControllerStatusService.readLogTail(logFile, lines);
			Json payload = Json.object()
				.set("target", "controller")
				.set("path", logFile.toString())
				.set("exists", tail.exists)
				.set("tail", tail.tail);
			String text = tail.exists ? tail.tail : "Controller log file not found.";

			return new CommandResult("logs controller", text, payload, json);
		}

		ControllerStatus status = ControllerLifecycle.requireRunningController("logs worker", json);
		ControllerClient client = ControllerStatusService.controllerClient(status);
		Json workerLogs = ControllerCalls.callController(
			client, "logs.worker", Json.object().set("maxLines", lines), "logs worker", json);
		Path logFile = Paths.get(workerLogs.at("worker").at("logPath").asString())
			.toAbsolutePath()
			.normalize();
		boolean exists = Files.isRegularFile(logFile);
		String tail = workerLogs.at("tail", "").asString();
		Json payload = Json.object()
			.set("target", "worker")
			.set("path", logFile.toString())
			.set("exists", exists)
			.set("tail", tail);
		String text = exists ? tail : "Worker log file not found.";

		return new CommandResult("logs worker", text, payload, json);
	}
}
