package emulator.graphics3D.m3g;

import javax.microedition.m3g.Node;
import javax.microedition.m3g.Transform;

public final class WeightedTransform {
    public Node m_bone;
    public Transform m_toBoneTransform;
    public int m_weight;
    public int m_firstVertex;
    public int m_lastVertex;
    public Transform m_positionTransform = new Transform();
    public Transform m_normalTransform = new Transform();

    public WeightedTransform(Node var1, Transform var2, int var3, int var4, int var5) {
        this.m_bone = var1;
        this.m_weight = var3;
        this.m_toBoneTransform = new Transform(var2);
        this.m_firstVertex = var4;
        this.m_lastVertex = var5;
    }
}
