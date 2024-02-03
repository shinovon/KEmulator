package javax.microedition.m3g;

public class PolygonMode
        extends Object3D {
    public static final int CULL_BACK = 160;
    public static final int CULL_FRONT = 161;
    public static final int CULL_NONE = 162;
    public static final int SHADE_FLAT = 164;
    public static final int SHADE_SMOOTH = 165;
    public static final int WINDING_CCW = 168;
    public static final int WINDING_CW = 169;

    public PolygonMode() {
        super(2);
    }

    public void setCulling(int paramInt) {
    }

    public int getCulling() {
        int i = 1;
        return i;
    }

    public void setWinding(int paramInt) {
    }

    public int getWinding() {
        int i = 1;
        return i;
    }

    public void setShading(int paramInt) {
    }

    public int getShading() {
        int i = 1;
        return i;
    }

    public void setTwoSidedLightingEnable(boolean paramBoolean) {
    }

    public boolean isTwoSidedLightingEnabled() {
        boolean bool = true;
        return bool;
    }

    public void setLocalCameraLightingEnable(boolean paramBoolean) {
    }

    public void setPerspectiveCorrectionEnable(boolean paramBoolean) {
    }

    public boolean isLocalCameraLightingEnabled() {
        boolean bool = true;
        return bool;
    }

    public boolean isPerspectiveCorrectionEnabled() {
        boolean bool = true;
        return bool;
    }

    PolygonMode(int paramInt) {
        super(paramInt);
    }
}
