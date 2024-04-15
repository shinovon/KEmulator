package emulator.custom.subclass;

import emulator.debug.Memory;

import javax.microedition.m3g.Transform;
import javax.microedition.m3g.VertexArray;

public class CustomTransform {
    
    public static void set(Transform o, float[] matrix) {
        synchronized(Memory.debugLock) {
            o.set(matrix);
        }
    }

    public static void set(Transform o, Transform transform) {
        synchronized(Memory.debugLock) {
            o.set(transform);
        }
    }

    public static void transform(Transform o, float[] array) {
        synchronized(Memory.debugLock) {
            o.transform(array);
        }
    }

    public static void get(Transform o, float[] paramArrayOfFloat) {
        synchronized(Memory.debugLock) {
            o.get(paramArrayOfFloat);
        }
    }

    public static void setIdentity(Transform o) {
        synchronized(Memory.debugLock) {
            o.setIdentity();
        }
    }

    public static void invert(Transform o) {
        synchronized(Memory.debugLock) {
            o.invert();
        }
    }

    public static void postMultiply(Transform o, Transform paramTransform) {
        synchronized(Memory.debugLock) {
            o.postMultiply(paramTransform);
        }
    }

    public static void postRotate(Transform o, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
        synchronized(Memory.debugLock) {
            o.postRotate(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
        }
    }

    public static void postRotateQuat(Transform o, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
        synchronized(Memory.debugLock) {
            o.postRotateQuat(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
        }
    }

    public static void postScale(Transform o, float paramFloat1, float paramFloat2, float paramFloat3) {
        synchronized(Memory.debugLock) {
            o.postScale(paramFloat1, paramFloat2, paramFloat3);
        }
    }

    public static void postTranslate(Transform o, float paramFloat1, float paramFloat2, float paramFloat3) {
        synchronized(Memory.debugLock) {
            o.postTranslate(paramFloat1, paramFloat2, paramFloat3);
        }
    }

    public static void transform(Transform o, VertexArray paramVertexArray, float[] paramArrayOfFloat, boolean paramBoolean) {
        synchronized(Memory.debugLock) {
            o.transform(paramVertexArray, paramArrayOfFloat, paramBoolean);
        }
    }

    public static void transpose(Transform o) {
        synchronized(Memory.debugLock) {
            o.transpose();
        }
    }
}
