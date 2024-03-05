package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class63 extends SelectionAdapter {
    private final Class90 aClass90_829;

    Class63(final Class90 aClass90_829) {
        super();
        this.aClass90_829 = aClass90_829;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class90.method528(this.aClass90_829).getSelection();
    }
}
