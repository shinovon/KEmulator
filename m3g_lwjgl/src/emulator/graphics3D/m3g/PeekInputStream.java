package emulator.graphics3D.m3g;

import java.io.IOException;
import java.io.InputStream;

public final class PeekInputStream extends InputStream {
	private int[] iPeekBuffer;
	private InputStream iStream;
	private int iBuffered;
	private int iCounter;

	public final int read() throws IOException {
		if (this.iCounter < this.iBuffered) {
			return this.iPeekBuffer[this.iCounter++];
		} else {
			int var1 = this.iStream.read();
			if (this.iBuffered < this.iPeekBuffer.length) {
				this.iPeekBuffer[this.iBuffered] = var1;
				++this.iBuffered;
			}

			++this.iCounter;
			return var1;
		}
	}

	public final int available() throws IOException {
		return this.iCounter < this.iBuffered ? this.iBuffered - this.iCounter + this.iStream.available() : this.iStream.available();
	}

	public final void close() throws IOException {
		this.iStream.close();
	}

	public final void rewind() throws IOException {
		if (this.iCounter > this.iBuffered) {
			throw new IOException("Peek buffer overrun.");
		} else {
			this.iCounter = 0;
		}
	}

	public PeekInputStream(InputStream var1, int var2) {
		this.iStream = var1;
		this.iPeekBuffer = new int[var2];
	}
}
