package javax.microedition.m3g;

public class Light
        extends Node {
    public static final int AMBIENT = 128;
    public static final int DIRECTIONAL = 129;
    public static final int OMNI = 130;
    public static final int SPOT = 131;

    public Light() {
        super(2);
    }

    public void setIntensity(float paramFloat) {
    }

    public float getIntensity() {
        float f = 1.0F;
        return f;
    }

    public void setColor(int paramInt) {
    }

    public int getColor() {
        int i = 1;
        return i;
    }

    public void setMode(int paramInt) {
    }

    public int getMode() {
        int i = 1;
        return i;
    }

    public void setSpotAngle(float paramFloat) {
    }

    public float getSpotAngle() {
        float f = 1.0F;
        return f;
    }

    public void setSpotExponent(float paramFloat) {
    }

    public float getSpotExponent() {
        float f = 1.0F;
        return f;
    }

    public void setAttenuation(float paramFloat1, float paramFloat2, float paramFloat3) {
    }

    public float getConstantAttenuation() {
        float f = 1.0F;
        return f;
    }

    public float getLinearAttenuation() {
        float f = 1.0F;
        return f;
    }

    public float getQuadraticAttenuation() {
        float f = 1.0F;
        return f;
    }

    Light(int paramInt) {
        super(paramInt);
    }
}
