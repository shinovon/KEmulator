package javax.microedition.m3g;

import javax.microedition.m3g.Object3D;

public class CompositingMode extends Object3D {
   public static final int ALPHA = 64;
   public static final int ALPHA_ADD = 65;
   public static final int MODULATE = 66;
   public static final int MODULATE_X2 = 67;
   public static final int REPLACE = 68;
   private int anInt37 = 68;
   private float aFloat681 = 0.0F;
   private float aFloat682 = 0.0F;
   private float aFloat683 = 0.0F;
   private boolean aBoolean36 = true;
   private boolean aBoolean190 = true;
   private boolean aBoolean684 = true;
   private boolean aBoolean685 = true;

   public void setBlending(int var1) {
      if(var1 >= 64 && var1 <= 68) {
         this.anInt37 = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getBlending() {
      return this.anInt37;
   }

   public void setAlphaThreshold(float var1) {
      if(var1 >= 0.0F && var1 <= 1.0F) {
         this.aFloat681 = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public float getAlphaThreshold() {
      return this.aFloat681;
   }

   public void setAlphaWriteEnable(boolean var1) {
      this.aBoolean685 = var1;
   }

   public boolean isAlphaWriteEnabled() {
      return this.aBoolean685;
   }

   public void setColorWriteEnable(boolean var1) {
      this.aBoolean684 = var1;
   }

   public boolean isColorWriteEnabled() {
      return this.aBoolean684;
   }

   public void setDepthWriteEnable(boolean var1) {
      this.aBoolean190 = var1;
   }

   public boolean isDepthWriteEnabled() {
      return this.aBoolean190;
   }

   public void setDepthTestEnable(boolean var1) {
      this.aBoolean36 = var1;
   }

   public boolean isDepthTestEnabled() {
      return this.aBoolean36;
   }

   public void setDepthOffset(float var1, float var2) {
      this.aFloat682 = var1;
      this.aFloat683 = var2;
   }

   public float getDepthOffsetFactor() {
      return this.aFloat682;
   }

   public float getDepthOffsetUnits() {
      return this.aFloat683;
   }
}
