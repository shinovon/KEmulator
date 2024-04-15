package javax.microedition.m3g;

import emulator.graphics3D.c;
import javax.microedition.m3g.VertexArray;

public class Transform {
   private c ac1306;

   public Transform() {
      this.ac1306 = new c();
   }

   public Transform(Transform var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else {
         this.ac1306 = new c();
         this.ac1306.method434(var1.ac1306);
      }
   }

   public c getImpl() {
      return this.ac1306;
   }

   public void setIdentity() {
      this.ac1306.method432();
   }

   public void set(Transform var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else {
         this.ac1306.method434(var1.ac1306);
      }
   }

   public void set(float[] var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var1.length < 16) {
         throw new IllegalArgumentException();
      } else {
         this.ac1306.method441(var1);
      }
   }

   public void get(float[] var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var1.length < 16) {
         throw new IllegalArgumentException();
      } else {
         this.ac1306.method433(var1);
      }
   }

   public void invert() {
      this.ac1306.method442();
   }

   public void transpose() {
      this.ac1306.method447();
   }

   public void postMultiply(Transform var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else {
         this.ac1306.method436(var1.ac1306, false);
      }
   }

   public void preMultiply(Transform var1) {
      this.ac1306.method436(var1.ac1306, true);
   }

   public void postScale(float var1, float var2, float var3) {
      this.ac1306.method438(var1, var2, var3);
   }

   public void postRotate(float var1, float var2, float var3, float var4) {
      if(var2 == 0.0F && var3 == 0.0F && var4 == 0.0F && var1 != 0.0F) {
         throw new IllegalArgumentException();
      } else {
         this.ac1306.method437(var1, var2, var3, var4);
      }
   }

   public void postRotateQuat(float var1, float var2, float var3, float var4) {
      if(var3 == 0.0F && var2 == 0.0F && var3 == 0.0F && var4 == 0.0F) {
         throw new IllegalArgumentException();
      } else {
         this.ac1306.method443(var1, var2, var3, var4);
      }
   }

   public void postTranslate(float var1, float var2, float var3) {
      this.ac1306.method444(var1, var2, var3);
   }

   public void transform(VertexArray var1, float[] var2, boolean var3) {
      if(var1 != null && var2 != null) {
         if(var1.getComponentCount() != 4 && var2.length >= 4 * var1.getVertexCount()) {
            int var4 = var3?1:0;
            int var5 = var1.getVertexCount();
            int var6 = var1.getComponentCount();
            int var7 = var5 * 4;
            int var8 = 0;
            int var9 = 0;
            if(var1.getComponentType() == 1) {
               byte[] var10 = new byte[var5 * var6];
               var1.get(0, var5, var10);

               while(var8 < var7) {
                  if(var8 % 4 < var6) {
                     var2[var8++] = (float)var10[var9++];
                  } else {
                     var2[var8++] = (float)var4;
                  }
               }
            } else {
               short[] var11 = new short[var5 * var6];
               var1.get(0, var5, var11);

               while(var8 < var7) {
                  if(var8 % 4 < var6) {
                     var2[var8++] = (float)var11[var9++];
                  } else {
                     var2[var8++] = (float)var4;
                  }
               }
            }

            this.ac1306.method446(var2);
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void transform(float[] var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var1.length % 4 != 0) {
         throw new IllegalArgumentException();
      } else {
         this.ac1306.method446(var1);
      }
   }
}
