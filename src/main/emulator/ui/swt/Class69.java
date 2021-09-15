package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class69 extends SelectionAdapter
{
    private final Class110 aClass110_852;
    
    Class69(final Class110 aClass110_852) {
        super();
        this.aClass110_852 = aClass110_852;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class110.method674(this.aClass110_852, Class110.method678(this.aClass110_852).getSelection());
        Class110.method647(this.aClass110_852);
    }
}
