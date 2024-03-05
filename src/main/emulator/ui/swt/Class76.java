package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class76 extends SelectionAdapter {
    private final Class46 aClass46_860;

    Class76(final Class46 aClass46_860) {
        super();
        this.aClass46_860 = aClass46_860;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class46.method443(this.aClass46_860, Class46.method441(this.aClass46_860).getSelection());
    }
}
