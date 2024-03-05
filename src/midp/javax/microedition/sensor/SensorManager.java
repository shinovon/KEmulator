package javax.microedition.sensor;

import emulator.sensor.*;

public class SensorManager {
    public SensorManager() {
        super();
    }

    public static SensorInfo[] findSensors(final String s, final String s2) {
        return SensorMan.findSensors(s, s2);
    }

    public static SensorInfo[] findSensors(final String s) {
        return SensorMan.findSensors(s);
    }

    public static void addSensorListener(final SensorListener sensorListener, final SensorInfo sensorInfo) {
        SensorMan.addSensorListener(sensorListener, sensorInfo);
    }

    public static void addSensorListener(final SensorListener sensorListener, final String s) {
        SensorMan.assSensorListener(sensorListener, s);
    }

    public static void removeSensorListener(final SensorListener sensorListener) {
        SensorMan.removeSensorListener(sensorListener);
    }
}
