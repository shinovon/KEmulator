package com.sprintpcs.util;

import emulator.Emulator;

// TODO
public class System {

	public static String getSystemState(String s) {
//        if("sprint.device.formfactor".equals(s)) {
//            return "OPEN";
//        }
		return "";
	}

	public static void setExitURI(String s) {
		try {
			Emulator.getMIDlet().platformRequest(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addSystemListener(SystemEventListener listener) {
	}

	public static String[] getPropertiesList() {
		return null;
	}

	public static void setSystemSetting(String property, String value) {}

	public static String getProtectedProperty(String property) {
		return null;
	}

	public static void promptMasterVolume() {}
}
