package emulator.graphics3D.view;

import emulator.Emulator;
import emulator.Settings;
import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.Transform3D;
import emulator.graphics3D.Vector4f;
import emulator.graphics3D.lwjgl.Emulator3D;
import emulator.graphics3D.m3g.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Vector;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.CompositingMode;
import javax.microedition.m3g.Fog;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.IndexBuffer;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.Sprite3D;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;
import javax.microedition.m3g.World;

import org.eclipse.swt.internal.opengl.win32.PIXELFORMATDESCRIPTOR;
import org.eclipse.swt.internal.opengl.win32.WGL;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.opengl.GLCanvas;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.GLExtensions;

import static org.lwjgl.opengl.GL11.*;

public final class b {
    private static final boolean useSoftwareWgl = false;
    private static b instance;
    private boolean xray;
    private int viewportWidth;
    private int viewportHeight;
    private float aFloat586;
    private float aFloat592;
    private static int anInt594;
    private static int anInt595;
    private static PIXELFORMATDESCRIPTOR aPIXELFORMATDESCRIPTOR587;
    private static Camera camera;
    private static Transform cameraTransform = new Transform();
    private static Vector lights = new Vector();
    private static Vector lightsTransforms = new Vector();
    private static GLCanvas glCanvas;

    private b() {
        instance = this;
        this.aFloat586 = 0.0F;
        this.aFloat592 = 1.0F;
    }

    public static b getViewInstance() {
        if (instance == null) {
            instance = new b();
        }

        return instance;
    }

    public final void setXray(boolean var1) {
        this.xray = var1;
    }

    public final void setViewport(int var1, int var2) {
        this.viewportWidth = var1;
        this.viewportHeight = var2;
    }

    private void setupViewport() {
        GL11.glViewport(0, 0, this.viewportWidth, this.viewportHeight);
        GL11.glScissor(0, 0, this.viewportWidth, this.viewportHeight);
    }

    private void setupDepth() {
        GL11.glDepthRange((double) this.aFloat586, (double) this.aFloat592);
    }

    public final void method367(Background var1) {
        this.setupViewport();
        this.setupDepth();
        GL11.glClearDepth(1.0D);
        GL11.glDepthMask(true);
        GL11.glColorMask(true, true, true, true);
        int var10000 = var1 != null && !this.xray ? var1.getColor() : 0;
        GL11.glClearColor(G3DUtils.getFloatColor(var10000, 16), G3DUtils.getFloatColor(var10000, 8), G3DUtils.getFloatColor(var10000, 0), G3DUtils.getFloatColor(var10000, 24));
        GL11.glClear(16640);
        if (var1 != null && !this.xray) {
            this.method385(var1);
        }

    }

    private void method385(Background var1) {
        if (var1 != null && var1.getImage() != null && var1.getCropWidth() > 0 && var1.getCropHeight() > 0) {
            GL11.glDisable(2896);
            GL11.glDisable(2912);
            int var2 = var1.getImage().getFormat() == 99 ? 6407 : 6408;
            int var3 = var1.getImage().getWidth();
            int var4 = var1.getImage().getHeight();
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
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
            ByteBuffer var15 = a.method394(var1.getImage().getImageData());

            for (int var16 = 0; var16 < var14; ++var16) {
                for (int var17 = 0; var17 < var13; ++var17) {
                    GL11.glRasterPos4f(0.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glBitmap(0, 0, 0.0F, 0.0F, var11 + (float) var17 * var9, var12 - (float) var16 * var10, var15);
                    GL11.glDrawPixels(var3, var4, var2, 5121, var15);
                }
            }

            GL11.glPixelStorei(3314, 0);
        }

    }

    public final void method368(Node var1, Transform var2) {
        if (var1 == null) {
            throw new NullPointerException();
        } else if (!(var1 instanceof Sprite3D) && !(var1 instanceof Mesh) && !(var1 instanceof Group)) {
            throw new IllegalArgumentException();
        } else {
            RenderPipe.getViewInstance().pushRenderNode(var1, var2 == null ? new Transform() : var2);
            this.method392();
        }
    }

    private void method392() {
        for (int var1 = 0; var1 < RenderPipe.getViewInstance().getSize(); ++var1) {
            RenderObject var2;
            if ((var2 = RenderPipe.getViewInstance().getRenderObj(var1)).node instanceof Mesh) {
                Mesh var3;
                IndexBuffer var4 = (var3 = (Mesh) var2.node).getIndexBuffer(var2.submeshIndex);
                Appearance var5 = var3.getAppearance(var2.submeshIndex);
                if (var4 != null && var5 != null) {
                    VertexBuffer var6 = MeshMorph.getViewInstance().getMorphedVertexBuffer(var3);
                    this.renderVertex(var6, var4, var5, var2.trans, var3.getScope(), var2.alphaFactor);
                }
            } else {
                this.renderSprite((Sprite3D) var2.node, var2.trans, var2.alphaFactor);
            }
        }

        RenderPipe.getViewInstance().clear();
        MeshMorph.getViewInstance().clearCache();
    }

    private void renderVertex(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int scope, float alphaFactor) {
        if ((camera.getScope() & scope) != 0) {
            this.setupViewport();
            this.setupDepth();
            setupCamera();
            setupLights(lights, lightsTransforms, scope);
            if (var4 != null) {
                Transform var7;
                (var7 = new Transform()).set(var4);
                var7.transpose();
                GL11.glMultMatrix(a.method401(((Transform3D) var7.getImpl()).m_matrix));
            }

            this.setupAppearance(var3, false);
            this.draw(var1, var2, var3, alphaFactor);
        }
    }

    private void renderSprite(Sprite3D var1, Transform var2, float alphaFactor) {
        Vector4f var3 = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        Vector4f var4 = new Vector4f(1.0F, 0.0F, 0.0F, 1.0F);
        Vector4f var5 = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
        Transform var6;
        (var6 = new Transform(cameraTransform)).postMultiply(var2);
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
        camera.getProjection(var10);
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
                    int var23 = (int) (0F - var19 / 2.0F);
                    int var24 = (int) (0F - var20 / 2.0F);
                    int var25 = (int) ((float) this.viewportWidth + var19);
                    int var26 = (int) ((float) this.viewportHeight + var20);
                    var10.transpose();
                    var6.transpose();
                    GL11.glViewport(var23, viewportHeight - var24 - var26, var25, var26);
                    GL11.glMatrixMode(5889);
                    GL11.glLoadMatrix(a.getFloatBuffer(((Transform3D) var10.getImpl()).m_matrix));
                    GL11.glMatrixMode(5888);
                    GL11.glLoadMatrix(a.getFloatBuffer(((Transform3D) var6.getImpl()).m_matrix));
                    GL11.glDisable(2896);
                    var27 = a.getImageBuffer(var1.getImage().getImageData());
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

                this.setupAppearance(var1.getAppearance(), true);
                GL11.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (255 * alphaFactor));
                GL11.glDisableClientState(GL_COLOR_ARRAY);

                GL11.glDrawPixels(var21[2], var21[3], var28, 5121, var27);
                GL11.glPixelStorei(3314, 0);
                GL11.glPixelStorei(3315, 0);
                GL11.glPixelStorei(3316, 0);
            }
        }
    }

    private static PIXELFORMATDESCRIPTOR method378() {
        if (aPIXELFORMATDESCRIPTOR587 == null) {
            aPIXELFORMATDESCRIPTOR587 = new PIXELFORMATDESCRIPTOR();
            aPIXELFORMATDESCRIPTOR587.nSize = 40;
            aPIXELFORMATDESCRIPTOR587.nVersion = 1;
            aPIXELFORMATDESCRIPTOR587.dwFlags = 37;
            aPIXELFORMATDESCRIPTOR587.iPixelType = 0;
            aPIXELFORMATDESCRIPTOR587.cColorBits = (byte) Emulator.getEmulator().getScreenDepth();
            aPIXELFORMATDESCRIPTOR587.iLayerType = 0;
            aPIXELFORMATDESCRIPTOR587.cDepthBits = 24;
        }

        return aPIXELFORMATDESCRIPTOR587;
    }

    public final boolean useContext(GLCanvas canvas) {
        glCanvas = canvas;
        if(useSoftwareWgl) {
            anInt594 = canvas.handle;
            int var2;
            int var3;
            if ((var3 = WGL.ChoosePixelFormat(var2 = OS.GetDC(anInt594), method378())) != 0 && WGL.SetPixelFormat(var2, var3, aPIXELFORMATDESCRIPTOR587)) {
                anInt595 = WGL.wglCreateContext(var2);
                if (anInt595 == 0) {
                    OS.ReleaseDC(anInt594, var2);
                    Emulator.getEmulator().getLogStream().println("wglCreateContext() error!!");
                    return false;
                } else {
                    OS.ReleaseDC(anInt594, var2);
                }
            } else {
                OS.ReleaseDC(anInt594, var2);
                Emulator.getEmulator().getLogStream().println("SetPixelFormat() error!!");
                return false;
            }
        }
        setCurrent();

        try {
            GLContext.useContext(canvas);
        } catch (Exception e) {
            e.printStackTrace();
            Emulator.getEmulator().getLogStream().println("useContext() error!!");
            return false;
        }

        GL11.glEnable(GL_SCISSOR_TEST);
        GL11.glEnable(GL_NORMALIZE);
        GL11.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        GL11.glEnable(GL_POINT_SMOOTH);
        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glEnable(GL_POLYGON_SMOOTH);
        GL11.glEnable(GL_DITHER);

        return true;
    }

    public final void setCurrent() {
        if(useSoftwareWgl) {
            if (!isCurrent()) {
                int var1;
                WGL.wglMakeCurrent(var1 = OS.GetDC(anInt594), anInt595);
                OS.ReleaseDC(anInt594, var1);
            }
            return;
        }
        if(!glCanvas.isCurrent()) {
            glCanvas.setCurrent();
        }
    }

    public static boolean isCurrent() {
        if(useSoftwareWgl) {
            boolean var0 = false;
            int var1 = OS.GetDC(anInt594);
            var0 = WGL.wglGetCurrentContext() == var1;
            OS.ReleaseDC(anInt594, var1);
            return var0;
        }
        return glCanvas.isCurrent();
    }

    public static void swapBuffers() {
        if(useSoftwareWgl) {
            int var0;
            WGL.SwapBuffers(var0 = OS.GetDC(anInt594));
            OS.ReleaseDC(anInt594, var0);
            return;
        }
        glCanvas.swapBuffers();
    }

    public static void releaseContext() {
        try {
            GLContext.useContext((Object) null);
        } catch (Exception ignored) {}
//        if(glCanvas != null)
//            glCanvas.notifyListeners(12, new Event());
        if(useSoftwareWgl) {
            WGL.wglDeleteContext(anInt595);
        }
    }

    public static void setCamera(Camera var0, Transform var1) {
        if (var1 != null) {
            cameraTransform.set(var1);
//            ((Transform3D) aTransform589.getImpl()).method445();
            ((Transform3D) cameraTransform.getImpl()).invert();
        } else {
            cameraTransform.setIdentity();
        }

        camera = var0;
    }

    public static int method381(Light var0, Transform var1) {
        if (var0 == null) {
            throw new NullPointerException();
        } else {
            lights.add(var0);
            if (var1 == null) {
                lightsTransforms.add(new Transform());
            } else {
                lightsTransforms.add(new Transform(var1));
            }

            return lights.size();
        }
    }

    public static void method388() {
        lights.clear();
        lightsTransforms.clear();
    }

    public final void method374(World var1) {
        method388();
        this.method365(var1, var1);
    }

    private void method365(World var1, Group var2) {
        Transform var3 = new Transform();

        for (int var4 = 0; var4 < var2.getChildCount(); ++var4) {
            Node var5;
            if ((var5 = var2.getChild(var4)) instanceof Light && var5.getTransformTo(var1, var3)) {
                lights.add(var5);
                lightsTransforms.add(new Transform(var3));
            } else if (var5 instanceof Group) {
                this.method365(var1, (Group) var5);
            }
        }

    }

    private void setupAppearance(Appearance var1, boolean var3) {
        if (!var3) {
            this.setupPolygonMode(var1.getPolygonMode());
        }

        this.setupCompositingMode(var1.getCompositingMode());
        if (!var3) {
            setupMaterial(var1.getMaterial());
        }

        this.setupFog(var1.getFog());
    }

    private void setupPolygonMode(PolygonMode pm) {
        if (pm == null) {
            pm = new PolygonMode();
        }

        GL11.glPolygonMode(GL_FRONT_AND_BACK, Settings.xrayView ? GL_LINE : GL_FILL);

        int var1 = pm.getCulling();
        if (var1 == PolygonMode.CULL_NONE) {
            GL11.glDisable(GL_CULL_FACE);
        } else {
            GL11.glEnable(GL_CULL_FACE);
            GL11.glCullFace(var1 == PolygonMode.CULL_FRONT ? GL_FRONT : GL_BACK);
        }

        GL11.glShadeModel(pm.getShading() == PolygonMode.SHADE_FLAT ? GL_FLAT : GL_SMOOTH);
        GL11.glFrontFace(pm.getWinding() == PolygonMode.WINDING_CW ? GL_CW : GL_CCW);
        GL11.glLightModelf(GL_LIGHT_MODEL_TWO_SIDE, pm.isTwoSidedLightingEnabled() ? 1.0F : 0.0F);
        GL11.glLightModelf(GL_LIGHT_MODEL_LOCAL_VIEWER, pm.isLocalCameraLightingEnabled() ? 1.0F : 0.0F);
        GL11.glHint(GL_PERSPECTIVE_CORRECTION_HINT, pm.isPerspectiveCorrectionEnabled() ? GL_NICEST : GL_FASTEST);
    }

    private void setupCompositingMode(CompositingMode cm) {
        if (cm == null) {
            cm = new CompositingMode();
        }

        GL11.glEnable(GL_DEPTH_TEST);

        GL11.glDepthMask(cm.isDepthWriteEnabled());
        GL11.glDepthFunc(cm.isDepthTestEnabled() ? GL_LEQUAL : GL_ALWAYS);
        GL11.glColorMask(cm.isColorWriteEnabled(), cm.isColorWriteEnabled(), cm.isColorWriteEnabled(), cm.isAlphaWriteEnabled());

        GL11.glAlphaFunc(GL_GEQUAL, cm.getAlphaThreshold());
        if (cm.getAlphaThreshold() == 0.0F) {
            GL11.glDisable(GL_ALPHA_TEST);
        } else {
            GL11.glEnable(GL_ALPHA_TEST);
        }

        if (cm.getBlending() == CompositingMode.REPLACE) {
            GL11.glDisable(GL_BLEND);
        } else {
            GL11.glEnable(GL_BLEND);
        }

        switch (cm.getBlending()) {
            case CompositingMode.ALPHA:
                GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                break;
            case CompositingMode.ALPHA_ADD:
                GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
                break;
            case CompositingMode.MODULATE:
                GL11.glBlendFunc(GL_DST_COLOR, GL_ZERO);
                break;
            case CompositingMode.MODULATE_X2:
                GL11.glBlendFunc(GL_DST_COLOR, GL_SRC_COLOR);
                break;
            case CompositingMode.REPLACE:
                GL11.glBlendFunc(GL_ONE, GL_ZERO);
                break;
            default:
                break;
        }

        GL11.glPolygonOffset(cm.getDepthOffsetFactor(), cm.getDepthOffsetUnits());
        if (cm.getDepthOffsetFactor() == 0.0F && cm.getDepthOffsetUnits() == 0.0F) {
            GL11.glDisable(Settings.xrayView ? GL_POLYGON_OFFSET_LINE : GL_POLYGON_OFFSET_FILL);
        } else {
            GL11.glEnable(Settings.xrayView ? GL_POLYGON_OFFSET_LINE : GL_POLYGON_OFFSET_FILL);
        }
    }

    private static void setupMaterial(Material mat) {
        if (mat != null) {
            GL11.glEnable(GL_LIGHTING);
            float[] tmpCol = new float[4];

            G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.AMBIENT));
            GL11.glMaterial(GL_FRONT_AND_BACK, GL_AMBIENT, a.getFloatBuffer(tmpCol));

            G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.DIFFUSE));
            GL11.glMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE, a.getFloatBuffer(tmpCol));

            G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.EMISSIVE));
            GL11.glMaterial(GL_FRONT_AND_BACK, GL_EMISSION, a.getFloatBuffer(tmpCol));

            G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.SPECULAR));
            GL11.glMaterial(GL_FRONT_AND_BACK, GL_SPECULAR, a.getFloatBuffer(tmpCol));

            GL11.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, mat.getShininess());

            if (mat.isVertexColorTrackingEnabled()) {
                GL11.glEnable(GL_COLOR_MATERIAL);
                GL11.glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
            } else {
                GL11.glDisable(GL_COLOR_MATERIAL);
            }
        } else {
            GL11.glDisable(GL_LIGHTING);
        }
    }

    private void setupFog(Fog var1) {
        if (var1 != null && !this.xray) {
            GL11.glEnable(2912);
            GL11.glFogi(2917, var1.getMode() == 81 ? 9729 : 2048);
            float[] var2;
            G3DUtils.fillFloatColor(var2 = new float[4], var1.getColor());
            var2[3] = 1.0F;
            GL11.glFog(2918, a.method401(var2));
            GL11.glFogf(2915, var1.getNearDistance());
            GL11.glFogf(2916, var1.getFarDistance());
            GL11.glFogf(2914, var1.getDensity());
        } else {
            GL11.glDisable(2912);
        }
    }

    private void draw(VertexBuffer vertexBuffer, IndexBuffer indexBuffer, Appearance appearance, float alphaFactor) {
        VertexArray colors = vertexBuffer.getColors();
        if (colors == null) {
            int col = vertexBuffer.getDefaultColor();
            GL11.glColor4ub((byte) (col >> 16 & 255), (byte) (col >> 8 & 255), (byte) (col & 255), (byte) ((int) ((float) (col >> 24 & 255) * alphaFactor)));
            GL11.glDisableClientState(GL_COLOR_ARRAY);
        } else {
            GL11.glEnableClientState(GL_COLOR_ARRAY);
            if (colors.getComponentType() == 1) {
                byte[] colorsBArr = colors.getByteValues();
                GL11.glColorPointer(alphaFactor == 1.0F ? colors.getComponentCount() : 4, 5121, 0, a.getColorBuffer(colorsBArr, alphaFactor, colors.getVertexCount()));
            }
        }

        VertexArray normals = vertexBuffer.getNormals();
        if (normals != null && appearance.getMaterial() != null) {
            GL11.glEnableClientState(GL_NORMAL_ARRAY);
            glEnable(GL_NORMALIZE);
            if (normals.getComponentType() == 1) {
                GL11.glNormalPointer(0, a.getNormalBuffer(normals.getByteValues()));
            } else {
                GLExtensions.glNormalPointer(0, a.getNormalBuffer(normals.getShortValues()));
            }
        } else {
            GL11.glDisableClientState(GL_NORMAL_ARRAY);
        }

        float[] scaleBias = new float[4];
        VertexArray positions = vertexBuffer.getPositions(scaleBias);
        GL11.glEnableClientState(GL_VERTEX_ARRAY);
        if (positions.getComponentType() == 1) {
            byte[] posesBArr = positions.getByteValues();
            GL11.glVertexPointer(positions.getComponentCount(), 0, a.getVertexBuffer(posesBArr));
        } else {
            short[] posesSArr = positions.getShortValues();
            GL11.glVertexPointer(positions.getComponentCount(), 0, a.getVertexBuffer(posesSArr));
        }

        GL11.glMatrixMode(GL_MODELVIEW);
        GL11.glTranslatef(scaleBias[1], scaleBias[2], scaleBias[3]);
        GL11.glScalef(scaleBias[0], scaleBias[0], scaleBias[0]);

        TriangleStripArray triangleStripArray = (TriangleStripArray) indexBuffer;
        int stripCount = triangleStripArray.getStripCount();

        int var9;
        int[] var22;
        if (appearance != null && !this.xray) {
            var9 = Emulator3D.NumTextureUnits;
            IntBuffer var10;
            GL11.glGenTextures(var10 = BufferUtils.createIntBuffer(Emulator3D.NumTextureUnits));

            for (int i = 0; i < var9; ++i) {
                Texture2D var12 = appearance.getTexture(i);
                scaleBias[3] = 0.0F;
                VertexArray texCoords = vertexBuffer.getTexCoords(i, scaleBias);
                if (var12 != null && texCoords != null) {
                    short var29;
                    label135:
                    {
                        GL13.glActiveTexture('\u84c0' + i);
                        GL13.glClientActiveTexture('\u84c0' + i);
                        GL11.glEnable(3553);
                        GL11.glBindTexture(3553, var10.get(i));
                        short var31;
                        short var32;
                        switch (var12.getBlending()) {
                            case 224:
                                var29 = 8960;
                                var31 = 8704;
                                var32 = 260;
                                break;
                            case 225:
                                var29 = 8960;
                                var31 = 8704;
                                var32 = 3042;
                                break;
                            case 226:
                                var29 = 8960;
                                var31 = 8704;
                                var32 = 8449;
                                break;
                            case 227:
                                var29 = 8960;
                                var31 = 8704;
                                var32 = 8448;
                                break;
                            case 228:
                                var29 = 8960;
                                var31 = 8704;
                                var32 = 7681;
                                break;
                            default:
                                break label135;
                        }

                        GL11.glTexEnvi(var29, var31, var32);
                    }

                    Image2D var15;
                    short var16;
                    label129:
                    {
                        float[] var14;
                        G3DUtils.fillFloatColor(var14 = new float[4], var12.getBlendColor());
                        var14[3] = 1.0F;
                        GL11.glTexEnv(8960, 8705, a.method401(var14));
                        var15 = var12.getImage();
                        var16 = 6407;
                        switch (var15.getFormat()) {
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
                                break label129;
                        }

                        var16 = var29;
                    }

                    short var19;
                    label122:
                    {
                        GL11.glTexImage2D(3553, 0, var16, var15.getWidth(), var15.getHeight(), 0, var16, 5121, a.method394(var15.getImageData()));
                        GL11.glTexParameterf(3553, 10242, var12.getWrappingS() == 240 ? 33071.0F : 10497.0F);
                        GL11.glTexParameterf(3553, 10243, var12.getWrappingT() == 240 ? 33071.0F : 10497.0F);
                        int var17 = var12.getLevelFilter();
                        int var18 = var12.getImageFilter();
                        if (var17 == 208) {
                            if (var18 == 210) {
                                var19 = 9728;
                                var29 = 9728;
                                break label122;
                            }

                            var29 = 9729;
                        } else if (var17 == 209) {
                            if (var18 == 210) {
                                var19 = 9985;
                                var29 = 9728;
                                break label122;
                            }

                            var29 = 9987;
                        } else {
                            if (var18 == 210) {
                                var19 = 9984;
                                var29 = 9728;
                                break label122;
                            }

                            var29 = 9986;
                        }

                        var19 = var29;
                        var29 = 9729;
                    }

                    short var20 = var29;
                    GL11.glTexParameteri(3553, 10241, var20);
                    GL11.glTexParameteri(3553, 10240, var19);
                    GL11.glEnableClientState('\u8078');

                    IntBuffer texCoordBuffer;
                    if (texCoords.getComponentType() == 1) {
                        texCoordBuffer = a.getTexCoordBuffer(texCoords.getByteValues(), i);
                    } else {
                        texCoordBuffer = a.getTexCoordBuffer(texCoords.getShortValues(), i);
                    }
                    GL11.glTexCoordPointer(texCoords.getComponentCount(), 0, texCoordBuffer);

                    Transform var34 = new Transform();
                    var12.getCompositeTransform(var34);
                    var34.transpose();
                    GL11.glMatrixMode(5890);
                    GL11.glLoadMatrix(a.method401(((Transform3D) var34.getImpl()).m_matrix));
                    GL11.glTranslatef(scaleBias[1], scaleBias[2], scaleBias[3]);
                    GL11.glScalef(scaleBias[0], scaleBias[0], scaleBias[0]);
                }
            }

            for (int i = 0; i < stripCount; ++i) {
                var22 = triangleStripArray.getIndexStrip(i);
                GL11.glDrawElements(5, a.getElementsBuffer(var22));
            }

            for (int i = 0; i < var9; ++i) {
                if (GL11.glIsTexture(var10.get(i))) {
                    GL13.glActiveTexture('\u84c0' + i);
                    GL13.glClientActiveTexture('\u84c0' + i);
                    GL11.glDisableClientState('\u8078');
                    GL11.glDisable(3553);
                }
            }

            GL11.glDeleteTextures(var10);
        } else {
            for (var9 = 0; var9 < stripCount; ++var9) {
                var22 = triangleStripArray.getIndexStrip(var9);
                GL11.glDrawElements(5, a.getElementsBuffer(var22));
            }

        }
    }

    private static void setupCamera() {
        if (camera != null) {
            Transform var0 = new Transform();
            camera.getProjection(var0);
            var0.transpose();
            GL11.glMatrixMode(5889);
            GL11.glLoadMatrix(a.method401(((Transform3D) var0.getImpl()).m_matrix));

            var0.set(cameraTransform);
            var0.transpose();
            GL11.glMatrixMode(5888);
            GL11.glLoadMatrix(a.method401(((Transform3D) var0.getImpl()).m_matrix));
        }
    }

    private static void setupLights(Vector var0, Vector var1, int var2) {
        Transform var3 = new Transform();
        int var4;
        if ((var4 = var0.size()) > 8) {
            var4 = 8;
        }

        int var5;
        for (var5 = 0; var5 < 8; ++var5) {
            GL11.glDisable(16384 + var5);
        }

        for (var5 = 0; var5 < var4; ++var5) {
            Light var6;
            if (((var6 = (Light) var0.get(var5)).getScope() & var2) != 0 && RenderPipe.getInstance().isVisible(var6)) {
                Transform var7;
                if ((var7 = (Transform) var1.get(var5)) != null) {
                    var3.set(var7);
                } else {
                    var3.setIdentity();
                }

                var3.transpose();
                GL11.glPushMatrix();
                GL11.glMatrixMode(5888);
                GL11.glMultMatrix(a.method401(((Transform3D) var3.getImpl()).m_matrix));
                float[] var8;
                (var8 = new float[4])[3] = 1.0F;
                GL11.glLight(16384 + var5, 4608, a.method401(var8));
                GL11.glLight(16384 + var5, 4609, a.method401(var8));
                GL11.glLight(16384 + var5, 4610, a.method401(var8));
                GL11.glLightf(16384 + var5, 4615, 1.0F);
                GL11.glLightf(16384 + var5, 4616, 0.0F);
                GL11.glLightf(16384 + var5, 4617, 0.0F);
                GL11.glLightf(16384 + var5, 4614, 180.0F);
                GL11.glLightf(16384 + var5, 4613, 0.0F);
                int var10000;
                short var10001;
                float[] var10002;
                if (var6.getMode() != 129) {
                    var10000 = 16384 + var5;
                    var10001 = 4611;
                    var10002 = LightsCache.LOCAL_ORIGIN;
                } else {
                    var10000 = 16384 + var5;
                    var10001 = 4611;
                    var10002 = LightsCache.POSITIVE_Z_AXIS;
                }

                label45:
                {
                    GL11.glLight(var10000, var10001, a.method401(var10002));
                    GL11.glLight(16384 + var5, 4612, a.method401(LightsCache.NEGATIVE_Z_AXIS));
                    float var9 = var6.getIntensity();
                    G3DUtils.fillFloatColor(var8, var6.getColor());
                    var8[3] = 1.0F;
                    var8[0] *= var9;
                    var8[1] *= var9;
                    var8[2] *= var9;
                    float var10 = var6.getConstantAttenuation();
                    float var11 = var6.getLinearAttenuation();
                    float var12 = var6.getQuadraticAttenuation();
                    float var15;
                    switch (var6.getMode()) {
                        case 128:
                            GL11.glLight(16384 + var5, 4608, a.method401(var8));
                            break label45;
                        case 129:
                            GL11.glLight(16384 + var5, 4609, a.method401(var8));
                            GL11.glLight(16384 + var5, 4610, a.method401(var8));
                            break label45;
                        case 130:
                            GL11.glLight(16384 + var5, 4609, a.method401(var8));
                            GL11.glLight(16384 + var5, 4610, a.method401(var8));
                            GL11.glLightf(16384 + var5, 4615, var10);
                            GL11.glLightf(16384 + var5, 4616, var11);
                            var10000 = 16384 + var5;
                            var10001 = 4617;
                            var15 = var12;
                            break;
                        case 131:
                            GL11.glLight(16384 + var5, 4609, a.method401(var8));
                            GL11.glLight(16384 + var5, 4610, a.method401(var8));
                            GL11.glLightf(16384 + var5, 4615, var10);
                            GL11.glLightf(16384 + var5, 4616, var11);
                            GL11.glLightf(16384 + var5, 4617, var12);
                            float var13 = var6.getSpotAngle();
                            float var14 = var6.getSpotExponent();
                            GL11.glLightf(16384 + var5, 4614, var13);
                            var10000 = 16384 + var5;
                            var10001 = 4613;
                            var15 = var14;
                            break;
                        default:
                            break label45;
                    }

                    GL11.glLightf(var10000, var10001, var15);
                }

                GL11.glEnable(16384 + var5);
                GL11.glPopMatrix();
            }
        }

    }

    public final void method372(float var1) {
        this.setupViewport();
        this.setupDepth();
        setupCamera();
        GL11.glPolygonMode(1032, 6914);
        GL11.glDisable(2884);
        GL11.glShadeModel(7425);
        GL11.glFrontFace(2305);
        GL11.glEnable(2929);
        GL11.glDepthFunc(519);
        GL11.glDisable(3008);
        GL11.glDisable(3042);
        GL11.glDisable('\u8037');
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        GL11.glColor4ub((byte) 70, (byte) 121, (byte) -80, (byte) -1);
        GL11.glDisableClientState('\u8076');
        GL11.glDisableClientState('\u8075');
        float var2 = var1 * 5.0F;
        boolean var3 = true;
        GL11.glMatrixMode(5888);
        GL11.glBegin(7);
        float var10000 = -var2;

        while (true) {
            float var4 = var10000;
            if (var10000 >= var2) {
                GL11.glEnd();
                return;
            }

            var10000 = -var2;

            while (true) {
                float var5 = var10000;
                if (var10000 >= var2) {
                    var3 = !var3;
                    var10000 = var4 + var1;
                    break;
                }

                if (var3) {
                    GL11.glVertex3f(var4, 0.0F, var5);
                    GL11.glVertex3f(var4 + var1, 0.0F, var5);
                    GL11.glVertex3f(var4 + var1, 0.0F, var5 + var1);
                    GL11.glVertex3f(var4, 0.0F, var5 + var1);
                }

                var3 = !var3;
                var10000 = var5 + var1;
            }
        }
    }

    public final void drawAxis() {
        this.setupViewport();
        this.setupDepth();
        setupCamera();
        GL11.glPolygonMode(1032, 6914);
        GL11.glDisable(2884);
        GL11.glShadeModel(7425);
        GL11.glFrontFace(2305);
        GL11.glEnable(2929);
        GL11.glDepthFunc(519);
        GL11.glDisable(3008);
        GL11.glDisable(3042);
        GL11.glDisable('\u8037');
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        GL11.glDisableClientState('\u8076');
        GL11.glDisableClientState('\u8075');
        GL11.glMatrixMode(5888);
        GL11.glColor4ub((byte) -1, (byte) 0, (byte) 0, (byte) -1);
        GL11.glBegin(1);
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glVertex3f(1.0F, 0.0F, 0.0F);
        GL11.glEnd();
        GL11.glBegin(6);
        GL11.glVertex3f(1.3F, 0.0F, 0.0F);
        GL11.glVertex3f(1.0F, 0.1F, 0.1F);
        GL11.glVertex3f(1.0F, -0.1F, 0.1F);
        GL11.glVertex3f(1.0F, -0.1F, -0.1F);
        GL11.glVertex3f(1.0F, 0.1F, -0.1F);
        GL11.glVertex3f(1.0F, 0.1F, 0.1F);
        GL11.glEnd();
        GL11.glColor4ub((byte) 0, (byte) -1, (byte) 0, (byte) -1);
        GL11.glBegin(1);
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glVertex3f(0.0F, 1.0F, 0.0F);
        GL11.glEnd();
        GL11.glBegin(6);
        GL11.glVertex3f(0.0F, 1.3F, 0.0F);
        GL11.glVertex3f(0.1F, 1.0F, 0.1F);
        GL11.glVertex3f(-0.1F, 1.0F, 0.1F);
        GL11.glVertex3f(-0.1F, 1.0F, -0.1F);
        GL11.glVertex3f(0.1F, 1.0F, -0.1F);
        GL11.glVertex3f(0.1F, 1.0F, 0.1F);
        GL11.glEnd();
        GL11.glColor4ub((byte) 0, (byte) 0, (byte) -1, (byte) -1);
        GL11.glBegin(1);
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glVertex3f(0.0F, 0.0F, 1.0F);
        GL11.glEnd();
        GL11.glBegin(6);
        GL11.glVertex3f(0.0F, 0.0F, 1.3F);
        GL11.glVertex3f(0.1F, 0.1F, 1.0F);
        GL11.glVertex3f(-0.1F, 0.1F, 1.0F);
        GL11.glVertex3f(-0.1F, -0.1F, 1.0F);
        GL11.glVertex3f(0.1F, -0.1F, 1.0F);
        GL11.glVertex3f(0.1F, 0.1F, 1.0F);
        GL11.glEnd();
    }
}
