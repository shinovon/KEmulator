package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class34 extends SelectionAdapter {
    private final MemoryView aClass110_619;

    Class34(final MemoryView aClass110_619) {
        super();
        this.aClass110_619 = aClass110_619;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        try {
            MemoryView.method641(this.aClass110_619, MemoryView.method664(this.aClass110_619).getSelection());
        } catch (Exception ignored) {
        }
    }
}
