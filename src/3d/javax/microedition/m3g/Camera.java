package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.RayIntersection;
import javax.microedition.m3g.Transform;

public class Camera extends Node {
   public static final int GENERIC = 48;
   public static final int PARALLEL = 49;
   public static final int PERSPECTIVE = 50;
   private int anInt37 = 48;
   private Transform aTransform917 = new Transform();
   private float[] aFloatArray918 = new float[4];

   protected Object3D duplicateObject() {
      Camera var1;
      (var1 = (Camera)super.duplicateObject()).aTransform917 = new Transform(this.aTransform917);
      var1.aFloatArray918 = (float[])this.aFloatArray918.clone();
      return var1;
   }

   public Camera() {
      this.aFloatArray918[0] = 2.0F;
      this.aFloatArray918[1] = 1.0F;
      this.aFloatArray918[2] = -1.0F;
      this.aFloatArray918[3] = 1.0F;
   }

   public void setParallel(float var1, float var2, float var3, float var4) {
      if(var1 > 0.0F && var2 > 0.0F) {
         this.aFloatArray918[0] = var1;
         this.aFloatArray918[1] = var2;
         this.aFloatArray918[2] = var3;
         this.aFloatArray918[3] = var4;
         this.anInt37 = 49;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setPerspective(float var1, float var2, float var3, float var4) {
      if(var1 > 0.0F && var1 < 180.0F && var2 > 0.0F && var3 > 0.0F && var4 > 0.0F) {
         this.aFloatArray918[0] = var1;
         this.aFloatArray918[1] = var2;
         this.aFloatArray918[2] = var3;
         this.aFloatArray918[3] = var4;
         this.anInt37 = 50;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setGeneric(Transform var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else {
         this.aTransform917.set(var1);
      }
   }

   public int getProjection(Transform var1) {
      if(var1 != null) {
         float var2;
         float var3;
         float var4;
         float[] var5;
         if(this.anInt37 == 49) {
            var2 = this.aFloatArray918[0];
            var3 = this.aFloatArray918[1] * var2;
            if((var4 = this.aFloatArray918[3] - this.aFloatArray918[2]) == 0.0F) {
               throw new ArithmeticException("near == far");
            }

            (var5 = new float[16])[0] = 2.0F / var3;
            var5[5] = 2.0F / var2;
            var5[10] = -2.0F / var4;
            var5[11] = -(this.aFloatArray918[2] + this.aFloatArray918[3]) / var4;
            var5[15] = 1.0F;
            var1.set(var5);
         } else if(this.anInt37 == 50) {
            var2 = (float)Math.tan(Math.toRadians((double)(this.aFloatArray918[0] / 2.0F)));
            var3 = this.aFloatArray918[1] * var2;
            if((var4 = this.aFloatArray918[3] - this.aFloatArray918[2]) == 0.0F) {
               throw new ArithmeticException("near == far");
            }

            (var5 = new float[16])[0] = 1.0F / var3;
            var5[5] = 1.0F / var2;
            var5[10] = -(this.aFloatArray918[2] + this.aFloatArray918[3]) / var4;
            var5[11] = -2.0F * this.aFloatArray918[2] * this.aFloatArray918[3] / var4;
            var5[14] = -1.0F;
            var1.set(var5);
         } else {
            var1.set(this.aTransform917);
         }
      }

      return this.anInt37;
   }

   public int getProjection(float[] var1) {
      if(var1 != null && var1.length < 4) {
         throw new IllegalArgumentException();
      } else {
         if(var1 != null && this.anInt37 != 48) {
            System.arraycopy(this.aFloatArray918, 0, var1, 0, 4);
         }

         return this.anInt37;
      }
   }

   protected void updateProperty(int var1, float[] var2) {
      if(this.anInt37 != 48) {
         switch(var1) {
         case 263:
            this.aFloatArray918[3] = this.anInt37 != 50?var2[0]:G3DUtils.method604(var2[0]);
            return;
         case 264:
            this.aFloatArray918[0] = this.anInt37 != 50?G3DUtils.method604(var2[0]):G3DUtils.method605(var2[0], 0.0F, 180.0F);
            return;
         case 265:
         case 266:
         default:
            break;
         case 267:
            this.aFloatArray918[2] = this.anInt37 != 50?var2[0]:G3DUtils.method604(var2[0]);
            return;
         }
      }

      super.updateProperty(var1, var2);
   }

   protected boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4) {
      return false;
   }
}
