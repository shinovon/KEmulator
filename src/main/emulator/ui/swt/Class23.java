package emulator.ui.swt;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

final class Class23 extends SelectionAdapter
{
    private final MemoryView aClass110_592;
    
    Class23(final MemoryView aClass110_592) {
        super();
        this.aClass110_592 = aClass110_592;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        ((Control)MemoryView.method642(this.aClass110_592)).redraw();
    }
}
