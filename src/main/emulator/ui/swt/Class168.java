package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class168 extends KeyAdapter
{
    private final Property aClass38_1437;
    
    Class168(final Property aClass38_1437) {
        super();
        this.aClass38_1437 = aClass38_1437;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Property.method376(this.aClass38_1437).getSelectionIndex() == 0 && Keyboard.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
            Property.method394(this.aClass38_1437).setText(Keyboard.keyToString(String.valueOf(keyEvent.keyCode)));
            Property.method365()[5] = Property.method394(this.aClass38_1437).getText().trim();
        }
    }
}
