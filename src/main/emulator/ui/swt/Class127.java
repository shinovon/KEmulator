package emulator.ui.swt;

final class DoUpdate implements Runnable
{
    private final Class110 aClass110_1210;
    
    private DoUpdate(final Class110 aClass110_1210) {
        super();
        this.aClass110_1210 = aClass110_1210;
    }
    
    public final void run() {
        Class110.method668(this.aClass110_1210);
    }
    
    DoUpdate(final Class110 class110, final Class129 class111) {
        this(class110);
    }
}
