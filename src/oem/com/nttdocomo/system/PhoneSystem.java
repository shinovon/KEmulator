package com.nttdocomo.system;

public class PhoneSystem {
    public static final int ATTR_BACKLIGHT_OFF = 0;
    public static final int ATTR_BACKLIGHT_ON = 1;
    public static final int ATTR_FOLDING_CLOSE = 0;
    public static final int ATTR_FOLDING_OPEN = 1;
    public static final int ATTR_MAIL_AT_CENTER = 2;
    public static final int ATTR_MAIL_NONE = 0;
    public static final int ATTR_MAIL_RECEIVED = 1;
    public static final int ATTR_MESSAGE_AT_CENTER = 2;
    public static final int ATTR_MESSAGE_NONE = 0;
    public static final int ATTR_MESSAGE_RECEIVED = 1;
    public static final int ATTR_VIBRATOR_OFF = 0;
    public static final int ATTR_VIBRATOR_ON = 1;
    public static final int ATTR_MANNER_OFF = 0;
    public static final int ATTR_MANNER_ON = 1;
    public static final int ATTR_SCREEN_INVISIBLE = 0;
    public static final int ATTR_SCREEN_VISIBLE = 1;
    public static final int ATTR_SURROUND_OFF = 0;
    public static final int ATTR_SURROUND_ON = 1;
    public static final int ATTR_AREAINFO_FOMA = 0;
    public static final int ATTR_AREAINFO_HSDPA = 1;
    public static final int ATTR_AREAINFO_OUTSIDE = 2;
    public static final int ATTR_AREAINFO_ROAMINGOUT = 3;
    public static final int ATTR_AREAINFO_SELFMODE = 4;
    public static final int ATTR_AREAINFO_COMMUNICATING = 5;
    public static final int ATTR_AREAINFO_HSUPA = 6;
    public static final int ATTR_AREAINFO_LTE = 7;
    public static final int ATTR_AREAINFO_UNKNOWN = 99;
    public static final int ATTR_POWER_BATTERY = 0;
    public static final int ATTR_POWER_EXTERNAL = 1;
    public static final int ATTR_SIGNAL_OUTSIDE = 0;
    public static final int ATTR_SIGNAL_LEVEL_1 = 1;
    public static final int ATTR_SIGNAL_LEVEL_2 = 2;
    public static final int ATTR_SIGNAL_LEVEL_3 = 3;
    public static final int ATTR_FEMTOCELL_OTHER = 0;
    public static final int ATTR_FEMTOCELL_PUBLIC = 1;
    public static final int ATTR_FEMTOCELL_PRIVATE = 2;
    public static final int ATTR_AUDIO_OUTPUT_EXTERNAL = 0;
    public static final int ATTR_AUDIO_OUTPUT_INTERNAL = 1;
    public static final int ATTR_EYE_DIRECTION_Y_PLUS = 0;
    public static final int ATTR_EYE_DIRECTION_X_PLUS = 1;
    public static final int ATTR_EYE_DIRECTION_Y_MINUS = 2;
    public static final int ATTR_EYE_DIRECTION_X_MINUS = 3;
    public static final int DEV_BACKLIGHT = 0;
    public static final int DEV_VIBRATOR = 1;
    public static final int DEV_FOLDING = 2;
    public static final int DEV_MAILBOX = 3;
    public static final int DEV_MESSAGEBOX = 4;
    public static final int DEV_MANNER = 7;
    public static final int DEV_KEYPAD = 8;
    public static final int DEV_SCREEN_VISIBLE = 9;
    public static final int DEV_AUDIO_SURROUND = 10;
    public static final int DEV_AREAINFO = 11;
    public static final int DEV_MAX_BATTERY_LEVEL = 12;
    public static final int DEV_BATTERY_LEVEL = 13;
    public static final int DEV_POWER_SOURCE = 14;
    public static final int DEV_SIGNAL_STATE = 15;
    public static final int DEV_CHARGING_TIME = 16;
    public static final int DEV_PHONE_REMAINING_TIME = 17;
    public static final int DEV_FEMTOCELL = 18;
    public static final int DEV_AUDIO_OUTPUT = 19;
    public static final int DEV_EYE_DIRECTION = 20;
    public static final int MIN_VENDOR_ATTR = 64;
    public static final int MAX_VENDOR_ATTR = 127;
    public static final int MAX_OPTION_ATTR = 255;
    public static final int MIN_OPTION_ATTR = 128;
    public static final int SOUND_INFO = 0;
    public static final int SOUND_WARNING = 1;
    public static final int SOUND_ERROR = 2;
    public static final int SOUND_ALARM = 3;
    public static final int SOUND_CONFIRM = 4;
    public static final String TERMINAL_ID = "terminal-id";
    public static final String USER_ID = "user-id";
    public static final String UIM_VERSION = "uim-version";

    public static final void setAttribute(int paramInt1, int paramInt2) {
    }

    public static final int getAttribute(int paramInt) {
        return 0;
    }

    public static final boolean isAvailable(int paramInt) {
        return false;
    }

    public static final void playSound(int paramInt) {
    }

    public static final String getProperty(String paramString) {
        return null;
    }

    public static final int measureRSInfo() {
        return 0;
    }

    public static final int[] getRSCP() {
        return null;
    }

    public static final int[] getEcNo() {
        return null;
    }
}
