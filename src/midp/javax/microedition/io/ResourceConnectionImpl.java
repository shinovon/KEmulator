package javax.microedition.io;

import emulator.AppSettings;
import emulator.Emulator;
import emulator.custom.ResourceManager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class ResourceConnectionImpl implements InputConnection {
	String url;

	ResourceConnectionImpl(String s) {
		Emulator.getEmulator().getLogStream().println("Resource opened: " + s);
		s = s.trim().replace("\\", "/");
		if (s.startsWith("resource://")) {
			this.url = s.substring("resource://".length());
		} else {
			this.url = s.substring("resource:".length());
		}
	}

	public final DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(this.openInputStream());
	}

	public final InputStream openInputStream() throws IOException {
		if ("!blank".equals(url))
			return new ByteArrayInputStream(new byte[0]);

		final InputStream i = ResourceManager.getResourceAsStream(url);
		if (i == null) {
			throw new ConnectionNotFoundException(url);
		}

		if (Emulator.doja || AppSettings.softbankApi) {
			return i;
		}

		return new InputStream() {
			public int read() throws IOException {
				int r = i.read();
				return r == -1 ? 0 : r;
			}

			public int read(byte[] b, int off, int len) throws IOException {
				int r = i.read(b, off, len);
				return r == -1 ? 0 : r;
			}

			public void close() throws IOException {
				i.close();
			}
		};
	}

	public final void close() {
	}
}
