package emulator;

import emulator.debug.MemoryViewImage;
import emulator.graphics2D.IImage;
import emulator.graphics3D.IGraphics3D;

import java.lang.reflect.Field;

public class EmulatorPlatform implements IEmulatorPlatform {

    public boolean isX64() {
        return false;
    }

    public String getName() {
        return "KEmulator nnmod";
    }

    public String getTitleName() {
        return "KEmnn";
    }

    public String getInfoString(String version) {
        return "KEmulator nnmod " + version + "\n\n\t" +
                UILocale.get("ABOUT_INFO_EMULATOR", "Mobile Game Emulator") + "\n\n" +
                UILocale.get("ABOUT_INFO_APIS", "Support APIs") + ":\n\n"
                + "\tMIDP 2.0 (JSR118)\n"
                + "\tNokiaUI 1.4\n"
                + "\tSamsung 1.0\n"
                + "\tSprint 1.0\n"
                + "\tWMA 1.0 (JSR120)\n"
                + "\tSensor (JSR256)\n"
                + "\tM3G 1.1 (JSR184)\n"
                + "\tOpenGL ES (JSR239)\n"
                + "\tMascot Capsule"
                ;
    }

    public void loadLibraries() {
        loadM3G();
    }

    private static void loadM3G() {
        boolean m3gLoaded = false;
        try {
            Class cls = Class.forName("javax.microedition.m3g.Graphics3D");
            Field f = null;
            try {
                f = cls.getField("_STUB");
                m3gLoaded = !f.getBoolean(null);
            } catch (Throwable ignored) {
                m3gLoaded = true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("m3g loaded: " + m3gLoaded);
    }

    public boolean supportsMascotCapsule() {
        return true;
    }

    public boolean supportsM3G() {
        return true;
    }

    public MemoryViewImage convertMicro3DTexture(Object o) {
        IImage img = ((com.mascotcapsule.micro3d.v3.Texture) o).debugImage;
        if(img == null)
            return null;
        return new MemoryViewImage(img);
    }

    public IGraphics3D getGraphics3D() {
        return emulator.graphics3D.lwjgl.Emulator3D.getInstance();
    }
}
