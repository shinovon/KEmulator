package com.sprintpcs.media;

import emulator.Emulator;
import emulator.ui.IEmulator;
import emulator.ui.IScreen;

public class Vibrator {
    public static void vibrate(int paramInt) {
        Emulator.getEmulator().getScreen().startVibra(paramInt);
    }
}
