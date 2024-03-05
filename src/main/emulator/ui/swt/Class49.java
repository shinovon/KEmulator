package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class49 extends ShellAdapter {
    private final Class90 aClass90_800;

    Class49(final Class90 aClass90_800) {
        super();
        this.aClass90_800 = aClass90_800;
    }

    public final void shellClosed(final ShellEvent shellEvent) {
        shellEvent.doit = false;
        this.aClass90_800.method507();
    }
}
