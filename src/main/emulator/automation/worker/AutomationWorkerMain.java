package emulator.automation.worker;

import emulator.Emulator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class AutomationWorkerMain {
	private AutomationWorkerMain() {
	}

	private static void redirectLogs(Path logFile) throws IOException {
		Path parent = logFile.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}

		PrintStream logStream = new PrintStream(new FileOutputStream(logFile.toFile(), true), true,
			StandardCharsets.UTF_8.name());
		System.setOut(logStream);
		System.setErr(logStream);
	}

	public static void main(String[] args) throws Exception {
		Path logFile = null;
		List<String> emulatorArgs = new ArrayList<String>();
		boolean passThrough = false;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (passThrough) {
				emulatorArgs.add(arg);
				continue;
			}

			if ("--".equals(arg)) {
				passThrough = true;
				continue;
			}

			if ("--log-file".equals(arg)) {
				if (i + 1 >= args.length) {
					throw new IllegalArgumentException("Missing value for --log-file");
				}

				logFile = Paths.get(args[++i]).toAbsolutePath().normalize();
				continue;
			}

			throw new IllegalArgumentException("Unexpected worker wrapper argument: " + arg);
		}

		if (logFile != null) {
			redirectLogs(logFile);
		}

		Emulator.main(emulatorArgs.toArray(new String[emulatorArgs.size()]));
	}
}
