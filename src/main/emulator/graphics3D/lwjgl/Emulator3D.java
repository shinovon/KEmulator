package emulator.graphics3D.lwjgl;

import emulator.graphics3D.*;

import java.util.*;
import javax.microedition.lcdui.*;

import emulator.*;
import org.lwjgl.*;
import org.eclipse.swt.graphics.Image;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.Drawable;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.internal.opengl.win32.PIXELFORMATDESCRIPTOR;
import org.eclipse.swt.internal.opengl.win32.WGL;
import emulator.graphics3D.m3g.e;
import emulator.graphics3D.m3g.f;


import emulator.graphics2D.swt.Graphics2DSWT;

import java.awt.image.*;
import java.nio.*;
import javax.microedition.m3g.*;

public final class Emulator3D implements IGraphics3D {

    private static Emulator3D instance;
    private Object target;
    private boolean depthBufferEnabled;
    private int hints;
    private static Hashtable contexts = new Hashtable();
    private static int targetWidth;
    private static int targetHeight;
    private static Pbuffer pbufferContext;
    private static ByteBuffer buffer;
    private static BufferedImage awtBufferImage;
    private static ImageData swtBufferImage;
    private static final PaletteData swtPalleteData = new PaletteData(-16777216, 16711680, '\uff00');
    private static Hashtable properties = new Hashtable();
    private static PixelFormat pixelFormat;
    private int wglContextHandle;
    private Image swtImage;
    private GC swtGC;
    private float depthRangeNear;
    private float depthRangeFar;
    private int viewportX;
    private int viewportY;
    private int viewportWidth;
    private int viewportHeight;

    private Emulator3D() {
        instance = this;
        properties.put("supportAntialiasing", Boolean.TRUE);
        properties.put("supportTrueColor", Boolean.TRUE);
        properties.put("supportDithering", Boolean.TRUE);
        properties.put("supportMipmapping", Boolean.TRUE);
        properties.put("supportPerspectiveCorrection", Boolean.TRUE);
        properties.put("supportLocalCameraLighting", Boolean.TRUE);
        properties.put("maxLights", new Integer(8));
        properties.put("maxViewportWidth", new Integer(2048));
        properties.put("maxViewportHeight", new Integer(2048));
        properties.put("maxViewportDimension", new Integer(2048));
        properties.put("maxTextureDimension", new Integer(1024));
        properties.put("maxSpriteCropDimension", new Integer(1024));
        properties.put("maxTransformsPerVertex", new Integer(1000));
        properties.put("numTextureUnits", new Integer(10));
        properties.put("coreID", "@KEmulator LWJ-OpenGL-M3G @liang.wu");
    }

    public static Emulator3D getInstance() {
        if (instance == null) {
            instance = new Emulator3D();
        }

        return instance;
    }

    private static PixelFormat method500() {
        if (pixelFormat == null) {
            int var0 = 4;
            int var1 = Emulator.getEmulator().getScreenDepth();

            while (true) {
                try {
                    pixelFormat = new PixelFormat(var1, 0, 24, 0, var0);
                    (new Pbuffer(1, 1, pixelFormat, (RenderTexture) null, (Drawable) null)).destroy();
                    break;
                } catch (Exception var3) {
                    if ((var0 >>= 1) == 0) {
                        pixelFormat = new PixelFormat(var1, 0, 24, 0, 0);
                        break;
                    }
                }
            }
        }

        return pixelFormat;
    }

    public final void bindTarget(Object var1) {
        int w;
        int h;
        if (var1 instanceof Graphics) {
            this.target = var1;
            w = ((Graphics) this.target).getImage().getWidth();
            h = ((Graphics) this.target).getImage().getHeight();
        } else {
            if (!(var1 instanceof Image2D)) {
                throw new IllegalArgumentException();
            }

            this.target = var1;
            w = ((Image2D) this.target).getWidth();
            h = ((Image2D) this.target).getHeight();
        }

        try {
            try {
                String var4 = w + "x" + h;
                pbufferContext = (Pbuffer) contexts.get(var4);
                if (pbufferContext == null) {
                    pbufferContext = new Pbuffer(w, h, method500(), (RenderTexture) null, (Drawable) null);
                    contexts.put(var4, pbufferContext);
                }

                pbufferContext.makeCurrent();
            } catch (Exception var5) {
//                this.method499(var2, var3);
            }

            if (targetWidth != w || targetHeight != h) {
                if (Settings.g2d == 1) {
                    awtBufferImage = new BufferedImage(w, h, 4);
                } else {
                    swtBufferImage = new ImageData(w, h, 32, swtPalleteData);
                }

                buffer = BufferUtils.createByteBuffer(w * h * 4);
                targetWidth = w;
                targetHeight = h;
            }

            GL11.glEnable(3089);
            GL11.glEnable(2977);
            GL11.glPixelStorei(3317, 1);
        } catch (Exception var6) {
            this.target = null;
            throw new IllegalArgumentException();
        }
    }

    public final void releaseTarget() {
//        System.out.println("releaseTarget");
        GL11.glFinish();
        this.method503();
        this.target = null;
        if (pbufferContext != null) {
            try {
                pbufferContext.releaseContext(); // XXX renamed from release()
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.releaseContext();
        }
    }

    private static boolean useGL13() {
        return pbufferContext != null;
    }

    private void method499(int var1, int var2) {
        try {
            Class.forName("org.eclipse.swt.internal.opengl.win32.WGL");
            if (this.swtImage == null || this.swtImage.getBounds().width != var1 || this.swtImage.getBounds().height != var2) {
                if (this.swtImage != null) {
                    this.swtImage.dispose();
                    this.swtGC.dispose();
                    WGL.wglDeleteContext(this.wglContextHandle);
                }

                ImageData var3 = new ImageData(var1, var2, 32, swtPalleteData);
                this.swtImage = new Image((Device) null, var3);
                this.swtGC = new GC(this.swtImage);
                PIXELFORMATDESCRIPTOR var4;
                (var4 = new PIXELFORMATDESCRIPTOR()).nSize = 40;
                var4.nVersion = 1;
                var4.dwFlags = 57;
                var4.iPixelType = 0;
                var4.cColorBits = (byte) Emulator.getEmulator().getScreenDepth();
                var4.iLayerType = 0;
                int var5;
                if ((var5 = WGL.ChoosePixelFormat(this.swtGC.handle, var4)) == 0 || !WGL.SetPixelFormat(this.swtGC.handle, var5, var4)) {
                    this.swtGC.dispose();
                    this.swtImage.dispose();
                    throw new IllegalArgumentException();
                }

                this.wglContextHandle = WGL.wglCreateContext(this.swtGC.handle);
                if (this.wglContextHandle == 0) {
                    this.swtGC.dispose();
                    this.swtImage.dispose();
                    throw new IllegalArgumentException();
                }
            }

            if (WGL.wglGetCurrentContext() != this.swtImage.handle) {
                while (WGL.wglGetCurrentContext() > 0) {
                    ;
                }

                WGL.wglMakeCurrent(this.swtGC.handle, this.wglContextHandle);

                try {
                    GLContext.useContext(this.swtImage);
                } catch (Exception var6) {
                    throw new IllegalArgumentException();
                }
            }
        } catch (ClassNotFoundException e) {
        }
    }

    private void releaseContext() {
//        WGL.wglMakeCurrent(this.swtGC.handle, -1);
        try {
            GLContext.useContext((Object) null);
        } catch (Exception e) {
            ;
        }
    }

    public final void method503() {
        if (this.target != null) {
            int var3;
            int var4;
            int var5;
            if (this.target instanceof Image2D) {
                Image2D var9 = (Image2D) this.target;
                buffer.rewind();
                GL11.glReadPixels(0, 0, targetWidth, targetHeight, 6408, 5121, buffer);
                byte[] var11 = new byte[targetWidth * targetHeight * 4];
                var3 = targetWidth << 2;
                var4 = var11.length - var3;

                for (var5 = targetHeight; var5 > 0; --var5) {
                    buffer.get(var11, var4, var3);
                    var4 -= var3;
                }

                if (var9.getFormat() == 100) {
                    var9.set(0, 0, var9.getWidth(), var9.getHeight(), var11);
                } else {
                    byte[] var13 = new byte[var9.getWidth() * var9.getHeight() * 3];
                    int var6 = var11.length - 1;

                    for (int var7 = var13.length - 1; var7 >= 0; var13[var7--] = var11[var6--]) {
                        --var6;
                        var13[var7--] = var11[var6--];
                        var13[var7--] = var11[var6--];
                    }

                    var9.set(0, 0, var9.getWidth(), var9.getHeight(), var13);
                }
            } else if (Settings.g2d == 0) {
                buffer.rewind();
                GL11.glReadPixels(0, 0, targetWidth, targetHeight, 6408, 5121, buffer);
                int var8 = swtBufferImage.width << 2;
                int var10 = swtBufferImage.data.length - var8;

                for (var3 = swtBufferImage.height; var3 > 0; --var3) {
                    buffer.get(swtBufferImage.data, var10, var8);
                    var10 -= var8;
                }

                Image var12 = new Image((Device) null, swtBufferImage);
                ((Graphics2DSWT) ((Graphics) this.target).getImpl()).method138().drawImage(var12, 0, 0);
                var12.dispose();
            } else {
                buffer.rewind();
                GL11.glReadPixels(0, 0, targetWidth, targetHeight, 6408, 5121, buffer);
                int[] var1 = ((DataBufferInt) awtBufferImage.getRaster().getDataBuffer()).getData();
                IntBuffer var2 = buffer.asIntBuffer();
                var3 = targetWidth;
                var4 = var1.length - var3;

                for (var5 = targetHeight; var5 > 0; --var5) {
                    var2.get(var1, var4, var3);
                    var4 -= var3;
                }

                ((emulator.graphics2D.awt.b) ((Graphics) this.target).getImpl()).g().drawImage(awtBufferImage, 0, 0, (ImageObserver) null);
            }
        }
    }

    public final void enableDepthBuffer(boolean var1) {
        this.depthBufferEnabled = var1;
    }

    public final boolean isDepthBufferEnabled() {
        return this.depthBufferEnabled;
    }

    public final void setHints(int var1) {
        this.hints = var1;
        if (this.target != null) {
            this.setHintsInternal();
        }

    }

    private void setHintsInternal() {
        if ((this.hints & 2) != 0) {
            GL11.glEnable(2832);
            GL11.glEnable(2848);
            GL11.glEnable(2881);
        } else {
            GL11.glDisable(2832);
            GL11.glDisable(2848);
            GL11.glDisable(2881);
        }

        if ((this.hints & 4) != 0) {
            GL11.glEnable(3024);
        } else {
            GL11.glDisable(3024);
        }
    }

    public final int getHints() {
        return this.hints;
    }

    public final Hashtable getProperties() {
        return properties;
    }

    public final void setDepthRange(float var1, float var2) {
        this.depthRangeNear = var1;
        this.depthRangeFar = var2;
    }

    private void depth() {
        GL11.glDepthRange((double) this.depthRangeNear, (double) this.depthRangeFar);
    }

    public final void setViewport(int var1, int var2, int var3, int var4) {
        this.viewportX = var1;
        this.viewportY = var2;
        this.viewportWidth = var3;
        this.viewportHeight = var4;
    }

    private void viewport() {
        GL11.glViewport(this.viewportX, targetHeight - this.viewportY - this.viewportHeight, this.viewportWidth, this.viewportHeight);
        GL11.glScissor(this.viewportX, targetHeight - this.viewportY - this.viewportHeight, this.viewportWidth, this.viewportHeight);
    }

    public final void clearBackgound(Object var1) {
        Background var2 = (Background) var1;
        this.viewport();
        this.depth();
        GL11.glClearDepth(1.0D);
        GL11.glDepthMask(true);
        GL11.glColorMask(true, true, true, true);
        int var10000 = var2 != null && !Settings.xrayView ? var2.getColor() : 0;
        int var3 = var10000;
        GL11.glClearColor(G3DUtils.method601(var10000, 16), G3DUtils.method601(var3, 8), G3DUtils.method601(var3, 0), G3DUtils.method601(var3, 24));
        if (var2 != null && !Settings.xrayView) {
            GL11.glClear((var2.isColorClearEnabled() ? 16384 : 0) | (this.depthBufferEnabled && var2.isDepthClearEnabled() ? 256 : 0));
            this.method504(var2);
        } else {
            GL11.glClear(16384 | (this.depthBufferEnabled ? 256 : 0));
        }
    }

    private void method504(Background var1) {
        if (var1 != null && var1.getImage() != null && var1.getCropWidth() > 0 && var1.getCropHeight() > 0) {
            GL11.glDisable(2896);
            GL11.glDisable(2912);
            int var2 = var1.getImage().getFormat() == 99 ? 6407 : 6408;
            int var3 = var1.getImage().getWidth();
            int var4 = var1.getImage().getHeight();
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            float var5 = (float) this.viewportWidth;
            float var6 = (float) this.viewportHeight;
            float var7 = var5 / (float) var1.getCropWidth();
            float var8 = var6 / (float) var1.getCropHeight();
            float var9 = var7 * (float) var3;
            float var10 = var8 * (float) var4;
            float var11 = -var5 * (float) var1.getCropX() / (float) var1.getCropWidth() - var5 / 2.0F;
            float var12 = var6 * (float) var1.getCropY() / (float) var1.getCropHeight() + var6 / 2.0F;
            int var13 = 1;
            int var14 = 1;
            if (var1.getImageModeX() == 33) {
                if ((var11 %= var9) > 0.0F) {
                    var11 -= var9;
                }

                var13 = (int) (2.5F + var5 / var9);
                var11 -= (float) (var13 / 2) * var9;
            }

            if (var1.getImageModeY() == 33) {
                var12 %= var10;
                var14 = (int) (2.5F + var6 / var10);
                var12 += (float) (var14 / 2) * var10;
            }

            GL11.glPixelStorei(3314, var3);
            GL11.glPixelStorei(3315, 0);
            GL11.glPixelStorei(3316, 0);
            GL11.glDepthFunc(519);
            GL11.glDepthMask(false);
            GL11.glPixelZoom(var7, -var8);
            ByteBuffer var15 = Buffers.method520(var1.getImage().getImageData());

            for (int var16 = 0; var16 < var14; ++var16) {
                for (int var17 = 0; var17 < var13; ++var17) {
                    GL11.glRasterPos4f(0.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glBitmap(0, 0, 0.0F, 0.0F, var11 + (float) var17 * var9, var12 - (float) var16 * var10, var15);
                    GL11.glDrawPixels(var3, var4, var2, 5121, var15);
                }
            }

            GL11.glPixelStorei(3314, 0);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5889);
            GL11.glPopMatrix();
        }

    }

    private static void method518() {
        if (f.aCamera1137 != null) {
            Transform var0 = new Transform();
            f.aCamera1137.getProjection(var0);
            var0.transpose();
            GL11.glMatrixMode(5889);
            GL11.glLoadMatrix(Buffers.method527(var0.getImpl().matrix));
            var0.set(f.aTransform1138);
            var0.transpose();
            GL11.glMatrixMode(5888);
            GL11.glLoadMatrix(Buffers.method527(var0.getImpl().matrix));
        }
    }

    private static void method505(Vector var0, Vector var1, int var2) {
        Transform var3 = new Transform();
        int var4 = var0.size();
        int var5 = ((Integer) properties.get("maxLights")).intValue();
        if (var4 > var5) {
            var4 = var5;
        }

        int var6;
        for (var6 = 0; var6 < var5; ++var6) {
            GL11.glDisable(16384 + var6);
        }

        for (var6 = 0; var6 < var4; ++var6) {
            Light var7;
            if ((var7 = (Light) var0.get(var6)) != null && (var7.getScope() & var2) != 0 && emulator.graphics3D.m3g.d.method788().method793(var7)) {
                Transform var8;
                if ((var8 = (Transform) var1.get(var6)) != null) {
                    var3.set(var8);
                } else {
                    var3.setIdentity();
                }

                var3.transpose();
                GL11.glPushMatrix();
                GL11.glMatrixMode(5888);
                GL11.glMultMatrix(Buffers.method527(var3.getImpl().matrix));
                float[] var9;
                (var9 = new float[4])[3] = 1.0F;
                GL11.glLight(16384 + var6, 4608, Buffers.method527(var9));
                GL11.glLight(16384 + var6, 4609, Buffers.method527(var9));
                GL11.glLight(16384 + var6, 4610, Buffers.method527(var9));
                GL11.glLightf(16384 + var6, 4615, 1.0F);
                GL11.glLightf(16384 + var6, 4616, 0.0F);
                GL11.glLightf(16384 + var6, 4617, 0.0F);
                GL11.glLightf(16384 + var6, 4614, 180.0F);
                GL11.glLightf(16384 + var6, 4613, 0.0F);
                int var10000;
                short var10001;
                float[] var10002;
                if (var7.getMode() != 129) {
                    var10000 = 16384 + var6;
                    var10001 = 4611;
                    var10002 = emulator.graphics3D.m3g.a.aFloatArray1149;
                } else {
                    var10000 = 16384 + var6;
                    var10001 = 4611;
                    var10002 = emulator.graphics3D.m3g.a.aFloatArray1151;
                }

                label48:
                {
                    GL11.glLight(var10000, var10001, Buffers.method527(var10002));
                    GL11.glLight(16384 + var6, 4612, Buffers.method527(emulator.graphics3D.m3g.a.aFloatArray1153));
                    float var10 = var7.getIntensity();
                    G3DUtils.method602(var9, var7.getColor());
                    var9[3] = 1.0F;
                    var9[0] *= var10;
                    var9[1] *= var10;
                    var9[2] *= var10;
                    float var11 = var7.getConstantAttenuation();
                    float var12 = var7.getLinearAttenuation();
                    float var13 = var7.getQuadraticAttenuation();
                    float var16;
                    switch (var7.getMode()) {
                        case 128:
                            GL11.glLight(16384 + var6, 4608, Buffers.method527(var9));
                            break label48;
                        case 129:
                            GL11.glLight(16384 + var6, 4609, Buffers.method527(var9));
                            GL11.glLight(16384 + var6, 4610, Buffers.method527(var9));
                            break label48;
                        case 130:
                            GL11.glLight(16384 + var6, 4609, Buffers.method527(var9));
                            GL11.glLight(16384 + var6, 4610, Buffers.method527(var9));
                            GL11.glLightf(16384 + var6, 4615, var11);
                            GL11.glLightf(16384 + var6, 4616, var12);
                            var10000 = 16384 + var6;
                            var10001 = 4617;
                            var16 = var13;
                            break;
                        case 131:
                            GL11.glLight(16384 + var6, 4609, Buffers.method527(var9));
                            GL11.glLight(16384 + var6, 4610, Buffers.method527(var9));
                            GL11.glLightf(16384 + var6, 4615, var11);
                            GL11.glLightf(16384 + var6, 4616, var12);
                            GL11.glLightf(16384 + var6, 4617, var13);
                            float var14 = var7.getSpotAngle();
                            float var15 = var7.getSpotExponent();
                            GL11.glLightf(16384 + var6, 4614, var14);
                            var10000 = 16384 + var6;
                            var10001 = 4613;
                            var16 = var15;
                            break;
                        default:
                            break label48;
                    }

                    GL11.glLightf(var10000, var10001, var16);
                }

                GL11.glEnable(16384 + var6);
                GL11.glPopMatrix();
            }
        }

    }

    public final void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5) {
        this.renderInternal(var1, var2, var3, var4, var5, 1.0F);
    }

    private void renderInternal(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5, float var6) {
        if ((f.aCamera1137.getScope() & var5) != 0) {
            this.viewport();
            this.depth();
            method518();
            method505(emulator.graphics3D.m3g.a.aVector1150, emulator.graphics3D.m3g.a.aVector1152, var5);
            if (var4 != null) {
                Transform var7;
                (var7 = new Transform()).set(var4);
                var7.transpose();
                GL11.glMultMatrix(Buffers.method527(var7.getImpl().matrix));
            }

            this.method511(var3, var6, false);
            this.method507(var1, var2, var3, var6);
        }
    }

    private void method511(Appearance var1, float var2, boolean var3) {
        if (!var3) {
            method513(var1.getPolygonMode());
        }

        this.method510(var1.getCompositingMode());
        if (!var3) {
            method512(var1.getMaterial(), var2);
        }

        method506(var1.getFog());
    }

    private static void method513(PolygonMode var0) {
        if (var0 == null) {
            var0 = new PolygonMode();
        }

        GL11.glPolygonMode(1032, Settings.xrayView ? 6913 : 6914);
        int var1;
        if ((var1 = var0.getCulling()) == 162) {
            GL11.glDisable(2884);
        } else {
            GL11.glEnable(2884);
            GL11.glCullFace(var1 == 161 ? 1028 : 1029);
        }

        GL11.glShadeModel(var0.getShading() == 164 ? 7424 : 7425);
        GL11.glFrontFace(var0.getWinding() == 169 ? 2304 : 2305);
        GL11.glLightModelf(2898, var0.isTwoSidedLightingEnabled() ? 1.0F : 0.0F);
        GL11.glLightModelf(2897, var0.isLocalCameraLightingEnabled() ? 1.0F : 0.0F);
        GL11.glHint(3152, var0.isPerspectiveCorrectionEnabled() ? 4354 : 4353);
    }

    private void method510(CompositingMode var1) {
        if (var1 == null) {
            var1 = new CompositingMode();
        }

        if (this.depthBufferEnabled) {
            GL11.glEnable(2929);
        } else {
            GL11.glDisable(2929);
        }

        GL11.glDepthMask(var1.isDepthWriteEnabled());
        GL11.glDepthFunc(var1.isDepthTestEnabled() ? 515 : 519);
        GL11.glColorMask(var1.isColorWriteEnabled(), var1.isColorWriteEnabled(), var1.isColorWriteEnabled(), var1.isAlphaWriteEnabled());
        GL11.glAlphaFunc(518, var1.getAlphaThreshold());
        if (var1.getAlphaThreshold() == 0.0F) {
            GL11.glDisable(3008);
        } else {
            GL11.glEnable(3008);
        }

        label51:
        {
            short var10000;
            short var10001;
            label50:
            {
                GL11.glEnable(3042);
                switch (var1.getBlending()) {
                    case 64:
                        var10000 = 770;
                        var10001 = 771;
                        break label50;
                    case 65:
                        var10000 = 770;
                        var10001 = 1;
                        break label50;
                    case 66:
                        var10000 = 774;
                        break;
                    case 67:
                        var10000 = 774;
                        var10001 = 768;
                        break label50;
                    case 68:
                        var10000 = 1;
                        break;
                    default:
                        break label51;
                }

                var10001 = 0;
            }

            GL11.glBlendFunc(var10000, var10001);
        }

        GL11.glPolygonOffset(var1.getDepthOffsetFactor(), var1.getDepthOffsetUnits());
        if (var1.getDepthOffsetFactor() == 0.0F && var1.getDepthOffsetUnits() == 0.0F) {
            GL11.glDisable(Settings.xrayView ? 10754 : '\u8037');
        } else {
            GL11.glEnable(Settings.xrayView ? 10754 : '\u8037');
        }
    }

    private static void method512(Material var0, float var1) {
        short var10000;
        if (var0 != null) {
            GL11.glEnable(2896);
            float[] var2;
            G3DUtils.method602(var2 = new float[4], var0.getColor(1024));
            GL11.glMaterial(1032, 4608, Buffers.method527(var2));
            G3DUtils.method602(var2, var0.getColor(2048));
            var2[3] *= var1;
            GL11.glMaterial(1032, 4609, Buffers.method527(var2));
            G3DUtils.method602(var2, var0.getColor(4096));
            GL11.glMaterial(1032, 5632, Buffers.method527(var2));
            G3DUtils.method602(var2, var0.getColor(8192));
            GL11.glMaterial(1032, 4610, Buffers.method527(var2));
            float var3 = var0.getShininess();
            GL11.glMaterialf(1032, 5633, var3);
            if (var0.isVertexColorTrackingEnabled()) {
                GL11.glEnable(2903);
                GL11.glColorMaterial(1032, 5634);
                return;
            }

            var10000 = 2903;
        } else {
            var10000 = 2896;
        }

        GL11.glDisable(var10000);
    }

    private static void method506(Fog var0) {
        if (var0 != null && !Settings.xrayView) {
            GL11.glEnable(2912);
            GL11.glFogi(2917, var0.getMode() == 81 ? 9729 : 2048);
            float[] var1;
            G3DUtils.method602(var1 = new float[4], var0.getColor());
            var1[3] = 1.0F;
            GL11.glFog(2918, Buffers.method527(var1));
            GL11.glFogf(2915, var0.getNearDistance());
            GL11.glFogf(2916, var0.getFarDistance());
            GL11.glFogf(2914, var0.getDensity());
        } else {
            GL11.glDisable(2912);
        }
    }

    private void method507(VertexBuffer var1, IndexBuffer var2, Appearance var3, float var4) {
        VertexArray var5;
        byte[] var23;
        short[] var24;
        if ((var5 = var1.getColors()) == null) {
            int var6;
            GL11.glColor4ub((byte) ((var6 = var1.getDefaultColor()) >> 16 & 255), (byte) (var6 >> 8 & 255), (byte) (var6 & 255), (byte) ((int) ((float) (var6 >> 24 & 255) * var4)));
            GL11.glDisableClientState('\u8076');
        } else {
            GL11.glEnableClientState('\u8076');
            if (var5.getComponentType() == 1) {
                var23 = new byte[var5.getComponentCount() * var5.getVertexCount()];
                var5.get(0, var5.getVertexCount(), var23);
                GL11.glColorPointer(var4 == 1.0F ? var5.getComponentCount() : 4, true, 0, Buffers.method522(var23, var4, var5.getVertexCount()));
            } else {
                System.err.println("short buffers not supported!!!!1");
                throw new RuntimeException();
//                var24 = new short[var5.getComponentCount() * var5.getVertexCount()];
//                var5.get(0, var5.getVertexCount(), var24);
//                GL11.glColorPointer(var4 == 1.0F ? var5.getComponentCount() : 4, true, 0, a.method523(var24, var4, var5.getVertexCount()));
            }
        }

        if ((var5 = var1.getNormals()) != null && var3.getMaterial() != null) {
            GL11.glEnableClientState('\u8075');
            if (var5.getComponentType() == 1) {
                var23 = new byte[var5.getComponentCount() * var5.getVertexCount()];
                var5.get(0, var5.getVertexCount(), var23);
                GL11.glNormalPointer(0, Buffers.method520(var23));
            } else {
                System.err.println("short buffers not supported!!!!");
                throw new RuntimeException();
//                var24 = new short[var5.getComponentCount() * var5.getVertexCount()];
//                var5.get(0, var5.getVertexCount(), var24);
//                GL11.glNormalPointer(0, a.method521(var24));
            }
        } else {
            GL11.glDisableClientState('\u8075');
        }

        float[] var26 = new float[4];
        var5 = var1.getPositions(var26);
        GL11.glEnableClientState('\u8074');
        if (var5.getComponentType() == 1) {
            byte[] var7 = new byte[var5.getComponentCount() * var5.getVertexCount()];
            var5.get(0, var5.getVertexCount(), var7);
            GL11.glVertexPointer(var5.getComponentCount(), 0, Buffers.method530(var7));
        } else {
            short[] var25 = new short[var5.getComponentCount() * var5.getVertexCount()];
            var5.get(0, var5.getVertexCount(), var25);
            GL11.glVertexPointer(var5.getComponentCount(), 0, Buffers.method529(var25));
        }

        GL11.glMatrixMode(5888);
        GL11.glTranslatef(var26[1], var26[2], var26[3]);
        GL11.glScalef(var26[0], var26[0], var26[0]);
        TriangleStripArray var27;
        int var8 = (var27 = (TriangleStripArray) var2).getStripCount();
        int var9;
        int[] var22;
        if (var3 != null && !Settings.xrayView) {
            var9 = Graphics3D.NumTextureUnits;
            IntBuffer var10;
            GL11.glGenTextures(var10 = BufferUtils.createIntBuffer(Graphics3D.NumTextureUnits));

            int var11;
            for (var11 = 0; var11 < var9; ++var11) {
                Texture2D var12 = var3.getTexture(var11);
                var26[3] = 0.0F;
                var5 = var1.getTexCoords(var11, var26);
                if (var12 != null && var5 != null) {
                    if (useGL13()) {
                        GL13.glActiveTexture('\u84c0' + var11);
                        GL13.glClientActiveTexture('\u84c0' + var11);
                    }

                    short var10000;
                    label141:
                    {
                        GL11.glEnable(3553);
                        GL11.glBindTexture(3553, var10.get(var11));
                        short var10001;
                        short var10002;
                        switch (var12.getBlending()) {
                            case 224:
                                var10000 = 8960;
                                var10001 = 8704;
                                var10002 = 260;
                                break;
                            case 225:
                                var10000 = 8960;
                                var10001 = 8704;
                                var10002 = 3042;
                                break;
                            case 226:
                                var10000 = 8960;
                                var10001 = 8704;
                                var10002 = 8449;
                                break;
                            case 227:
                                var10000 = 8960;
                                var10001 = 8704;
                                var10002 = 8448;
                                break;
                            case 228:
                                var10000 = 8960;
                                var10001 = 8704;
                                var10002 = 7681;
                                break;
                            default:
                                break label141;
                        }

                        GL11.glTexEnvi(var10000, var10001, var10002);
                    }

                    Image2D var15;
                    short var16;
                    label135:
                    {
                        float[] var14;
                        G3DUtils.method602(var14 = new float[4], var12.getBlendColor());
                        var14[3] = 1.0F;
                        GL11.glTexEnv(8960, 8705, Buffers.method527(var14));
                        var15 = var12.getImage();
                        var16 = 6407;
                        switch (var15.getFormat()) {
                            case 96:
                                var10000 = 6406;
                                break;
                            case 97:
                                var10000 = 6409;
                                break;
                            case 98:
                                var10000 = 6410;
                                break;
                            case 99:
                                var10000 = 6407;
                                break;
                            case 100:
                                var10000 = 6408;
                                break;
                            default:
                                break label135;
                        }

                        var16 = var10000;
                    }

                    short var19;
                    label128:
                    {
                        GL11.glTexImage2D(3553, 0, var16, var15.getWidth(), var15.getHeight(), 0, var16, 5121, Buffers.method528(var15.getImageData()));
                        GL11.glTexParameterf(3553, 10242, var12.getWrappingS() == 240 ? 33071.0F : 10497.0F);
                        GL11.glTexParameterf(3553, 10243, var12.getWrappingT() == 240 ? 33071.0F : 10497.0F);
                        int var17 = var12.getLevelFilter();
                        int var18 = var12.getImageFilter();
                        if (var17 == 208) {
                            if (var18 == 210) {
                                var19 = 9728;
                                var10000 = 9728;
                                break label128;
                            }

                            var10000 = 9729;
                        } else if (var17 == 209) {
                            if (var18 == 210) {
                                var19 = 9985;
                                var10000 = 9728;
                                break label128;
                            }

                            var10000 = 9987;
                        } else {
                            if (var18 == 210) {
                                var19 = 9984;
                                var10000 = 9728;
                                break label128;
                            }

                            var10000 = 9986;
                        }

                        var19 = var10000;
                        var10000 = 9729;
                    }

                    short var20 = var10000;
                    GL11.glTexParameteri(3553, 10241, var20);
                    GL11.glTexParameteri(3553, 10240, var19);
                    GL11.glEnableClientState('\u8078');
                    byte var28;
                    IntBuffer var29;
                    int var32;
                    if (var5.getComponentType() == 1) {
                        byte[] var21 = new byte[var5.getComponentCount() * var5.getVertexCount()];
                        var5.get(0, var5.getVertexCount(), var21);
                        var32 = var5.getComponentCount();
                        var28 = 0;
                        var29 = Buffers.method526(var21);
                    } else {
                        short[] var30 = new short[var5.getComponentCount() * var5.getVertexCount()];
                        var5.get(0, var5.getVertexCount(), var30);
                        var32 = var5.getComponentCount();
                        var28 = 0;
                        var29 = Buffers.method525(var30);
                    }

                    GL11.glTexCoordPointer(var32, var28, var29);
                    Transform var31 = new Transform();
                    var12.getCompositeTransform(var31);
                    var31.transpose();
                    GL11.glMatrixMode(5890);
                    GL11.glLoadMatrix(Buffers.method527(var31.getImpl().matrix));
                    GL11.glTranslatef(var26[1], var26[2], var26[3]);
                    GL11.glScalef(var26[0], var26[0], var26[0]);
                }
            }

            for (var11 = 0; var11 < var8; ++var11) {
                var22 = var27.getIndexStrip(var11);
                GL11.glDrawElements(5, Buffers.method524(var22));
            }

            for (var11 = 0; var11 < var9; ++var11) {
                if (GL11.glIsTexture(var10.get(var11))) {
                    if (useGL13()) {
                        GL13.glActiveTexture('\u84c0' + var11);
                        GL13.glClientActiveTexture('\u84c0' + var11);
                    }

                    GL11.glDisableClientState('\u8078');
                    GL11.glDisable(3553);
                }
            }

            GL11.glDeleteTextures(var10);
        } else {
            for (var9 = 0; var9 < var8; ++var9) {
                var22 = var27.getIndexStrip(var9);
                GL11.glDrawElements(5, Buffers.method524(var22));
            }

        }
    }

    public final void render(World var1) {
        Transform var2 = new Transform();
        this.clearBackgound(var1.getBackground());
        var1.getActiveCamera().getTransformTo(var1, var2);
        f.method785(var1.getActiveCamera(), var2);
        emulator.graphics3D.m3g.a.method798(var1);
        emulator.graphics3D.m3g.d.method788().method795(var1, (Transform) null);
        this.method519();
    }

    public final void render(Node var1, Transform var2) {
        emulator.graphics3D.m3g.d.method788().method795(var1, var2);
        this.method519();
    }

    private void method519() {
        for (int var1 = 0; var1 < emulator.graphics3D.m3g.d.method788().method790(); ++var1) {
            emulator.graphics3D.m3g.c var2;
            if ((var2 = emulator.graphics3D.m3g.d.method788().method792(var1)).aNode1140 instanceof Mesh) {
                Mesh var3;
                IndexBuffer var4 = (var3 = (Mesh) var2.aNode1140).getIndexBuffer(var2.anInt1142);
                Appearance var5 = var3.getAppearance(var2.anInt1142);
                if (var4 != null && var5 != null) {
                    VertexBuffer var6 = e.getInstance().method779(var3);
                    this.renderInternal(var6, var4, var5, var2.aTransform1141, var3.getScope(), var2.aFloat1143);
                }
            } else {
                this.method509((Sprite3D) var2.aNode1140, var2.aTransform1141);
            }
        }

        emulator.graphics3D.m3g.d.method788().method791();
        e.getInstance().method778();
    }

    private void method509(Sprite3D var1, Transform var2) {
        emulator.graphics3D.b var3 = new emulator.graphics3D.b(0.0F, 0.0F, 0.0F, 1.0F);
        emulator.graphics3D.b var4 = new emulator.graphics3D.b(1.0F, 0.0F, 0.0F, 1.0F);
        emulator.graphics3D.b var5 = new emulator.graphics3D.b(0.0F, 1.0F, 0.0F, 1.0F);
        Transform var6;
        (var6 = new Transform(f.aTransform1138)).postMultiply(var2);
        var6.getImpl().method440(var3);
        var6.getImpl().method440(var4);
        var6.getImpl().method440(var5);
        emulator.graphics3D.b var7 = new emulator.graphics3D.b(var3);
        var3.method425(1.0F / var3.aFloat614);
        var4.method425(1.0F / var4.aFloat614);
        var5.method425(1.0F / var5.aFloat614);
        var4.method431(var3);
        var5.method431(var3);
        emulator.graphics3D.b var8 = new emulator.graphics3D.b(var4.method424(), 0.0F, 0.0F, 0.0F);
        emulator.graphics3D.b var9 = new emulator.graphics3D.b(0.0F, var5.method424(), 0.0F, 0.0F);
        var8.method429(var7);
        var9.method429(var7);
        Transform var10 = new Transform();
        f.aCamera1137.getProjection(var10);
        var10.getImpl().method440(var7);
        var10.getImpl().method440(var8);
        var10.getImpl().method440(var9);
        if (var7.aFloat614 > 0.0F && -var7.aFloat614 < var7.aFloat612 && var7.aFloat612 <= var7.aFloat614) {
            var7.method425(1.0F / var7.aFloat614);
            var8.method425(1.0F / var8.aFloat614);
            var9.method425(1.0F / var9.aFloat614);
            var8.method431(var7);
            var9.method431(var7);
            boolean var11 = var1.isScaled();
            int[] var12;
            boolean var13 = (var12 = new int[]{var1.getCropX(), var1.getCropY(), var1.getCropWidth(), var1.getCropHeight()})[2] < 0;
            boolean var14 = var12[3] < 0;
            var12[2] = Math.abs(var12[2]);
            var12[3] = Math.abs(var12[3]);
            float var15 = 1.0F;
            float var16 = 1.0F;
            float var17 = (float) ((var13 ? var12[2] : -var12[2]) / 2);
            float var18 = (float) ((var14 ? -var12[3] : var12[3]) / 2);
            float var19;
            float var20;
            if (!var11) {
                if (var13) {
                    var15 = -1.0F;
                }

                if (var14) {
                    var16 = -1.0F;
                }

                var19 = (float) var12[2];
                var20 = (float) var12[3];
            } else {
                var15 = var8.method424() * (float) this.viewportWidth * 0.5F;
                var16 = var9.method424() * (float) this.viewportHeight * 0.5F;
                var19 = var15;
                var20 = var16;
                var17 = -var15 / 2.0F;
                var18 = var16 / 2.0F;
                if (var13) {
                    var17 += var15;
                }

                if (var14) {
                    var18 -= var16;
                }

                var15 /= var13 ? -((float) var12[2]) : (float) var12[2];
                var16 /= var14 ? -((float) var12[3]) : (float) var12[3];
            }

            int[] var21 = new int[4];
            if (G3DUtils.method608(var12[0], var12[1], var12[2], var12[3], 0, 0, var1.getImage().getWidth(), var1.getImage().getHeight(), var21)) {
                float var10000;
                label96:
                {
                    if (!var13) {
                        var10000 = var17 - var15 * (float) (var12[0] - var21[0]);
                    } else {
                        if (var12[0] <= 0) {
                            break label96;
                        }

                        var10000 = var17 + var15 * (float) (var12[0] - var21[0]);
                    }

                    var17 = var10000;
                }

                label90:
                {
                    if (!var14) {
                        var10000 = var18 + var16 * (float) (var12[1] - var21[1]);
                    } else {
                        if (var12[1] <= 0) {
                            break label90;
                        }

                        var10000 = var18 - var16 * (float) (var12[1] - var21[1]);
                    }

                    var18 = var10000;
                }

                ByteBuffer var27;
                short var28;
                label84:
                {
                    Transform var22;
                    (var22 = new Transform()).postScale((float) this.viewportWidth / ((float) this.viewportWidth + var19), (float) this.viewportHeight / ((float) this.viewportHeight + var20), 1.0F);
                    var22.postMultiply(var10);
                    var10.set(var22);
                    int var23 = (int) ((float) this.viewportX - var19 / 2.0F);
                    int var24 = (int) ((float) this.viewportY - var20 / 2.0F);
                    int var25 = (int) ((float) this.viewportWidth + var19);
                    int var26 = (int) ((float) this.viewportHeight + var20);
                    var10.transpose();
                    var6.transpose();
                    GL11.glViewport(var23, targetHeight - var24 - var26, var25, var26);
                    GL11.glMatrixMode(5889);
                    GL11.glLoadMatrix(Buffers.method527(var10.getImpl().matrix));
                    GL11.glMatrixMode(5888);
                    GL11.glLoadMatrix(Buffers.method527(var6.getImpl().matrix));
                    GL11.glDisable(2896);
                    var27 = Buffers.method520(var1.getImage().getImageData());
                    GL11.glRasterPos4f(0.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glPixelStorei(3314, var1.getImage().getWidth());
                    GL11.glPixelStorei(3315, var21[1]);
                    GL11.glPixelStorei(3316, var21[0]);
                    GL11.glBitmap(0, 0, 0.0F, 0.0F, var17, var18, var27);
                    GL11.glPixelZoom(var15, -var16);
                    var28 = 6407;
                    short var29;
                    switch (var1.getImage().getFormat()) {
                        case 96:
                            var29 = 6406;
                            break;
                        case 97:
                            var29 = 6409;
                            break;
                        case 98:
                            var29 = 6410;
                            break;
                        case 99:
                            var29 = 6407;
                            break;
                        case 100:
                            var29 = 6408;
                            break;
                        default:
                            break label84;
                    }

                    var28 = var29;
                }

                this.method511(var1.getAppearance(), 1.0F, true);
                GL11.glDrawPixels(var21[2], var21[3], var28, 5121, var27);
                GL11.glPixelStorei(3314, 0);
                GL11.glPixelStorei(3315, 0);
                GL11.glPixelStorei(3316, 0);
            }
        }
    }

    public final void v3bind(Graphics var1) {
    }

    public final void v3release(Graphics var1) {
    }

    public final void v3flush() {
    }
}