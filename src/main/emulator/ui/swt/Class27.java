package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.debug.*;

final class Class27 extends SelectionAdapter
{
    private final Class110 aClass110_596;
    
    Class27(final Class110 aClass110_596) {
        super();
        this.aClass110_596 = aClass110_596;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final Object value;
        if (Class110.method649(this.aClass110_596).getSelectionIndex() != -1 && Class110.method649(this.aClass110_596).getSelectionIndex() < Class110.method629(this.aClass110_596).aVector1465.size() && (value = Class110.method629(this.aClass110_596).aVector1465.get(Class110.method649(this.aClass110_596).getSelectionIndex())) != null) {
            a.method860(value, 2);
        }
    }
}
