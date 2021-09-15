package com.nokia.mid.impl.isa.ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.midlet.MIDlet;

import com.nokia.mid.ui.lcdui.DisplayStateListener;
import com.nokia.mid.ui.lcdui.VisibilityListener;

public abstract interface DisplayAccess {
	public abstract void setForeground(MIDlet paramMIDlet);

	public abstract DisplayAccess replaceDisplay(MIDlet paramMIDlet);

	public abstract void resetDisplay(MIDlet paramMIDlet);

	public abstract void flushImageToScreen(Displayable paramDisplayable, Image paramImage, int paramInt1,
			int paramInt2, int paramInt3, int paramInt4);

	public abstract Item createMMItem();

	public abstract void showCanvasVideo(Canvas paramCanvas, int paramInt1, boolean paramBoolean, int paramInt2,
			int paramInt3, int paramInt4, int paramInt5);

	public abstract boolean isDisplayActive(Display paramDisplay);

	public abstract void setCurrent(Display paramDisplay, Displayable paramDisplayable, String paramString);

	public abstract void setCurrent(Display paramDisplay, Alert paramAlert, Displayable paramDisplayable,
			String paramString);

	public abstract void setDisplayStateListener(Display paramDisplay, DisplayStateListener paramDisplayStateListener);

	public abstract void setVisibilityListener(Displayable paramDisplayable,
			VisibilityListener paramVisibilityListener);
}
