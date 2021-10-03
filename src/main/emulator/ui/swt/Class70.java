package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class70 extends SelectionAdapter
{
    private final MemoryView aClass110_853;
    
    Class70(final MemoryView aClass110_853) {
        super();
        this.aClass110_853 = aClass110_853;
    }
    
    public final void widgetSelected(final SelectionEvent selectionEvent) {
        if (MemoryView.method648(this.aClass110_853).getSelection()) {
            final long long1;
            if ((long1 = Long.parseLong(MemoryView.method652(this.aClass110_853).getText().trim())) > 0L) {
                if (MemoryView.method653(this.aClass110_853) != null) {
                    MemoryView.method653(this.aClass110_853).aBoolean885 = false;
                }
                MemoryView.method633(this.aClass110_853, this.aClass110_853.new AutoUpdate(aClass110_853, long1));
                MemoryView.method635(this.aClass110_853, new Thread(MemoryView.method653(this.aClass110_853)));
                MemoryView.method626(this.aClass110_853).start();
            }
            return;
        }
        if (MemoryView.method653(this.aClass110_853) != null) {
            MemoryView.method653(this.aClass110_853).aBoolean885 = false;
            if (MemoryView.method626(this.aClass110_853).isAlive()) {
                MemoryView.method626(this.aClass110_853).interrupt();
            }
        }
    }
}
