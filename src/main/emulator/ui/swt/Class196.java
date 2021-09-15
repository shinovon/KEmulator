package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;
import emulator.graphics2D.swt.*;
import emulator.graphics2D.swt.ImageSWT;

final class Class196 implements PaintListener
{
    private final Property aClass38_1500;
    
    Class196(final Property aClass38_1500) {
        super();
        this.aClass38_1500 = aClass38_1500;
    }
    
    public final void paintControl(final PaintEvent paintEvent) {
        if (Property.method378(this.aClass38_1500) == null) {
            Property.method369(this.aClass38_1500, 2);
        }
        if (Settings.g2d == 0) {
            ((ImageSWT)Property.method378(this.aClass38_1500)).method12(paintEvent.gc, 0, 0);
            return;
        }
        if (Settings.g2d == 1) {
            ((emulator.graphics2D.awt.d)Property.method378(this.aClass38_1500)).method12(paintEvent.gc, 0, 0);
        }
    }
}
