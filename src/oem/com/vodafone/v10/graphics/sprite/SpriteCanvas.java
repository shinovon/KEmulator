/*
 * Copyright 2020 Nikita Shakarun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vodafone.v10.graphics.sprite;

import emulator.Emulator;
import emulator.graphics2D.IImage;
import emulator.ui.IScreen;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class SpriteCanvas extends Canvas {
	public static Image _virtualImage;
	public static Graphics _virtualGraphics;

	public Image _frameImage;
	protected Graphics frameGraphics;

	private int[] palette;
	private byte[] patternData;
	private int[] pixels;

	private Graphics screenGraphics;
	public boolean _skipCopy;
	protected int scrollX, scrollY;

	public SpriteCanvas(int numPalettes, int numPatterns) {
		super();
		this.palette = new int[numPalettes];
		this.patternData = new byte[numPatterns * 64];
		this.pixels = new int[64];

		if (_virtualImage == null) {
			_virtualImage = Image.createImage(getVirtualWidth(), getVirtualHeight(), 0);
			_virtualGraphics = _virtualImage.getGraphics();
		}
	}

	public void createFrameBuffer(int fw, int fh) {
		if (fw == 0 || fh == 0) {
			fw = getVirtualWidth();
			fh = getVirtualHeight();
		}
		_frameImage = Image.createImage(fw, fh, 0);
		frameGraphics = _frameImage.getGraphics();
	}

	public void disposeFrameBuffer() {
		_frameImage = null;
		frameGraphics = null;
	}

	public void copyArea(int sx, int sy, int fw, int fh, int tx, int ty) {
		_virtualImage.getImpl().copyImage(frameGraphics.getImpl(), sx, sy, fw, fh, tx, ty);
		_skipCopy = true;
	}

	public void copyFullScreen(int tx, int ty) {
		copyArea(0, 0, getWidth(), getHeight(), tx, ty);
	}

	public void drawFrameBuffer(int tx, int ty) {
		IScreen screen = Emulator.getEmulator().getScreen();
		IImage screenImage = screen.getScreenImg();

		if (screenGraphics == null) {
			screenGraphics = new Graphics(screenImage, null);
		} else {
			screenGraphics._reset(screenImage, null);
		}

		screenGraphics.drawImage(_frameImage, tx, ty, 0);

		frameGraphics.setColor(0);
		frameGraphics.getImage().setAlpha(0, 0, _frameImage.getWidth(), _frameImage.getHeight(), 0);
		screen.repaint();
	}

	public void setPalette(int index, int palette) {
		this.palette[index] = palette | 0xFF000000;
	}

	public void setPattern(int index, byte[] data) {
		System.arraycopy(data, 0, patternData, index * 64, data.length);
	}

	public static short createCharacterCommand(int offset, boolean transparent, int rotation,
											   boolean isUpsideDown, boolean isRightsideLeft, int patternNo) {
		int i = (offset & 0x7) << 13;

		if (transparent) {
			i |= 0x1000;
		}

		i |= (rotation & 0x3) << 10;

		if (isUpsideDown) {
			i |= 0x200;
		}
		if (isRightsideLeft) {
			i |= 0x100;
		}
		i |= patternNo & 0xFF;

		return (short) i;
	}

	private synchronized void drawChar(short command, boolean bg) {
		int n = command & 0xFFFF;
		int offset = (n >> 13) & 0x7;
		int rotation = (n >> 10) & 0x3;
		boolean transparent = (n & 0x1000) != 0;
		boolean isUpsideDown = (n & 0x200) != 0;
		boolean isRightsideLeft = (n & 0x100) != 0;
		int patternNo = n & 0xff;

		for (int x1 = 0; x1 < 8; x1++) {
			for (int y1 = 0; y1 < 8; y1++) {
				int i = (isUpsideDown ? 7 - y1 : y1) * 8 + (isRightsideLeft ? 7 - x1 : x1);
				int colorId = patternData[patternNo * 64 + i] & 0xFF;
				if (rotation == 1) { // 90
					i = (7 - x1) * 8 + y1;
				} else if (rotation == 2) { // 180
					i = (7 - y1) * 8 + (7 - x1);
				} else if (rotation == 3) { // 270
					i = x1 * 8 + (7 - y1);
				} else { // 0
					i = y1 * 8 + x1;
				}
				pixels[i] = transparent && colorId == 0 ? (bg ? 0xFF000000 : 0) : (palette[colorId] + (offset * 32));
			}
		}
	}

	public void drawSpriteChar(short command, short x, short y) {
		// draw on framebuffer
		drawChar(command, false);
		frameGraphics.drawRGB(pixels, 0, 8, x, y, 8, 8, true);
	}

	public void drawBackground(short command, short x, short y) {
		// draw on virtual screen
		drawChar(command, true);
		_virtualGraphics.drawRGB(pixels, 0, 8, x * 8, y * 8, 8, 8, true);
	}

	public static int getVirtualWidth() {
		return Emulator.getEmulator().getScreen().getWidth();
	}

	public static int getVirtualHeight() {
		return Emulator.getEmulator().getScreen().getHeight();
	}
}