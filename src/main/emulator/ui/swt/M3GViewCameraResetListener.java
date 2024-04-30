package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class M3GViewCameraResetListener extends SelectionAdapter {
    private final M3GViewUI aClass90_1209;

    M3GViewCameraResetListener(final M3GViewUI aClass90_1209) {
        super();
        this.aClass90_1209 = aClass90_1209;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        M3GViewUI.method519(this.aClass90_1209);
    }
}
