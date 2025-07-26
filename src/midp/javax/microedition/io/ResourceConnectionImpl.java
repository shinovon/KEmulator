package javax.microedition.io;

import emulator.Emulator;
import emulator.custom.ResourceManager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class ResourceConnectionImpl implements InputConnection {
	String url;

	ResourceConnectionImpl(final String s) {
		Emulator.getEmulator().getLogStream().println("Resource opened: " + s);
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
		return "!blank".equals(url) ? new ByteArrayInputStream(new byte[0]) : ResourceManager.getResourceAsStream(url);
	}

	public final void close() {
	}
}
