package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class62 extends SelectionAdapter
{
    private final Class90 aClass90_828;
    
    Class62(final Class90 aClass90_828) {
        super();
        this.aClass90_828 = aClass90_828;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class90.method529(this.aClass90_828);
    }
}
