package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class171 extends KeyAdapter
{
    private final Property aClass38_1440;
    
    Class171(final Property aClass38_1440) {
        super();
        this.aClass38_1440 = aClass38_1440;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Property.method376(this.aClass38_1440).getSelectionIndex() == 0 && Keyboard.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
            Property.method401(this.aClass38_1440).setText(Keyboard.keyToString(String.valueOf(keyEvent.keyCode)));
            Property.method365()[8] = Property.method401(this.aClass38_1440).getText().trim();
        }
    }
}
