package com.nokia.microedition.media;

import javax.microedition.media.protocol.SourceStream;
import java.io.IOException;

public class BufferSourceStream extends InputStreamSourceStream {
	private static final int BUFFER_SIZE = 5120;
	SourceStream iSourceStream;
	byte[] iBuffer;
	int iPos;

	public BufferSourceStream(SourceStream aSourceStream) {
		this.iSourceStream = aSourceStream;
	}

	public byte[] getHeader() throws IOException {
		byte[] tmpBuffer = new byte[5120];
		int bytesInBuffer = 5120;
		if (this.iBuffer == null) {
			bytesInBuffer = readAndBuffer(tmpBuffer, 0, 256);
			if (bytesInBuffer < 0) {
				throw new IOException();
			}
		} else if (this.iBuffer.length < 256) {
			bytesInBuffer = readAndBuffer(tmpBuffer, this.iBuffer.length, 256 - this.iBuffer.length);
			if (bytesInBuffer == -1) {
				bytesInBuffer = this.iBuffer.length;
			}
		}
		if (this.iBuffer.length < bytesInBuffer) {
			System.arraycopy(this.iBuffer, 0, tmpBuffer, 0, this.iBuffer.length);
		} else {
			System.arraycopy(this.iBuffer, 0, tmpBuffer, 0, bytesInBuffer);
		}
		return tmpBuffer;
	}

	public int readAndBuffer(byte[] aBuffer, int aOffset, int aLength) throws IOException {
		int bytesInBuffer = this.iSourceStream.read(aBuffer, aOffset, aLength);
		if (this.iBuffer == null) {
			this.iBuffer = new byte[bytesInBuffer];

			System.arraycopy(aBuffer, aOffset, this.iBuffer, 0, bytesInBuffer);
		} else if (bytesInBuffer >= 0) {
			byte[] tempBuffer = this.iBuffer;
			this.iBuffer = new byte[this.iBuffer.length + bytesInBuffer];

			System.arraycopy(tempBuffer, 0, this.iBuffer, 0, tempBuffer.length);

			System.arraycopy(aBuffer, aOffset, this.iBuffer, tempBuffer.length, bytesInBuffer);

			tempBuffer = null;
		}
		return bytesInBuffer;
	}

	public int read(byte[] aBuffer, int aOffset, int aLength) throws IOException {
		int bytesFromBuffer = 0;
		if ((this.iBuffer != null) && (this.iPos < this.iBuffer.length)) {
			if (aLength < this.iBuffer.length - this.iPos) {
				bytesFromBuffer = aLength;
			} else {
				bytesFromBuffer = this.iBuffer.length - this.iPos;
			}
			System.arraycopy(this.iBuffer, this.iPos, aBuffer, aOffset, bytesFromBuffer);

			this.iPos += bytesFromBuffer;
			return bytesFromBuffer;
		}
		int bytesFromStream = 0;
		if (bytesFromBuffer < aLength) {
			bytesFromStream = this.iSourceStream.read(aBuffer, bytesFromBuffer, aLength - bytesFromBuffer);
			if (bytesFromStream != -1) {
				this.iPos += bytesFromStream;
			}
		}
		return bytesFromStream;
	}
}
