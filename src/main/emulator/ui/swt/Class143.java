package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;

final class Class143 extends SelectionAdapter {
    private final Class90 aClass90_1302;

    Class143(final Class90 aClass90_1302) {
        super();
        this.aClass90_1302 = aClass90_1302;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final Class30 class30;
        (class30 = new Class30(Class90.method499(this.aClass90_1302), 2)).setText(UILocale.get("M3G_VIEW_CAMEAR_POSITION", "Camera Position"));
        class30.method339(Class90.method525(this.aClass90_1302), Class90.method532(this.aClass90_1302), Class90.method537(this.aClass90_1302));
        class30.method340();
        Class90.method526(this.aClass90_1302, class30.aFloat610);
        Class90.method533(this.aClass90_1302, class30.aFloat612);
        Class90.method538(this.aClass90_1302, class30.aFloat614);
    }
}
