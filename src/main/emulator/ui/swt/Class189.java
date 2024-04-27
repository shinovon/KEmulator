package emulator.ui.swt;

final class ShellPosition implements Runnable {
    int anInt1478;
    int anInt1481;
    boolean aBoolean1479;
    private final EmulatorScreen aClass93_1480;

    ShellPosition(final EmulatorScreen aClass93_1480, final int anInt1478, final int anInt1479, final boolean aBoolean1479) {
        super();
        this.aClass93_1480 = aClass93_1480;
        this.anInt1478 = anInt1478;
        this.anInt1481 = anInt1479;
        this.aBoolean1479 = aBoolean1479;
    }

    public final void run() {
        if (this.aBoolean1479) {
            this.anInt1478 = EmulatorScreen.method561(this.aClass93_1480).getLocation().x;
            this.anInt1481 = EmulatorScreen.method561(this.aClass93_1480).getLocation().y;
            return;
        }
        EmulatorScreen.method561(this.aClass93_1480).setLocation(this.anInt1478, this.anInt1481);
    }
}
