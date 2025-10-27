package emulator.sensor;

public final class c extends a implements Runnable {
	private SensorSimulatorClient af468;
	private Thread aThread469;
	private boolean aBoolean470;
	final float[] aFloatArray471;

	public c(final int n) {
		super(n, 0, 0);
		this.af468 = new SensorSimulatorClient();
		this.aThread469 = null;
		this.aBoolean470 = false;
		this.aFloatArray471 = new float[3];
	}

	public final float method223(final int n) {
		float n2;
		if ((n2 = this.aFloatArray471[n]) > 180.0f) {
			n2 -= 360.0f;
		}
		return n2;
	}

	private void method257(final float[] array) {
		if (this.af468.connected) {
			this.af468.readSensor(SensorRegistry.getSensor(super.anInt445).getDescription(), array);
		}
	}

	public final boolean start() {
		final String description = SensorRegistry.getSensor(super.anInt445).getDescription();
		this.af468.connect();
		this.af468.enableSensor(description);
		this.method258();
		return true;
	}

	public final boolean method225() {
		this.af468.disableSensor(SensorRegistry.getSensor(super.anInt445).getDescription());
		this.af468.disconnect();
		this.method259();
		return true;
	}

	public final boolean isAvailable() {
		return true;
	}

	public final void run() {
		do {
			try {
				Thread.sleep(30L);
				synchronized (this.aFloatArray471) {
					this.method257(this.aFloatArray471);
				}
			} catch (InterruptedException ignored) {}
		} while (!this.aBoolean470);
	}

	private void method258() {
		this.aBoolean470 = false;
		(this.aThread469 = new Thread(this)).start();
	}

	private void method259() {
		this.aBoolean470 = true;
		try {
			if (this.aThread469.isAlive()) {
				this.aThread469.join();
			}
		} catch (Exception ex) {
			Thread.currentThread().interrupt();
		}
	}
}
