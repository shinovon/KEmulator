package javax.microedition.m3g;

import java.nio.IntBuffer;

public class TriangleStripArray
        extends IndexBuffer {
    public TriangleStripArray(int paramInt, int[] paramArrayOfInt) {
        super(2);
    }

    public TriangleStripArray(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
        super(2);
    }

    public int getIndexCount() {
        return 1;
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

    public IntBuffer getBuffer() {
        return null;
    }

    public int profilerCount() {
        return 0;
    }
}
