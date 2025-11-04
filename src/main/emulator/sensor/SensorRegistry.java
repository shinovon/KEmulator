package emulator.sensor;

import javax.microedition.sensor.MeasurementRange;
import javax.microedition.sensor.SensorInfo;
import javax.microedition.sensor.SensorListener;
import javax.microedition.sensor.Unit;
import java.util.Vector;
//taken from
//https://github.com/hbao/phonemefeaturedevices/blob/78b194c67b3b21a9ec6a847972e6bf5dbafdde04/VirtualMachine/phoneme_feature/jsr256/src/share/classes/com/sun/javame/sensor/SensorRegistry.java
public final class SensorRegistry {
	private static final Sensor[] sensorsCache;

	public SensorRegistry() {
		super();
	}

	private static Sensor[] createSensors() {
		final Sensor[] array = new Sensor[2];
		final String[] array2 = {"axis_x", "axis_y", "axis_z"};
		final ChannelImpl[] array3 = new ChannelImpl[3];
		for (int i = 0; i < 3; ++i) {
			final MeasurementRange[] array4 = {null};
			for (int j = 0; j < 1; ++j) {
				array4[j] = new MeasurementRange(-180.0, 180.0, 0.10000000149011612);
			}
			array3[i] = new ChannelImpl(0, i, array2[i], 1, 0.4f, 0, Unit.getUnit("m/s^2"), array4);
		}
		final SensorPropertiesImpl h;
		(h = new SensorPropertiesImpl()).setProperty("location", "acceleration");
		final SensorDevice createAccelSensorProvider = SensorFactory.createSensorProvider(0);
		array[0] = new Sensor(0, "orientation", "acceleration", "device", "ACCM01", 256, 1, h, false, true, array3, createAccelSensorProvider);
		final ChannelImpl[] array5 = new ChannelImpl[3];
		for (int k = 0; k < 3; ++k) {
			final MeasurementRange[] array6 = {null};
			for (int l = 0; l < 1; ++l) {
				array6[l] = new MeasurementRange(-180.0, 180.0, 0.10000000149011612);
			}
			array5[k] = new ChannelImpl(0, k, array2[k], 2, 0.4f, 0, Unit.getUnit("m/s^2"), array6);
		}
		array[1] = new Sensor(0, "orientation", "acceleration", "user", "ACCM01", 256, 1, h, false, true, array5, createAccelSensorProvider);
		return array;
	}

	public static SensorInfo[] findSensors(final String s, final String contextType) {
		if (contextType != null && !"ambient".equals(contextType) && !"device".equals(contextType) && !"user".equals(contextType) && !"vehicle".equals(contextType)) {
			throw new IllegalArgumentException("Illegal contextType");
		}
		if (s == null && contextType == null) {
			return getAllSensors();
		}
		final Vector vector = new Vector<Sensor>(SensorRegistry.sensorsCache.length);
		for (int i = 0; i < SensorRegistry.sensorsCache.length; ++i) {
			final Sensor j;
			if ((j = SensorRegistry.sensorsCache[i]).matches(s, contextType)) {
				vector.addElement(j);
			}
		}
		final SensorInfo[] array = new SensorInfo[vector.size()];
		vector.copyInto(array);
		return array;
	}

	public static Sensor[] findSensors(final String url) {
		if (url == null) {
			throw new NullPointerException("url is null");
		}
		return findSensors(SensorUrl.parseUrl(url));
	}

	private static Sensor[] findSensors(final SensorUrl d) {
		final Vector vector = new Vector<Sensor>(SensorRegistry.sensorsCache.length);
		for (int i = 0; i < SensorRegistry.sensorsCache.length; ++i) {
			final Sensor j;
			if ((j = SensorRegistry.sensorsCache[i]).matches(d)) {
				vector.addElement(j);
			}
		}
		final Sensor[] array = new Sensor[vector.size()];
		vector.copyInto(array);
		return array;
	}

	private static Sensor[] getAllSensors() {
		final Sensor[] array = new Sensor[SensorRegistry.sensorsCache.length];
		for (int i = 0; i < SensorRegistry.sensorsCache.length; ++i) {
			array[i] = SensorRegistry.sensorsCache[i];
		}
		return array;
	}

	static Sensor getSensor(final int sensorNumber) {
		Sensor retValue = null;
		if (0 <= sensorNumber && sensorNumber < SensorRegistry.sensorsCache.length) {
			retValue = SensorRegistry.sensorsCache[sensorNumber];
		}
		return retValue;
	}

	public static void addSensorListener(final SensorListener listener, final String quantity) {
		if (listener == null) {
			throw new NullPointerException("Listener is null");
		}
		if (quantity == null) {
			throw new NullPointerException("Quantity is null");
		}
		for (int i = 0; i < SensorRegistry.sensorsCache.length; ++i) {
			if (SensorRegistry.sensorsCache[i].getQuantity().equals(quantity)) {
				addSensorListener(listener, SensorRegistry.sensorsCache[i]);
			}
		}
	}

	public static void addSensorListener(final SensorListener listener, final SensorInfo info) {
		if (listener == null) {
			throw new NullPointerException("Listener is null");
		}
		if (info == null) {
			throw new NullPointerException("Info is null");
		}
		if (!containsSensorInfo(info)) {
			throw new IllegalArgumentException("Invalid SensorInfo");
		}
	}

	public static void removeSensorListener(final SensorListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener is null");
		}
	}

	private static boolean containsSensorInfo(final SensorInfo info) {
		for (int i = 0; i < SensorRegistry.sensorsCache.length; ++i) {
			if (SensorRegistry.sensorsCache[i] == info) {
				return true;
			}
		}
		return false;
	}

	static {
		sensorsCache = createSensors();
	}
}
