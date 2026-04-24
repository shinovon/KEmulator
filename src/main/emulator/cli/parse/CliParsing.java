package emulator.cli.parse;

import emulator.cli.core.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CliParsing {
	private CliParsing() {
	}

	public static int parseIntegerArgument(String value, String label, String commandName, boolean json) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new KemuCliException(
				"USAGE_ERROR",
				"Expected integer for " + label + ": " + value,
				CliExitCodes.USAGE,
				commandName,
				json);
		}
	}

	public static int requireInclusiveRange(
		int value, int min, int max, String label, String commandName, boolean json) {
		if (value < min || value > max) {
			throw new KemuCliException(
				"USAGE_ERROR",
				"Expected " + label + " between " + min + " and " + max + ": " + value,
				CliExitCodes.USAGE,
				commandName,
				json);
		}

		return value;
	}

	public static Path resolveUserPath(String value) {
		Path path = Paths.get(value);

		return path.toAbsolutePath().normalize();
	}
}
