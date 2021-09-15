package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class118 extends SelectionAdapter
{
    private final Class90 aClass90_1184;
    
    Class118(final Class90 aClass90_1184) {
        super();
        this.aClass90_1184 = aClass90_1184;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class90.method510(this.aClass90_1184, 1);
        Class90.method505(this.aClass90_1184).setEnabled(false);
        Class90.method511(this.aClass90_1184);
    }
}
