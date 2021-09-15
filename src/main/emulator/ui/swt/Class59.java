package emulator.ui.swt;

import org.eclipse.swt.widgets.*;

final class Class59 implements Listener
{
    private final Class90 aClass90_825;
    
    Class59(final Class90 aClass90_825) {
        super();
        this.aClass90_825 = aClass90_825;
    }
    
    public final void handleEvent(final Event event) {
        switch (event.type) {
            case 37: {
                Class90.method542(this.aClass90_825, event.count);
                if (Class90.method517(this.aClass90_825) <= 0.0f) {
                    Class90.method544(this.aClass90_825, event.count);
                }
                else if (Class90.method517(this.aClass90_825) >= 180.0f && Class90.method500(this.aClass90_825) == 0) {
                    Class90.method544(this.aClass90_825, event.count);
                }
                Class90.method511(this.aClass90_825);
                break;
            }
        }
    }
}
