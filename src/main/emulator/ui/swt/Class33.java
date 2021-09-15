package emulator.ui.swt;

import org.eclipse.swt.events.*;
import emulator.*;
import javax.microedition.lcdui.*;
import org.eclipse.swt.widgets.*;

final class Class33 extends SelectionAdapter
{
    private final Class110 aClass110_618;
    
    Class33(final Class110 aClass110_618) {
        super();
        this.aClass110_618 = aClass110_618;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        final DirectoryDialog directoryDialog;
        ((Dialog)(directoryDialog = new DirectoryDialog(Class110.method632(this.aClass110_618)))).setText(UILocale.uiText("MEMORY_VIEW_SAVE_ALL_IMAGES", "Save all images"));
        directoryDialog.setMessage(UILocale.uiText("MEMORY_VIEW_CHOOSE_DIRECTORY", "Choose a directory"));
        directoryDialog.setFilterPath(System.getProperty("user.dir"));
        final String open;
        if ((open = directoryDialog.open()) != null) {
            for (int i = 0; i < Class110.method638().size(); ++i) {
                try {
                    ((Image)Class110.method638().get(i)).getImpl().saveToFile(open + "/" + i + ".png");
                }
                catch (Exception ex) {}
            }
        }
    }
}
