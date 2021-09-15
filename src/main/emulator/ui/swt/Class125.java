package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class125 extends SelectionAdapter
{
    private final Class110 aClass110_1208;
    
    Class125(final Class110 aClass110_1208) {
        super();
        this.aClass110_1208 = aClass110_1208;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class110.method659(this.aClass110_1208, Class110.method666(this.aClass110_1208).getSelection());
        Class110.method647(this.aClass110_1208);
    }
}
