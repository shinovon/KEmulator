package javax.microedition.m3g;

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
        Group localGroup = null;
        return localGroup;
    }

    public void getBoneTransform(Node paramNode, Transform paramTransform) {
    }

    public int getBoneVertices(Node paramNode, int[] paramArrayOfInt, float[] paramArrayOfFloat) {
        int i = 1;
        return i;
    }

    SkinnedMesh(int paramInt) {
        super(paramInt);
    }

    public Vector getTransforms() {
        return null;
    }
}
