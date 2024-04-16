package javax.microedition.m3g;

public abstract class Node
        extends Transformable {
    public static final int NONE = 144;
    public static final int ORIGIN = 145;
    public static final int X_AXIS = 146;
    public static final int Y_AXIS = 147;
    public static final int Z_AXIS = 148;

    public Node getParent() {
        Node localNode = null;
        return localNode;
    }

    public boolean getTransformTo(Node paramNode, Transform paramTransform) {
        boolean bool = true;
        return bool;
    }

    public void setAlignment(Node paramNode1, int paramInt1, Node paramNode2, int paramInt2) {
    }

    public void setAlphaFactor(float paramFloat) {
    }

    public float getAlphaFactor() {
        float f = 1.0F;
        return f;
    }

    public void setRenderingEnable(boolean paramBoolean) {
    }

    public boolean isRenderingEnabled() {
        boolean bool = true;
        return bool;
    }

    public void setPickingEnable(boolean paramBoolean) {
    }

    public boolean isPickingEnabled() {
        boolean bool = true;
        return bool;
    }

    public void setScope(int paramInt) {
    }

    public int getScope() {
        int i = 1;
        return i;
    }

    public final void align(Node paramNode) {
    }

    public Node getAlignmentReference(int paramInt) {
        Node localNode = null;
        return localNode;
    }

    public int getAlignmentTarget(int paramInt) {
        int i = 1;
        return i;
    }

    Node(int paramInt) {
        super(paramInt);
    }
}
