package javax.microedition.io;

import java.net.*;
import java.util.Arrays;

import emulator.*;
import java.io.*;

final class HttpConnectionImpl implements HttpConnection {
	HttpURLConnection connection;
	private boolean closed;
	private String url;
	private boolean setted;

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
			} catch (IOException ex) {
			}
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
		//System.out.println("setRequestProperty " + s +": " + s2);
		if (s.equalsIgnoreCase("User-Agent")) {
			setua(s2);
			return;
		}
		if (s.equalsIgnoreCase("X-Online-Host")) {
			this.method134(s2);
			//this.connection.setRequestProperty("Host", s2);
			//return;
		}
		this.connection.setRequestProperty(s, s2);
	}

	private void setua(String s2) {
		if(s2 != null && s2.length() > 0) {
			String x = null;
        	DevicePlatform c = Devices.getPlatform(Emulator.deviceName);
        	if(c.exists("SW_PLATFORM") || c.exists("SW_PLATFORM_VERSION"))  {
        		x = "UNTRUSTED/1.0";
        	}
    		this.connection.setRequestProperty("User-Agent", s2 + (x == null ? "" : (" " + x)));
		}
	}

	public final int getResponseCode() throws IOException {
		setUserAgent();
		if (Settings.networkNotAvailable) {
			return 502;
		}
		if (this.connection == null) {
			return 200;
		}
		return this.connection.getResponseCode();
	}

	public final String getResponseMessage() throws IOException {
		setUserAgent();
		if (this.connection == null) {
			return "OK";
		}
		return this.connection.getResponseMessage();
	}

	public final long getExpiration() throws IOException {
		return this.connection.getExpiration();
	}

	public final long getDate() throws IOException {
		return this.connection.getDate();
	}

	public final long getLastModified() throws IOException {
		return this.connection.getLastModified();
	}

	public final String getHeaderField(final String s) throws IOException {
		return this.connection.getHeaderField(s);
	}

	public final int getHeaderFieldInt(final String s, final int n) throws IOException {
		return this.connection.getHeaderFieldInt(s, n);
	}

	public final long getHeaderFieldDate(final String s, final long n) throws IOException {
		return this.connection.getHeaderFieldDate(s, n);
	}

	public final String getHeaderField(final int n) throws IOException {
		return this.connection.getHeaderField(n);
	}

	public final String getHeaderFieldKey(final int n) throws IOException {
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
		setUserAgent();
		if (this.closed) {
			throw new IOException();
		}
		try {
			return this.connection.getInputStream();
		} catch (IOException e) {
			return this.connection.getErrorStream();
		}
	}

	private void setUserAgent() {
		if (setted)
			return;
		try {
			if (getRequestProperty("User-Agent") == null) {
				if (Emulator.customUA != null) {
					connection.setRequestProperty("User-Agent", 
							Emulator.customUA);
				} else {
					connection.setRequestProperty("User-Agent", 
							Emulator.deviceName + " (KEmulator/" + Emulator.titleVersion + "; Profile/MIDP-2.1 Configuration/CLDC-1.1)");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setted = true;
	}

	public final DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(this.openOutputStream());
	}

	public final OutputStream openOutputStream() throws IOException {
		//System.out.println(Arrays.toString(connection.getRequestProperties().entrySet().toArray()));
		setUserAgent();
		if (this.closed) {
			throw new IOException();
		}
		try {
			return this.connection.getOutputStream();
		} catch (Exception ex) {
			throw new IOException(ex.toString());
		}
	}
}
