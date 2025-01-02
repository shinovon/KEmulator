package com.j_phone.system;

import java.io.IOException;

public class MotionDetectiveSensor {
	public static final int POSTURE_INFO = 1;
	public static final int KEY_COMPATIBLE = 2;
	public static final int KEY_SENSOR = 3;
	public static final int CYCLE_20 = 1;
	public static final int CYCLE_40 = 2;
	public static final int CYCLE_60 = 3;
	public static final int CYCLE_80 = 4;
	public static final int CYCLE_100 = 5;

	public static final MotionDetectiveSensor getDefaultMotionDetectiveSensor()
			throws IOException {
		return null;
	}

	public void startSensor(int paramInt1, int paramInt2) {
	}

	public void stopSensor() {
	}

	public PostureInfo getPostureInfoLatest()
			throws IOException {
		return null;
	}

	public PostureInfo getPostureInfoStack(int paramInt)
			throws IOException {
		return null;
	}

	public int getStackCount() {
		return 0;
	}

	public int getState() {
		return 0;
	}

	public void setNeutralPosition()
			throws IOException {
	}

	public void setDefaultNeutralPosition()
			throws IOException {
	}
}
