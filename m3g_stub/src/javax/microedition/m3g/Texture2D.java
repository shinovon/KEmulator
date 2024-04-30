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
        return null;
    }

    public void setFiltering(int paramInt1, int paramInt2) {
    }

    public void setWrapping(int paramInt1, int paramInt2) {
    }

    public int getWrappingS() {
        return 1;
    }

    public int getWrappingT() {
        return 1;
    }

    public void setBlending(int paramInt) {
    }

    public int getBlending() {
        return 1;
    }

    public void setBlendColor(int paramInt) {
    }

    public int getBlendColor() {
        return 1;
    }

    public int getImageFilter() {
        return 1;
    }

    public int getLevelFilter() {
        return 1;
    }

    Texture2D(int paramInt) {
        super(paramInt);
    }
}
