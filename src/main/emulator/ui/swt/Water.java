package emulator.ui.swt;

import emulator.ui.effect.WaterEffect;

final class Water implements Runnable {
	private final About.WaterTask waterTask;
	private WaterEffect waterEffect;

	Water(final About.WaterTask waterTask, WaterEffect ana811) {
		super();
		this.waterTask = waterTask;
		this.waterEffect = ana811;
	}

	public final void run() {

	}
}
