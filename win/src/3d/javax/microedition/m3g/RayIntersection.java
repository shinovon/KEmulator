package javax.microedition.m3g;

import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Node;

public class RayIntersection {
   float[] aFloatArray1156 = new float[6];
   float[] aFloatArray1161;
   Node aNode1157;
   int anInt1158;
   float aFloat1159;
   float[] aFloatArray1164;
   float[] aFloatArray1166;
   private float aFloat1162;
   private Node aNode1163;
   private float aFloat1165;
   private float aFloat1167;
   private Camera aCamera1160;

   public RayIntersection() {
      this.aFloatArray1156[5] = 1.0F;
      this.aNode1157 = null;
      this.anInt1158 = 0;
      this.aFloat1159 = 0.0F;
      this.aFloatArray1161 = new float[3];
      this.aFloatArray1161[2] = 1.0F;
      this.aFloatArray1164 = new float[Graphics3D.NumTextureUnits];
      this.aFloatArray1166 = new float[Graphics3D.NumTextureUnits];
   }

   public Node getIntersected() {
      return this.aNode1157;
   }

   public void getRay(float[] var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var1.length < 6) {
         throw new IllegalArgumentException();
      } else {
         var1[0] = this.aFloatArray1156[0];
         var1[1] = this.aFloatArray1156[1];
         var1[2] = this.aFloatArray1156[2];
         var1[3] = this.aFloatArray1156[3] - this.aFloatArray1156[0];
         var1[4] = this.aFloatArray1156[4] - this.aFloatArray1156[1];
         var1[5] = this.aFloatArray1156[5] - this.aFloatArray1156[2];
      }
   }

   public float getDistance() {
      return this.aFloat1159;
   }

   public int getSubmeshIndex() {
      return this.anInt1158;
   }

   public float getTextureS(int var1) {
      if(var1 >= 0 && var1 < Graphics3D.NumTextureUnits) {
         return this.aFloatArray1164[var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public float getTextureT(int var1) {
      if(var1 >= 0 && var1 < Graphics3D.NumTextureUnits) {
         return this.aFloatArray1166[var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public float getNormalX() {
      return this.aFloatArray1161[0];
   }

   public float getNormalY() {
      return this.aFloatArray1161[1];
   }

   public float getNormalZ() {
      return this.aFloatArray1161[2];
   }

   protected Node getRoot() {
      return this.aNode1163;
   }

   protected Camera getCamera() {
      return this.aCamera1160;
   }

   protected float getPickX() {
      return this.aFloat1165;
   }

   protected float getPickY() {
      return this.aFloat1167;
   }

   protected void startPick(Node var1, float[] var2, float var3, float var4, Camera var5) {
      this.aNode1163 = var1;
      this.aFloatArray1156 = var2;
      this.aFloat1165 = var3;
      this.aFloat1167 = var4;
      this.aCamera1160 = var5;
      this.aFloat1162 = Float.MAX_VALUE;
   }

   protected boolean testDistance(float var1) {
      return var1 > 0.0F && var1 < this.aFloat1162;
   }

   protected boolean endPick(float var1, float[] var2, float[] var3, int var4, Node var5, float var6, float[] var7) {
      if(var1 > 0.0F && var1 < this.aFloat1162) {
         int var8;
         if(var2 != null && var3 != null) {
            for(var8 = 0; var8 < var2.length; ++var8) {
               this.aFloatArray1164[var8] = var2[var8];
               this.aFloatArray1166[var8] = var3[var8];
            }
         } else {
            for(var8 = 0; var8 < this.aFloatArray1164.length; ++var8) {
               this.aFloatArray1164[var8] = 0.0F;
               this.aFloatArray1166[var8] = 0.0F;
            }
         }

         float[] var10;
         byte var10001;
         float var10002;
         label26: {
            this.anInt1158 = var4;
            this.aFloat1159 = var6;
            this.aNode1157 = var5;
            RayIntersection var10000;
            if(var7 != null) {
               this.aFloatArray1161[0] = var7[0];
               this.aFloatArray1161[1] = var7[1];
               this.aFloatArray1161[2] = var7[2];
               float var9;
               if((var9 = (float)Math.sqrt((double)(var7[0] * var7[0] + var7[1] * var7[1] + var7[2] * var7[2]))) > 1.0E-5F) {
                  this.aFloatArray1161[0] /= var9;
                  this.aFloatArray1161[1] /= var9;
                  var10 = this.aFloatArray1161;
                  var10001 = 2;
                  var10002 = this.aFloatArray1161[2] / var9;
                  break label26;
               }

               var10000 = this;
            } else {
               var10000 = this;
            }

            var10000.aFloatArray1161[0] = 0.0F;
            this.aFloatArray1161[1] = 0.0F;
            var10 = this.aFloatArray1161;
            var10001 = 2;
            var10002 = 1.0F;
         }

         var10[var10001] = var10002;
         this.aFloat1162 = var1;
         return true;
      } else {
         return false;
      }
   }
}
