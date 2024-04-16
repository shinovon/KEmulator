package javax.microedition.m3g;

public class Group extends Node {
    Group(int n) {
        super(n);
    }

    public Group() {
        this(create());
        Engine.addJavaPeer(this.swerveHandle, this);
        this.ii = (getClass() != Group.class);
    }

    private static native int create();

    public native int getChildCount();

    public Node getChild(int n) {
        return (Node) Engine.instantiateJavaPeer(getChildImpl(n));
    }

    private native int getChildImpl(int paramInt);

    public void addChild(Node node) {
        addChildImpl(node);
        Engine.addXOT(node);
    }

    private native void addChildImpl(Node paramNode);

    public native void removeChild(Node paramNode);

    public boolean pick(int n, float n2, float n3, float n4, float n5, float n6, float n7,
                        RayIntersection rayIntersection) {
        return pickNode(n, n2, n3, n4, n5, n6, n7, rayIntersection);
    }

    private native boolean pickNode(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3,
                                    float paramFloat4, float paramFloat5, float paramFloat6, RayIntersection paramRayIntersection);

    public boolean pick(int n, float n2, float n3, Camera camera, RayIntersection rayIntersection) {
        return pickCamera(n, n2, n3, camera, rayIntersection);
    }

    private native boolean pickCamera(int paramInt, float paramFloat1, float paramFloat2, Camera paramCamera,
                                      RayIntersection paramRayIntersection);
}
