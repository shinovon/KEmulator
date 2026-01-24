package emulator.graphics2D;

import emulator.Emulator;
import emulator.Settings;

public final class GraphicsUtils {
	private static IImage bufferImage;
	private static int[] bufferData;

	public GraphicsUtils() {
		super();
	}

	private static void createBuffer(int max, int max2) {
		if (GraphicsUtils.bufferImage == null || max > GraphicsUtils.bufferImage.getWidth() || max2 > GraphicsUtils.bufferImage.getHeight()) {
			max = Math.max(max, Emulator.getEmulator().getScreen().getWidth());
			max2 = Math.max(max2, Emulator.getEmulator().getScreen().getHeight());
			GraphicsUtils.bufferImage = Emulator.getEmulator().newImage(max, max2, true);
			if (Settings.g2d == 0) {
				GraphicsUtils.bufferData = new int[max * max2];
			}
		}
	}

	public static void setImageData(final IImage image, final int[] array, final boolean b, int n, final int n2, final int n3, final int n4) {
		int[] data = null;
		if (Settings.g2d == 0) {
			data = new int[image.getWidth() * image.getHeight()];
		} else if (Settings.g2d == 1) {
			data = image.getData();
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
		if (!image.directAccess()) {
			image.setData(data);
		}
	}

	public static IImage setImageData(final int[] rgbData, final boolean processAlpha, int offset, final int scanlength, final int width, final int height) {
		createBuffer(width, height);
		int[] data = null;
		if (Settings.g2d == 0) {
			data = GraphicsUtils.bufferData;
		} else if (Settings.g2d == 1){
			data = GraphicsUtils.bufferImage.getData();
		}
		int n5 = 0;
		for (int i = 0; i < height; ++i) {
			System.arraycopy(rgbData, offset, data, n5, width);
			if (!processAlpha) {
				for (int j = 0; j < width; ++j) {
					final int n6 = n5 + j;
					data[n6] |= 0xFF000000;
				}
			}
			n5 += GraphicsUtils.bufferImage.getWidth();
			offset += scanlength;
		}
		if (!GraphicsUtils.bufferImage.directAccess()) {
			GraphicsUtils.bufferImage.setData(data);
		}
		return GraphicsUtils.bufferImage;
	}

	public static IImage setImageData(final short[] array, final boolean b, int n, final int n2, final int n3, final int n4) {
		createBuffer(n3, n4);
		int[] data = null;
		if (Settings.g2d == 0) {
			data = bufferData;
		} else if (Settings.g2d == 1) {
			data = bufferImage.getData();
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
			n5 += bufferImage.getWidth();
		}
		if (!bufferImage.directAccess()) {
			bufferImage.setData(data);
		}
		return bufferImage;
	}

	public static void getImageData(final IImage image, final int[] array, int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
		final int[] data = image.getData();
		int n7 = n4 * image.getWidth() + n3;
		for (int i = 0; i < n6; ++i) {
			System.arraycopy(data, n7, array, n, n5);
			n7 += image.getWidth();
			n += n2;
		}
	}

	public static void getImageData(final IImage image, final short[] array, int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
		try {
			for (int i = 0; i < n6; ++i) {
				int n7 = n;
				for (int j = 0; j < n5; ++j) {
					final int rgb = image.getRGB(n3 + j, n4 + i);
					array[n7++] = (short) ((rgb >> 24 & 0xF0) << 8 | (rgb >> 16 & 0xF0) << 4 | (rgb >> 8 & 0xF0) << 0 | (rgb & 0xF0) >> 4);
				}
				n += n2;
			}
		} catch (Exception ignored) {}
	}
}
