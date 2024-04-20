package emulator.ui.swt;

import org.eclipse.swt.widgets.*;

final class Class58 implements Listener {
    private final Class90 aClass90_824;

    Class58(final Class90 aClass90_824) {
        super();
        this.aClass90_824 = aClass90_824;
    }

    public final void handleEvent(final Event event) {
        switch (event.type) {
            case 12: {
                Class90.method243(this.aClass90_824, false);
                break;
            }
        }
    }
}
