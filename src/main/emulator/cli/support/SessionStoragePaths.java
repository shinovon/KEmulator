package emulator.cli.support;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SessionStoragePaths {
	public final Path dataDir;
	public final Path rmsDir;
	public final Path fileRoot;

	private SessionStoragePaths(Path dataDir, Path rmsDir, Path fileRoot) {
		this.dataDir = dataDir.toAbsolutePath().normalize();
		this.rmsDir = rmsDir.toAbsolutePath().normalize();
		this.fileRoot = fileRoot.toAbsolutePath().normalize();
	}

	private static Path configPath() {
		return KemuPaths.automationSessionDir().resolve("storage.paths");
	}

	public static SessionStoragePaths defaults() {
		return new SessionStoragePaths(KemuPaths.dataDir(), KemuPaths.rmsDir(), KemuPaths.fileRoot());
	}

	public static SessionStoragePaths of(Path dataDir, Path rmsDir, Path fileRoot) {
		return new SessionStoragePaths(dataDir, rmsDir, fileRoot);
	}

	public static SessionStoragePaths read() throws IOException {
		Path path = configPath();
		if (!Files.isRegularFile(path)) {
			return defaults();
		}
		Map<String, String> values = new LinkedHashMap<String, String>();
		BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				int separator = line.indexOf('=');
				if (separator <= 0) {
					continue;
				}
				values.put(line.substring(0, separator), line.substring(separator + 1));
			}
		} finally {
			reader.close();
		}
		if (!values.containsKey("dataDir")
			|| !values.containsKey("rmsDir")
			|| !values.containsKey("fileRoot")) {
			return defaults();
		}
		return new SessionStoragePaths(
			Paths.get(values.get("dataDir")),
			Paths.get(values.get("rmsDir")),
			Paths.get(values.get("fileRoot")));
	}

	public void write() throws IOException {
		Path path = configPath();
		Files.createDirectories(path.getParent());
		Path temporary = path.resolveSibling(path.getFileName().toString() + ".tmp");
		BufferedWriter writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8);
		try {
			writer.write("dataDir=" + dataDir);
			writer.newLine();
			writer.write("rmsDir=" + rmsDir);
			writer.newLine();
			writer.write("fileRoot=" + fileRoot);
			writer.newLine();
		} finally {
			writer.close();
		}
		try {
			Files.move(
				temporary,
				path,
				StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.ATOMIC_MOVE);
		} catch (AtomicMoveNotSupportedException ignored) {
			Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
		}
	}
}
