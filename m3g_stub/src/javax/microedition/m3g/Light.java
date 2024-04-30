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
        return 1.0F;
    }

    public void setColor(int paramInt) {
    }

    public int getColor() {
        return 1;
    }

    public void setMode(int paramInt) {
    }

    public int getMode() {
        return 1;
    }

    public void setSpotAngle(float paramFloat) {
    }

    public float getSpotAngle() {
        return 1.0F;
    }

    public void setSpotExponent(float paramFloat) {
    }

    public float getSpotExponent() {
        return 1.0F;
    }

    public void setAttenuation(float paramFloat1, float paramFloat2, float paramFloat3) {
    }

    public float getConstantAttenuation() {
        return 1.0F;
    }

    public float getLinearAttenuation() {
        return 1.0F;
    }

    public float getQuadraticAttenuation() {
        return 1.0F;
    }

    Light(int paramInt) {
        super(paramInt);
    }
}
