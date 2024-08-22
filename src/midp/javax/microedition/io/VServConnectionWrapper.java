package javax.microedition.io;

import emulator.Emulator;

import java.io.*;
import java.nio.charset.StandardCharsets;

final class VServConnectionWrapper implements HttpConnection {
	private String url;
	private ByteArrayOutputStream output;
	private boolean userAgentSet;

	public VServConnectionWrapper(String url) {
		this.url = url;
		Emulator.getEmulator().getLogStream().println("vServ connection wrapped: " + url);
	}

	public final String getURL() {
		return url;
	}

	public final String getProtocol() {
		return "http";
	}

	public final String getHost() {
		return "";
	}

	public final String getFile() {
		return "";
	}

	public final String getRef() {
		return "";
	}

	public final String getQuery() {
		return "";
	}

	public final int getPort() {
		return 80;
	}

	public final String getRequestMethod() {
		return "GET";
	}

	public final void setRequestMethod(final String requestMethod) throws IOException {
	}

	public final String getRequestProperty(final String s) {
		return "";
	}

	public final void setRequestProperty(final String s, final String s2) throws IOException {
		if ("user-agent".equalsIgnoreCase(s)) {
			userAgentSet = true;
		}
	}

	public final int getResponseCode() throws IOException {
//		if (output != null) {
//			System.out.println("output: " + new String(output.toByteArray(), "UTF-8"));
//		}
		return url.startsWith("vserv:") ? 200 : 302;
	}

	public final String getResponseMessage() throws IOException {
		return "";
	}

	public final long getExpiration() throws IOException {
		return 0;
	}

	public final long getDate() throws IOException {
		return 0;
	}

	public final long getLastModified() throws IOException {
		return 0;
	}

	public final String getHeaderField(final String s) throws IOException {
		if ("location".equalsIgnoreCase(s)) {
			return "vserv:";
		}
		if ("X-VSERV-CONTEXT".equals(s)) {
			return "asd";
		}
		return "";
	}

	public final int getHeaderFieldInt(final String s, final int n) throws IOException {
		return 0;
	}

	public final long getHeaderFieldDate(final String s, final long n) throws IOException {
		return 0;
	}

	public final String getHeaderField(final int n) throws IOException {
		throw new IOException();
	}

	public final String getHeaderFieldKey(final int n) throws IOException {
		throw new IOException();
	}

	public final void close() throws IOException {
	}

	public final String getType() {
		return "";
	}

	public final String getEncoding() {
		return "";
	}

	public final long getLength() {
		return 0;
	}

	public final DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(this.openInputStream());
	}

	public final InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream("resource://!blank".getBytes(StandardCharsets.UTF_8));
	}

	public final DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(this.openOutputStream());
	}

	public final OutputStream openOutputStream() throws IOException {
		return output = new ByteArrayOutputStream();
	}
}
