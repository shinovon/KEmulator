package emulator.ui.swt;

final class Class40 implements Runnable
{
    private final EmulatorScreen aClass93_776;
    
    Class40(final EmulatorScreen aClass93_776) {
        super();
        this.aClass93_776 = aClass93_776;
    }
    
    public final void run() {
        EmulatorScreen.method574(this.aClass93_776).setText(EmulatorScreen.method573(this.aClass93_776));
    }
}
