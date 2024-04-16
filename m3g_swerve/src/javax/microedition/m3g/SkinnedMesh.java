package javax.microedition.m3g;

public class SkinnedMesh extends Mesh {

    SkinnedMesh(final int n) {
        super(n);
    }

    public SkinnedMesh(final VertexBuffer vertexBuffer, final IndexBuffer[] array, final Appearance[] array2, final Group group) {
        this(createMultiSubmesh(vertexBuffer, Engine.getJavaPeerArrayHandles(array), Engine.getJavaPeerArrayHandles(array2), group));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != SkinnedMesh.class);
        Engine.addXOT(vertexBuffer);
        Engine.addXOT(array);
        Engine.addXOT(array2);
        Engine.addXOT(group);
    }

    private static native int createMultiSubmesh(final VertexBuffer p0, final int[] p1, final int[] p2, final Group p3);

    public SkinnedMesh(final VertexBuffer vertexBuffer, final IndexBuffer indexBuffer, final Appearance appearance, final Group group) {
        this(createSingleSubmesh(vertexBuffer, indexBuffer, appearance, group));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != SkinnedMesh.class);
        Engine.addXOT(vertexBuffer);
        Engine.addXOT(indexBuffer);
        Engine.addXOT(appearance);
        Engine.addXOT(group);
    }

    private static native int createSingleSubmesh(final VertexBuffer p0, final IndexBuffer p1, final Appearance p2, final Group p3);

    public Group getSkeleton() {
        return (Group) Engine.instantiateJavaPeer(this.getSkeletonImpl());
    }

    private native int getSkeletonImpl();

    public void addTransform(final Node node, final int n, final int n2, final int n3) {
        this.addTransformImpl(node, n, n2, n3);
        Engine.addXOT(node);
    }

    private native void addTransformImpl(final Node p0, final int p1, final int p2, final int p3);

    public native int getBoneVertices(final Node p0, final int[] p1, final float[] p2);

    public native void getBoneTransform(final Node p0, final Transform p1);
}
