package com.samsung.util;

public class LCDLight {
    public static boolean isSupported() {
        return false;
    }

    public static void on(int paramInt) {
        //Emulator.screenBrightness = paramInt;
    }

    public static void off() {
        //Emulator.screenBrightness = 15;
    }
}