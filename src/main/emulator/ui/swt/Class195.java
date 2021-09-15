package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;
import emulator.graphics2D.swt.*;
import emulator.graphics2D.swt.ImageSWT;

final class Class195 implements PaintListener
{
    private final Class38 aClass38_1499;
    
    Class195(final Class38 aClass38_1499) {
        super();
        this.aClass38_1499 = aClass38_1499;
    }
    
    public final void paintControl(final PaintEvent paintEvent) {
        if (Class38.method382(this.aClass38_1499) == null) {
            Class38.method369(this.aClass38_1499, 4);
        }
        if (Settings.g2d == 0) {
            ((ImageSWT)Class38.method382(this.aClass38_1499)).method12(paintEvent.gc, 0, 0);
            return;
        }
        if (Settings.g2d == 1) {
            ((emulator.graphics2D.awt.d)Class38.method382(this.aClass38_1499)).method12(paintEvent.gc, 0, 0);
        }
    }
}
