package emulator.ui.swt;

import org.eclipse.swt.events.*;
import java.io.*;

final class Class103 extends SelectionAdapter
{
    private final Class38 aClass38_1057;
    
    Class103(final Class38 aClass38_1057) {
        super();
        this.aClass38_1057 = aClass38_1057;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final String string = Class38.method368(this.aClass38_1057) + ".";
        for (int i = 0; i < Class38.method367(this.aClass38_1057).getItemCount(); ++i) {
            final File file;
            if (Class38.method367(this.aClass38_1057).getItem(i).getChecked() && (file = new File(string + Class38.method367(this.aClass38_1057).getItem(i).getText().trim())).exists()) {
                final File[] listFiles = file.listFiles();
                for (int j = 0; j < listFiles.length; ++j) {
                    listFiles[j].delete();
                }
                file.delete();
            }
        }
        Class38.method389(this.aClass38_1057);
    }
}
