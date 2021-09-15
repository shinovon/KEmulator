package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class183 extends KeyAdapter
{
    private final Class38 aClass38_1454;
    
    Class183(final Class38 aClass38_1454) {
        super();
        this.aClass38_1454 = aClass38_1454;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Class38.method376(this.aClass38_1454).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Class38.method417(this.aClass38_1454).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Class38.method365()[13] = Class38.method417(this.aClass38_1454).getText().trim();
        }
    }
}
