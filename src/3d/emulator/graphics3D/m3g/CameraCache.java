package emulator.graphics3D.m3g;

import emulator.graphics3D.Transform3D;

import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Transform;

public final class CameraCache {
    public static Camera camera;
    public static Transform m_model2camTransform = new Transform();
    private static Transform a = new Transform();

    public static void setCamera(Camera var0, Transform var1) {
        if (var1 != null) {
            a.set(var1);
            m_model2camTransform.set(var1);
            ((Transform3D) m_model2camTransform.getImpl()).method445();
        } else {
            a.setIdentity();
            m_model2camTransform.setIdentity();
        }

        camera = var0;
    }

    public static Camera getCamera(Transform var0) {
        if (var0 != null) {
            var0.set(a);
        }

        return camera;
    }
}
