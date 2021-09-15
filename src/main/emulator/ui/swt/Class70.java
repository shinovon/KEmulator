package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class70 extends SelectionAdapter
{
    private final Class110 aClass110_853;
    
    Class70(final Class110 aClass110_853) {
        super();
        this.aClass110_853 = aClass110_853;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        if (Class110.method648(this.aClass110_853).getSelection()) {
            final long long1;
            if ((long1 = Long.parseLong(Class110.method652(this.aClass110_853).getText().trim())) > 0L) {
                if (Class110.method653(this.aClass110_853) != null) {
                    Class110.method653(this.aClass110_853).aBoolean885 = false;
                }
                Class110.method633(this.aClass110_853, this.aClass110_853.new AutoUpdate(aClass110_853, long1));
                Class110.method635(this.aClass110_853, new Thread(Class110.method653(this.aClass110_853)));
                Class110.method626(this.aClass110_853).start();
            }
            return;
        }
        if (Class110.method653(this.aClass110_853) != null) {
            Class110.method653(this.aClass110_853).aBoolean885 = false;
            if (Class110.method626(this.aClass110_853).isAlive()) {
                Class110.method626(this.aClass110_853).interrupt();
            }
        }
    }
}
