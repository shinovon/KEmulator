package net.rim.device.api.ui;

import emulator.KeyMapping;

public class Keypad {
	public static final int KEY_SEND = 0;
	public static final int KEY_ESCAPE = 1;

	public static int key(int paramInt) {
		if (KeyMapping.isLeftSoft(paramInt)) {
			return 0;
		}
		if (KeyMapping.isRightSoft(paramInt)) {
			return 1;
		}
		return paramInt;
	}
}
