package javax.microedition.m3g;

public class Background extends Object3D {
    public static final int BORDER = 32;
    public static final int REPEAT = 33;

    Background(final int n) {
        super(n);
    }

    public Background() {
        this(create());
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != Background.class);
    }

    private static native int create();

    public native int getColor();

    public Image2D getImage() {
        return (Image2D) Engine.instantiateJavaPeer(getImageImpl());
    }

    private native int getImageImpl();

    public native int getImageModeX();

    public native int getImageModeY();

    public native int getCropX();

    public native int getCropY();

    public native int getCropWidth();

    public native int getCropHeight();

    public native boolean isColorClearEnabled();

    public native boolean isDepthClearEnabled();

    public native void setColor(final int p0);

    public void setImage(final Image2D imageImpl) {
        this.setImageImpl(imageImpl);
        Engine.addXOT(imageImpl);
    }

    private native void setImageImpl(final Image2D p0);

    public native void setColorClearEnable(final boolean p0);

    public native void setDepthClearEnable(final boolean p0);

    public native void setImageMode(final int p0, final int p1);

    public native void setCrop(final int p0, final int p1, final int p2, final int p3);
}
