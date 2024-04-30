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
        return 1;
    }

    public void setWinding(int paramInt) {
    }

    public int getWinding() {
        return 1;
    }

    public void setShading(int paramInt) {
    }

    public int getShading() {
        return 1;
    }

    public void setTwoSidedLightingEnable(boolean paramBoolean) {
    }

    public boolean isTwoSidedLightingEnabled() {
        return true;
    }

    public void setLocalCameraLightingEnable(boolean paramBoolean) {
    }

    public void setPerspectiveCorrectionEnable(boolean paramBoolean) {
    }

    public boolean isLocalCameraLightingEnabled() {
        return true;
    }

    public boolean isPerspectiveCorrectionEnabled() {
        return true;
    }

    PolygonMode(int paramInt) {
        super(paramInt);
    }
}
