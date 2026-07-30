package emulator.cli.support;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class SessionStorageArchives {
	private static final int BUFFER_SIZE = 16384;

	private SessionStorageArchives() {
	}

	private static void validateMutableRoot(Path root, String name) throws IOException {
		Path normalized = root.toAbsolutePath().normalize();
		if (normalized.getParent() == null) {
			throw new IOException(name + " must not be a filesystem root: " + normalized);
		}
		Path runtime = KemuPaths.runtimeRootDir().toAbsolutePath().normalize();
		if (normalized.equals(runtime) || runtime.startsWith(normalized)) {
			throw new IOException(name + " must not contain the KEmulator runtime: " + normalized);
		}
	}

	public static void reset(Path root) throws IOException {
		final Path normalized = root.toAbsolutePath().normalize();
		validateMutableRoot(normalized, "storage root");
		if (Files.exists(normalized)) {
			Files.walkFileTree(normalized, new SimpleFileVisitor<Path>() {
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult postVisitDirectory(Path dir, IOException error) throws IOException {
					if (error != null) {
						throw error;
					}
					if (!dir.equals(normalized)) {
						Files.delete(dir);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}
		Files.createDirectories(normalized);
	}

	private static void copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int read;
		while ((read = input.read(buffer)) >= 0) {
			if (read > 0) {
				output.write(buffer, 0, read);
			}
		}
	}

	private static void addTree(final ZipOutputStream zip, final Path root, final String prefix)
		throws IOException {
		if (!Files.exists(root)) {
			return;
		}
		Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Path relative = root.relativize(dir);
				if (relative.toString().length() > 0) {
					zip.putNextEntry(new ZipEntry(prefix + relative.toString().replace('\\', '/') + "/"));
					zip.closeEntry();
				}
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (!attrs.isRegularFile()) {
					return FileVisitResult.CONTINUE;
				}
				String entryName = prefix + root.relativize(file).toString().replace('\\', '/');
				zip.putNextEntry(new ZipEntry(entryName));
				InputStream input = new BufferedInputStream(Files.newInputStream(file));
				try {
					copy(input, zip);
				} finally {
					input.close();
				}
				zip.closeEntry();
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public static void exportArchive(Path output, Map<String, Path> roots) throws IOException {
		Path normalizedOutput = output.toAbsolutePath().normalize();
		for (Path root : roots.values()) {
			if (normalizedOutput.startsWith(root.toAbsolutePath().normalize())) {
				throw new IOException("Archive output must be outside the archived storage roots: " + normalizedOutput);
			}
		}
		Path parent = normalizedOutput.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}
		Path temporary = normalizedOutput.resolveSibling(normalizedOutput.getFileName().toString() + ".tmp");
		ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(
			temporary,
			StandardOpenOption.CREATE,
			StandardOpenOption.TRUNCATE_EXISTING,
			StandardOpenOption.WRITE)));
		try {
			zip.putNextEntry(new ZipEntry("META-INF/kemu-storage-v1"));
			zip.write("schemaVersion=1\n".getBytes("UTF-8"));
			zip.closeEntry();
			for (Map.Entry<String, Path> root : roots.entrySet()) {
				String prefix = root.getKey() + "/";
				addTree(zip, root.getValue().toAbsolutePath().normalize(), prefix);
			}
		} finally {
			zip.close();
		}
		try {
			Files.move(
				temporary,
				normalizedOutput,
				StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.ATOMIC_MOVE);
		} catch (java.nio.file.AtomicMoveNotSupportedException ignored) {
			Files.move(temporary, normalizedOutput, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public static void importArchive(Path archive, Map<String, Path> roots) throws IOException {
		Path normalizedArchive = archive.toAbsolutePath().normalize();
		if (!Files.isRegularFile(normalizedArchive)) {
			throw new IOException("Archive not found: " + normalizedArchive);
		}
		validateArchive(normalizedArchive, roots);
		for (Path root : roots.values()) {
			validateMutableRoot(root, "storage root");
			reset(root);
		}
		ZipInputStream zip = new ZipInputStream(new BufferedInputStream(Files.newInputStream(normalizedArchive)));
		try {
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				String name = entry.getName();
				if ("META-INF/kemu-storage-v1".equals(name)) {
					zip.closeEntry();
					continue;
				}
				int separator = name.indexOf('/');
				if (separator <= 0) {
					throw new IOException("Unknown archive entry: " + name);
				}
				Path root = roots.get(name.substring(0, separator));
				if (root == null) {
					throw new IOException("Unknown archive root: " + name.substring(0, separator));
				}
				String relativeName = name.substring(separator + 1);
				if (relativeName.length() == 0) {
					zip.closeEntry();
					continue;
				}
				Path target = root.toAbsolutePath().normalize().resolve(relativeName).normalize();
				if (!target.startsWith(root.toAbsolutePath().normalize())) {
					throw new IOException("Archive entry escapes storage root: " + name);
				}
				if (entry.isDirectory()) {
					Files.createDirectories(target);
				} else {
					Files.createDirectories(target.getParent());
					OutputStream output = new BufferedOutputStream(Files.newOutputStream(
						target,
						StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING,
						StandardOpenOption.WRITE));
					try {
						copy(zip, output);
					} finally {
						output.close();
					}
				}
				zip.closeEntry();
			}
		} finally {
			zip.close();
		}
	}

	private static void validateArchive(Path archive, Map<String, Path> roots) throws IOException {
		boolean markerSeen = false;
		ZipInputStream zip = new ZipInputStream(new BufferedInputStream(Files.newInputStream(archive)));
		try {
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				String name = entry.getName();
				if ("META-INF/kemu-storage-v1".equals(name)) {
					markerSeen = true;
					zip.closeEntry();
					continue;
				}
				int separator = name.indexOf('/');
				if (separator <= 0) {
					throw new IOException("Unknown archive entry: " + name);
				}
				Path root = roots.get(name.substring(0, separator));
				if (root == null) {
					throw new IOException("Unknown archive root: " + name.substring(0, separator));
				}
				String relativeName = name.substring(separator + 1);
				Path normalizedRoot = root.toAbsolutePath().normalize();
				Path target = normalizedRoot.resolve(relativeName).normalize();
				if (!target.startsWith(normalizedRoot)) {
					throw new IOException("Archive entry escapes storage root: " + name);
				}
				zip.closeEntry();
			}
		} finally {
			zip.close();
		}
		if (!markerSeen) {
			throw new IOException("Unsupported KEmulator storage archive");
		}
	}

	public static Map<String, Path> rmsRoots(SessionStoragePaths paths) {
		Map<String, Path> roots = new LinkedHashMap<String, Path>();
		roots.put("rms", paths.rmsDir);
		return roots;
	}

	public static Map<String, Path> stateRoots(SessionStoragePaths paths) {
		Map<String, Path> roots = new LinkedHashMap<String, Path>();
		roots.put("data", paths.dataDir);
		if (!paths.rmsDir.startsWith(paths.dataDir)) {
			roots.put("rms", paths.rmsDir);
		}
		if (!paths.fileRoot.startsWith(paths.dataDir) && !paths.fileRoot.startsWith(paths.rmsDir)) {
			roots.put("files", paths.fileRoot);
		}
		return roots;
	}
}
