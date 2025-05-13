package com.nttdocomo.ui;

import javax.microedition.lcdui.*;

import emulator.*;

public abstract class Frame {
	public static final int SOFT_KEY_1 = 0;
	public static final int SOFT_KEY_2 = 1;
	int backgroundColor = -1;
	Displayable a;

	public final int getWidth() {
		return Emulator.getEmulator().getScreen().getWidth();
	}

	public final int getHeight() {
		return Emulator.getEmulator().getScreen().getHeight();
	}

	public void setBackground(final int n) {
		backgroundColor = n;
	}

	public void setSoftLabel(final int n, String s) {
		if (s == null) {
			s = "";
		}
		if (n == 0) {
			Emulator.getEmulator().getScreen().setLeftSoftLabel(s);
		} else {
			if (n != 1) {
				throw new IllegalArgumentException("key must be SOFT_KEY_1 or SOFT_KEY_2");
			}
			Emulator.getEmulator().getScreen().setRightSoftLabel(s);
		}
	}
}
