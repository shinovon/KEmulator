package emulator;

import emulator.debug.MemoryViewImage;
import emulator.graphics3D.IGraphics3D;

public interface IEmulatorPlatform {
    public boolean isX64();

    String getName();

    String getTitleName();

    public String getInfoString(String version);

    public void loadLibraries();

    public void loadM3G();

    boolean supportsM3G();

    boolean supportsMascotCapsule();

    MemoryViewImage convertMicro3DTexture(Object o);

    IGraphics3D getGraphics3D();
}
