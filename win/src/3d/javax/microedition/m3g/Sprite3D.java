package javax.microedition.m3g;

public class Sprite3D extends Node {

    Sprite3D(final int n) {
        super(n);
    }

    public Sprite3D(final boolean b, final Image2D image2D, final Appearance appearance) {
        this(create(b, image2D, appearance));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != Sprite3D.class);
        Engine.addXOT(image2D);
        Engine.addXOT(appearance);
    }

    private static native int create(final boolean p0, final Image2D p1, final Appearance p2);

    public Appearance getAppearance() {
        return (Appearance) Engine.instantiateJavaPeer(this.getAppearanceImpl());
    }

    private native int getAppearanceImpl();

    public Image2D getImage() {
        return (Image2D) Engine.instantiateJavaPeer(this.getImageImpl());
    }

    private native int getImageImpl();

    public native int getCropHeight();

    public native int getCropWidth();

    public native int getCropX();

    public native int getCropY();

    public void setAppearance(final Appearance appearanceImpl) {
        this.setAppearanceImpl(appearanceImpl);
        Engine.addXOT(appearanceImpl);
    }

    private native void setAppearanceImpl(final Appearance p0);

    public void setImage(final Image2D imageImpl) {
        this.setImageImpl(imageImpl);
        Engine.addXOT(imageImpl);
    }

    private native void setImageImpl(final Image2D p0);

    public native boolean isScaled();

    public native void setCrop(final int p0, final int p1, final int p2, final int p3);
}
