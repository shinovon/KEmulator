package com.nokia.mid.ui.lcdui;

import emulator.Emulator;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

public final class LCDUIUtils {

	private LCDUIUtils() {
		throw new IllegalStateException();
	}

	public static void setDisplayStateListener(Display paramDisplay, DisplayStateListener paramDisplayStateListener) {
	}

	public static void setVisibilityListener(Displayable paramDisplayable, VisibilityListener paramVisibilityListener) {
	}

	public static boolean isDisplayActive(Display param) {
		if (param == null) {
			throw new NullPointerException();
		}
		return Emulator.getCurrentDisplay().equals(param);
	}

	public static void setCurrent(Display paramDisplay, Displayable paramDisplayable) {
		if ((paramDisplay == null) || (paramDisplayable != null)) {
			throw new NullPointerException();
		}
		paramDisplay.setCurrent(paramDisplayable);
	}


	public static void setCurrentNoWaitForForeground(Display paramDisplay, Displayable paramDisplayable) {
		if ((paramDisplay == null) || (paramDisplayable != null)) {
			throw new NullPointerException();
		}
		paramDisplay.setCurrent(paramDisplayable);
	}


	public static void setCurrent(Display paramDisplay, Displayable paramDisplayable, String paramString) {
		if ((paramDisplay == null) || ((paramDisplayable != null) && (paramString == null))) {
			throw new NullPointerException();
		}
		paramDisplay.setCurrent(paramDisplayable);
	}

	public static void setCurrent(Display paramDisplay, Alert paramAlert, Displayable paramDisplayable,
								  String paramString) {
		if ((paramDisplay == null) || (paramAlert == null) || (paramDisplayable == null) || (paramString == null)) {
			throw new NullPointerException();
		}
		paramDisplay.setCurrent(paramAlert, paramDisplayable);
	}
}
