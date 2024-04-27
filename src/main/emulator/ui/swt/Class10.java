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

        try {
            if (Class90.method232(Class90.Refresher.method464(this.a))) {
                if (!Class90.method242(Class90.Refresher.method464(this.a))) {
                    Class90.method511(Class90.Refresher.method464(this.a));
                    Class90.method236(Class90.Refresher.method464(this.a), true);
                    return;
                }
            } else {
                final Rectangle clientArea = Class90.method231(Class90.Refresher.method464(this.a)).getClientArea();
                final GC gc;
                (gc = new GC(Class90.method231(Class90.Refresher.method464(this.a)))).setBackground(Display.getCurrent().getSystemColor(2));
                gc.fillRectangle(clientArea);
                gc.setForeground(Display.getCurrent().getSystemColor(3));
                gc.drawString("M3GView init .....", clientArea.width >> 2, clientArea.height >> 2, true);
                gc.dispose();
            }

        } catch (Exception ignored) {}

    }
}
