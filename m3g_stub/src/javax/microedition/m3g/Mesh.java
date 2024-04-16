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
        Appearance localAppearance = null;
        return localAppearance;
    }

    public IndexBuffer getIndexBuffer(int paramInt) {
        IndexBuffer localIndexBuffer = null;
        return localIndexBuffer;
    }

    public VertexBuffer getVertexBuffer() {
        VertexBuffer localVertexBuffer = null;
        return localVertexBuffer;
    }

    public int getSubmeshCount() {
        int i = 1;
        return i;
    }

    Mesh(int paramInt) {
        super(paramInt);
    }
}
