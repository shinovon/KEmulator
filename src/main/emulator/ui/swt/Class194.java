package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class194 extends SelectionAdapter
{
    private final Class38 aClass38_1498;
    
    Class194(final Class38 aClass38_1498) {
        super();
        this.aClass38_1498 = aClass38_1498;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        for (int i = 0; i < Class38.method367(this.aClass38_1498).getItemCount(); ++i) {
            Class38.method367(this.aClass38_1498).getItem(i).setChecked(true);
        }
    }
}
