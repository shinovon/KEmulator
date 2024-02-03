package emulator.graphics3D.egl;

import javax.microedition.khronos.egl.*;
import java.util.*;

/**
 * EGLDisplayImpl
 */
public final class EGLDisplayImpl extends EGLDisplay
{
    private static Hashtable aHashtable1352;
    int anInt1353;
    
    private EGLDisplayImpl(final int anInt1353) {
        super();
        synchronized (EGLDisplayImpl.aHashtable1352) {
            this.anInt1353 = anInt1353;
            EGLDisplayImpl.aHashtable1352.put(new Integer(anInt1353), this);
        }
    }
    
    public static EGLDisplayImpl method803(final int n) {
        synchronized (EGLDisplayImpl.aHashtable1352) {
            final EGLDisplayImpl value;
            if ((value = (EGLDisplayImpl) EGLDisplayImpl.aHashtable1352.get(new Integer(n))) == null) {
                return new EGLDisplayImpl(n);
            }
            return value;
        }
    }
    
    public final String toString() {
        return "EGLDisplayImpl[" + this.anInt1353 + "]";
    }
    
    static {
        EGLDisplayImpl.aHashtable1352 = new Hashtable();
    }
}
