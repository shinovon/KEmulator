package com.j_phone.system;

import javax.microedition.lcdui.Image;

public class SubDisplay {
	private static SubDisplay instance;

	public static SubDisplay getInstance()
			throws RuntimeException {
		if (instance == null) {
			return instance = new SubDisplay(0);
		}
		return instance;
	}

	public int getWidth() {
		return 0;
	}

	public int getHeight() {
		return 0;
	}

	public int getFullWidth() {
		return 0;
	}

	public int getFullHeight() {
		return 0;
	}

	public boolean isColor() {
		return false;
	}

	public int numColors() {
		return 1;
	}

	public void setViewPort(int paramInt1, int paramInt2)
			throws IllegalArgumentException, RuntimeException {
	}

	public void releaseViewPort()
			throws RuntimeException {
	}

	public void setWallPaperImage(Image paramImage)
			throws RuntimeException {
	}

	private SubDisplay(int paramInt)
			throws RuntimeException {
	}
}
