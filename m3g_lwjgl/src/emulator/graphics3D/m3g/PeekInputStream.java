package emulator.graphics3D.m3g;

import java.io.IOException;
import java.io.InputStream;

public final class PeekInputStream extends InputStream {
	private int[] anIntArray1076;
	private InputStream anInputStream1077;
	private int anInt1078;
	private int anInt1079;

	public final int read() throws IOException {
		if (this.anInt1079 < this.anInt1078) {
			return this.anIntArray1076[this.anInt1079++];
		} else {
			int var1 = this.anInputStream1077.read();
			if (this.anInt1078 < this.anIntArray1076.length) {
				this.anIntArray1076[this.anInt1078] = var1;
				++this.anInt1078;
			}

			++this.anInt1079;
			return var1;
		}
	}

	public final int available() throws IOException {
		return this.anInt1079 < this.anInt1078 ? this.anInt1078 - this.anInt1079 + this.anInputStream1077.available() : this.anInputStream1077.available();
	}

	public final void close() throws IOException {
		this.anInputStream1077.close();
	}

	public final void rewind() throws IOException {
		if (this.anInt1079 > this.anInt1078) {
			throw new IOException("Peek buffer overrun.");
		} else {
			this.anInt1079 = 0;
		}
	}

	public PeekInputStream(InputStream var1, int var2) {
		this.anInputStream1077 = var1;
		this.anIntArray1076 = new int[var2];
	}
}
