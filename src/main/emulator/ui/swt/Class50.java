package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class50 extends MouseAdapter {
    private final EmulatorScreen aClass93_801;

    Class50(final EmulatorScreen aClass93_801) {
        super();
        this.aClass93_801 = aClass93_801;
    }

    public final void mouseDown(final MouseEvent mouseEvent) {
        if (EmulatorScreen.method566(this.aClass93_801) == 0 || Emulator.getCurrentDisplay().getCurrent() == null) {
            return;
        }
        new Thread(new Class85(this)).start();
    }
}
