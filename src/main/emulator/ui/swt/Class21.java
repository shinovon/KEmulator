package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.debug.*;

final class Class21 extends SelectionAdapter
{
    private final Class110 aClass110_590;
    
    Class21(final Class110 aClass110_590) {
        super();
        this.aClass110_590 = aClass110_590;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class110.method624(this.aClass110_590).setText(String.valueOf(Class110.method630(this.aClass110_590).getSelection()));
        final Object value;
        if (Class110.method649(this.aClass110_590).getSelectionIndex() != -1 && Class110.method649(this.aClass110_590).getSelectionIndex() < Class110.method629(this.aClass110_590).aVector1465.size() && (value = Class110.method629(this.aClass110_590).aVector1465.get(Class110.method649(this.aClass110_590).getSelectionIndex())) != null) {
            a.method853(value, Class110.method630(this.aClass110_590).getSelection());
        }
    }
}
