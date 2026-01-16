package emulator.custom;

import emulator.Emulator;
import emulator.Settings;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import java.io.*;
import java.util.Enumeration;

public final class ResourceManager {
	public ResourceManager() {
	}

	public static InputStream getResourceAsStream(final String s) {
		String name = s;
		byte[] data;
		try {
			if (Emulator.midletJar != null) {
				while (name.length() > 0 && name.startsWith("/")) {
					name = name.substring(1);
				}
				synchronized (Emulator.zipFileLock) {
					ZipFile zipFile = Emulator.zipFile;
					ZipEntry entry;
					if ((entry = zipFile.getEntry(name)) == null) {
						// try again by searching entry ignoring case
						Enumeration entries = zipFile.getEntries();
						fail:
						{
							do {
								entry = (ZipEntry) entries.nextElement();
								if (entry.getName().equalsIgnoreCase(name)) {
									break fail;
								}
							} while (entries.hasMoreElements());
							Emulator.getEmulator().getLogStream().println("Custom.jar.getResourceStream: " + s + " (null)");
							throw new IOException();
						}
					}
					data = new byte[(int) entry.getSize()];
					Emulator.getEmulator().getLogStream().println("Custom.jar.getResourceStream: " + s + " (" + data.length + ")");
					new DataInputStream(zipFile.getInputStream(entry)).readFully(data);
				}
			} else {
				final File fileFromClassPath;
				if ((fileFromClassPath = Emulator.getFileFromClassPath(s)) == null || !fileFromClassPath.exists()) {
					Emulator.getEmulator().getLogStream().println("Custom.path.getResourceStream: " + s + " (null)");
					throw new IOException();
				}
				data = new byte[(int) fileFromClassPath.length()];
				Emulator.getEmulator().getLogStream().println("Custom.path.getResourceStream: " + s + " (" + fileFromClassPath.length() + ")");
				try (DataInputStream in = new DataInputStream(new FileInputStream(fileFromClassPath))) {
					in.readFully(data);
				}
			}
		} catch (Exception ex) {
			if (Settings.hideEmulation) {
				return null;
			}
			return Emulator.class.getResourceAsStream(s);
		}

		if (Settings.hideEmulation) {
			return new ByteArrayInputStream(data);
		}
		return new ResourceStream(data, name);
	}

	public static InputStream getResourceAsStream(final Object o, String substring) {
		String s;
		if (substring.length() > 0 && substring.charAt(0) == '/') {
			s = substring.substring(1);
		} else {
			if (substring.length() > 1 && substring.charAt(0) == '.' && substring.charAt(1) == '/') {
				substring = substring.substring(2);
			}
			final String name;
			final int lastIndex;
			if ((lastIndex = (name = ((Class) o).getName()).lastIndexOf(46)) < 0) {
				return getResourceAsStream(substring);
			}
			s = "/" + name.substring(0, lastIndex + 1).replace('.', '/') + substring;
		}
		substring = s;
		return getResourceAsStream(substring);
	}

	public static byte[] getBytes(final String s) throws IOException {
		return getBytes(((s.indexOf(58) != -1) ? ((InputConnection) Connector.open(s, Connector.READ)).openInputStream() : getResourceAsStream(s)));
	}

	public static byte[] getBytes(final InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return null;
		}
		try {
			int available;
			if ((available = inputStream.available()) <= 0) {
				available = 128;
			}
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(available);
			final byte[] array = new byte[available];
			int read;
			while ((read = inputStream.read(array)) > 0) {
				byteArrayOutputStream.write(array, 0, read);
			}
			final byte[] byteArray = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
			return byteArray;
		} finally {
			inputStream.close();
		}
	}

	public static void write(final InputStream i, OutputStream o) throws IOException {
		if (i == null) {
			throw new IllegalArgumentException("input = null");
		}
		int available;
		if ((available = i.available()) <= 0) {
			available = 128;
		}
		final byte[] buf = new byte[available];
		int read;
		while ((read = i.read(buf)) > 0) {
			o.write(buf, 0, read);
		}
		i.close();
	}
}
