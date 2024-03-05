package javax.microedition.m3g;

import emulator.i;

import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;

public class Graphics3D {
    Object bbPixels = null;
    private Object boundTarget = null;
    private boolean isGraphics = true;
    private boolean preload = false;
    private boolean unload = false;
    private boolean overwrite = false;
    private int clipX = 0;
    private int clipY = 0;
    private int clipWidth = 0;
    private int clipHeight = 0;
    private int viewportX = 0;
    private int viewportY = 0;
    private int viewportWidth = 0;
    private int viewportHeight = 0;

    Graphics3D(int var1) {
        this.swerveHandle = var1;
    }

    public static final Graphics3D getInstance() {
        if (instance == null) {
            instance = (Graphics3D) Engine.instantiateJavaPeer(createImpl());
        }
        return instance;
    }

    public Object getTarget() {
        return this.boundTarget;
    }

    public synchronized void bindTarget(Object var1, boolean var2, int var3) {
        if (this.boundTarget != null) {
            throw new IllegalStateException();
        }
        if (var1 == null) {
            throw new NullPointerException();
        }
        if ((var3 & 0xFFFFFFE1) != 0) {
            throw new IllegalArgumentException();
        }
        if ((var1 instanceof Graphics)) {
            this.isGraphics = true;
            this.overwrite = ((var3 & 0x10) != 0);
            this.unload = false;
            bindTarget((Graphics) var1);
        } else {
            if (!(var1 instanceof Image2D)) {
                throw new IllegalArgumentException();
            }
            this.isGraphics = false;
            setBackBufferImage2D((Image2D) var1);
            this.boundTarget = var1;
        }
        setHints(var2, var3);
    }

    public synchronized void bindTarget(Object var1) {
        if (this.boundTarget != null) {
            throw new IllegalStateException();
        }
        if (var1 == null) {
            throw new NullPointerException();
        }
        if ((var1 instanceof Graphics)) {
            this.isGraphics = true;
            this.overwrite = false;
            this.unload = false;
            bindTarget((Graphics) var1);
        } else if ((var1 instanceof Image2D)) {
            this.isGraphics = false;
            setBackBufferImage2D((Image2D) var1);
            this.boundTarget = var1;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void bindTarget(Graphics var1) {
        this.clipX = (Helpers.getTranslateX(var1) + Helpers.getClipX(var1));
        this.clipY = (Helpers.getTranslateY(var1) + Helpers.getClipY(var1));
        this.clipWidth = Helpers.getClipWidth(var1);
        this.clipHeight = Helpers.getClipHeight(var1);
        if ((this.clipWidth <= maxViewportWidth) && (this.clipHeight <= maxViewportHeight)) {
            this.boundTarget = var1;
            Helpers.bindTarget(this, var1, this.clipX, this.clipY, this.clipWidth, this.clipHeight);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public synchronized void releaseTarget() {
        if (this.boundTarget != null) {
            this.preload = false;
            if (this.isGraphics) {
                Helpers.releaseTarget(this, (Graphics) this.boundTarget);
            } else {
                setBackBufferImage2D(null);
            }
            this.boundTarget = null;
        }
    }

    public synchronized void setViewport(int var1, int var2, int var3, int var4) {
        if ((var3 > 0) && (var3 <= maxViewportWidth) && (var4 > 0) && (var4 <= maxViewportHeight)) {
            this.viewportX = var1;
            this.viewportY = var2;
            this.viewportWidth = var3;
            this.viewportHeight = var4;
            if (this.boundTarget != null) {
                if ((this.boundTarget instanceof Graphics)) {
                    Graphics var5 = (Graphics) this.boundTarget;
                    var1 += Helpers.getTranslateX(var5);
                    var2 += Helpers.getTranslateY(var5);
                    this.preload = ((!this.unload)
                            && ((!this.overwrite) || (var1 > this.clipX) || (var1 + var3 < this.clipX + this.clipWidth)
                            || (var2 > this.clipY) || (var2 + var4 < this.clipY + this.clipHeight)));
                    setViewportImpl(var1, var2, var3, var4);
                    this.unload = ((this.unload) || (this.preload));
                    return;
                }
                setViewportImpl(var1, var2, var3, var4);
            }
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

    public synchronized void clear(Background var1) {
        if (this.boundTarget == null) {
            throw new IllegalStateException();
        }
        this.preload = ((!this.unload) && (!this.overwrite) && (var1 != null) && (!var1.isColorClearEnabled()));
        clearImpl(var1);
        this.unload = true;
    }

    public void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4) {
        render(var1, var2, var3, var4, -1);
    }

    public synchronized void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5) {
        if (this.boundTarget == null) {
            throw new IllegalStateException();
        }
        this.preload = ((!this.unload) && (!this.overwrite));
        renderPrimitive(var1, var2, var3, var4, var5);
        this.unload = true;
    }

    public synchronized void render(Node var1, Transform var2) {
        if (this.boundTarget == null) {
            throw new IllegalStateException();
        }
        this.preload = ((!this.unload) && (!this.overwrite));
        renderNode(var1, var2);
        this.unload = true;
    }

    public synchronized void render(World var1) {
        if (this.boundTarget == null) {
            throw new IllegalStateException();
        }
        Background var2 = var1.getBackground();
        this.preload = ((!this.unload) && (!this.overwrite) && (var2 != null) && (!var2.isColorClearEnabled()));
        renderWorld(var1);
        this.unload = true;
    }

    public Camera getCamera(Transform var1) {
        return (Camera) Engine.instantiateJavaPeer(getCameraImpl(var1));
    }

    public void setCamera(Camera var1, Transform var2) {
        setCameraImpl(var1, var2);
        Engine.addXOT(var1);
    }

    public void setLight(int var1, Light var2, Transform var3) {
        setLightImpl(var1, var2, var3);
        Engine.addXOT(var2);
    }

    public Light getLight(int var1, Transform var2) {
        return (Light) Engine.instantiateJavaPeer(getLightImpl(var1, var2));
    }

    public int addLight(Light var1, Transform var2) {
        int var3 = addLightImpl(var1, var2);
        Engine.addXOT(var1);
        return var3;
    }

    public static final synchronized Hashtable getProperties() {
        Hashtable var0;
        (var0 = new Hashtable()).put(new String("supportAntialiasing"), new Boolean(getCapability(0) == 1));
        var0.put(new String("supportTrueColor"), new Boolean(getCapability(1) == 1));
        var0.put(new String("supportDithering"), new Boolean(getCapability(2) == 1));
        var0.put(new String("supportMipmapping"), new Boolean(getCapability(3) == 1));
        var0.put(new String("supportPerspectiveCorrection"), new Boolean(getCapability(4) == 1));
        var0.put(new String("supportLocalCameraLighting"), new Boolean(getCapability(5) == 1));
        var0.put(new String("maxLights"), new Integer(getCapability(6)));
        var0.put(new String("maxViewportDimension"), new Integer(getCapability(7)));
        var0.put(new String("maxTextureDimension"), new Integer(getCapability(8)));
        var0.put(new String("maxSpriteCropDimension"), new Integer(getCapability(9)));
        var0.put(new String("numTextureUnits"), new Integer(getCapability(10)));
        var0.put(new String("maxTransformsPerVertex"), new Integer(getCapability(11)));
        var0.put(new String("maxViewportWidth"), new Integer(maxViewportWidth));
        var0.put(new String("maxViewportHeight"), new Integer(maxViewportHeight));
        var0.put(new String("C3A458D3-2015-41f5-8338-66A2D3014335"), Engine.getVersionMajor() + "."
                + Engine.getVersionMinor() + "." + Engine.getRevisionMajor() + "." + Engine.getRevisionMinor() + ":"
                + Engine.getBranchNumber());
        try {
            var0.put(new String("com.superscape.m3gx.DebugUtils"), Class.forName("javax.microedition.m3g.DebugUtils")
                    .newInstance());
        } catch (Exception localException) {
        }
        try {
            var0.put(new String("com.superscape.m3gx.Image2DUtils"), Class
                    .forName("javax.microedition.m3g.Image2DUtils").newInstance());
        } catch (Exception localException1) {
        }
        try {
            var0.put(new String("com.superscape.m3gx.SerializeOut"), Class
                    .forName("javax.microedition.m3g.SerializeOut").newInstance());
        } catch (Exception localException2) {
        }
        return var0;
    }

    static {
        i.a("jsr184client");
        Engine.cacheFID(Graphics3D.class, 1);
    }

    private static Graphics3D instance = null;
    private static int maxViewportWidth = getCapability(12);
    private static int maxViewportHeight = getCapability(13);
    int swerveHandle;
    public static final int ANTIALIAS = 2;
    public static final int DITHER = 4;
    public static final int TRUE_COLOR = 8;
    public static final int OVERWRITE = 16;
    static final int SUPPORTANTIALIASING = 0;
    static final int SUPPORTTRUECOLOR = 1;
    static final int SUPPORTDITHERING = 2;
    static final int SUPPORTMIPMAP = 3;
    static final int SUPPORTPERSPECTIVECORRECTION = 4;
    static final int SUPPORTLOCALCAMERALIGHTING = 5;
    static final int MAXLIGHTS = 6;
    static final int MAXVIEWPORTDIMENSION = 7;
    static final int MAXTEXTUREDIMENSION = 8;
    static final int MAXSPRITECROPDIMENSION = 9;
    static final int NUMTEXTUREUNITS = 10;
    static final int MAXTRANSFORMSPERVERTEX = 11;
    static final int MAXVIEWPORTWIDTH = 12;
    static final int MAXVIEWPORTHEIGHT = 13;
    static final int SUPPORTROTATE90 = 14;
    static final int SUPPORTROTATE180 = 15;
    static final int SUPPORTROTATE270 = 16;

    protected native void finalize();

    Graphics3D() {
    }

    public native float getDepthRangeNear();

    public native float getDepthRangeFar();

    public native boolean isDepthBufferEnabled();

    public native int getHints();

    public native int getLightCount();

    private static native int createImpl();

    private native void setBackBufferImage2D(Image2D paramImage2D);

    private native void setHints(boolean paramBoolean, int paramInt);

    private native void setViewportImpl(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

    public native void setDepthRange(float paramFloat1, float paramFloat2);

    private native void clearImpl(Background paramBackground);

    private native void renderPrimitive(VertexBuffer paramVertexBuffer, IndexBuffer paramIndexBuffer,
                                        Appearance paramAppearance, Transform paramTransform, int paramInt);

    private native void renderNode(Node paramNode, Transform paramTransform);

    private native void renderWorld(World paramWorld);

    private native int getCameraImpl(Transform paramTransform);

    private native void setCameraImpl(Camera paramCamera, Transform paramTransform);

    private native void setLightImpl(int paramInt, Light paramLight, Transform paramTransform);

    private native int getLightImpl(int paramInt, Transform paramTransform);

    private native int addLightImpl(Light paramLight, Transform paramTransform);

    public native void resetLights();

    private static native int getCapability(int paramInt);
}
