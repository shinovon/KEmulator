package emulator.custom.subclass;

import emulator.debug.Memory;

import javax.microedition.m3g.Transform;
import javax.microedition.m3g.Transformable;

public class CustomTransformable {

    public static void getTranslation(Transformable o, final float[] p0) {
        synchronized (Memory.debugLock) {
            o.getTranslation(p0);
        }
    }

    public static void getScale(Transformable o, final float[] p0) {
        synchronized (Memory.debugLock) {
            o.getScale(p0);
        }
    }

    public static void getOrientation(Transformable o, final float[] p0) {
        synchronized (Memory.debugLock) {
            o.getTranslation(p0);
        }
    }

    public static void setTranslation(Transformable o, final float p0, final float p1, final float p2) {
        synchronized (Memory.debugLock) {
            o.setTranslation(p0, p1, p2);
        }
    }

    public static void setScale(Transformable o, final float p0, final float p1, final float p2) {
        synchronized (Memory.debugLock) {
            o.setScale(p0, p1, p2);
        }
    }

    public static void setOrientation(Transformable o, final float p0, final float p1, final float p2, final float p3) {
        synchronized (Memory.debugLock) {
            o.setOrientation(p0, p1, p2, p3);
        }
    }

    public static void translate(Transformable o, final float p0, final float p1, final float p2) {
        synchronized (Memory.debugLock) {
            o.translate(p0, p1, p2);
        }
    }

    public static void scale(Transformable o, final float p0, final float p1, final float p2) {
        synchronized (Memory.debugLock) {
            o.scale(p0, p1, p2);
        }
    }

    public static void preRotate(Transformable o, final float p0, final float p1, final float p2, final float p3) {
        synchronized (Memory.debugLock) {
            o.preRotate(p0, p1, p2, p3);
        }
    }

    public static void postRotate(Transformable o, final float p0, final float p1, final float p2, final float p3) {
        synchronized (Memory.debugLock) {
            o.postRotate(p0, p1, p2, p3);
        }
    }

    public static void getTransform(Transformable o, final Transform p0) {
        synchronized (Memory.debugLock) {
            o.getTransform(p0);
        }
    }

    public static void getCompositeTransform(Transformable o, final Transform p0) {
        synchronized (Memory.debugLock) {
            o.getCompositeTransform(p0);
        }
    }

    public static void setTransform(Transformable o, final Transform p0) {
        synchronized (Memory.debugLock) {
            o.setTransform(p0);
        }
    }

}
