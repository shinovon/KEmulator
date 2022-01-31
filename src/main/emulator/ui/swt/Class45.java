package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class45 extends SelectionAdapter
{
    private final int anInt782;
    
    Class45(final EmulatorScreen class93, final int anInt782) {
        super();
        this.anInt782 = anInt782;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Settings.recordedKeysFile = null;
        Emulator.loadGame(Settings.aArray[this.anInt782], Settings.g2d, Settings.g3d, false);
    }
}
