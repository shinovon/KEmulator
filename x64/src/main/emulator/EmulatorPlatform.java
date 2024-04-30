package emulator;

import emulator.debug.MemoryViewImage;
import emulator.graphics3D.IGraphics3D;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class EmulatorPlatform implements IEmulatorPlatform {

    public boolean isX64() {
        return true;
    }

    public String getName() {
        return "KEmulator nnx64";
    }

    public String getTitleName() {
        return "KEmnnx64";
    }

    public String getInfoString(String version) {
        return "KEmulator nnmod " + version+"\n\n"
                + "\tMulti-Platform\n"
                + "\t" + UILocale.get("ABOUT_INFO_EMULATOR", "Mobile Game Emulator") + "\n\n"
                + UILocale.get("ABOUT_INFO_APIS", "Support APIs") + ":\n\n"
                + "\tMIDP 2.0 (JSR118)\n"
                + "\tNokiaUI 1.4\n"
                + "\tSprint 1.0\n"
                + "\tWMA 1.0 (JSR120)\n"
                + "\tM3G 1.1 (JSR184)\n"
                + "\tOpenGL ES (JSR239)\n"
                + "\tSoftBank MEXA"
                ;
    }

    public void loadLibraries() {
        System.setProperty("jna.nosys", "true");
        System.setProperty("org.lwjgl.librarypath", Emulator.getAbsolutePath());
        loadSWTLibrary();
    }

    public boolean supportsMascotCapsule() {
        return false;
    }

    public boolean supportsM3G() {
        return true;
    }

    public MemoryViewImage convertMicro3DTexture(Object o) {
        return null;
    }

    public IGraphics3D getGraphics3D() {
        return emulator.graphics3D.lwjgl.Emulator3D.getInstance();
    }

    public void loadM3G() {
        if(!supportsM3G()) return;
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
        } catch (Throwable ignored) {
        }
        System.out.println("m3g loaded: " + m3gLoaded);
        if(!m3gLoaded) {
            // TODO
            addToClassPath("m3g_lwjgl.jar");
        }
    }

    private static void loadSWTLibrary() {
        String osn = System.getProperty("os.name").toLowerCase();
        String osa = System.getProperty("os.arch").toLowerCase();
        String os =
                osn.contains("win") ? "win32" :
                        osn.contains("mac") ? "macosx" :
                                osn.contains("linux") || osn.contains("nix") ? "gtk-linux" :
                                        null;
        if(os == null) {
            throw new RuntimeException("unsupported os: " + osn);
        }
        if(!osa.contains("amd64") && !osa.contains("86") && !osa.contains("aarch64")) {
            throw new RuntimeException("unsupported arch: " + osa);
        }
        String arch = osa.contains("amd64") ? "x86_64" : osa.contains("86") ? "x86" : osa;
        String swtFileName = "swt-" + os + "-" + arch + ".jar";
        addToClassPath(swtFileName);
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