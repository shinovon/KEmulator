package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class71 extends SelectionAdapter
{
    private final Class110 aClass110_854;
    
    Class71(final Class110 aClass110_854) {
        super();
        this.aClass110_854 = aClass110_854;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class110.method629(this.aClass110_854).aVector1463.clear();
        Class110.method647(this.aClass110_854);
    }
}
