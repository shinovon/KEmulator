package com.sprintpcs.util;

import emulator.Emulator;

// TODO
public class System {

    public static String getSystemState(String s) {
        return "";
    }

    public static void setExitURI(String s) {
        try {
            Emulator.getMIDlet().platformRequest(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
