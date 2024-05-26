package com.nokia.mid.ui.lcdui;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

public abstract interface VisibilityListener {
	public abstract void showNotify(Display paramDisplay, Displayable paramDisplayable);

	public abstract void hideNotify(Display paramDisplay, Displayable paramDisplayable);
}
