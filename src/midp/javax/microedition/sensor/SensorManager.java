package javax.microedition.sensor;

import emulator.sensor.SensorRegistry;

public class SensorManager {
	public SensorManager() {
		super();
	}

	public static SensorInfo[] findSensors(final String s, final String s2) {
		return SensorRegistry.findSensors(s, s2);
	}

	public static SensorInfo[] findSensors(final String s) {
		return SensorRegistry.findSensors(s);
	}

	public static void addSensorListener(final SensorListener sensorListener, final SensorInfo sensorInfo) {
		SensorRegistry.addSensorListener(sensorListener, sensorInfo);
	}

	public static void addSensorListener(final SensorListener sensorListener, final String s) {
		SensorRegistry.addSensorListener(sensorListener, s);
	}

	public static void removeSensorListener(final SensorListener sensorListener) {
		SensorRegistry.removeSensorListener(sensorListener);
	}
}
