package emulator.graphics3D.view;

import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Transform;

import org.eclipse.swt.internal.opengl.win32.PIXELFORMATDESCRIPTOR;
import org.eclipse.swt.internal.opengl.win32.WGL;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Canvas;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import emulator.Emulator;

public final class a
{
    static a a;
    private static int handle;
    private static int d;
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
        int i = OS.GetDC(handle);
        bool = WGL.wglGetCurrentContext() == i;
        OS.ReleaseDC(handle, i);
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
      handle = paramCanvas.handle;
      int i;
      int j;
      if (((j = WGL.ChoosePixelFormat(i = OS.GetDC(handle), a())) == 0) || (!WGL.SetPixelFormat(i, j, pixelFormat)))
      {
        OS.ReleaseDC(handle, i);
        Emulator.getEmulator().getLogStream().println("SetPixelFormat() error!!");
        return false;
      }
      d = WGL.wglCreateContext(i);
      if (d == 0)
      {
        OS.ReleaseDC(handle, i);
        Emulator.getEmulator().getLogStream().println("wglCreateContext() error!!");
        return false;
      }
      OS.ReleaseDC(handle, i);
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
    }

	public void a1() {

	    if (abool()) {
	      return;
	    }
	    int i;
	    WGL.wglMakeCurrent(i = OS.GetDC(handle), d);
	    OS.ReleaseDC(handle, i);
		
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
