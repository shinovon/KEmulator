package com.nokia.mid.ui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class DirectUtils {
	public static Image createImage(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
		return Image.createImage(paramArrayOfByte, paramInt1, paramInt2);
	}

	public static Image createImage(int paramInt1, int paramInt2, int paramInt3) {
		return Image.createImage(paramInt1, paramInt2);
	}

	public static DirectGraphics getDirectGraphics(Graphics paramGraphics) {
		return new a(paramGraphics);
	}

	public static Font getFont(int face, int style, int height) {
		return FreeSizeFontInvoker.getFont(face, style, height);
	}

	public static Font getFont(int s) {
		return Font.getDefaultFont();
	}
}