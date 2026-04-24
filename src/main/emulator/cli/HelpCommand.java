package emulator.cli;

import emulator.cli.core.*;
import emulator.cli.output.CliTextRenderer;
import emulator.cli.support.KemuPaths;
import java.util.ArrayList;
import java.util.List;
import mjson.Json;

final class HelpCommand implements CliCommand {
	private final CommandRegistry registry;

	HelpCommand(CommandRegistry registry) {
		this.registry = registry;
	}

	public CommandPath path() {
		return CommandPath.of("help");
	}

	public CommandResult run(CliInvocation invocation) {
		List<String> topicTokens = resolveTopicTokens(invocation.tokens());
		String topic = null;
		String usage;
		if (topicTokens.isEmpty()) {
			usage = CliTextRenderer.usageText();
		} else {
			CliCommand target = registry.resolve(topicTokens);
			if (target == null) {
				throw new KemuCliException(
					"UNKNOWN_COMMAND",
					"Unknown command: " + join(topicTokens),
					CliExitCodes.USAGE,
					"help",
					invocation.json());
			}

			if (target.path().length() != topicTokens.size()) {
				throw new KemuCliException(
					"USAGE_ERROR",
					CliTextRenderer.usageText(target.path().asString()),
					CliExitCodes.USAGE,
					target.path().asString(),
					invocation.json());
			}

			topic = target.path().asString();
			usage = CliTextRenderer.usageText(topic);
		}

		Json payload = Json.object()
			.set("usage", usage)
			.set("rootDir", KemuPaths.rootDir().toString());
		if (topic != null) {
			payload.set("topic", topic);
		}

		return new CommandResult("help", usage, payload, invocation.json());
	}

	private static List<String> resolveTopicTokens(List<String> tokens) {
		ArrayList<String> topicTokens = new ArrayList<String>();
		if (tokens.isEmpty()) {
			return topicTokens;
		}

		int start = 0;
		if ("help".equals(tokens.get(0)) || "--help".equals(tokens.get(0)) || "-h".equals(tokens.get(0))) {
			start = 1;
		}

		for (int i = start; i < tokens.size(); i++) {
			String token = tokens.get(i);
			if ("--help".equals(token) || "-h".equals(token)) {
				continue;
			}

			topicTokens.add(token);
		}

		return topicTokens;
	}

	private static String join(List<String> tokens) {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < tokens.size(); i++) {
			if (i > 0) {
				out.append(' ');
			}

			out.append(tokens.get(i));
		}

		return out.toString();
	}
}
