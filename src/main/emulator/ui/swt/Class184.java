package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class184 extends SelectionAdapter
{
    private final Property aClass38_1455;
    
    Class184(final Property aClass38_1455) {
        super();
        this.aClass38_1455 = aClass38_1455;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Controllers.refresh(true);
        Property.method386(this.aClass38_1455);
    }
}
