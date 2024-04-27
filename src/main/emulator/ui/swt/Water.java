package emulator.ui.swt;

import emulator.ui.effect.*;

final class Water implements Runnable {
    private final Class54.WaterTask aClass39_939;
    private a ana;

    Water(final Class54.WaterTask aClass39_939, a ana811) {
        super();
        this.aClass39_939 = aClass39_939;
        this.ana = ana811;
    }

    public final void run() {
        if (Class54.method458(Class54.WaterTask.method433(this.aClass39_939)).isDisposed()) {
            Class54.method459(Class54.WaterTask.method433(this.aClass39_939)).cancel();
            Class54.method460(Class54.WaterTask.method433(this.aClass39_939), ana);
            return;
        }
        Class54.method461(Class54.WaterTask.method433(this.aClass39_939)).setData(Class54.WaterTask.method433(this.aClass39_939).anIntArray818);
        Class54.method461(Class54.WaterTask.method433(this.aClass39_939)).method12(Class54.WaterTask.method433(this.aClass39_939).aGC814, 0, 0);
    }
}
