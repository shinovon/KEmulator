package javax.microedition.m3g;

import emulator.*;

public class RayIntersection {
    int swerveHandle;

    protected native void finalize();

    RayIntersection(final int swerveHandle) {
        super();
        this.swerveHandle = swerveHandle;
    }

    public RayIntersection() {
        super();
        Engine.addJavaPeer(this.swerveHandle = create(), this);
    }

    private static native int create();

    public native float getDistance();

    public Node getIntersected() {
        return (Node) Engine.instantiateJavaPeer(this.getIntersectedImpl());
    }

    private native int getIntersectedImpl();

    public native int getSubmeshIndex();

    public native float getNormalX();

    public native float getNormalY();

    public native float getNormalZ();

    public native float getTextureS(final int p0);

    public native float getTextureT(final int p0);

    public native void getRay(final float[] p0);

    static {
        i.a("jsr184client");
        Engine.cacheFID(RayIntersection.class, 3);
    }
}
