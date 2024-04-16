package javax.microedition.m3g;

public class Fog
        extends Object3D {
    public static final int EXPONENTIAL = 80;
    public static final int LINEAR = 81;

    public Fog() {
        super(2);
    }

    public void setMode(int paramInt) {
    }

    public int getMode() {
        int i = 1;
        return i;
    }

    public void setLinear(float paramFloat1, float paramFloat2) {
    }

    public float getNearDistance() {
        float f = 1.0F;
        return f;
    }

    public float getFarDistance() {
        float f = 1.0F;
        return f;
    }

    public void setDensity(float paramFloat) {
    }

    public float getDensity() {
        float f = 1.0F;
        return f;
    }

    public void setColor(int paramInt) {
    }

    public int getColor() {
        int i = 1;
        return i;
    }

    Fog(int paramInt) {
        super(paramInt);
    }
}
