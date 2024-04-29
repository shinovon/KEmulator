package emulator.ui.swt;

import emulator.Emulator;
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
            if(a.aClass90_830.canvas != null) {
                ((EmulatorScreen) Emulator.getEmulator().getScreen()).pollKeyboard(a.aClass90_830.canvas);
            }
            if (Class90.method232(a.aClass90_830)) {
                if (!Class90.method242(a.aClass90_830)) {
                    Class90.method511(a.aClass90_830);
                    Class90.method236(a.aClass90_830, true);
                    return;
                }
            } else {
                final Rectangle clientArea = Class90.method231(a.aClass90_830).getClientArea();
                final GC gc;
                (gc = new GC(Class90.method231(a.aClass90_830))).setBackground(Display.getCurrent().getSystemColor(2));
                gc.fillRectangle(clientArea);
                gc.setForeground(Display.getCurrent().getSystemColor(3));
                gc.drawString("M3GView init .....", clientArea.width >> 2, clientArea.height >> 2, true);
                gc.dispose();
            }

        } catch (Exception ignored) {}

    }
}
