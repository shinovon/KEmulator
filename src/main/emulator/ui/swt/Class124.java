package emulator.ui.swt;

//import emulator.graphics3D.view.*;

import org.eclipse.swt.widgets.*;

final class Flusher implements Runnable {
    private final Class90 aClass90_1207;

    private Flusher(final Class90 aClass90_1207) {
        super();
        this.aClass90_1207 = aClass90_1207;
    }

    public final void run() {
        // Class90.method497(this.aClass90_1207, a.abool());
        Class90.method497(this.aClass90_1207, false);
        while (Class90.method496(this.aClass90_1207) != null) {
            if (((Widget) Class90.method496(this.aClass90_1207)).isDisposed()) {
                return;
            }
            if (Class90.method498(this.aClass90_1207) && Class90.method508(this.aClass90_1207)) {
                try {
                    Class90.method502(this.aClass90_1207);
                } catch (Exception ex) {
                }
                Class90.method509(this.aClass90_1207, false);
            }
            try {
                Thread.sleep(10L);
            } catch (Exception ex2) {
            }
        }
    }

    Flusher(final Class90 class90, final Class122 class91) {
        this(class90);
    }
}
