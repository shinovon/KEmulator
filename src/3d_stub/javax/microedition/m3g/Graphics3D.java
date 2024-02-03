package javax.microedition.m3g;

import java.util.Hashtable;

public class Graphics3D {
    public static final int ANTIALIAS = 2;
    public static final int DITHER = 4;
    public static final int TRUE_COLOR = 8;
    public static final int OVERWRITE = 16;

    public static final Graphics3D getInstance() {
        Graphics3D localGraphics3D = null;
        return localGraphics3D;
    }

    public void bindTarget(Object paramObject) {
    }

    public void bindTarget(Object paramObject, boolean paramBoolean, int paramInt) {
    }

    public void releaseTarget() {
    }

    public void setViewport(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    }

    public void clear(Background paramBackground) {
    }

    public void render(World paramWorld) {
    }

    public void render(VertexBuffer paramVertexBuffer, IndexBuffer paramIndexBuffer, Appearance paramAppearance, Transform paramTransform) {
    }

    public void render(VertexBuffer paramVertexBuffer, IndexBuffer paramIndexBuffer, Appearance paramAppearance, Transform paramTransform, int paramInt) {
    }

    public void render(Node paramNode, Transform paramTransform) {
    }

    public void setCamera(Camera paramCamera, Transform paramTransform) {
    }

    public int addLight(Light paramLight, Transform paramTransform) {
        int i = 1;
        return i;
    }

    public void setLight(int paramInt, Light paramLight, Transform paramTransform) {
    }

    public void resetLights() {
    }

    public static final Hashtable getProperties() {
        Hashtable localHashtable = null;
        return localHashtable;
    }

    public void setDepthRange(float paramFloat1, float paramFloat2) {
    }

    public Camera getCamera(Transform paramTransform) {
        Camera localCamera = null;
        return localCamera;
    }

    public float getDepthRangeFar() {
        float f = 1.0F;
        return f;
    }

    public float getDepthRangeNear() {
        float f = 1.0F;
        return f;
    }

    public Light getLight(int paramInt, Transform paramTransform) {
        Light localLight = null;
        return localLight;
    }

    public int getLightCount() {
        int i = 1;
        return i;
    }

    public Object getTarget() {
        Object localObject = null;
        return localObject;
    }

    public int getViewportHeight() {
        int i = 1;
        return i;
    }

    public int getViewportWidth() {
        int i = 1;
        return i;
    }

    public int getViewportX() {
        int i = 1;
        return i;
    }

    public int getViewportY() {
        int i = 1;
        return i;
    }

    public int getHints() {
        int i = 1;
        return i;
    }

    public boolean isDepthBufferEnabled() {
        boolean bool = true;
        return bool;
    }
}
