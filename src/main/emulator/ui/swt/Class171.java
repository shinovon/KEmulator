package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class171 extends KeyAdapter
{
    private final Class38 aClass38_1440;
    
    Class171(final Class38 aClass38_1440) {
        super();
        this.aClass38_1440 = aClass38_1440;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1440).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method401(this.aClass38_1440).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[8] = Class38.method401(this.aClass38_1440).getText().trim();
        }
    }
}
