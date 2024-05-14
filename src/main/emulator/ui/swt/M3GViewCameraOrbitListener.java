package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class M3GViewCameraOrbitListener extends SelectionAdapter {
    private final M3GViewUI aClass90_1052;

    M3GViewCameraOrbitListener(final M3GViewUI aClass90_1052) {
        super();
        this.aClass90_1052 = aClass90_1052;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        M3GViewUI.setCameraMode(this.aClass90_1052, 0);
    }
}
