package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class77 extends SelectionAdapter
{
    private final Class46 aClass46_861;
    
    Class77(final Class46 aClass46_861) {
        super();
        this.aClass46_861 = aClass46_861;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class46.method443(this.aClass46_861, Class46.method441(this.aClass46_861).getSelection());
    }
}
