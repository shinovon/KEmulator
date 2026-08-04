package emulator.graphics3D.m3g;

import java.io.IOException;
import java.io.InputStream;

public class CountedInputStream extends InputStream {
	private InputStream stream;
	private int counter;

	public int read() throws IOException {
		++this.counter;
		return this.stream.read();
	}

	public final void resetCounter() {
		this.counter = 0;
	}

	public final int getCounter() {
		return this.counter;
	}

	public int available() throws IOException {
		return this.stream.available();
	}

	public void close() throws IOException {
		this.stream.close();
	}

	public CountedInputStream(InputStream var1) {
		this.stream = var1;
		this.resetCounter();
	}
}
