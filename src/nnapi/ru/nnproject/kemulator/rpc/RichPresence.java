package ru.nnproject.kemulator.rpc;

import emulator.Emulator;
import emulator.Settings;

public class RichPresence {

    public static boolean isEnabled() {
        return Settings.rpc;
    }

    public static void setState(String state) {
        Emulator.rpcState = state;
    }

    public static void setDetails(String details) {
        Emulator.rpcDetails = details;
    }

    public static void setStartTimestamp(long timestamp) {
        Emulator.rpcStartTimestamp = timestamp / 1000L;
    }

    public static void setEndTimestamp(long timestamp) {
        Emulator.rpcEndTimestamp = timestamp / 1000L;
    }

    public static void setPartySize(int partySize) {
        Emulator.rpcPartySize = partySize;
    }

    public static void setPartyMax(int partyMax) {
        Emulator.rpcPartyMax = partyMax;
    }

    public static void updatePresence() {
        Emulator.updatePresence();
    }
}
