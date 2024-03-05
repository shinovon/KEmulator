package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.debug.*;

final class Class28 extends SelectionAdapter {
    private final MemoryView aClass110_597;

    Class28(final MemoryView aClass110_597) {
        super();
        this.aClass110_597 = aClass110_597;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final Object value;
        if (MemoryView.method649(this.aClass110_597).getSelectionIndex() != -1 && MemoryView.method649(this.aClass110_597).getSelectionIndex() < MemoryView.method629(this.aClass110_597).players.size() && (value = MemoryView.method629(this.aClass110_597).players.get(MemoryView.method649(this.aClass110_597).getSelectionIndex())) != null) {
            Memory.playerAct(value, 1);
        }
    }
}
