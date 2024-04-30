package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class61 extends SelectionAdapter {
    private final M3GViewUI aClass90_827;

    Class61(final M3GViewUI aClass90_827) {
        super();
        this.aClass90_827 = aClass90_827;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        M3GViewUI.method520(this.aClass90_827, M3GViewUI.method514(this.aClass90_827).getSelection());
    }
}
