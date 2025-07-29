package com.nttdocomo.io.maker;

import java.io.*;

public class ScratchpadInputStream extends InputStream {
	InputStream a;

	public ScratchpadInputStream(final InputStream a) {
		this.a = a;
	}

	public int read() throws IOException {
		return this.a.read();
	}

	public int read(final byte[] array) throws IOException {
		return this.a.read(array);
	}

	public int read(final byte[] array, final int n, final int n2) throws IOException {
		return this.a.read(array, n, n2);
	}

	public long skip(final long n) throws IOException {
		return this.a.skip(n);
	}

	public int available() throws IOException {
		return this.a.available();
	}

	public void close() throws IOException {
		this.a.close();
	}

	public void mark(final int n) {
		this.a.mark(n);
	}

	public boolean markSupported() {
		return this.a.markSupported();
	}

	public void reset() throws IOException {
		this.a.reset();
	}
}
