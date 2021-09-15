package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class StreamConnectionImpl implements StreamConnection {

	private Socket socket;

	public StreamConnectionImpl(Socket accept) {
		socket = accept;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return socket.getInputStream();
	}

	@Override
	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(socket.getInputStream());
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	@Override
	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(socket.getOutputStream());
	}

}
