package emulator.ui.swt;

import emulator.*;

final class Class115 implements Runnable
{
    private final Class67 aClass67_1166;
    
    Class115(final Class67 aClass67_1166) {
        super();
        this.aClass67_1166 = aClass67_1166;
    }
    
    public final void run() {
        Class67.method472(this.aClass67_1166).setVisible(false);
        if(!Settings.canvasKeyboard)
        	((EmulatorScreen)Emulator.getEmulator().getScreen()).method570(true);
    }
}
