package emulator.ui.swt;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

final class Class10 implements Runnable {
    private final Class90.Refresher a;

    Class10(final Class90.Refresher aClass64_567) {
        super();
        this.a = aClass64_567;
    }

    public Class10(Refresher refresher) {
        super();
        this.a = null;
    }

    public final void run() {
		/*
        try {
            final Rectangle clientArea = ((Scrollable)Class90.method496(Class90.Refresher.method464(this.aClass64_567))).getClientArea();
            final GC gc;
            (gc = new GC((Drawable)Class90.method496(Class90.Refresher.method464(this.aClass64_567)))).setBackground(Display.getCurrent().getSystemColor(2));
            gc.fillRectangle(clientArea);
            gc.setForeground(Display.getCurrent().getSystemColor(3));
            gc.drawString("M3GView init .....", clientArea.width >> 2, clientArea.height >> 2, true);
            gc.dispose();
        }
        catch (Exception ex) {}*/

        try {
            if (Class90.method498(Class90.Refresher.method464(this.a))) {
                if (!Class90.method508(Class90.Refresher.method464(this.a))) {
                    Class90.method511(Class90.Refresher.method464(this.a));
                    Class90.method497(Class90.Refresher.method464(this.a), true);
                    return;
                }
            } else {
                Rectangle var1 = Class90.method496(Class90.Refresher.method464(this.a)).getClientArea();
                GC var2;
                (var2 = new GC(Class90.method496(Class90.Refresher.method464(this.a)))).setBackground(Display.getCurrent().getSystemColor(2));
                var2.fillRectangle(var1);
                var2.setForeground(Display.getCurrent().getSystemColor(3));
                var2.drawString("M3GView NOT WORKS!", var1.width / 3, var1.height / 3, true);
                var2.drawString("M3GView не работает!", var1.width / 3, (var1.height / 3) + 16, true);
                var2.dispose();
            }

        } catch (Exception var3) {
            ;
        }

    }
}
