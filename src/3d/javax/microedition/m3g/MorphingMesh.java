package javax.microedition.m3g;

public class MorphingMesh extends Mesh
{
    static Class class$javax$microedition$m3g$MorphingMesh;
    
    MorphingMesh(final int n) {
        super(n);
    }
    
    public MorphingMesh(final VertexBuffer vertexBuffer, final VertexBuffer[] array, final IndexBuffer[] array2, final Appearance[] array3) {
        this(createMultiSubmesh(vertexBuffer, Engine.getJavaPeerArrayHandles(array), Engine.getJavaPeerArrayHandles(array2), Engine.getJavaPeerArrayHandles(array3)));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != ((MorphingMesh.class$javax$microedition$m3g$MorphingMesh == null) ? (MorphingMesh.class$javax$microedition$m3g$MorphingMesh = class$("javax.microedition.m3g.MorphingMesh")) : MorphingMesh.class$javax$microedition$m3g$MorphingMesh));
        Engine.addXOT(vertexBuffer);
        Engine.addXOT(array);
        Engine.addXOT(array2);
        Engine.addXOT(array3);
    }
    
    private static native int createMultiSubmesh(final VertexBuffer p0, final int[] p1, final int[] p2, final int[] p3);
    
    public MorphingMesh(final VertexBuffer vertexBuffer, final VertexBuffer[] array, final IndexBuffer indexBuffer, final Appearance appearance) {
        this(createSingleSubmesh(vertexBuffer, Engine.getJavaPeerArrayHandles(array), indexBuffer, appearance));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != ((MorphingMesh.class$javax$microedition$m3g$MorphingMesh == null) ? (MorphingMesh.class$javax$microedition$m3g$MorphingMesh = class$("javax.microedition.m3g.MorphingMesh")) : MorphingMesh.class$javax$microedition$m3g$MorphingMesh));
        Engine.addXOT(vertexBuffer);
        Engine.addXOT(array);
        Engine.addXOT(indexBuffer);
        Engine.addXOT(appearance);
    }
    
    private static native int createSingleSubmesh(final VertexBuffer p0, final int[] p1, final IndexBuffer p2, final Appearance p3);
    
    public native int getMorphTargetCount();
    
    public VertexBuffer getMorphTarget(final int n) {
        return (VertexBuffer)Engine.instantiateJavaPeer(this.getMorphTargetImpl(n));
    }
    
    private native int getMorphTargetImpl(final int p0);
    
    public native void getWeights(final float[] p0);
    
    public native void setWeights(final float[] p0);
    
    static Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
}
