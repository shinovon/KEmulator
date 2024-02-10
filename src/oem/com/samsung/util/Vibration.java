package com.samsung.util;

import emulator.Emulator;
import emulator.ui.IEmulator;
import emulator.ui.IScreen;

public class Vibration {
    public static boolean isSupported() {
        return true;
    }

    public static void start(int paramInt1, int paramInt2) {
        Emulator.getEmulator().getScreen().startVibra(paramInt1);
    }

    public static void stop() {
        Emulator.getEmulator().getScreen().stopVibra();
    }
}
