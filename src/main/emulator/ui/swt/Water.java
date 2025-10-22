package emulator.ui.swt;

import emulator.ui.effect.WaterEffect;

final class Water implements Runnable {
	private final About.WaterTask aClass39_939;
	private WaterEffect ana;

	Water(final About.WaterTask aClass39_939, WaterEffect ana811) {
		super();
		this.aClass39_939 = aClass39_939;
		this.ana = ana811;
	}

	public final void run() {
		if (About.method458(About.WaterTask.method433(this.aClass39_939)).isDisposed()) {
			About.method459(About.WaterTask.method433(this.aClass39_939)).cancel();
			About.method460(About.WaterTask.method433(this.aClass39_939), ana);
			return;
		}
		About.method461(About.WaterTask.method433(this.aClass39_939)).setData(About.WaterTask.method433(this.aClass39_939).anIntArray818);
		About.method461(About.WaterTask.method433(this.aClass39_939)).method12(About.WaterTask.method433(this.aClass39_939).aGC814, 0, 0);
	}
}
