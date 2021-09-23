package emulator.ui.swt;

import emulator.*;

final class Class85 implements Runnable
{
    Class85(final Class50 class50) {
        super();
    }
    
    public final void run() {
        Emulator.getCurrentDisplay().getCurrent().handleSoftKeyAction(Keyboard.soft2(), true);
    }
}
