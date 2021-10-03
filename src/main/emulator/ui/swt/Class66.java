package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class66 extends SelectionAdapter
{
    private final MemoryView aClass110_834;
    
    Class66(final MemoryView aClass110_834) {
        super();
        this.aClass110_834 = aClass110_834;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        MemoryView.method667(this.aClass110_834, MemoryView.method673(this.aClass110_834).getSelection());
        MemoryView.method661(this.aClass110_834);
        MemoryView.method668(this.aClass110_834);
    }
}
