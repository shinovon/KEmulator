package javax.microedition.m3g;

import javax.microedition.m3g.Object3D;

public class PolygonMode extends Object3D {
   public static final int CULL_BACK = 160;
   public static final int CULL_FRONT = 161;
   public static final int CULL_NONE = 162;
   public static final int SHADE_FLAT = 164;
   public static final int SHADE_SMOOTH = 165;
   public static final int WINDING_CCW = 168;
   public static final int WINDING_CW = 169;
   private int anInt37 = 160;
   private int anInt39 = 165;
   private int anInt40 = 168;
   private boolean aBoolean36 = false;
   private boolean aBoolean190 = false;
   private boolean aBoolean684 = false;

   public void setCulling(int var1) {
      if(var1 >= 160 && var1 <= 162) {
         this.anInt37 = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getCulling() {
      return this.anInt37;
   }

   public void setWinding(int var1) {
      if(var1 != 169 && var1 != 168) {
         throw new IllegalArgumentException();
      } else {
         this.anInt40 = var1;
      }
   }

   public int getWinding() {
      return this.anInt40;
   }

   public void setShading(int var1) {
      if(var1 != 164 && var1 != 165) {
         throw new IllegalArgumentException();
      } else {
         this.anInt39 = var1;
      }
   }

   public int getShading() {
      return this.anInt39;
   }

   public void setTwoSidedLightingEnable(boolean var1) {
      this.aBoolean36 = var1;
   }

   public boolean isTwoSidedLightingEnabled() {
      return this.aBoolean36;
   }

   public void setLocalCameraLightingEnable(boolean var1) {
      this.aBoolean190 = var1;
   }

   public boolean isLocalCameraLightingEnabled() {
      return this.aBoolean190;
   }

   public void setPerspectiveCorrectionEnable(boolean var1) {
      this.aBoolean684 = var1;
   }

   public boolean isPerspectiveCorrectionEnabled() {
      return this.aBoolean684;
   }
}
