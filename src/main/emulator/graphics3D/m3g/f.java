package emulator.graphics3D.m3g;

import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Transform;

public final class f {
    public static Camera aCamera1137;
    public static Transform aTransform1138 = new Transform();
    private static Transform aTransform1139 = new Transform();

    public static void method785(Camera var0, Transform var1) {
        if (var1 != null) {
            aTransform1139.set(var1);
            aTransform1138.set(var1);
            aTransform1138.getImpl().method445();
        } else {
            aTransform1139.setIdentity();
            aTransform1138.setIdentity();
        }

        aCamera1137 = var0;
    }

    public static Camera method786(Transform var0) {
        if (var0 != null) {
            var0.set(aTransform1139);
        }

        return aCamera1137;
    }
}
