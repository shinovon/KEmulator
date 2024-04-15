package javax.microedition.m3g;

import emulator.graphics3D.a;
import javax.microedition.m3g.Object3D;

public class KeyframeSequence extends Object3D {
   public static final int CONSTANT = 192;
   public static final int LOOP = 193;
   public static final int LINEAR = 176;
   public static final int SLERP = 177;
   public static final int SPLINE = 178;
   public static final int SQUAD = 179;
   public static final int STEP = 180;
   private int anInt37;
   private int anInt39;
   private int anInt40;
   private float[][] aFloatArrayArray144;
   private int[] anIntArray145;
   private int anInt150;
   private int anInt151;
   private int anInt152;
   private int anInt153;
   private boolean aBoolean36;
   private float[][] aFloatArrayArray147;
   private float[][] aFloatArrayArray149;
   private a[] anaArray146;
   private a[] anaArray148;

   protected Object3D duplicateObject() {
      KeyframeSequence var1;
      (var1 = (KeyframeSequence)super.duplicateObject()).anIntArray145 = (int[])this.anIntArray145.clone();
      var1.aFloatArrayArray144 = new float[this.anInt37][this.anInt39];
      int var2;
      if(this.anInt40 == 179) {
         for(var2 = 0; var2 < this.anaArray146.length; ++var2) {
            var1.anaArray146[var2] = new a(this.anaArray146[var2]);
            var1.anaArray148[var2] = new a(this.anaArray148[var2]);
         }

         for(var2 = 0; var2 < this.anInt37; ++var2) {
            var1.aFloatArrayArray144[var2] = (float[])this.aFloatArrayArray144[var2].clone();
         }
      } else if(this.anInt40 == 178) {
         var1.aFloatArrayArray147 = new float[this.anInt37][this.anInt39];
         var1.aFloatArrayArray149 = new float[this.anInt37][this.anInt39];

         for(var2 = 0; var2 < this.anInt37; ++var2) {
            var1.aFloatArrayArray147[var2] = (float[])this.aFloatArrayArray147[var2].clone();
            var1.aFloatArrayArray149[var2] = (float[])this.aFloatArrayArray149[var2].clone();
            var1.aFloatArrayArray144[var2] = (float[])this.aFloatArrayArray144[var2].clone();
         }
      }

      return var1;
   }

   public KeyframeSequence(int var1, int var2, int var3) {
      if(var1 >= 1 && var2 >= 1 && method14(var3)) {
         if((var3 == 177 || var3 == 179) && var2 != 4) {
            throw new IllegalArgumentException();
         } else {
            this.anInt37 = var1;
            this.anInt39 = var2;
            this.anInt40 = var3;
            this.aFloatArrayArray144 = new float[var1][var2];
            this.anIntArray145 = new int[var1];
            this.anInt153 = 192;
            this.anInt150 = 0;
            this.anInt151 = this.anInt37 - 1;
            this.anInt152 = 0;
            this.aBoolean36 = false;
            if(this.anInt40 == 178) {
               this.aFloatArrayArray147 = new float[var1][var2];
               this.aFloatArrayArray149 = new float[var1][var2];
            } else {
               if(this.anInt40 == 179) {
                  this.anaArray146 = new a[var1];
                  this.anaArray148 = new a[var1];

                  for(int var4 = 0; var4 < var1; ++var4) {
                     this.anaArray146[var4] = new a();
                     this.anaArray148[var4] = new a();
                  }
               }

            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static boolean method14(int var0) {
      return var0 >= 176 && var0 <= 180;
   }

   public int getComponentCount() {
      return this.anInt39;
   }

   public int getKeyframeCount() {
      return this.anInt37;
   }

   public int getInterpolationType() {
      return this.anInt40;
   }

   public void setKeyframe(int var1, int var2, float[] var3) {
      if(var3 == null) {
         throw new NullPointerException();
      } else if(var1 >= 0 && var1 < this.anInt37) {
         if(var2 >= 0 && var3.length >= this.anInt39) {
            this.anIntArray145[var1] = var2;
            float[] var4 = this.aFloatArrayArray144[var1];
            if(this.anInt40 != 177 && this.anInt40 != 179) {
               System.arraycopy(var3, 0, var4, 0, var4.length);
            } else {
               a var5;
               (var5 = new a(var3[0], var3[1], var3[2], var3[3])).method404();
               var4[0] = var5.aFloat603;
               var4[1] = var5.aFloat604;
               var4[2] = var5.aFloat605;
               var4[3] = var5.aFloat606;
            }

            this.aBoolean36 = false;
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getKeyframe(int var1, float[] var2) {
      if(var1 >= 0 && var1 < this.anInt37) {
         if(var2 != null && var2.length < this.anInt39) {
            throw new IllegalArgumentException();
         } else {
            if(var2 != null) {
               System.arraycopy(this.aFloatArrayArray144[var1], 0, var2, 0, this.anInt39);
            }

            return this.anIntArray145[var1];
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setValidRange(int var1, int var2) {
      if(var1 >= 0 && var1 < this.anInt37 && var2 >= 0 && var2 < this.anInt37) {
         this.anInt150 = var1;
         this.anInt151 = var2;
         this.aBoolean36 = false;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getValidRangeFirst() {
      return this.anInt150;
   }

   public int getValidRangeLast() {
      return this.anInt151;
   }

   public void setDuration(int var1) {
      this.anInt152 = var1;
   }

   public int getDuration() {
      return this.anInt152;
   }

   public void setRepeatMode(int var1) {
      if(var1 != 192 && var1 != 193) {
         throw new IllegalArgumentException();
      } else {
         this.anInt153 = var1;
         if(this.aBoolean36) {
            this.method122();
         }

      }
   }

   public int getRepeatMode() {
      return this.anInt153;
   }

   protected int getSampleFrame(float var1, float[] var2) {
      if(!this.aBoolean36) {
         this.method110();
      }

      float var4;
      if(this.anInt153 == 193) {
         if((var1 = var1 < 0.0F?var1 % (float)this.anInt152 + (float)this.anInt152:var1 % (float)this.anInt152) < (float)this.anIntArray145[this.anInt150]) {
            var1 += (float)this.anInt152;
         }
      } else {
         float[] var6;
         if(var1 < (float)this.anIntArray145[this.anInt150]) {
            System.arraycopy(var6 = this.aFloatArrayArray144[this.anInt150], 0, var2, 0, var6.length);
            if((var4 = (float)this.anIntArray145[this.anInt150] - var1) <= 2.14748365E9F) {
               return (int)var4;
            }

            return Integer.MAX_VALUE;
         }

         if(var1 >= (float)this.anIntArray145[this.anInt151]) {
            System.arraycopy(var6 = this.aFloatArrayArray144[this.anInt151], 0, var2, 0, var6.length);
            return Integer.MAX_VALUE;
         }
      }

      int var10000 = this.anInt150;

      while(true) {
         int var3 = var10000;
         if(var10000 == this.anInt151 || (float)this.anIntArray145[this.method13(var3)] > var1) {
            if(var1 - (float)this.anIntArray145[var3] >= 1.0E-5F && this.anInt40 != 180) {
               var4 = (var1 - (float)this.anIntArray145[var3]) / (float)this.method120(var3);
               int var5 = this.method13(var3);
               switch(this.anInt40) {
               case 176:
                  this.method111(var2, var4, var3, var5);
                  break;
               case 177:
                  this.method118(var2, var4, var3, var5);
                  break;
               case 178:
                  this.method114(var2, var4, var3, var5);
                  break;
               case 179:
                  this.method121(var2, var4, var3, var5);
                  break;
               default:
                  throw new Error("Invalid type for interpolation!");
               }

               return 1;
            } else {
               System.arraycopy(this.aFloatArrayArray144[var3], 0, var2, 0, this.anInt39);
               return this.anInt40 != 180?1:(int)((float)this.method120(var3) - (var1 - (float)this.anIntArray145[var3]));
            }
         }

         var10000 = this.method13(var3);
      }
   }

   private void method110() {
      if(this.anInt152 <= 0) {
         throw new IllegalStateException();
      } else {
         int var10000 = this.anInt150;

         while(true) {
            int var1 = var10000;
            if(var10000 == this.anInt151) {
               this.aBoolean36 = true;
               this.method122();
               return;
            }

            int var2 = var1 >= this.anIntArray145.length - 1?0:var1 + 1;
            if(this.anIntArray145[var2] < this.anIntArray145[var1] || this.anIntArray145[var2] > this.anInt152) {
               throw new IllegalStateException();
            }

            var10000 = var2;
         }
      }
   }

   private final void method111(float[] var1, float var2, int var3, int var4) {
      float[] var5 = this.aFloatArrayArray144[var3];
      float[] var6 = this.aFloatArrayArray144[var4];

      for(int var7 = 0; var7 < var1.length; ++var7) {
         var1[var7] = var5[var7] + var2 * (var6[var7] - var5[var7]);
      }

   }

   private final void method114(float[] var1, float var2, int var3, int var4) {
      float[] var5 = this.aFloatArrayArray144[var3];
      float[] var6 = this.aFloatArrayArray144[var4];

      for(int var7 = 0; var7 < var1.length; ++var7) {
         float var8 = this.aFloatArrayArray149[var3][var7];
         float var9 = this.aFloatArrayArray147[var4][var7];
         var1[var7] = method112(var2, var5[var7], var6[var7], var8, var9);
      }

   }

   private final void method115() {
      int var1 = this.anInt150;

      do {
         float[] var2 = this.aFloatArrayArray144[this.method117(var1)];
         float[] var3 = this.aFloatArrayArray144[this.method13(var1)];
         float var4 = this.method113(var1);
         float var5 = this.method116(var1);

         for(int var6 = 0; var6 < this.anInt39; ++var6) {
            this.aFloatArrayArray147[var1][var6] = 0.5F * (var3[var6] - var2[var6]) * var4;
            this.aFloatArrayArray149[var1][var6] = 0.5F * (var3[var6] - var2[var6]) * var5;
         }
      } while((var1 = this.method13(var1)) != this.anInt150);

   }

   private final void method118(float[] var1, float var2, int var3, int var4) {
      if(var1.length != 4) {
         throw new Error("Invalid keyframe type");
      } else {
         a var5 = new a(this.aFloatArrayArray144[var3]);
         a var6 = new a(this.aFloatArrayArray144[var4]);
         a var7;
         (var7 = new a()).method411(var2, var5, var6);
         var1[0] = var7.aFloat603;
         var1[1] = var7.aFloat604;
         var1[2] = var7.aFloat605;
         var1[3] = var7.aFloat606;
      }
   }

   private final void method121(float[] var1, float var2, int var3, int var4) {
      if(var1.length != 4) {
         throw new Error("Invalid keyframe type");
      } else {
         a var5 = new a(this.aFloatArrayArray144[var3]);
         a var6 = new a(this.aFloatArrayArray144[var4]);
         a var7;
         (var7 = new a()).method412(var2, var5, this.anaArray146[var3], this.anaArray148[var4], var6);
         var1[0] = var7.aFloat603;
         var1[1] = var7.aFloat604;
         var1[2] = var7.aFloat605;
         var1[3] = var7.aFloat606;
      }
   }

   private final void method119() {
      a var1 = new a();
      a var2 = new a();
      a var3 = new a();
      a var4 = new a();
      a var5 = new a();
      a var6 = new a();
      a var7 = new a();
      int var8 = this.anInt150;

      do {
         var3.method405(this.aFloatArrayArray144[this.method117(var8)]);
         var1.method405(this.aFloatArrayArray144[var8]);
         var2.method405(this.aFloatArrayArray144[this.method13(var8)]);
         var4.method405(this.aFloatArrayArray144[this.method13(this.method13(var8))]);
         var7.method410(var1, var2);
         var6.method410(var3, var1);
         var7.method417(var6);
         var7.method408(0.5F);
         var5.method406(var7);
         var5.method408(this.method116(var8));
         var6.method410(var1, var2);
         var5.method418(var6);
         var5.method408(0.5F);
         var6.method419(var5);
         this.anaArray146[var8].method406(var1);
         this.anaArray146[var8].method416(var6);
         var5.method406(var7);
         var5.method408(this.method113(var8));
         var6.method410(var3, var1);
         var6.method418(var5);
         var6.method408(0.5F);
         var6.method419(var6);
         this.anaArray148[var8].method406(var1);
         this.anaArray148[var8].method416(var6);
      } while((var8 = this.method13(var8)) != this.anInt150);

   }

   private static float method112(float var0, float var1, float var2, float var3, float var4) {
      float var5;
      float var6 = (var5 = var0 * var0) * var0;
      return (2.0F * var6 - 3.0F * var5 + 1.0F) * var1 + (-2.0F * var6 + 3.0F * var5) * var2 + (var6 - 2.0F * var5 + var0) * var3 + (var6 - var5) * var4;
   }

   private float method113(int var1) {
      if(this.anInt153 != 192 || var1 != this.anInt150 && var1 != this.anInt151) {
         int var2 = this.method117(var1);
         return 2.0F * (float)this.method120(var2) / (float)(this.method120(var1) + this.method120(var2));
      } else {
         return 0.0F;
      }
   }

   private float method116(int var1) {
      if(this.anInt153 != 192 || var1 != this.anInt150 && var1 != this.anInt151) {
         int var2 = this.method117(var1);
         return 2.0F * (float)this.method120(var1) / (float)(this.method120(var2) + this.method120(var1));
      } else {
         return 0.0F;
      }
   }

   private void method122() {
      if(!this.aBoolean36) {
         throw new Error();
      } else if(this.anInt40 == 178) {
         this.method115();
      } else {
         if(this.anInt40 == 179) {
            this.method119();
         }

      }
   }

   private int method13(int var1) {
      return var1 == this.anInt151?this.anInt150:(var1 == this.anIntArray145.length - 1?0:var1 + 1);
   }

   private int method117(int var1) {
      return var1 == this.anInt150?this.anInt151:(var1 == 0?this.anIntArray145.length - 1:var1 - 1);
   }

   private int method120(int var1) {
      return var1 == this.anInt151?this.anInt152 - this.anIntArray145[this.anInt151] + this.anIntArray145[this.anInt150]:this.anIntArray145[this.method13(var1)] - this.anIntArray145[var1];
   }
}
