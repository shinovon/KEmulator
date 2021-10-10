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
    	double i = MemoryView.method631(this.aClass110_620).getSelectionIndex();
    	if(i == 0) {
    		i = 0.5d;
    	}
        MemoryView.method651(this.aClass110_620, i);
        MemoryView.method668(this.aClass110_620);
    }
}
