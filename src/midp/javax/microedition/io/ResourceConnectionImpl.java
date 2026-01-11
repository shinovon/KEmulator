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
			this.url = s.replace("\\", "/").substring("resource://".length());
		} else {
			this.url = s.replace("\\", "/").substring("resource:".length());
		}
	}

	public final DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(this.openInputStream());
	}

	public final InputStream openInputStream() throws IOException {
		if ("!blank".equals(url))
			return new ByteArrayInputStream(new byte[0]);
		InputStream i = ResourceManager.getResourceAsStream(url);
		if (i == null)
			throw new ConnectionNotFoundException(url);
		return i;
	}

	public final void close() {
	}
}
