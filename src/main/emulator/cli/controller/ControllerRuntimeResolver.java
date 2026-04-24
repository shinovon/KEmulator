package emulator.cli.controller;

import emulator.automation.shared.TextValues;
import emulator.cli.core.CliExitCodes;
import emulator.cli.core.KemuCliException;
import emulator.cli.support.KemuPaths;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

final class ControllerRuntimeResolver {
	private ControllerRuntimeResolver() {
	}

	static String javaBinary() {
		String bootstrapJava = System.getProperty("kemu.bootstrap.java");
		if (bootstrapJava != null && bootstrapJava.length() > 0) {
			File bootstrapExe = new File(bootstrapJava);
			if (bootstrapExe.exists()) {
				return bootstrapExe.getAbsolutePath();
			}
		}

		String javaHome = System.getProperty("java.home");
		if (javaHome != null && javaHome.length() > 0) {
			File exe = new File(javaHome, ControllerStatusService.isWindows() ? "bin/java.exe" : "bin/java");
			if (exe.exists()) {
				return exe.getAbsolutePath();
			}
		}

		return "java";
	}

	static int findFreePort() throws IOException {
		java.net.ServerSocket socket = new java.net.ServerSocket(0);
		try {
			return socket.getLocalPort();
		} finally {
			socket.close();
		}
	}

	private static boolean hasNonBlankEnv(String name) {
		String value = System.getenv(name);

		return !TextValues.isBlank(value);
	}

	private static boolean hasX11Display() {
		return hasNonBlankEnv("DISPLAY");
	}

	private static boolean isCommandAvailable(String command) {
		try {
			Process process = new ProcessBuilder(
				ControllerStatusService.isWindows()
					? new String[]{"where", command}
					: new String[]{"sh", "-lc", "command -v \"$1\" >/dev/null 2>&1", "sh", command})
				.start();
			try {
				return process.waitFor() == 0;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();

				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}

	private static String joinClasspath(String... entries) {
		StringBuilder builder = new StringBuilder();
		for (String entry : entries) {
			if (entry == null || entry.length() == 0) {
				continue;
			}

			if (builder.length() > 0) {
				builder.append(File.pathSeparatorChar);
			}

			builder.append(entry);
		}

		return builder.toString();
	}

	private static Path resolveLinuxSwtJar(String commandName, boolean json) {
		String arch = System.getProperty("os.arch", "").toLowerCase(Locale.US);
		ArrayList<String> candidates = new ArrayList<String>();
		if ((arch.contains("x86") || arch.contains("amd")) && arch.contains("64")) {
			candidates.add("home/swt-gtk-linux-x86_64.jar");
		} else if (arch.contains("86")) {
			candidates.add("home/swt-gtk-linux-x86.jar");
		} else if (arch.contains("aarch64") || arch.contains("arm64")) {
			candidates.add("home/swt-gtk-linux-aarch64.jar");
		} else if (arch.contains("arm")) {
			candidates.add("home/swt-gtk-linux-armhf.jar");
		}

		Collections.addAll(
			candidates,
			"home/swt-gtk-linux-x86_64.jar",
			"home/swt-gtk-linux-x86.jar",
			"home/swt-gtk-linux-armhf.jar",
			"home/swt-gtk-linux-aarch64.jar");
		for (String candidate : candidates) {
			Path path = KemuPaths.runtimeRootDir().resolve(candidate);
			if (Files.isRegularFile(path)) {
				return path;
			}
		}

		throw new KemuCliException(
			"MISSING_SWT",
			"Could not find a Linux SWT runtime jar under home/.",
			CliExitCodes.RUNTIME,
			commandName,
			json);
	}

	static ResolvedRuntime resolveRuntime(String requested, String commandName, boolean json) {
		String currentRuntimeKind = System.getProperty("kemu.bootstrap.runtime");
		String currentClasspath = System.getProperty("kemu.bootstrap.classpath");
		if (currentClasspath == null || currentClasspath.length() == 0) {
			currentClasspath = System.getProperty("java.class.path");
		}

		if (currentRuntimeKind != null
			&& currentRuntimeKind.length() > 0
			&& currentClasspath != null
			&& currentClasspath.length() > 0) {
			if (requested == null || requested.equals(currentRuntimeKind)) {
				return new ResolvedRuntime(currentRuntimeKind, currentClasspath);
			}
		}

		ArrayList<ResolvedRuntime> candidates = new ArrayList<ResolvedRuntime>();
		if (Files.isDirectory(KemuPaths.runtimeRootDir().resolve("out/classes-linux"))) {
			candidates.add(new ResolvedRuntime(
				"dev-linux",
				joinClasspath(
					KemuPaths.runtimeRootDir()
						.resolve("out/classes-linux")
						.toString(),
					KemuPaths.runtimeRootDir().resolve("src/res").toString(),
					KemuPaths.runtimeRootDir().resolve("lib/*").toString(),
					resolveLinuxSwtJar(commandName, json).toString())));
		}

		if (Files.isRegularFile(KemuPaths.runtimeRootDir().resolve("KEmulator.jar"))) {
			candidates.add(new ResolvedRuntime(
				"release",
				joinClasspath(
					KemuPaths.runtimeRootDir().resolve("KEmulator.jar").toString(),
					KemuPaths.runtimeRootDir().resolve("lib/*").toString())));
		}

		if (requested != null) {
			for (ResolvedRuntime candidate : candidates) {
				if (requested.equals(candidate.kind)) {
					return candidate;
				}
			}

			throw new KemuCliException(
				"UNKNOWN_RUNTIME",
				"Requested runtime is not available: " + requested,
				CliExitCodes.USAGE,
				commandName,
				json);
		}

		if (candidates.size() == 1) {
			return candidates.get(0);
		}

		if (candidates.isEmpty()) {
			throw new KemuCliException(
				"NO_RUNTIME",
				"Could not find a supported KEmulator runtime.",
				CliExitCodes.RUNTIME,
				commandName,
				json);
		}

		throw new KemuCliException(
			"AMBIGUOUS_RUNTIME",
			"Multiple runtimes detected. Pass --runtime.",
			CliExitCodes.RUNTIME,
			commandName,
			json);
	}

	static String resolveControllerMode(StartOptions options, String commandName, boolean json) {
		if ("headless".equals(options.mode)) {
			if (!ControllerStatusService.isLinux()) {
				throw new KemuCliException(
					"HEADLESS_UNSUPPORTED",
					"Headless mode is currently supported only on Linux.",
					CliExitCodes.RUNTIME,
					commandName,
					json);
			}

			if (!isCommandAvailable("xvfb-run")) {
				throw new KemuCliException(
					"HEADLESS_DEPENDENCY_MISSING",
					"Headless mode requires xvfb-run.",
					CliExitCodes.RUNTIME,
					commandName,
					json);
			}

			return "headless";
		}

		if ("visible".equals(options.mode)) {
			if (!hasX11Display()) {
				throw new KemuCliException(
					"DISPLAY_REQUIRED",
					"Visible mode currently requires X11 DISPLAY.",
					CliExitCodes.RUNTIME,
					commandName,
					json);
			}

			return "visible";
		}

		if (hasX11Display()) {
			return "visible";
		}

		if (ControllerStatusService.isLinux() && isCommandAvailable("xvfb-run")) {
			return "headless";
		}

		throw new KemuCliException(
			"DISPLAY_REQUIRED",
			"No display detected and xvfb-run is unavailable.",
			CliExitCodes.RUNTIME,
			commandName,
			json);
	}
}
