package javax.microedition.m3g;

import emulator.i;

public class Transform {
    int swerveHandle;

    Transform(int swerveHandle) {
        this.swerveHandle = swerveHandle;
    }

    public Transform() {
        Engine.addJavaPeer(this.swerveHandle = create(), this);
    }

    public Transform(Transform transform) {
        Engine.addJavaPeer(this.swerveHandle = createCopy(transform), this);
    }

    public void set(float[] matrix) {
        setMatrix(matrix);
    }

    public void set(Transform transform) {
        setTransform(transform);
    }

    public void transform(float[] array) {
        transformPoints(array);
    }

    static {
        i.a("jsr184client");
        Engine.cacheFID(Transform.class, 4);
    }

    protected native void finalize();

    private static native int create();

    private static native int createCopy(Transform paramTransform);

    public native void get(float[] paramArrayOfFloat);

    private native void setMatrix(float[] paramArrayOfFloat);

    private native void setTransform(Transform paramTransform);

    public native void setIdentity();

    public native void invert();

    public native void postMultiply(Transform paramTransform);

    public native void postRotate(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

    public native void postRotateQuat(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

    public native void postScale(float paramFloat1, float paramFloat2, float paramFloat3);

    public native void postTranslate(float paramFloat1, float paramFloat2, float paramFloat3);

    public native void transform(VertexArray paramVertexArray, float[] paramArrayOfFloat, boolean paramBoolean);

    private native void transformPoints(float[] paramArrayOfFloat);

    public native void transpose();
}
