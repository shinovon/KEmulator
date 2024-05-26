package emulator.media;

public final class a {
	public int anInt1275;
	public int anInt1277;
	public int anInt1278;
	public int anInt1279;
	public int anInt1280;
	public int anInt1281;
	public int anInt1282;
	public int anInt1283;
	static int anInt1284;
	byte[] aByteArray1276;

	public a() {
		super();
		this.aByteArray1276 = new byte[100];
		a.anInt1284 = 0;
	}

	public final byte[] method730() {
		return this.aByteArray1276;
	}

	private void method731(final int n, final int n2) {
		if (a.anInt1284 + n2 >= this.aByteArray1276.length) {
			final int n3 = (n2 > 100) ? n2 : 100;
			final byte[] array = new byte[this.aByteArray1276.length];
			System.arraycopy(this.aByteArray1276, 0, array, 0, this.aByteArray1276.length);
			System.arraycopy(array, 0, this.aByteArray1276 = new byte[array.length + n3], 0, array.length);
		}
		for (int i = n2 - 1; i >= 0; --i) {
			this.aByteArray1276[a.anInt1284++] = (byte) (n >> (i << 3) & 0xFF);
		}
	}

	private void method732(final int n) {
		if (n == this.aByteArray1276.length) {
			return;
		}
		byte[] array2;
		int n2;
		byte[] array3;
		int n3;
		int length;
		if (n < this.aByteArray1276.length) {
			final byte[] array = new byte[this.aByteArray1276.length];
			System.arraycopy(this.aByteArray1276, 0, array, 0, this.aByteArray1276.length);
			this.aByteArray1276 = new byte[n];
			array2 = array;
			n2 = 0;
			array3 = this.aByteArray1276;
			n3 = 0;
			length = n;
		} else {
			final byte[] array4 = new byte[this.aByteArray1276.length];
			System.arraycopy(this.aByteArray1276, 0, array4, 0, this.aByteArray1276.length);
			this.aByteArray1276 = new byte[n];
			array2 = array4;
			n2 = 0;
			array3 = this.aByteArray1276;
			n3 = 0;
			length = array4.length;
		}
		System.arraycopy(array2, n2, array3, n3, length);
	}

	private void method735(final int n) {
		int n2 = 7;
		int n3;
		while (true) {
			n3 = n2;
			if (n >> n3 == 0) {
				break;
			}
			n2 = n3 + 7;
		}
		int n5;
		int n4 = n5 = n3 - 7;
		while (true) {
			final int n6 = n5;
			if (n4 < 0) {
				break;
			}
			a a;
			int n7;
			if (n6 != 0) {
				a = this;
				n7 = (n >> n6 & 0x7F) + 128;
			} else {
				a = this;
				n7 = (n >> n6 & 0x7F);
			}
			a.method731(n7, 1);
			n4 = (n5 = n6 - 7);
		}
	}

	public final void method733() {
		this.method731(1297377380, 4);
		this.method731(6, 4);
		this.method731(1, 2);
		this.anInt1283 = a.anInt1284;
		this.method731(1, 2);
		this.method731(240, 2);
		this.method731(1297379947, 4);
		this.anInt1282 = a.anInt1284;
		this.method731(0, 4);
		this.method735(0);
		this.method731(192, 1);
		this.method731(80, 1); // replaced from 64
		this.method737();
	}

	public final void method736() {
		int n = (int) (240.0f * (4.0f / (1 << this.anInt1277)));
		Label_0047:
		{
			int n2;
			int n3;
			int n4;
			if (this.anInt1278 == 1) {
				n2 = n;
				n3 = n;
				n4 = 2;
			} else {
				if (this.anInt1278 != 2) {
					break Label_0047;
				}
				n2 = n + n / 2;
				n3 = n;
				n4 = 4;
			}
			n = n2 + n3 / n4;
		}
		this.method735(0);
		this.method731(144, 1);
		a a;
		int n5;
		if (this.anInt1275 == 0) {
			a = this;
			n5 = 0;
		} else {
			a = this;
			n5 = 60 + (this.anInt1275 - 1) + this.anInt1279 * 12;
		}
		a.method731(n5, 1);
		this.method731(255, 1);
		this.method735(n);
		this.method731(128, 1);
		a a2;
		int n6;
		if (this.anInt1275 == 0) {
			this.method731(0, 1);
			a2 = this;
			n6 = 0;
		} else {
			this.method731(60 + (this.anInt1275 - 1) + this.anInt1279 * 12, 1);
			int n7;
			if ((n7 = (int) (255.0f * (this.anInt1281 / 14.0f))) > 255) {
				n7 = 255;
			}
			a2 = this;
			n6 = n7;
		}
		a2.method731(n6, 1);
	}

	public final void method737() {
		this.method735(0);
		this.method731(255, 1);
		this.method731(81, 1);
		this.method731(3, 1);
		// tempo
		final int n = this.anInt1280 == 0 ? 60000000 / 250 : 60000000 / this.anInt1280;
		this.method731(n >> 16, 1);
		this.method731(n >> 8 & 0xFF, 1);
		this.method731(n & 0xFF, 1);
	}

	public final void method738() {
		this.method735(0);
		this.method731(255, 1);
		this.method731(47, 1);
		this.method731(0, 1);
		final int anInt1284 = a.anInt1284;
		a.anInt1284 = this.anInt1282;
		this.method731(anInt1284 - this.anInt1282 - 4, 4);
		this.method732(a.anInt1284 = anInt1284);
		this.method734(this.anInt1282 - 4, a.anInt1284, 10);
	}

	private void method734(final int n, final int n2, final int n3) {
		final int n4 = n2 - n;
		int n5 = this.aByteArray1276.length;
		for (int i = 0; i < n3; ++i) {
			this.method732(n5 + n4);
			System.arraycopy(this.aByteArray1276, n, this.aByteArray1276, n5, n4);
			n5 = this.aByteArray1276.length;
		}
		final int anInt1284 = a.anInt1284;
		a.anInt1284 = this.anInt1283;
		this.method731(n3 + 1, 2);
		a.anInt1284 = anInt1284;
	}
}
