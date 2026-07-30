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

	private static String join(List<String> tokens) {
		StringBuilder value = new StringBuilder();
		for (int i = 0; i < tokens.size(); i++) {
			if (i > 0) {
				value.append(' ');
			}
			value.append(tokens.get(i));
		}
		return value.toString();
	}

	public CommandResult run(String[] args) throws Exception {
		List<String> tokens = new ArrayList<String>();
		boolean json = false;
		String sessionId = null;
		boolean literal = false;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("--".equals(arg)) {
				literal = true;
				tokens.add(arg);
				continue;
			}
			if ("--json".equals(arg)) {
				json = true;
			} else if (!literal && "--session-id".equals(arg)) {
				if (sessionId != null || i + 1 >= args.length) {
					throw new KemuCliException(
						"USAGE_ERROR",
						"Expected one value for --session-id.",
						CliExitCodes.USAGE,
						null,
						json);
				}
				sessionId = args[++i];
			} else {
				tokens.add(arg);
			}
		}
		if (sessionId != null) {
			if (!sessionId.matches("[A-Za-z0-9][A-Za-z0-9._-]{0,63}")) {
				throw new KemuCliException(
					"USAGE_ERROR",
					"Invalid --session-id. Use 1-64 letters, digits, dot, underscore, or dash.",
					CliExitCodes.USAGE,
					null,
					json);
			}
			System.setProperty("kemu.session.id", sessionId);
		}

		CliInvocation invocation = new CliInvocation(tokens, json);
		if (tokens.isEmpty() || isHelpToken(tokens.get(0))) {
			return helpCommand.run(invocation);
		}

		if (isHelpToken(tokens.get(tokens.size() - 1))) {
			List<String> topicTokens = new ArrayList<String>(tokens.subList(0, tokens.size() - 1));
			CliCommand exact = registry.resolveExact(topicTokens);
			if (exact == null) {
				if (CliTextRenderer.hasUsageTopic(join(topicTokens))) {
					return helpCommand.run(new CliInvocation(topicTokens, json));
				}
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
