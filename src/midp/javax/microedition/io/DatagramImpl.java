package javax.microedition.io;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

class DatagramImpl implements Datagram {

	private String addr;
	DatagramPacket packet;

	private DataInputStream dataInputStream;
	private DatagramByteArrayOutputStream byteOStreamAdapter;
	private DataOutputStream dataOutputStream;

	private byte[] data;
	private int offset;
	private int length;

	DatagramImpl(byte[] buf, int size, String addr) throws IOException {
		if (buf == null) {
			buf = new byte[size];
		}
		this.data = buf;
		this.length = size;

		packet = new DatagramPacket(buf, size);
		if (addr != null) {
			setAddress(addr);
		}
		dataInputStream = new DataInputStream(new ByteArrayInputStream(buf, 0, size));
		dataOutputStream = new DataOutputStream(byteOStreamAdapter = new DatagramByteArrayOutputStream(buf));
	}

	void send(DatagramSocket socket) throws IOException {
		packet.setLength(length);
		socket.send(packet);
	}

	void receive(DatagramSocket socket) throws IOException {
		socket.receive(packet);
		addr = "datagram://" + packet.getAddress().getHostAddress() + ":" + packet.getPort();
		setLength(packet.getLength());
	}

	public String getAddress() {
		return addr;
	}

	public byte[] getData() {
		return data;
	}

	public int getLength() {
		return length;
	}

	public int getOffset() {
		return offset;
	}

	public void setAddress(String addr) throws IOException {
		if (addr == null || !addr.startsWith("datagram://")) {
			throw new IllegalArgumentException();
		}
		this.addr = addr;
		addr = addr.substring("datagram://".length());
		int i = addr.indexOf(':');
		int port = 0;
		if (i != -1) {
			port = Integer.parseInt(addr.substring(i + 1));
			addr = addr.substring(i);
		}
		if (addr.isEmpty()) {
			addr = "0.0.0.0";
		}
		packet.setSocketAddress(new InetSocketAddress(addr, port));
	}

	public void setAddress(Datagram p0) {
		if (p0 == null) throw new IllegalArgumentException();
		packet.setAddress(((DatagramImpl) p0).packet.getAddress());
	}

	public void setLength(int len) {
		setData(data, offset, len);
	}

	public void setData(byte[] buffer, int offset, int len) {
		if (buffer == null || offset < 0 || len < 0 || offset + len > buffer.length) {
			throw new IllegalArgumentException();
		}
		this.data = buffer;
		this.offset = offset;
		this.length = len;
		packet.setData(buffer, offset, len);
		dataInputStream = new DataInputStream(new ByteArrayInputStream(data, offset, len));
		dataOutputStream = new DataOutputStream(byteOStreamAdapter = new DatagramByteArrayOutputStream(data));
	}

	public void reset() {
		dataInputStream = new DataInputStream(new ByteArrayInputStream(data, 0, 0));
		byteOStreamAdapter.reset();
		dataOutputStream = new DataOutputStream(byteOStreamAdapter);
		offset = 0;
		length = 0;
	}

	public void readFully(byte[] b) throws IOException {
		dataInputStream.readFully(b);
	}

	public void readFully(byte[] b, int off, int len) throws IOException {
		dataInputStream.readFully(b, off, len);
	}

	public int skipBytes(int n) throws IOException {
		return dataInputStream.skipBytes(n);
	}

	public boolean readBoolean() throws IOException {
		return dataInputStream.readBoolean();
	}

	public byte readByte() throws IOException {
		return dataInputStream.readByte();
	}

	public int readUnsignedByte() throws IOException {
		return dataInputStream.readUnsignedByte();
	}

	public short readShort() throws IOException {
		return dataInputStream.readShort();
	}

	public int readUnsignedShort() throws IOException {
		return dataInputStream.readUnsignedShort();
	}

	public char readChar() throws IOException {
		return dataInputStream.readChar();
	}

	public int readInt() throws IOException {
		return dataInputStream.readInt();
	}

	public long readLong() throws IOException {
		return dataInputStream.readLong();
	}

	public float readFloat() throws IOException {
		return dataInputStream.readFloat();
	}

	public double readDouble() throws IOException {
		return dataInputStream.readDouble();
	}

	public String readLine() throws IOException {
		return dataInputStream.readLine();
	}

	public String readUTF() throws IOException {
		return dataInputStream.readUTF();
	}

	public void write(int b) throws IOException {
		dataOutputStream.write(b);
		length = byteOStreamAdapter.size();
	}

	public void write(byte[] b) throws IOException {
		dataOutputStream.write(b);
		length = byteOStreamAdapter.size();
	}

	public void write(byte[] b, int off, int len) throws IOException {
		dataOutputStream.write(b, off, len);
		length = byteOStreamAdapter.size();
	}

	public void writeBoolean(boolean v) throws IOException {
		dataOutputStream.writeBoolean(v);
		length = byteOStreamAdapter.size();
	}

	public void writeByte(int v) throws IOException {
		dataOutputStream.writeByte(v);
		length = byteOStreamAdapter.size();
	}

	public void writeShort(int v) throws IOException {
		dataOutputStream.writeShort(v);
		length = byteOStreamAdapter.size();
	}

	public void writeChar(int v) throws IOException {
		dataOutputStream.writeChar(v);
		length = byteOStreamAdapter.size();
	}

	public void writeInt(int v) throws IOException {
		dataOutputStream.writeInt(v);
		length = byteOStreamAdapter.size();
	}

	public void writeLong(long v) throws IOException {
		dataOutputStream.writeLong(v);
		length = byteOStreamAdapter.size();
	}

	public void writeFloat(float v) throws IOException {
		dataOutputStream.writeFloat(v);
		length = byteOStreamAdapter.size();
	}

	public void writeDouble(double v) throws IOException {
		dataOutputStream.writeDouble(v);
		length = byteOStreamAdapter.size();
	}

	public void writeBytes(String s) throws IOException {
		dataOutputStream.writeBytes(s);
		length = byteOStreamAdapter.size();
	}

	public void writeChars(String s) throws IOException {
		dataOutputStream.writeChars(s);
		length = byteOStreamAdapter.size();
	}

	public void writeUTF(String s) throws IOException {
		dataOutputStream.writeUTF(s);
		length = byteOStreamAdapter.size();
	}
}
