package com.nttdocomo.io.maker;

import com.nttdocomo.io.*;

import java.io.*;

public class HttpConnectionImpl implements HttpConnection {
	javax.microedition.io.HttpConnection a;
	int b;

	public HttpConnectionImpl(final String s, final int n) throws IOException {
		this.a = (javax.microedition.io.HttpConnection) new javax.microedition.io.HttpConnectionImpl(s);
	}

	public DataInputStream openDataInputStream() throws IOException {
		return this.a.openDataInputStream();
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return this.a.openDataOutputStream();
	}

	public void connect() throws IOException {
		this.b = this.a.getResponseCode();
	}

	public void close() throws IOException {
		this.a.close();
	}

	public InputStream openInputStream() throws IOException {
		return this.a.openInputStream();
	}

	public OutputStream openOutputStream() throws IOException {
		return this.a.openOutputStream();
	}

	public String getEncoding() {
		return this.a.getEncoding();
	}

	public long getLength() {
		return this.a.getLength();
	}

	public String getType() {
		return this.a.getType();
	}

	public String getURL() {
		return this.a.getURL();
	}

	public void setRequestMethod(final String requestMethod) throws IOException {
		this.a.setRequestMethod(requestMethod);
	}

	public void setRequestProperty(final String s, final String s2) throws IOException {
		this.a.setRequestProperty(s, s2);
	}

	public int getResponseCode() throws IOException {
		return this.b;
	}

	public String getResponseMessage() throws IOException {
		return this.a.getResponseMessage();
	}

	public String getHeaderField(final String s) {
		try {
			return this.a.getHeaderField(s);
		} catch (IOException ex) {
			return null;
		}
	}

	public long getDate() {
		try {
			return this.a.getDate();
		} catch (IOException ex) {
			return 0L;
		}
	}

	public long getExpiration() {
		try {
			return this.a.getExpiration();
		} catch (IOException ex) {
			return 0L;
		}
	}

	public long getLastModified() {
		try {
			return this.a.getLastModified();
		} catch (IOException ex) {
			return 0L;
		}
	}

	public void setIfModifiedSince(final long n) {
		System.out.println("** HttpConnectionImpl.setIfModifiedSince(long ifmodifiedsince) not implemented yet **");
	}
}
