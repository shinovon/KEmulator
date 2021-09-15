package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class172 extends KeyAdapter
{
    private final Class38 aClass38_1441;
    
    Class172(final Class38 aClass38_1441) {
        super();
        this.aClass38_1441 = aClass38_1441;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1441).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method399(this.aClass38_1441).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[7] = Class38.method399(this.aClass38_1441).getText().trim();
        }
    }
}
