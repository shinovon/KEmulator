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
			int value = this.stream.read();
			if (this.buffered < this.peekBuffer.length) {
				this.peekBuffer[this.buffered] = value;
				++this.buffered;
			}

			++this.counter;
			return value;
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

	public PeekInputStream(InputStream stream, int bufferSize) {
		this.stream = stream;
		this.peekBuffer = new int[bufferSize];
	}
}
