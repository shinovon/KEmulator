package emulator.ui.swt;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

final class Class26 implements PaintListener
{
    private final Class110 aClass110_595;
    
    Class26(final Class110 aClass110_595) {
        super();
        this.aClass110_595 = aClass110_595;
    }
    
    public final void paintControl(final PaintEvent paintEvent) {
        Class110.method669(this.aClass110_595, ((Control)Class110.method642(this.aClass110_595)).getSize().x);
        Class110.method675(this.aClass110_595, ((Control)Class110.method642(this.aClass110_595)).getSize().y);
        Class110.method680(this.aClass110_595, ((Scrollable)Class110.method642(this.aClass110_595)).getVerticalBar().getSelection());
        Class110.method650(this.aClass110_595, paintEvent.gc);
    }
}
