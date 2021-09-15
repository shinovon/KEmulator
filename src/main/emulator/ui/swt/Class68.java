package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class68 extends SelectionAdapter
{
    private final Class110 aClass110_851;
    
    Class68(final Class110 aClass110_851) {
        super();
        this.aClass110_851 = aClass110_851;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class110.method679(this.aClass110_851, Class110.method682(this.aClass110_851).getSelection());
        Class110.method647(this.aClass110_851);
    }
}
