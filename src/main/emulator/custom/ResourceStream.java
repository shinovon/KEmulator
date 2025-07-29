package emulator.custom;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ResourceStream extends ByteArrayInputStream {
	private final String name;

	public ResourceStream(byte[] data, String name) {
		super(data);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getPosition() {
		return pos;
	}

	public int read(byte[] b, int off, int len) {
		int pos = this.pos;
		int i = super.read(b, off, len);

		return i;
	}

	public void close() throws IOException {
		super.close();
	}
}
