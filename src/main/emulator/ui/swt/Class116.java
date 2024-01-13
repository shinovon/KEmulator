package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class116 extends SelectionAdapter
{
    private final Class90 aClass90_1167;
    
    Class116(final Class90 aClass90_1167) {
        super();
        this.aClass90_1167 = aClass90_1167;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final Class30 class30;
        (class30 = new Class30(Class90.method499(this.aClass90_1167), 1)).setText(UILocale.get("M3G_VIEW_CAMERA_CLIP_PLANES", "Clipping Planes"));
        class30.method338(Class90.method503(this.aClass90_1167), Class90.method512(this.aClass90_1167));
        class30.method340();
        if (Class90.method500(this.aClass90_1167) == 0) {
            if (class30.aFloat606 > 0.0f) {
                Class90.method506(this.aClass90_1167, class30.aFloat606);
            }
            if (class30.aFloat608 > 0.0f) {
                Class90.method513(this.aClass90_1167, class30.aFloat608);
            }
        }
        else {
            Class90.method506(this.aClass90_1167, class30.aFloat606);
            Class90.method513(this.aClass90_1167, class30.aFloat608);
        }
        Class90.method511(this.aClass90_1167);
    }
}
