package javax.microedition.m3g;

public class MorphingMesh
        extends Mesh {
    public MorphingMesh(VertexBuffer paramVertexBuffer, VertexBuffer[] paramArrayOfVertexBuffer, IndexBuffer paramIndexBuffer, Appearance paramAppearance) {
        super(2);
    }

    public MorphingMesh(VertexBuffer paramVertexBuffer, VertexBuffer[] paramArrayOfVertexBuffer, IndexBuffer[] paramArrayOfIndexBuffer, Appearance[] paramArrayOfAppearance) {
        super(2);
    }

    public VertexBuffer getMorphTarget(int paramInt) {
        VertexBuffer localVertexBuffer = null;
        return localVertexBuffer;
    }

    public int getMorphTargetCount() {
        int i = 1;
        return i;
    }

    public void setWeights(float[] paramArrayOfFloat) {
    }

    public void getWeights(float[] paramArrayOfFloat) {
    }

    MorphingMesh(int paramInt) {
        super(paramInt);
    }

    public float getBaseWeight() {
        return 0;
    }
}
