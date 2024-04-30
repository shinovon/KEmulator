package javax.microedition.m3g;

public class Mesh
        extends Node {
    public Mesh(VertexBuffer paramVertexBuffer, IndexBuffer[] paramArrayOfIndexBuffer, Appearance[] paramArrayOfAppearance) {
        super(2);
    }

    public Mesh(VertexBuffer paramVertexBuffer, IndexBuffer paramIndexBuffer, Appearance paramAppearance) {
        super(2);
    }

    public void setAppearance(int paramInt, Appearance paramAppearance) {
    }

    public Appearance getAppearance(int paramInt) {
        return null;
    }

    public IndexBuffer getIndexBuffer(int paramInt) {
        return null;
    }

    public VertexBuffer getVertexBuffer() {
        return null;
    }

    public int getSubmeshCount() {
        return 1;
    }

    Mesh(int paramInt) {
        super(paramInt);
    }
}
