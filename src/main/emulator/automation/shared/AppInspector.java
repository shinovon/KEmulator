package emulator.automation.shared;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public final class AppInspector {
	private AppInspector() {
	}

	private static List<MidletDescriptor> parseMidlets(Properties props) {
		List<MidletDescriptor> entries = new ArrayList<MidletDescriptor>();
		Enumeration<?> keys = props.propertyNames();
		while (keys.hasMoreElements()) {
			String key = String.valueOf(keys.nextElement());
			if (!key.startsWith("MIDlet-")) {
				continue;
			}

			String suffix = key.substring("MIDlet-".length());
			int index;
			try {
				index = Integer.parseInt(suffix);
			} catch (NumberFormatException ignored) {
				continue;
			}

			String raw = props.getProperty(key);
			if (raw == null) {
				continue;
			}

			String[] parts = raw.split(",", 3);
			String name = parts.length > 0 ? TextValues.trimToEmpty(parts[0]) : "";
			String icon = parts.length > 1 ? TextValues.trimToEmpty(parts[1]) : "";
			String className = parts.length > 2 ? TextValues.trimToEmpty(parts[2]) : "";
			if (className.length() == 0) {
				continue;
			}

			entries.add(new MidletDescriptor(index, key, name, icon, className));
		}

		Collections.sort(entries, new Comparator<MidletDescriptor>() {
			public int compare(MidletDescriptor a, MidletDescriptor b) {
				return Integer.compare(a.index, b.index);
			}
		});

		return entries;
	}

	private static void mergeManifest(Properties target, Manifest manifest) {
		if (manifest == null) {
			return;
		}

		Attributes attrs = manifest.getMainAttributes();
		for (Map.Entry<Object, Object> entry : attrs.entrySet()) {
			String key = String.valueOf(entry.getKey());
			if (target.getProperty(key) == null) {
				target.put(key, String.valueOf(entry.getValue()));
			}
		}
	}

	private static Path siblingJarPath(Path descriptorPath) {
		String name = descriptorPath.getFileName().toString();

		return descriptorPath
			.resolveSibling(name.substring(0, name.length() - 3) + "jar")
			.normalize();
	}

	private static Path existingSiblingDescriptorPath(Path jarPath) {
		String name = jarPath.getFileName().toString();
		if (name.length() < 4) {
			return null;
		}

		Path sibling = jarPath.resolveSibling(name.substring(0, name.length() - 3) + "jad")
			.normalize();
		if (Files.exists(sibling) && Files.isRegularFile(sibling)) {
			return sibling;
		}

		return null;
	}

	private static void ensureDescriptorJarExists(Path jarPath) {
		if (jarPath == null || !Files.exists(jarPath) || !Files.isRegularFile(jarPath)) {
			throw new AutomationException(AutomationErrorCodes.PATH_NOT_FOUND, "Descriptor JAR not found: " + jarPath);
		}
	}

	private static void ensureLaunchableMidlets(Path input, List<MidletDescriptor> midlets) {
		if (midlets == null || midlets.isEmpty()) {
			throw new AutomationException(
				AutomationErrorCodes.UNSUPPORTED_INPUT, "No launchable MIDlets found: " + input);
		}
	}

	private static Properties loadProperties(Path path) throws IOException {
		Properties props = new Properties();
		InputStream in = Files.newInputStream(path);
		try {
			props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
		} finally {
			in.close();
		}

		return props;
	}

	private static String decodeDescriptorJarUrl(String rawJarUrl) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(rawJarUrl.length());
		for (int i = 0; i < rawJarUrl.length(); i++) {
			char c = rawJarUrl.charAt(i);
			if (c == '%') {
				if (i + 2 >= rawJarUrl.length()) {
					throw unsupportedDescriptorJarUrl(rawJarUrl, "Incomplete percent-encoded sequence");
				}

				int hi = Character.digit(rawJarUrl.charAt(i + 1), 16);
				int lo = Character.digit(rawJarUrl.charAt(i + 2), 16);
				if (hi < 0 || lo < 0) {
					throw unsupportedDescriptorJarUrl(rawJarUrl, "Invalid percent-encoded sequence");
				}

				out.write((hi << 4) + lo);
				i += 2;
				continue;
			}

			if (c <= 0x7F) {
				out.write((byte) c);
				continue;
			}

			byte[] bytes = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
			out.write(bytes, 0, bytes.length);
		}

		return new String(out.toByteArray(), StandardCharsets.UTF_8);
	}

	private static Path validateDescriptorJarUrl(String decodedJarUrl, String rawJarUrl) {
		if (decodedJarUrl.length() == 0) {
			throw unsupportedDescriptorJarUrl(rawJarUrl, "Value is empty");
		}

		if (looksLikeUriScheme(decodedJarUrl)) {
			throw unsupportedDescriptorJarUrl(rawJarUrl, "Only local relative JAR paths are supported");
		}

		Path path;
		try {
			path = Paths.get(decodedJarUrl);
		} catch (RuntimeException e) {
			throw unsupportedDescriptorJarUrl(rawJarUrl, "Value is not a valid local path");
		}

		if (path.isAbsolute()) {
			throw unsupportedDescriptorJarUrl(rawJarUrl, "Absolute paths are not supported");
		}

		return path;
	}

	private static boolean looksLikeUriScheme(String value) {
		if (value.length() < 2 || !Character.isLetter(value.charAt(0))) {
			return false;
		}

		for (int i = 1; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == ':') {
				return true;
			}

			if (!Character.isLetterOrDigit(c) && c != '+' && c != '-' && c != '.') {
				return false;
			}
		}

		return false;
	}

	private static AutomationException unsupportedDescriptorJarUrl(String rawJarUrl, String reason) {
		return new AutomationException(
			AutomationErrorCodes.UNSUPPORTED_INPUT,
			"Unsupported MIDlet-Jar-URL: " + rawJarUrl + " (" + reason + ')');
	}

	private static byte[] readZipEntry(JarFile zip, ZipEntry entry) throws IOException {
		InputStream in = zip.getInputStream(entry);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}

			return out.toByteArray();
		} finally {
			in.close();
		}
	}

	private static boolean hasUtf8Bom(byte[] raw) {
		return raw != null
			&& raw.length >= 3
			&& (raw[0] & 0xFF) == 0xEF
			&& (raw[1] & 0xFF) == 0xBB
			&& (raw[2] & 0xFF) == 0xBF;
	}

	private static Manifest loadBomManifestFallback(JarFile zip, ZipEntry entry) throws IOException {
		if (entry == null) {
			return null;
		}

		byte[] raw = readZipEntry(zip, entry);
		if (!hasUtf8Bom(raw)) {
			return null;
		}

		Properties props = new Properties();
		InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(raw, 3, raw.length - 3),
			StandardCharsets.UTF_8);
		try {
			props.load(reader);
		} finally {
			reader.close();
		}

		Manifest manifest = new Manifest();
		Attributes attrs = manifest.getMainAttributes();
		for (String key : props.stringPropertyNames()) {
			attrs.putValue(key, props.getProperty(key));
		}

		return manifest;
	}

	private static Path resolveDescriptorJarPath(Path descriptorPath, Properties props) {
		String jarUrl = TextValues.trimToNull(props.getProperty("MIDlet-Jar-URL"));
		if (jarUrl != null) {
			String decoded = decodeDescriptorJarUrl(jarUrl);
			Path localJarPath = validateDescriptorJarUrl(decoded, jarUrl);
			Path descriptorDir = descriptorPath.toAbsolutePath().normalize().getParent();

			return descriptorDir == null
				? localJarPath.normalize()
				: descriptorDir.resolve(localJarPath).normalize();
		}

		return siblingJarPath(descriptorPath);
	}

	private static Manifest loadManifest(Path jarPath) {
		JarFile zip = null;
		try {
			zip = new JarFile(jarPath.toFile());
			try {
				Manifest manifest = zip.getManifest();
				if (manifest != null) {
					return manifest;
				}
			} catch (IOException e) {
				Manifest bomFallback = loadBomManifestFallback(zip, zip.getEntry("META-INF/MANIFEST.MF"));
				if (bomFallback != null) {
					return bomFallback;
				}

				throw e;
			}

			ZipEntry entry = zip.getEntry("META-INF/MANIFEST.MF");
			if (entry == null) {
				return null;
			}

			InputStream in = zip.getInputStream(entry);
			try {
				return new Manifest(in);
			} catch (IOException e) {
				Manifest bomFallback = loadBomManifestFallback(zip, entry);
				if (bomFallback != null) {
					return bomFallback;
				}

				throw e;
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new AutomationException(
				AutomationErrorCodes.UNSUPPORTED_INPUT, "Invalid JAR archive: " + jarPath, null, e);
		} finally {
			if (zip != null) {
				try {
					zip.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	private static String stripExtension(String name) {
		int dot = name.lastIndexOf('.');

		return dot == -1 ? name : name.substring(0, dot);
	}

	public static AppInspection inspect(Path input) throws IOException {
		if (input == null || !Files.exists(input) || !Files.isRegularFile(input)) {
			throw new AutomationException(AutomationErrorCodes.PATH_NOT_FOUND, "Path does not exist: " + input);
		}

		String lower = input.getFileName().toString().toLowerCase(Locale.US);
		if (!(lower.endsWith(".jar") || lower.endsWith(".jad"))) {
			throw new AutomationException(AutomationErrorCodes.UNSUPPORTED_INPUT, "Expected .jar or .jad: " + input);
		}

		Properties props = new Properties();
		Manifest manifest = null;
		Path launchPath = input;
		Path jarPath = null;
		Path jadPath = null;
		String sourceKind;

		if (lower.endsWith(".jar")) {
			sourceKind = "jar";
			jarPath = input;
			jadPath = existingSiblingDescriptorPath(jarPath);
			if (jadPath != null) {
				props.putAll(loadProperties(jadPath));
				boolean descriptorDefinesPrimaryMidlet = props.containsKey("MIDlet-1");
				manifest = loadManifest(jarPath);
				if (!descriptorDefinesPrimaryMidlet) {
					mergeManifest(props, manifest);
				}
			} else {
				manifest = loadManifest(jarPath);
				mergeManifest(props, manifest);
			}
		} else {
			sourceKind = "jad";
			jadPath = input;
			props.putAll(loadProperties(jadPath));
			boolean descriptorDefinesPrimaryMidlet = props.containsKey("MIDlet-1");
			jarPath = resolveDescriptorJarPath(jadPath, props);
			ensureDescriptorJarExists(jarPath);
			manifest = loadManifest(jarPath);
			if (!descriptorDefinesPrimaryMidlet) {
				mergeManifest(props, manifest);
			}
		}

		List<MidletDescriptor> midlets = parseMidlets(props);
		ensureLaunchableMidlets(input, midlets);
		String displayName = TextValues.firstNonBlank(
			props.getProperty("MIDlet-Name"),
			midlets.isEmpty() ? null : midlets.get(0).name,
			stripExtension(input.getFileName().toString()));

		return new AppInspection(
			input,
			launchPath,
			jarPath,
			jadPath,
			sourceKind,
			displayName,
			props.getProperty("MIDlet-Vendor"),
			props.getProperty("MIDlet-Version"),
			midlets);
	}
}
