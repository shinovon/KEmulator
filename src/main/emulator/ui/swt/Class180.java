package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class180 extends KeyAdapter
{
    private final Class38 aClass38_1450;
    
    Class180(final Class38 aClass38_1450) {
        super();
        this.aClass38_1450 = aClass38_1450;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1450).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method407(this.aClass38_1450).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[10] = Class38.method407(this.aClass38_1450).getText().trim();
        }
    }
}
