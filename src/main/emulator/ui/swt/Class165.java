package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class165 extends SelectionAdapter {
    private final Class46 aClass46_1433;

    Class165(final Class46 aClass46_1433) {
        super();
        this.aClass46_1433 = aClass46_1433;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        try {
            Class46.method443(this.aClass46_1433, Class46.method441(this.aClass46_1433).getSelection());
        } catch (Exception ex) {
        }
    }
}
