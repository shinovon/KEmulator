package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class177 extends KeyAdapter
{
    private final Property aClass38_1446;
    
    Class177(final Property aClass38_1446) {
        super();
        this.aClass38_1446 = aClass38_1446;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Property.method376(this.aClass38_1446).getSelectionIndex() == 0 && Keyboard.method594(String.valueOf(keyEvent.keyCode)) != null) {
            Property.method391(this.aClass38_1446).setText(Keyboard.method594(String.valueOf(keyEvent.keyCode)));
            Property.method365()[4] = Property.method391(this.aClass38_1446).getText().trim();
        }
    }
}
