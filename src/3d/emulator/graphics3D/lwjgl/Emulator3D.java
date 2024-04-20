package emulator.graphics3D.lwjgl;

import emulator.graphics3D.*;

import java.util.*;

import emulator.*;
import emulator.graphics3D.m3g.*;
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

import emulator.graphics2D.swt.Graphics2DSWT;

import java.awt.image.*;
import java.nio.*;
import javax.microedition.lcdui.Graphics;
import javax.microedition.m3g.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

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
    private static Vector texturesToRelease = new Vector();

    public static final int MaxViewportWidth = 2048;
    public static final int MaxViewportHeight = 2048;
    public static final int NumTextureUnits = 10;
    public static final int MaxTextureDimension = 1024;
    public static final int MaxSpriteCropDimension = 1024;
    public static final int MaxLights = 8;
    private boolean exiting;

    private Emulator3D() {
        instance = this;
        properties.put("supportAntialiasing", Boolean.TRUE);
        properties.put("supportTrueColor", Boolean.TRUE);
        properties.put("supportDithering", Boolean.TRUE);
        properties.put("supportMipmapping", Boolean.TRUE);
        properties.put("supportPerspectiveCorrection", Boolean.TRUE);
        properties.put("supportLocalCameraLighting", Boolean.TRUE);
        properties.put("maxLights", new Integer(MaxLights));
        properties.put("maxViewportWidth", new Integer(MaxViewportWidth));
        properties.put("maxViewportHeight", new Integer(MaxViewportHeight));
        properties.put("maxViewportDimension", new Integer(2048));
        properties.put("maxTextureDimension", new Integer(MaxTextureDimension));
        properties.put("maxSpriteCropDimension", new Integer(MaxSpriteCropDimension));
        properties.put("maxTransformsPerVertex", new Integer(1000));
        properties.put("numTextureUnits", new Integer(NumTextureUnits));
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
        if(exiting) {
            throw new IllegalStateException("exiting");
        }
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
            } catch (Exception e) {
                e.printStackTrace();
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
            var6.printStackTrace();
            this.target = null;
            throw new IllegalArgumentException();
        }
    }

    public final void releaseTarget() {
        GL11.glFinish();
        this.method503();
        if(exiting) {
            while (texturesToRelease.size() > 0) {
                releaseTexture((Image2D) texturesToRelease.remove(0));
            }
        }
        this.target = null;
        if (pbufferContext != null) {
            try {
                pbufferContext.releaseContext();
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
        if ((hints & Graphics3D.ANTIALIAS) != 0) {
            GL11.glEnable(GL_POINT_SMOOTH);
            GL11.glEnable(GL_LINE_SMOOTH);
            GL11.glEnable(GL_POLYGON_SMOOTH);
//            if(useGL13())
            GL11.glEnable(GL_MULTISAMPLE);
        } else {
            GL11.glDisable(GL_POINT_SMOOTH);
            GL11.glDisable(GL_LINE_SMOOTH);
            GL11.glDisable(GL_POLYGON_SMOOTH);
//            if(useGL13())
            GL11.glDisable(GL_MULTISAMPLE);
        }

        if ((hints & Graphics3D.DITHER) != 0) {
            GL11.glEnable(GL_DITHER);
        } else {
            GL11.glDisable(GL_DITHER);
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
        GL11.glClearColor(G3DUtils.getFloatColor(var10000, 16), G3DUtils.getFloatColor(var3, 8), G3DUtils.getFloatColor(var3, 0), G3DUtils.getFloatColor(var3, 24));
        if (var2 != null && !Settings.xrayView) {
            GL11.glClear((var2.isColorClearEnabled() ? 16384 : 0) | (this.depthBufferEnabled && var2.isDepthClearEnabled() ? 256 : 0));
            this.method504(var2);
        } else {
            GL11.glClear(GL_COLOR_BUFFER_BIT | (this.depthBufferEnabled ? 256 : 0));
        }
    }

    private void method504(Background var1) {
        if (var1 != null && var1.getImage() != null && var1.getCropWidth() > 0 && var1.getCropHeight() > 0) {
            GL11.glDisable(2896);
            GL11.glDisable(2912);
            int var2 = var1.getImage().getFormat() == 99 ? GL_RGB : GL_RGBA;
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
            ByteBuffer var15 = LWJGLUtility.getByteBuffer(var1.getImage().getImageData());

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

    private static void setupCamera() {
        if (CameraCache.camera != null) {
            Transform tmpMat = new Transform();

            CameraCache.camera.getProjection(tmpMat);
            tmpMat.transpose();
            GL11.glMatrixMode(GL_PROJECTION);
            GL11.glLoadMatrix(LWJGLUtility.getFloatBuffer(((Transform3D) tmpMat.getImpl()).m_matrix));

            tmpMat.set(CameraCache.m_model2camTransform);
            tmpMat.transpose();
            GL11.glMatrixMode(GL_MODELVIEW);
            GL11.glLoadMatrix(LWJGLUtility.getFloatBuffer(((Transform3D) tmpMat.getImpl()).m_matrix));
        }
    }

    private static void setupLights(Vector lights, Vector lightMats, int scope) {
        int lightsCount = Math.min(lights.size(), MaxLights);

        for (int i = 0; i < MaxLights; ++i) {
                GL11.glDisable(GL_LIGHT0 + i);
        }

        //ARBColorBufferFloat.glClampColorARB(ARBColorBufferFloat.GL_CLAMP_VERTEX_COLOR_ARB, GL_FALSE);

        Transform tmpMat = new Transform();

        for (int i = 0; i < lightsCount; ++i) {
            Light light = (Light) lights.get(i);

            if (light == null || (light.getScope() & scope) == 0 || !RenderPipe.getInstance().isVisible(light)) {
                continue;
            }

            Transform lightMat = (Transform) lightMats.get(i);

            if (lightMat != null) {
                tmpMat.set(lightMat);
            } else {
                tmpMat.setIdentity();
            }
            tmpMat.transpose();

            GL11.glPushMatrix();
            GL11.glMatrixMode(GL_MODELVIEW);
            GL11.glMultMatrix(LWJGLUtility.getFloatBuffer(((Transform3D) tmpMat.getImpl()).m_matrix));

            int lightId = GL_LIGHT0 + i;
            float[] lightColor = new float[] {0, 0, 0, 1}; //rgba

            //Set default light preferences?
            GL11.glLight(lightId, GL_AMBIENT, LWJGLUtility.getFloatBuffer(lightColor));
            GL11.glLight(lightId, GL_DIFFUSE, LWJGLUtility.getFloatBuffer(lightColor));
            GL11.glLight(lightId, GL_SPECULAR, LWJGLUtility.getFloatBuffer(lightColor));

            GL11.glLightf(lightId, GL_CONSTANT_ATTENUATION, 1.0F);
            GL11.glLightf(lightId, GL_LINEAR_ATTENUATION, 0.0F);
            GL11.glLightf(lightId, GL_QUADRATIC_ATTENUATION, 0.0F);
            GL11.glLightf(lightId, GL_SPOT_CUTOFF, 180.0F);
            GL11.glLightf(lightId, GL_SPOT_EXPONENT, 0.0F);

            //if(light.getMode() != Light.OMNI) continue;

            float[] tmpLightPos;
            if (light.getMode() == Light.DIRECTIONAL) {
                tmpLightPos = LightsCache.POSITIVE_Z_AXIS; //light direction!
            } else {
                tmpLightPos = LightsCache.LOCAL_ORIGIN;
            }

            GL11.glLight(lightId, GL_POSITION, LWJGLUtility.getFloatBuffer(tmpLightPos));
            GL11.glLight(lightId, GL_SPOT_DIRECTION, LWJGLUtility.getFloatBuffer(LightsCache.NEGATIVE_Z_AXIS));

            G3DUtils.fillFloatColor(lightColor, light.getColor());
            float lightIntensity = light.getIntensity();
            lightColor[0] *= lightIntensity;
            lightColor[1] *= lightIntensity;
            lightColor[2] *= lightIntensity;
            lightColor[3] = 1.0F;

            int lightMode = light.getMode();

            if(lightMode == Light.AMBIENT) {
                GL11.glLight(lightId, GL_AMBIENT, LWJGLUtility.getFloatBuffer(lightColor));
            } else {
                GL11.glLight(lightId, GL_DIFFUSE, LWJGLUtility.getFloatBuffer(lightColor));
                GL11.glLight(lightId, GL_SPECULAR, LWJGLUtility.getFloatBuffer(lightColor));
            }

            if(lightMode == Light.SPOT) {
                GL11.glLightf(lightId, GL_SPOT_CUTOFF, light.getSpotAngle());
                GL11.glLightf(lightId, GL_SPOT_EXPONENT, light.getSpotExponent());
            }

            if(lightMode == Light.SPOT || lightMode == Light.OMNI) {
                GL11.glLightf(lightId, GL_CONSTANT_ATTENUATION, light.getConstantAttenuation());
                GL11.glLightf(lightId, GL_LINEAR_ATTENUATION, light.getLinearAttenuation());
                GL11.glLightf(lightId, GL_QUADRATIC_ATTENUATION, light.getQuadraticAttenuation());
            }

            GL11.glEnable(lightId);
            GL11.glPopMatrix();
        }
    }

    public final void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5) {
        this.renderVertex(var1, var2, var3, var4, var5, 1.0F);
    }

    private void renderVertex(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5, float var6) {
        if ((CameraCache.camera.getScope() & var5) != 0) {
            this.viewport();
            this.depth();
            setupCamera();
            setupLights(LightsCache.m_lights, LightsCache.m_lightsTransform, var5);
            if (var4 != null) {
                Transform var7;
                (var7 = new Transform()).set(var4);
                var7.transpose();
                GL11.glMultMatrix(LWJGLUtility.getFloatBuffer(((Transform3D) var7.getImpl()).m_matrix));
            }

            this.appearance(var3, var6, false);
            this.draw(var1, var2, var3, var6);
        }
    }

    private void appearance(Appearance var1, float var2, boolean var3) {
        if (!var3) {
            polygonMode(var1.getPolygonMode());
        }

        this.compositingMode(var1.getCompositingMode());
        if (!var3) {
            material(var1.getMaterial(), var2);
        }

        fog(var1.getFog());
    }

    private static void polygonMode(PolygonMode var0) {
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

    private void compositingMode(CompositingMode var1) {
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

    private static void material(Material var0, float var1) {
        short var10000;
        if (var0 != null) {
            GL11.glEnable(2896);
            float[] var2;
            G3DUtils.fillFloatColor(var2 = new float[4], var0.getColor(1024));
            GL11.glMaterial(1032, 4608, LWJGLUtility.getFloatBuffer(var2));
            G3DUtils.fillFloatColor(var2, var0.getColor(2048));
            var2[3] *= var1;
            GL11.glMaterial(1032, 4609, LWJGLUtility.getFloatBuffer(var2));
            G3DUtils.fillFloatColor(var2, var0.getColor(4096));
            GL11.glMaterial(1032, 5632, LWJGLUtility.getFloatBuffer(var2));
            G3DUtils.fillFloatColor(var2, var0.getColor(8192));
            GL11.glMaterial(1032, 4610, LWJGLUtility.getFloatBuffer(var2));
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

    private static void fog(Fog var0) {
        if (var0 != null && !Settings.xrayView) {
            GL11.glEnable(2912);
            GL11.glFogi(2917, var0.getMode() == 81 ? 9729 : 2048);
            float[] var1;
            G3DUtils.fillFloatColor(var1 = new float[4], var0.getColor());
            var1[3] = 1.0F;
            GL11.glFog(2918, LWJGLUtility.getFloatBuffer(var1));
            GL11.glFogf(2915, var0.getNearDistance());
            GL11.glFogf(2916, var0.getFarDistance());
            GL11.glFogf(2914, var0.getDensity());
        } else {
            GL11.glDisable(2912);
        }
    }

    private void draw(VertexBuffer vertexBuffer, IndexBuffer indexBuffer, Appearance appearance, float alphaFactor) {
        VertexArray texCoords;
        byte[] var23;
        short[] var24;
        if ((texCoords = vertexBuffer.getColors()) == null) {
            int var6;
            GL11.glColor4ub((byte) ((var6 = vertexBuffer.getDefaultColor()) >> 16 & 255), (byte) (var6 >> 8 & 255), (byte) (var6 & 255), (byte) ((int) ((float) (var6 >> 24 & 255) * alphaFactor)));
            GL11.glDisableClientState(32886);
        } else {
            GL11.glEnableClientState(32886);
            if (texCoords.getComponentType() == 1) {
                var23 = texCoords.getByteValues();
                GL11.glColorPointer(alphaFactor == 1.0F ? texCoords.getComponentCount() : 4, 5121, 0, LWJGLUtility.getByteBuffer(var23, alphaFactor, texCoords.getVertexCount()));
            } else {
                var24 = texCoords.getShortValues();
                GL11.glColorPointer(alphaFactor == 1.0F ? texCoords.getComponentCount() : 4, 5121, 0, LWJGLUtility.getByteBuffer(var24, alphaFactor, texCoords.getVertexCount()));
            }
        }

        if ((texCoords = vertexBuffer.getNormals()) != null && appearance.getMaterial() != null) {
            GL11.glEnableClientState(32885);
            if (texCoords.getComponentType() == 1) {
                GL11.glNormalPointer(0, LWJGLUtility.getByteBuffer(texCoords.getByteValues()));
            } else {
                GL11.glNormalPointer(0, LWJGLUtility.getIntBuffer(texCoords.getShortValues()));
            }
        } else {
            GL11.glDisableClientState(32885);
        }

        float[] var26 = new float[4];
        texCoords = vertexBuffer.getPositions(var26);
        GL11.glEnableClientState(32884);
        if (texCoords.getComponentType() == 1) {
            byte[] var7 = texCoords.getByteValues();
            GL11.glVertexPointer(texCoords.getComponentCount(), 0, LWJGLUtility.getIntBuffer(var7));
        } else {
            short[] var25 = texCoords.getShortValues();
            GL11.glVertexPointer(texCoords.getComponentCount(), 0, LWJGLUtility.getShortBuffer(var25));
        }

        GL11.glMatrixMode(5888);
        GL11.glTranslatef(var26[1], var26[2], var26[3]);
        GL11.glScalef(var26[0], var26[0], var26[0]);
        TriangleStripArray triangleStripArray;
        int stripCount = (triangleStripArray = (TriangleStripArray) indexBuffer).getStripCount();
        int i;
        int[] var22;
        if (appearance != null && !Settings.xrayView) {
            int j;
            i = NumTextureUnits;

            for (j = 0; j < i; ++j) {
                Texture2D texture2D = appearance.getTexture(j);
                var26[3] = 0.0F;
                texCoords = vertexBuffer.getTexCoords(j, var26);
                if (texture2D != null && texCoords != null) {
                    Image2D image2D = texture2D.getImage();
                    if (useGL13()) {
                        GL13.glActiveTexture('\u84c0' + j);
                        GL13.glClientActiveTexture('\u84c0' + j);
                    }

                    int id = image2D.getId();
                    if(id == 0) {
                        image2D.setId(id = GL11.glGenTextures());
                        image2D.setLoaded(false);
                        if(!texturesToRelease.contains(image2D))
                            texturesToRelease.add(image2D);
                    }
                    short var10000;
                    label141:
                    {
                        GL11.glEnable(GL_TEXTURE_2D);
                        GL11.glBindTexture(GL_TEXTURE_2D, id);
                        short var10002;
                        switch (texture2D.getBlending()) {
                            case 224:
                                var10002 = 260;
                                break;
                            case 225:
                                var10002 = 3042;
                                break;
                            case 226:
                                var10002 = 8449;
                                break;
                            case 227:
                                var10002 = 8448;
                                break;
                            case 228:
                                var10002 = 7681;
                                break;
                            default:
                                break label141;
                        }
                        GL11.glTexEnvi(89600, 8704, var10002);
                    }

                    float[] var14;
                    G3DUtils.fillFloatColor(var14 = new float[4], texture2D.getBlendColor());
                    var14[3] = 1.0F;
                    GL11.glTexEnv(8960, 8705, LWJGLUtility.getFloatBuffer(var14));

                    if(!image2D.isLoaded()) {
                        image2D.setLoaded(true);
                        System.out.println("loaded texture: " + image2D + " " + id);
                        short var16 = GL_RGB;
                        switch (image2D.getFormat()) {
                            case 96:
                                var16 = GL_ALPHA;
                                break;
                            case 97:
                                var16 = GL_LUMINANCE;
                                break;
                            case 98:
                                var16 = GL_LUMINANCE_ALPHA;
                                break;
                            case 99:
                                var16 = GL_RGB;
                                break;
                            case 100:
                                var16 = GL_RGBA;
                        }
                        GL11.glTexImage2D(GL_TEXTURE_2D, 0, var16, image2D.getWidth(), image2D.getHeight(), 0, var16, 5121, LWJGLUtility.getByteBuffer(image2D.getImageData()));
                    }

                    GL11.glTexParameterf(GL_TEXTURE_2D, 10242, texture2D.getWrappingS() == 240 ? 33071.0F : 10497.0F);
                    GL11.glTexParameterf(GL_TEXTURE_2D, 10243, texture2D.getWrappingT() == 240 ? 33071.0F : 10497.0F);

                    int var17 = texture2D.getLevelFilter();
                    int var18 = texture2D.getImageFilter();
                    short var19;
                    label128:
                    {
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
                    GL11.glTexParameteri(GL_TEXTURE_2D, 10241, var20);
                    GL11.glTexParameteri(GL_TEXTURE_2D, 10240, var19);
                    GL11.glEnableClientState('\u8078');
                    FloatBuffer var29;
                    if (texCoords.getComponentType() == 1) {
                        var29 = LWJGLUtility.getFloatBuffer(texCoords.getByteValues());
                    } else {
                        var29 = LWJGLUtility.getFloatBuffer(texCoords.getShortValues());
                    }

                    GL11.glTexCoordPointer(texCoords.getComponentCount(), 0, var29);
                    Transform var31 = new Transform();
                    texture2D.getCompositeTransform(var31);
                    var31.transpose();
                    GL11.glMatrixMode(5890);
                    GL11.glLoadMatrix(LWJGLUtility.getFloatBuffer(((Transform3D) var31.getImpl()).m_matrix));
                    GL11.glTranslatef(var26[1], var26[2], var26[3]);
                    GL11.glScalef(var26[0], var26[0], var26[0]);
                }
            }

            for (j = 0; j < stripCount; ++j) {
                var22 = triangleStripArray.getIndexStrip(j);
                GL11.glDrawElements(5, LWJGLUtility.getIntBuffer(var22));
            }

            for (j = 0; j < i; ++j) {
                Texture2D tex = appearance.getTexture(j);
                Image2D image2D;
                if (tex != null && ((image2D = tex.getImage()).getId()) != 0) {
                    if (useGL13()) {
                        GL13.glActiveTexture('\u84c0' + j);
                        GL13.glClientActiveTexture('\u84c0' + j);
                    }
                    GL11.glBindTexture(GL_TEXTURE_2D, 0);
                    if(exiting) {
                        releaseTexture(image2D);
                    }
                }
            }
        } else {
            for (i = 0; i < stripCount; ++i) {
                var22 = triangleStripArray.getIndexStrip(i);
                GL11.glDrawElements(5, LWJGLUtility.getIntBuffer(var22));
            }

        }
        if(exiting) {
            releaseTarget();
        }
    }

    public final void render(World var1) {
        Transform var2 = new Transform();
        this.clearBackgound(var1.getBackground());
        var1.getActiveCamera().getTransformTo(var1, var2);
        CameraCache.setCamera(var1.getActiveCamera(), var2);
        LightsCache.getLightsInWorld(var1);
        RenderPipe.getInstance().pushRenderNode(var1, (Transform) null);
        this.method519();
    }

    public final void render(Node var1, Transform var2) {
        RenderPipe.getInstance().pushRenderNode(var1, var2);
        this.method519();
    }

    private void method519() {
        for (int var1 = 0; var1 < RenderPipe.getInstance().getSize(); ++var1) {
            RenderObject var2;
            if ((var2 = RenderPipe.getInstance().getRenderObj(var1)).m_node instanceof Mesh) {
                Mesh var3;
                IndexBuffer var4 = (var3 = (Mesh) var2.m_node).getIndexBuffer(var2.m_index);
                Appearance var5 = var3.getAppearance(var2.m_index);
                if (var4 != null && var5 != null) {
                    VertexBuffer var6 = MeshMorph.getInstance().getMorphedVertexBuffer(var3);
                    this.renderVertex(var6, var4, var5, var2.m_transform, var3.getScope(), var2.m_alphaFactor);
                }
            } else {
                this.method509((Sprite3D) var2.m_node, var2.m_transform);
            }
        }

        RenderPipe.getInstance().clear();
        MeshMorph.getInstance().clearCache();
    }

    private void method509(Sprite3D var1, Transform var2) {
        Vector4f var3 = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        Vector4f var4 = new Vector4f(1.0F, 0.0F, 0.0F, 1.0F);
        Vector4f var5 = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
        Transform var6;
        (var6 = new Transform(CameraCache.m_model2camTransform)).postMultiply(var2);
        Transform3D impl = (Transform3D) var6.getImpl();
        impl.transform(var3);
        impl.transform(var4);
        impl.transform(var5);
        Vector4f var7 = new Vector4f(var3);
        var3.mul(1.0F / var3.w);
        var4.mul(1.0F / var4.w);
        var5.mul(1.0F / var5.w);
        var4.sub(var3);
        var5.sub(var3);
        Vector4f var8 = new Vector4f(var4.length(), 0.0F, 0.0F, 0.0F);
        Vector4f var9 = new Vector4f(0.0F, var5.length(), 0.0F, 0.0F);
        var8.add(var7);
        var9.add(var7);
        Transform var10 = new Transform();
        CameraCache.camera.getProjection(var10);
        impl = (Transform3D) var10.getImpl();
        impl.transform(var7);
        impl.transform(var8);
        impl.transform(var9);
        if (var7.w > 0.0F && -var7.w < var7.z && var7.z <= var7.w) {
            var7.mul(1.0F / var7.w);
            var8.mul(1.0F / var8.w);
            var9.mul(1.0F / var9.w);
            var8.sub(var7);
            var9.sub(var7);
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
                var15 = var8.length() * (float) this.viewportWidth * 0.5F;
                var16 = var9.length() * (float) this.viewportHeight * 0.5F;
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
            if (G3DUtils.intersectRectangle(var12[0], var12[1], var12[2], var12[3], 0, 0, var1.getImage().getWidth(), var1.getImage().getHeight(), var21)) {
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
                    GL11.glLoadMatrix(LWJGLUtility.getFloatBuffer(((Transform3D) var10.getImpl()).m_matrix));
                    GL11.glMatrixMode(5888);
                    GL11.glLoadMatrix(LWJGLUtility.getFloatBuffer(((Transform3D) var6.getImpl()).m_matrix));
                    GL11.glDisable(2896);
                    var27 = LWJGLUtility.getByteBuffer(var1.getImage().getImageData());
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

                this.appearance(var1.getAppearance(), 1.0F, true);
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

    public void finalizeTexture(Image2D image2D) {
        // TODO
        texturesToRelease.add(image2D);
    }

    public void invalidateTexture(Image2D image2D) {
        System.out.println("invalidateTexture: " + image2D);
        image2D.setLoaded(false);
    }

    public void finalize() {
        // TODO
        if(pbufferContext == null || exiting) return;
        exiting = true;
        try {
            // try to make context current
            pbufferContext.makeCurrent();

            while (texturesToRelease.size() > 0) {
                releaseTexture((Image2D) texturesToRelease.remove(0));
            }
            pbufferContext.destroy();
        } catch (Exception ignored) {}
    }

    private void releaseTexture(Image2D image2D) {
        int id = image2D.getId();
        if(id == 0) return;
        System.out.println("releaseTexture: " + image2D + " " + id);
        GL11.glDeleteTextures(id);
        image2D.setId(0);
    }
}