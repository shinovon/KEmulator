package emulator.ui.swt;

import emulator.Emulator;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

final class Class10 implements Runnable {
    private final M3GViewUI.Refresher a;

    Class10(final M3GViewUI.Refresher aClass64_567) {
        super();
        this.a = aClass64_567;
    }

    public final void run() {
        try {
            if(a.aClass90_830.canvas != null) {
                ((EmulatorScreen) Emulator.getEmulator().getScreen()).pollKeyboard(a.aClass90_830.canvas);
            }
            if (M3GViewUI.method232(a.aClass90_830)) {
                if (!M3GViewUI.method242(a.aClass90_830)) {
                    M3GViewUI.method511(a.aClass90_830);
                    a.aClass90_830.aBoolean909 = true;
                    return;
                }
            } else {
                final Rectangle clientArea = a.aClass90_830.canvas.getClientArea();
                final GC gc;
                (gc = new GC(a.aClass90_830.canvas)).setBackground(Display.getCurrent().getSystemColor(2));
                gc.fillRectangle(clientArea);
                gc.setForeground(Display.getCurrent().getSystemColor(3));
                gc.drawString("M3GView init .....", clientArea.width >> 2, clientArea.height >> 2, true);
                gc.dispose();
            }

        } catch (Exception ignored) {}

    }
}
