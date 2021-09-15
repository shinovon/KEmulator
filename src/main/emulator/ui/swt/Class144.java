package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class144 extends SelectionAdapter
{
    private final Class110 aClass110_1303;
    
    Class144(final Class110 aClass110_1303) {
        super();
        this.aClass110_1303 = aClass110_1303;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class110.method627(this.aClass110_1303, Class110.method657(this.aClass110_1303).getSelection());
        Class110.method647(this.aClass110_1303);
    }
}
