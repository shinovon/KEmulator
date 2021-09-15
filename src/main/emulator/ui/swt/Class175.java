package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class175 extends KeyAdapter
{
    private final Class38 aClass38_1444;
    
    Class175(final Class38 aClass38_1444) {
        super();
        this.aClass38_1444 = aClass38_1444;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1444).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method405(this.aClass38_1444).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[0] = Class38.method405(this.aClass38_1444).getText().trim();
        }
    }
}
