package emulator.ui.swt;

import org.eclipse.swt.widgets.*;

final class Refresher implements Runnable {
    private final Class90 aClass90_830;

    private Refresher(final Class90 aClass90_830) {
        super();
        this.aClass90_830 = aClass90_830;
    }

    public final void run() {
        while (Class90.method231(this.aClass90_830) != null) {
            if (((Widget) Class90.method231(this.aClass90_830)).isDisposed()) {
                return;
            }
            EmulatorImpl.syncExec(new Class10(this));
            try {
                Thread.sleep(10L);
            } catch (Exception ex) {
            }
        }
    }

    Refresher(final Class90 class90, final Class122 class91) {
        this(class90);
    }

    static Class90 method464(final Refresher refresher) {
        return refresher.aClass90_830;
    }
}
