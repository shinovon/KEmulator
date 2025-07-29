package com.nttdocomo.ui;

import emulator.*;

public class Display {
	public static final int KEY_PRESSED_EVENT = 0;
	public static final int KEY_RELEASED_EVENT = 1;
	public static final int RESUME_VM_EVENT = 4;
	public static final int RESET_VM_EVENT = 5;
	public static final int UPDATE_VM_EVENT = 6;
	public static final int TIMER_EXPIRED_EVENT = 7;
	public static final int MEDIA_EVENT = 8;
	public static final int KEY_0 = 0;
	public static final int KEY_1 = 1;
	public static final int KEY_2 = 2;
	public static final int KEY_3 = 3;
	public static final int KEY_4 = 4;
	public static final int KEY_5 = 5;
	public static final int KEY_6 = 6;
	public static final int KEY_7 = 7;
	public static final int KEY_8 = 8;
	public static final int KEY_9 = 9;
	public static final int KEY_ASTERISK = 10;
	public static final int KEY_POUND = 11;
	public static final int KEY_LEFT = 16;
	public static final int KEY_UP = 17;
	public static final int KEY_RIGHT = 18;
	public static final int KEY_DOWN = 19;
	public static final int KEY_SELECT = 20;
	public static final int KEY_SOFT1 = 21;
	public static final int KEY_SOFT2 = 22;
	public static final int KEY_IAPP = 24;
	static Frame a;

	private Display() {
	}

	public static final int getWidth() {
		return Emulator.getEmulator().getScreen().getWidth();
	}

	public static final int getHeight() {
		return Emulator.getEmulator().getScreen().getHeight();
	}

	public static final boolean isColor() {
		return true;
	}

	public static final int numColors() {
		return 0;
	}

	public static final void setCurrent(final Frame a) {
		if (a instanceof Dialog) {
			throw new IllegalArgumentException("frame can not be a Dialog");
		}
		Display.a = a;
		javax.microedition.lcdui.Display.getDisplay(Emulator.getMIDlet()).setCurrent((a != null) ? a.a : null);
		if (Display.a instanceof Canvas) {
			((Canvas) Display.a).repaint();
		}
	}

	public static final Frame getCurrent() {
		return Display.a;
	}
}
