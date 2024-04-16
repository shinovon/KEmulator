package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;

public class Material extends Object3D {
   public static final int AMBIENT = 1024;
   public static final int DIFFUSE = 2048;
   public static final int EMISSIVE = 4096;
   public static final int SPECULAR = 8192;
   private boolean vertexColorTracking = false;
   private int ambientColor = 3355443;
   private int diffuseColor = -3355444;
   private int emissiveColor = 0;
   private int specularColor = 0;
   private float shininess = 0.0F;

   public void setColor(int var1, int var2) {
      if((var1 & 1024) == 0 && (var1 & 2048) == 0 && (var1 & 4096) == 0 && (var1 & 8192) == 0) {
         throw new IllegalArgumentException();
      } else {
         if((var1 & 1024) != 0) {
            this.ambientColor = var2;
         }

         if((var1 & 2048) != 0) {
            this.diffuseColor = var2;
         }

         if((var1 & 4096) != 0) {
            this.emissiveColor = var2;
         }

         if((var1 & 8192) != 0) {
            this.specularColor = var2;
         }

      }
   }

   public int getColor(int var1) {
      if(var1 != 1024 && var1 != 2048 && var1 != 4096 && var1 != 8192) {
         throw new IllegalArgumentException();
      } else {
         return var1 == 1024?this.ambientColor :(var1 == 2048?this.diffuseColor :(var1 == 4096?this.emissiveColor :(var1 == 8192?this.specularColor :0)));
      }
   }

   public void setShininess(float var1) {
      if(var1 >= 0.0F && var1 <= 128.0F) {
         this.shininess = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public float getShininess() {
      return this.shininess;
   }

   public void setVertexColorTrackingEnable(boolean var1) {
      this.vertexColorTracking = var1;
   }

   public boolean isVertexColorTrackingEnabled() {
      return this.vertexColorTracking;
   }

   protected void updateProperty(int var1, float[] var2) {
      switch(var1) {
      case 256:
         this.diffuseColor = this.diffuseColor & 16777215 | G3DUtils.getIntColor(var2) & -16777216;
         return;
      case 257:
         this.ambientColor = G3DUtils.getIntColor(var2);
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
         this.diffuseColor = this.diffuseColor & -16777216 | G3DUtils.getIntColor(var2) & 16777215;
         return;
      case 262:
         this.emissiveColor = G3DUtils.getIntColor(var2);
         return;
      case 271:
         this.shininess = G3DUtils.method605(var2[0], 0.0F, 128.0F);
         return;
      case 272:
         this.specularColor = G3DUtils.getIntColor(var2);
      }
   }
}
