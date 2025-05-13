package com.nttdocomo.ui;

import com.nttdocomo.ui.maker.*;

public abstract class Image {
	protected Image() {
	}

	public static Image createImage(final int n, final int n2) {
		return (Image) new ImageImpl(javax.microedition.lcdui.Image.createImage(n, n2));
	}

	public static Image createImage(final int n, final int n2, final int[] array, final int n3) {
		final Image image = createImage(n, n2);
		image.getGraphics().setRGBPixels(0, 0, n, n2, array, n3);
		return image;
	}

	public int getWidth() {
		return 0;
	}

	public int getHeight() {
		return 0;
	}

	public abstract void dispose();

	public Graphics getGraphics() {
		return null;
	}

	public void setAlpha(int i) {

	}
}
