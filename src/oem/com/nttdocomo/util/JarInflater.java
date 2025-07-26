package com.nttdocomo.util;

import com.nttdocomo.lang.IllegalStateException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class JarInflater {
	Hashtable a;
	Hashtable b;

	public JarInflater(InputStream paramInputStream)
			throws JarFormatException, IOException {
		a(new JarInputStream(paramInputStream));
	}

	public JarInflater(byte[] paramArrayOfByte)
			throws JarFormatException {
		try {
			byte[] arrayOfByte = new byte[paramArrayOfByte.length];
			System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
			a(new JarInputStream(new ByteArrayInputStream(arrayOfByte)));
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
	}

	void a(JarInputStream paramJarInputStream)
			throws IOException {
		this.a = new Hashtable();
		this.b = new Hashtable();
		JarEntry localJarEntry = paramJarInputStream.getNextJarEntry();
		byte[] arrayOfByte = new byte['?'];
		while ((paramJarInputStream.available() > 0) && (localJarEntry != null)) {
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			while (paramJarInputStream.available() > 0) {
				int i = paramJarInputStream.read(arrayOfByte, 0, arrayOfByte.length);
				if (i != -1) {
					localByteArrayOutputStream.write(arrayOfByte, 0, i);
				}
			}
			this.a.put(localJarEntry.getName(), localJarEntry);
			this.b.put(localJarEntry.getName(), localByteArrayOutputStream.toByteArray());
			localJarEntry = paramJarInputStream.getNextJarEntry();
		}
	}

	public void close() {
		this.a = null;
	}

	public long getSize(String paramString)
			throws JarFormatException {
		if (this.a == null) {
			throw new IllegalStateException("JarInflater is closed");
		}
		if (paramString == null) {
			throw new NullPointerException("name is null");
		}
		JarEntry localJarEntry = (JarEntry) this.a.get(paramString);
		return localJarEntry.getSize();
	}

	public InputStream getInputStream(String paramString)
			throws JarFormatException {
		if (this.a == null) {
			throw new IllegalStateException("JarInflater is closed");
		}
		if (paramString == null) {
			throw new NullPointerException("name is null");
		}
		byte[] arrayOfByte = (byte[]) this.b.get(paramString);
		return new ByteArrayInputStream(arrayOfByte);
	}
}
