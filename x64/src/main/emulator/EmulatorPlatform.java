package emulator;

import emulator.debug.Texture;

import java.io.File;
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
                + "\t(no 3d support)"
                ;
    }

    public void loadLibraries() {
        System.setProperty("jna.nosys", "true");
        loadSWTLibrary();
        loadJOGLLibrary();
    }

    public boolean supportsMascotCapsule() {
        return false;
    }

    public boolean supportsM3G() {
        return false;
    }

    public Texture convertMicro3DTexture(Object o) {
        return null;
    }

    private static void loadJOGLLibrary() {
        String osn = System.getProperty("os.name").toLowerCase();
        String osa = System.getProperty("os.arch").toLowerCase();
        String os =
                osn.contains("win") ? "windows" :
                        osn.contains("mac") ? "macosx" :
                                osn.contains("linux") || osn.contains("nix") ? "linux" :
                                        null;
        if(os == null) {
            return;
        }
        if(!osa.contains("amd64") && !osa.contains("86") && !osa.contains("armv6")) {
            return;
        }
        String arch = osn.contains("macosx") ? "universal" : osa.contains("amd64") ? "amd64" : osa.contains("86") ? "i586" : osa;
        String suffix = "-natives-" + os + "-" + arch + ".jar";

        addToClassPath("gluegen-rt.jar");
        addToClassPath("gluegen-rt" + suffix);
        addToClassPath("jogl-all.jar");
        addToClassPath("jogl-all" + suffix);
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
            File f = new File("./"+s);
            URL swtFileUrl = f.toURL();
            addUrlMethod.invoke(classLoader, swtFileUrl);
        }
        catch(Exception e) {
            throw new RuntimeException(s, e);
        }
    }
}