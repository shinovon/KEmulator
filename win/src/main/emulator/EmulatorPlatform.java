package emulator;

import emulator.debug.MemoryViewImage;
import emulator.graphics2D.IImage;
import emulator.graphics3D.IGraphics3D;
import org.eclipse.swt.internal.opengl.win32.PIXELFORMATDESCRIPTOR;
import ru.woesss.j2me.micro3d.TextureImpl;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.IntBuffer;

public class EmulatorPlatform implements IEmulatorPlatform {

    private static Class wglClass;
    private static Class pixelFormatDescriptorClass;
    private static Method ChoosePixelFormat;
    private static Method SetPixelFormat;
    private static Method wglCreateContext;
    private static Method wglDeleteContext;
    private static Method wglMakeCurrent;
    private static Method wglGetCurrentContext;
    private static Class osClass;
    private static Method getDC;
    private static Method releaseDC;
    private static Method SwapBuffers;

    static {
        try {
            wglClass = Class.forName("org.eclipse.swt.internal.opengl.win32.WGL");
            pixelFormatDescriptorClass = Class.forName("org.eclipse.swt.internal.opengl.win32.PIXELFORMATDESCRIPTOR");
            osClass = Class.forName("org.eclipse.swt.internal.win32.OS");
            ChoosePixelFormat = ReflectUtil.getMethod(wglClass, "ChoosePixelFormat", int.class, pixelFormatDescriptorClass);
            SetPixelFormat = ReflectUtil.getMethod(wglClass, "SetPixelFormat", int.class, int.class, pixelFormatDescriptorClass);
            wglCreateContext = ReflectUtil.getMethod(wglClass, "wglCreateContext", int.class);
            wglMakeCurrent = ReflectUtil.getMethod(wglClass, "wglMakeCurrent", int.class, int.class);
            wglDeleteContext = ReflectUtil.getMethod(wglClass, "wglDeleteContext", int.class);
            SwapBuffers = ReflectUtil.getMethod(wglClass, "wglSwapBuffers", int.class);
            wglGetCurrentContext = ReflectUtil.getMethod(wglClass, "wglGetCurrentContext");
            getDC = ReflectUtil.getMethod(osClass, "getDC", int.class);
            releaseDC = ReflectUtil.getMethod(osClass, "releaseDC", int.class, int.class);
        } catch (Throwable ignored) {}
    }

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
                + "\tSiemens API\n"
                + "\tWMA 1.0 (JSR120)\n"
                + "\tSensor (JSR256)\n"
                + "\tM3G 1.1 (JSR184)\n"
                + "\tOpenGL ES (JSR239)\n"
                + "\tMascotCapsule v3\n"
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
        Class cls = o.getClass();
        try {
            if(Settings.micro3d == 1) {
                TextureImpl impl = (TextureImpl) cls.getField("impl").get(o);
                if (impl == null)
                    return null;
                IntBuffer ib = impl.image.getRaster().asIntBuffer();
                int w = impl.getWidth(), h = impl.getHeight();
                IImage img = Emulator.getEmulator().newImage(w, h, true);
                int[] data = img.getData();
                int i = data.length - w;

                for (int j = h; j > 0; --j) {
                    ib.get(data, i, w);
                    i -= w;
                }
                return new MemoryViewImage(img);
            } else {
                IImage img = (IImage) cls.getField("debugImage").get(o);
                return new MemoryViewImage(img);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public IGraphics3D getGraphics3D() {
        return emulator.graphics3D.lwjgl.Emulator3D.getInstance();
    }

    public long createGLContext(long gcHandle, boolean b) throws Exception {
        int handle = (int) gcHandle;
        PIXELFORMATDESCRIPTOR var4 = new PIXELFORMATDESCRIPTOR();
        var4.nSize = 40;
        var4.nVersion = 1;
        var4.dwFlags = 37 + (b ? 20 : 0);
        var4.iPixelType = 0;
        var4.cColorBits = (byte) Emulator.getEmulator().getScreenDepth();
        var4.iLayerType = 0;
        int var5 = (Integer) ChoosePixelFormat.invoke(null, handle, var4);
        if (var5 == 0) {
            return 0;
        }

        if(!((Boolean) SetPixelFormat.invoke(null, handle, var5, var4))) {
            return 0;
        }

        return (Integer) wglCreateContext.invoke(null, handle);
    }

    public boolean isGLContextCurrent(long imgHandle) throws Exception {
        return (Integer) wglGetCurrentContext.invoke(null) == imgHandle;
    }

    public void setGLContextCurrent(long gcHandle, long contextHandle) throws Exception {
        while ((Integer) wglGetCurrentContext.invoke(null) > 0);
        wglMakeCurrent.invoke(null, (int) gcHandle, (int) contextHandle);
    }

    public void releaseGLContext(long gcHandle) throws Exception {
        wglMakeCurrent.invoke(null, (int) gcHandle, -1);
    }

    public void deleteGLContext(long contextHandle) throws Exception {
        wglDeleteContext.invoke(null, (int) contextHandle);
    }

    public long getDC(long handle) throws Exception {
        return (int) getDC.invoke(null, (int) handle);
    }

    public void releaseDC(long handle, long dc) throws Exception {
        releaseDC.invoke(null, (int) handle, (int) dc);
    }

    public void swapBuffers(long dc) throws Exception {
        SwapBuffers.invoke(null, (int)dc);
    }

    public void load3D() {
        if (supportsM3G()) {
            // load m3g
            boolean m3gLoaded = false;
            try {
                Class cls = Class.forName("javax.microedition.m3g.Graphics3D");
                try {
                    m3gLoaded = !cls.getField("_STUB").getBoolean(null);
//                if (!m3gLoaded) {
//                    System.out.println("m3g stub!!");
//                }
                } catch (Throwable e) {
                    m3gLoaded = true;
                }
            } catch (Throwable ignored) {}
            if (!m3gLoaded) {
                addToClassPath(Settings.g3d == 0 ? "m3g_swerve.jar" : "m3g_lwjgl.jar");
            }
        }

        if(supportsMascotCapsule()) {
            // load mascot
            boolean mascotLoaded = false;
            try {
                Class cls = Class.forName("com.mascotcapsule.micro3d.v3.Graphics3D");
                try {
                    mascotLoaded = !cls.getField("_STUB").getBoolean(null);
//                if (!m3gLoaded) {
//                    System.out.println("m3g stub!!");
//                }
                } catch (Throwable e) {
                    mascotLoaded = true;
                }
            } catch (Throwable ignored) {}
            if (!mascotLoaded) {
                addToClassPath(Settings.micro3d == 0 ? "micro3d_dll.jar" : "micro3d_gl.jar");
            }
        }
    }

    private static void addToClassPath(String s) {
        try {
            Agent.addClassPath(new File(Emulator.getAbsolutePath() + "/" + s));
        } catch (Exception e) {
            throw new RuntimeException(s, e);
        }
    }
}
