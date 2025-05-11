package com.nttdocomo.ui;

import javax.microedition.lcdui.game.*;

import com.nttdocomo.ui.maker.*;

import java.awt.*;
import java.awt.image.*;
import javax.microedition.lcdui.*;

import emulator.graphics2D.GraphicsUtils;

public class Graphics {
	public static final int FLIP_NONE = 0;
	public static final int FLIP_HORIZONTAL = 1;
	public static final int FLIP_VERTICAL = 2;
	public static final int FLIP_ROTATE = 3;
	public static final int FLIP_ROTATE_LEFT = 4;
	public static final int FLIP_ROTATE_RIGHT = 5;
	javax.microedition.lcdui.Graphics a;
	boolean b;
	int c;
	int d;
	public static final int BLACK = 0;
	public static final int BLUE = 1;
	public static final int LIME = 2;
	public static final int AQUA = 3;
	public static final int RED = 4;
	public static final int FUCHSIA = 5;
	public static final int YELLOW = 6;
	public static final int WHITE = 7;
	public static final int GRAY = 8;
	public static final int NAVY = 9;
	public static final int GREEN = 10;
	public static final int TEAL = 11;
	public static final int MAROON = 12;
	public static final int PURPLE = 13;
	public static final int OLIVE = 14;
	public static final int SILVER = 15;

	public Graphics(final javax.microedition.lcdui.Graphics a) {
		this.a = a;
		this.b = true;
		this.c = 0;
		this.d = 0;
	}

	public Graphics copy() {
		return new Graphics(this.a._copy());
	}

	public void dispose() {
		this.b = false;
	}

	public synchronized void lock() {
		++this.c;
	}

	public synchronized void unlock(final boolean b) {
		final int c = this.c - 1;
		this.c = c;
		if (c < 0 || b) {
			this.c = 0;
		}
		if (this.c == 0 && Display.getCurrent() instanceof Canvas) {
			final Canvas canvas = (Canvas) Display.getCurrent();
			if (!canvas.b) {
				((GameCanvas) canvas.a).flushGraphics();
			}
		}
	}

	public void setOrigin(final int n, final int n2) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		this.a.translate(n - this.a.getTranslateX(), n2 - this.a.getTranslateY());
	}

	public static int getColorOfRGB(final int n, final int n2, final int n3) {
		return new Color(n, n2, n3).getRGB();
	}

	public static int getColorOfName(final int n) {
		switch (n) {
			case 0: {
				return 0;
			}
			case 1: {
				return 255;
			}
			case 2: {
				return 65280;
			}
			case 3: {
				return 65535;
			}
			case 4: {
				return 16711680;
			}
			case 5: {
				return 16711935;
			}
			case 6: {
				return 16776960;
			}
			case 7: {
				return 16777215;
			}
			case 8: {
				return 8421504;
			}
			case 9: {
				return 128;
			}
			case 10: {
				return 32768;
			}
			case 11: {
				return 32896;
			}
			case 12: {
				return 8388608;
			}
			case 13: {
				return 8388736;
			}
			case 14: {
				return 8421376;
			}
			case 15: {
				return 12632256;
			}
			default: {
				throw new IllegalArgumentException("color name is invalid:" + n);
			}
		}
	}

	public void setColor(final int color) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		this.a.setColor(color);
	}

	public void setFont(final Font font) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		this.a.setFont(font.getImpl());
	}

	public void clearRect(final int n, final int n2, final int n3, final int n4) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		if (n3 < 0 || n4 < 0) {
			throw new IllegalArgumentException("width or height should be positive");
		}
		final int color = this.a.getColor();
		this.a.setColor(0);
		this.a.fillRect(n, n2, n3, n4);
		this.a.setColor(color);
	}

	public void drawLine(final int n, final int n2, final int n3, final int n4) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		this.a.drawLine(n, n2, n3, n4);
	}

	public void drawRect(final int n, final int n2, final int n3, final int n4) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		if (n3 < 0 || n4 < 0) {
			throw new IllegalArgumentException("width or height should be positive");
		}
		this.a.drawRect(n, n2, n3, n4);
	}

	public void fillRect(final int n, final int n2, final int n3, final int n4) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		if (n3 < 0 || n4 < 0) {
			throw new IllegalArgumentException("width or height should be positive");
		}
		this.a.fillRect(n, n2, n3, n4);
	}

	protected int getMidp2Transformation() {
		switch (this.d) {
			case 1: {
				return 2;
			}
			case 2: {
				return 1;
			}
			case 3: {
				return 3;
			}
			case 4: {
				return 6;
			}
			case 5: {
				return 5;
			}
			default: {
				return 0;
			}
		}
	}

	public void drawImage(final Image image, final int[] array) {
		System.out.println("** Graphics.drawImage(Image image, int[] matrix) not implemented yet **");
	}

	public void drawImage(final Image image, final int[] array, final int n, final int n2, final int n3, final int n4) {
		System.out.println("** Graphics.drawImage(Image image, int[] matrix, int sx, int sy, int width, int height) not implemented yet **");
	}

	public void drawImage(final Image image, final int n, final int n2) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		this.a.drawRegion(((ImageImpl) image).getImpl(), 0, 0, image.getWidth(), image.getHeight(), this.getMidp2Transformation(), n, n2, 0);
	}

	public void drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
		this.a.drawRegion(((ImageImpl) image).getImpl(), n3, n4, n5, n6, this.getMidp2Transformation(), n, n2, 0);
	}

	public void drawPolyline(final int[] array, final int[] array2, final int n) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		int[] arrayOfInt3 = new int[n << 1];
		for (int i = 0; i < n; i++) {
			arrayOfInt3[(i << 1)] = array[i];
			arrayOfInt3[((i << 1) + 1)] = array2[i];
		}
		this.a.getImpl().drawPolyline(arrayOfInt3);
	}

	public void drawPolyline(final int[] array, final int[] array2, final int n, final int n2) {
		final int[] array3 = new int[n2];
		final int[] array4 = new int[n2];
		System.arraycopy(array, n, array3, 0, n2);
		System.arraycopy(array2, n, array4, 0, n2);
		this.drawPolyline(array3, array4, n2);
	}

	public void fillPolygon(final int[] array, final int[] array2, final int n) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		int[] arrayOfInt3 = new int[n << 1];
		for (int i = 0; i < n; i++) {
			arrayOfInt3[(i << 1)] = array[i];
			arrayOfInt3[((i << 1) + 1)] = array2[i];
		}
		this.a.getImpl().fillPolygon(arrayOfInt3);
	}

	public void fillPolygon(final int[] array, final int[] array2, final int n, final int n2) {
		final int[] array3 = new int[n2];
		final int[] array4 = new int[n2];
		System.arraycopy(array, n, array3, 0, n2);
		System.arraycopy(array2, n, array4, 0, n2);
		this.fillPolygon(array3, array4, n2);
	}

	public void drawString(final String s, final int n, final int n2) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		this.a.drawString(s, n, n2, 36);
	}

	public void drawChars(final char[] array, final int n, final int n2, final int n3, final int n4) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		this.drawString(new String(array, n3, n4), n, n2);
	}

	public void setPictoColorEnabled(final boolean b) {
	}

	public void setClip(final int n, final int n2, final int n3, final int n4) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		this.a.setClip(n, n2, n3, n4);
	}

	public void clipRect(final int n, final int n2, final int n3, final int n4) {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		this.a.clipRect(n, n2, n3, n4);
	}

	public void clearClip() {
		if (!this.b) {
			throw new UIException(1, "Graphics disposed");
		}
		this.a.setClip(-this.a.getTranslateX(), -this.a.getTranslateY(), this.a._getWidth(), this.a._getHeight());
	}

	public void copyArea(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
		this.a.copyArea(n, n2, n3, n4, n + n5, n2 + n6, 0);
	}

	public void setFlipMode(final int d) {
		this.d = d;
	}

	public void drawScaledImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
		this.a._drawRegion(((ImageImpl) image).getImpl().getImpl(), n5, n6, n7, n8, n3, n4, this.a.getImpl().getTransform().newTransform(n3, n4, this.getMidp2Transformation(), n, n2, 0));
	}

	public void drawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
		this.a.drawArc(n, n2, n3, n4, n5, n6);
	}

	public void fillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
		this.a.fillArc(n, n2, n3, n4, n5, n6);
	}

	public int getPixel(final int n, final int n2) {
		return this.getRGBPixel(n, n2);
	}

	public int getRGBPixel(final int n, final int n2) {
		return this.a.getImage().getRGB(n, n2);
	}

	public int[] getPixels(final int n, final int n2, final int n3, final int n4, final int[] array, final int n5) {
		return this.getRGBPixels(n, n2, n3, n4, array, n5);
	}

	public int[] getRGBPixels(final int n, final int n2, final int n3, final int n4, int[] array, final int n5) {
		if (array == null) {
			array = new int[n3 * n4];
		}
		GraphicsUtils.getImageData(this.a.getImage(), array, n5, n3, n, n2, n3, n4);
		return array;
	}

	public void setPixel(final int n, final int n2) {
		this.a.fillRect(n, n2, 1, 1);
	}

	public void setPixel(final int n, final int n2, final int color) {
		final int color2 = this.a.getColor();
		this.a.setColor(color);
		this.setPixel(n, n2);
		this.a.setColor(color2);
	}

	public void setRGBPixel(final int n, final int n2, final int n3) {
		this.setPixel(n, n2, n3);
	}

	public void setPixels(final int n, final int n2, final int n3, final int n4, final int[] array, final int n5) {
		this.setRGBPixels(n, n2, n3, n4, array, n5);
	}

	public void setRGBPixels(final int n, final int n2, final int n3, final int n4, final int[] array, final int n5) {
		this.a.drawRGB(array, n5, n3, n, n2, n3, n4, false);
	}

	public void drawSpriteSet(final SpriteSet set) {
		System.out.println("** Graphics.drawSpriteSet(SpriteSet sprites) not implemented yet **");
	}

	public void drawSpriteSet(final SpriteSet set, final int n, final int n2) {
		System.out.println("** Graphics.drawSpriteSet(SpriteSet sprites, int offset, int count) not implemented yet **");
	}

	public void drawImageMap(final ImageMap imageMap, final int n, final int n2) {
		System.out.println("** Graphics.drawImageMap(ImageMap map, int x, int y) not implemented yet **");
	}
}
