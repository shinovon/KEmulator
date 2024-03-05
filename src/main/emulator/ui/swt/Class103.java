package emulator.ui.swt;

import org.eclipse.swt.events.*;

import javax.microedition.rms.RecordStore;
import java.io.*;

final class Class103 extends SelectionAdapter {
    private final Property aClass38_1057;

    Class103(final Property aClass38_1057) {
        super();
        this.aClass38_1057 = aClass38_1057;
    }

    public final void widgetSelected(final SelectionEvent selectionEvent) {
        // TODO: rms delete
        final String string = Property.method368(this.aClass38_1057) + ".";
        for (int i = 0; i < Property.method367(this.aClass38_1057).getItemCount(); ++i) {
            final File file;
            if (Property.method367(this.aClass38_1057).getItem(i).getChecked()) {
                try {
                    RecordStore.deleteRecordStore(Property.method367(this.aClass38_1057).getItem(i).getText().trim());
                } catch (Exception e) {

                }
            }
        }
        Property.method389(this.aClass38_1057);
    }
}
