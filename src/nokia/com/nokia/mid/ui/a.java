package com.nokia.mid.ui;

import emulator.Emulator;
import emulator.Settings;
import emulator.debug.Profiler;
import emulator.graphics2D.IGraphics2D;
import emulator.graphics2D.IImage;
import emulator.graphics2D.ITransform;
import emulator.graphics2D.awt.TransformAWT;
import emulator.graphics2D.GraphicsUtils;
import emulator.graphics2D.swt.TransformSWT;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import java.awt.geom.AffineTransform;

final class a
		implements DirectGraphics {
	Graphics gc;
	IGraphics2D impl;
	static final int[][] jdField_a_of_type_Array2dOfInt = {{0, 24756}, {16384, 8372}, {8192, 16564}, {180, 24576}, {16474, 8462}, {270, 24666}, {90, 24846}, {8282, 16654}};
	private int[] tempPixels;

	public a(Graphics paramGraphics) {
		this.gc = paramGraphics;
		this.impl = paramGraphics.getImpl();
	}

	private static int a(int paramInt) {
		for (int i = 0; i < jdField_a_of_type_Array2dOfInt.length; i++) {
			for (int j = 0; j < jdField_a_of_type_Array2dOfInt[i].length; j++) {
				if (jdField_a_of_type_Array2dOfInt[i][j] == paramInt) {
					return i;
				}
			}
		}
		Emulator.getEmulator().getLogStream().println("*** nokiaManip2MIDP2Manip: Invalid Nokia Manipulation: " + paramInt);
		return 0;
	}

	public final void drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		IImage localIImage;
		int i = (localIImage = paramImage._getImpl()).getWidth();
		int j = localIImage.getHeight();
		int k = a(paramInt4);
		this.gc.drawRegion(paramImage, 0, 0, i, j, k, paramInt1, paramInt2, paramInt3);
		Profiler.nokiaDrawImageCallCount += 1;
		Profiler.nokiaDrawImagePixelCount += i * j;
		Profiler.drawRegionCallCount -= 1;
		Profiler.drawRegionPixelCount -= i * j;
	}

	public final void drawPixels(short[] paramArrayOfShort, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
		IImage localIImage = GraphicsUtils.setImageData(paramArrayOfShort, paramBoolean, paramInt1, paramInt2, paramInt5, paramInt6);
		ITransform localITransform = this.impl.getTransform().newTransform(paramInt5, paramInt6, a(paramInt7), paramInt3, paramInt4, 0);
		this.gc.drawRegion(localIImage, 0, 0, paramInt5, paramInt6, localITransform, 65280);
		Profiler.nokiaDrawPixelCallCount += 1;
		Profiler.nokiaDrawPixelPixelCount += paramInt5 * paramInt6;
	}

	public void drawPixels(byte[] pix, byte[] alpha, int off, int scanlen, int x, int y, int width, int height, int manipulation, int format) {
		if (pix == null) {
			throw new NullPointerException();
		}
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}
		if (width == 0 || height == 0) {
			return;
		}

		int transform = getTransformation(manipulation);
		if(tempPixels == null || tempPixels.length < width * height)
			tempPixels = new int[height * width];

		switch (format) {
			case TYPE_BYTE_1_GRAY: {
				int b = 7 - off % 8;
				for (int yj = 0; yj < height; yj++) {
					int line = off + yj * scanlen;
					int ypos = yj * width;
					for (int xj = 0; xj < width; xj++) {
						tempPixels[ypos + xj] = doAlpha(pix, alpha, (line + xj) / 8, b);
						b--;
						if (b < 0) b = 7;
					}
					b = b - (scanlen - width) % 8;
					if (b < 0) b = 8 + b;
				}
				break;
			}
			case TYPE_BYTE_1_GRAY_VERTICAL: {
				int ods = off / scanlen;
				int oms = off % scanlen;
				int b = ods % 8;
				for (int yj = 0; yj < height; yj++) {
					int ypos = yj * width;
					int tmp = (ods + yj) / 8 * scanlen + oms;
					for (int xj = 0; xj < width; xj++) {
						tempPixels[ypos + xj] = doAlpha(pix, alpha, tmp + xj, b);
					}
					b++;
					if (b > 7) b = 0;
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Illegal format: " + format);
		}

		Image image = Image.createRGBImage(tempPixels, width, height, true);
		gc.drawRegion(image, 0, 0, width, height, transform, x, y, 0);
	}

	public final void drawPixels(int[] paramArrayOfInt, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
		IImage localIImage = GraphicsUtils.setImageData(paramArrayOfInt, paramBoolean, paramInt1, paramInt2, paramInt5, paramInt6);
		ITransform localITransform = this.impl.getTransform().newTransform(paramInt5, paramInt6, a(paramInt7), paramInt3, paramInt4, 0);
		this.gc.drawRegion(localIImage, 0, 0, paramInt5, paramInt6, localITransform, 65280);
		Profiler.nokiaDrawPixelCallCount += 1;
		Profiler.nokiaDrawPixelPixelCount += paramInt5 * paramInt6;
	}

	public final void drawPolygon(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int paramInt3, int paramInt4) {
		int[] arrayOfInt1 = new int[paramInt3];
		int[] arrayOfInt2 = new int[paramInt3];
		System.arraycopy(paramArrayOfInt1, paramInt1, arrayOfInt1, 0, paramInt3);
		System.arraycopy(paramArrayOfInt2, paramInt2, arrayOfInt2, 0, paramInt3);
		int[] arrayOfInt3 = new int[paramInt3 << 1];
		for (int i = 0; i < paramInt3; i++) {
			arrayOfInt3[(i << 1)] = arrayOfInt1[i];
			arrayOfInt3[((i << 1) + 1)] = arrayOfInt2[i];
		}
		int i = this.impl.getColor();
		this.impl.setColor(paramInt4, true);
		this.impl.drawPolygon(arrayOfInt3);
		this.impl.setColor(i, true);
	}

	public final void drawTriangle(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
		drawPolygon(new int[]{paramInt1, paramInt3, paramInt5}, 0, new int[]{paramInt2, paramInt4, paramInt6}, 0, 3, paramInt7);
	}

	public final void fillPolygon(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int paramInt3, int paramInt4) {
		int[] arrayOfInt1 = new int[paramInt3];
		int[] arrayOfInt2 = new int[paramInt3];
		System.arraycopy(paramArrayOfInt1, paramInt1, arrayOfInt1, 0, paramInt3);
		System.arraycopy(paramArrayOfInt2, paramInt2, arrayOfInt2, 0, paramInt3);
		int[] arrayOfInt3 = new int[paramInt3 << 1];
		for (int i = 0; i < paramInt3; i++) {
			arrayOfInt3[(i << 1)] = arrayOfInt1[i];
			arrayOfInt3[((i << 1) + 1)] = arrayOfInt2[i];
		}
		int i = this.impl.getColor();
		this.impl.setColor(paramInt4, true);
		this.impl.fillPolygon(arrayOfInt3);
		this.impl.setColor(i, true);
	}

	public final void fillTriangle(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
		fillPolygon(new int[]{paramInt1, paramInt3, paramInt5}, 0, new int[]{paramInt2, paramInt4, paramInt6}, 0, 3, paramInt7);
	}

	public final int getAlphaComponent() {
		return this.impl.getColor() >> 24 & 0xFF;
	}

	public final int getNativePixelFormat() {
		return 4444;
	}

	public final void getPixels(int[] pixels, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
		GraphicsUtils.getImageData(this.gc.getImage(), pixels, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
	}

	public final void getPixels(short[] pixels, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
		GraphicsUtils.getImageData(this.gc.getImage(), pixels, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
	}

	public final void getPixels(byte[] pixels, byte[] transparencyMask, int offset, int scanLength, int x, int y, int width, int height, int format) {
		if (pixels == null) {
			throw new NullPointerException();
		}
		if (x < 0 || y < 0 || width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}
		if (width == 0 || height == 0) return;
		switch (format) {
			case TYPE_BYTE_1_GRAY:
				final int dataLen = height * scanLength - (scanLength - width);
				int minBytesLen = (dataLen + 7) / 8;
				if (minBytesLen > pixels.length - offset)
					throw new ArrayIndexOutOfBoundsException();
				if (transparencyMask != null && minBytesLen > transparencyMask.length - offset)
					throw new IllegalArgumentException();
				if(tempPixels == null || tempPixels.length < width * height)
					tempPixels = new int[width * height];
				gc.getImage().getRGB(tempPixels, 0, width, x, y, width, height);
				for (int i = offset, k = 0, w = 0, d = 0; d < dataLen; i++) {
					for (int j = 7; j >= 0 && d < dataLen; j--, w++, d++) {
						if (w == scanLength) w = 0;
						if (w >= width) {
							continue;
						}
						int color = tempPixels[k++];
						int alpha = color >>> 31;
						int gray = (((color & 0x80) >> 7) + ((color & 0x8000) >> 15) + ((color & 0x800000) >> 23)) >> 1;
						if (gray == 0 && alpha == 1) pixels[i] |= 1 << j;
						else pixels[i] &= ~(1 << j);
						if (transparencyMask != null) {
							if (alpha == 1) transparencyMask[i] |= 1 << j;
							else transparencyMask[i] &= ~(1 << j);
						}
					}
				}
				break;
			case TYPE_BYTE_1_GRAY_VERTICAL:
			case TYPE_BYTE_2_GRAY:
			case TYPE_BYTE_4_GRAY:
			case TYPE_BYTE_8_GRAY:
			case TYPE_BYTE_332_RGB:
				System.err.println("getPixels(byte[] pixels, byte[] transparencyMask, int offset, int scanLength, int x, int y, int width, int height, int format)");
			default:
				throw new IllegalArgumentException();
		}
	}

	public final void setARGBColor(int paramInt) {
		this.impl.setColor(paramInt, true);
	}

	private static Image manipulateImage(Image image, int manipulation) {
		final int HV = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.FLIP_VERTICAL;
		final int H90 = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.ROTATE_90;
		switch (manipulation) {
			case DirectGraphics.FLIP_HORIZONTAL:
				return transformImage(image, Sprite.TRANS_MIRROR);
			case DirectGraphics.FLIP_VERTICAL:
				return transformImage(image, Sprite.TRANS_MIRROR_ROT180);
			case DirectGraphics.ROTATE_90:
				return transformImage(image, Sprite.TRANS_ROT270);
			case DirectGraphics.ROTATE_180:
				return transformImage(image, Sprite.TRANS_ROT180);
			case DirectGraphics.ROTATE_270:
				return transformImage(image, Sprite.TRANS_ROT90);
			case HV:
				return transformImage(image, Sprite.TRANS_ROT180);
			case H90:
				return transformImage(transformImage(image, Sprite.TRANS_MIRROR), Sprite.TRANS_ROT270);
			case 0: /* No Manipulation */
				break;
			default:
				System.out.println("manipulateImage " + manipulation + " not defined");
		}
		return image;
	}
	public static Image transformImage(Image image, int transform) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		int out_width = width;
		int out_height = height;

		AffineTransform af = new AffineTransform();

		switch (transform) {
			case Sprite.TRANS_NONE:
				break;

			case Sprite.TRANS_ROT90:
				af.translate(height, 0);
				af.rotate(Math.PI / 2);
				out_width = height;
				out_height = width;
				break;

			case Sprite.TRANS_ROT180:
				af.translate(width, height);
				af.rotate(Math.PI);
				break;

			case Sprite.TRANS_ROT270:
				af.translate(0, width);
				af.rotate(Math.PI * 3 / 2);
				out_width = height;
				out_height = width;
				break;

			case Sprite.TRANS_MIRROR:
				af.translate(width, 0);
				af.scale(-1, 1);
				break;

			case Sprite.TRANS_MIRROR_ROT90:
				af.translate(height, 0);
				af.rotate(Math.PI / 2);
				af.translate(width, 0);
				af.scale(-1, 1);
				out_width = height;
				out_height = width;
				break;

			case Sprite.TRANS_MIRROR_ROT180:
				af.translate(width, 0);
				af.scale(-1, 1);
				af.translate(width, height);
				af.rotate(Math.PI);
				break;

			case Sprite.TRANS_MIRROR_ROT270:
				af.translate(0, width);
				af.rotate(Math.PI * 3 / 2);
				af.translate(width, 0);
				af.scale(-1, 1);
				out_width = height;
				out_height = width;
				break;
		}

		Image transimage = Image.createImage(out_width, out_height, 0);
		Graphics gc = transimage.getGraphics();
		if (Settings.g2d == 0) {
			gc.getImpl().setTransform(new TransformSWT(af));
		} else {
			gc.getImpl().setTransform(new TransformAWT(af));
		}
		gc.drawImage(image, 0, 0, 0);
		return transimage;
	}

	private static int getTransformation(int manipulation) {
		// manipulations are C-CW and sprite rotations are CW
		int ret = -1;
		int rotation = manipulation & 0x0FFF;
		if ((manipulation & FLIP_HORIZONTAL) != 0) {
			if ((manipulation & FLIP_VERTICAL) != 0) {
				// horiz and vertical flipping
				switch (rotation) {
					case 0:
						ret = Sprite.TRANS_ROT180;
						break;
					case ROTATE_90:
						ret = Sprite.TRANS_ROT90;
						break;
					case ROTATE_180:
						ret = Sprite.TRANS_NONE;
						break;
					case ROTATE_270:
						ret = Sprite.TRANS_ROT270;
						break;
					default:
				}
			} else {
				// horizontal flipping
				switch (rotation) {
					case 0:
						ret = Sprite.TRANS_MIRROR;
						break;
					case ROTATE_90:
						ret = Sprite.TRANS_MIRROR_ROT90;
						break;
					case ROTATE_180:
						ret = Sprite.TRANS_MIRROR_ROT180;
						break;
					case ROTATE_270:
						ret = Sprite.TRANS_MIRROR_ROT270;
						break;
					default:
				}
			}
		} else {
			if ((manipulation & FLIP_VERTICAL) != 0) {
				// vertical flipping
				switch (rotation) {
					case 0:
						ret = Sprite.TRANS_MIRROR_ROT180;
						break;
					case ROTATE_90:
						ret = Sprite.TRANS_MIRROR_ROT270;
						break;
					case ROTATE_180:
						ret = Sprite.TRANS_MIRROR;
						break;
					case ROTATE_270:
						ret = Sprite.TRANS_MIRROR_ROT90;
						break;
					default:
				}
			} else {
				// no flipping
				switch (rotation) {
					case 0:
						ret = Sprite.TRANS_NONE;
						break;
					case ROTATE_90:
						ret = Sprite.TRANS_ROT270;
						break;
					case ROTATE_180:
						ret = Sprite.TRANS_ROT180;
						break;
					case ROTATE_270:
						ret = Sprite.TRANS_ROT90;
						break;
					default:
				}
			}
		}
		return ret;
	}

	private static int doAlpha(byte[] pix, byte[] alpha, int pos, int shift) {
		int p;
		int a;
		if (isBitSet(pix[pos], shift))
			p = 0;
		else
			p = 0x00FFFFFF;
		if (alpha == null || isBitSet(alpha[pos], shift))
			a = 0xFF000000;
		else
			a = 0;
		return p | a;
	}

	private static boolean isBitSet(byte b, int pos) {
		return ((b & (byte) (1 << pos)) != 0);
	}
}
