package emulator.cli.support;

import emulator.automation.shared.TextValues;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class KemuPaths {
	private KemuPaths() {
	}

	private static Path configuredDir(String propertyName, Path fallback) {
		String configured = TextValues.trimToNull(System.getProperty(propertyName));
		Path root;
		if (configured != null) {
			root = Paths.get(configured).toAbsolutePath().normalize();
		} else if (fallback != null) {
			root = fallback.toAbsolutePath().normalize();
		} else {
			root = Paths.get("").toAbsolutePath().normalize();
		}

		return root;
	}

	public static Path rootDir() {
		return configuredDir("kemu.root", null);
	}

	public static Path runtimeRootDir() {
		return configuredDir("kemu.runtime.root", rootDir());
	}

	public static Path automationDir() {
		return rootDir().resolve("automation");
	}

	public static Path automationRunDir() {
		return automationDir().resolve("run");
	}

	public static Path automationLogsDir() {
		return automationDir().resolve("logs");
	}

	public static Path automationCapturesDir() {
		return automationDir().resolve("captures");
	}

	public static Path automationControllerState() {
		return automationRunDir().resolve("controller.state");
	}

	public static Path automationControllerLock() {
		return automationRunDir().resolve("controller.lock");
	}

	public static Path automationControllerLog() {
		return automationLogsDir().resolve("controller.log");
	}
}
