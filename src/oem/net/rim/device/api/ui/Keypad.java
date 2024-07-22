package net.rim.device.api.ui;

import emulator.KeyMapping;

import javax.microedition.lcdui.Canvas;

public class Keypad {
	public static final int KEY_SEND = 0;
	public static final int KEY_ESCAPE = 1;

	public static int key(int k) {
		if (KeyMapping.isLeftSoft(k)) {
			return 0;
		}
		if (KeyMapping.isRightSoft(k)) {
			return 1;
		}
		if (k == KeyMapping.getArrowKeyFromDevice(Canvas.UP)) {
			return Canvas.UP;
		}
		if (k == KeyMapping.getArrowKeyFromDevice(Canvas.DOWN)) {
			return Canvas.DOWN;
		}
		if (k == KeyMapping.getArrowKeyFromDevice(Canvas.LEFT)) {
			return Canvas.LEFT;
		}
		if (k == KeyMapping.getArrowKeyFromDevice(Canvas.RIGHT)) {
			return Canvas.RIGHT;
		}
		if (k == KeyMapping.getArrowKeyFromDevice(Canvas.FIRE)) {
			return -8;
		}
		if (k >= 'a' && k <= 'z') {
			return Character.toUpperCase(k);
		}
		return k;
	}
}
