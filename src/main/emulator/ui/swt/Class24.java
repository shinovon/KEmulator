package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.debug.*;

final class Class24 extends SelectionAdapter
{
    private final Class110 aClass110_593;
    
    Class24(final Class110 aClass110_593) {
        super();
        this.aClass110_593 = aClass110_593;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final Object value;
        if (Class110.method649(this.aClass110_593).getSelectionIndex() != -1 && Class110.method649(this.aClass110_593).getSelectionIndex() < Class110.method629(this.aClass110_593).aVector1465.size() && (value = Class110.method629(this.aClass110_593).aVector1465.get(Class110.method649(this.aClass110_593).getSelectionIndex())) != null) {
            Class110.method630(this.aClass110_593).setSelection(a.method865(value));
            Class110.method624(this.aClass110_593).setText(String.valueOf(Class110.method630(this.aClass110_593).getSelection()));
            Class110.method646(this.aClass110_593).setSelection(a.method863(value));
        }
    }
}
