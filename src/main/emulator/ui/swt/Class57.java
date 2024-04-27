package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class57 extends ControlAdapter {
    private final Class90 aClass90_823;

    Class57(final Class90 aClass90_823) {
        super();
        this.aClass90_823 = aClass90_823;
    }

    public final void controlResized(final ControlEvent controlEvent) {
        if (Class90.method499(this.aClass90_823).getVisible()) {
            Class90.method252(this.aClass90_823);
        }
    }
}
