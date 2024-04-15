package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Transformable;

public class Texture2D extends Transformable {
   public static final int FILTER_BASE_LEVEL = 208;
   public static final int FILTER_LINEAR = 209;
   public static final int FILTER_NEAREST = 210;
   public static final int FUNC_ADD = 224;
   public static final int FUNC_BLEND = 225;
   public static final int FUNC_DECAL = 226;
   public static final int FUNC_MODULATE = 227;
   public static final int FUNC_REPLACE = 228;
   public static final int WRAP_CLAMP = 240;
   public static final int WRAP_REPEAT = 241;
   private Image2D anImage2D157;
   private int anInt37;
   private int anInt39;
   private int anInt40;
   private int anInt150;
   private int anInt151;
   private int anInt152;

   public Texture2D(Image2D var1) {
      this.setImage(var1);
      this.anInt37 = 241;
      this.anInt39 = 241;
      this.anInt40 = 208;
      this.anInt150 = 210;
      this.anInt151 = 227;
      this.anInt152 = 0;
   }

   private static boolean method14(int var0) {
      return (var0 & var0 - 1) == 0;
   }

   public void setImage(Image2D var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else {
         int var2 = var1.getWidth();
         int var3 = var1.getHeight();
         if(method14(var2) && method14(var3)) {
            if(var2 <= Graphics3D.MaxTextureDimension && var3 <= Graphics3D.MaxTextureDimension) {
               this.removeReference(this.anImage2D157);
               this.anImage2D157 = var1;
               this.addReference(this.anImage2D157);
            } else {
               throw new IllegalArgumentException("the width or height of image exceeds the MaxTextureDimension :" + Graphics3D.MaxTextureDimension);
            }
         } else {
            throw new IllegalArgumentException("the width or height of image is not a positive power of two");
         }
      }
   }

   public Image2D getImage() {
      return this.anImage2D157;
   }

   public void setFiltering(int var1, int var2) {
      if(var1 != 208 && var1 != 209 && var1 != 210) {
         throw new IllegalArgumentException();
      } else if(var2 != 209 && var2 != 210) {
         throw new IllegalArgumentException();
      } else {
         this.anInt40 = var1;
         this.anInt150 = var2;
      }
   }

   public int getLevelFilter() {
      return this.anInt40;
   }

   public int getImageFilter() {
      return this.anInt150;
   }

   public void setWrapping(int var1, int var2) {
      if(var1 != 240 && var1 != 241) {
         throw new IllegalArgumentException();
      } else if(var2 != 240 && var2 != 241) {
         throw new IllegalArgumentException();
      } else {
         this.anInt37 = var1;
         this.anInt39 = var2;
      }
   }

   public int getWrappingS() {
      return this.anInt37;
   }

   public int getWrappingT() {
      return this.anInt39;
   }

   public void setBlending(int var1) {
      if(var1 >= 224 && var1 <= 228) {
         this.anInt151 = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getBlending() {
      return this.anInt151;
   }

   public void setBlendColor(int var1) {
      this.anInt152 = var1;
   }

   public int getBlendColor() {
      return this.anInt152;
   }

   protected void updateProperty(int var1, float[] var2) {
      switch(var1) {
      case 258:
         this.anInt152 = G3DUtils.method603(var2);
         return;
      default:
         super.updateProperty(var1, var2);
      }
   }
}
