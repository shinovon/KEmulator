package emulator.graphics3D.view;

import emulator.Emulator;
import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.Transform3D;
import emulator.graphics3D.Vector4f;
import emulator.graphics3D.lwjgl.Emulator3D;
import emulator.graphics3D.m3g.LightsCache;
import emulator.graphics3D.m3g.RenderObject;
import emulator.graphics3D.m3g.RenderPipe;
import emulator.graphics3D.m3g.MeshMorph;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
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

import emulator.ui.swt.EmulatorImpl;
import org.eclipse.swt.internal.opengl.win32.PIXELFORMATDESCRIPTOR;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Event;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;

public final class b {
    private static b ab583;
    private boolean aBoolean584;
    private int anInt585;
    private int anInt591;
    private float aFloat586;
    private float aFloat592;
    private static int anInt594;
    private static int anInt595;
    private static PIXELFORMATDESCRIPTOR aPIXELFORMATDESCRIPTOR587;
    private static Camera aCamera588;
    private static Transform aTransform589 = new Transform();
    private static Vector aVector590 = new Vector();
    private static Vector aVector593 = new Vector();
    private static GLCanvas glCanvas;

    private b() {
        ab583 = this;
        this.aFloat586 = 0.0F;
        this.aFloat592 = 1.0F;
    }

    public static b method362() {
        if (ab583 == null) {
            ab583 = new b();
        }

        return ab583;
    }

    public final void method363(boolean var1) {
        this.aBoolean584 = var1;
    }

    public final void method364(int var1, int var2) {
        this.anInt585 = var1;
        this.anInt591 = var2;
    }

    private void method390() {
        GL11.glViewport(0, 0, this.anInt585, this.anInt591);
        GL11.glScissor(0, 0, this.anInt585, this.anInt591);
    }

    private void method391() {
        GL11.glDepthRange((double) this.aFloat586, (double) this.aFloat592);
    }

    public final void method367(Background var1) {
        this.method390();
        this.method391();
        GL11.glClearDepth(1.0D);
        GL11.glDepthMask(true);
        GL11.glColorMask(true, true, true, true);
        int var10000 = var1 != null && !this.aBoolean584 ? var1.getColor() : 0;
        int var2 = var10000;
        GL11.glClearColor(G3DUtils.getFloatColor(var10000, 16), G3DUtils.getFloatColor(var2, 8), G3DUtils.getFloatColor(var2, 0), G3DUtils.getFloatColor(var2, 24));
        GL11.glClear(16640);
        if (var1 != null && !this.aBoolean584) {
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
            float var5 = (float) this.anInt585;
            float var6 = (float) this.anInt591;
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
                    this.method373(var6, var4, var5, var2.trans, var3.getScope(), var2.alphaFactor);
                }
            } else {
                this.method375((Sprite3D) var2.node, var2.trans);
            }
        }

        RenderPipe.getViewInstance().clear();
        MeshMorph.getViewInstance().clearCache();
    }

    private void method373(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5, float var6) {
        if ((aCamera588.getScope() & var5) != 0) {
            this.method390();
            this.method391();
            method393();
            method370(aVector590, aVector593, var5);
            if (var4 != null) {
                Transform var7;
                (var7 = new Transform()).set(var4);
                var7.transpose();
                GL11.glMultMatrix(a.method401(((Transform3D) var7.getImpl()).m_matrix));
            }

            this.method383(var3, var6, false);
            this.method382(var1, var2, var3, var6);
        }
    }

    private void method375(Sprite3D var1, Transform var2) {
        Vector4f var3 = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        Vector4f var4 = new Vector4f(1.0F, 0.0F, 0.0F, 1.0F);
        Vector4f var5 = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
        Transform var6;
        (var6 = new Transform(aTransform589)).postMultiply(var2);
        ((Transform3D) var6.getImpl()).transform(var3);
        ((Transform3D) var6.getImpl()).transform(var4);
        ((Transform3D) var6.getImpl()).transform(var5);
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
        aCamera588.getProjection(var10);
        ((Transform3D) var10.getImpl()).transform(var7);
        ((Transform3D) var10.getImpl()).transform(var8);
        ((Transform3D) var10.getImpl()).transform(var9);
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
                var15 = var8.length() * (float) this.anInt585 * 0.5F;
                var16 = var9.length() * (float) this.anInt591 * 0.5F;
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
                    (var22 = new Transform()).postScale((float) this.anInt585 / ((float) this.anInt585 + var19), (float) this.anInt591 / ((float) this.anInt591 + var20), 1.0F);
                    var22.postMultiply(var10);
                    var10.set(var22);
                    int var23 = (int) (0.0F - var19 / 2.0F);
                    int var24 = (int) (0.0F - var20 / 2.0F);
                    int var25 = (int) ((float) this.anInt585 + var19);
                    int var26 = (int) ((float) this.anInt591 + var20);
                    var10.transpose();
                    var6.transpose();
                    GL11.glViewport(var23, -var24, var25, var26);
                    GL11.glMatrixMode(5889);
                    GL11.glLoadMatrix(a.method401(((Transform3D) var10.getImpl()).m_matrix));
                    GL11.glMatrixMode(5888);
                    GL11.glLoadMatrix(a.method401(((Transform3D) var6.getImpl()).m_matrix));
                    GL11.glDisable(2896);
                    var27 = a.method394(var1.getImage().getImageData());
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

                this.method383(var1.getAppearance(), 1.0F, true);
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
//        anInt594 = canvas.handle;
//        int var2;
//        int var3;
//        if ((var3 = WGL.ChoosePixelFormat(var2 = OS.GetDC(anInt594), method378())) != 0 && WGL.SetPixelFormat(var2, var3, aPIXELFORMATDESCRIPTOR587)) {
//            anInt595 = WGL.wglCreateContext(var2);
//            if (anInt595 == 0) {
//                OS.ReleaseDC(anInt594, var2);
//                Emulator.getEmulator().getLogStream().println("wglCreateContext() error!!");
//                return false;
//            } else {
//                OS.ReleaseDC(anInt594, var2);
        setCurrent();

        try {
            GLContext.useContext(canvas);
        } catch (Exception e) {
            e.printStackTrace();
            Emulator.getEmulator().getLogStream().println("useContext() error!!");
            return false;
        }

        GL11.glEnable(3089);
        GL11.glEnable(2977);
        GL11.glPixelStorei(3317, 1);
        GL11.glEnable(2832);
        GL11.glEnable(2848);
        GL11.glEnable(2881);
        GL11.glEnable(3024);
        return true;
//            }
//        } else {
//            OS.ReleaseDC(anInt594, var2);
//            Emulator.getEmulator().getLogStream().println("SetPixelFormat() error!!");
//            return false;
//        }
    }

    public final void setCurrent() {
//        if (!method384()) {
//            int var1;
//            WGL.wglMakeCurrent(var1 = OS.GetDC(anInt594), anInt595);
//            OS.ReleaseDC(anInt594, var1);
//        }
        if(!glCanvas.isCurrent()) {
            glCanvas.setCurrent();
        }
    }

    public static boolean isCurrent() {
        return glCanvas.isCurrent();
//        boolean var0 = false;
//        int var1 = OS.GetDC(anInt594);
//        var0 = WGL.wglGetCurrentContext() == var1;
//        OS.ReleaseDC(anInt594, var1);
//        return var0;
    }

    public static void swapBuffers() {
        glCanvas.swapBuffers();
//        int var0;
//        WGL.SwapBuffers(var0 = OS.GetDC(anInt594));
//        OS.ReleaseDC(anInt594, var0);
    }

    public static void releaseContext() {
        try {
            GLContext.useContext((Object) null);
        } catch (Exception var0) {
            ;
        }
//        if(glCanvas != null)
//            glCanvas.notifyListeners(12, new Event());
//        WGL.wglDeleteContext(anInt595);
    }

    public static void method371(Camera var0, Transform var1) {
        if (var1 != null) {
            aTransform589.set(var1);
//            ((Transform3D) aTransform589.getImpl()).method445();
            ((Transform3D) aTransform589.getImpl()).invert();
        } else {
            aTransform589.setIdentity();
        }

        aCamera588 = var0;
    }

    public static int method381(Light var0, Transform var1) {
        if (var0 == null) {
            throw new NullPointerException();
        } else {
            aVector590.add(var0);
            if (var1 == null) {
                aVector593.add(new Transform());
            } else {
                aVector593.add(new Transform(var1));
            }

            return aVector590.size();
        }
    }

    public static void method388() {
        aVector590.clear();
        aVector593.clear();
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
                aVector590.add(var5);
                aVector593.add(new Transform(var3));
            } else if (var5 instanceof Group) {
                this.method365(var1, (Group) var5);
            }
        }

    }

    private void method383(Appearance var1, float var2, boolean var3) {
        if (!var3) {
            this.method366(var1.getPolygonMode());
        }

        this.method369(var1.getCompositingMode());
        if (!var3) {
            method376(var1.getMaterial(), var2);
        }

        this.method377(var1.getFog());
    }

    private void method366(PolygonMode var1) {
        if (var1 == null) {
            var1 = new PolygonMode();
        }

        GL11.glPolygonMode(1032, this.aBoolean584 ? 6913 : 6914);
        int var2;
        if ((var2 = var1.getCulling()) == 162) {
            GL11.glDisable(2884);
        } else {
            GL11.glEnable(2884);
            GL11.glCullFace(var2 == 161 ? 1028 : 1029);
        }

        GL11.glShadeModel(var1.getShading() == 164 ? 7424 : 7425);
        GL11.glFrontFace(var1.getWinding() == 169 ? 2304 : 2305);
        GL11.glLightModelf(2898, var1.isTwoSidedLightingEnabled() ? 1.0F : 0.0F);
        GL11.glLightModelf(2897, var1.isLocalCameraLightingEnabled() ? 1.0F : 0.0F);
        GL11.glHint(3152, var1.isPerspectiveCorrectionEnabled() ? 4354 : 4353);
    }

    private void method369(CompositingMode var1) {
        if (var1 == null) {
            var1 = new CompositingMode();
        }

        GL11.glEnable(2929);
        GL11.glDepthMask(var1.isDepthWriteEnabled());
        GL11.glDepthFunc(var1.isDepthTestEnabled() ? 515 : 519);
        GL11.glColorMask(var1.isColorWriteEnabled(), var1.isColorWriteEnabled(), var1.isColorWriteEnabled(), var1.isAlphaWriteEnabled());
        GL11.glAlphaFunc(518, var1.getAlphaThreshold());
        if (var1.getAlphaThreshold() == 0.0F) {
            GL11.glDisable(3008);
        } else {
            GL11.glEnable(3008);
        }

        if (var1.getBlending() == 68) {
            GL11.glDisable(3042);
        } else {
            label48:
            {
                short var10000;
                short var10001;
                label47:
                {
                    GL11.glEnable(3042);
                    switch (var1.getBlending()) {
                        case 64:
                            var10000 = 770;
                            var10001 = 771;
                            break label47;
                        case 65:
                            var10000 = 770;
                            var10001 = 1;
                            break label47;
                        case 66:
                            var10000 = 774;
                            break;
                        case 67:
                            var10000 = 774;
                            var10001 = 768;
                            break label47;
                        case 68:
                            var10000 = 1;
                            break;
                        default:
                            break label48;
                    }

                    var10001 = 0;
                }

                GL11.glBlendFunc(var10000, var10001);
            }
        }

        GL11.glPolygonOffset(var1.getDepthOffsetFactor(), var1.getDepthOffsetUnits());
        if (var1.getDepthOffsetFactor() == 0.0F && var1.getDepthOffsetUnits() == 0.0F) {
            GL11.glDisable(this.aBoolean584 ? 10754 : '\u8037');
        } else {
            GL11.glEnable(this.aBoolean584 ? 10754 : '\u8037');
        }
    }

    private static void method376(Material var0, float var1) {
        short var10000;
        if (var0 != null) {
            GL11.glEnable(2896);
            float[] var2;
            G3DUtils.fillFloatColor(var2 = new float[4], var0.getColor(1024));
            GL11.glMaterial(1032, 4608, a.method401(var2));
            G3DUtils.fillFloatColor(var2, var0.getColor(2048));
            var2[3] *= var1;
            GL11.glMaterial(1032, 4609, a.method401(var2));
            G3DUtils.fillFloatColor(var2, var0.getColor(4096));
            GL11.glMaterial(1032, 5632, a.method401(var2));
            G3DUtils.fillFloatColor(var2, var0.getColor(8192));
            GL11.glMaterial(1032, 4610, a.method401(var2));
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

    private void method377(Fog var1) {
        if (var1 != null && !this.aBoolean584) {
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

    private void method382(VertexBuffer var1, IndexBuffer var2, Appearance var3, float var4) {
        VertexArray var5;
        int var10000;
        byte[] var23;
        short[] var24;
        if ((var5 = var1.getColors()) == null) {
            int var6;
            GL11.glColor4ub((byte) ((var6 = var1.getDefaultColor()) >> 16 & 255), (byte) (var6 >> 8 & 255), (byte) (var6 & 255), (byte) ((int) ((float) (var6 >> 24 & 255) * var4)));
            GL11.glDisableClientState('\u8076');
        } else {
            GL11.glEnableClientState('\u8076');
            boolean var10001;
            byte var10002;
            ByteBuffer var10003;
            if (var5.getComponentType() == 1) {
                var23 = new byte[var5.getComponentCount() * var5.getVertexCount()];
                var5.get(0, var5.getVertexCount(), var23);
                var10000 = var4 == 1.0F ? var5.getComponentCount() : 4;
                var10001 = true;
                var10002 = 0;
                var10003 = a.method396(var23, var4, var5.getVertexCount());
            } else {
                var24 = new short[var5.getComponentCount() * var5.getVertexCount()];
                var5.get(0, var5.getVertexCount(), var24);
                var10000 = var4 == 1.0F ? var5.getComponentCount() : 4;
                var10001 = true;
                var10002 = 0;
                var10003 = a.method397(var24, var4, var5.getVertexCount());
            }

            GL11.glColorPointer(var10000, var10001, var10002, var10003);
        }

        if ((var5 = var1.getNormals()) != null && var3.getMaterial() != null) {
            GL11.glEnableClientState('\u8075');
            if (var5.getComponentType() == 1) {
                var23 = new byte[var5.getComponentCount() * var5.getVertexCount()];
                var5.get(0, var5.getVertexCount(), var23);
                GL11.glNormalPointer(0, a.method394(var23));
            } else {
                var24 = new short[var5.getComponentCount() * var5.getVertexCount()];
                var5.get(0, var5.getVertexCount(), var24);
                GL11.glNormalPointer(0, a.method395(var24));
            }
        } else {
            GL11.glDisableClientState('\u8075');
        }

        float[] var26 = new float[4];
        var5 = var1.getPositions(var26);
        GL11.glEnableClientState('\u8074');
        byte var28;
        IntBuffer var30;
        if (var5.getComponentType() == 1) {
            byte[] var7 = new byte[var5.getComponentCount() * var5.getVertexCount()];
            var5.get(0, var5.getVertexCount(), var7);
            var10000 = var5.getComponentCount();
            var28 = 0;
            var30 = a.method400(var7);
        } else {
            short[] var25 = new short[var5.getComponentCount() * var5.getVertexCount()];
            var5.get(0, var5.getVertexCount(), var25);
            var10000 = var5.getComponentCount();
            var28 = 0;
            var30 = a.method399(var25);
        }

        GL11.glVertexPointer(var10000, var28, var30);
        GL11.glMatrixMode(5888);
        GL11.glTranslatef(var26[1], var26[2], var26[3]);
        GL11.glScalef(var26[0], var26[0], var26[0]);
        TriangleStripArray var27;
        int var8 = (var27 = (TriangleStripArray) var2).getStripCount();
        int var9;
        int[] var22;
        if (var3 != null && !this.aBoolean584) {
            var9 = Emulator3D.NumTextureUnits;
            IntBuffer var10;
            GL11.glGenTextures(var10 = BufferUtils.createIntBuffer(Emulator3D.NumTextureUnits));

            int var11;
            for (var11 = 0; var11 < var9; ++var11) {
                Texture2D var12 = var3.getTexture(var11);
                var26[3] = 0.0F;
                var5 = var1.getTexCoords(var11, var26);
                if (var12 != null && var5 != null) {
                    short var29;
                    label135:
                    {
                        GL13.glActiveTexture('\u84c0' + var11);
                        GL13.glClientActiveTexture('\u84c0' + var11);
                        GL11.glEnable(3553);
                        GL11.glBindTexture(3553, var10.get(var11));
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
                    FloatBuffer var35;
                    if (var5.getComponentType() == 1) {
                        byte[] var21 = new byte[var5.getComponentCount() * var5.getVertexCount()];
                        var5.get(0, var5.getVertexCount(), var21);
                        var10000 = var5.getComponentCount();
                        var28 = 0;
                        var35 = a.method403(var21);
                    } else {
                        short[] var33 = new short[var5.getComponentCount() * var5.getVertexCount()];
                        var5.get(0, var5.getVertexCount(), var33);
                        var10000 = var5.getComponentCount();
                        var28 = 0;
                        var35 = a.method402(var33);
                    }

                    GL11.glTexCoordPointer(var10000, var28, var35);
                    Transform var34 = new Transform();
                    var12.getCompositeTransform(var34);
                    var34.transpose();
                    GL11.glMatrixMode(5890);
                    GL11.glLoadMatrix(a.method401(((Transform3D) var34.getImpl()).m_matrix));
                    GL11.glTranslatef(var26[1], var26[2], var26[3]);
                    GL11.glScalef(var26[0], var26[0], var26[0]);
                }
            }

            for (var11 = 0; var11 < var8; ++var11) {
                var22 = var27.getIndexStrip(var11);
                GL11.glDrawElements(5, a.method398(var22));
            }

            for (var11 = 0; var11 < var9; ++var11) {
                if (GL11.glIsTexture(var10.get(var11))) {
                    GL13.glActiveTexture('\u84c0' + var11);
                    GL13.glClientActiveTexture('\u84c0' + var11);
                    GL11.glDisableClientState('\u8078');
                    GL11.glDisable(3553);
                }
            }

            GL11.glDeleteTextures(var10);
        } else {
            for (var9 = 0; var9 < var8; ++var9) {
                var22 = var27.getIndexStrip(var9);
                GL11.glDrawElements(5, a.method398(var22));
            }

        }
    }

    private static void method393() {
        if (aCamera588 != null) {
            Transform var0 = new Transform();
            aCamera588.getProjection(var0);
            var0.transpose();
            GL11.glMatrixMode(5889);
            GL11.glLoadMatrix(a.method401(((Transform3D) var0.getImpl()).m_matrix));
            var0.set(aTransform589);
            var0.transpose();
            GL11.glMatrixMode(5888);
            GL11.glLoadMatrix(a.method401(((Transform3D) var0.getImpl()).m_matrix));
        }
    }

    private static void method370(Vector var0, Vector var1, int var2) {
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
        this.method390();
        this.method391();
        method393();
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

    public final void method389() {
        this.method390();
        this.method391();
        method393();
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
