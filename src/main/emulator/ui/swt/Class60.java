package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class60 extends SelectionAdapter {
    private final M3GViewUI aClass90_826;

    Class60(final M3GViewUI aClass90_826) {
        super();
        this.aClass90_826 = aClass90_826;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        M3GViewUI.method527(this.aClass90_826, M3GViewUI.method521(this.aClass90_826).getSelection());
    }
}
