package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;

public class Fog extends Object3D {
   public static final int EXPONENTIAL = 80;
   public static final int LINEAR = 81;
   private int anInt37 = 81;
   private float aFloat681 = 1.0F;
   private float aFloat682 = 0.0F;
   private float aFloat683 = 1.0F;
   private int anInt39 = 0;

   public void setMode(int var1) {
      if(var1 != 80 && var1 != 81) {
         throw new IllegalArgumentException();
      } else {
         this.anInt37 = var1;
      }
   }

   public int getMode() {
      return this.anInt37;
   }

   public void setLinear(float var1, float var2) {
      this.aFloat682 = var1;
      this.aFloat683 = var2;
   }

   public float getNearDistance() {
      return this.aFloat682;
   }

   public float getFarDistance() {
      return this.aFloat683;
   }

   public void setDensity(float var1) {
      if(var1 < 0.0F) {
         throw new IllegalArgumentException();
      } else {
         this.aFloat681 = var1;
      }
   }

   public float getDensity() {
      return this.aFloat681;
   }

   public void setColor(int var1) {
      this.anInt39 = var1;
   }

   public int getColor() {
      return this.anInt39;
   }

   protected void updateProperty(int var1, float[] var2) {
      switch(var1) {
      case 258:
         this.anInt39 = G3DUtils.getIntColor(var2);
         return;
      case 259:
      case 261:
      case 262:
      case 264:
      case 265:
      case 266:
      default:
         super.updateProperty(var1, var2);
         return;
      case 260:
         this.aFloat681 = G3DUtils.limitPositive(var2[0]);
         return;
      case 263:
         this.aFloat683 = var2[0];
         return;
      case 267:
         this.aFloat682 = var2[0];
      }
   }
}
