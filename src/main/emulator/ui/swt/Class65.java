package emulator.ui.swt;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

final class Class65 implements ModifyListener
{
    private final Class38 aClass38_831;
    
    Class65(final Class38 aClass38_831) {
        super();
        this.aClass38_831 = aClass38_831;
    }
    
    public final void modifyText(final ModifyEvent modifyEvent) {
        final boolean b = Class38.method383(this.aClass38_831).getSelectionIndex() != 0;
        ((Control)Class38.method423(this.aClass38_831)).setEnabled(b);
        ((Control)Class38.method425(this.aClass38_831)).setEnabled(b);
        ((Control)Class38.method427(this.aClass38_831)).setEnabled(b);
        ((Control)Class38.method429(this.aClass38_831)).setEnabled(b);
        ((Control)Class38.method431(this.aClass38_831)).setEnabled(b);
        ((Control)Class38.method357(this.aClass38_831)).setEnabled(b);
        Class38.method395(this.aClass38_831);
    }
}
