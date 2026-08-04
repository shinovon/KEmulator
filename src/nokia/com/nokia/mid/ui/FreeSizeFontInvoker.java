package com.nokia.mid.ui;

import javax.microedition.lcdui.Font;

public abstract class FreeSizeFontInvoker {

	public static void setInvoker(FreeSizeFontInvoker invoker) {
	}

	public static Font getFont(int aFace, int aStyle, int aHeight) {
		return Font._getNokiaUiFont(aFace, aStyle, aHeight);
	}
}
