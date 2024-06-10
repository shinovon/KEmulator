package javax.microedition.io;

import java.net.*;
import emulator.*;
import java.io.*;

final class SocketConnectionImpl implements SocketConnection
{
	Socket socket;
	
	public SocketConnectionImpl(final String s) throws IOException {
		super();
		Emulator.getEmulator().getLogStream().println("Socket opened: " + s);
		final int n = s.indexOf("://") + 3;
		final int n2 = s.lastIndexOf(":") + 1;
		final String substring = s.substring(n, n2 - 1);
		final int int1 = Integer.parseInt(s.substring(n2));
		Emulator.getEmulator().getLogStream().println("host:" + substring);
		Emulator.getEmulator().getLogStream().println("port:" + int1);
		socket = new Socket(substring, int1);
	}
	
	public final String getAddress() throws IOException {
		return socket.getInetAddress().getHostAddress();
	}
	
	public final String getLocalAddress() throws IOException {
		return socket.getLocalAddress().getHostAddress();
	}
	
	public final int getLocalPort() throws IOException {
		return socket.getLocalPort();
	}
	
	public final int getPort() throws IOException {
		return socket.getPort();
	}
	
	public final int getSocketOption(byte b) throws IllegalArgumentException, IOException {
		if (socket != null && socket.isClosed()) {
			throw new IOException("socket closed");
		}
		switch (b) {
			case DELAY: {
				return socket.getTcpNoDelay() ?  1 : 0;
			}
			case LINGER: {
				int value = socket.getSoLinger();
				return value == -1 ? 0 : value;
			}
			case KEEPALIVE: {
				return socket.getKeepAlive() ? 1 : 0;
			}
			case RCVBUF: {
				return socket.getReceiveBufferSize();
			}
			case SNDBUF: {
				return socket.getSendBufferSize();
			}
			default: {
				throw new IllegalArgumentException();
			}
		}
	}
	
	public final void setSocketOption(byte option, int value) throws IllegalArgumentException, IOException {
		switch (option) {
			case DELAY: {
				socket.setTcpNoDelay(value != 0);
				break;
			}
			case LINGER: {
				if (value <= 0) {
					throw new IllegalArgumentException();
				}
				socket.setSoLinger(value != 0, value);
				break;
			}
			case KEEPALIVE: {
				socket.setKeepAlive(value != 0);
				break;
			}
			case RCVBUF: {
				if (value <= 0) {
					throw new IllegalArgumentException();
				}
				socket.setReceiveBufferSize(value);
				break;
			}
			case SNDBUF: {
				if (value <= 0) {
					throw new IllegalArgumentException();
				}
				socket.setSendBufferSize(value);
				break;
			}
			default: {
				throw new IllegalArgumentException();
			}
		}
	}
	
	public final void close() throws IOException {
		socket.close();
	}
	
	public final DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(openInputStream());
	}
	
	public final InputStream openInputStream() throws IOException {
		return socket.getInputStream();
	}
	
	public final DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}
	
	public final OutputStream openOutputStream() throws IOException {
		return socket.getOutputStream();
	}
}
