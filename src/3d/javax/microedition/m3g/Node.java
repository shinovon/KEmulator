package javax.microedition.m3g;

public abstract class Node extends Transformable {
	public static final int NONE = 144;
	public static final int ORIGIN = 145;
	public static final int X_AXIS = 146;
	public static final int Y_AXIS = 147;
	public static final int Z_AXIS = 148;

	Node(int n) {
		super(n);
	}

	Node() {
	}

	public Node getParent() {
		return (Node) Engine.instantiateJavaPeer(getParentImpl());
	}

	private native int getParentImpl();

	public native float getAlphaFactor();

	public native boolean isRenderingEnabled();

	public native boolean isPickingEnabled();

	public native int getScope();

	public native void setAlphaFactor(float paramFloat);

	public native void setRenderingEnable(boolean paramBoolean);

	public native void setPickingEnable(boolean paramBoolean);

	public native void setScope(int paramInt);

	public native boolean getTransformTo(Node paramNode, Transform paramTransform);

	public void setAlignment(Node node, int n, Node node2, int n2) {
		setAlignmentImpl(node, n, node2, n2);
		Engine.addXOT(node);
		Engine.addXOT(node2);
	}

	private native void setAlignmentImpl(Node paramNode1, int paramInt1, Node paramNode2, int paramInt2);

	public native int getAlignmentTarget(int paramInt);

	public Node getAlignmentReference(int n) {
		return (Node) Engine.instantiateJavaPeer(getAlignmentReferenceImpl(n));
	}

	private native int getAlignmentReferenceImpl(int paramInt);

	public final native void align(Node paramNode);
}
