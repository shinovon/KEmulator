package javax.microedition.m3g;

import emulator.graphics3D.a;

public abstract class Transformable extends Object3D {
   float[] aFloatArray735 = new float[3];
   float[] translation = new float[3];
   a ana864 = new a(0.0F, 0.0F, 0.0F, 1.0F);
   Transform transform = new Transform();

   Transformable() {
      this.aFloatArray735[0] = this.aFloatArray735[1] = this.aFloatArray735[2] = 1.0F;
   }

   public void setOrientation(float var1, float var2, float var3, float var4) {
      if(var1 != 0.0F && var2 == 0.0F && var3 == 0.0F && var4 == 0.0F) {
         throw new IllegalArgumentException();
      } else {
         this.ana864.method407(var1, var2, var3, var4);
      }
   }

   public void getOrientation(float[] var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var1.length < 4) {
         throw new IllegalArgumentException();
      } else {
         this.ana864.method415(var1);
      }
   }

   public void preRotate(float var1, float var2, float var3, float var4) {
      if(var1 != 0.0F && var2 == 0.0F && var3 == 0.0F && var4 == 0.0F) {
         throw new IllegalArgumentException();
      } else {
         a var5;
         (var5 = new a()).method407(var1, var2, var3, var4);
         var5.method416(this.ana864);
         this.ana864.method406(var5);
      }
   }

   public void postRotate(float var1, float var2, float var3, float var4) {
      if(var1 != 0.0F && var2 == 0.0F && var3 == 0.0F && var4 == 0.0F) {
         throw new IllegalArgumentException();
      } else {
         a var5;
         (var5 = new a()).method407(var1, var2, var3, var4);
         this.ana864.method416(var5);
      }
   }

   public void setScale(float var1, float var2, float var3) {
      this.aFloatArray735[0] = var1;
      this.aFloatArray735[1] = var2;
      this.aFloatArray735[2] = var3;
   }

   public void scale(float var1, float var2, float var3) {
      this.aFloatArray735[0] *= var1;
      this.aFloatArray735[1] *= var2;
      this.aFloatArray735[2] *= var3;
   }

   public void getScale(float[] var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var1.length < 3) {
         throw new IllegalArgumentException();
      } else {
         System.arraycopy(this.aFloatArray735, 0, var1, 0, 3);
      }
   }

   public void setTranslation(float var1, float var2, float var3) {
      this.translation[0] = var1;
      this.translation[1] = var2;
      this.translation[2] = var3;
   }

   public void translate(float var1, float var2, float var3) {
      this.translation[0] += var1;
      this.translation[1] += var2;
      this.translation[2] += var3;
   }

   public void getTranslation(float[] var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var1.length < 3) {
         throw new IllegalArgumentException();
      } else {
         System.arraycopy(this.translation, 0, var1, 0, 3);
      }
   }

   public void setTransform(Transform var1) {
      if(var1 == null) {
         this.transform.setIdentity();
      } else {
         this.transform.set(var1);
      }
   }

   public void getTransform(Transform var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else {
         var1.set(this.transform);
      }
   }

   public void getCompositeTransform(Transform var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else {
         var1.setIdentity();
         var1.postTranslate(this.translation[0], this.translation[1], this.translation[2]);
         var1.postRotateQuat(this.ana864.aFloat603, this.ana864.aFloat604, this.ana864.aFloat605, this.ana864.aFloat606);
         var1.postScale(this.aFloatArray735[0], this.aFloatArray735[1], this.aFloatArray735[2]);
         var1.postMultiply(this.transform);
      }
   }

   protected void updateProperty(int var1, float[] var2) {
      switch(var1) {
      case 268:
         this.ana864.method405(var2);
         this.ana864.method404();
         return;
      case 270:
         if(var2.length == 1) {
            this.aFloatArray735[0] = this.aFloatArray735[1] = this.aFloatArray735[2] = var2[0];
            return;
         }

         this.aFloatArray735[0] = var2[0];
         this.aFloatArray735[1] = var2[1];
         this.aFloatArray735[2] = var2[2];
         return;
      case 275:
         this.translation[0] = var2[0];
         this.translation[1] = var2[1];
         this.translation[2] = var2[2];
         return;
      default:
         super.updateProperty(var1, var2);
      }
   }

   protected Object3D duplicateObject() {
      Transformable var1;
      (var1 = (Transformable)super.duplicateObject()).ana864 = new a(this.ana864);
      var1.transform = new Transform(this.transform);
      var1.translation = (float[])this.translation.clone();
      var1.aFloatArray735 = (float[])this.aFloatArray735.clone();
      return var1;
   }
}
