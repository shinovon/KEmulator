package emulator.sensor;

public final class SensorSimulator extends SensorDevice implements Runnable {
	private SensorSimulatorClient simulatorClient;
	private Thread pollThread;
	private boolean stopRequested;
	final float[] latestValues;

	public SensorSimulator(final int n) {
		super(n, 0, 0);
		this.simulatorClient = new SensorSimulatorClient();
		this.pollThread = null;
		this.stopRequested = false;
		this.latestValues = new float[3];
	}

	public final float getNormalizedAngle(final int n) {
		float n2;
		if ((n2 = this.latestValues[n]) > 180.0f) {
			n2 -= 360.0f;
		}
		return n2;
	}

	private void updateSensorValues(final float[] array) {
		if (this.simulatorClient.connected) {
			this.simulatorClient.readSensor(SensorRegistry.getSensor(super.sensorId).getDescription(), array);
		}
	}

	public final boolean start() {
		final String description = SensorRegistry.getSensor(super.sensorId).getDescription();
		this.simulatorClient.connect();
		this.simulatorClient.enableSensor(description);
		this.startPolling();
		return true;
	}

	public final boolean shutdownSensor() {
		this.simulatorClient.disableSensor(SensorRegistry.getSensor(super.sensorId).getDescription());
		this.simulatorClient.disconnect();
		this.stopPolling();
		return true;
	}

	public final boolean isAvailable() {
		return true;
	}

	public final void run() {
		do {
			try {
				Thread.sleep(30L);
				synchronized (this.latestValues) {
					this.updateSensorValues(this.latestValues);
				}
			} catch (InterruptedException ignored) {}
		} while (!this.stopRequested);
	}

	private void startPolling() {
		this.stopRequested = false;
		(this.pollThread = new Thread(this)).start();
	}

	private void stopPolling() {
		this.stopRequested = true;
		try {
			if (this.pollThread.isAlive()) {
				this.pollThread.join();
			}
		} catch (Exception ex) {
			Thread.currentThread().interrupt();
		}
	}
}
