package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class181 extends KeyAdapter
{
    private final Class38 aClass38_1451;
    
    Class181(final Class38 aClass38_1451) {
        super();
        this.aClass38_1451 = aClass38_1451;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1451).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method409(this.aClass38_1451).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[11] = Class38.method409(this.aClass38_1451).getText().trim();
        }
    }
}
