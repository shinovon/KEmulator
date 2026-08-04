package emulator.custom;

import java.io.IOException;
import java.io.InputStream;

public class ResourceConnectionStream extends InputStream {
	final InputStream in;

	public ResourceConnectionStream(InputStream in) {
		this.in = in;
	}

	public int read() throws IOException {
		int r = in.read();
		return r == -1 ? 0 : r;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		int r = in.read(b, off, len);
		return r == -1 ? 0 : r;
	}

	public void close() throws IOException {
		in.close();
	}
}
