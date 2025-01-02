package com.jblend.graphics.sprite;

import emulator.Emulator;

public abstract class SpriteCanvas extends com.vodafone.v10.graphics.sprite.SpriteCanvas {

	public SpriteCanvas(int numPalettes, int numPatterns) {
		super(numPalettes, numPatterns);
	}

	public static int getVirtualWidth() {
		return Emulator.getEmulator().getScreen().getWidth();
	}

	public static int getVirtualHeight() {
		return Emulator.getEmulator().getScreen().getHeight();
	}

	public static short createCharacterCommand(int offset, boolean transparent, int rotation,
											   boolean isUpsideDown, boolean isRightsideLeft, int patternNo) {
		return com.vodafone.v10.graphics.sprite.SpriteCanvas.createCharacterCommand(offset, transparent,
				rotation, isUpsideDown, isRightsideLeft, patternNo);
	}
}
