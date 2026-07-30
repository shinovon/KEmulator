package emulator.cli.app;

import emulator.automation.shared.AutomationLimits;
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
import emulator.cli.parse.CliParsing;
import mjson.Json;

public final class WaitLogCommand implements CliCommand {
	private final boolean logsAlias;

	public WaitLogCommand() {
		this(false);
	}

	public WaitLogCommand(boolean logsAlias) {
		this.logsAlias = logsAlias;
	}

	public CommandPath path() {
		return logsAlias ? CommandPath.of("logs", "wait") : CommandPath.of("wait", "log");
	}

	private KemuCliException usage(boolean json) {
		String commandName = logsAlias ? "logs wait" : "wait log";
		return new KemuCliException(
			"USAGE_ERROR",
			"Usage: kemu " + commandName + " --regex REGEX [--since CURSOR] [--timeout MS]",
			CliExitCodes.USAGE,
			commandName,
			json);
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		boolean json = invocation.json();
		String commandName = logsAlias ? "logs wait" : "wait log";
		String regex = null;
		String since = null;
		int timeoutMs = 5000;
		boolean sawTimeout = false;
		for (int i = 2; i < invocation.tokens().size(); i++) {
			String token = invocation.tokens().get(i);
			if ("--regex".equals(token)) {
				if (regex != null || i + 1 >= invocation.tokens().size()) {
					throw usage(json);
				}
				regex = invocation.tokens().get(++i);
			} else if ("--since".equals(token)) {
				if (since != null || i + 1 >= invocation.tokens().size()) {
					throw usage(json);
				}
				since = invocation.tokens().get(++i);
			} else if ("--timeout".equals(token)) {
				if (sawTimeout || i + 1 >= invocation.tokens().size()) {
					throw usage(json);
				}
				sawTimeout = true;
				timeoutMs = CliParsing.parseIntegerArgument(
					invocation.tokens().get(++i),
					"--timeout",
					commandName,
					json);
				timeoutMs = CliParsing.requireInclusiveRange(
					timeoutMs,
					0,
					AutomationLimits.MAX_WAIT_MS,
					"--timeout",
					commandName,
					json);
			} else {
				throw usage(json);
			}
		}
		if (regex == null) {
			throw usage(json);
		}
		Json request = Json.object().set("regex", regex).set("timeoutMs", timeoutMs);
		if (since != null) {
			request.set("since", since);
		}
		ControllerStatus status = ControllerLifecycle.requireRunningController(commandName, json);
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			"logs.wait",
			request,
			commandName,
			json));
		return new CommandResult(commandName, "Log condition matched.", payload, json);
	}
}
