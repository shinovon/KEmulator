package emulator.debug;

import emulator.Emulator;
import emulator.Settings;
import emulator.graphics2D.IImage;
import ru.woesss.j2me.micro3d.TextureImpl;

import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Image2D;
import java.nio.IntBuffer;

public final class MemoryViewImage extends Image {
	public MemoryViewImage(final IImage image) {
		super(image);
	}

	public static IImage createFromM3GImage(final Image2D img2d) {
		int imgFormat = img2d.getFormat();
		if (imgFormat < Image2D.ALPHA || imgFormat > Image2D.RGBA) return null; //no m3g support

		int imgW = img2d.getWidth(), imgH = img2d.getHeight();
		int imgBPP = img2d.getBitsPerColor();
		boolean imgHasAlpha =
				imgFormat == Image2D.ALPHA ||
						imgFormat == Image2D.LUMINANCE_ALPHA ||
						imgFormat == Image2D.RGBA;

		final byte[] imgPixels;
		final byte[] imgPal;

		if (img2d.isPalettized()) {
			imgPixels = new byte[imgW * imgH];
			img2d.getPixels(imgPixels);

			imgPal = new byte[imgBPP * 256];
			img2d.getPalette(imgPal);
		} else {
			imgPixels = new byte[imgW * imgH * imgBPP];
			img2d.getPixels(imgPixels);
			imgPal = null;
		}

		IImage image = Emulator.getEmulator().newImage(img2d.getWidth(), img2d.getHeight(), imgHasAlpha);
		int[] data = image.getData();

		for (int y = 0; y < imgH; y++) {
			for (int x = 0; x < imgW; x++) {
				int col = 0;

				byte[] tmpPixels;
				int pxIndex;

				if (imgPal != null) {
					tmpPixels = imgPal;
					pxIndex = (imgPixels[x + y * imgW] & 0xff) * imgBPP;
				} else {
					tmpPixels = imgPixels;
					pxIndex = (x + y * imgW) * imgBPP;
				}

				if (imgHasAlpha) {
					col |= (tmpPixels[pxIndex + imgBPP - 1] & 0xff) << 24;
				} else {
					col |= 0xff000000;
				}

				if (imgFormat == Image2D.LUMINANCE || imgFormat == Image2D.LUMINANCE_ALPHA) {
					col |= 0x010101 * (tmpPixels[pxIndex] & 0xff);
				} else if (imgFormat == Image2D.RGB || imgFormat == Image2D.RGBA) {
					col |= ((tmpPixels[pxIndex] & 0xff) << 16) |
							((tmpPixels[pxIndex + 1] & 0xff) << 8) |
							((tmpPixels[pxIndex + 2] & 0xff));
				} else {
					col |= 0xffffff; //Image2D.ALPHA format
				}

				data[x + y * imgW] = col;
			}
		}

		image.setData(data);
		return image;
	}

	public static IImage createFromMicro3DTexture(Object tex) {
		Class texCls = tex.getClass();

		try {
			if (Settings.micro3d == 1) {
				TextureImpl texImpl = (TextureImpl) texCls.getField("impl").get(tex);
				if (texImpl == null) return null;

				IntBuffer texPixels = texImpl.image.getRaster().asIntBuffer();

				int w = texImpl.getWidth(), h = texImpl.getHeight();
				IImage img = Emulator.getEmulator().newImage(w, h, true);
				int[] data = img.getData();

				for (int i = 0; i < w * h; i++) {
					int col = texPixels.get(i);
					data[i] = 0xff000000 |
							((col & 0x00ff0000) >> 16) |
							(col & 0x0000ff00) |
							((col & 0x000000ff) << 16);
				}

				return img;
			} else {
				IImage img = (IImage) texCls.getField("debugImage").get(tex);
				return img;
			}
		} catch (Exception ignored) {}

		return null;
	}
}
