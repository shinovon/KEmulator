package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class173 extends KeyAdapter
{
    private final Property aClass38_1442;
    
    Class173(final Property aClass38_1442) {
        super();
        this.aClass38_1442 = aClass38_1442;
    }
    
    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Property.method376(this.aClass38_1442).getSelectionIndex() == 0 && Keyboard.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
            Property.method397(this.aClass38_1442).setText(Keyboard.keyToString(String.valueOf(keyEvent.keyCode)));
            Property.method365()[6] = Property.method397(this.aClass38_1442).getText().trim();
        }
    }
}
