package javax.microedition.m3g;

public class VertexArray extends Object3D {
    VertexArray(int n) {
        super(n);
    }

    public VertexArray(int n, int n2, int n3) {
        this(create(n, n2, n3));
        Engine.addJavaPeer(this.swerveHandle, this);
        this.ii = (getClass() != VertexArray.class);
    }

    private static native int create(int paramInt1, int paramInt2, int paramInt3);

    public native int getVertexCount();

    public native int getComponentCount();

    public native int getComponentType();

    public void get(int n, int n2, short[] array) {
        get16(n, n2, array);
    }

    private native void get16(int paramInt1, int paramInt2, short[] paramArrayOfShort);

    public void set(int n, int n2, short[] array) {
        set16(n, n2, array);
    }

    private native void set16(int paramInt1, int paramInt2, short[] paramArrayOfShort);

    public void get(int n, int n2, byte[] array) {
        get8(n, n2, array);
    }

    private native void get8(int paramInt1, int paramInt2, byte[] paramArrayOfByte);

    public void set(int n, int n2, byte[] array) {
        set8(n, n2, array);
    }

    private native void set8(int paramInt1, int paramInt2, byte[] paramArrayOfByte);
}
