package emulator.automation.controller;

import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationException;
import emulator.automation.shared.TextValues;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import mjson.Json;

final class WorkerLaunchOptions {
	final Path dataDir;
	final Path rmsDir;
	final Path fileRoot;
	final List<String> jvmOptions;
	final String sessionId;

	private WorkerLaunchOptions(
		Path dataDir,
		Path rmsDir,
		Path fileRoot,
		List<String> jvmOptions,
		String sessionId) {
		this.dataDir = dataDir;
		this.rmsDir = rmsDir;
		this.fileRoot = fileRoot;
		this.jvmOptions = jvmOptions;
		this.sessionId = sessionId;
	}

	private static Path path(Json request, String key, Path fallback) {
		if (!request.has(key) || request.at(key).isNull()) {
			return fallback.toAbsolutePath().normalize();
		}
		return Paths.get(request.at(key).asString()).toAbsolutePath().normalize();
	}

	private static boolean explicitlyConfigured(Json request, String key) {
		String explicitKey = key + "Explicit";
		if (request.has(explicitKey)) {
			return request.at(explicitKey).asBoolean();
		}
		return request.has(key) && !request.at(key).isNull();
	}

	private static void validateWritableRoot(
		Path path,
		Path runtimeRoot,
		String name,
		boolean explicitlyConfigured) {
		if (path.getParent() == null) {
			throw new AutomationException(
				AutomationErrorCodes.INVALID_REQUEST,
				name + " must not be a filesystem root: " + path);
		}
		if (explicitlyConfigured
			&& (path.equals(runtimeRoot)
			|| runtimeRoot.startsWith(path)
			|| path.startsWith(runtimeRoot))) {
			throw new AutomationException(
				AutomationErrorCodes.INVALID_REQUEST,
				name + " must not overlap the KEmulator runtime bundle: " + path);
		}
	}

	private static void deleteTree(Path root) throws IOException {
		if (!Files.exists(root)) {
			return;
		}
		Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult postVisitDirectory(Path dir, IOException error) throws IOException {
				if (error != null) {
					throw error;
				}
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private static void copyLegacyFileRoot(Path runtimeRoot, final Path fileRoot)
		throws IOException {
		final Path legacyRoot = runtimeRoot.resolve("file").resolve("root").normalize();
		if (Files.exists(fileRoot) || !Files.isDirectory(legacyRoot)) {
			return;
		}
		Files.walkFileTree(legacyRoot, new SimpleFileVisitor<Path>() {
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
				throws IOException {
				Path target = fileRoot.resolve(legacyRoot.relativize(dir)).normalize();
				if (!target.startsWith(fileRoot)) {
					throw new IOException("Legacy file-root entry escapes the session root: " + dir);
				}
				Files.createDirectories(target);
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				throws IOException {
				Path target = fileRoot.resolve(legacyRoot.relativize(file)).normalize();
				if (!target.startsWith(fileRoot)) {
					throw new IOException("Legacy file-root entry escapes the session root: " + file);
				}
				Files.copy(
					file,
					target,
					StandardCopyOption.COPY_ATTRIBUTES,
					StandardCopyOption.REPLACE_EXISTING);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private static List<String> splitJvmOptions(String value) {
		ArrayList<String> result = new ArrayList<String>();
		if (TextValues.isBlank(value)) {
			return result;
		}
		StringBuilder current = new StringBuilder();
		char quote = 0;
		boolean escaped = false;
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			if (escaped) {
				current.append(ch);
				escaped = false;
			} else if (ch == '\\') {
				escaped = true;
			} else if (quote != 0) {
				if (ch == quote) {
					quote = 0;
				} else {
					current.append(ch);
				}
			} else if (ch == '\'' || ch == '"') {
				quote = ch;
			} else if (Character.isWhitespace(ch)) {
				if (current.length() > 0) {
					result.add(current.toString());
					current.setLength(0);
				}
			} else {
				current.append(ch);
			}
		}
		if (escaped || quote != 0) {
			throw new AutomationException(
				AutomationErrorCodes.INVALID_REQUEST,
				"Invalid KEMU_WORKER_JAVA_OPTS quoting");
		}
		if (current.length() > 0) {
			result.add(current.toString());
		}
		return result;
	}

	private static List<String> jvmOptions(Json request) {
		List<String> options = splitJvmOptions(System.getenv("KEMU_WORKER_JAVA_OPTS"));
		String workerXmx = request.has("workerXmx") && !request.at("workerXmx").isNull()
			? request.at("workerXmx").asString()
			: null;
		if (workerXmx != null && !workerXmx.matches("[1-9][0-9]*[kKmMgG]")) {
			throw new AutomationException(
				AutomationErrorCodes.INVALID_REQUEST,
				"Invalid worker heap size: " + workerXmx);
		}
		boolean hasXmx = false;
		for (int i = options.size() - 1; i >= 0; i--) {
			String option = options.get(i);
			if (option.toLowerCase(Locale.US).startsWith("-xmx")) {
				hasXmx = true;
				if (workerXmx != null) {
					options.remove(i);
				}
			}
		}
		if (workerXmx != null) {
			options.add("-Xmx" + workerXmx);
		} else if (!hasXmx) {
			options.add("-Xmx512M");
		}
		return options;
	}

	static WorkerLaunchOptions prepare(Json request, Path sessionRoot, Path runtimeRoot) throws IOException {
		Path dataDir = path(request, "dataDir", sessionRoot.resolve("data"));
		Path rmsDir = path(request, "rmsDir", dataDir.resolve("rms"));
		Path fileRoot = path(request, "fileRoot", dataDir.resolve("files"));
		validateWritableRoot(
			dataDir,
			runtimeRoot,
			"dataDir",
			explicitlyConfigured(request, "dataDir"));
		validateWritableRoot(
			rmsDir,
			runtimeRoot,
			"rmsDir",
			explicitlyConfigured(request, "rmsDir"));
		validateWritableRoot(
			fileRoot,
			runtimeRoot,
			"fileRoot",
			explicitlyConfigured(request, "fileRoot"));
		if (request.at("resetState", false).asBoolean()) {
			deleteTree(dataDir);
			if (!rmsDir.startsWith(dataDir)) {
				deleteTree(rmsDir);
			}
			if (!fileRoot.startsWith(dataDir) && !fileRoot.startsWith(rmsDir)) {
				deleteTree(fileRoot);
			}
		}
		if (!explicitlyConfigured(request, "fileRoot")) {
			copyLegacyFileRoot(runtimeRoot, fileRoot);
		}
		Files.createDirectories(dataDir);
		Files.createDirectories(rmsDir);
		Files.createDirectories(fileRoot);
		Files.createDirectories(dataDir.resolve("tmp"));
		String sessionId = request.at("sessionId", "default").asString();
		return new WorkerLaunchOptions(dataDir, rmsDir, fileRoot, jvmOptions(request), sessionId);
	}
}
