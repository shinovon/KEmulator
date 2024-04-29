package javax.microedition.m3g;

import java.util.Hashtable;
import java.util.Vector;

public class SkinnedMesh
        extends Mesh {
    public SkinnedMesh(VertexBuffer paramVertexBuffer, IndexBuffer[] paramArrayOfIndexBuffer, Appearance[] paramArrayOfAppearance, Group paramGroup) {
        super(2);
    }

    public SkinnedMesh(VertexBuffer paramVertexBuffer, IndexBuffer paramIndexBuffer, Appearance paramAppearance, Group paramGroup) {
        super(2);
    }

    public void addTransform(Node paramNode, int paramInt1, int paramInt2, int paramInt3) {
    }

    public Group getSkeleton() {
        return null;
    }

    SkinnedMesh(int paramInt) {
        super(paramInt);
    }

    public Vector getTransforms() {
        return null;
    }

    public int[] getVerticesBones() { return null; }

    public int[] getVerticesWeights() { return null; }
}
