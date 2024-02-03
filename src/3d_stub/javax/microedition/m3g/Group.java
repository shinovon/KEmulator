package javax.microedition.m3g;

public class Group
        extends Node {
    public Group() {
        super(2);
    }

    public void addChild(Node paramNode) {
    }

    public void removeChild(Node paramNode) {
    }

    public int getChildCount() {
        int i = 1;
        return i;
    }

    public Node getChild(int paramInt) {
        Node localNode = null;
        return localNode;
    }

    public boolean pick(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, RayIntersection paramRayIntersection) {
        boolean bool = true;
        return bool;
    }

    public boolean pick(int paramInt, float paramFloat1, float paramFloat2, Camera paramCamera, RayIntersection paramRayIntersection) {
        boolean bool = true;
        return bool;
    }

    Group(int paramInt) {
        super(paramInt);
    }
}
