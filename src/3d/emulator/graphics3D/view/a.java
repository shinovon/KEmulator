package emulator.graphics3D.view;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Transform;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.internal.opengl.win32.PIXELFORMATDESCRIPTOR;
import org.eclipse.swt.internal.opengl.win32.WGL;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;

import emulator.Emulator;

public final class a
{
    static a a;
    private static long handle;
    private static long d;
    private static PIXELFORMATDESCRIPTOR pixelFormat;
    private static Camera camera;
    private static Transform trans = new Transform();
    
    private a() {
        super();
        a = this;
    }
    
    public static a acreate() {
        if (a == null) {
            a = new a();
        }
        return a;
    }
    
    public static boolean abool() {
        boolean bool = false;
        long i = OS_GetDC(handle);
        bool = WGL_wglGetCurrentContext() == i;
        OS_ReleaseDC(handle, i);
        return bool;
    }
    

    
    private static PIXELFORMATDESCRIPTOR a()
    {
      if (pixelFormat == null)
      {
        pixelFormat = new PIXELFORMATDESCRIPTOR();
        pixelFormat.nSize = 40;
        pixelFormat.nVersion = 1;
        pixelFormat.dwFlags = 37;
        pixelFormat.iPixelType = 0;
        pixelFormat.cColorBits = ((byte)Emulator.getEmulator().getScreenDepth());
        pixelFormat.iLayerType = 0;
        pixelFormat.cDepthBits = 24;
      }
      return pixelFormat;
    }
    
    public final boolean a(Canvas paramCanvas)
    {
        /*
      handle = getHandle(paramCanvas);
      long i;
      long j;
      if (((j = WGL_ChoosePixelFormat(i = OS_GetDC(handle), a())) == 0) || (!WGL_SetPixelFormat(i, j, pixelFormat)))
      {
        OS_ReleaseDC(handle, i);
        Emulator.getEmulator().getLogStream().println("SetPixelFormat() error!!");
        return false;
      }
      d = WGL_wglCreateContext(i);
      if (d == 0)
      {
        OS_ReleaseDC(handle, i);
        Emulator.getEmulator().getLogStream().println("wglCreateContext() error!!");
        return false;
      }
      OS_ReleaseDC(handle, i);
      a();
      try
      {
        GLContext.useContext(paramCanvas);
      }
      catch (Exception localException)
      {
        Emulator.getEmulator().getLogStream().println("useContext() error!!");
        return false;
      }
      GL11.glEnable(3089);
      GL11.glEnable(2977);
      GL11.glPixelStorei(3317, 1);
      GL11.glEnable(2832);
      GL11.glEnable(2848);
      GL11.glEnable(2881);
      GL11.glEnable(3024);
      return true;

         */
        return false;
    }
    
    private static long getHandle(Control c) {
    	try {
    		Class<?> cl = c.getClass();
    		Field f = cl.getField("handle");
    		return f.getLong(c);
    	} catch (Exception e) {
    		throw new Error(e);
    	}
	}
    
    private static long OS(String n, Class[] t, Object[] p, Class[] t64, Object[] p64) {
    	try {
    		Class<?> os = OS.class;
    		Method m = null;
    		try {
    			m = os.getMethod(n, t);
    			return toLong(m.invoke(null, p));
    		} catch (Exception e) {
    			m = os.getMethod(n, t64);
    			return toLong(m.invoke(null, p64));
    		}
    	} catch (Exception e) {
    		throw new Error(e);
    	}
	}
    
    private static long toLong(Object o) {
    	if(o instanceof Integer) {
    		return ((Integer) o).longValue();
    	}
    	if(o instanceof Long) {
    		return ((Long) o).longValue();
    	}
    	throw new Error("Not number: " + o.getClass());
	}

	private static Object WGL(String n, Class[] t, Object[] p, Class[] t64, Object[] p64) {
    	try {
    		Class<?> os = WGL.class;
    		Method m = null;
    		try {
    			m = os.getMethod(n, t);
    			return m.invoke(null, p);
    		} catch (Exception e) {
    			m = os.getMethod(n, t64);
    			return m.invoke(null, p64);
    		}
    	} catch (Exception e) {
    		throw new Error(e);
    	}
	}
    
    private static long OS_ReleaseDC(long h, long i) {
    	return OS("ReleaseDC",
    			new Class[] { int.class, int.class }, 
    			new Object[] { (int)h, (int)i }, 
    			new Class[] { long.class, long.class }, 
    			new Object[] { h, i } );
    }
    
    private static long OS_GetDC(long h) {
    	return OS("GetDC",
    			new Class[] { int.class}, 
    			new Object[] { (int) h}, 
    			new Class[] { long.class}, 
    			new Object[] { h } );
    }
    
    private static long WGL_wglMakeCurrent(long i, long d) {
    	return toLong(WGL("wglMakeCurrent",
    			new Class[] { int.class, int.class }, 
    			new Object[] { (int)i, (int)d }, 
    			new Class[] { long.class, long.class }, 
    			new Object[] { i, d } ));
    }
    
    private static long WGL_wglCreateContext(long i) {
    	return toLong(WGL("wglCreateContext",
    			new Class[] { int.class }, 
    			new Object[] { (int)i }, 
    			new Class[] { long.class }, 
    			new Object[] { i } ));
    }
    
    private static boolean WGL_SetPixelFormat(long i, long j, PIXELFORMATDESCRIPTOR k) {
    	return (boolean) WGL("SetPixelFormat",
    			new Class[] { int.class, int.class, PIXELFORMATDESCRIPTOR.class }, 
    			new Object[] { (int)i, (int)j, k }, 
    			new Class[] { long.class, int.class, PIXELFORMATDESCRIPTOR.class }, 
    			new Object[] { i, (int)j, k } );
    }
    
    private static long WGL_ChoosePixelFormat(long i, PIXELFORMATDESCRIPTOR k) {
    	return toLong(WGL("ChoosePixelFormat",
    			new Class[] { int.class, PIXELFORMATDESCRIPTOR.class }, 
    			new Object[] { (int)i, k }, 
    			new Class[] { long.class, PIXELFORMATDESCRIPTOR.class }, 
    			new Object[] { i, k } ));
    }
    
    private static long WGL_wglGetCurrentContext() {
    	return toLong(WGL("wglGetCurrentContext", null, null, null, null));
    }

	public void a1() {

	    if (abool()) {
	      return;
	    }
	    long i;
	    WGL_wglMakeCurrent(i = OS_GetDC(handle), d);
	    OS_ReleaseDC(handle, i);
		
	}

	
  public static void a(Camera paramCamera, Transform paramTransform)
  {
    if (paramTransform != null)
    {
      trans.set(paramTransform);
      //jdField_a_of_type_JavaxMicroeditionM3gTransform.c.c();
    }
    else
    {
      trans.setIdentity();
    }
    camera = paramCamera;
  }
}
