package emulator.cli.support;

import emulator.automation.shared.TextValues;
import java.nio.file.Files;
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
		String configured = TextValues.trimToNull(System.getProperty("kemu.automation.dir"));
		if (configured == null) {
			configured = TextValues.trimToNull(System.getenv("KEMU_AUTOMATION_DIR"));
		}
		if (configured != null) {
			return Paths.get(configured).toAbsolutePath().normalize();
		}
		Path legacy = rootDir().resolve("automation");
		if ((Files.isDirectory(legacy) && Files.isWritable(legacy))
			|| (!Files.exists(legacy) && Files.isWritable(rootDir()))) {
			return legacy;
		}
		String rootKey = Integer.toHexString(rootDir().toString().hashCode());
		return Paths.get(System.getProperty("java.io.tmpdir"))
			.resolve("kemu-automation-" + rootKey)
			.toAbsolutePath()
			.normalize();
	}

	public static String sessionId() {
		String configured = TextValues.trimToNull(System.getProperty("kemu.session.id"));
		return configured == null ? "default" : configured;
	}

	public static Path automationSessionDir() {
		if ("default".equals(sessionId())) {
			return automationDir();
		}
		return automationDir().resolve("sessions").resolve(sessionId());
	}

	public static Path automationRunDir() {
		return automationSessionDir().resolve("run");
	}

	public static Path automationLogsDir() {
		return automationSessionDir().resolve("logs");
	}

	public static Path automationCapturesDir() {
		return automationSessionDir().resolve("captures");
	}

	public static Path dataDir() {
		return automationSessionDir().resolve("data");
	}

	public static Path rmsDir() {
		return dataDir().resolve("rms");
	}

	public static Path fileRoot() {
		return dataDir().resolve("files");
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
