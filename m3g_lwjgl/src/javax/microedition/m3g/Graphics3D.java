package javax.microedition.m3g;

import emulator.Emulator;
import emulator.graphics3D.IGraphics3D;
import emulator.graphics3D.lwjgl.Emulator3D;
import emulator.graphics3D.m3g.LightsCache;
import emulator.graphics3D.m3g.CameraCache;

import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Graphics3D {
    public static final int ANTIALIAS = 2;
    public static final int DITHER = 4;
    public static final int OVERWRITE = 16;
    public static final int TRUE_COLOR = 8;
    private static Object target = null;
    private static IGraphics3D impl;
    private static Graphics3D inst = new Graphics3D();
    private int viewportX;
    private int viewportY;
    private int viewportWidth;
    private int viewportHeight;
    private float depthRangeNear;
    private float depthRangeFar;

    private Graphics3D() {
        impl = Emulator.getGraphics3D();
        this.depthRangeNear = 0.0F;
        this.depthRangeFar = 1.0F;
    }

    public static final Graphics3D getInstance() {
        return inst;
    }

    public static final IGraphics3D getImpl() {
        return impl;
    }

    public static final Hashtable getProperties() {
        return impl.getProperties();
    }

    public void bindTarget(Object var1) {
        this.bindTarget(var1, true, 0);
    }

    public void bindTarget(Object target, boolean depthBuffer, int hints) {
        impl.enableDepthBuffer(depthBuffer);

        if (Graphics3D.target != null) {
            throw new IllegalStateException();
        } else if (target == null) {
            throw new NullPointerException();
        } else if (hints != 0 && (hints & (ANTIALIAS | DITHER | OVERWRITE | TRUE_COLOR)) == 0) {
            throw new IllegalArgumentException();
        }

        boolean overwrite = (hints & OVERWRITE) != 0;

        Background bck = new Background();
        bck.setColorClearEnable(!overwrite);
        bck.setDepthClearEnable(depthBuffer);

        if (target instanceof Graphics) {
            Graphics g = (Graphics) target;

            int targetW = g.getImage().getWidth();
            int targetH = g.getImage().getHeight();
            if (targetW > Emulator3D.MaxViewportWidth || targetH > Emulator3D.MaxViewportHeight) {
                throw new IllegalArgumentException();
            }

            Graphics3D.target = target;
            impl.bindTarget(g);
            setViewport(g.getClipX(), g.getClipY(), g.getClipWidth(), g.getClipHeight());

            if (!overwrite) {
                Image2D bckImage = new Image2D(Image2D.RGB, new Image(g.getImage()));
                bck.setImage(bckImage);
                bck.setCrop(this.viewportX, this.viewportY, this.viewportWidth, this.viewportHeight);
            }
        } else if (target instanceof Image2D) {
            Image2D img2d = (Image2D) target;

            int targetW = img2d.getWidth();
            int targetH = img2d.getHeight();
            if (targetW > Emulator3D.MaxViewportWidth || targetH > Emulator3D.MaxViewportHeight) {
                throw new IllegalArgumentException();
            }

            int imageFormat = img2d.getFormat();
            if (!img2d.isMutable() || imageFormat != Image2D.RGB && imageFormat != Image2D.RGBA) {
                throw new IllegalArgumentException();
            }

            Graphics3D.target = target;
            impl.bindTarget(img2d);
            setViewport(0, 0, targetW, targetH);

            if (!overwrite) {
                bck.setImage(img2d);
                bck.setCrop(viewportX, viewportY, viewportWidth, viewportHeight);
            }
        } else {
            throw new IllegalArgumentException();
        }

        if (Graphics3D.target != null) {
            impl.setHints(hints);
            setDepthRange(this.depthRangeNear, this.depthRangeFar);
            clear(bck);
        }
    }

    public void releaseTarget() {
        if (target != null) {
            impl.releaseTarget();
            target = null;
        }

    }

    public Object getTarget() {
        return target;
    }

    public int getHints() {
        return impl.getHints();
    }

    public boolean isDepthBufferEnabled() {
        return impl.isDepthBufferEnabled();
    }

    public void setViewport(int var1, int var2, int var3, int var4) {
        if (var3 > 0 && var4 > 0 && var3 <= Emulator3D.MaxViewportWidth && var4 <= Emulator3D.MaxViewportHeight) {
            this.viewportX = var1;
            this.viewportY = var2;
            this.viewportWidth = var3;
            this.viewportHeight = var4;
            impl.setViewport(var1, var2, var3, var4);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int getViewportX() {
        return this.viewportX;
    }

    public int getViewportY() {
        return this.viewportY;
    }

    public int getViewportWidth() {
        return this.viewportWidth;
    }

    public int getViewportHeight() {
        return this.viewportHeight;
    }

    public void setDepthRange(float var1, float var2) {
        if (var1 >= 0.0F && var1 <= 1.0F && var2 >= 0.0F && var2 <= 1.0F) {
            this.depthRangeNear = var1;
            this.depthRangeFar = var2;
            impl.setDepthRange(var1, var2);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public float getDepthRangeNear() {
        return this.depthRangeNear;
    }

    public float getDepthRangeFar() {
        return this.depthRangeFar;
    }

    public void clear(Background var1) {
        if (target == null) {
            throw new IllegalStateException();
        } else {
            impl.clearBackgound(var1);
        }
    }

    public void render(World var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else if (target != null && var1.getActiveCamera() != null && var1.getActiveCamera().isDescendantOf(var1)) {
            impl.render(var1);
        } else {
            throw new IllegalStateException();
        }
    }

    public void render(Node var1, Transform var2) {
        if (var1 == null) {
            throw new NullPointerException();
        } else if (!(var1 instanceof Sprite3D) && !(var1 instanceof Mesh) && !(var1 instanceof Group)) {
            throw new IllegalArgumentException();
        } else if (target != null && CameraCache.camera != null) {
            impl.render(var1, var2);
        } else {
            throw new IllegalStateException();
        }
    }

    public void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5) {
        if (var1 != null && var2 != null && var3 != null) {
            if (target != null && CameraCache.camera != null) {
                impl.render(var1, var2, var3, var4, var5);
            } else {
                throw new IllegalStateException();
            }
        } else {
            throw new NullPointerException();
        }
    }

    public void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4) {
        this.render(var1, var2, var3, var4, -1);
    }

    public void setCamera(Camera var1, Transform var2) {
        CameraCache.setCamera(var1, var2);
    }

    public Camera getCamera(Transform var1) {
        return CameraCache.getCamera(var1);
    }

    public int addLight(Light var1, Transform var2) {
        return LightsCache.addLight(var1, var2);
    }

    public void setLight(int var1, Light var2, Transform var3) {
        LightsCache.setLight(var1, var2, var3);
    }

    public void resetLights() {
        LightsCache.resetLights();
    }

    public int getLightCount() {
        return LightsCache.getLightCount();
    }

    public Light getLight(int var1, Transform var2) {
        return LightsCache.getLight(var1, var2);
    }
}
