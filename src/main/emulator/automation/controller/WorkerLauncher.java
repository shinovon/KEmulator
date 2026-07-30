package emulator.automation.controller;

import emulator.automation.shared.ProcessIdentity;
import emulator.automation.shared.TextValues;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import mjson.Json;

final class WorkerLauncher {
	private final Path logsRoot;
	private final int screenWidth;
	private final int screenHeight;

	WorkerLauncher(Path logsRoot, int screenWidth, int screenHeight) {
		this.logsRoot = logsRoot;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	private static int findFreePort() throws IOException {
		ServerSocket socket = new ServerSocket(0);
		try {
			return socket.getLocalPort();
		} finally {
			socket.close();
		}
	}

	private static String currentPid() {
		String runtimeName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		int separator = runtimeName.indexOf('@');

		return separator <= 0 ? runtimeName : runtimeName.substring(0, separator);
	}

	private static String extractProcessPid(Process process) {
		if (process == null) {
			return null;
		}

		try {
			java.lang.reflect.Method pidMethod = Process.class.getMethod("pid");
			Object value = pidMethod.invoke(process);
			if (value != null) {
				return String.valueOf(value);
			}
		} catch (Throwable ignored) {
		}

		try {
			Field field = process.getClass().getDeclaredField("pid");
			field.setAccessible(true);
			Object value = field.get(process);

			return value == null ? null : String.valueOf(value);
		} catch (Throwable ignored) {
			return null;
		}
	}

	WorkerProcess launch(AppTarget entry, String midletClassName, Json request) throws IOException {
		int workerPort = findFreePort();
		Path runtimeRoot = WorkerLaunchEnvironment.resolveRuntimeRoot();
		WorkerLaunchOptions options = WorkerLaunchOptions.prepare(
			request,
			logsRoot.getParent(),
			runtimeRoot);
		Path kemHome = WorkerLaunchEnvironment.resolveKemHome();
		Path javaAgent = WorkerLaunchEnvironment.resolveJavaAgentJar();
		ArrayList<String> command = new ArrayList<String>();
		command.add(WorkerLaunchEnvironment.javaBinary());
		command.addAll(options.jvmOptions);
		command.add("-cp");
		command.add(System.getProperty("java.class.path"));
		if (javaAgent != null) {
			command.add("-javaagent:" + javaAgent.toString());
		}

		command.add("-Dkemu.runtime.root=" + runtimeRoot.toString());
		command.add("-Dkemu.data.dir=" + options.dataDir.toString());
		command.add("-Dkemu.rms.dir=" + options.rmsDir.toString());
		command.add("-Dkemu.file.root=" + options.fileRoot.toString());
		command.add("-Dkemu.session.id=" + options.sessionId);
		command.add("-Dkem.path=" + kemHome.toString());
		command.add("-Duser.home=" + options.dataDir.toString());
		command.add("-Djava.io.tmpdir=" + options.dataDir.resolve("tmp").toString());
		command.add("-Djava.library.path=" + kemHome.toString());
		command.add("-Dfile.encoding=UTF-8");
		command.add("-Dswt.autoScale=false");
		command.add("-XX:+IgnoreUnrecognizedVMOptions");
		if (WorkerLaunchEnvironment.isMac()) {
			command.add("-XstartOnFirstThread");
		}

		if (WorkerLaunchEnvironment.isJava9()) {
			command.add("--add-exports");
			command.add("java.desktop/com.sun.media.sound=ALL-UNNAMED");
			command.add("--add-opens");
			command.add("java.base/java.lang=ALL-UNNAMED");
			command.add("--add-opens");
			command.add("java.base/java.lang.reflect=ALL-UNNAMED");
			command.add("--add-opens");
			command.add("java.base/java.lang.ref=ALL-UNNAMED");
			command.add("--add-opens");
			command.add("java.base/java.io=ALL-UNNAMED");
			command.add("--add-opens");
			command.add("java.base/java.util=ALL-UNNAMED");
			command.add("--add-opens");
			command.add("jdk.unsupported/sun.misc=ALL-UNNAMED");
			command.add("--add-opens");
			command.add("java.desktop/com.sun.media.sound=ALL-UNNAMED");
			command.add("--add-opens");
			command.add("java.desktop/javax.sound.midi=ALL-UNNAMED");
			if (WorkerLaunchEnvironment.isJava17()) {
				command.add("--enable-native-access=ALL-UNNAMED");
			}
		}

		Path logDir = logsRoot.resolve(entry.appLogId);
		Files.createDirectories(logDir);
		String launchId = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS", Locale.US).format(new Date());
		Path logPath = logDir.resolve("worker-" + launchId + ".log");
		Path readyPath = logDir.resolve("worker-" + launchId + ".ready");
		final Path exitPath = logDir.resolve("worker-" + launchId + ".exit");
		Files.deleteIfExists(readyPath);
		Files.deleteIfExists(exitPath);
		command.add("emulator.automation.worker.AutomationWorkerMain");
		command.add("--log-file");
		command.add(logPath.toAbsolutePath().toString());
		command.add("--");
		command.add("emulator.Emulator");
		command.add("-automationworker");
		command.add("-s");
		command.add("-updated");
		if (entry.jadPath != null) {
			command.add("-jad");
			command.add(entry.jadPath.toAbsolutePath().toString());
		}

		if (entry.jarPath != null) {
			command.add("-jar");
			command.add(entry.jarPath.toAbsolutePath().toString());
		}

		String normalizedMidletClassName = TextValues.trimToNull(midletClassName);
		if (normalizedMidletClassName != null) {
			command.add("-midlet");
			command.add(normalizedMidletClassName);
		}

		command.add("-automationport");
		command.add(String.valueOf(workerPort));
		command.add("-automationhost");
		command.add("127.0.0.1");
		if (currentPid() != null && currentPid().length() > 0) {
			command.add("-automationcontrollerpid");
			command.add(currentPid());
		}

		Long controllerStartTimeMs = ProcessIdentity.currentStartTimeMillis();
		if (controllerStartTimeMs != null) {
			command.add("-automationcontrollerstartms");
			command.add(String.valueOf(controllerStartTimeMs.longValue()));
		}

		String controllerStartTicks = ProcessIdentity.currentStartTicks();
		if (controllerStartTicks != null && controllerStartTicks.length() > 0) {
		command.add("-automationcontrollerstartticks");
			command.add(controllerStartTicks);
		}
		command.add("-automationreadyfile");
		command.add(readyPath.toAbsolutePath().toString());

		command.add("-screen");
		command.add(screenWidth + "x" + screenHeight);

		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(runtimeRoot.toFile());
		builder.redirectErrorStream(true);
		builder.redirectOutput(logPath.toFile());
		builder.environment().remove("GDK_BACKEND");
		builder.environment().remove("WAYLAND_DISPLAY");
		builder.environment().put("SWT_GTK4", "0");
		if (WorkerLaunchEnvironment.isLinux()) {
			builder.environment().put("GDK_BACKEND", "x11");
		}

		Process process = builder.start();

		WorkerProcess worker = new WorkerProcess();
		worker.entry = entry;
		worker.pid = extractProcessPid(process);
		worker.port = workerPort;
		worker.process = process;
		worker.startedAt = System.currentTimeMillis();
		worker.logPath = logPath;
		worker.readyPath = readyPath;
		worker.exitPath = exitPath;
		worker.command = new ArrayList<String>(command);
		worker.jvmOptions = new ArrayList<String>(options.jvmOptions);
		worker.dataDir = options.dataDir;
		worker.rmsDir = options.rmsDir;
		worker.fileRoot = options.fileRoot;
		worker.sessionId = options.sessionId;
		final Process watchedProcess = process;
		Thread exitWatcher = new Thread(
			new Runnable() {
				public void run() {
					try {
						int exitCode = watchedProcess.waitFor();
						Files.write(
							exitPath,
							("exitCode=" + exitCode + "\n").getBytes(StandardCharsets.UTF_8));
					} catch (Exception ignored) {
					}
				}
			},
			"KEmulator-Automation-Worker-Exit-Watcher");
		exitWatcher.setDaemon(true);
		exitWatcher.start();

		return worker;
	}
}
