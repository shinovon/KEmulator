package emulator.ui.swt;

import org.eclipse.swt.events.*;
import javax.microedition.lcdui.*;

final class Class123 extends SelectionAdapter
{
    private final Class110 aClass110_1206;
    
    Class123(final Class110 aClass110_1206) {
        super();
        this.aClass110_1206 = aClass110_1206;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        for (int i = 0; i < Class110.method629(this.aClass110_1206).aVector1461.size(); ++i) {
            ((Image)Class110.method629(this.aClass110_1206).aVector1461.get(i)).resetUsedRegion();
        }
        Class110.method647(this.aClass110_1206);
    }
}
