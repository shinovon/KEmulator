package emulator.ui.swt;

import emulator.*;

final class Class121 implements Runnable {
    private final CaretImpl i;

    Class121(final CaretImpl aClass67_1204) {
        super();
        this.i = aClass67_1204;
    }

    public final void run() {
        this.i.setCaretLocation(CaretImpl.caretX(this.i), CaretImpl.caretY(this.i));
        CaretImpl.caret(this.i).setVisible(true);
        this.i.setWindowZoom(CaretImpl.caretFloat(this.i));
        ((EmulatorScreen) Emulator.getEmulator().getScreen()).toggleMenuAccelerators(false);
    }
}
