package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class182 extends KeyAdapter
{
    private final Class38 aClass38_1453;
    
    Class182(final Class38 aClass38_1453) {
        super();
        this.aClass38_1453 = aClass38_1453;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1453).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method419(this.aClass38_1453).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[16] = Class38.method419(this.aClass38_1453).getText().trim();
        }
    }
}
