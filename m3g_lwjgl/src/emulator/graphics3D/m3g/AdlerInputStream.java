package emulator.graphics3D.m3g;

import java.io.IOException;
import java.io.InputStream;

public final class AdlerInputStream extends CountedInputStream {
	private int s1 = 1;
	private int s2 = 0;

	public final int read() throws IOException {
		int value;
		int unsignedValue;
		if ((unsignedValue = value = super.read()) < 0) {
			unsignedValue += 256;
		}

		this.s1 = (this.s1 + unsignedValue) % '\ufff1';
		this.s2 = (this.s2 + this.s1) % '\ufff1';
		return value;
	}

	public final long getChecksum() {
		return ((long) this.s2 << 16) + (long) this.s1;
	}

	public AdlerInputStream(InputStream in) {
		super(in);
	}
}
