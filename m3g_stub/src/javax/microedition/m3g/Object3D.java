package javax.microedition.m3g;

public abstract class Object3D {
    int handle;

    public final Object3D duplicate() {
        return null;
    }

    public int getReferences(Object3D[] paramArrayOfObject3D) {
        return 1;
    }

    public void setUserID(int paramInt) {
    }

    public int getUserID() {
        return 1;
    }

    public Object3D find(int paramInt) {
        return null;
    }

    public void addAnimationTrack(AnimationTrack paramAnimationTrack) {
    }

    public AnimationTrack getAnimationTrack(int paramInt) {
        return null;
    }

    public void removeAnimationTrack(AnimationTrack paramAnimationTrack) {
    }

    public int getAnimationTrackCount() {
        return 1;
    }

    public final int animate(int paramInt) {
        return 1;
    }

    public void setUserObject(Object paramObject) {
    }

    public Object getUserObject() {
        return null;
    }

    Object3D(int paramInt) {
    }
}
