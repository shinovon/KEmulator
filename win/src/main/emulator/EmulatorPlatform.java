package emulator;

import emulator.debug.MemoryViewImage;
import emulator.graphics2D.IImage;
import emulator.graphics3D.IGraphics3D;
import org.eclipse.swt.internal.opengl.win32.PIXELFORMATDESCRIPTOR;
import org.eclipse.swt.internal.opengl.win32.WGL;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

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
                + "\tMascot Capsule V3\n"
                + "\tSoftBank MEXA"
                ;
    }

    public void loadLibraries() {
        System.setProperty("org.lwjgl.librarypath", Emulator.getAbsolutePath());
    }

    public boolean supportsMascotCapsule() {
        return true;
    }

    public boolean supportsM3G() {
        return true;
    }

    public MemoryViewImage convertMicro3DTexture(Object o) {
        IImage img = ((com.mascotcapsule.micro3d.v3.Texture) o).debugImage;
        if (img == null)
            return null;
        return new MemoryViewImage(img);
    }

    public IGraphics3D getGraphics3D() {
        return emulator.graphics3D.lwjgl.Emulator3D.getInstance();
    }

    public int createGLContext(int gcHandle) {
        PIXELFORMATDESCRIPTOR var4;
        (var4 = new PIXELFORMATDESCRIPTOR()).nSize = 40;
        var4.nVersion = 1;
        var4.dwFlags = 57;
        var4.iPixelType = 0;
        var4.cColorBits = (byte) Emulator.getEmulator().getScreenDepth();
        var4.iLayerType = 0;
        int var5;
        if ((var5 = WGL.ChoosePixelFormat(gcHandle, var4)) == 0 || !WGL.SetPixelFormat(gcHandle, var5, var4)) {
            return 0;
        }

        return WGL.wglCreateContext(gcHandle);
    }

    public boolean isGLContextCurrent(int imgHandle) {
        return WGL.wglGetCurrentContext() == imgHandle;
    }

    public void setGLContextCurrent(int gcHandle, int contextHandle) {
        while (WGL.wglGetCurrentContext() > 0);
        WGL.wglMakeCurrent(gcHandle, contextHandle);
    }

    public void releaseGLContext(int gcHandle) {
        WGL.wglMakeCurrent(gcHandle, -1);
    }

    public void deleteGLContext(int contextHandle) {
        WGL.wglDeleteContext(contextHandle);
    }

    public void loadM3G() {
        if (!supportsM3G()) return;
        boolean m3gLoaded = false;
        try {
            Class cls = Class.forName("javax.microedition.m3g.Graphics3D");
            Field f = null;
            try {
                f = cls.getField("_STUB");
                m3gLoaded = !f.getBoolean(null);
                if (!m3gLoaded) {
                    System.out.println("m3g stub!!");
                }
            } catch (Throwable ignored) {
                m3gLoaded = true;
            }
        } catch (Throwable ignored) {}
        if (!m3gLoaded) {
            addToClassPath(Settings.g3d == 0 ? "m3g_swerve.jar" : "m3g_lwjgl.jar");
        } else {
            System.out.println("m3g preloaded");
        }
    }

    private static void addToClassPath(String s) {
        try {
            URLClassLoader classLoader = (URLClassLoader) Emulator.class.getClassLoader();
            Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrlMethod.setAccessible(true);
            File f = new File(Emulator.getAbsolutePath() + "/" + s);
            URL swtFileUrl = f.toURL();
            addUrlMethod.invoke(classLoader, swtFileUrl);
        }
        catch(Exception e) {
            throw new RuntimeException(s, e);
        }
    }
}
