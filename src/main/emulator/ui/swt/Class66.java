package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class66 extends SelectionAdapter
{
    private final Class110 aClass110_834;
    
    Class66(final Class110 aClass110_834) {
        super();
        this.aClass110_834 = aClass110_834;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class110.method667(this.aClass110_834, Class110.method673(this.aClass110_834).getSelection());
        Class110.method661(this.aClass110_834);
        Class110.method668(this.aClass110_834);
    }
}
