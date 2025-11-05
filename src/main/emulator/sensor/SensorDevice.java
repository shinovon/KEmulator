package emulator.sensor;

public abstract class SensorDevice {
	protected int sensorId;

	public SensorDevice(final int sensorId, final int n, final int n2) {
		super();
		this.sensorId = sensorId;
	}

	public abstract float getNormalizedAngle(final int p0);

	public abstract boolean start();

	public abstract boolean shutdownSensor();

	public abstract boolean isAvailable();
}
