package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.RayIntersection;
import javax.microedition.m3g.Transform;

public class Light extends Node {
   public static final int AMBIENT = 128;
   public static final int DIRECTIONAL = 129;
   public static final int OMNI = 130;
   public static final int SPOT = 131;
   private int anInt37 = 129;
   private int anInt39 = 16777215;
   private float aFloat681 = 1.0F;
   private float aFloat682 = 1.0F;
   private float aFloat683 = 0.0F;
   private float aFloat1172 = 0.0F;
   private float aFloat1173 = 45.0F;
   private float aFloat1174 = 0.0F;

   public void setMode(int var1) {
      if(var1 >= 128 && var1 <= 131) {
         this.anInt37 = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getMode() {
      return this.anInt37;
   }

   public void setIntensity(float var1) {
      this.aFloat681 = var1;
   }

   public float getIntensity() {
      return this.aFloat681;
   }

   public void setColor(int var1) {
      this.anInt39 = var1;
   }

   public int getColor() {
      return this.anInt39;
   }

   public void setSpotAngle(float var1) {
      if(var1 >= 0.0F && var1 <= 90.0F) {
         this.aFloat1173 = var1;
      } else {
         throw new IllegalArgumentException("angle is not in [0, 90]");
      }
   }

   public float getSpotAngle() {
      return this.aFloat1173;
   }

   public void setSpotExponent(float var1) {
      if(var1 >= 0.0F && var1 <= 128.0F) {
         this.aFloat1174 = var1;
      } else {
         throw new IllegalArgumentException("exponent is not in [0, 128]");
      }
   }

   public float getSpotExponent() {
      return this.aFloat1174;
   }

   public void setAttenuation(float var1, float var2, float var3) {
      if(var1 >= 0.0F && var2 >= 0.0F && var3 >= 0.0F) {
         if(var1 == 0.0F && var2 == 0.0F && var3 == 0.0F) {
            throw new IllegalArgumentException("all of the parameter values are zero");
         } else {
            this.aFloat682 = var1;
            this.aFloat683 = var2;
            this.aFloat1172 = var3;
         }
      } else {
         throw new IllegalArgumentException("any of the parameter values are negative");
      }
   }

   public float getConstantAttenuation() {
      return this.aFloat682;
   }

   public float getLinearAttenuation() {
      return this.aFloat683;
   }

   public float getQuadraticAttenuation() {
      return this.aFloat1172;
   }

   protected void updateProperty(int var1, float[] var2) {
      switch(var1) {
      case 258:
         this.anInt39 = G3DUtils.method603(var2);
         return;
      case 265:
         this.aFloat681 = var2[0];
         return;
      case 273:
         this.aFloat1173 = G3DUtils.method605(var2[0], 0.0F, 90.0F);
         return;
      case 274:
         this.aFloat1174 = G3DUtils.method605(var2[0], 0.0F, 128.0F);
         return;
      default:
         super.updateProperty(var1, var2);
      }
   }

   protected boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4) {
      return false;
   }
}
