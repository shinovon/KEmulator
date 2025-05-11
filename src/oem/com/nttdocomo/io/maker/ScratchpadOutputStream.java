package com.nttdocomo.io.maker;

import java.io.*;

public class ScratchpadOutputStream extends OutputStream {
	OutputStream a;

	public ScratchpadOutputStream(final OutputStream a) {
		this.a = a;
	}

	public void write(final byte[] array) throws IOException {
		this.a.write(array);
	}

	public void write(final byte[] array, final int n, final int n2) throws IOException {
		this.a.write(array, n, n2);
	}

	public void write(final int n) throws IOException {
		this.a.write(n);
	}

	public void flush() throws IOException {
		this.a.flush();
	}

	public void close() throws IOException {
		this.a.close();
	}
}
