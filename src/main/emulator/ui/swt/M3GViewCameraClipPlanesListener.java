package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class M3GViewCameraClipPlanesListener extends SelectionAdapter {
    private final M3GViewUI aClass90_1167;

    M3GViewCameraClipPlanesListener(final M3GViewUI aClass90_1167) {
        super();
        this.aClass90_1167 = aClass90_1167;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final Class30 class30;
        (class30 = new Class30(M3GViewUI.method499(this.aClass90_1167), 1)).setText(UILocale.get("M3G_VIEW_CAMERA_CLIP_PLANES", "Clipping Planes"));
        class30.method338(M3GViewUI.method503(this.aClass90_1167), M3GViewUI.method512(this.aClass90_1167));
        class30.method340();
        if (M3GViewUI.method500(this.aClass90_1167) == 0) {
            if (class30.aFloat606 > 0.0f) {
                M3GViewUI.method506(this.aClass90_1167, class30.aFloat606);
            }
            if (class30.aFloat608 > 0.0f) {
                M3GViewUI.method513(this.aClass90_1167, class30.aFloat608);
            }
        } else {
            M3GViewUI.method506(this.aClass90_1167, class30.aFloat606);
            M3GViewUI.method513(this.aClass90_1167, class30.aFloat608);
        }
        M3GViewUI.method252(this.aClass90_1167);
    }
}
