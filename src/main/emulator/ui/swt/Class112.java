package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class112 extends SelectionAdapter {
    private final Class90 aClass90_1162;

    Class112(final Class90 aClass90_1162) {
        super();
        this.aClass90_1162 = aClass90_1162;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class90.method510(this.aClass90_1162, 0);
        Class90.method505(this.aClass90_1162).setEnabled(true);
        Class90.method252(this.aClass90_1162);
    }
}
