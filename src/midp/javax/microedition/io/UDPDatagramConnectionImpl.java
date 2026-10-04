package javax.microedition.io;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

class UDPDatagramConnectionImpl implements UDPDatagramConnection {

	DatagramSocket socket;
	final String address;
	final boolean server;

	UDPDatagramConnectionImpl(String addr) throws IOException {
		address = addr;
		final int n = addr.indexOf("://") + 3;
		final int n2 = addr.lastIndexOf(":") + 1;
		if (addr.startsWith("datagram://:")) {
			this.socket = new DatagramSocket(n2 == addr.length() ? 0 : Integer.parseInt(addr.substring(n2)));
			server = true;
		} else {
			if (n2 == addr.length()) {
				throw new IllegalArgumentException("Invalid url: " + addr);
			}
			this.socket = new DatagramSocket(new InetSocketAddress(addr.substring(n, n2 - 1), Integer.parseInt(addr.substring(n2))));
			server = false;
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
		return newDatagram(size, null);
	}

	public Datagram newDatagram(int size, String addr) throws IOException {
		if (size < 0) {
			throw new IllegalArgumentException("size");
		}
		byte[] buf = new byte[size];
		return newDatagram(buf, size, null);
	}

	public Datagram newDatagram(byte[] buf, int size) throws IOException {
		return newDatagram(buf, size, null);
	}

	public Datagram newDatagram(byte[] buf, int size, String addr) throws IOException {
		if (buf == null || size < 0 || size > buf.length) {
			throw new IllegalArgumentException();
		}
		if (addr == null && !server) {
			addr = address;
		}
		return new DatagramImpl(buf, size, addr);
	}

	public void close() throws IOException {
		socket.close();
	}
}
