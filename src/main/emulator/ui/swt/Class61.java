package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class61 extends SelectionAdapter
{
    private final Class90 aClass90_827;
    
    Class61(final Class90 aClass90_827) {
        super();
        this.aClass90_827 = aClass90_827;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class90.method520(this.aClass90_827, Class90.method514(this.aClass90_827).getSelection());
    }
}
