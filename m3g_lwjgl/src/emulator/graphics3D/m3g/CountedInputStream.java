package emulator.graphics3D.m3g;

import java.io.IOException;
import java.io.InputStream;

public class CountedInputStream extends InputStream {
	private InputStream iStream;
	private int iCounter;

	public int read() throws IOException {
		++this.iCounter;
		return this.iStream.read();
	}

	public final void resetCounter() {
		this.iCounter = 0;
	}

	public final int getCounter() {
		return this.iCounter;
	}

	public int available() throws IOException {
		return this.iStream.available();
	}

	public void close() throws IOException {
		this.iStream.close();
	}

	public CountedInputStream(InputStream var1) {
		this.iStream = var1;
		this.resetCounter();
	}
}
