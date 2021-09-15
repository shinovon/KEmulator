package emulator.ui.swt;

import emulator.*;

final class Class121 implements Runnable
{
    private final Class67 aClass67_1204;
    
    Class121(final Class67 aClass67_1204) {
        super();
        this.aClass67_1204 = aClass67_1204;
    }
    
    public final void run() {
        this.aClass67_1204.method468(Class67.method471(this.aClass67_1204), Class67.method475(this.aClass67_1204));
        Class67.method472(this.aClass67_1204).setVisible(true);
        this.aClass67_1204.method469(Class67.method473(this.aClass67_1204));
        if(!Settings.canvasKeyboard)
        ((EmulatorScreen)Emulator.getEmulator().getScreen()).method570(false);
    }
}
