package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.b;

public class Sprite3D extends Node {
   private boolean scaled;
   private Image2D image;
   private Appearance appearance;
   private int cropX;
   private int cropY;
   private int cropWidth;
   private int cropHeight;

   public Sprite3D(boolean var1, Image2D var2, Appearance var3) {
      this.scaled = var1;
      this.setImage(var2);
      this.setAppearance(var3);
   }

   public void setImage(Image2D var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else {
         this.removeReference(this.image);
         this.image = var1;
         this.addReference(this.image);
         this.cropX = this.cropY = 0;
         this.cropWidth = Math.min(var1.getWidth(), Graphics3D.MaxSpriteCropDimension);
         this.cropHeight = Math.min(var1.getHeight(), Graphics3D.MaxSpriteCropDimension);
      }
   }

   public Image2D getImage() {
      return this.image;
   }

   public boolean isScaled() {
      return this.scaled;
   }

   public void setAppearance(Appearance var1) {
      this.removeReference(this.appearance);
      this.appearance = var1;
      this.addReference(this.appearance);
   }

   public Appearance getAppearance() {
      return this.appearance;
   }

   public void setCrop(int var1, int var2, int var3, int var4) {
      if(Math.abs(var3) <= Graphics3D.MaxSpriteCropDimension && Math.abs(var4) <= Graphics3D.MaxSpriteCropDimension) {
         this.cropX = var1;
         this.cropY = var2;
         this.cropWidth = var3;
         this.cropHeight = var4;
      } else {
         throw new IllegalArgumentException("width or height exceeds the MaxSpriteCropDimension:" + Graphics3D.MaxSpriteCropDimension);
      }
   }

   public int getCropX() {
      return this.cropX;
   }

   public int getCropY() {
      return this.cropY;
   }

   public int getCropWidth() {
      return this.cropWidth;
   }

   public int getCropHeight() {
      return this.cropHeight;
   }

   protected void updateProperty(int var1, float[] var2) {
      switch(var1) {
      case 259:
         this.cropX = G3DUtils.method607(var2[0]);
         this.cropY = G3DUtils.method607(var2[1]);
         if(var2.length > 2) {
            this.cropWidth = G3DUtils.method606(G3DUtils.method607(var2[2]), -Graphics3D.MaxSpriteCropDimension, Graphics3D.MaxSpriteCropDimension);
            this.cropHeight = G3DUtils.method606(G3DUtils.method607(var2[3]), -Graphics3D.MaxSpriteCropDimension, Graphics3D.MaxSpriteCropDimension);
            return;
         }
         break;
      default:
         super.updateProperty(var1, var2);
      }

   }

   protected boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4) {
      if(this.appearance != null && this.image != null && this.scaled && this.cropWidth != 0 && this.cropHeight != 0) {
         Camera var5;
         if((var5 = var3.getCamera()) != null && this.image != null) {
            int[] var6;
            boolean var7 = (var6 = new int[]{this.cropX, this.cropY, this.cropWidth, this.cropHeight})[2] < 0;
            boolean var8 = var6[3] < 0;
            var6[2] = Math.abs(var6[2]);
            var6[3] = Math.abs(var6[3]);
            int[] var9 = new int[4];
            if(!G3DUtils.method608(var6[0], var6[1], var6[2], var6[3], 0, 0, this.image.getWidth(), this.image.getHeight(), var9)) {
               return false;
            } else {
               b var10 = new b(0.0F, 0.0F, 0.0F, 1.0F);
               b var11 = new b(0.5F, 0.0F, 0.0F, 1.0F);
               b var12 = new b(0.0F, 0.5F, 0.0F, 1.0F);
               Transform var13 = new Transform();
               this.getTransformTo(var5, var13);
               var13.getImpl().method440(var10);
               var13.getImpl().method440(var11);
               var13.getImpl().method440(var12);
               b var14 = new b(var10);
               var10.method425(1.0F / var10.aFloat614);
               var11.method425(1.0F / var11.aFloat614);
               var12.method425(1.0F / var12.aFloat614);
               float var15 = (var10.aFloat612 - var2[6]) / (var2[7] - var2[6]);
               var11.method431(var10);
               var12.method431(var10);
               b var16 = new b(var11.method424(), 0.0F, 0.0F, 0.0F);
               b var17 = new b(0.0F, var12.method424(), 0.0F, 0.0F);
               var16.method429(var14);
               var17.method429(var14);
               var5.getProjection(var13);
               var13.getImpl().method440(var14);
               var13.getImpl().method440(var16);
               var13.getImpl().method440(var17);
               if(var14.aFloat614 > 0.0F && -var14.aFloat614 < var14.aFloat612 && var14.aFloat612 <= var14.aFloat614) {
                  var14.method425(1.0F / var14.aFloat614);
                  var16.method425(1.0F / var16.aFloat614);
                  var17.method425(1.0F / var17.aFloat614);
                  var16.method431(var14);
                  var17.method431(var14);
                  var16.aFloat608 = var16.method424() / (float)var6[2];
                  var17.aFloat610 = var17.method424() / (float)var6[3];
                  var14.aFloat608 -= (float)(2 * var6[0] + var6[2] - 2 * var9[0] - var9[2]) * var16.aFloat608;
                  var14.aFloat610 += (float)(2 * var6[1] + var6[3] - 2 * var9[1] - var9[3]) * var17.aFloat610;
                  var16.aFloat608 *= (float)var9[2];
                  var17.aFloat610 *= (float)var9[3];
                  float[] var18 = new float[12];
                  int[] var19 = new int[8];
                  var18[0] = var14.aFloat608 - var16.aFloat608;
                  var18[1] = var14.aFloat610 + var17.aFloat610;
                  var18[2] = var14.aFloat612;
                  var18[3] = var18[0];
                  var18[4] = var14.aFloat610 - var17.aFloat610;
                  var18[5] = var18[2];
                  var18[6] = var14.aFloat608 + var16.aFloat608;
                  var18[7] = var18[1];
                  var18[8] = var18[2];
                  var18[9] = var18[6];
                  var18[10] = var18[4];
                  var18[11] = var18[2];
                  int[] var10000;
                  byte var10001;
                  int var10002;
                  if(!var7) {
                     var19[0] = var9[0];
                     var19[2] = var9[0];
                     var19[4] = var9[0] + var9[2];
                     var10000 = var19;
                     var10001 = 6;
                     var10002 = var9[0] + var9[2];
                  } else {
                     var19[0] = var9[0] + var9[2];
                     var19[2] = var9[0] + var9[2];
                     var19[4] = var9[0];
                     var10000 = var19;
                     var10001 = 6;
                     var10002 = var9[0];
                  }

                  var10000[var10001] = var10002;
                  if(!var8) {
                     var19[1] = var9[1];
                     var19[3] = var9[1] + var9[3];
                     var19[5] = var9[1];
                     var10000 = var19;
                     var10001 = 7;
                     var10002 = var9[1] + var9[3];
                  } else {
                     var19[1] = var9[1] + var9[3];
                     var19[3] = var9[1];
                     var19[5] = var9[1] + var9[3];
                     var10000 = var19;
                     var10001 = 7;
                     var10002 = var9[1];
                  }

                  var10000[var10001] = var10002;
                  float var20 = 2.0F * var3.getPickX() - 1.0F;
                  float var21 = 1.0F - 2.0F * var3.getPickY();
                  if(var20 >= var18[0] && var20 <= var18[6] && var21 <= var18[1] && var21 >= var18[4]) {
                     if(!var3.testDistance(var15)) {
                        return false;
                     }

                     var20 -= var18[0];
                     var21 = var18[1] - var21;
                     float[] var22 = new float[]{0.0F};
                     float[] var23 = new float[]{0.0F};
                     float[] var29;
                     float var30;
                     if(!var7) {
                        var29 = var22;
                        var10001 = 0;
                        var30 = (float)var19[0] + (float)(var19[4] - var19[0]) * var20 / (var18[6] - var18[0]);
                     } else {
                        var29 = var22;
                        var10001 = 0;
                        var30 = (float)var19[0] - (float)(var19[0] - var19[4]) * var20 / (var18[6] - var18[0]);
                     }

                     var29[var10001] = var30;
                     if(!var8) {
                        var29 = var23;
                        var10001 = 0;
                        var30 = (float)var19[1] + (float)(var19[3] - var19[1]) * var21 / (var18[1] - var18[4]);
                     } else {
                        var29 = var23;
                        var10001 = 0;
                        var30 = (float)var19[1] - (float)(var19[1] - var19[3]) * var21 / (var18[1] - var18[4]);
                     }

                     var29[var10001] = var30;
                     int var24 = G3DUtils.method606(G3DUtils.method607(var22[0]), 0, this.image.getWidth() - 1);
                     int var25 = G3DUtils.method606(G3DUtils.method607(var23[0]), 0, this.image.getWidth() - 1);
                     var22[0] = G3DUtils.method605(var22[0], 0.0F, (float)this.image.getWidth());
                     var23[0] = G3DUtils.method605(var23[0], 0.0F, (float)this.image.getHeight());
                     int var26 = 0;
                     byte var27 = -1;
                     if(this.appearance.getCompositingMode() != null) {
                        var26 = (int)(this.appearance.getCompositingMode().getAlphaThreshold() * 256.0F);
                     }

                     label71: {
                        byte[] var28 = this.image.getImageData();
                        byte[] var31;
                        int var32;
                        byte var33;
                        switch(this.image.getFormat()) {
                        case 96:
                           var31 = var28;
                           var32 = var25 * this.image.getWidth() * 1 + var24 * 1;
                           var33 = 0;
                           break;
                        case 97:
                        case 99:
                        default:
                           break label71;
                        case 98:
                           var31 = var28;
                           var32 = var25 * this.image.getWidth() * 2 + var24 * 2;
                           var33 = 1;
                           break;
                        case 100:
                           var31 = var28;
                           var32 = var25 * this.image.getWidth() * 4 + var24 * 4;
                           var33 = 3;
                        }

                        var27 = var31[var32 + var33];
                     }

                     var22[0] /= (float)this.image.getWidth();
                     var23[0] /= (float)this.image.getHeight();
                     if((var27 & 255) >= var26) {
                        return var3.endPick(var15, var22, var23, 0, this, var15, (float[])null);
                     }
                  }

                  return false;
               } else {
                  return false;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
