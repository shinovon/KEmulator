package emulator.graphics2D;

import emulator.*;

public final class b {
	private static IImage anIImage351;
	private static int[] anIntArray352;

	public b() {
		super();
	}

	private static void method161(int max, int max2) {
		if (b.anIImage351 == null || max > b.anIImage351.getWidth() || max2 > b.anIImage351.getHeight()) {
			max = Math.max(max, Emulator.getEmulator().getScreen().getWidth());
			max2 = Math.max(max2, Emulator.getEmulator().getScreen().getHeight());
			b.anIImage351 = Emulator.getEmulator().newImage(max, max2, true);
			if (Settings.g2d == 0) {
				b.anIntArray352 = new int[max * max2];
			}
		}
	}

	public static void method162(final IImage image, final int[] array, final boolean b, int n, final int n2, final int n3, final int n4) {
		int[] data = null;
		Label_0042:
		{
			if (Settings.g2d == 0) {
				data = new int[image.getWidth() * image.getHeight()];
			} else {
				if (Settings.g2d != 1) {
					break Label_0042;
				}
				data = image.getData();
			}
		}
		int n5 = 0;
		for (int i = 0; i < n4; ++i) {
			System.arraycopy(array, n, data, n5, n3);
			if (!b) {
				for (int j = 0; j < n3; ++j) {
					final int n6 = n5 + j;
					data[n6] |= 0xFF000000;
				}
			}
			n5 += image.getWidth();
			n += n2;
		}
		image.setData(data);
	}

	public static IImage method163(final int[] array, final boolean b, int n, final int n2, final int n3, final int n4) {
		method161(n3, n4);
		int[] data = null;
		Label_0039:
		{
			int[] array2;
			if (Settings.g2d == 0) {
				array2 = emulator.graphics2D.b.anIntArray352;
			} else {
				if (Settings.g2d != 1) {
					break Label_0039;
				}
				array2 = emulator.graphics2D.b.anIImage351.getData();
			}
			data = array2;
		}
		int n5 = 0;
		for (int i = 0; i < n4; ++i) {
			System.arraycopy(array, n, data, n5, n3);
			if (!b) {
				for (int j = 0; j < n3; ++j) {
					final int n6 = n5 + j;
					data[n6] |= 0xFF000000;
				}
			}
			n5 += emulator.graphics2D.b.anIImage351.getWidth();
			n += n2;
		}
		emulator.graphics2D.b.anIImage351.setData(data);
		return emulator.graphics2D.b.anIImage351;
	}

	public static IImage method164(final short[] array, final boolean b, int n, final int n2, final int n3, final int n4) {
		method161(n3, n4);
		int[] data = null;
		Label_0039:
		{
			int[] array2;
			if (Settings.g2d == 0) {
				array2 = emulator.graphics2D.b.anIntArray352;
			} else {
				if (Settings.g2d != 1) {
					break Label_0039;
				}
				array2 = emulator.graphics2D.b.anIImage351.getData();
			}
			data = array2;
		}
		int n5 = 0;
		for (int i = 0; i < n4; ++i) {
			int n6 = n;
			int n7 = n5;
			for (int j = 0; j < n3; ++j) {
				final int n8 = array[n6] & 0xF000;
				final int n10;
				final int n9 = (n10 = (b ? ((n8 << 16) + (n8 << 12)) : -16777216)) | (array[n6] & 0xF00) << 12 | (array[n6] & 0xF0) << 8 | (array[n6] & 0xF) << 4;
				++n6;
				data[n7++] = n9;
			}
			n += n2;
			n5 += emulator.graphics2D.b.anIImage351.getWidth();
		}
		emulator.graphics2D.b.anIImage351.setData(data);
		return emulator.graphics2D.b.anIImage351;
	}

	public static void method165(final IImage image, final int[] array, int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
		final int[] data = image.getData();
		int n7 = n4 * image.getWidth() + n3;
		for (int i = 0; i < n6; ++i) {
			System.arraycopy(data, n7, array, n, n5);
			n7 += image.getWidth();
			n += n2;
		}
	}

	public static void method166(final IImage image, final short[] array, int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
		for (int i = 0; i < n6; ++i) {
			int n7 = n;
			for (int j = 0; j < n5; ++j) {
				final int rgb = image.getRGB(n3 + j, n4 + i);
				array[n7++] = (short) ((rgb >> 24 & 0xF0) << 8 | (rgb >> 16 & 0xF0) << 4 | (rgb >> 8 & 0xF0) << 0 | (rgb & 0xF0) >> 4);
			}
			n += n2;
		}
	}
}
