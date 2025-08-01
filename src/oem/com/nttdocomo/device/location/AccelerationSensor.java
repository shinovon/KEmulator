package com.nttdocomo.device.location;

public class AccelerationSensor {
	public static final int ACCELERATION_X = 1;
	public static final int ACCELERATION_Y = 2;
	public static final int ACCELERATION_Z = 3;
	public static final int ROLL = 4;
	public static final int PITCH = 5;
	public static final int SCREEN_ORIENTATION = 6;
	public static final int DOUBLE_TAP_LEFT = 0;
	public static final int DOUBLE_TAP_RIGHT = 1;
	public static final int DOUBLE_TAP_FRONT = 2;
	public static final int DOUBLE_TAP_BACK = 3;
	public static final int EVENT_SCREEN_ORIENTATION = 1;
	public static final int EVENT_DOUBLE_TAP = 2;

	public static final AccelerationSensor getAccelerationSensor() {
		return null;
	}

	public int[] getAvailableData() {
		return null;
	}

	public int getMinDataValue(int paramInt) {
		return 0;
	}

	public int getMaxDataValue(int paramInt) {
		return 0;
	}

	public AccelerationData getCurrentData() {
		return null;
	}

	public void start(int paramInt) {
	}

	public void stop() {
	}

	public int getIntervalResolution() {
		return 0;
	}

	public int getMaxDataSize() {
		return 0;
	}

	public int getDataSize() {
		return 0;
	}

	public AccelerationData getData() {
		return null;
	}

	public AccelerationData peekLatestData() {
		return null;
	}

	public void disposeData() {
	}

	public int[] getAvailableEvent() {
		return null;
	}

	public void startEventDetection(int paramInt) {
	}

	public void stopEventDetection() {
	}

	public void setEventListener(AccelerationEventListener paramAccelerationEventListener) {
	}
}
