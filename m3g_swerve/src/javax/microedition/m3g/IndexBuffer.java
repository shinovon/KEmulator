package javax.microedition.m3g;

public abstract class IndexBuffer extends Object3D {
    IndexBuffer(final int n) {
        super(n);
    }

    IndexBuffer() {
        super();
    }

    public abstract int getIndexCount();

    native int getIndexCountImpl();

    public abstract void getIndices(final int[] p0);

    native void getIndicesImpl(final int[] p0);
}
