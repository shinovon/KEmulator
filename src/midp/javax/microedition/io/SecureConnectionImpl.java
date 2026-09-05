package javax.microedition.io;

import emulator.Emulator;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

class SecureConnectionImpl extends SocketConnectionImpl implements SecureConnection {

	SSLSocket sslSocket;

	public SecureConnectionImpl(final String s) throws IOException {
		super();
		Emulator.getEmulator().getLogStream().println("Socket opened: " + s);
		final int n = s.indexOf("://") + 3;
		final int n2 = s.lastIndexOf(":") + 1;
		socket = SSLSocketFactory.getDefault().createSocket(s.substring(n, n2 - 1), Integer.parseInt(s.substring(n2)));
		sslSocket = (SSLSocket) socket;
	}

	public SecurityInfo getSecurityInfo() throws IOException {
		sslSocket.startHandshake();
		return new SecurityInfoImpl(sslSocket.getHandshakeSession());
	}
}
