package emulator.sensor;

import javax.microedition.sensor.MeasurementRange;
import javax.microedition.sensor.SensorInfo;
import javax.microedition.sensor.SensorListener;
import javax.microedition.sensor.Unit;
import java.util.Vector;
//taken from
//https://github.com/hbao/phonemefeaturedevices/blob/78b194c67b3b21a9ec6a847972e6bf5dbafdde04/VirtualMachine/phoneme_feature/jsr256/src/share/classes/com/sun/javame/sensor/SensorRegistry.java
public final class SensorMan {
	private static final SensorImpl[] sensorsCache;

	public SensorMan() {
		super();
	}

	private static SensorImpl[] createSensors() {
		final SensorImpl[] array = new SensorImpl[2];
		final String[] array2 = {"axis_x", "axis_y", "axis_z"};
		final k[] array3 = new k[3];
		for (int i = 0; i < 3; ++i) {
			final MeasurementRange[] array4 = {null};
			for (int j = 0; j < 1; ++j) {
				array4[j] = new MeasurementRange(-180.0, 180.0, 0.10000000149011612);
			}
			array3[i] = new k(0, i, array2[i], 1, 0.4f, 0, Unit.getUnit("m/s^2"), array4);
		}
		final h h;
		(h = new h()).setProperty("location", "acceleration");
		final a method279 = m.method279(0);
		array[0] = new SensorImpl(0, "orientation", "acceleration", "device", "ACCM01", 256, 1, h, false, true, array3, method279);
		final k[] array5 = new k[3];
		for (int k = 0; k < 3; ++k) {
			final MeasurementRange[] array6 = {null};
			for (int l = 0; l < 1; ++l) {
				array6[l] = new MeasurementRange(-180.0, 180.0, 0.10000000149011612);
			}
			array5[k] = new k(0, k, array2[k], 2, 0.4f, 0, Unit.getUnit("m/s^2"), array6);
		}
		array[1] = new SensorImpl(0, "orientation", "acceleration", "user", "ACCM01", 256, 1, h, false, true, array5, method279);
		return array;
	}

	public static SensorInfo[] findSensors(final String s, final String contextType) {
		if (contextType != null && !"ambient".equals(contextType) && !"device".equals(contextType) && !"user".equals(contextType) && !"vehicle".equals(contextType)) {
			throw new IllegalArgumentException("Illegal contextType");
		}
		if (s == null && contextType == null) {
			return getAllSensors();
		}
		final Vector vector = new Vector<SensorImpl>(SensorMan.sensorsCache.length);
		for (int i = 0; i < SensorMan.sensorsCache.length; ++i) {
			final SensorImpl j;
			if ((j = SensorMan.sensorsCache[i]).method237(s, contextType)) {
				vector.addElement(j);
			}
		}
		final SensorInfo[] array = new SensorInfo[vector.size()];
		vector.copyInto(array);
		return array;
	}

	public static SensorImpl[] findSensors(final String url) {
		if (url == null) {
			throw new NullPointerException("url is null");
		}
		return findSensors(SensorUtil.method250(url));
	}

	private static SensorImpl[] findSensors(final SensorUtil d) {
		final Vector vector = new Vector<SensorImpl>(SensorMan.sensorsCache.length);
		for (int i = 0; i < SensorMan.sensorsCache.length; ++i) {
			final SensorImpl j;
			if ((j = SensorMan.sensorsCache[i]).method238(d)) {
				vector.addElement(j);
			}
		}
		final SensorImpl[] array = new SensorImpl[vector.size()];
		vector.copyInto(array);
		return array;
	}

	private static SensorImpl[] getAllSensors() {
		final SensorImpl[] array = new SensorImpl[SensorMan.sensorsCache.length];
		for (int i = 0; i < SensorMan.sensorsCache.length; ++i) {
			array[i] = SensorMan.sensorsCache[i];
		}
		return array;
	}

	static SensorImpl getSensors(final int n) {
		SensorImpl j = null;
		if (0 <= n && n < SensorMan.sensorsCache.length) {
			j = SensorMan.sensorsCache[n];
		}
		return j;
	}

	public static void addSensorListener(final SensorListener listener, final String quantity) {
		if (listener == null) {
			throw new NullPointerException("Listener is null");
		}
		if (quantity == null) {
			throw new NullPointerException("Quantity is null");
		}
		for (int i = 0; i < SensorMan.sensorsCache.length; ++i) {
			if (SensorMan.sensorsCache[i].getQuantity().equals(quantity)) {
				addSensorListener(listener, SensorMan.sensorsCache[i]);
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
		for (int i = 0; i < SensorMan.sensorsCache.length; ++i) {
			if (SensorMan.sensorsCache[i] == info) {
				return true;
			}
		}
		return false;
	}

	static {
		sensorsCache = createSensors();
	}
}
