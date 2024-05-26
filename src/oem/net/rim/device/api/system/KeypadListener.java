package net.rim.device.api.system;

public abstract interface KeypadListener {
	public static final int STATUS_ALT = 1;
	public static final int STATUS_SHIFT = 2;
	public static final int STATUS_CAPS_LOCK = 4;
	public static final int STATUS_KEY_HELD_WHILE_ROLLING = 8;
	public static final int STATUS_ALT_LOCK = 16;
	public static final int STATUS_SHIFT_LEFT = 32;
	public static final int STATUS_SHIFT_RIGHT = 64;
	public static final int STATUS_NOT_FROM_KEYPAD = 32768;
	public static final int STATUS_TRACKWHEEL = 1073741824;
	public static final int STATUS_FOUR_WAY = 536870912;
}