package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class37 extends SelectionAdapter {
    private final Class90 aClass90_625;

    Class37(final Class90 aClass90_625) {
        super();
        this.aClass90_625 = aClass90_625;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final Class30 class30;
        (class30 = new Class30(Class90.method499(this.aClass90_625), 0)).setText(UILocale.get("M3G_VIEW_CAMEAR_FIELD_OF_VIEW", "Field of View"));
        class30.method337(Class90.method517(this.aClass90_625));
        class30.method340();
        if (class30.aFloat603 > 0.0f) {
            if (Class90.method500(this.aClass90_625) == 0) {
                if (class30.aFloat603 < 180.0f) {
                    Class90.method518(this.aClass90_625, class30.aFloat603);
                }
            } else {
                Class90.method518(this.aClass90_625, class30.aFloat603);
            }
            Class90.method252(this.aClass90_625);
        }
    }
}
