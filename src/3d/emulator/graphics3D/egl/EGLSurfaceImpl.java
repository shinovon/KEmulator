package emulator.graphics3D.egl;

import javax.microedition.khronos.egl.*;
import java.util.*;
import javax.microedition.lcdui.*;

/**
 * EGLSurfaceImpl
 */
public final class EGLSurfaceImpl extends EGLSurface
{
    static final Hashtable aHashtable1335;
    private int anInt1336;
    private Graphics aGraphics1337;
    
    private EGLSurfaceImpl(final int anInt1336, final int n, final int n2) {
        super();
        synchronized (EGLSurfaceImpl.aHashtable1335) {
            this.anInt1336 = anInt1336;
            EGLSurfaceImpl.aHashtable1335.put(new Integer(anInt1336), this);
        }
    }
    
    public static EGLSurfaceImpl method781(final int n, final int n2, final int n3) {
        synchronized (EGLSurfaceImpl.aHashtable1335) {
            final EGLSurfaceImpl value;
            if ((value = (EGLSurfaceImpl) EGLSurfaceImpl.aHashtable1335.get(new Integer(n))) == null) {
                return new EGLSurfaceImpl(n, n2, n3);
            }
            return value;
        }
    }
    
    public static EGLSurfaceImpl method782(final int n) {
        return method781(n, -1, -1);
    }
    
    public final String toString() {
        return "EGLSurfaceImpl[" + this.anInt1336 + "]";
    }
    
    public final void method783(final Graphics aGraphics1337) {
        this.aGraphics1337 = aGraphics1337;
    }
    
    public final Graphics method784() {
        return this.aGraphics1337;
    }
    
    static {
        aHashtable1335 = new Hashtable();
    }
}
