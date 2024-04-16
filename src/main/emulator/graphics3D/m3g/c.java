package emulator.graphics3D.m3g;

import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Sprite3D;
import javax.microedition.m3g.Transform;

public final class c {
    public Node aNode1140;
    public Transform aTransform1141;
    public int anInt1142;
    public int anInt1144;
    public float aFloat1143;

    public c(Node var1, Transform var2, int var3, d var4) {
        this.aNode1140 = var1;
        this.aTransform1141 = new Transform(var2);
        this.anInt1142 = var3;
        c var10000;
        Appearance var10001;
        if (var1 instanceof Sprite3D) {
            var10000 = this;
            var10001 = ((Sprite3D) var1).getAppearance();
        } else {
            var10000 = this;
            var10001 = ((Mesh) var1).getAppearance(var3);
        }

        var10000.anInt1144 = method787(var10001);
        this.aFloat1143 = var4.method794(var1);
    }

    private static int method787(Appearance var0) {
        int var1 = var0.getLayer() << 25;
        if (var0.getCompositingMode() != null && var0.getCompositingMode().getBlending() != 68) {
            var1 += 16777216;
        }

        return var1;
    }
}
