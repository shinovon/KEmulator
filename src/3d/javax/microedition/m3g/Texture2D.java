package javax.microedition.m3g;

public class Texture2D extends Transformable
{
    public static final int FILTER_BASE_LEVEL = 208;
    public static final int FILTER_LINEAR = 209;
    public static final int FILTER_NEAREST = 210;
    public static final int FUNC_ADD = 224;
    public static final int FUNC_BLEND = 225;
    public static final int FUNC_DECAL = 226;
    public static final int FUNC_MODULATE = 227;
    public static final int FUNC_REPLACE = 228;
    public static final int WRAP_CLAMP = 240;
    public static final int WRAP_REPEAT = 241;
    
    Texture2D(final int n) {
        super(n);
    }
    
    public Texture2D(final Image2D image2D) {
        this(create(image2D));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (getClass() != Texture2D.class);
        Engine.addXOT(image2D);
    }
    
    private static native int create(final Image2D p0);
    
    public Image2D getImage() {
        return (Image2D)Engine.instantiateJavaPeer(this.getImageImpl());
    }
    
    private native int getImageImpl();
    
    public native int getBlending();
    
    public native int getBlendColor();
    
    public native int getImageFilter();
    
    public native int getLevelFilter();
    
    public void setImage(final Image2D imageImpl) {
        this.setImageImpl(imageImpl);
        Engine.addXOT(imageImpl);
    }
    
    private native void setImageImpl(final Image2D p0);
    
    public native void setBlending(final int p0);
    
    public native void setBlendColor(final int p0);
    
    public native int getWrappingS();
    
    public native int getWrappingT();
    
    public native void setWrapping(final int p0, final int p1);
    
    public native void setFiltering(final int p0, final int p1);
}
