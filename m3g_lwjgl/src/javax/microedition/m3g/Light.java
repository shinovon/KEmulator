package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;

public class Light extends Node {
    public static final int AMBIENT = 128;
    public static final int DIRECTIONAL = 129;
    public static final int OMNI = 130;
    public static final int SPOT = 131;
    private int mode = 129;
    private int color = 16777215;
    private float intensity = 1.0F;
    private float constantAttenuation = 1.0F;
    private float linearAttenuation = 0.0F;
    private float quadraticAttenuation = 0.0F;
    private float spotAngle = 45.0F;
    private float spotExponent = 0.0F;

    public void setMode(int var1) {
        if (var1 >= 128 && var1 <= 131) {
            this.mode = var1;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int getMode() {
        return this.mode;
    }

    public void setIntensity(float var1) {
        this.intensity = var1;
    }

    public float getIntensity() {
        return this.intensity;
    }

    public void setColor(int var1) {
        this.color = var1;
    }

    public int getColor() {
        return this.color;
    }

    public void setSpotAngle(float var1) {
        if (var1 >= 0.0F && var1 <= 90.0F) {
            this.spotAngle = var1;
        } else {
            throw new IllegalArgumentException("angle is not in [0, 90]");
        }
    }

    public float getSpotAngle() {
        return this.spotAngle;
    }

    public void setSpotExponent(float var1) {
        if (var1 >= 0.0F && var1 <= 128.0F) {
            this.spotExponent = var1;
        } else {
            throw new IllegalArgumentException("exponent is not in [0, 128]");
        }
    }

    public float getSpotExponent() {
        return this.spotExponent;
    }

    public void setAttenuation(float var1, float var2, float var3) {
        if (var1 >= 0.0F && var2 >= 0.0F && var3 >= 0.0F) {
            if (var1 == 0.0F && var2 == 0.0F && var3 == 0.0F) {
                throw new IllegalArgumentException("all of the parameter values are zero");
            } else {
                this.constantAttenuation = var1;
                this.linearAttenuation = var2;
                this.quadraticAttenuation = var3;
            }
        } else {
            throw new IllegalArgumentException("any of the parameter values are negative");
        }
    }

    public float getConstantAttenuation() {
        return this.constantAttenuation;
    }

    public float getLinearAttenuation() {
        return this.linearAttenuation;
    }

    public float getQuadraticAttenuation() {
        return this.quadraticAttenuation;
    }

    protected void updateProperty(int var1, float[] var2) {
        switch (var1) {
            case 258:
                this.color = G3DUtils.getIntColor(var2);
                return;
            case 265:
                this.intensity = var2[0];
                return;
            case 273:
                this.spotAngle = G3DUtils.limit(var2[0], 0.0F, 90.0F);
                return;
            case 274:
                this.spotExponent = G3DUtils.limit(var2[0], 0.0F, 128.0F);
                return;
            default:
                super.updateProperty(var1, var2);
        }
    }

    protected boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4) {
        return false;
    }
}
