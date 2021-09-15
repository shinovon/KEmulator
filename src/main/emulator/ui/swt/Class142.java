package emulator.ui.swt;

import org.eclipse.swt.widgets.*;

final class Textout implements Runnable
{
    private final Class11 aClass11_1301;
    
    private Textout(final Class11 aClass11_1301) {
        super();
        this.aClass11_1301 = aClass11_1301;
    }
    
    public final void run() {
        if (((Widget)Class11.method331(this.aClass11_1301)).isDisposed()) {
            return;
        }
        Class11.method331(this.aClass11_1301).append(Class11.aString572);
        Class11.method331(this.aClass11_1301).setTopIndex(Class11.method331(this.aClass11_1301).getLineCount());
    }
    
    Textout(final Class11 class11, final Class89 object) {
        this(class11);
    }
}
