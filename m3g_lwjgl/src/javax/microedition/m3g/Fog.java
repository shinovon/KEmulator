package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;

public class Fog extends Object3D {
    public static final int EXPONENTIAL = 80;
    public static final int LINEAR = 81;
    private int mode = 81;
    private float density = 1.0F;
    private float near = 0.0F;
    private float far = 1.0F;
    private int color = 0;

    public void setMode(int var1) {
        if (var1 != 80 && var1 != 81) {
            throw new IllegalArgumentException();
        } else {
            this.mode = var1;
        }
    }

    public int getMode() {
        return this.mode;
    }

    public void setLinear(float var1, float var2) {
        this.near = var1;
        this.far = var2;
    }

    public float getNearDistance() {
        return this.near;
    }

    public float getFarDistance() {
        return this.far;
    }

    public void setDensity(float var1) {
        if (var1 < 0.0F) {
            throw new IllegalArgumentException();
        } else {
            this.density = var1;
        }
    }

    public float getDensity() {
        return this.density;
    }

    public void setColor(int var1) {
        this.color = var1;
    }

    public int getColor() {
        return this.color;
    }

    protected void updateProperty(int var1, float[] var2) {
        switch (var1) {
            case 258:
                this.color = G3DUtils.getIntColor(var2);
                return;
            case 259:
            case 261:
            case 262:
            case 264:
            case 265:
            case 266:
            default:
                super.updateProperty(var1, var2);
                return;
            case 260:
                this.density = G3DUtils.limitPositive(var2[0]);
                return;
            case 263:
                this.far = var2[0];
                return;
            case 267:
                this.near = var2[0];
        }
    }
}
