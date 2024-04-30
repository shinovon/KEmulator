package emulator.ui.swt;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;

final class Class6 extends TreeAdapter {
    private final Class5 aClass5_562;

    Class6(final Class5 aClass5_562) {
        super();
        this.aClass5_562 = aClass5_562;
    }

    public final void treeExpanded(final TreeEvent treeEvent) {
        if (!((TreeItem) treeEvent.item).getExpanded()) {
            Class5.method316(this.aClass5_562, (TreeItem) treeEvent.item);
            EmulatorImpl.asyncExec(Class5.method308(this.aClass5_562));
        }
    }
}
