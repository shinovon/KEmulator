package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class184 extends SelectionAdapter
{
    private final Class38 aClass38_1455;
    
    Class184(final Class38 aClass38_1455) {
        super();
        this.aClass38_1455 = aClass38_1455;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Controllers.method750(false);
        Controllers.method750(true);
        Class38.method386(this.aClass38_1455);
    }
}
