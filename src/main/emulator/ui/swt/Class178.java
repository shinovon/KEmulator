package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class178 extends KeyAdapter {
    private final Property aClass38_1448;

    Class178(final Property aClass38_1448) {
        super();
        this.aClass38_1448 = aClass38_1448;
    }

    public final void keyPressed(final KeyEvent keyEvent) {
        keyEvent.keyCode &= 0xFEFFFFFF;
        if (Property.method376(this.aClass38_1448).getSelectionIndex() == 0 && KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)) != null) {
            Property.method415(this.aClass38_1448).setText(KeyMapping.keyToString(String.valueOf(keyEvent.keyCode)));
            Property.method365()[12] = Property.method415(this.aClass38_1448).getText().trim();
        }
    }
}
