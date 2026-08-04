package emulator.graphics3D.m3g;

import java.io.IOException;
import java.io.InputStream;

public final class PeekInputStream extends InputStream {
	private int[] peekBuffer;
	private InputStream stream;
	private int buffered;
	private int counter;

	public final int read() throws IOException {
		if (this.counter < this.buffered) {
			return this.peekBuffer[this.counter++];
		} else {
			int var1 = this.stream.read();
			if (this.buffered < this.peekBuffer.length) {
				this.peekBuffer[this.buffered] = var1;
				++this.buffered;
			}

			++this.counter;
			return var1;
		}
	}

	public final int available() throws IOException {
		return this.counter < this.buffered ? this.buffered - this.counter + this.stream.available() : this.stream.available();
	}

	public final void close() throws IOException {
		this.stream.close();
	}

	public final void rewind() throws IOException {
		if (this.counter > this.buffered) {
			throw new IOException("Peek buffer overrun.");
		} else {
			this.counter = 0;
		}
	}

	public PeekInputStream(InputStream var1, int var2) {
		this.stream = var1;
		this.peekBuffer = new int[var2];
	}
}
