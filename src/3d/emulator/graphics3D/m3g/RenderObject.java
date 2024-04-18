package emulator.graphics3D.m3g;

import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Sprite3D;
import javax.microedition.m3g.Transform;

public final class RenderObject {
    public Node m_node;
    public Transform m_transform;
    public int m_index;
    public int m_sortKey;
    public float m_alphaFactor;

    public RenderObject(Node var1, Transform var2, int var3, RenderPipe var4) {
        this.m_node = var1;
        this.m_transform = new Transform(var2);
        this.m_index = var3;
        RenderObject var10000;
        Appearance var10001;
        if (var1 instanceof Sprite3D) {
            var10000 = this;
            var10001 = ((Sprite3D) var1).getAppearance();
        } else {
            var10000 = this;
            var10001 = ((Mesh) var1).getAppearance(var3);
        }

        var10000.m_sortKey = method787(var10001);
        this.m_alphaFactor = var4.getEffectiveAlphaFactor(var1);
    }

    private static int method787(Appearance var0) {
        int var1 = var0.getLayer() << 25;
        if (var0.getCompositingMode() != null && var0.getCompositingMode().getBlending() != 68) {
            var1 += 16777216;
        }

        return var1;
    }
}
