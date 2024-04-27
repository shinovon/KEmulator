package javax.microedition.m3g;

public class VertexArray
        extends Object3D {
    public VertexArray(int paramInt1, int paramInt2, int paramInt3) {
        super(2);
    }

    public void set(int paramInt1, int paramInt2, short[] paramArrayOfShort) {
    }

    public void set(int paramInt1, int paramInt2, byte[] paramArrayOfByte) {
    }

    public void get(int paramInt1, int paramInt2, byte[] paramArrayOfByte) {
    }

    public void get(int paramInt1, int paramInt2, short[] paramArrayOfShort) {
    }

    public int getComponentCount() {
        return 1;
    }

    public int getComponentType() {
        return 1;
    }

    public int getVertexCount() {
        return 1;
    }

    VertexArray(int paramInt) {
        super(paramInt);
    }

    public void morph(VertexArray[] var1, VertexArray var2, float[] var4, float var5) {
    }

    public void morphColors(VertexArray[] var1, VertexArray var2, float[] var4, float var5) {
    }

    public short[] getShortValues() {
        return null;
    }

    public byte[] getByteValues() {
        return null;
    }
}
