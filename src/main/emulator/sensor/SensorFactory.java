package emulator.sensor;

public final class SensorFactory {
	public SensorFactory() {
		super();
	}

	public static SensorDevice createSensorProvider(final int n) {
		return new SensorSimulator(n);
	}
}
