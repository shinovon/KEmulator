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

import java.util.ArrayList;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class SpriteCanvas extends Canvas {
	private static ArrayList<CharacterCommand> commands = new ArrayList<>();
	private Image spriteImage;
	private Graphics graphics;
	private int[] palette;
	private byte[] patternData;
	private int[] pixels;
	private Graphics bufferGraphics;

	public boolean _skipCopy;

	public SpriteCanvas(int numPalettes, int numPatterns) {
		super();
		this.palette = new int[numPalettes];
		this.patternData = new byte[numPatterns * 64];
		this.pixels = new int[64];
	}

	public void createFrameBuffer(int fw, int fh) {
		spriteImage = Image.createImage(fw, fh, -1);
		graphics = spriteImage.getGraphics();
	}

	public void disposeFrameBuffer() {
		spriteImage = null;
		graphics = null;
	}

	public void copyArea(int sx, int sy, int fw, int fh, int tx, int ty) {
		Emulator.getEmulator().getScreen().getBackBufferImage().copyImage(graphics.getImpl(), sx, sy, fw, fh, tx, ty);
	}

	public void copyFullScreen(int tx, int ty) {
		copyArea(0, 0, getWidth(), getHeight(), tx, ty);
	}

	public void drawFrameBuffer(int tx, int ty) {
		IScreen screen = Emulator.getEmulator().getScreen();
		IImage screenImage = screen.getScreenImg();
		IImage backBufferImage = screen.getBackBufferImage();

		backBufferImage.cloneImage(screenImage);

		if (bufferGraphics == null)
			bufferGraphics = new Graphics(screenImage, null);
		bufferGraphics.drawImage(spriteImage, tx, ty, 0);

		_skipCopy = true;

		graphics.setColor(0);
		graphics.fillRect(0, 0, spriteImage.getWidth(), spriteImage.getHeight());
	}

	public void setPalette(int index, int palette) {
		this.palette[index] = palette | 0xFF000000;
	}

	public void setPattern(int index, byte[] data) {
		System.arraycopy(data, 0, patternData, index * 64, data.length);
	}

	public static short createCharacterCommand(int offset, boolean transparent, int rotation,
											   boolean isUpsideDown, boolean isRightsideLeft, int patternNo) {
		CharacterCommand command = new CharacterCommand();
		command.offset = offset;
		command.transparent = transparent;
		command.rotation = rotation;
		command.isUpsideDown = isUpsideDown;
		command.isRightsideLeft = isRightsideLeft;
		command.patternNo = patternNo;
		commands.add(command);
		return (short) (commands.size() - 1);
	}

	public void drawSpriteChar(short command, short x, short y) {
		CharacterCommand c = commands.get(command);
		for (int x1 = 0; x1 < 8; x1++) {
			for (int y1 = 0; y1 < 8; y1++) {
				int i = (c.isUpsideDown ? 7 - y1 : y1) * 8 + (c.isRightsideLeft ? 7 - x1 : x1);
				int colorId = patternData[c.patternNo * 64 + i];
				pixels[y1 * 8 + x1] = c.transparent && colorId == 0 ? 0 : palette[colorId];
			}
		}
		graphics.drawRGB(pixels, 0, 8, x, y, 8, 8, true);
	}

	private static class CharacterCommand {
		int offset; // TODO
		boolean transparent;
		int rotation; // TODO
		boolean isUpsideDown;
		boolean isRightsideLeft;
		int patternNo;
	}
}