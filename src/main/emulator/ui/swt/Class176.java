package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class176 extends KeyAdapter
{
    private final Class38 aClass38_1445;
    
    Class176(final Class38 aClass38_1445) {
        super();
        this.aClass38_1445 = aClass38_1445;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1445).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method411(this.aClass38_1445).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[14] = Class38.method411(this.aClass38_1445).getText().trim();
        }
    }
}
