package javax.microedition.m3g;

public class PolygonMode extends Object3D
{
    public static final int CULL_BACK = 160;
    public static final int CULL_FRONT = 161;
    public static final int CULL_NONE = 162;
    public static final int SHADE_FLAT = 164;
    public static final int SHADE_SMOOTH = 165;
    public static final int WINDING_CCW = 168;
    public static final int WINDING_CW = 169;
    static Class class$javax$microedition$m3g$PolygonMode;
    
    PolygonMode(final int n) {
        super(n);
    }
    
    public PolygonMode() {
        this(create());
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != ((PolygonMode.class$javax$microedition$m3g$PolygonMode == null) ? (PolygonMode.class$javax$microedition$m3g$PolygonMode = class$("javax.microedition.m3g.PolygonMode")) : PolygonMode.class$javax$microedition$m3g$PolygonMode));
    }
    
    private static native int create();
    
    public native int getCulling();
    
    public native int getShading();
    
    public native int getWinding();
    
    public native boolean isLocalCameraLightingEnabled();
    
    public native boolean isPerspectiveCorrectionEnabled();
    
    public native boolean isTwoSidedLightingEnabled();
    
    public native void setCulling(final int p0);
    
    public native void setShading(final int p0);
    
    public native void setWinding(final int p0);
    
    public native void setLocalCameraLightingEnable(final boolean p0);
    
    public native void setPerspectiveCorrectionEnable(final boolean p0);
    
    public native void setTwoSidedLightingEnable(final boolean p0);
    
    static Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
}
