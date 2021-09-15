package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class53 extends ShellAdapter
{
    private final EmulatorScreen aClass93_804;
    
    Class53(final EmulatorScreen aClass93_804) {
        super();
        this.aClass93_804 = aClass93_804;
    }
    
    public final void shellDeactivated(final ShellEvent shellEvent) {
        for (int i = EmulatorScreen.method556(this.aClass93_804).length - 1; i >= 0; --i) {
            if (EmulatorScreen.method556(this.aClass93_804)[i]) {
                this.aClass93_804.handleKeyRelease(i);
            }
        }
    }
}
