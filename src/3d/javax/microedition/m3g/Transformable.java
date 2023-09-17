package javax.microedition.m3g;

public abstract class Transformable extends Object3D
{
    Transformable(final int n) {
        super(n);
    }
    
    Transformable() {
    }
    
    public native void getTranslation(final float[] p0);
    
    public native void getScale(final float[] p0);
    
    public native void getOrientation(final float[] p0);
    
    public native void setTranslation(final float p0, final float p1, final float p2);
    
    public native void setScale(final float p0, final float p1, final float p2);
    
    public native void setOrientation(final float p0, final float p1, final float p2, final float p3);
    
    public native void translate(final float p0, final float p1, final float p2);
    
    public native void scale(final float p0, final float p1, final float p2);
    
    public native void preRotate(final float p0, final float p1, final float p2, final float p3);
    
    public native void postRotate(final float p0, final float p1, final float p2, final float p3);
    
    public native void getTransform(final Transform p0);
    
    public native void getCompositeTransform(final Transform p0);
    
    public native void setTransform(final Transform p0);
}
