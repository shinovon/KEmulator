package emulator.sensor;

public final class c extends a implements Runnable {
	private f af468;
	private Thread aThread469;
	private boolean aBoolean470;
	final float[] aFloatArray471;

	public c(final int n) {
		super(n, 0, 0);
		this.af468 = new f();
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
		if (this.af468.aBoolean472) {
			this.af468.method262(SensorMan.getSensors(super.anInt445).getDescription(), array);
		}
	}

	public final boolean start() {
		final String description = SensorMan.getSensors(super.anInt445).getDescription();
		this.af468.method260();
		this.af468.method264(description);
		this.method258();
		return true;
	}

	public final boolean method225() {
		this.af468.method261(SensorMan.getSensors(super.anInt445).getDescription());
		this.af468.method263();
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
