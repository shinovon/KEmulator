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

public final class ConditionWaitCommand implements CliCommand {
	private final String type;
	private final CommandPath path;

	public ConditionWaitCommand(String type) {
		this.type = type;
		this.path = CommandPath.of("wait", type);
	}

	public CommandPath path() {
		return path;
	}

	private KemuCliException usage(boolean json) {
		return new KemuCliException(
			"USAGE_ERROR",
			"Invalid options for wait " + type + '.',
			CliExitCodes.USAGE,
			"wait " + type,
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
		Json request = Json.object().set("type", type);
		int timeoutMs = 5000;
		boolean sawTimeout = false;
		for (int i = 2; i < invocation.tokens().size(); i++) {
			String token = invocation.tokens().get(i);
			if ("--timeout".equals(token)) {
				if (sawTimeout || i + 1 >= invocation.tokens().size()) {
					throw usage(json);
				}
				sawTimeout = true;
				timeoutMs = CliParsing.parseIntegerArgument(
					invocation.tokens().get(++i),
					"--timeout",
					"wait " + type,
					json);
				timeoutMs = CliParsing.requireInclusiveRange(
					timeoutMs,
					0,
					AutomationLimits.MAX_WAIT_MS,
					"--timeout",
					"wait " + type,
					json);
			} else if ("display".equals(type) && "--kind".equals(token)) {
				if (i + 1 >= invocation.tokens().size() || request.has("kind")) {
					throw usage(json);
				}
				request.set("kind", invocation.tokens().get(++i));
			} else if ("display".equals(type) && "--title".equals(token)) {
				if (i + 1 >= invocation.tokens().size() || request.has("title")) {
					throw usage(json);
				}
				request.set("title", invocation.tokens().get(++i));
			} else if ("display".equals(type) && "--selected-index".equals(token)) {
				if (i + 1 >= invocation.tokens().size() || request.has("selectedIndex")) {
					throw usage(json);
				}
				request.set(
					"selectedIndex",
					CliParsing.parseIntegerArgument(
						invocation.tokens().get(++i),
						"--selected-index",
						"wait display",
						json));
			} else if (("display".equals(type) || "frame".equals(type))
				&& "--after-revision".equals(token)) {
				if (i + 1 >= invocation.tokens().size() || request.has("afterRevision")) {
					throw usage(json);
				}
				request.set("afterRevision", parseRevision(invocation.tokens().get(++i), json));
			} else if ("permission".equals(type) && "--name".equals(token)) {
				if (i + 1 >= invocation.tokens().size() || request.has("name")) {
					throw usage(json);
				}
				request.set("name", invocation.tokens().get(++i));
			} else {
				throw usage(json);
			}
		}
		if ("display".equals(type)
			&& !request.has("kind")
			&& !request.has("title")
			&& !request.has("selectedIndex")
			&& !request.has("afterRevision")) {
			throw usage(json);
		}
		request.set("timeoutMs", timeoutMs);
		ControllerStatus status = ControllerLifecycle.requireRunningController("wait " + type, json);
		String operation = "worker-exit".equals(type)
			? "app.wait-worker-exit"
			: "app.wait-condition";
		Json payload = CliResponses.normalizePublicJson(ControllerCalls.callController(
			ControllerStatusService.controllerClient(status),
			operation,
			request,
			"wait " + type,
			json));
		return new CommandResult(
			"wait " + type,
			"Condition matched: " + type + '.',
			payload,
			json);
	}
}
