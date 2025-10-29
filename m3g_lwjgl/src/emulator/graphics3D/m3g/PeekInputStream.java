package emulator.graphics3D.m3g;

import java.io.IOException;
import java.io.InputStream;

public final class PeekInputStream extends InputStream {
	private int[] PeekBuffer;
	private InputStream stream;
	private int buffered;
	private int counter;

	public final int read() throws IOException {
		if (this.counter < this.buffered) {
			return this.PeekBuffer[this.counter++];
		} else {
			int var1 = this.stream.read();
			if (this.buffered < this.PeekBuffer.length) {
				this.PeekBuffer[this.buffered] = var1;
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
		this.PeekBuffer = new int[var2];
	}
}
