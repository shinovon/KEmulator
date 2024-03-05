package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class86 extends SelectionAdapter {
    private final Class83 aClass83_883;

    Class86(final Class83 aClass83_883) {
        super();
        this.aClass83_883 = aClass83_883;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class83.method485(this.aClass83_883, Class83.method486(this.aClass83_883).getSelection());
    }
}
