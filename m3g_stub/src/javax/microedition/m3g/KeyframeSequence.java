package javax.microedition.m3g;

public class KeyframeSequence
        extends Object3D {
    public static final int LINEAR = 176;
    public static final int SLERP = 177;
    public static final int SPLINE = 178;
    public static final int SQUAD = 179;
    public static final int STEP = 180;
    public static final int CONSTANT = 192;
    public static final int LOOP = 193;

    public KeyframeSequence(int paramInt1, int paramInt2, int paramInt3) {
        super(2);
    }

    public void setKeyframe(int paramInt1, int paramInt2, float[] paramArrayOfFloat) {
    }

    public void setValidRange(int paramInt1, int paramInt2) {
    }

    public void setDuration(int paramInt) {
    }

    public int getDuration() {
        int i = 1;
        return i;
    }

    public void setRepeatMode(int paramInt) {
    }

    public int getRepeatMode() {
        int i = 1;
        return i;
    }

    public int getComponentCount() {
        int i = 1;
        return i;
    }

    public int getInterpolationType() {
        int i = 1;
        return i;
    }

    public int getKeyframe(int paramInt, float[] paramArrayOfFloat) {
        int i = 1;
        return i;
    }

    public int getKeyframeCount() {
        int i = 1;
        return i;
    }

    public int getValidRangeFirst() {
        int i = 1;
        return i;
    }

    public int getValidRangeLast() {
        int i = 1;
        return i;
    }

    KeyframeSequence(int paramInt) {
        super(paramInt);
    }
}
