package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class174 extends KeyAdapter
{
    private final Class38 aClass38_1443;
    
    Class174(final Class38 aClass38_1443) {
        super();
        this.aClass38_1443 = aClass38_1443;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1443).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method403(this.aClass38_1443).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[9] = Class38.method403(this.aClass38_1443).getText().trim();
        }
    }
}
