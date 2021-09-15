package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class107 extends KeyAdapter
{
    private final Class38 aClass38_1068;
    
    Class107(final Class38 aClass38_1068) {
        super();
        this.aClass38_1068 = aClass38_1068;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1068).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method366(this.aClass38_1068).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[17] = Class38.method366(this.aClass38_1068).getText().trim();
        }
    }
}
