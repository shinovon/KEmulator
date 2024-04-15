package emulator.custom.subclass;

import emulator.debug.Memory;

public class M3GDebug {

    public static void waitDebug() {
        if(!Memory.lockM3GObjects) return;
        try {
            synchronized (Memory.debugLock) {
                Memory.debugLock.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
