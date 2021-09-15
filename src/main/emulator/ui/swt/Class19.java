package emulator.ui.swt;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

final class Class19 extends SelectionAdapter
{
    private final Class110 aClass110_586;
    
    Class19(final Class110 aClass110_586) {
        super();
        this.aClass110_586 = aClass110_586;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        if (Class110.method639(this.aClass110_586)) {
            Class110.method683(this.aClass110_586, Class110.method670(this.aClass110_586).getSelectionIndex());
            ((Control)Class110.method642(this.aClass110_586)).redraw();
        }
    }
}
