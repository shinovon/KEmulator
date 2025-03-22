package com.j_phone.amuse;

import com.jblend.ui.SequenceInterface;
import com.vodafone.v10.graphics.sprite.SpriteCanvas;
import emulator.Emulator;
import emulator.graphics2D.IImage;
import emulator.ui.IScreen;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.util.ArrayList;

public abstract class ACanvas
		extends SpriteCanvas
		implements SequenceInterface {

	public ACanvas(int numPalettes, int numPatterns, int fw, int fh) {
		super(numPalettes, numPatterns);
		createFrameBuffer(fw, fh);
	}
	public static int getVirtualWidth() {
		return Emulator.getEmulator().getScreen().getWidth();
	}

	public static int getVirtualHeight() {
		return Emulator.getEmulator().getScreen().getHeight();
	}

	public void scroll(int dx, int dy) {
		// TODO
		scrollX += dx;
		scrollY += dy;
	}

	public void flush(int tx, int ty) {
		drawFrameBuffer(tx, ty);
	}

	public static short createCharacterCommand(int offset, boolean transparent, int rotation,
											   boolean isUpsideDown, boolean isRightsideLeft, int patternNo) {
		return SpriteCanvas.createCharacterCommand(offset, transparent,
				rotation, isUpsideDown, isRightsideLeft, patternNo);
	}

	public final void sequenceStart() {
	}

	public final void sequenceStop() {
	}
}
