package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.debug.*;

final class Class27 extends SelectionAdapter
{
    private final MemoryView aClass110_596;
    
    Class27(final MemoryView aClass110_596) {
        super();
        this.aClass110_596 = aClass110_596;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final Object value;
        if (MemoryView.method649(this.aClass110_596).getSelectionIndex() != -1 && MemoryView.method649(this.aClass110_596).getSelectionIndex() < MemoryView.method629(this.aClass110_596).players.size() && (value = MemoryView.method629(this.aClass110_596).players.get(MemoryView.method649(this.aClass110_596).getSelectionIndex())) != null) {
            Memory.playerAct(value, 2);
        }
    }
}
