package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class60 extends SelectionAdapter
{
    private final Class90 aClass90_826;
    
    Class60(final Class90 aClass90_826) {
        super();
        this.aClass90_826 = aClass90_826;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class90.method527(this.aClass90_826, Class90.method521(this.aClass90_826).getSelection());
    }
}
