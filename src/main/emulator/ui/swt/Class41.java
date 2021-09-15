package emulator.ui.swt;

final class Class41 implements Runnable
{
    private final EmulatorScreen aClass93_777;
    
    Class41(final EmulatorScreen aClass93_777) {
        super();
        this.aClass93_777 = aClass93_777;
    }
    
    public final void run() {
        EmulatorScreen.method563(this.aClass93_777).setText(EmulatorScreen.method560(this.aClass93_777));
    }
}
