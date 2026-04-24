package emulator.cli.core;

import emulator.cli.output.CliTextRenderer;
import java.util.ArrayList;
import java.util.List;

public final class CliApp {
	private final CommandRegistry registry;
	private final CliCommand helpCommand;

	public CliApp(CommandRegistry registry, CliCommand helpCommand) {
		this.registry = registry;
		this.helpCommand = helpCommand;
	}

	private static boolean isHelpToken(String token) {
		return "help".equals(token) || "--help".equals(token) || "-h".equals(token);
	}

	public CommandResult run(String[] args) throws Exception {
		List<String> tokens = new ArrayList<String>();
		boolean json = false;
		for (String arg : args) {
			if ("--json".equals(arg)) {
				json = true;
			} else {
				tokens.add(arg);
			}
		}

		CliInvocation invocation = new CliInvocation(tokens, json);
		if (tokens.isEmpty() || isHelpToken(tokens.get(0))) {
			return helpCommand.run(invocation);
		}

		if (isHelpToken(tokens.get(tokens.size() - 1))) {
			List<String> topicTokens = new ArrayList<String>(tokens.subList(0, tokens.size() - 1));
			CliCommand exact = registry.resolveExact(topicTokens);
			if (exact == null) {
				CliCommand candidate = registry.resolve(topicTokens);
				if (candidate != null) {
					throw new KemuCliException(
						"USAGE_ERROR",
						CliTextRenderer.usageText(candidate.path().asString()),
						CliExitCodes.USAGE,
						candidate.path().asString(),
						json);
				}

				throw new KemuCliException(
					"UNKNOWN_COMMAND",
					"Unknown command: " + topicTokens.get(0),
					CliExitCodes.USAGE,
					topicTokens.get(0),
					json);
			}

			return helpCommand.run(new CliInvocation(topicTokens, json));
		}

		CliCommand command = registry.resolve(tokens);
		if (command == null) {
			throw new KemuCliException(
				"UNKNOWN_COMMAND", "Unknown command: " + tokens.get(0), CliExitCodes.USAGE, tokens.get(0), json);
		}

		return command.run(invocation);
	}
}
