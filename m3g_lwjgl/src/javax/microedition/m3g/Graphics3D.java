package javax.microedition.m3g;

import emulator.Emulator;
import emulator.graphics3D.IGraphics3D;
import emulator.graphics3D.lwjgl.Emulator3D;
import emulator.graphics3D.m3g.LightsCache;
import emulator.graphics3D.m3g.CameraCache;
import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Graphics3D {
   public static final int ANTIALIAS = 2;
   public static final int DITHER = 4;
   public static final int OVERWRITE = 16;
   public static final int TRUE_COLOR = 8;
   private static Object target = null;
   private static IGraphics3D impl;
   private static Graphics3D aGraphics3D878 = new Graphics3D();
   private int viewportX;
   private int viewportY;
   private int viewportWidth;
   private int viewportHeight;
   private float depthRangeNear;
   private float depthRangeFar;

   private Graphics3D() {
      impl = Emulator.getGraphics3D();
      this.depthRangeNear = 0.0F;
      this.depthRangeFar = 1.0F;
   }

   public static final Graphics3D getInstance() {
      return aGraphics3D878;
   }

   public static final IGraphics3D getImpl() {
      return impl;
   }

   public static final Hashtable getProperties() {
      return impl.getProperties();
   }

   public void bindTarget(Object var1) {
      this.bindTarget(var1, true, 0);
   }

   public void bindTarget(Object var1, boolean var2, int var3) {
      impl.enableDepthBuffer(var2);
      if(target != null) {
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
            if(var6 > Emulator3D.MaxViewportWidth || var7 > Emulator3D.MaxViewportHeight) {
               throw new IllegalArgumentException();
            }

            target = var1;
            impl.bindTarget(var5);
            this.setViewport(var5.getClipX(), var5.getClipY(), var5.getClipWidth(), var5.getClipHeight());
            Image2D var8 = new Image2D(99, new Image(var5.getImage()));
            var4.setImage(var8);
            var4.setCrop(this.viewportX, this.viewportY, this.viewportWidth, this.viewportHeight);
         } else {
            if(!(var1 instanceof Image2D)) {
               throw new IllegalArgumentException();
            }

            Image2D var9;
            var6 = (var9 = (Image2D)var1).getWidth();
            var7 = var9.getHeight();
            if(var9.getWidth() > Emulator3D.MaxViewportWidth || var9.getHeight() > Emulator3D.MaxViewportHeight) {
               throw new IllegalArgumentException();
            }

            int var10 = var9.getFormat();
            if(!var9.isMutable() || var10 != 99 && var10 != 100) {
               throw new IllegalArgumentException();
            }

            target = var1;
            impl.bindTarget(var9);
            this.setViewport(0, 0, var6, var7);
            var4.setImage(var9);
            var4.setCrop(this.viewportX, this.viewportY, this.viewportWidth, this.viewportHeight);
         }

         if(target != null) {
            impl.setHints(var3);
            this.setDepthRange(this.depthRangeNear, this.depthRangeFar);
            this.clear(var4);
         }

      }
   }

   public void releaseTarget() {
      if(target != null) {
         impl.releaseTarget();
         target = null;
      }

   }

   public Object getTarget() {
      return target;
   }

   public int getHints() {
      return impl.getHints();
   }

   public boolean isDepthBufferEnabled() {
      return impl.isDepthBufferEnabled();
   }

   public void setViewport(int var1, int var2, int var3, int var4) {
      if(var3 > 0 && var4 > 0 && var3 <= Emulator3D.MaxViewportWidth && var4 <= Emulator3D.MaxViewportHeight) {
         this.viewportX = var1;
         this.viewportY = var2;
         this.viewportWidth = var3;
         this.viewportHeight = var4;
         impl.setViewport(var1, var2, var3, var4);
      } else {
         throw new IllegalArgumentException();
      }
   }

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

   public void setDepthRange(float var1, float var2) {
      if(var1 >= 0.0F && var1 <= 1.0F && var2 >= 0.0F && var2 <= 1.0F) {
         this.depthRangeNear = var1;
         this.depthRangeFar = var2;
         impl.setDepthRange(var1, var2);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public float getDepthRangeNear() {
      return this.depthRangeNear;
   }

   public float getDepthRangeFar() {
      return this.depthRangeFar;
   }

   public void clear(Background var1) {
      if(target == null) {
         throw new IllegalStateException();
      } else {
         impl.clearBackgound(var1);
      }
   }

   public void render(World var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(target != null && var1.getActiveCamera() != null && var1.getActiveCamera().isDescendantOf(var1)) {
         impl.render(var1);
      } else {
         throw new IllegalStateException();
      }
   }

   public void render(Node var1, Transform var2) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(!(var1 instanceof Sprite3D) && !(var1 instanceof Mesh) && !(var1 instanceof Group)) {
         throw new IllegalArgumentException();
      } else if(target != null && CameraCache.camera != null) {
         impl.render(var1, var2);
      } else {
         throw new IllegalStateException();
      }
   }

   public void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5) {
      if(var1 != null && var2 != null && var3 != null) {
         if(target != null && CameraCache.camera != null) {
            impl.render(var1, var2, var3, var4, var5);
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
      CameraCache.setCamera(var1, var2);
   }

   public Camera getCamera(Transform var1) {
      return CameraCache.getCamera(var1);
   }

   public int addLight(Light var1, Transform var2) {
      return LightsCache.addLight(var1, var2);
   }

   public void setLight(int var1, Light var2, Transform var3) {
      LightsCache.setLight(var1, var2, var3);
   }

   public void resetLights() {
      LightsCache.resetLights();
   }

   public int getLightCount() {
      return LightsCache.getLightCount();
   }

   public Light getLight(int var1, Transform var2) {
      return LightsCache.getLight(var1, var2);
   }
}
