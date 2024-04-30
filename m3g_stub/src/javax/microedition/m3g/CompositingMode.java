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
        return 1;
    }

    public void setAlphaThreshold(float paramFloat) {
    }

    public float getAlphaThreshold() {
        return 1.0F;
    }

    public void setAlphaWriteEnable(boolean paramBoolean) {
    }

    public boolean isAlphaWriteEnabled() {
        return true;
    }

    public void setColorWriteEnable(boolean paramBoolean) {
    }

    public boolean isColorWriteEnabled() {
        return true;
    }

    public void setDepthWriteEnable(boolean paramBoolean) {
    }

    public boolean isDepthWriteEnabled() {
        return true;
    }

    public void setDepthTestEnable(boolean paramBoolean) {
    }

    public boolean isDepthTestEnabled() {
        return true;
    }

    public void setDepthOffset(float paramFloat1, float paramFloat2) {
    }

    public float getDepthOffsetFactor() {
        return 1.0F;
    }

    public float getDepthOffsetUnits() {
        return 1.0F;
    }

    CompositingMode(int paramInt) {
        super(paramInt);
    }
}
