package emulator.graphics3D.m3g;

import java.io.IOException;
import java.io.InputStream;

public final class AdlerInputStream extends CountedInputStream {
	private int iS1 = 1;
	private int iS2 = 0;

	public final int read() throws IOException {
		int var1;
		int var2;
		if ((var2 = var1 = super.read()) < 0) {
			var2 += 256;
		}

		this.iS1 = (this.iS1 + var2) % '\ufff1';
		this.iS2 = (this.iS2 + this.iS1) % '\ufff1';
		return var1;
	}

	public final long getChecksum() {
		return ((long) this.iS2 << 16) + (long) this.iS1;
	}

	public AdlerInputStream(InputStream var1) {
		super(var1);
	}
}
