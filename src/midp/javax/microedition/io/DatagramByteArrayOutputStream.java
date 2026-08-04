package javax.microedition.io;

import java.io.ByteArrayOutputStream;

class DatagramByteArrayOutputStream extends ByteArrayOutputStream {

	DatagramByteArrayOutputStream(byte[] buf) {
		this.buf = buf;
		this.count = buf.length;
	}

	public int getLength() {
		return buf.length;
	}

	public void write(int b) {
		if (count == buf.length) {
			throw new IndexOutOfBoundsException();
		}
		super.write(b);
	}

	public void write(byte[] buf, int off, int len) {
		if (count + len > this.buf.length) {
			throw new IndexOutOfBoundsException();
		}
		super.write(buf, off, len);
	}
}
