package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class111 implements SelectionListener
{
    private final Class38 aClass38_1161;
    
    Class111(final Class38 aClass38_1161) {
        super();
        this.aClass38_1161 = aClass38_1161;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class38.method364(this.aClass38_1161).dispose();
    }
    
    public final void widgetDefaultSelected(final SelectionEvent selectionEvent) {
    }
}
