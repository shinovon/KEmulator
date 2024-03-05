package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;
import emulator.UILocale;
import emulator.custom.h;

import java.io.*;
import java.util.*;

import org.eclipse.swt.widgets.*;

final class Class164 extends SelectionAdapter {
    private final Class46 aClass46_1432;

    Class164(final Class46 aClass46_1432) {
        super();
        this.aClass46_1432 = aClass46_1432;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Emulator.getEmulator().getScreen();
        EmulatorScreen.pauseStep();
        final FileDialog fileDialog;
        ((Dialog) (fileDialog = new FileDialog(Class46.method442(this.aClass46_1432), 8192))).setText(UILocale.get("METHOD_FRAME_SAVE_BYTECODE", "Save all the methods bytecode:"));
        fileDialog.setFileName("bytecode.txt");
        final String open;
        if ((open = fileDialog.open()) != null) {
            try {
                final PrintWriter printWriter = new PrintWriter(new FileOutputStream(open));
                final Enumeration<h.MethodInfo> elements = h.aHashtable1061.elements();
                while (elements.hasMoreElements()) {
                    printWriter.write(elements.nextElement().method705(true, true));
                }
                printWriter.close();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        ((EmulatorScreen) Emulator.getEmulator().getScreen()).resumeStep();
    }
}
