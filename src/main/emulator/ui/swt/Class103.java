package emulator.ui.swt;

import org.eclipse.swt.events.*;
import java.io.*;

final class Class103 extends SelectionAdapter
{
    private final Property aClass38_1057;
    
    Class103(final Property aClass38_1057) {
        super();
        this.aClass38_1057 = aClass38_1057;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final String string = Property.method368(this.aClass38_1057) + ".";
        for (int i = 0; i < Property.method367(this.aClass38_1057).getItemCount(); ++i) {
            final File file;
            if (Property.method367(this.aClass38_1057).getItem(i).getChecked() && (file = new File(string + Property.method367(this.aClass38_1057).getItem(i).getText().trim())).exists()) {
                final File[] listFiles = file.listFiles();
                for (int j = 0; j < listFiles.length; ++j) {
                    listFiles[j].delete();
                }
                file.delete();
            }
        }
        Property.method389(this.aClass38_1057);
    }
}
