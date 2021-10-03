package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class35 implements ModifyListener
{
    private final MemoryView aClass110_620;
    
    Class35(final MemoryView aClass110_620) {
        super();
        this.aClass110_620 = aClass110_620;
    }
    
    public final void modifyText(final ModifyEvent modifyEvent) {
        MemoryView.method651(this.aClass110_620, MemoryView.method631(this.aClass110_620).getSelectionIndex() + 1);
        MemoryView.method668(this.aClass110_620);
    }
}
