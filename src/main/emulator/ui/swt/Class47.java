package emulator.ui.swt;

import emulator.*;

final class Class47 implements Runnable
{
    Class47(final Class43 class43) {
        super();
    }
    
    public final void run() {
        Emulator.getCurrentDisplay().getCurrent().handleSoftKeyAction(Keyboard.method598(), true);
    }
}
