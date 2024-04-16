package javax.microedition.m3g;

import javax.microedition.lcdui.*;

public class Image2D extends Object3D {
    public static final int ALPHA = 96;
    public static final int LUMINANCE = 97;
    public static final int LUMINANCE_ALPHA = 98;
    public static final int RGB = 99;
    public static final int RGBA = 100;

    Image2D(final int n) {
        super(n);
    }

    public Image2D(final int n, final int n2, final int n3, final byte[] array) {
        this(create(n, n2, n3, array));
        Engine.addJavaPeer(super.swerveHandle, this);
        this.ii = (getClass() != Image2D.class);
    }

    private static native int create(final int p0, final int p1, final int p2, final byte[] p3);

    public Image2D(final int n, final int n2, final int n3, final byte[] array, final byte[] array2) {
        this(createPalettized(n, n2, n3, array, array2));
        Engine.addJavaPeer(super.swerveHandle, this);
        this.ii = (getClass() != Image2D.class);
    }

    private static native int createPalettized(final int p0, final int p1, final int p2, final byte[] p3, final byte[] p4);

    public Image2D(final int n, final int n2, final int n3) {
        this(createMutable(n, n2, n3));
        Engine.addJavaPeer(super.swerveHandle, this);
        this.ii = (getClass() != Image2D.class);
    }

    private static native int createMutable(final int p0, final int p1, final int p2);

    private static native int createUninitialized(final int p0, final int p1, final int p2, final boolean p3);

    public Image2D(final int n, final Object o) {
        this(createImage(n, o));
        Engine.addJavaPeer(super.swerveHandle, this);
        this.ii = (getClass() != Image2D.class);
        final Image image = (Image) o;
        final int width = this.getWidth();
        final int height = this.getHeight();
        final boolean opaque = isOpaque(image);
        if (width <= height) {
            final int[] array = new int[width];
            for (int i = 0; i < height; ++i) {
                getImageRGB(image, array, 0, width, 0, i, width, 1);
                this.setRGB(array, 0, i, width, 1, opaque);
            }
            return;
        }
        final int[] array2 = new int[height];
        for (int j = 0; j < width; ++j) {
            getImageRGB(image, array2, 0, 1, j, 0, 1, height);
            this.setRGB(array2, j, 0, 1, height, opaque);
        }
    }

    Image2D(final byte[] array, final int n, final int n2) {
        this(createImageImpl(array, n, n2));
        Engine.addJavaPeer(super.swerveHandle, this);
        this.ii = (getClass() != Image2D.class);
    }

    public native int getFormat();

    public native int getWidth();

    public native int getHeight();

    public native boolean isMutable();

    public native void set(final int p0, final int p1, final int p2, final int p3, final byte[] p4);

    native void unpalettize();

    private native void setRGB(final int[] p0, final int p1, final int p2, final int p3, final int p4, final boolean p5);

    private static int createImage(final int n, final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (!(o instanceof Image)) {
            throw new IllegalArgumentException();
        }
        final Image image = (Image) o;
        return createUninitialized(n, getImageWidth(image), getImageHeight(image), true);
    }

    private static native int createImageImpl(final byte[] p0, final int p1, final int p2);

    private static int getImageWidth(final Image image) {
        return Helpers.getImageWidth(image);
    }

    private static int getImageHeight(final Image image) {
        return Helpers.getImageHeight(image);
    }

    static void getImageRGB(final Image image, final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        Helpers.getImageRGB(image, array, n, n2, n3, n4, n5, n6);
    }

    static boolean isOpaque(final Image image) {
        return Helpers.isOpaque(image);
    }

    public native boolean isPalettized();

    public native void getPalette(final byte[] p0);

    native void setPalette(final byte[] p0);

    public native void getPixels(final byte[] p0);

    native void setPixels(final byte[] p0);

    private native int qonvertATITCImpl(final int p0);

    Image2D qonvertATITC(final int n) {
        return (Image2D) Engine.instantiateJavaPeer(this.qonvertATITCImpl(n));
    }

    public int getBitsPerColor() {
        int m = 1;
        switch (getFormat()) {
            case ALPHA:
            case LUMINANCE:
                m = 1;
                break;
            case LUMINANCE_ALPHA:
                m = 2;
                break;
            case RGB:
                m = 3;
                break;
            case RGBA:
                m = 4;
                break;
        }

        return m;
    }

    public int size() {
        return getWidth() * getHeight() * getBitsPerColor() + 4;
    }
}
