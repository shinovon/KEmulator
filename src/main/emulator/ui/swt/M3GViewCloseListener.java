package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class M3GViewCloseListener extends ShellAdapter {
    private final M3GViewUI aClass90_800;

    M3GViewCloseListener(final M3GViewUI aClass90_800) {
        super();
        this.aClass90_800 = aClass90_800;
    }

    public final void shellClosed(final ShellEvent shellEvent) {
        shellEvent.doit = false;
        this.aClass90_800.method507();
    }
}
