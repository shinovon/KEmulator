package com.vodafone.system;

import emulator.Emulator;

import javax.microedition.midlet.MIDlet;

public class DeviceControl {
	public static final int BATTERY = 1;
	public static final int FIELD_INTENSITY = 2;
	public static final int FLIP_STATE = 7;
	public static final int FLIP_OPENED = 0;
	public static final int FLIP_CLOSED = 1;

	private static DeviceControl instance;

	public static final DeviceControl getDefaultDeviceControl() {
		if (instance == null) {
			instance = new DeviceControl();
		}
		return instance;
	}

	public int getDeviceState(int deviceNo) {
		switch (deviceNo) {
			case BATTERY:
			case FIELD_INTENSITY:
				return 100;
			case com.vodafone.v10.system.device.DeviceControl.KEY_STATE:
				try {
					return Emulator.getCanvas()._getKeyStatesVodafone();
				} catch (Exception ignored) {}
			default:
				return 0;
		}
	}

	public String getWakeupParam(MIDlet paramMIDlet, String paramString) {
		return null;
	}

	public static void setBodyOpenListener(BodyOpenListener paramBodyOpenListener) {}
}
