package javax.microedition.m3g;

public class Mesh extends Node
{
    
    Mesh(final int n) {
        super(n);
    }
    
    public Mesh(final VertexBuffer vertexBuffer, final IndexBuffer[] array, final Appearance[] array2) {
        this(createMultiSubmesh(vertexBuffer, Engine.getJavaPeerArrayHandles(array), Engine.getJavaPeerArrayHandles(array2)));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != Mesh.class);
        Engine.addXOT(vertexBuffer);
        Engine.addXOT(array);
        Engine.addXOT(array2);
    }
    
    private static native int createMultiSubmesh(final VertexBuffer p0, final int[] p1, final int[] p2);
    
    public Mesh(final VertexBuffer vertexBuffer, final IndexBuffer indexBuffer, final Appearance appearance) {
        this(createSingleSubmesh(vertexBuffer, indexBuffer, appearance));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != Mesh.class);
        Engine.addXOT(vertexBuffer);
        Engine.addXOT(indexBuffer);
        Engine.addXOT(appearance);
    }
    
    private static native int createSingleSubmesh(final VertexBuffer p0, final IndexBuffer p1, final Appearance p2);
    
    public native int getSubmeshCount();
    
    public VertexBuffer getVertexBuffer() {
        return (VertexBuffer)Engine.instantiateJavaPeer(this.getVertexBufferImpl());
    }
    
    private native int getVertexBufferImpl();
    
    public IndexBuffer getIndexBuffer(final int n) {
        return (IndexBuffer)Engine.instantiateJavaPeer(this.getIndexBufferImpl(n));
    }
    
    private native int getIndexBufferImpl(final int p0);
    
    public Appearance getAppearance(final int n) {
        return (Appearance)Engine.instantiateJavaPeer(this.getAppearanceImpl(n));
    }
    
    private native int getAppearanceImpl(final int p0);
    
    public void setAppearance(final int n, final Appearance appearance) {
        this.setAppearanceImpl(n, appearance);
        Engine.addXOT(appearance);
    }
    
    private native void setAppearanceImpl(final int p0, final Appearance p1);
}
