package javax.microedition.m3g;

public class Material
        extends Object3D {
    public static final int AMBIENT = 1024;
    public static final int DIFFUSE = 2048;
    public static final int EMISSIVE = 4096;
    public static final int SPECULAR = 8192;

    public Material() {
        super(2);
    }

    public void setColor(int paramInt1, int paramInt2) {
    }

    public int getColor(int paramInt) {
        int i = 1;
        return i;
    }

    public void setShininess(float paramFloat) {
    }

    public float getShininess() {
        float f = 1.0F;
        return f;
    }

    public void setVertexColorTrackingEnable(boolean paramBoolean) {
    }

    public boolean isVertexColorTrackingEnabled() {
        boolean bool = true;
        return bool;
    }

    Material(int paramInt) {
        super(paramInt);
    }
}
