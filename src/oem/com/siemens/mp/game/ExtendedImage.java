/*
 *  Siemens API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */

package com.siemens.mp.game;

import emulator.Emulator;
import emulator.graphics2D.awt.ImageAWT;

import javax.microedition.lcdui.*;

@SuppressWarnings("unused")
public class ExtendedImage extends com.siemens.mp.misc.NativeMem {
	private final boolean hasAlpha;
	private Image image;

	public ExtendedImage(Image image) {
		if (image == null || image.getWidth() % 8 != 0) {
			throw new IllegalArgumentException("ExtendedImage: width is not divisible by 8");
		}
		this.image = image;
		hasAlpha = image._getImpl().isTransparent();
	}

	public void blitToScreen(int x, int y) {
		Displayable current = Display.getDisplay(null).getCurrent();
		if (current instanceof Canvas) {
			Graphics g = new Graphics(Emulator.getEmulator().getScreen().getBackBufferImage(), Emulator.getEmulator().getScreen().getXRayScreenImage());
			g.drawImage(image, x, y, 0);
			Emulator.getEventQueue().gameGraphicsFlush();
		}
	}

	public void clear(byte color) {
		image._getImpl().fill(color == 0 ? 0xFFFFFFFF : 0xFF000000);
	}

	public Image getImage() {
		return image;
	}

	public int getPixel(int x, int y) {
		final int pixel = image._getImpl().getRGB(x, y);
		if (hasAlpha) {
			if ((pixel & 0xFF000000) != 0xFF000000) return 0;
			return ((pixel & 0xFFFFFF) == 0xFFFFFF) ? 1 : 2;
		}
		return ((pixel & 0xFFFFFF) == 0xFFFFFF) ? 0 : 1;
	}

	public void getPixelBytes(byte[] pixels, int x, int y, int width, int height) {
		int[] colors = new int[width * height];
		image.getRGB(colors, 0, width, x, y, width, height);
		if (hasAlpha) {
			final int dataLen = colors.length / 4;
			for (int i = 0, k = 0; i < dataLen; i++) {
				int data = 0;
				for (int j = 0; j < 4; j++) {
					data <<= 2;
					int color = colors[k++];
					if ((color & 0xFF000000) != 0xFF000000) continue;
					if ((color & 0xFFFFFF) == 0xFFFFFF) data |= 1;
					else data |= 2;
				}
				pixels[i] = (byte) data;
			}
		} else {
			final int dataLen = colors.length / 8;
			for (int i = 0, k = 0; i < dataLen; i++) {
				int data = 0;
				for (int j = 0; j < 8; j++) {
					data <<= 1;
					if ((colors[k++] & 0xFFFFFF) != 0xFFFFFF) {
						data |= 1;
					}
				}
				pixels[i] = (byte) data;
			}
		}
	}

	public void setPixel(int x, int y, byte color) {
		if (!hasAlpha) {
			image._getImpl().setRGB(x, y, color == 1 ? 0 : -1);
			return;
		}
		if (color == 0) {
			image._getImpl().setRGB(x, y, 0);
		} else {
			image._getImpl().setRGB(x, y, color == 1 ? -1 : 0);
		}
	}

	public void setPixels(byte[] pixels, int x, int y, int width, int height) {
		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();
		int right = x + width;
		int bottom = y + height;
		if (x >= imgWidth || right <= 0 || y >= imgHeight || bottom <= 0) {
			return;
		}
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (right > imgWidth) right = imgWidth;
		if (bottom > imgHeight) bottom = imgHeight;
		width = right - x;
		height = bottom - y;
		int[] colors = new int[width * height];
		if (hasAlpha) {
			final int dataLen = Math.min(pixels.length, colors.length / 4);
			for (int i = 0, k = 0; i < dataLen; i++) {
				final int data = pixels[i];
				for (int j = 3; j >= 0; j--) {
					int color = (data >> j) & 3;
					if (color == 0) {
						colors[k++] = 0;
					} else {
						colors[k++] = color == 1 ? -1 : 0;
					}
				}
			}
		} else {
			final int dataLen = Math.min(pixels.length, colors.length / 8);
			for (int i = 0, k = 0; i < dataLen; i++) {
				final int data = pixels[i];
				for (int j = 7; j >= 0; j--) {
					final int color = (data >> j) & 1;
					colors[k++] = color == 1 ? 0 : -1;
				}
			}
		}
		((ImageAWT) image._getImpl()).getBufferedImage().setRGB(x, y, width, height, colors, 0, width);
	}
}