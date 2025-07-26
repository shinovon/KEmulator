package com.nttdocomo.ui;

import java.io.*;

public abstract class PalettedImage extends Image {
	protected PalettedImage() {
	}

	public static PalettedImage createPalettedImage(final InputStream inputStream) {
		return null;
	}

	public static PalettedImage createPalettedImage(final byte[] array) {
		return null;
	}

	public void setPalette(final Palette palette) {
	}

	public Palette getPalette() {
		return null;
	}

	public final Graphics getGraphics() {
		return null;
	}
}
