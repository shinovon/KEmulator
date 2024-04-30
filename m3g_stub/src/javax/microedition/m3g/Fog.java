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
        return 1;
    }

    public void setLinear(float paramFloat1, float paramFloat2) {
    }

    public float getNearDistance() {
        return 1.0F;
    }

    public float getFarDistance() {
        return 1.0F;
    }

    public void setDensity(float paramFloat) {
    }

    public float getDensity() {
        return 1.0F;
    }

    public void setColor(int paramInt) {
    }

    public int getColor() {
        return 1;
    }

    Fog(int paramInt) {
        super(paramInt);
    }
}
