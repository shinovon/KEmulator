package emulator.ui.swt;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

final class Class25 extends MouseAdapter
{
    private final Class110 aClass110_594;
    
    Class25(final Class110 aClass110_594) {
        super();
        this.aClass110_594 = aClass110_594;
    }
    
    public final void mouseDown(final MouseEvent mouseEvent) {
        ((Control)Class110.method642(this.aClass110_594)).forceFocus();
        if (mouseEvent.button == 3) {
            Canvas canvas;
            Menu menu;
            if (Class110.method625(this.aClass110_594, mouseEvent.x, mouseEvent.y)) {
                canvas = Class110.method642(this.aClass110_594);
                menu = Class110.method643(this.aClass110_594);
            }
            else {
                canvas = Class110.method642(this.aClass110_594);
                menu = Class110.method663(this.aClass110_594);
            }
            ((Control)canvas).setMenu(menu);
        }
    }
}
