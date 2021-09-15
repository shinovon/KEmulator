package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class168 extends KeyAdapter
{
    private final Class38 aClass38_1437;
    
    Class168(final Class38 aClass38_1437) {
        super();
        this.aClass38_1437 = aClass38_1437;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1437).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method394(this.aClass38_1437).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[5] = Class38.method394(this.aClass38_1437).getText().trim();
        }
    }
}
