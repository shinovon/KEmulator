package javax.microedition.m3g;

public abstract class Node
        extends Transformable {
    public static final int NONE = 144;
    public static final int ORIGIN = 145;
    public static final int X_AXIS = 146;
    public static final int Y_AXIS = 147;
    public static final int Z_AXIS = 148;

    public Node getParent() {
        return null;
    }

    public boolean getTransformTo(Node paramNode, Transform paramTransform) {
        return true;
    }

    public void setAlignment(Node paramNode1, int paramInt1, Node paramNode2, int paramInt2) {
    }

    public void setAlphaFactor(float paramFloat) {
    }

    public float getAlphaFactor() {
        return 1.0F;
    }

    public void setRenderingEnable(boolean paramBoolean) {
    }

    public boolean isRenderingEnabled() {
        return true;
    }

    public void setPickingEnable(boolean paramBoolean) {
    }

    public boolean isPickingEnabled() {
        return true;
    }

    public void setScope(int paramInt) {
    }

    public int getScope() {
        return 1;
    }

    public final void align(Node paramNode) {
    }

    public Node getAlignmentReference(int paramInt) {
        return null;
    }

    public int getAlignmentTarget(int paramInt) {
        return 1;
    }

    Node(int paramInt) {
        super(paramInt);
    }
}
