package emulator;

import emulator.debug.MemoryViewImage;
import emulator.graphics2D.IImage;
import emulator.graphics3D.IGraphics3D;
import ru.woesss.j2me.micro3d.TextureImpl;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
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

            ChoosePixelFormat = ReflectUtil.getMethod(wglClass, "ChoosePixelFormat", long.class, pixelFormatDescriptorClass);
            if(ChoosePixelFormat != null) {
                SetPixelFormat = ReflectUtil.getMethod(wglClass, "SetPixelFormat", long.class, int.class, pixelFormatDescriptorClass);
                wglCreateContext = ReflectUtil.getMethod(wglClass, "wglCreateContext", long.class);
                wglMakeCurrent = ReflectUtil.getMethod(wglClass, "wglMakeCurrent", long.class, long.class);
                wglDeleteContext = ReflectUtil.getMethod(wglClass, "wglDeleteContext", long.class);
                getDC = ReflectUtil.getMethod(osClass, "getDC", long.class);
                releaseDC = ReflectUtil.getMethod(osClass, "releaseDC", long.class, long.class);
            } else {
                ChoosePixelFormat = ReflectUtil.getMethod(wglClass, "ChoosePixelFormat", int.class, pixelFormatDescriptorClass);
                SetPixelFormat = ReflectUtil.getMethod(wglClass, "SetPixelFormat", int.class, int.class, pixelFormatDescriptorClass);
                wglCreateContext = ReflectUtil.getMethod(wglClass, "wglCreateContext", int.class);
                wglMakeCurrent = ReflectUtil.getMethod(wglClass, "wglMakeCurrent", int.class, int.class);
                wglDeleteContext = ReflectUtil.getMethod(wglClass, "wglDeleteContext", int.class);
                getDC = ReflectUtil.getMethod(osClass, "getDC", int.class);
                releaseDC = ReflectUtil.getMethod(osClass, "releaseDC", int.class, int.class);
            }
            wglGetCurrentContext = ReflectUtil.getMethod(wglClass, "wglGetCurrentContext");
        } catch (Throwable ignored) {}
    }

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
                + "\tSiemens API\n"
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
        return true;
    }

    public boolean supportsM3G() {
        return true;
    }

    public MemoryViewImage convertMicro3DTexture(Object o) {
        try {
            Class cls = o.getClass();
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
        } catch (Exception ignored) {}
        return null;
    }

    public IGraphics3D getGraphics3D() {
        return emulator.graphics3D.lwjgl.Emulator3D.getInstance();
    }

    public long createGLContext(long gcHandle, boolean b) throws Exception {
        if(wglClass == null) {
            throw new UnsupportedOperationException();
        }
        Object var4 = pixelFormatDescriptorClass.newInstance();
        pixelFormatDescriptorClass.getField("nSize").set(var4, (short) 40);
        pixelFormatDescriptorClass.getField("nVersion").set(var4, (short) 1);
        pixelFormatDescriptorClass.getField("dwFlags").set(var4, 37 + (b ? 20 : 0));
        pixelFormatDescriptorClass.getField("iPixelType").set(var4, (byte) 0);
        pixelFormatDescriptorClass.getField("cColorBits").set(var4, (byte) Emulator.getEmulator().getScreenDepth());
        pixelFormatDescriptorClass.getField("iLayerType").set(var4, (byte) 0);
        int var5;
        try {
            var5 = (Integer) ChoosePixelFormat.invoke(null, gcHandle, var4);
        } catch (IllegalArgumentException e) {
            var5 = (Integer) ChoosePixelFormat.invoke(null, (int) gcHandle, var4);
        }
        if (var5 == 0) {
            return 0;
        }

        try {
            if(!((Boolean) SetPixelFormat.invoke(null, gcHandle, var5, var4))) {
                return 0;
            }
        } catch (IllegalArgumentException e) {
            if(!((Boolean) SetPixelFormat.invoke(null, (int) gcHandle, var5, var4))) {
                return 0;
            }
        }

        try {
            return (Long) wglCreateContext.invoke(null, gcHandle);
        } catch (IllegalArgumentException e) {
            return (Integer) wglCreateContext.invoke(null, (int) gcHandle);
        }
    }

    private long wglGetCurrentContext() throws Exception {
        Object o = wglGetCurrentContext.invoke(null);
        if(o instanceof Long) return (Long) o;
        return (Integer) o;
    }

    public boolean isGLContextCurrent(long imgHandle) throws Exception {
        if(wglClass == null) {
            throw new UnsupportedOperationException();
        }
        return wglGetCurrentContext() == imgHandle;
    }

    public void setGLContextCurrent(long gcHandle, long contextHandle) throws Exception {
        if(wglClass == null) {
            throw new UnsupportedOperationException();
        }
        while (wglGetCurrentContext() > 0);
        try {
            wglMakeCurrent.invoke(null, gcHandle, contextHandle);
        } catch (IllegalArgumentException e) {
            wglMakeCurrent.invoke(null, (int) gcHandle, (int) contextHandle);
        }
    }

    public void releaseGLContext(long gcHandle) throws Exception {
        if(wglClass == null) {
            throw new UnsupportedOperationException();
        }
        try {
            wglMakeCurrent.invoke(null, gcHandle, -1L);
        } catch (IllegalArgumentException e) {
            wglMakeCurrent.invoke(null, (int) gcHandle, -1);
        }
    }

    public void deleteGLContext(long contextHandle) throws Exception {
        if(wglClass == null) {
            throw new UnsupportedOperationException();
        }
        try {
            wglDeleteContext.invoke(null, contextHandle);
        } catch (IllegalArgumentException e) {
            wglDeleteContext.invoke(null, (int) contextHandle);
        }
    }

    public long getDC(long handle) throws Exception {
        try {
            return (Long) getDC.invoke(null, handle);
        } catch (IllegalArgumentException e) {
            return (Integer) getDC.invoke(null, (int) handle);
        }
    }

    public void releaseDC(long handle, long dc) throws Exception {
        try {
            releaseDC.invoke(null, handle, dc);
        } catch (IllegalArgumentException e) {
            releaseDC.invoke(null, (int) handle, (int) dc);
        }
    }

    public void swapBuffers(long dc) throws Exception {
        try {
            SwapBuffers.invoke(null, dc);
        } catch (IllegalArgumentException e) {
            SwapBuffers.invoke(null, (int) dc);
        }
    }

    public void load3D() {
        if(supportsM3G()) {
            // load m3g
            boolean m3gLoaded = false;
            try {
                Class cls = Class.forName("javax.microedition.m3g.Graphics3D");
                try {
                    m3gLoaded = !cls.getField("_STUB").getBoolean(null);
                } catch (Throwable e) {
                    m3gLoaded = true;
                }
            } catch (Throwable ignored) {}
            if(!m3gLoaded) {
                // TODO
                addToClassPath("m3g_lwjgl.jar");
            }
        }

        // load mascot
        if(supportsMascotCapsule()) {
            boolean mascotLoaded = false;
            try {
                Class cls = Class.forName("com.mascotcapsule.micro3d.v3.Graphics3D");
                try {
                    mascotLoaded = !cls.getField("_STUB").getBoolean(null);
                } catch (Throwable e) {
                    mascotLoaded = true;
                }
            } catch (Throwable ignored) {}
            if (!mascotLoaded) {
                addToClassPath("micro3d_gl.jar");
            }
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
            Agent.addClassPath(new File(Emulator.getAbsolutePath() + "/" + s));
        } catch (Exception e) {
            throw new RuntimeException(s, e);
        }
    }
}