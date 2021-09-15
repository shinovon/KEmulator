package javax.microedition.m3g;

public class Material extends Object3D
{
    public static final int AMBIENT = 1024;
    public static final int DIFFUSE = 2048;
    public static final int EMISSIVE = 4096;
    public static final int SPECULAR = 8192;
    static Class class$javax$microedition$m3g$Material;
    
    Material(final int n) {
        super(n);
    }
    
    public Material() {
        this(create());
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != ((Material.class$javax$microedition$m3g$Material == null) ? (Material.class$javax$microedition$m3g$Material = class$("javax.microedition.m3g.Material")) : Material.class$javax$microedition$m3g$Material));
    }
    
    private static native int create();
    
    public native float getShininess();
    
    public native boolean isVertexColorTrackingEnabled();
    
    public native void setShininess(final float p0);
    
    public native void setVertexColorTrackingEnable(final boolean p0);
    
    public native int getColor(final int p0);
    
    public native void setColor(final int p0, final int p1);
    
    static Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
}
