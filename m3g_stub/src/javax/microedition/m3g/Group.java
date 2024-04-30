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
        return 1;
    }

    public Node getChild(int paramInt) {
        return null;
    }

    public boolean pick(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, RayIntersection paramRayIntersection) {
        return true;
    }

    public boolean pick(int paramInt, float paramFloat1, float paramFloat2, Camera paramCamera, RayIntersection paramRayIntersection) {
        return true;
    }

    Group(int paramInt) {
        super(paramInt);
    }
}
