package emulator.ui.swt;

import emulator.*;

final class Class115 implements Runnable
{
    private final CaretImpl aClass67_1166;
    
    Class115(final CaretImpl aClass67_1166) {
        super();
        this.aClass67_1166 = aClass67_1166;
    }
    
    public final void run() {
        CaretImpl.caret(this.aClass67_1166).setVisible(false);
        if(!Settings.canvasKeyboard)
        	((EmulatorScreen)Emulator.getEmulator().getScreen()).toggleMenuAccelerators(true);
    }
}
