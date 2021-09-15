package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class141 implements ModifyListener
{
    private final Class5 aClass5_1300;
    
    Class141(final Class5 aClass5_1300) {
        super();
        this.aClass5_1300 = aClass5_1300;
    }
    
    public final void modifyText(final ModifyEvent modifyEvent) {
        if (Class5.method312(this.aClass5_1300).getSelection()) {
            Class5.method317(this.aClass5_1300);
            Class146.asyncExec(Class5.method308(this.aClass5_1300));
        }
    }
}
