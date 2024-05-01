package emulator.graphics3D.m3g;

import emulator.graphics3D.Transform3D;

import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Transform;

public final class CameraCache {
    public static Camera camera;

    private static Transform camTrans = new Transform();
    public static Transform invCam = new Transform();

    public static void setCamera(Camera cam, Transform transform) {
        if (transform != null) {
            camTrans.set(transform);
            invCam.set(transform);
            ((Transform3D) invCam.getImpl()).invert();
        } else {
            camTrans.setIdentity();
            invCam.setIdentity();
        }

        camera = cam;
    }

    public static Camera getCamera(Transform transform) {
        if (transform != null) {
            transform.set(camTrans);
        }

        return camera;
    }
}
