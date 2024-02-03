package javax.microedition.m3g;

public abstract class IndexBuffer
        extends Object3D {
    public abstract int getIndexCount();

    public abstract void getIndices(int[] paramArrayOfInt);

    IndexBuffer(int paramInt) {
        super(paramInt);
    }
}
