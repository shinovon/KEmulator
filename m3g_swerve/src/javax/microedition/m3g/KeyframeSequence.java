package javax.microedition.m3g;

public class KeyframeSequence extends Object3D {
    public static final int LINEAR = 176;
    public static final int SLERP = 177;
    public static final int SPLINE = 178;
    public static final int SQUAD = 179;
    public static final int STEP = 180;
    public static final int CONSTANT = 192;
    public static final int LOOP = 193;

    KeyframeSequence(final int n) {
        super(n);
    }

    public KeyframeSequence(final int n, final int n2, final int n3) {
        this(create(n, n2, n3));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != KeyframeSequence.class);
    }

    private static native int create(final int p0, final int p1, final int p2);

    public native int getDuration();

    public native int getRepeatMode();

    public native int getKeyframeCount();

    public native int getComponentCount();

    public native int getInterpolationType();

    public native int getValidRangeFirst();

    public native int getValidRangeLast();

    public native void setDuration(final int p0);

    public native void setRepeatMode(final int p0);

    public native int getKeyframe(final int p0, final float[] p1);

    public native void setKeyframe(final int p0, final int p1, final float[] p2);

    public native void setValidRange(final int p0, final int p1);
}
