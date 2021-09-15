package javax.microedition.m3g;

public class CompositingMode extends Object3D
{
    public static final int ALPHA = 64;
    public static final int ALPHA_ADD = 65;
    public static final int MODULATE = 66;
    public static final int MODULATE_X2 = 67;
    public static final int REPLACE = 68;
    static Class class$javax$microedition$m3g$CompositingMode;
    
    CompositingMode(final int n) {
        super(n);
    }
    
    public CompositingMode() {
        this(create());
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != ((CompositingMode.class$javax$microedition$m3g$CompositingMode == null) ? (CompositingMode.class$javax$microedition$m3g$CompositingMode = class$("javax.microedition.m3g.CompositingMode")) : CompositingMode.class$javax$microedition$m3g$CompositingMode));
    }
    
    private static native int create();
    
    public native float getAlphaThreshold();
    
    public native int getBlending();
    
    public native boolean isColorWriteEnabled();
    
    public native boolean isAlphaWriteEnabled();
    
    public native boolean isDepthWriteEnabled();
    
    public native boolean isDepthTestEnabled();
    
    public native float getDepthOffsetFactor();
    
    public native float getDepthOffsetUnits();
    
    public native void setAlphaThreshold(final float p0);
    
    public native void setBlending(final int p0);
    
    public native void setColorWriteEnable(final boolean p0);
    
    public native void setAlphaWriteEnable(final boolean p0);
    
    public native void setDepthWriteEnable(final boolean p0);
    
    public native void setDepthTestEnable(final boolean p0);
    
    public native void setDepthOffset(final float p0, final float p1);
    
    static Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
}
