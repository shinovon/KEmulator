package javax.microedition.m3g;

public class Camera
        extends Node {
    public static final int GENERIC = 48;
    public static final int PARALLEL = 49;
    public static final int PERSPECTIVE = 50;

    public Camera() {
        super(2);
    }

    public void setParallel(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
    }

    public void setPerspective(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
    }

    public void setGeneric(Transform paramTransform) {
    }

    public int getProjection(Transform paramTransform) {
        int i = 1;
        return i;
    }

    public int getProjection(float[] paramArrayOfFloat) {
        int i = 1;
        return i;
    }

    Camera(int paramInt) {
        super(paramInt);
    }
}
