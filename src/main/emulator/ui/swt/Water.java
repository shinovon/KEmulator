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
		if (About.method458(About.WaterTask.method433(this.waterTask)).isDisposed()) {
			About.method459(About.WaterTask.method433(this.waterTask)).cancel();
			About.method460(About.WaterTask.method433(this.waterTask), waterEffect);
			return;
		}
		About.method461(About.WaterTask.method433(this.waterTask)).setData(About.WaterTask.method433(this.waterTask).waterImageData);
		About.method461(About.WaterTask.method433(this.waterTask)).method12(About.WaterTask.method433(this.waterTask).canvasGC, 0, 0);
	}
}
