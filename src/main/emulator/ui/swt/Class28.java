package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.debug.*;

final class Class28 extends SelectionAdapter
{
    private final Class110 aClass110_597;
    
    Class28(final Class110 aClass110_597) {
        super();
        this.aClass110_597 = aClass110_597;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final Object value;
        if (Class110.method649(this.aClass110_597).getSelectionIndex() != -1 && Class110.method649(this.aClass110_597).getSelectionIndex() < Class110.method629(this.aClass110_597).aVector1465.size() && (value = Class110.method629(this.aClass110_597).aVector1465.get(Class110.method649(this.aClass110_597).getSelectionIndex())) != null) {
            a.method860(value, 1);
        }
    }
}
