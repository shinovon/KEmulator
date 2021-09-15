package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class163 extends SelectionAdapter
{
    private final Class54 aClass54_1431;
    
    Class163(final Class54 aClass54_1431) {
        super();
        this.aClass54_1431 = aClass54_1431;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class54.method456(this.aClass54_1431).dispose();
    }
}
