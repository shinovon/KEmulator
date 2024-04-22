package emulator.graphics3D.m3g;

import javax.microedition.m3g.*;

public final class RenderObject {
    public Node node;
    public Transform trans;

    public int submeshIndex;
    public int sortKey;
    public float alphaFactor;

    public RenderObject(Node node, Transform trans, int submeshIndex, RenderPipe renderPipe) {
        this.node = node;
        this.trans = new Transform(trans);
        this.submeshIndex = submeshIndex;

        Appearance ap;
        if (node instanceof Sprite3D) {
            sortKey = getSortKey(((Sprite3D) node).getAppearance());
        } else {
            sortKey = getSortKey(((Mesh) node).getAppearance(submeshIndex));
        }

        alphaFactor = renderPipe.getEffectiveAlphaFactor(node);
    }

    private int getSortKey(Appearance ap) {
        int sortKey = ap.getLayer();

        if (ap.getCompositingMode() != null && ap.getCompositingMode().getBlending() != CompositingMode.REPLACE) {
            sortKey += 127;
        }

        return sortKey;
    }
}
