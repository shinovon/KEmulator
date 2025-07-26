package com.nttdocomo.io;

import java.io.*;

public class BufferedReader extends Reader {
	public BufferedReader(final Reader reader) {
	}

	public BufferedReader(final Reader reader, final int n) {
	}

	public int read() throws IOException {
		return -1;
	}

	public int read(final char[] array, final int n, final int n2) throws IOException {
		return -1;
	}

	public String readLine() throws IOException {
		return null;
	}

	public long skip(final long n) throws IOException {
		return 0L;
	}

	public boolean ready() throws IOException {
		return false;
	}

	public boolean markSupported() {
		return false;
	}

	public void mark(final int n) throws IOException {
	}

	public void reset() throws IOException {
	}

	public void close() throws IOException {
	}
}
