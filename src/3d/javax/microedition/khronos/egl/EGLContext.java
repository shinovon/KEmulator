package javax.microedition.khronos.egl;

import emulator.graphics3D.egl.*;
import javax.microedition.khronos.opengles.*;

public abstract class EGLContext
{
    public EGLContext() {
        super();
    }
    
    public static synchronized EGL getEGL() {
        return EGL10Impl.getEgl();
    }
    
    public abstract GL getGL();
}
