package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class13 extends MouseAdapter {
    private final MemoryView aClass110_578;

    Class13(final MemoryView aClass110_578) {
        super();
        this.aClass110_578 = aClass110_578;
    }

    public final void mouseDown(final MouseEvent mouseEvent) {
        if (mouseEvent.button == 3) {
            try {
                MemoryView.method658(this.aClass110_578, MemoryView.method670(this.aClass110_578).getSelection());
            } catch (Exception ignored) {
            }
        }
    }
}
