package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class135 extends KeyAdapter
{
    private final Class38 aClass38_1294;
    
    Class135(final Class38 aClass38_1294) {
        super();
        this.aClass38_1294 = aClass38_1294;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1294).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method380(this.aClass38_1294).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[1] = Class38.method380(this.aClass38_1294).getText().trim();
        }
    }
}
