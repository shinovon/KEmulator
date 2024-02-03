package javax.microedition.m3g;

public class CompositingMode
        extends Object3D {
    public static final int ALPHA = 64;
    public static final int ALPHA_ADD = 65;
    public static final int MODULATE = 66;
    public static final int MODULATE_X2 = 67;
    public static final int REPLACE = 68;

    public CompositingMode() {
        super(2);
    }

    public void setBlending(int paramInt) {
    }

    public int getBlending() {
        int i = 1;
        return i;
    }

    public void setAlphaThreshold(float paramFloat) {
    }

    public float getAlphaThreshold() {
        float f = 1.0F;
        return f;
    }

    public void setAlphaWriteEnable(boolean paramBoolean) {
    }

    public boolean isAlphaWriteEnabled() {
        boolean bool = true;
        return bool;
    }

    public void setColorWriteEnable(boolean paramBoolean) {
    }

    public boolean isColorWriteEnabled() {
        boolean bool = true;
        return bool;
    }

    public void setDepthWriteEnable(boolean paramBoolean) {
    }

    public boolean isDepthWriteEnabled() {
        boolean bool = true;
        return bool;
    }

    public void setDepthTestEnable(boolean paramBoolean) {
    }

    public boolean isDepthTestEnabled() {
        boolean bool = true;
        return bool;
    }

    public void setDepthOffset(float paramFloat1, float paramFloat2) {
    }

    public float getDepthOffsetFactor() {
        float f = 1.0F;
        return f;
    }

    public float getDepthOffsetUnits() {
        float f = 1.0F;
        return f;
    }

    CompositingMode(int paramInt) {
        super(paramInt);
    }
}
