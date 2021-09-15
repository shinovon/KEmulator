package javax.microedition.m3g;

public class TriangleStripArray extends IndexBuffer
{
    static Class class$javax$microedition$m3g$TriangleStripArray;
    
    TriangleStripArray(final int n) {
        super(n);
    }
    
    public TriangleStripArray(final int n, final int[] array) {
        this(createImplicit(n, array));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != ((TriangleStripArray.class$javax$microedition$m3g$TriangleStripArray == null) ? (TriangleStripArray.class$javax$microedition$m3g$TriangleStripArray = class$("javax.microedition.m3g.TriangleStripArray")) : TriangleStripArray.class$javax$microedition$m3g$TriangleStripArray));
    }
    
    private static native int createImplicit(final int p0, final int[] p1);
    
    public TriangleStripArray(final int[] array, final int[] array2) {
        this(createExplicit(array, array2));
        Engine.addJavaPeer(super.swerveHandle, this);
        super.ii = (this.getClass() != ((TriangleStripArray.class$javax$microedition$m3g$TriangleStripArray == null) ? (TriangleStripArray.class$javax$microedition$m3g$TriangleStripArray = class$("javax.microedition.m3g.TriangleStripArray")) : TriangleStripArray.class$javax$microedition$m3g$TriangleStripArray));
    }
    
    private static native int createExplicit(final int[] p0, final int[] p1);
    
    public int getIndexCount() {
        return this.getIndexCountImpl();
    }
    
    public void getIndices(final int[] array) {
        this.getIndicesImpl(array);
    }
    
    static Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
}
