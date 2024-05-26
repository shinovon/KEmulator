package com.nec.io;

import java.io.IOException;
import javax.microedition.io.StreamConnection;

public abstract interface SocketConnection
		extends StreamConnection {
	public static final byte DELAY = 0;
	public static final byte LINGER = 1;
	public static final byte KEEPALIVE = 2;

	public abstract void setSocketOption(byte paramByte, int paramInt)
			throws IOException;

	public abstract int getSocketOption(byte paramByte)
			throws IOException;

	public abstract String getAddress();

	public abstract int getPort();
}
