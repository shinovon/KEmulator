package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class8 extends SelectionAdapter
{
    private final Class5 aClass5_564;
    
    Class8(final Class5 aClass5_564) {
        super();
        this.aClass5_564 = aClass5_564;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class146.asyncExec(Class5.method308(this.aClass5_564));
    }
}
