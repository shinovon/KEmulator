package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class22 implements ModifyListener
{
    private final Class110 aClass110_591;
    
    Class22(final Class110 aClass110_591) {
        super();
        this.aClass110_591 = aClass110_591;
    }
    
    public final void modifyText(final ModifyEvent modifyEvent) {
        Class110.method662(this.aClass110_591, Class110.method654(this.aClass110_591).getSelectionIndex());
        Class110.method661(this.aClass110_591);
        Class110.method668(this.aClass110_591);
    }
}
