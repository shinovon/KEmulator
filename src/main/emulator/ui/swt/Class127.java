package emulator.ui.swt;

final class DoUpdate implements Runnable
{
    private final MemoryView aClass110_1210;
    
    private DoUpdate(final MemoryView aClass110_1210) {
        super();
        this.aClass110_1210 = aClass110_1210;
    }
    
    public final void run() {
        MemoryView.method668(this.aClass110_1210);
    }
    
    DoUpdate(final MemoryView class110, final Class129 class111) {
        this(class110);
    }
}
