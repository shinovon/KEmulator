package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class M3GViewUpdateWorldListener extends SelectionAdapter {
    private final M3GViewUI aClass90_828;

    M3GViewUpdateWorldListener(final M3GViewUI aClass90_828) {
        super();
        this.aClass90_828 = aClass90_828;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        M3GViewUI.method529(this.aClass90_828);
    }
}
