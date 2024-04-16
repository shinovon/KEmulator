package javax.microedition.m3g;

public class TriangleStripArray
        extends IndexBuffer {
    public TriangleStripArray(int paramInt, int[] paramArrayOfInt) {
        super(2);
    }

    public TriangleStripArray(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
        super(2);
    }

    public int getIndexCount() {
        int i = 1;
        return i;
    }

    public void getIndices(int[] paramArrayOfInt) {
    }

    TriangleStripArray(int paramInt) {
        super(paramInt);
    }

    public int getStripCount() {
        return 0;
    }

    public int[] getIndexStrip(int var11) {
        return null;
    }
}
