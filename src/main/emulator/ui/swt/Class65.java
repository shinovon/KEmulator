package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class65 implements ModifyListener {
    private final Property aClass38_831;

    Class65(final Property aClass38_831) {
        super();
        this.aClass38_831 = aClass38_831;
    }

    public final void modifyText(final ModifyEvent modifyEvent) {
        final boolean b = Property.method383(this.aClass38_831).getSelectionIndex() != 0;
        Property.method423(this.aClass38_831).setEnabled(b);
        Property.method425(this.aClass38_831).setEnabled(b);
        Property.method427(this.aClass38_831).setEnabled(b);
        Property.method429(this.aClass38_831).setEnabled(b);
        Property.method431(this.aClass38_831).setEnabled(b);
        Property.method357(this.aClass38_831).setEnabled(b);
        Property.method395(this.aClass38_831);
    }
}
