package javax.microedition.m3g;

import emulator.i;

public abstract class Object3D {
    int swerveHandle;
    Object uo;
    boolean ii;

    Object3D() {
        this.uo = null;
        this.ii = false;
    }

    Object3D(int swerveHandle) {
        this.uo = null;
        this.ii = false;
        this.swerveHandle = swerveHandle;
    }

    public AnimationTrack getAnimationTrack(int n) {
        return (AnimationTrack) Engine.instantiateJavaPeer(getAnimationTrackImpl(n));
    }

    public void addAnimationTrack(AnimationTrack animationTrack) {
        addAnimationTrackImpl(animationTrack);
        Engine.addXOT(animationTrack);
    }

    public Object3D find(int n) {
        return (Object3D) Engine.instantiateJavaPeer(findImpl(n));
    }

    public int getReferences(Object3D[] array) {
        int referencesImpl = getReferencesImpl(null);
        if (array == null) {
            return referencesImpl;
        }
        if (array.length < referencesImpl) {
            throw new IllegalArgumentException();
        }
        int[] array2 = new int[array.length];
        int referencesImpl2;
        if ((referencesImpl2 = getReferencesImpl(array2)) > referencesImpl) {
            referencesImpl2 = referencesImpl;
        }
        for (int i = 0; i < referencesImpl2; i++) {
            array[i] = ((Object3D) Engine.instantiateJavaPeer(array2[i]));
        }
        return referencesImpl2;
    }

    public final Object3D duplicate() {
        Object3D object3D;
        duplicateHelper(object3D = (Object3D) Engine.instantiateJavaPeer(duplicateImpl()), this);
        return object3D;
    }

    private static void duplicateHelper(Object3D object3D, Object3D object3D2) {
        object3D.setUserObject(object3D2.uo);
        if ((object3D2 instanceof Group)) {
            Group group = (Group) object3D2;
            Group group2 = (Group) object3D;
            int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                try {
                    duplicateHelper(group2.getChild(i), group.getChild(i));
                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                    return;
                }
            }
            return;
        }
        if ((object3D2 instanceof SkinnedMesh)) {
            duplicateHelper(((SkinnedMesh) object3D).getSkeleton(), ((SkinnedMesh) object3D2).getSkeleton());
        }
    }

    public Object getUserObject() {
        return this.uo;
    }

    public void setUserObject(Object uo) {
        this.uo = uo;
        if (this.uo != null) {
            Engine.addXOT(this);
        }
    }

    static {
        i.a("jsr184client");
        Engine.cacheFID(Object3D.class, 0);
    }

    protected native void finalize();

    public native int getUserID();

    public native int getAnimationTrackCount();

    public native void setUserID(int paramInt);

    private native int getAnimationTrackImpl(int paramInt);

    private native void addAnimationTrackImpl(AnimationTrack paramAnimationTrack);

    public native void removeAnimationTrack(AnimationTrack paramAnimationTrack);

    public final native int animate(int paramInt);

    private native int findImpl(int paramInt);

    private native int getReferencesImpl(int[] paramArrayOfInt);

    native void removeUserParameters();

    native int getUserParameterID(int paramInt);

    native int getUserParameterValue(int paramInt, byte[] paramArrayOfByte);

    private native int duplicateImpl();
}
