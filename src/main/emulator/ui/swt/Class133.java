package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class133 extends KeyAdapter
{
    private final Class38 aClass38_1285;
    
    Class133(final Class38 aClass38_1285) {
        super();
        this.aClass38_1285 = aClass38_1285;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1285).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method385(this.aClass38_1285).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[2] = Class38.method385(this.aClass38_1285).getText().trim();
        }
    }
}
