package javax.microedition.m3g;

import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;

import emulator.i;

public class Graphics3D {
   int swerveHandle;
   public static final int ANTIALIAS = 2;
   public static final int DITHER = 4;
   public static final int TRUE_COLOR = 8;
   public static final int OVERWRITE = 16;
   static final int SUPPORTANTIALIASING = 0;
   static final int SUPPORTTRUECOLOR = 1;
   static final int SUPPORTDITHERING = 2;
   static final int SUPPORTMIPMAP = 3;
   static final int SUPPORTPERSPECTIVECORRECTION = 4;
   static final int SUPPORTLOCALCAMERALIGHTING = 5;
   static final int MAXLIGHTS = 6;
   static final int MAXVIEWPORTDIMENSION = 7;
   static final int MAXTEXTUREDIMENSION = 8;
   static final int MAXSPRITECROPDIMENSION = 9;
   static final int NUMTEXTUREUNITS = 10;
   static final int MAXTRANSFORMSPERVERTEX = 11;
   static final int MAXVIEWPORTWIDTH = 12;
   static final int MAXVIEWPORTHEIGHT = 13;
   static final int SUPPORTROTATE90 = 14;
   static final int SUPPORTROTATE180 = 15;
   static final int SUPPORTROTATE270 = 16;
   private static Graphics3D instance;
   Object bbPixels = null;
   private Object boundTarget = null;
   private boolean isGraphics = true;
   private boolean preload = false;
   private boolean unload = false;
   private boolean overwrite = false;
   private int clipX = 0;
   private int clipY = 0;
   private int clipWidth = 0;
   private int clipHeight = 0;
   private int viewportX = 0;
   private int viewportY = 0;
   private int viewportWidth = 0;
   private int viewportHeight = 0;
   private static int maxViewportWidth;
   private static int maxViewportHeight;
   static Class class$javax$microedition$m3g$Graphics3D;

   protected native void finalize();

   Graphics3D() {
   }

   Graphics3D(int var1) {
      this.swerveHandle = var1;
   }

   public native float getDepthRangeNear();

   public native float getDepthRangeFar();

   public native boolean isDepthBufferEnabled();

   public native int getHints();

   public native int getLightCount();

   private static native int createImpl();

   public static final Graphics3D getInstance() {
      if(instance == null) {
         instance = (Graphics3D)Engine.instantiateJavaPeer(createImpl());
      }

      return instance;
   }

   private native void setBackBufferImage2D(Image2D var1);

   private native void setHints(boolean var1, int var2);

   public Object getTarget() {
      return this.boundTarget;
   }

   public synchronized void bindTarget(Object var1, boolean var2, int var3) {
      if(this.boundTarget != null) {
         throw new IllegalStateException();
      } else if(var1 == null) {
         throw new NullPointerException();
      } else if((var3 & -31) != 0) {
         throw new IllegalArgumentException();
      } else {
         if(var1 instanceof Graphics) {
            this.isGraphics = true;
            this.overwrite = (var3 & 16) != 0;
            this.unload = false;
            this.bindTarget((Graphics)var1);
         } else {
            if(!(var1 instanceof Image2D)) {
               throw new IllegalArgumentException();
            }

            this.isGraphics = false;
            this.setBackBufferImage2D((Image2D)var1);
            this.boundTarget = var1;
         }

         this.setHints(var2, var3);
      }
   }

   public synchronized void bindTarget(Object var1) {
      if(this.boundTarget != null) {
         throw new IllegalStateException();
      } else if(var1 == null) {
         throw new NullPointerException();
      } else if(var1 instanceof Graphics) {
         this.isGraphics = true;
         this.overwrite = false;
         this.unload = false;
         this.bindTarget((Graphics)var1);
      } else if(var1 instanceof Image2D) {
         this.isGraphics = false;
         this.setBackBufferImage2D((Image2D)var1);
         this.boundTarget = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void bindTarget(Graphics var1) {
      this.clipX = Helpers.getTranslateX(var1) + Helpers.getClipX(var1);
      this.clipY = Helpers.getTranslateY(var1) + Helpers.getClipY(var1);
      this.clipWidth = Helpers.getClipWidth(var1);
      this.clipHeight = Helpers.getClipHeight(var1);
      if(this.clipWidth <= maxViewportWidth && this.clipHeight <= maxViewportHeight) {
         this.boundTarget = var1;
         Helpers.bindTarget(this, var1, this.clipX, this.clipY, this.clipWidth, this.clipHeight);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public synchronized void releaseTarget() {
      if(this.boundTarget != null) {
         this.preload = false;
         if(this.isGraphics) {
            Helpers.releaseTarget(this, (Graphics)this.boundTarget);
         } else {
            this.setBackBufferImage2D((Image2D)null);
         }

         this.boundTarget = null;
      }
   }

   public synchronized void setViewport(int var1, int var2, int var3, int var4) {
      if(var3 > 0 && var3 <= maxViewportWidth && var4 > 0 && var4 <= maxViewportHeight) {
         this.viewportX = var1;
         this.viewportY = var2;
         this.viewportWidth = var3;
         this.viewportHeight = var4;
         if(this.boundTarget != null) {
            if(this.boundTarget instanceof Graphics) {
               Graphics var5 = (Graphics)this.boundTarget;
               var1 += Helpers.getTranslateX(var5);
               var2 += Helpers.getTranslateY(var5);
               this.preload = !this.unload && (!this.overwrite || var1 > this.clipX || var1 + var3 < this.clipX + this.clipWidth || var2 > this.clipY || var2 + var4 < this.clipY + this.clipHeight);
               this.setViewportImpl(var1, var2, var3, var4);
               this.unload = this.unload || this.preload;
               return;
            }

            this.setViewportImpl(var1, var2, var3, var4);
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   private native void setViewportImpl(int var1, int var2, int var3, int var4);

   public int getViewportX() {
      return this.viewportX;
   }

   public int getViewportY() {
      return this.viewportY;
   }

   public int getViewportWidth() {
      return this.viewportWidth;
   }

   public int getViewportHeight() {
      return this.viewportHeight;
   }

   public native void setDepthRange(float var1, float var2);

   private native void clearImpl(Background var1);

   public synchronized void clear(Background var1) {
      if(this.boundTarget == null) {
         throw new IllegalStateException();
      } else {
         this.preload = !this.unload && !this.overwrite && var1 != null && !var1.isColorClearEnabled();
         this.clearImpl(var1);
         this.unload = true;
      }
   }

   public void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4) {
      this.render(var1, var2, var3, var4, -1);
   }

   private native void renderPrimitive(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5);

   public synchronized void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5) {
      if(this.boundTarget == null) {
         throw new IllegalStateException();
      } else {
         this.preload = !this.unload && !this.overwrite;
         this.renderPrimitive(var1, var2, var3, var4, var5);
         this.unload = true;
      }
   }

   private native void renderNode(Node var1, Transform var2);

   public synchronized void render(Node var1, Transform var2) {
      if(this.boundTarget == null) {
         throw new IllegalStateException();
      } else {
         this.preload = !this.unload && !this.overwrite;
         this.renderNode(var1, var2);
         this.unload = true;
      }
   }

   private native void renderWorld(World var1);

   public synchronized void render(World var1) {
      if(this.boundTarget == null) {
         throw new IllegalStateException();
      } else {
         Background var2 = var1.getBackground();
         this.preload = !this.unload && !this.overwrite && var2 != null && !var2.isColorClearEnabled();
         this.renderWorld(var1);
         this.unload = true;
      }
   }

   public Camera getCamera(Transform var1) {
      return (Camera)Engine.instantiateJavaPeer(this.getCameraImpl(var1));
   }

   private native int getCameraImpl(Transform var1);

   public void setCamera(Camera var1, Transform var2) {
      this.setCameraImpl(var1, var2);
      Engine.addXOT(var1);
   }

   private native void setCameraImpl(Camera var1, Transform var2);

   public void setLight(int var1, Light var2, Transform var3) {
      this.setLightImpl(var1, var2, var3);
      Engine.addXOT(var2);
   }

   private native void setLightImpl(int var1, Light var2, Transform var3);

   public Light getLight(int var1, Transform var2) {
      return (Light)Engine.instantiateJavaPeer(this.getLightImpl(var1, var2));
   }

   private native int getLightImpl(int var1, Transform var2);

   public int addLight(Light var1, Transform var2) {
      int var3 = this.addLightImpl(var1, var2);
      Engine.addXOT(var1);
      return var3;
   }

   private native int addLightImpl(Light var1, Transform var2);

   public native void resetLights();

   public static final synchronized Hashtable getProperties() {
      Hashtable var0;
      (var0 = new Hashtable()).put(new String("supportAntialiasing"), new Boolean(getCapability(0) == 1));
      var0.put(new String("supportTrueColor"), new Boolean(getCapability(1) == 1));
      var0.put(new String("supportDithering"), new Boolean(getCapability(2) == 1));
      var0.put(new String("supportMipmapping"), new Boolean(getCapability(3) == 1));
      var0.put(new String("supportPerspectiveCorrection"), new Boolean(getCapability(4) == 1));
      var0.put(new String("supportLocalCameraLighting"), new Boolean(getCapability(5) == 1));
      var0.put(new String("maxLights"), new Integer(getCapability(6)));
      var0.put(new String("maxViewportDimension"), new Integer(getCapability(7)));
      var0.put(new String("maxTextureDimension"), new Integer(getCapability(8)));
      var0.put(new String("maxSpriteCropDimension"), new Integer(getCapability(9)));
      var0.put(new String("numTextureUnits"), new Integer(getCapability(10)));
      var0.put(new String("maxTransformsPerVertex"), new Integer(getCapability(11)));
      var0.put(new String("maxViewportWidth"), new Integer(maxViewportWidth));
      var0.put(new String("maxViewportHeight"), new Integer(maxViewportHeight));
      var0.put(new String("C3A458D3-2015-41f5-8338-66A2D3014335"), Engine.getVersionMajor() + "." + Engine.getVersionMinor() + "." + Engine.getRevisionMajor() + "." + Engine.getRevisionMinor() + ":" + Engine.getBranchNumber());

      try {
         var0.put(new String("com.superscape.m3gx.DebugUtils"), Class.forName("javax.microedition.m3g.DebugUtils").newInstance());
      } catch (Exception var3) {
         ;
      }

      try {
         var0.put(new String("com.superscape.m3gx.Image2DUtils"), Class.forName("javax.microedition.m3g.Image2DUtils").newInstance());
      } catch (Exception var2) {
         ;
      }

      try {
         var0.put(new String("com.superscape.m3gx.SerializeOut"), Class.forName("javax.microedition.m3g.SerializeOut").newInstance());
      } catch (Exception var1) {
         ;
      }

      return var0;
   }

   private static native int getCapability(int var0);

   static Class class$(String var0) {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   static {
      i.a("jsr184client");
      Engine.cacheFID(class$javax$microedition$m3g$Graphics3D == null?(class$javax$microedition$m3g$Graphics3D = class$("javax.microedition.m3g.Graphics3D")):class$javax$microedition$m3g$Graphics3D, 1);
      instance = null;
      maxViewportWidth = getCapability(12);
      maxViewportHeight = getCapability(13);
   }
}

/*
import javax.microedition.lcdui.*;
import java.util.*;
import emulator.*;

public class Graphics3D
{
    int swerveHandle;
    public static final int ANTIALIAS = 2;
    public static final int DITHER = 4;
    public static final int TRUE_COLOR = 8;
    public static final int OVERWRITE = 16;
    static final int SUPPORTANTIALIASING = 0;
    static final int SUPPORTTRUECOLOR = 1;
    static final int SUPPORTDITHERING = 2;
    static final int SUPPORTMIPMAP = 3;
    static final int SUPPORTPERSPECTIVECORRECTION = 4;
    static final int SUPPORTLOCALCAMERALIGHTING = 5;
    static final int MAXLIGHTS = 6;
    static final int MAXVIEWPORTDIMENSION = 7;
    static final int MAXTEXTUREDIMENSION = 8;
    static final int MAXSPRITECROPDIMENSION = 9;
    static final int NUMTEXTUREUNITS = 10;
    static final int MAXTRANSFORMSPERVERTEX = 11;
    static final int MAXVIEWPORTWIDTH = 12;
    static final int MAXVIEWPORTHEIGHT = 13;
    static final int SUPPORTROTATE90 = 14;
    static final int SUPPORTROTATE180 = 15;
    static final int SUPPORTROTATE270 = 16;
    private static Graphics3D instance;
    Object bbPixels;
    private Object boundTarget;
    private boolean isGraphics;
    private boolean preload;
    private boolean unload;
    private boolean overwrite;
    private int clipX;
    private int clipY;
    private int clipWidth;
    private int clipHeight;
    private int viewportX;
    private int viewportY;
    private int viewportWidth;
    private int viewportHeight;
    private static int maxViewportWidth;
    private static int maxViewportHeight;
    static Class class$javax$microedition$m3g$Graphics3D;
    
    protected native void finalize();
    
    Graphics3D() {
        super();
        this.bbPixels = null;
        this.boundTarget = null;
        this.isGraphics = true;
        this.preload = false;
        this.unload = false;
        this.overwrite = false;
        this.clipX = 0;
        this.clipY = 0;
        this.clipWidth = 0;
        this.clipHeight = 0;
        this.viewportX = 0;
        this.viewportY = 0;
        this.viewportWidth = 0;
        this.viewportHeight = 0;
    }
    
    Graphics3D(final int swerveHandle) {
        super();
        this.bbPixels = null;
        this.boundTarget = null;
        this.isGraphics = true;
        this.preload = false;
        this.unload = false;
        this.overwrite = false;
        this.clipX = 0;
        this.clipY = 0;
        this.clipWidth = 0;
        this.clipHeight = 0;
        this.viewportX = 0;
        this.viewportY = 0;
        this.viewportWidth = 0;
        this.viewportHeight = 0;
        this.swerveHandle = swerveHandle;
    }
    
    public native float getDepthRangeNear();
    
    public native float getDepthRangeFar();
    
    public native boolean isDepthBufferEnabled();
    
    public native int getHints();
    
    public native int getLightCount();
    
    private static native int createImpl();
    
    public static final Graphics3D getInstance() {
        if (Graphics3D.instance == null) {
            Graphics3D.instance = (Graphics3D)Engine.instantiateJavaPeer(createImpl());
        }
        return Graphics3D.instance;
    }
    
    private native void setBackBufferImage2D(final Image2D p0);
    
    private native void setHints(final boolean p0, final int p1);
    
    public Object getTarget() {
        return this.boundTarget;
    }
    
    public synchronized void bindTarget(final Object boundTarget, final boolean b, final int n) {
        if (this.boundTarget != null) {
            throw new IllegalStateException();
        }
        if (boundTarget == null) {
            throw new NullPointerException();
        }
        if ((n & 0xFFFFFFE1) != 0x0) {
            throw new IllegalArgumentException();
        }
        if (boundTarget instanceof Graphics) {
            this.isGraphics = true;
            this.overwrite = ((n & 0x10) != 0x0);
            this.unload = false;
            this.bindTarget((Graphics)boundTarget);
        }
        else {
            if (!(boundTarget instanceof Image2D)) {
                throw new IllegalArgumentException();
            }
            this.isGraphics = false;
            this.setBackBufferImage2D((Image2D)boundTarget);
            this.boundTarget = boundTarget;
        }
        this.setHints(b, n);
    }
    
    public synchronized void bindTarget(final Object boundTarget) {
        if (this.boundTarget != null) {
            throw new IllegalStateException();
        }
        if (boundTarget == null) {
            throw new NullPointerException();
        }
        if (boundTarget instanceof Graphics) {
            this.isGraphics = true;
            this.overwrite = false;
            this.unload = false;
            this.bindTarget((Graphics)boundTarget);
            return;
        }
        if (boundTarget instanceof Image2D) {
            this.isGraphics = false;
            this.setBackBufferImage2D((Image2D)boundTarget);
            this.boundTarget = boundTarget;
            return;
        }
        throw new IllegalArgumentException();
    }
    
    private void bindTarget(final Graphics boundTarget) {
        this.clipX = Helpers.getTranslateX(boundTarget) + Helpers.getClipX(boundTarget);
        this.clipY = Helpers.getTranslateY(boundTarget) + Helpers.getClipY(boundTarget);
        this.clipWidth = Helpers.getClipWidth(boundTarget);
        this.clipHeight = Helpers.getClipHeight(boundTarget);
        if (this.clipWidth > Graphics3D.maxViewportWidth || this.clipHeight > Graphics3D.maxViewportHeight) {
            throw new IllegalArgumentException();
        }
        Helpers.bindTarget(this, (Graphics)(this.boundTarget = boundTarget), this.clipX, this.clipY, this.clipWidth, this.clipHeight);
    }
    
    public synchronized void releaseTarget() {
        if (this.boundTarget == null) {
            return;
        }
        this.preload = false;
        if (this.isGraphics) {
            Helpers.releaseTarget(this, (Graphics)this.boundTarget);
        }
        else {
            this.setBackBufferImage2D(null);
        }
        this.boundTarget = null;
    }
    
    public synchronized void setViewport(int viewportX, int viewportY, final int viewportWidth, final int viewportHeight) {
        if (viewportWidth <= 0 || viewportWidth > Graphics3D.maxViewportWidth || viewportHeight <= 0 || viewportHeight > Graphics3D.maxViewportHeight) {
            throw new IllegalArgumentException();
        }
        this.viewportX = viewportX;
        this.viewportY = viewportY;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        if (this.boundTarget != null) {
            if (this.boundTarget instanceof Graphics) {
                final Graphics graphics = (Graphics)this.boundTarget;
                viewportX += Helpers.getTranslateX(graphics);
                viewportY += Helpers.getTranslateY(graphics);
                this.preload = (!this.unload && (!this.overwrite || viewportX > this.clipX || viewportX + viewportWidth < this.clipX + this.clipWidth || viewportY > this.clipY || viewportY + viewportHeight < this.clipY + this.clipHeight));
                this.setViewportImpl(viewportX, viewportY, viewportWidth, viewportHeight);
                this.unload = (this.unload || this.preload);
                return;
            }
            this.setViewportImpl(viewportX, viewportY, viewportWidth, viewportHeight);
        }
    }
    
    private native void setViewportImpl(final int p0, final int p1, final int p2, final int p3);
    
    public int getViewportX() {
        return this.viewportX;
    }
    
    public int getViewportY() {
        return this.viewportY;
    }
    
    public int getViewportWidth() {
        return this.viewportWidth;
    }
    
    public int getViewportHeight() {
        return this.viewportHeight;
    }
    
    public native void setDepthRange(final float p0, final float p1);
    
    private native void clearImpl(final Background p0);
    
    public synchronized void clear(final Background background) {
        if (this.boundTarget == null) {
            throw new IllegalStateException();
        }
        this.preload = (!this.unload && !this.overwrite && background != null && !background.isColorClearEnabled());
        this.clearImpl(background);
        this.unload = true;
    }
    
    public void render(final VertexBuffer vertexBuffer, final IndexBuffer indexBuffer, final Appearance appearance, final Transform transform) {
        this.render(vertexBuffer, indexBuffer, appearance, transform, -1);
    }
    
    private native void renderPrimitive(final VertexBuffer p0, final IndexBuffer p1, final Appearance p2, final Transform p3, final int p4);
    
    public synchronized void render(final VertexBuffer vertexBuffer, final IndexBuffer indexBuffer, final Appearance appearance, final Transform transform, final int n) {
        if (this.boundTarget == null) {
            throw new IllegalStateException();
        }
        this.preload = (!this.unload && !this.overwrite);
        this.renderPrimitive(vertexBuffer, indexBuffer, appearance, transform, n);
        this.unload = true;
    }
    
    private native void renderNode(final Node p0, final Transform p1);
    
    public synchronized void render(final Node node, final Transform transform) {
        if (this.boundTarget == null) {
            throw new IllegalStateException();
        }
        this.preload = (!this.unload && !this.overwrite);
        this.renderNode(node, transform);
        this.unload = true;
    }
    
    private native void renderWorld(final World p0);
    
    public synchronized void render(final World world) {
        if (this.boundTarget == null) {
            throw new IllegalStateException();
        }
        final Background background = world.getBackground();
        this.preload = (!this.unload && !this.overwrite && background != null && !background.isColorClearEnabled());
        this.renderWorld(world);
        this.unload = true;
    }
    
    public Camera getCamera(final Transform transform) {
        return (Camera)Engine.instantiateJavaPeer(this.getCameraImpl(transform));
    }
    
    private native int getCameraImpl(final Transform p0);
    
    public void setCamera(final Camera camera, final Transform transform) {
        this.setCameraImpl(camera, transform);
        Engine.addXOT(camera);
    }
    
    private native void setCameraImpl(final Camera p0, final Transform p1);
    
    public void setLight(final int n, final Light light, final Transform transform) {
        this.setLightImpl(n, light, transform);
        Engine.addXOT(light);
    }
    
    private native void setLightImpl(final int p0, final Light p1, final Transform p2);
    
    public Light getLight(final int n, final Transform transform) {
        return (Light)Engine.instantiateJavaPeer(this.getLightImpl(n, transform));
    }
    
    private native int getLightImpl(final int p0, final Transform p1);
    
    public int addLight(final Light light, final Transform transform) {
        final int addLightImpl = this.addLightImpl(light, transform);
        Engine.addXOT(light);
        return addLightImpl;
    }
    
    private native int addLightImpl(final Light p0, final Transform p1);
    
    public native void resetLights();
    
    public static final synchronized Hashtable getProperties()
    {
      Hashtable localHashtable;
      (localHashtable = new Hashtable()).put(new String("supportAntialiasing"), new Boolean(getCapability(0) == 1));
      localHashtable.put(new String("supportTrueColor"), new Boolean(getCapability(1) == 1));
      localHashtable.put(new String("supportDithering"), new Boolean(getCapability(2) == 1));
      localHashtable.put(new String("supportMipmapping"), new Boolean(getCapability(3) == 1));
      localHashtable.put(new String("supportPerspectiveCorrection"), new Boolean(getCapability(4) == 1));
      localHashtable.put(new String("supportLocalCameraLighting"), new Boolean(getCapability(5) == 1));
      localHashtable.put(new String("maxLights"), new Integer(getCapability(6)));
      localHashtable.put(new String("maxViewportDimension"), new Integer(getCapability(7)));
      localHashtable.put(new String("maxTextureDimension"), new Integer(getCapability(8)));
      localHashtable.put(new String("maxSpriteCropDimension"), new Integer(getCapability(9)));
      localHashtable.put(new String("numTextureUnits"), new Integer(getCapability(10)));
      localHashtable.put(new String("maxTransformsPerVertex"), new Integer(getCapability(11)));
      localHashtable.put(new String("maxViewportWidth"), new Integer(maxViewportWidth));
      localHashtable.put(new String("maxViewportHeight"), new Integer(maxViewportHeight));
      localHashtable.put(new String("C3A458D3-2015-41f5-8338-66A2D3014335"), Engine.getVersionMajor() + "." + Engine.getVersionMinor() + "." + Engine.getRevisionMajor() + "." + Engine.getRevisionMinor() + ":" + Engine.getBranchNumber());
      try
      {
        localHashtable.put(new String("com.superscape.m3gx.DebugUtils"), Class.forName("javax.microedition.m3g.DebugUtils").newInstance());
      }
      catch (Exception localException1) {
    	  localException1.printStackTrace();}
      try
      {
        localHashtable.put(new String("com.superscape.m3gx.Image2DUtils"), Class.forName("javax.microedition.m3g.Image2DUtils").newInstance());
      }
      catch (Exception localException2) {
    	  localException2.printStackTrace();}
      try
      {
        localHashtable.put(new String("com.superscape.m3gx.SerializeOut"), Class.forName("javax.microedition.m3g.SerializeOut").newInstance());
      }
      catch (Exception localException3) {
    	  localException3.printStackTrace();}
      return localHashtable;
    }
    
    private static native int getCapability(final int p0);
    
    static Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    static {
        i.a("jsr184client");
        Engine.cacheFID((Graphics3D.class$javax$microedition$m3g$Graphics3D == null) ? (Graphics3D.class$javax$microedition$m3g$Graphics3D = class$("javax.microedition.m3g.Graphics3D")) : Graphics3D.class$javax$microedition$m3g$Graphics3D, 1);
        Graphics3D.instance = null;
        Graphics3D.maxViewportWidth = getCapability(12);
        Graphics3D.maxViewportHeight = getCapability(13);
    }
}*/
