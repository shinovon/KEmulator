package javax.microedition.io;

import java.io.IOException;

public interface SocketConnection extends StreamConnection {
	public static final byte DELAY = 0;
	public static final byte LINGER = 1;
	public static final byte KEEPALIVE = 2;
	public static final byte RCVBUF = 3;
	public static final byte SNDBUF = 4;

	void setSocketOption(final byte p0, final int p1) throws IllegalArgumentException, IOException;

	int getSocketOption(final byte p0) throws IllegalArgumentException, IOException;

	String getLocalAddress() throws IOException;

	int getLocalPort() throws IOException;

	String getAddress() throws IOException;

	int getPort() throws IOException;
}
