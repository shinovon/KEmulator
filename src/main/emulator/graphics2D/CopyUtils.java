package emulator.graphics2D;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

import java.awt.*;
import java.awt.image.*;

/**
 * Methods to transfer images between SWT and AWT.
 */
public final class CopyUtils {
	private static final PaletteData palleteData = new PaletteData(65280, 16711680, -16777216);

	private static int[] buffer;
	private static BufferedImage lastImg;
	private static ImageData imageData;

	private CopyUtils() {
	}

	public static BufferedImage toAwtForCapture(final ImageData imageData) {
		final PaletteData palette;
		if ((palette = imageData.palette).isDirect) {
			final DirectColorModel directColorModel = new DirectColorModel(imageData.depth, palette.redMask, palette.greenMask, palette.blueMask);
			final BufferedImage bufferedImage;
			final WritableRaster raster = (bufferedImage = new BufferedImage(directColorModel, directColorModel.createCompatibleWritableRaster(imageData.width, imageData.height), 0 != 0, null)).getRaster();
			final int[] array = new int[3];
			for (int i = 0; i < imageData.height; ++i) {
				for (int j = 0; j < imageData.width; ++j) {
					final RGB rgb = palette.getRGB(imageData.getPixel(j, i));
					array[0] = rgb.red;
					array[1] = rgb.green;
					array[2] = rgb.blue;
					raster.setPixels(j, i, 1, 1, array);
				}
			}
			return bufferedImage;
		}
		final RGB[] rgBs;
		final byte[] array2 = new byte[(rgBs = palette.getRGBs()).length];
		final byte[] array3 = new byte[rgBs.length];
		final byte[] array4 = new byte[rgBs.length];
		for (int k = 0; k < rgBs.length; ++k) {
			final RGB rgb2 = rgBs[k];
			array2[k] = (byte) rgb2.red;
			array3[k] = (byte) rgb2.green;
			array4[k] = (byte) rgb2.blue;
		}
		final IndexColorModel indexColorModel = (imageData.transparentPixel != -1) ? new IndexColorModel(imageData.depth, rgBs.length, array2, array3, array4, imageData.transparentPixel) : new IndexColorModel(imageData.depth, rgBs.length, array2, array3, array4);
		final BufferedImage bufferedImage2;
		final WritableRaster raster2 = (bufferedImage2 = new BufferedImage(indexColorModel, indexColorModel.createCompatibleWritableRaster(imageData.width, imageData.height), 0 != 0, null)).getRaster();
		final int[] array5 = {0};
		for (int l = 0; l < imageData.height; ++l) {
			for (int n = 0; n < imageData.width; ++n) {
				array5[0] = imageData.getPixel(n, l);
				raster2.setPixel(n, l, array5);
			}
		}
		return bufferedImage2;
	}

	public static BufferedImage toAwt(final ImageData imageData) {
		final BufferedImage bufferedImage;
		int[] data = ((DataBufferInt) (bufferedImage = new BufferedImage(imageData.width, imageData.height, 2)).getRaster().getDataBuffer()).getData();
		final int n = imageData.width * imageData.height;
		imageData.getPixels(0, 0, n, data, 0);
		if (imageData.alphaData != null) {
			for (int i = n - 1; i >= 0; --i) {
				final RGB rgb = imageData.palette.getRGB(data[i]);
				data[i] = (imageData.alphaData[i] << 24) + ((rgb.red & 0xFF) << 16) + ((rgb.green & 0xFF) << 8) + (rgb.blue & 0xFF);
			}
		} else if (imageData.transparentPixel != -1 && !imageData.palette.isDirect) {
			final RGB[] colors = imageData.palette.colors;
			for (int j = n - 1; j >= 0; --j) {
				int[] array;
				int n2;
				int n3;
				if (data[j] == imageData.transparentPixel) {
					array = data;
					n2 = j;
					n3 = 0;
				} else {
					final RGB rgb2 = colors[data[j] % colors.length];
					array = data;
					n2 = j;
					n3 = -16777216 + ((rgb2.red & 0xFF) << 16) + ((rgb2.green & 0xFF) << 8) + (rgb2.blue & 0xFF);
				}
				array[n2] = n3;
			}
		} else if (!imageData.palette.isDirect) {
			final RGB[] colors2 = imageData.palette.colors;
			for (int k = n - 1; k >= 0; --k) {
				final RGB rgb3 = imageData.palette.getRGB(data[k] % colors2.length);
				data[k] = -16777216 + ((rgb3.red & 0xFF) << 16) + ((rgb3.green & 0xFF) << 8) + (rgb3.blue & 0xFF);
			}
		} else {
			for (int l = n - 1; l >= 0; --l) {
				final RGB rgb4 = imageData.palette.getRGB(data[l]);
				data[l] = -16777216 + ((rgb4.red & 0xFF) << 16) + ((rgb4.green & 0xFF) << 8) + (rgb4.blue & 0xFF);
			}
		}
		data = null;
		System.gc();
		return bufferedImage;
	}

	public static ImageData toSwt(final BufferedImage bufferedImage) {
		if (bufferedImage.getType() == BufferedImage.TYPE_INT_RGB ||
				bufferedImage.getType() == BufferedImage.TYPE_INT_ARGB ||
				bufferedImage.getType() == BufferedImage.TYPE_INT_BGR) {
			return toSwtAsIs(bufferedImage);
		}
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			return toSwtDCM(bufferedImage);
		}
		if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			return toSwtICM(bufferedImage);
		}
		return null;
	}

	private static ImageData toSwtAsIs(final BufferedImage bufferedImage) {
		if (buffer == null || bufferedImage != lastImg) {
			imageData = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), 32, CopyUtils.palleteData);
			buffer = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
			lastImg = bufferedImage;
		}
		ImageData data = CopyUtils.imageData;
		int[] source = CopyUtils.buffer;
		byte[] target = data.data;
		int n = target.length - 1;
		switch (bufferedImage.getType()) {
			case BufferedImage.TYPE_INT_BGR:
				for (int i = source.length - 1; i >= 0; --i) {
					target[n--] = (byte) (0xFF);
					target[n--] = (byte) (source[i] & 0xFF);
					target[n--] = (byte) (source[i] >> 8 & 0xFF);
					target[n--] = (byte) (source[i] >> 16 & 0xFF);
				}
				break;
			case BufferedImage.TYPE_INT_RGB:
				for (int i = source.length - 1; i >= 0; --i) {
					target[n--] = (byte) (0xFF);
					target[n--] = (byte) (source[i] >> 16 & 0xFF);
					target[n--] = (byte) (source[i] >> 8 & 0xFF);
					target[n--] = (byte) (source[i] & 0xFF);
				}
				break;
			case BufferedImage.TYPE_INT_ARGB:
				data.alphaData = new byte[source.length];
				for (int i = source.length - 1; i >= 0; --i) {
					byte alpha = (byte) (source[i] >> 24 & 0xFF);
					target[n--] = alpha;
					data.alphaData[i] = alpha;
					target[n--] = (byte) (source[i] >> 16 & 0xFF);
					target[n--] = (byte) (source[i] >> 8 & 0xFF);
					target[n--] = (byte) (source[i] & 0xFF);
				}
				break;
		}
		return data;
	}

	private static ImageData toSwtDCM(BufferedImage bufferedImage) {
		final DirectColorModel directColorModel = (DirectColorModel) bufferedImage.getColorModel();
		final PaletteData paletteData = new PaletteData(directColorModel.getRedMask(), directColorModel.getGreenMask(), directColorModel.getBlueMask());
		final ImageData imageData = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), directColorModel.getPixelSize(), paletteData);
		final WritableRaster raster = bufferedImage.getRaster();
		int imgW = imageData.width;
		final int[] awtPixels = new int[4 * imgW];
		final int[] swtPixels = new int[imgW];

		for (int i = 0; i < imageData.height; ++i) {
			raster.getPixels(0, i, imgW, 1, awtPixels);

			for (int j = 0; j < imgW; ++j) {
				RGB rgb = new RGB(awtPixels[j * 4 + 0], awtPixels[j * 4 + 1], awtPixels[j * 4 + 2]);
				swtPixels[j] = paletteData.getPixel(rgb);
			}
			imageData.setPixels(0, i, imgW, swtPixels, 0);
			if (directColorModel.hasAlpha()) {
				for (int j = 0; j < imgW; ++j) {
					imageData.setAlpha(j, i, awtPixels[j * 4 + 3]);
				}
			}
		}
		return imageData;
	}

	private static ImageData toSwtICM(BufferedImage bufferedImage) {
		final IndexColorModel indexColorModel;
		final int mapSize;
		final byte[] array2 = new byte[mapSize = (indexColorModel = (IndexColorModel) bufferedImage.getColorModel()).getMapSize()];
		final byte[] array3 = new byte[mapSize];
		final byte[] array4 = new byte[mapSize];
		indexColorModel.getReds(array2);
		indexColorModel.getGreens(array3);
		indexColorModel.getBlues(array4);
		final RGB[] array5 = new RGB[mapSize];
		for (int k = 0; k < array5.length; ++k) {
			array5[k] = new RGB(array2[k] & 0xFF, array3[k] & 0xFF, array4[k] & 0xFF);
		}
		final ImageData imageData2;
		(imageData2 = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), indexColorModel.getPixelSize(), new PaletteData(array5))).transparentPixel = indexColorModel.getTransparentPixel();
		final WritableRaster raster2 = bufferedImage.getRaster();
		final int[] array6 = {0};
		for (int l = 0; l < imageData2.height; ++l) {
			for (int n = 0; n < imageData2.width; ++n) {
				raster2.getPixel(n, l, array6);
				imageData2.setPixel(n, l, array6[0]);
			}
		}
		return imageData2;
	}

	public static void setClipboard(final BufferedImage bufferedImage) {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new a(bufferedImage), null);
	}
}
