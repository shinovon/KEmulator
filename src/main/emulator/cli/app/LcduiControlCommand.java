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

public final class LcduiControlCommand implements CliCommand {
	private final String group;
	private final String action;
	private final CommandPath path;

	public LcduiControlCommand(String group, String action) {
		this.group = group;
		this.action = action;
		this.path = CommandPath.of(group, action);
	}

	public CommandPath path() {
		return path;
	}

	private String commandName() {
		return group + " " + action;
	}

	private KemuCliException usage(boolean json) {
		return new KemuCliException(
			"USAGE_ERROR",
			"Invalid options for " + commandName() + '.',
			CliExitCodes.USAGE,
			commandName(),
			json);
	}

	private long parseRevision(String value, boolean json) {
		try {
			long revision = Long.parseLong(value);
			if (revision < 0L) {
				throw usage(json);
			}
			return revision;
		} catch (NumberFormatException e) {
			throw usage(json);
		}
	}

	public CommandResult run(CliInvocation invocation) throws Exception {
		boolean json = invocation.json();
		Json request = Json.object();
		int valueIndex = 2;
		if (invocation.tokens().size() <= valueIndex) {
			throw usage(json);
		}
		if ("list".equals(group) && "move".equals(action)) {
			request.set("direction", invocation.tokens().get(valueIndex));
		} else if ("text-field".equals(group)) {
			request.set("value", invocation.tokens().get(valueIndex));
		} else {
			String key = "gauge".equals(group) ? "value" : "index";
			request.set(
				key,
				CliParsing.parseIntegerArgument(
					invocation.tokens().get(valueIndex),
					"<value>",
					commandName(),
					json));
		}
		for (int i = valueIndex + 1; i < invocation.tokens().size(); i++) {
			String token = invocation.tokens().get(i);
			if ("--expect-revision".equals(token)) {
				if (i + 1 >= invocation.tokens().size() || request.has("expectRevision")) {
					throw usage(json);
				}
				request.set("expectRevision", parseRevision(invocation.tokens().get(++i), json));
			} else if ("--timeout".equals(token)) {
				if (i + 1 >= invocation.tokens().size() || request.has("timeoutMs")) {
					throw usage(json);
				}
				int timeout = CliParsing.parseIntegerArgument(
					invocation.tokens().get(++i),
					"--timeout",
					commandName(),
					json);
				request.set(
					"timeoutMs",
					CliParsing.requireInclusiveRange(
						timeout,
						0,
						AutomationLimits.MAX_WAIT_MS,
						"--timeout",
						commandName(),
						json));
			} else if ("--item-index".equals(token)
				&& ("choice".equals(group) || "gauge".equals(group) || "text-field".equals(group))) {
				if (i + 1 >= invocation.tokens().size() || request.has("itemIndex")) {
					throw usage(json);
				}
				request.set(
					"itemIndex",
					CliParsing.parseIntegerArgument(
						invocation.tokens().get(++i),
						"--item-index",
						commandName(),
						json));
			} else if ("--count".equals(token) && "list".equals(group) && "move".equals(action)) {
				if (i + 1 >= invocation.tokens().size() || request.has("count")) {
					throw usage(json);
				}
				request.set(
					"count",
					CliParsing.parseIntegerArgument(
						invocation.tokens().get(++i),
						"--count",
						commandName(),
						json));
			} else {
				throw usage(json);
			}
		}
		if (!request.has("timeoutMs")) {
			request.set("timeoutMs", 5000);
		}
		String operation = "app." + group + "." + action;
		ControllerStatus status = ControllerLifecycle.requireRunningController(commandName(), json);
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			operation,
			request,
			commandName(),
			json));
		return new CommandResult(commandName(), "LCDUI model updated.", payload, json);
	}
}
