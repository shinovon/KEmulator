package com.nttdocomo.ui;

public class PhoneSystem {
	public static final int DEV_BACKLIGHT = 0;
	public static final int ATTR_BACKLIGHT_OFF = 0;
	public static final int ATTR_BACKLIGHT_ON = 1;
	public static final int DEV_VIBRATOR = 1;
	public static final int ATTR_VIBRATOR_OFF = 0;
	public static final int ATTR_VIBRATOR_ON = 1;
	public static final int DEV_FOLDING = 2;
	public static final int ATTR_FOLDING_CLOSE = 0;
	public static final int ATTR_FOLDING_OPEN = 1;
	public static final int DEV_MAILBOX = 3;
	public static final int ATTR_MAIL_NONE = 0;
	public static final int ATTR_MAIL_RECEIVED = 1;
	public static final int ATTR_MAIL_AT_CENTER = 2;
	public static final int DEV_BATTERY = 5;
	public static final int ATTR_BATTERY_PARTIAL = 0;
	public static final int ATTR_BATTERY_FULL = 1;
	public static final int ATTR_BATTERY_CHARGING = 2;
	public static final int DEV_SERVICEAREA = 6;
	public static final int ATTR_SERVICEAREA_OUTSIDE = 0;
	public static final int ATTR_SERVICEAREA_INSIDE = 1;
	public static final int DEV_MANNER = 7;
	public static final int ATTR_MANNER_OFF = 0;
	public static final int ATTR_MANNER_ON = 1;
	public static final int SOUND_INFO = 0;
	public static final int SOUND_WARNING = 1;
	public static final int SOUND_ERROR = 2;
	public static final int SOUND_ALARM = 3;
	public static final int SOUND_CONFIRM = 4;

	private PhoneSystem() {
	}

	public static final void setAttribute(final int n, final int n2) {
		switch (n) {
		}
	}

	public static final int getAttribute(final int n) {
		switch (n) {
			case 0: {
				return 0;
			}
			case 1: {
				return 0;
			}
			case 2: {
				return 1;
			}
			case 3: {
				return 0;
			}
			case 5: {
				return 1;
			}
			case 6: {
				return 0;
			}
			case 7: {
				return 0;
			}
			default: {
				return 0;
			}
		}
	}

	public static final boolean isAvailable(final int n) {
		return false;
	}

	public static final void playSound(final int n) {
	}
}
