package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class M3GViewCameraDollyListener extends SelectionAdapter {
    private final M3GViewUI aClass90_1060;

    M3GViewCameraDollyListener(final M3GViewUI aClass90_1060) {
        super();
        this.aClass90_1060 = aClass90_1060;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        M3GViewUI.setCameraMode(this.aClass90_1060, 2);
    }
}
