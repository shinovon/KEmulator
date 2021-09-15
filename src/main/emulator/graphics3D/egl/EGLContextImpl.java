package emulator.graphics3D.egl;

import javax.microedition.khronos.egl.*;
import java.util.*;
import javax.microedition.khronos.opengles.*;

/**
 * EGLContext
 */
public final class EGLContextImpl extends EGLContext
{
    private static Hashtable aHashtable1319;
    private GL gl;
    private EGLDisplayImpl ag1321;
    private EGLSurfaceImpl ana1322;
    private EGLSurfaceImpl ana1324;
    private boolean aBoolean1323;
    
    private EGLContextImpl(final int n) {
        super();
        this.gl = null;
        this.ag1321 = null;
        this.ana1322 = null;
        this.ana1324 = null;
        this.aBoolean1323 = false;
        synchronized (EGLContextImpl.aHashtable1319) {
            EGLContextImpl.aHashtable1319.put(new Integer(n), this);
        }
    }
    
    public final EGLDisplayImpl method760() {
        return this.ag1321;
    }
    
    public final void method761(final EGLDisplayImpl ag1321) {
        this.ag1321 = ag1321;
    }
    
    public final EGLSurfaceImpl method762() {
        return this.ana1322;
    }
    
    public final void method763(final EGLSurfaceImpl ana1322) {
        this.ana1322 = ana1322;
    }
    
    public final EGLSurfaceImpl method766() {
        return this.ana1324;
    }
    
    public final void method767(final EGLSurfaceImpl ana1324) {
        this.ana1324 = ana1324;
    }
    
    public final boolean method764() {
        return this.aBoolean1323;
    }
    
    public static EGLContextImpl method765(final int n) {
        synchronized (EGLContextImpl.aHashtable1319) {
            final EGLContextImpl value;
            if ((value = (EGLContextImpl) EGLContextImpl.aHashtable1319.get(new Integer(n))) == null) {
                return new EGLContextImpl(n);
            }
            return value;
        }
    }
    
    public final GL getGL() {
        synchronized (this) {
            if (this.gl == null) {
                this.gl = new GL11Impl(this);
            }
            return this.gl;
        }
    }
    
    static {
        EGLContextImpl.aHashtable1319 = new Hashtable();
    }
}
