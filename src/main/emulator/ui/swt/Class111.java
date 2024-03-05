package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class111 implements SelectionListener {
    private final Property aClass38_1161;

    Class111(final Property aClass38_1161) {
        super();
        this.aClass38_1161 = aClass38_1161;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Property.method364(this.aClass38_1161).dispose();
    }

    public final void widgetDefaultSelected(final SelectionEvent selectionEvent) {
    }
}
