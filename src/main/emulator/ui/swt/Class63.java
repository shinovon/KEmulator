package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class63 extends SelectionAdapter {
    private final M3GViewUI aClass90_829;

    Class63(final M3GViewUI aClass90_829) {
        super();
        this.aClass90_829 = aClass90_829;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        aClass90_829.setXray(M3GViewUI.method528(this.aClass90_829).getSelection());
    }
}
