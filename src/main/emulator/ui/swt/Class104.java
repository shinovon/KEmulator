package emulator.ui.swt;

import emulator.*;
import org.eclipse.swt.graphics.*;

final class WindowOpen implements Runnable {
    int anInt1058;
    private final EmulatorScreen aClass93_1059;

    WindowOpen(final EmulatorScreen aClass93_1059, final int anInt1058) {
        super();
        this.aClass93_1059 = aClass93_1059;
        this.anInt1058 = 0;
        this.anInt1058 = anInt1058;
    }

    public final void run() {
        switch (this.anInt1058) {
            case 0: {
                if (Settings.showMemViewFrame) {
                    this.aClass93_1059.memoryViewMenuItem.setSelection(true);
                    ((EmulatorImpl) Emulator.getEmulator()).method823().method621();
                    return;
                }
                break;
            }
            case 1: {
                if (Settings.showLogFrame) {
                    this.aClass93_1059.logMenuItem.setSelection(true);
                    ((Class11) Emulator.getEmulator().getLogStream()).method329(EmulatorScreen.method561(this.aClass93_1059));
                    return;
                }
                break;
            }
            case 2: {
                if (Settings.showInfoFrame) {
                    this.aClass93_1059.infosMenuItem.setSelection(true);
                    EmulatorScreen.method558(this.aClass93_1059).setCursor(new Cursor(EmulatorScreen.method564(), 2));
                    ((EmulatorImpl) Emulator.getEmulator()).method825().method607(EmulatorScreen.method561(this.aClass93_1059));
                    break;
                }
                break;
            }
        }
    }
}
