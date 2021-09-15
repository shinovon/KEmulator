package com.nokia.mid.ui;

import javax.microedition.lcdui.Font;

public abstract class FreeSizeFontInvoker {

	public static void setInvoker(FreeSizeFontInvoker invoker) {
	}

	public static Font getFont(int aFace, int aStyle, int aHeight) {
		return Font.getFont(aFace, 0, 0).deriveFont(aStyle, (int) ((float)aHeight * 0.78F));
	}
}
