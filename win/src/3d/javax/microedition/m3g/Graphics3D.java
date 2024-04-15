package javax.microedition.m3g;

import emulator.Emulator;
import emulator.graphics3D.IGraphics3D;
import emulator.graphics3D.m3g.a;
import emulator.graphics3D.m3g.f;
import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Graphics3D {
   public static final int ANTIALIAS = 2;
   public static final int DITHER = 4;
   public static final int OVERWRITE = 16;
   public static final int TRUE_COLOR = 8;
   private static Object anObject876 = null;
   private static IGraphics3D anIGraphics3D877;
   private static Graphics3D aGraphics3D878 = new Graphics3D();
   public static final int MaxViewportWidth = ((Integer)getProperties().get("maxViewportWidth")).intValue();
   public static final int MaxViewportHeight = ((Integer)getProperties().get("maxViewportHeight")).intValue();
   public static final int NumTextureUnits = ((Integer)getProperties().get("numTextureUnits")).intValue();
   public static final int MaxTextureDimension = ((Integer)getProperties().get("maxTextureDimension")).intValue();
   public static final int MaxSpriteCropDimension = ((Integer)getProperties().get("maxSpriteCropDimension")).intValue();
   private int anInt879;
   private int anInt881;
   private int anInt883;
   private int anInt884;
   private float aFloat880;
   private float aFloat882;

   private Graphics3D() {
      anIGraphics3D877 = Emulator.getEmulator().getGraphics3D();
      this.aFloat880 = 0.0F;
      this.aFloat882 = 1.0F;
   }

   public static final Graphics3D getInstance() {
      return aGraphics3D878;
   }

   public static final IGraphics3D getImpl() {
      return anIGraphics3D877;
   }

   public static final Hashtable getProperties() {
      return anIGraphics3D877.getProperties();
   }

   public void bindTarget(Object var1) {
      this.bindTarget(var1, true, 0);
   }

   public void bindTarget(Object var1, boolean var2, int var3) {
      anIGraphics3D877.enableDepthBuffer(var2);
      if(anObject876 != null) {
         throw new IllegalStateException();
      } else if(var1 == null) {
         throw new NullPointerException();
      } else if(var3 != 0 && (var3 & 30) == 0) {
         throw new IllegalArgumentException();
      } else {
         Background var4 = new Background();
         int var6;
         int var7;
         if(var1 instanceof Graphics) {
            Graphics var5;
            var6 = (var5 = (Graphics)var1).getImage().getWidth();
            var7 = var5.getImage().getHeight();
            if(var6 > MaxViewportWidth || var7 > MaxViewportHeight) {
               throw new IllegalArgumentException();
            }

            anObject876 = var1;
            anIGraphics3D877.bindTarget(var5);
            this.setViewport(var5.getClipX(), var5.getClipY(), var5.getClipWidth(), var5.getClipHeight());
            Image2D var8 = new Image2D(99, new Image(var5.getImage()));
            var4.setImage(var8);
            var4.setCrop(this.anInt879, this.anInt881, this.anInt883, this.anInt884);
         } else {
            if(!(var1 instanceof Image2D)) {
               throw new IllegalArgumentException();
            }

            Image2D var9;
            var6 = (var9 = (Image2D)var1).getWidth();
            var7 = var9.getHeight();
            if(var9.getWidth() > MaxViewportWidth || var9.getHeight() > MaxViewportHeight) {
               throw new IllegalArgumentException();
            }

            int var10 = var9.getFormat();
            if(!var9.isMutable() || var10 != 99 && var10 != 100) {
               throw new IllegalArgumentException();
            }

            anObject876 = var1;
            anIGraphics3D877.bindTarget(var9);
            this.setViewport(0, 0, var6, var7);
            var4.setImage(var9);
            var4.setCrop(this.anInt879, this.anInt881, this.anInt883, this.anInt884);
         }

         if(anObject876 != null) {
            anIGraphics3D877.setHints(var3);
            this.setDepthRange(this.aFloat880, this.aFloat882);
            this.clear(var4);
         }

      }
   }

   public void releaseTarget() {
      if(anObject876 != null) {
         anIGraphics3D877.releaseTarget();
         anObject876 = null;
      }

   }

   public Object getTarget() {
      return anObject876;
   }

   public int getHints() {
      return anIGraphics3D877.getHints();
   }

   public boolean isDepthBufferEnabled() {
      return anIGraphics3D877.isDepthBufferEnabled();
   }

   public void setViewport(int var1, int var2, int var3, int var4) {
      if(var3 > 0 && var4 > 0 && var3 <= MaxViewportWidth && var4 <= MaxViewportHeight) {
         this.anInt879 = var1;
         this.anInt881 = var2;
         this.anInt883 = var3;
         this.anInt884 = var4;
         anIGraphics3D877.setViewport(var1, var2, var3, var4);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getViewportX() {
      return this.anInt879;
   }

   public int getViewportY() {
      return this.anInt881;
   }

   public int getViewportWidth() {
      return this.anInt883;
   }

   public int getViewportHeight() {
      return this.anInt884;
   }

   public void setDepthRange(float var1, float var2) {
      if(var1 >= 0.0F && var1 <= 1.0F && var2 >= 0.0F && var2 <= 1.0F) {
         this.aFloat880 = var1;
         this.aFloat882 = var2;
         anIGraphics3D877.setDepthRange(var1, var2);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public float getDepthRangeNear() {
      return this.aFloat880;
   }

   public float getDepthRangeFar() {
      return this.aFloat882;
   }

   public void clear(Background var1) {
      if(anObject876 == null) {
         throw new IllegalStateException();
      } else {
         anIGraphics3D877.clearBackgound(var1);
      }
   }

   public void render(World var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(anObject876 != null && var1.getActiveCamera() != null && var1.getActiveCamera().isDescendantOf(var1)) {
         anIGraphics3D877.render(var1);
      } else {
         throw new IllegalStateException();
      }
   }

   public void render(Node var1, Transform var2) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(!(var1 instanceof Sprite3D) && !(var1 instanceof Mesh) && !(var1 instanceof Group)) {
         throw new IllegalArgumentException();
      } else if(anObject876 != null && f.aCamera1137 != null) {
         anIGraphics3D877.render(var1, var2);
      } else {
         throw new IllegalStateException();
      }
   }

   public void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5) {
      if(var1 != null && var2 != null && var3 != null) {
         if(anObject876 != null && f.aCamera1137 != null) {
            anIGraphics3D877.render(var1, var2, var3, var4, var5);
         } else {
            throw new IllegalStateException();
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4) {
      this.render(var1, var2, var3, var4, -1);
   }

   public void setCamera(Camera var1, Transform var2) {
      f.method785(var1, var2);
   }

   public Camera getCamera(Transform var1) {
      return f.method786(var1);
   }

   public int addLight(Light var1, Transform var2) {
      return a.method800(var1, var2);
   }

   public void setLight(int var1, Light var2, Transform var3) {
      a.method801(var1, var2, var3);
   }

   public void resetLights() {
      a.method802();
   }

   public int getLightCount() {
      return a.method803();
   }

   public Light getLight(int var1, Transform var2) {
      return a.method804(var1, var2);
   }
}
