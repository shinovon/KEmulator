package com.nokia.mid.ui;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import emulator.Emulator;

public abstract class DirectGraphicsInvoker {
	private static DirectGraphicsInvoker sInvoker;

	public static void setInvoker(DirectGraphicsInvoker aInvoker) {
		sInvoker = aInvoker;
	}

	public static DirectGraphics getDirectGraphics(Graphics aGraphics) {
		return DirectUtils.getDirectGraphics(aGraphics);
	}

	static void setColorValues(DirectGraphics aDirectGraphics, int aColor, Image aImage) {

	}

	static Display getDisplay() {
		return Emulator.getCurrentDisplay();
	}
}
