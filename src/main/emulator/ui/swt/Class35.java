package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class35 implements ModifyListener
{
    private final Class110 aClass110_620;
    
    Class35(final Class110 aClass110_620) {
        super();
        this.aClass110_620 = aClass110_620;
    }
    
    public final void modifyText(final ModifyEvent modifyEvent) {
        Class110.method651(this.aClass110_620, Class110.method631(this.aClass110_620).getSelectionIndex() + 1);
        Class110.method668(this.aClass110_620);
    }
}
