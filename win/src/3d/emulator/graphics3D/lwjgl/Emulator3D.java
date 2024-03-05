package emulator.graphics3D.lwjgl;

import emulator.graphics3D.*;

import java.util.*;
import javax.microedition.lcdui.*;

import emulator.*;
import org.lwjgl.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.opengl.win32.*;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.Drawable;

import emulator.graphics2D.swt.Graphics2DSWT;

import java.awt.image.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.*;
import javax.microedition.m3g.*;
import javax.microedition.m3g.Transform;

public final class Emulator3D implements IGraphics3D {
    private static Emulator3D ab383;
    private Object renderTarget;
    private boolean aBoolean382;
    private int anInt387;
    private static Hashtable aHashtable388;
    private static int anInt396;
    private static int anInt399;
    private static Pbuffer aPbuffer390;
    private static ByteBuffer aByteBuffer391;
    private static BufferedImage aBufferedImage392;
    private static ImageData anImageData393;
    private static final PaletteData aPaletteData394;
    private static Hashtable m3gProps;
    private static PixelFormat aPixelFormat395;
    private long context;
    private Image image;
    private GC gc;
    private float aFloat386;
    private float aFloat398;
    private int anInt401;
    private int anInt402;
    private int anInt403;
    private int anInt404;

    private Emulator3D() {
        super();
        Emulator3D.ab383 = this;
        Emulator3D.m3gProps.put("supportAntialiasing", Boolean.TRUE);
        Emulator3D.m3gProps.put("supportTrueColor", Boolean.TRUE);
        Emulator3D.m3gProps.put("supportDithering", Boolean.TRUE);
        Emulator3D.m3gProps.put("supportMipmapping", Boolean.TRUE);
        Emulator3D.m3gProps.put("supportPerspectiveCorrection", Boolean.TRUE);
        Emulator3D.m3gProps.put("supportLocalCameraLighting", Boolean.TRUE);
        Emulator3D.m3gProps.put("maxLights", new Integer(8));
        Emulator3D.m3gProps.put("maxViewportWidth", new Integer(2048));
        Emulator3D.m3gProps.put("maxViewportHeight", new Integer(2048));
        Emulator3D.m3gProps.put("maxViewportDimension", new Integer(2048));
        Emulator3D.m3gProps.put("maxTextureDimension", new Integer(1024));
        Emulator3D.m3gProps.put("maxSpriteCropDimension", new Integer(1024));
        Emulator3D.m3gProps.put("maxTransformsPerVertex", new Integer(1000));
        Emulator3D.m3gProps.put("numTextureUnits", new Integer(10));
        Emulator3D.m3gProps.put("coreID", "@KEmulator LWJ-OpenGL-M3G @liang.wu");
    }

    public static Emulator3D method189() {
        if (Emulator3D.ab383 == null) {
            Emulator3D.ab383 = new Emulator3D();
        }
        return Emulator3D.ab383;
    }

    private static PixelFormat method190() {
        if (Emulator3D.aPixelFormat395 == null) {
            int n = 4;
            final int screenDepth = Emulator.getEmulator().getScreenDepth();
            while (true) {
                try {
                    Emulator3D.aPixelFormat395 = new PixelFormat(screenDepth, 0, 24, 0, n);
                    new Pbuffer(1, 1, Emulator3D.aPixelFormat395, (RenderTexture) null, (Drawable) null).destroy();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if ((n >>= 1) != 0) {
                        continue;
                    }
                    Emulator3D.aPixelFormat395 = new PixelFormat(screenDepth, 0, 24, 0, 0);
                }
                break;
            }
        }
        return Emulator3D.aPixelFormat395;
    }

    public final void bindTarget(final Object o) {
        int anInt396;
        int anInt397;
        if (o instanceof Graphics) {
            this.renderTarget = o;
            anInt396 = ((Graphics) this.renderTarget).getImage().getWidth();
            anInt397 = ((Graphics) this.renderTarget).getImage().getHeight();
        } else {
            if (!(o instanceof Image2D)) {
                throw new IllegalArgumentException();
            }
            this.renderTarget = o;
            anInt396 = ((Image2D) this.renderTarget).getWidth();
            anInt397 = ((Image2D) this.renderTarget).getHeight();
        }
        try {
            try {
                final String string = anInt396 + "x" + anInt397;
                Emulator3D.aPbuffer390 = (Pbuffer) Emulator3D.aHashtable388.get(string);
                if (Emulator3D.aPbuffer390 == null) {
                    Emulator3D.aPbuffer390 = new Pbuffer(anInt396, anInt397, method190(), (RenderTexture) null, (Drawable) null);
                    Emulator3D.aHashtable388.put(string, Emulator3D.aPbuffer390);
                }
                Emulator3D.aPbuffer390.makeCurrent();
            } catch (Exception ex) {
                ex.printStackTrace();
                this.method191(anInt396, anInt397);
            }
            if (Emulator3D.anInt396 != anInt396 || Emulator3D.anInt399 != anInt397) {
                if (Settings.g2d == 1) {
                    Emulator3D.aBufferedImage392 = new BufferedImage(anInt396, anInt397, 4);
                } else {
                    Emulator3D.anImageData393 = new ImageData(anInt396, anInt397, 32, Emulator3D.aPaletteData394);
                }
                Emulator3D.aByteBuffer391 = BufferUtils.createByteBuffer(anInt396 * anInt397 * 4);
                Emulator3D.anInt396 = anInt396;
                Emulator3D.anInt399 = anInt397;
            }
            GL11.glEnable(3089);
            GL11.glEnable(2977);
            GL11.glPixelStorei(3317, 1);
        } catch (Exception ex2) {
            ex2.printStackTrace();
            this.renderTarget = null;
            throw new IllegalArgumentException();
        }
    }

    public final void releaseTarget() {
        GL11.glFinish();
        this.method192();
        this.renderTarget = null;
        if (Emulator3D.aPbuffer390 != null) {
            try {
                Emulator3D.aPbuffer390.releaseContext();
            } catch (LWJGLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
        this.releaseGl();
    }

    private void method191(final int n, final int n2) {
        if (this.image == null || this.image.getBounds().width != n || this.image.getBounds().height != n2) {
            if (this.image != null) {
                this.image.dispose();
                this.gc.dispose();
                if (useWgl()) {
                    WGL_wglDeleteContext(this.context);
                } else {
                    // TODO
                }
            }
            this.image = new Image((Device) null, new ImageData(n, n2, 32, Emulator3D.aPaletteData394));
            this.gc = new GC((org.eclipse.swt.graphics.Drawable) this.image);
            if (useWgl()) {
                final PIXELFORMATDESCRIPTOR pixelformatdescriptor;
                (pixelformatdescriptor = new PIXELFORMATDESCRIPTOR()).nSize = 40;
                pixelformatdescriptor.nVersion = 1;
                pixelformatdescriptor.dwFlags = 57;
                pixelformatdescriptor.iPixelType = 0;
                pixelformatdescriptor.cColorBits = (byte) Emulator.getEmulator().getScreenDepth();
                pixelformatdescriptor.iLayerType = 0;
                final long pixelFormat = WGL_ChoosePixelFormat(getHandle(gc), pixelformatdescriptor);
                if (pixelFormat == 0 || !WGL_SetPixelFormat(getHandle(gc), pixelFormat, pixelformatdescriptor)) {
                    this.gc.dispose();
                    this.image.dispose();
                    throw new IllegalArgumentException();
                }
                this.context = WGL_wglCreateContext(getHandle(gc));
                if (this.context == 0) {
                    this.gc.dispose();
                    this.image.dispose();
                    throw new IllegalArgumentException();
                }
            }
            if (WGL_wglGetCurrentContext() == getHandle(image)) {
                return;
            }
            while (WGL_wglGetCurrentContext() > 0) {
            }
            WGL_wglMakeCurrent(getHandle(gc), this.context);
        } else {
            // TODO
        }
        try {
            GLContext.useContext((Object) this.image);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    private static boolean useWgl() {
        return !Emulator.isX64() || System.getProperty("os.name").toLowerCase().startsWith("win");
    }

    private void releaseGl() {
        if (useWgl()) {
            WGL_wglMakeCurrent(getHandle(gc), -1);
        } else {
            // TODO
        }
        try {
            GLContext.useContext((Object) null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public final void method192() {
        if (this.renderTarget == null) {
            return;
        }
        if (this.renderTarget instanceof Image2D) {
            final Image2D image2D = (Image2D) this.renderTarget;
            Emulator3D.aByteBuffer391.rewind();
            GL11.glReadPixels(0, 0, Emulator3D.anInt396, Emulator3D.anInt399, 6408, 5121, Emulator3D.aByteBuffer391);
            final byte[] array = new byte[Emulator3D.anInt396 * Emulator3D.anInt399 * 4];
            final int n = Emulator3D.anInt396 << 2;
            int n2 = array.length - n;
            for (int i = Emulator3D.anInt399; i > 0; --i) {
                Emulator3D.aByteBuffer391.get(array, n2, n);
                n2 -= n;
            }
            Image2D image2D2;
            int n3;
            int n4;
            int n5;
            int n6;
            byte[] array2;
            if (image2D.getFormat() == 100) {
                image2D2 = image2D;
                n3 = 0;
                n4 = 0;
                n5 = image2D.getWidth();
                n6 = image2D.getHeight();
                array2 = array;
            } else {
                final byte[] array3 = new byte[image2D.getWidth() * image2D.getHeight() * 3];
                for (int n7 = array.length - 1, j = array3.length - 1; j >= 0; array3[j--] = array[n7--], array3[j--] = array[n7--], array3[j--] = array[n7--]) {
                    --n7;
                }
                image2D2 = image2D;
                n3 = 0;
                n4 = 0;
                n5 = image2D.getWidth();
                n6 = image2D.getHeight();
                array2 = array3;
            }
            image2D2.set(n3, n4, n5, n6, array2);
            return;
        }
        if (Settings.g2d == 0) {
            Emulator3D.aByteBuffer391.rewind();
            GL11.glReadPixels(0, 0, Emulator3D.anInt396, Emulator3D.anInt399, 6408, 5121, Emulator3D.aByteBuffer391);
            final int n8 = Emulator3D.anImageData393.width << 2;
            int n9 = Emulator3D.anImageData393.data.length - n8;
            for (int k = Emulator3D.anImageData393.height; k > 0; --k) {
                Emulator3D.aByteBuffer391.get(Emulator3D.anImageData393.data, n9, n8);
                n9 -= n8;
            }
            final Image image = new Image((Device) null, Emulator3D.anImageData393);
            ((Graphics2DSWT) ((Graphics) this.renderTarget).getImpl()).method299().drawImage(image, 0, 0);
            image.dispose();
            return;
        }
        Emulator3D.aByteBuffer391.rewind();
        GL11.glReadPixels(0, 0, Emulator3D.anInt396, Emulator3D.anInt399, 6408, 5121, Emulator3D.aByteBuffer391);
        final int[] data = ((DataBufferInt) Emulator3D.aBufferedImage392.getRaster().getDataBuffer()).getData();
        final IntBuffer intBuffer = Emulator3D.aByteBuffer391.asIntBuffer();
        final int anInt396 = Emulator3D.anInt396;
        int n10 = data.length - anInt396;
        for (int l = Emulator3D.anInt399; l > 0; --l) {
            intBuffer.get(data, n10, anInt396);
            n10 -= anInt396;
        }
        ((emulator.graphics2D.awt.b) ((Graphics) this.renderTarget).getImpl()).g().drawImage(Emulator3D.aBufferedImage392, 0, 0, null);
    }

    public final void enableDepthBuffer(final boolean aBoolean382) {
        this.aBoolean382 = aBoolean382;
    }

    public final boolean isDepthBufferEnabled() {
        return this.aBoolean382;
    }

    public final void setHints(final int anInt387) {
        this.anInt387 = anInt387;
        if (this.renderTarget != null) {
            this.method195();
        }
    }

    private void method195() {
        if ((this.anInt387 & 0x2) != 0x0) {
            GL11.glEnable(2832);
            GL11.glEnable(2848);
            GL11.glEnable(2881);
        } else {
            GL11.glDisable(2832);
            GL11.glDisable(2848);
            GL11.glDisable(2881);
        }
        if ((this.anInt387 & 0x4) != 0x0) {
            GL11.glEnable(3024);
            return;
        }
        GL11.glDisable(3024);
    }

    public final int getHints() {
        return this.anInt387;
    }

    public final Hashtable getProperties() {
        return Emulator3D.m3gProps;
    }

    public final void setDepthRange(final float aFloat386, final float aFloat387) {
        this.aFloat386 = aFloat386;
        this.aFloat398 = aFloat387;
    }

    private void method196() {
        GL11.glDepthRange((double) this.aFloat386, (double) this.aFloat398);
    }

    public final void setViewport(final int anInt401, final int anInt402, final int anInt403, final int anInt404) {
        this.anInt401 = anInt401;
        this.anInt402 = anInt402;
        this.anInt403 = anInt403;
        this.anInt404 = anInt404;
    }

    private void method197() {
        GL11.glViewport(this.anInt401, Emulator3D.anInt399 - this.anInt402 - this.anInt404, this.anInt403, this.anInt404);
        GL11.glScissor(this.anInt401, Emulator3D.anInt399 - this.anInt402 - this.anInt404, this.anInt403, this.anInt404);
    }

    public final void clearBackgound(final Object o) {
        final Background background = (Background) o;
        this.method197();
        this.method196();
        GL11.glClearDepth(1.0);
        GL11.glDepthMask(true);
        GL11.glColorMask(true, true, true, true);
        final int n;
        GL11.glClearColor(emulator.graphics3D.G3DUtils.method817(n = ((background == null || Settings.xrayView) ? 0 : background.getColor()), 16), emulator.graphics3D.G3DUtils.method817(n, 8), emulator.graphics3D.G3DUtils.method817(n, 0), emulator.graphics3D.G3DUtils.method817(n, 24));
        if (background != null && !Settings.xrayView) {
            GL11.glClear((background.isColorClearEnabled() ? 16384 : 0) | ((this.aBoolean382 && background.isDepthClearEnabled()) ? 256 : 0));
            this.method193(background);
            return;
        }
        GL11.glClear(0x4000 | (this.aBoolean382 ? 256 : 0));
    }

    private void method193(final Background background) {
        if (background != null && background.getImage() != null && background.getCropWidth() > 0 && background.getCropHeight() > 0) {
            GL11.glDisable(2896);
            GL11.glDisable(2912);
            final int n = (background.getImage().getFormat() == 99) ? 6407 : 6408;
            final int width = background.getImage().getWidth();
            final int height = background.getImage().getHeight();
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            final float n2 = this.anInt403;
            final float n3 = this.anInt404;
            final float n4 = n2 / background.getCropWidth();
            final float n5 = n3 / background.getCropHeight();
            final float n6 = n4 * width;
            final float n7 = n5 * height;
            float n8 = -n2 * background.getCropX() / background.getCropWidth() - n2 / 2.0f;
            float n9 = n3 * background.getCropY() / background.getCropHeight() + n3 / 2.0f;
            int n10 = 1;
            int n11 = 1;
            if (background.getImageModeX() == 33) {
                float n12;
                if ((n12 = n8 % n6) > 0.0f) {
                    n12 -= n6;
                }
                n10 = (int) (2.5f + n2 / n6);
                n8 = n12 - n10 / 2 * n6;
            }
            if (background.getImageModeY() == 33) {
                final float n13 = n9 % n7;
                n11 = (int) (2.5f + n3 / n7);
                n9 = n13 + n11 / 2 * n7;
            }
            GL11.glPixelStorei(3314, width);
            GL11.glPixelStorei(3315, 0);
            GL11.glPixelStorei(3316, 0);
            GL11.glDepthFunc(519);
            GL11.glDepthMask(false);
            GL11.glPixelZoom(n4, -n5);
            final byte[] array = new byte[width * height * 4];
            background.getImage().getPixels(array);
            final ByteBuffer method198 = a.method198(array);
            for (int i = 0; i < n11; ++i) {
                for (int j = 0; j < n10; ++j) {
                    GL11.glRasterPos4f(0.0f, 0.0f, 0.0f, 1.0f);
                    GL11.glBitmap(0, 0, 0.0f, 0.0f, n8 + j * n6, n9 - i * n7, method198);
                    GL11.glDrawPixels(width, height, n, 5121, method198);
                }
            }
            GL11.glPixelStorei(3314, 0);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5889);
            GL11.glPopMatrix();
        }
    }

    public final void render(final VertexBuffer vertexBuffer, final IndexBuffer indexBuffer, final Appearance appearance, final Transform transform, final int n) {
    }

    public final void render(final Node node, final Transform transform) {
    }

    public final void render(final World world) {
    }

    public final void v3bind(final Graphics graphics) {

    }

    public final void v3flush() {
    }

    public final void v3release(final Graphics graphics) {
    }

    static {
        Emulator3D.aHashtable388 = new Hashtable();
        aPaletteData394 = new PaletteData(-16777216, 16711680, 65280);
        Emulator3D.m3gProps = new Hashtable();
    }

    private static long toLong(Object o) {
        if (o instanceof Integer) {
            return ((Integer) o).longValue();
        }
        if (o instanceof Long) {
            return ((Long) o).longValue();
        }
        throw new Error("Not number: " + o.getClass());
    }

    private static long Win32(String n, Class[] t, Object[] p, Class[] t64, Object[] p64) {
        try {
            Class<?> os = Class.forName("org.eclipse.swt.internal.win32.OS");
            Method m = null;
            try {
                m = os.getMethod(n, t);
                return toLong(m.invoke(null, p));
            } catch (Exception e) {
                m = os.getMethod(n, t64);
                return toLong(m.invoke(null, p64));
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private static long GTK(String n, Class[] t, Object[] p, Class[] t64, Object[] p64) {
        try {
            Class<?> os = Class.forName("org.eclipse.swt.internal.gtk.OS");
            Method m = null;
            try {
                m = os.getMethod(n, t);
                return toLong(m.invoke(null, p));
            } catch (Exception e) {
                m = os.getMethod(n, t64);
                return toLong(m.invoke(null, p64));
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private static long GTK_gtk_widget_get_window(long n) {
        return GTK("gtk_widget_get_window",
                new Class[]{int.class},
                new Object[]{(int) n},
                new Class[]{long.class},
                new Object[]{n});
    }

    private static long GTK_gdk_x11_display_get_xdisplay(long n) {
        return GTK("gdk_x11_display_get_xdisplay",
                new Class[]{int.class},
                new Object[]{(int) n},
                new Class[]{long.class},
                new Object[]{n});
    }

    private static long GTK_gdk_window_get_display(long n) {
        return GTK("gdk_window_get_display",
                new Class[]{int.class},
                new Object[]{(int) n},
                new Class[]{long.class},
                new Object[]{n});
    }

    private static long GTK_XDefaultScreen(long n) {
        return GTK("XDefaultScreen",
                new Class[]{int.class},
                new Object[]{(int) n},
                new Class[]{long.class},
                new Object[]{n});
    }

    private long gdk_x11_display_get_xdisplay(long n) {
        return GTK_gdk_x11_display_get_xdisplay(GTK_gdk_window_get_display(n));
    }

    private static Object WGL(String n, Class[] t, Object[] p, Class[] t64, Object[] p64) {
        try {
            Class<?> os = WGL.class;
            Method m = null;
            try {
                m = os.getMethod(n, t);
                return m.invoke(null, p);
            } catch (Exception e) {
                m = os.getMethod(n, t64);
                return m.invoke(null, p64);
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private static long Win32_ReleaseDC(long h, long i) {
        return Win32("ReleaseDC",
                new Class[]{int.class, int.class},
                new Object[]{(int) h, (int) i},
                new Class[]{long.class, long.class},
                new Object[]{h, i});
    }

    private static long Win32_GetDC(long h) {
        return Win32("GetDC",
                new Class[]{int.class},
                new Object[]{(int) h},
                new Class[]{long.class},
                new Object[]{h});
    }

    private static long WGL_wglMakeCurrent(long i, long d) {
        return toLong(WGL("wglMakeCurrent",
                new Class[]{int.class, int.class},
                new Object[]{(int) i, (int) d},
                new Class[]{long.class, long.class},
                new Object[]{i, d}));
    }

    private static long WGL_wglCreateContext(long i) {
        return toLong(WGL("wglCreateContext",
                new Class[]{int.class},
                new Object[]{(int) i},
                new Class[]{long.class},
                new Object[]{i}));
    }

    private static boolean WGL_SetPixelFormat(long i, long j, PIXELFORMATDESCRIPTOR k) {
        return (boolean) WGL("SetPixelFormat",
                new Class[]{int.class, int.class, PIXELFORMATDESCRIPTOR.class},
                new Object[]{(int) i, (int) j, k},
                new Class[]{long.class, int.class, PIXELFORMATDESCRIPTOR.class},
                new Object[]{i, (int) j, k});
    }

    private static long WGL_ChoosePixelFormat(long i, PIXELFORMATDESCRIPTOR k) {
        return toLong(WGL("ChoosePixelFormat",
                new Class[]{int.class, PIXELFORMATDESCRIPTOR.class},
                new Object[]{(int) i, k},
                new Class[]{long.class, PIXELFORMATDESCRIPTOR.class},
                new Object[]{i, k}));
    }

    private static long WGL_wglGetCurrentContext() {
        return toLong(WGL("wglGetCurrentContext", null, null, null, null));
    }

    private static long WGL_wglDeleteContext(long i) {
        return toLong(WGL("wglDeleteContext",
                new Class[]{int.class},
                new Object[]{(int) i},
                new Class[]{long.class},
                new Object[]{i}));
    }

    private static long getHandle(Object c) {
        try {
            Class<?> cl = c.getClass();
            Field f = cl.getField("handle");
            return f.getLong(c);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
