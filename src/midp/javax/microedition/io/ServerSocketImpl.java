package javax.microedition.io;

import emulator.Emulator;

import java.io.IOException;
import java.net.ServerSocket;

class ServerSocketImpl implements ServerSocketConnection {

	private ServerSocket socket;

	public ServerSocketImpl(String s) throws NumberFormatException, IOException {
		Emulator.getEmulator().getLogStream().println("Socket server opened: " + s);
		socket = new ServerSocket(Integer.parseInt(s.replace("socket://:", "")));
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

	@Override
	public StreamConnection acceptAndOpen() throws IOException {
		return new StreamConnectionImpl(socket.accept());
	}

	@Override
	public String getLocalAddress() throws IOException {
		return socket.getLocalSocketAddress().toString();
	}

	@Override
	public int getLocalPort() throws IOException {
		return socket.getLocalPort();
	}

}
