package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class31 extends SelectionAdapter {
    private final MemoryView aClass110_616;

    Class31(final MemoryView aClass110_616) {
        super();
        this.aClass110_616 = aClass110_616;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        MemoryView.method623(this.aClass110_616, 0);
    }
}
