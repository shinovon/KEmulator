package emulator.automation.controller;

import emulator.automation.shared.TextValues;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

final class WorkerLaunchEnvironment {
	private WorkerLaunchEnvironment() {
	}

	static Path resolveRuntimeRoot() {
		String configured = TextValues.trimToNull(System.getProperty("kemu.runtime.root"));
		if (configured != null) {
			return Paths.get(configured).toAbsolutePath().normalize();
		}

		return Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
	}

	private static Path createFallbackAgentJar() {
		InputStream classStream = WorkerLaunchEnvironment.class
			.getClassLoader()
			.getResourceAsStream("emulator/Agent.class");
		if (classStream == null) {
			return null;
		}

		JarOutputStream out = null;
		try {
			Path temp = Files.createTempFile("kem-agent-", ".jar");
			temp.toFile().deleteOnExit();
			Manifest manifest = new Manifest();
			Attributes attrs = manifest.getMainAttributes();
			attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
			attrs.putValue("Premain-Class", "emulator.Agent");
			attrs.putValue("Launcher-Agent-Class", "emulator.Agent");
			attrs.putValue("Can-Redefine-Classes", "false");
			attrs.putValue("Can-Retransform-Classes", "false");
			out = new JarOutputStream(Files.newOutputStream(temp), manifest);
			out.putNextEntry(new JarEntry("emulator/Agent.class"));
			byte[] buffer = new byte[4096];
			int read;
			while ((read = classStream.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}

			out.closeEntry();

			return temp;
		} catch (IOException ignored) {
			return null;
		} finally {
			try {
				classStream.close();
			} catch (IOException ignored) {
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	private static int javaMajor() {
		String version = System.getProperty("java.version", "0");
		try {
			if (version.startsWith("1.")) {
				return Integer.parseInt(version.substring(2, 3));
			}

			int dot = version.indexOf('.');
			int dash = version.indexOf('-');
			int end = dot == -1 ? version.length() : dot;
			if (dash != -1 && dash < end) {
				end = dash;
			}

			return Integer.parseInt(version.substring(0, end));
		} catch (Exception ignored) {
			return 0;
		}
	}

	static String javaBinary() {
		String javaHome = System.getProperty("java.home");
		if (javaHome != null && javaHome.length() > 0) {
			File javaExe = new File(javaHome, "bin/java.exe");
			if (javaExe.exists()) {
				return javaExe.getAbsolutePath();
			}

			File java = new File(javaHome, "bin/java");
			if (java.exists()) {
				return java.getAbsolutePath();
			}
		}

		return "java";
	}

	static Path resolveKemHome() {
		Path runtimeRoot = resolveRuntimeRoot();
		Path home = runtimeRoot.resolve("home").toAbsolutePath().normalize();

		return Files.isDirectory(home) ? home : runtimeRoot;
	}

	static Path resolveJavaAgentJar() {
		String configured = TextValues.trimToNull(System.getProperty("kem.agent"));
		if (configured != null) {
			Path path = Paths.get(configured).toAbsolutePath().normalize();
			if (Files.isRegularFile(path)) {
				return path;
			}
		}

		try {
			Path codeSource = Paths.get(WorkerLaunchEnvironment.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.toURI())
				.toAbsolutePath()
				.normalize();
			if (Files.isRegularFile(codeSource)
				&& codeSource.toString().toLowerCase(Locale.US).endsWith(".jar")) {
				return codeSource;
			}
		} catch (Exception ignored) {
		}

		Path homeJar = Paths.get("home", "KEmulator.jar").toAbsolutePath().normalize();
		if (Files.isRegularFile(homeJar)) {
			return homeJar;
		}

		Path rootJar = Paths.get("KEmulator.jar").toAbsolutePath().normalize();
		if (Files.isRegularFile(rootJar)) {
			return rootJar;
		}

		return createFallbackAgentJar();
	}

	static boolean isLinux() {
		return System.getProperty("os.name", "").toLowerCase(Locale.US).contains("linux");
	}

	static boolean isMac() {
		String os = System.getProperty("os.name", "").toLowerCase(Locale.US);

		return os.contains("mac");
	}

	static boolean isJava9() {
		return javaMajor() >= 9;
	}

	static boolean isJava17() {
		return javaMajor() >= 17;
	}
}
