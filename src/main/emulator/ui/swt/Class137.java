package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class137 extends KeyAdapter {
    private final Property aClass38_1296;

    Class137(final Property aClass38_1296) {
        super();
        this.aClass38_1296 = aClass38_1296;
    }

    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Property.method376(this.aClass38_1296).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
            Property.method377(this.aClass38_1296).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
            Property.method365()[18] = Property.method377(this.aClass38_1296).getText().trim();
        }
    }
}
