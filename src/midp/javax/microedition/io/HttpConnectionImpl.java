package javax.microedition.io;

import java.net.*;

import emulator.*;
import java.io.*;

final class HttpConnectionImpl implements HttpConnection {
	HttpURLConnection connection;
	private boolean closed;
	private String url;
	private boolean userAgentSet;

	private void method134(final String s) {
		if (this.url.startsWith("http://10.0.0.172")) {
			try {
				final String substring = this.url.substring("http://10.0.0.172".length());
				String s2 = "http://" + s + "/";
				final int index;
				if ((index = substring.indexOf(47)) != -1) {
					s2 += substring.substring(index + 1);
				}
				Emulator.getEmulator().getLogStream().println("update Connect to: " + s2);
				(this.connection = (HttpURLConnection) new URL(s2).openConnection()).setDoInput(true);
				this.connection.setDoOutput(true);
			} catch (IOException ignored) {}
		}
	}

	public HttpConnectionImpl(String url) throws IOException {
		super();
		this.url = url;
		this.closed = false;
		Emulator.getEmulator().getLogStream().println("Connect to: " + url);
		this.connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
	}

	private void connect() {
		setUserAgent();
	}

	public final String getURL() {
		return this.connection.getURL().toString();
	}

	public final String getProtocol() {
		return this.connection.getURL().getProtocol();
	}

	public final String getHost() {
		return this.connection.getURL().getHost();
	}

	public final String getFile() {
		return this.connection.getURL().getFile();
	}

	public final String getRef() {
		return this.connection.getURL().getRef();
	}

	public final String getQuery() {
		return this.connection.getURL().getQuery();
	}

	public final int getPort() {
		return this.connection.getURL().getPort();
	}

	public final String getRequestMethod() {
		if (this.connection == null) {
			return "GET";
		}
		return this.connection.getRequestMethod();
	}

	public final void setRequestMethod(final String requestMethod) throws IOException {
		if (this.connection != null) {
			this.connection.setRequestMethod(requestMethod);
		}
	}

	public final String getRequestProperty(final String s) {
		return this.connection.getRequestProperty(s);
	}

	public final void setRequestProperty(final String s, final String s2) throws IOException {
		if (s.equalsIgnoreCase("User-Agent")) {
			if(s2 != null && s2.length() > 0) {
				String x = null;
				DevicePlatform c = Devices.getPlatform(Emulator.deviceName);
				if(c.exists("SW_PLATFORM") || c.exists("SW_PLATFORM_VERSION"))  {
					x = "UNTRUSTED/1.0";
				}
				connection.setRequestProperty("User-Agent", s2 + (x == null ? "" : (" " + x)));
				userAgentSet = true;
			}
			return;
		}
		if (s.equalsIgnoreCase("X-Online-Host")) {
			method134(s2);
		}
		connection.setRequestProperty(s, s2);
	}

	public final int getResponseCode() throws IOException {
		if (Settings.networkNotAvailable) {
			return 502;
		}
		if (this.connection == null) {
			return 200;
		}
		connect();
		try {
			return this.connection.getResponseCode();
		} catch (ConnectException e) {
			throw new IOException(e);
		}
	}

	public final String getResponseMessage() throws IOException {
		connect();
		if (this.connection == null) {
			return "OK";
		}
		return this.connection.getResponseMessage();
	}

	public final long getExpiration() throws IOException {
		connect();
		return this.connection.getExpiration();
	}

	public final long getDate() throws IOException {
		connect();
		return this.connection.getDate();
	}

	public final long getLastModified() throws IOException {
		connect();
		return this.connection.getLastModified();
	}

	public final String getHeaderField(final String s) throws IOException {
		connect();
		return this.connection.getHeaderField(s);
	}

	public final int getHeaderFieldInt(final String s, final int n) throws IOException {
		connect();
		return this.connection.getHeaderFieldInt(s, n);
	}

	public final long getHeaderFieldDate(final String s, final long n) throws IOException {
		connect();
		return this.connection.getHeaderFieldDate(s, n);
	}

	public final String getHeaderField(final int n) throws IOException {
		connect();
		return this.connection.getHeaderField(n);
	}

	public final String getHeaderFieldKey(final int n) throws IOException {
		connect();
		return this.connection.getHeaderFieldKey(n);
	}

	public final void close() throws IOException {
		this.closed = true;
	}

	public final String getType() {
		return this.connection.getContentType();
	}

	public final String getEncoding() {
		return this.connection.getContentEncoding();
	}

	public final long getLength() {
		return this.connection.getContentLength();
	}

	public final DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(this.openInputStream());
	}

	/**
	 * @author Shinovon
	 */
	public final InputStream openInputStream() throws IOException {
		if (this.closed) throw new IOException();
		connect();
		try {
			return this.connection.getInputStream();
		} catch (IOException e) {
			InputStream i = this.connection.getErrorStream();
			if(i != null) return i;
			throw e;
		}
	}

	private void setUserAgent() {
		if (userAgentSet) return;
		try {
			if (getRequestProperty("User-Agent") == null) {
				if (Emulator.httpUserAgent != null) {
					connection.setRequestProperty("User-Agent",
							Emulator.httpUserAgent);
				} else {
					connection.setRequestProperty("User-Agent",
							Emulator.deviceName + " (KEmulator/" + Emulator.version + "; Profile/MIDP-2.1 Configuration/CLDC-1.1)");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		userAgentSet = true;
	}

	public final DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(this.openOutputStream());
	}

	public final OutputStream openOutputStream() throws IOException {
		if (this.closed) throw new IOException();
		connect();
		try {
			return this.connection.getOutputStream();
		} catch (Exception ex) {
			throw new IOException(ex.toString());
		}
	}
}
