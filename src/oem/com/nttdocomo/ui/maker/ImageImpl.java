package com.nttdocomo.ui.maker;

import com.nttdocomo.ui.*;

public class ImageImpl extends Image {
	protected javax.microedition.lcdui.Image impl;

	public ImageImpl(final javax.microedition.lcdui.Image impl) {
		this.impl = impl;
	}

	public javax.microedition.lcdui.Image getImpl() {
		return this.impl;
	}

	public void dispose() {
		this.impl = null;
	}

	public int getHeight() {
		return this.impl.getHeight();
	}

	public int getWidth() {
		return this.impl.getWidth();
	}

	public Graphics getGraphics() {
		return new Graphics(this.impl.getGraphics());
	}
}
