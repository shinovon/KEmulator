/*
Copyright (c) 2024-2025 Arman Jussupgaliyev
*/
package ru.nnproject.kemulator.rpc;

import emulator.Emulator;
import emulator.Settings;

public class RichPresence {

	public static boolean isEnabled() {
		return Settings.rpc;
	}

	public static void setState(String state) {
		emulator.ui.RichPresence.rpcState = state;
	}

	public static void setDetails(String details) {
		emulator.ui.RichPresence.rpcDetails = details;
	}

	public static void setStartTimestamp(long timestamp) {
		emulator.ui.RichPresence.rpcStartTimestamp = timestamp / 1000L;
	}

	public static void setEndTimestamp(long timestamp) {
		emulator.ui.RichPresence.rpcEndTimestamp = timestamp / 1000L;
	}

	public static void setPartySize(int partySize) {
		emulator.ui.RichPresence.rpcPartySize = partySize;
	}

	public static void setPartyMax(int partyMax) {
		emulator.ui.RichPresence.rpcPartyMax = partyMax;
	}

	public static void updatePresence() {
		emulator.ui.RichPresence.updatePresence();
	}
}
