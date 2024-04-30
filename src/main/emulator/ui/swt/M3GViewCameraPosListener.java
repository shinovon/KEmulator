package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class M3GViewCameraPosListener extends SelectionAdapter {
    private final M3GViewUI aClass90_1302;

    M3GViewCameraPosListener(final M3GViewUI aClass90_1302) {
        super();
        this.aClass90_1302 = aClass90_1302;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final Class30 class30;
        (class30 = new Class30(M3GViewUI.method499(this.aClass90_1302), 2)).setText(UILocale.get("M3G_VIEW_CAMEAR_POSITION", "Camera Position"));
        class30.method339(M3GViewUI.method525(this.aClass90_1302), M3GViewUI.method532(this.aClass90_1302), M3GViewUI.method537(this.aClass90_1302));
        class30.method340();
        M3GViewUI.method526(this.aClass90_1302, class30.aFloat610);
        M3GViewUI.method533(this.aClass90_1302, class30.aFloat612);
        M3GViewUI.method538(this.aClass90_1302, class30.aFloat614);
    }
}
