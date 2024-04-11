package emulator;

import emulator.debug.MemoryViewImage;

public interface IEmulatorPlatform {
    public boolean isX64();

    String getName();

    String getTitleName();

    public String getInfoString(String version);

    public void loadLibraries();

    boolean supportsM3G();

    boolean supportsMascotCapsule();

    MemoryViewImage convertMicro3DTexture(Object o);
}
