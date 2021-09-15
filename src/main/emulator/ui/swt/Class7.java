package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.debug.*;

final class Class7 extends SelectionAdapter
{
    private final Class110 aClass110_563;
    
    Class7(final Class110 aClass110_563) {
        super();
        this.aClass110_563 = aClass110_563;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final Object value;
        if (Class110.method649(this.aClass110_563).getSelectionIndex() != -1 && Class110.method649(this.aClass110_563).getSelectionIndex() < Class110.method629(this.aClass110_563).aVector1465.size() && (value = Class110.method629(this.aClass110_563).aVector1465.get(Class110.method649(this.aClass110_563).getSelectionIndex())) != null) {
            a.method860(value, 0);
        }
    }
}
