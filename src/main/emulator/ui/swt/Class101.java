package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;
import org.eclipse.swt.widgets.*;

final class Class101 extends SelectionAdapter {
    private final Property aClass38_1055;

    Class101(final Property aClass38_1055) {
        super();
        this.aClass38_1055 = aClass38_1055;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Emulator.getEmulator().getScreen();
        EmulatorScreen.pauseStep();
        final DirectoryDialog directoryDialog;
        ((Dialog) (directoryDialog = new DirectoryDialog(Property.method364(this.aClass38_1055)))).setText(UILocale.get("OPTION_RECORDS_OPEN_DIRECTORY", "Select a directory for rms"));
        directoryDialog.setMessage(UILocale.get("OPTION_RECORDS_CHOOSE_DIR", "Choose a directory"));
        directoryDialog.setFilterPath(System.getProperty("user.dir"));
        final String open;
        if ((open = directoryDialog.open()) != null) {
            Property.method421(this.aClass38_1055).setText(open);
            Property.method389(this.aClass38_1055);
        }
        ((EmulatorScreen) Emulator.getEmulator().getScreen()).resumeStep();
    }
}
