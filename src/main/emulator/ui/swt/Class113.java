package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class113 implements SelectionListener
{
    private final Class38 aClass38_1163;
    
    Class113(final Class38 aClass38_1163) {
        super();
        this.aClass38_1163 = aClass38_1163;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        Class38.method375(this.aClass38_1163);
        this.aClass38_1163.saveProperties();
        Class38.method364(this.aClass38_1163).dispose();
    }
    
    public final void widgetDefaultSelected(final SelectionEvent selectionEvent) {
    }
}
