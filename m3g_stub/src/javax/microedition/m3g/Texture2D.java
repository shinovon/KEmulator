package javax.microedition.m3g;

public class Texture2D
        extends Transformable {
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

    public Texture2D(Image2D paramImage2D) {
        super(2);
    }

    public void setImage(Image2D paramImage2D) {
    }

    public Image2D getImage() {
        Image2D localImage2D = null;
        return localImage2D;
    }

    public void setFiltering(int paramInt1, int paramInt2) {
    }

    public void setWrapping(int paramInt1, int paramInt2) {
    }

    public int getWrappingS() {
        int i = 1;
        return i;
    }

    public int getWrappingT() {
        int i = 1;
        return i;
    }

    public void setBlending(int paramInt) {
    }

    public int getBlending() {
        int i = 1;
        return i;
    }

    public void setBlendColor(int paramInt) {
    }

    public int getBlendColor() {
        int i = 1;
        return i;
    }

    public int getImageFilter() {
        int i = 1;
        return i;
    }

    public int getLevelFilter() {
        int i = 1;
        return i;
    }

    Texture2D(int paramInt) {
        super(paramInt);
    }
}
