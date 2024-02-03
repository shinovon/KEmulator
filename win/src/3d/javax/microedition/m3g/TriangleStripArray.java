package javax.microedition.m3g;

public class TriangleStripArray extends IndexBuffer
{
    
    TriangleStripArray(final int n) {
        super(n);
    }
    
    public TriangleStripArray(final int n, final int[] array) {
        this(createImplicit(n, array));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != TriangleStripArray.class);
    }
    
    private static native int createImplicit(final int p0, final int[] p1);
    
    public TriangleStripArray(final int[] array, final int[] array2) {
        this(createExplicit(array, array2));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != TriangleStripArray.class);
    }
    
    private static native int createExplicit(final int[] p0, final int[] p1);
    
    public int getIndexCount() {
        return this.getIndexCountImpl();
    }
    
    public void getIndices(final int[] array) {
        this.getIndicesImpl(array);
    }
}
