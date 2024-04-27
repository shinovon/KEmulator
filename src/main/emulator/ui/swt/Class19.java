package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class19 extends SelectionAdapter {
    private final MemoryView aClass110_586;

    Class19(final MemoryView aClass110_586) {
        super();
        this.aClass110_586 = aClass110_586;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        if (MemoryView.method639(this.aClass110_586)) {
            MemoryView.method683(this.aClass110_586, MemoryView.method670(this.aClass110_586).getSelectionIndex());
            MemoryView.method642(this.aClass110_586).redraw();
        }
    }
}
