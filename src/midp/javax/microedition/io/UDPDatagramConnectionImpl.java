package javax.microedition.io;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

class UDPDatagramConnectionImpl implements UDPDatagramConnection {

	DatagramSocket socket;

	UDPDatagramConnectionImpl(String addr) throws IOException {
		final int n = addr.indexOf("://") + 3;
		final int n2 = addr.lastIndexOf(":") + 1;
		if (n == n2 - 1) {
			this.socket = new DatagramSocket(Integer.parseInt(addr.substring(n2)));
		} else {
			this.socket = new DatagramSocket(new InetSocketAddress(addr.substring(n, n2 - 1), Integer.parseInt(addr.substring(n2))));
		}
	}

	public String getLocalAddress() throws IOException {
		return socket.getLocalAddress().toString();
	}

	public int getLocalPort() throws IOException {
		return socket.getLocalPort();
	}

	public int getMaximumLength() throws IOException {
		return 65536;
	}

	public int getNominalLength() throws IOException {
		return 256;
	}

	public void send(Datagram dgram) throws IOException {
		((DatagramImpl) dgram).send(socket);
	}

	public void receive(Datagram dgram) throws IOException {
		((DatagramImpl) dgram).receive(socket);
	}

	public Datagram newDatagram(int size) throws IOException {
		if (size < 0) {
			throw new IllegalArgumentException();
		}
		return new DatagramImpl(null, size, null);
	}

	public Datagram newDatagram(int size, String addr) throws IOException {
		if (size < 0) {
			throw new IllegalArgumentException();
		}
		return new DatagramImpl(null, size, addr);
	}

	public Datagram newDatagram(byte[] buf, int size) throws IOException {
		if (buf == null || size < 0 || size > buf.length) {
			throw new IllegalArgumentException();
		}
		return new DatagramImpl(buf, size, null);
	}

	public Datagram newDatagram(byte[] buf, int size, String addr) throws IOException {
		if (buf == null || size < 0 || size > buf.length) {
			throw new IllegalArgumentException();
		}
		return new DatagramImpl(buf, size, addr);
	}

	public void close() throws IOException {
		socket.close();
	}
}
