package com.j_phone.amuse;

import com.jblend.ui.SequenceInterface;
import emulator.Emulator;
import emulator.graphics2D.IImage;
import emulator.ui.IScreen;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.util.ArrayList;

public abstract class ACanvas
		extends Canvas
		implements SequenceInterface {
	private static ArrayList<ACanvas.CharacterCommand> commands = new ArrayList<>();
	private Image spriteImage;
	private Graphics graphics;
	private int[] palette;
	private byte[] patternData;
	private int[] pixels;

	private Graphics bufferGraphics;
	private Graphics screenGraphics;

	public boolean _skipCopy;

	public ACanvas(int numPalettes, int numPatterns, int fw, int fh) {
		super();
		this.palette = new int[numPalettes];
		this.patternData = new byte[numPatterns * 64];
		this.pixels = new int[64];
		spriteImage = Image.createImage(fw, fh, -1);
		graphics = spriteImage.getGraphics();
	}

	public static int getVirtualWidth() {
		return Emulator.getEmulator().getScreen().getWidth();
	}

	public static int getVirtualHeight() {
		return Emulator.getEmulator().getScreen().getHeight();
	}

	public void setPalette(int index, int palette) {
		this.palette[index] = palette | 0xFF000000;
	}

	public void setPattern(int index, byte[] data) {
		System.arraycopy(data, 0, patternData, index * 64, data.length);
	}

	public void drawBackground(short command, short x, short y) {
		ACanvas.CharacterCommand c = commands.get(command);
		for (int x1 = 0; x1 < 8; x1++) {
			for (int y1 = 0; y1 < 8; y1++) {
				int i = (c.isUpsideDown ? 7 - y1 : y1) * 8 + (c.isRightsideLeft ? 7 - x1 : x1);
				int colorId = patternData[c.patternNo * 64 + i];
				pixels[y1 * 8 + x1] = palette[colorId];
			}
		}
		if (screenGraphics == null)
			screenGraphics = new Graphics(Emulator.getEmulator().getScreen().getBackBufferImage(), null);
		screenGraphics.drawRGB(pixels, 0, 8, x, y, 8, 8, true);
	}

	public void copyArea(int sx, int sy, int fw, int fh, int tx, int ty) {
		Emulator.getEmulator().getScreen().getBackBufferImage().copyImage(graphics.getImpl(), sx, sy, fw, fh, tx, ty);
	}

	public void scroll(int dx, int dy) {
		// TODO
	}

	public void flush(int tx, int ty) {
		IScreen screen = Emulator.getEmulator().getScreen();
		IImage screenImage = screen.getScreenImg();

		screen.getBackBufferImage().cloneImage(screenImage);

		if (bufferGraphics == null)
			bufferGraphics = new Graphics(screenImage, null);
		bufferGraphics.drawImage(spriteImage, tx, ty, 0);

		_skipCopy = true;

		graphics.setColor(0);
		graphics.fillRect(0, 0, spriteImage.getWidth(), spriteImage.getHeight());
	}

	public static short createCharacterCommand(int offset, boolean transparent, int rotation,
											   boolean isUpsideDown, boolean isRightsideLeft, int patternNo) {
		ACanvas.CharacterCommand command = new ACanvas.CharacterCommand();
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
		ACanvas.CharacterCommand c = commands.get(command);
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

	public final void sequenceStart() {}

	public final void sequenceStop() {}
}
