package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class34 extends SelectionAdapter
{
    private final Class110 aClass110_619;
    
    Class34(final Class110 aClass110_619) {
        super();
        this.aClass110_619 = aClass110_619;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        try {
            Class110.method641(this.aClass110_619, Class110.method664(this.aClass110_619).getSelection());
        }
        catch (Exception ex) {}
    }
}
