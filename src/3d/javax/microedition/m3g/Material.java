package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import javax.microedition.m3g.Object3D;

public class Material extends Object3D {
   public static final int AMBIENT = 1024;
   public static final int DIFFUSE = 2048;
   public static final int EMISSIVE = 4096;
   public static final int SPECULAR = 8192;
   private boolean aBoolean36 = false;
   private int anInt37 = 3355443;
   private int anInt39 = -3355444;
   private int anInt40 = 0;
   private int anInt150 = 0;
   private float aFloat681 = 0.0F;

   public void setColor(int var1, int var2) {
      if((var1 & 1024) == 0 && (var1 & 2048) == 0 && (var1 & 4096) == 0 && (var1 & 8192) == 0) {
         throw new IllegalArgumentException();
      } else {
         if((var1 & 1024) != 0) {
            this.anInt37 = var2;
         }

         if((var1 & 2048) != 0) {
            this.anInt39 = var2;
         }

         if((var1 & 4096) != 0) {
            this.anInt40 = var2;
         }

         if((var1 & 8192) != 0) {
            this.anInt150 = var2;
         }

      }
   }

   public int getColor(int var1) {
      if(var1 != 1024 && var1 != 2048 && var1 != 4096 && var1 != 8192) {
         throw new IllegalArgumentException();
      } else {
         return var1 == 1024?this.anInt37:(var1 == 2048?this.anInt39:(var1 == 4096?this.anInt40:(var1 == 8192?this.anInt150:0)));
      }
   }

   public void setShininess(float var1) {
      if(var1 >= 0.0F && var1 <= 128.0F) {
         this.aFloat681 = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public float getShininess() {
      return this.aFloat681;
   }

   public void setVertexColorTrackingEnable(boolean var1) {
      this.aBoolean36 = var1;
   }

   public boolean isVertexColorTrackingEnabled() {
      return this.aBoolean36;
   }

   protected void updateProperty(int var1, float[] var2) {
      switch(var1) {
      case 256:
         this.anInt39 = this.anInt39 & 16777215 | G3DUtils.method603(var2) & -16777216;
         return;
      case 257:
         this.anInt37 = G3DUtils.method603(var2);
         return;
      case 258:
      case 259:
      case 260:
      case 263:
      case 264:
      case 265:
      case 266:
      case 267:
      case 268:
      case 269:
      case 270:
      default:
         super.updateProperty(var1, var2);
         return;
      case 261:
         this.anInt39 = this.anInt39 & -16777216 | G3DUtils.method603(var2) & 16777215;
         return;
      case 262:
         this.anInt40 = G3DUtils.method603(var2);
         return;
      case 271:
         this.aFloat681 = G3DUtils.method605(var2[0], 0.0F, 128.0F);
         return;
      case 272:
         this.anInt150 = G3DUtils.method603(var2);
      }
   }
}
