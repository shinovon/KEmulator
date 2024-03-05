package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class78 extends FocusAdapter {
    private final Class46 aClass46_862;

    Class78(final Class46 aClass46_862) {
        super();
        this.aClass46_862 = aClass46_862;
    }

    public final void focusLost(final FocusEvent focusEvent) {
        Class46.method445(this.aClass46_862, 0);
    }
}
