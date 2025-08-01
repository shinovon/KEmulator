package com.sun.cldc.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public abstract class StreamReader
		extends Reader {
	public InputStream in;

	public Reader open(InputStream paramInputStream, String paramString)
			throws UnsupportedEncodingException {
		return null;
	}

	public boolean ready() {
		return false;
	}

	public boolean markSupported() {
		return false;
	}

	public void mark(int paramInt)
			throws IOException {
	}

	public void reset()
			throws IOException {
	}

	public void close()
			throws IOException {
	}

	public abstract int sizeOf(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
}
