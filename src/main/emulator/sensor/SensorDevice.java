package emulator.sensor;

public abstract class SensorDevice {
	protected int anInt445;

	public SensorDevice(final int anInt445, final int n, final int n2) {
		super();
		this.anInt445 = anInt445;
	}

	public abstract float getNormalizedAngle(final int p0);

	public abstract boolean start();

	public abstract boolean shutdownSensor();

	public abstract boolean isAvailable();
}
