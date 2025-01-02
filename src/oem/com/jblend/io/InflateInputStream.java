package com.jblend.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

public class InflateInputStream extends InputStream {

	private final InflaterInputStream in;

	public InflateInputStream(InputStream in) {
		this.in = new InflaterInputStream(in);
	}

	public int read() throws IOException {
		return in.read();
	}

	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	public void close() throws IOException {
		in.close();
	}
}
