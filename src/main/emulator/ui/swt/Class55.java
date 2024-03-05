package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class55 extends SelectionAdapter {
    private final EmulatorScreen aClass93_821;

    Class55(final EmulatorScreen aClass93_821) {
        super();
        this.aClass93_821 = aClass93_821;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        EmulatorScreen.method562(this.aClass93_821, 1);
        this.aClass93_821.interposeNearestMenuItem.setSelection(false);
        this.aClass93_821.interposeLowMenuItem.setSelection(true);
        this.aClass93_821.interposeHightMenuItem.setSelection(false);
    }
}
