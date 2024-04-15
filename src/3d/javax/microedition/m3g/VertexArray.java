package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import javax.microedition.m3g.Object3D;

public class VertexArray extends Object3D {
   private int anInt37;
   private int anInt39;
   private int anInt40;
   private byte[] aByteArray35;
   private short[] aShortArray110;

   public VertexArray(int var1, int var2, int var3) {
      if((var1 >= 1 || var1 <= '\uffff') && (var2 >= 2 || var2 <= 4) && (var3 >= 1 || var3 <= 2)) {
         this.anInt39 = var1;
         this.anInt40 = var2;
         this.anInt37 = var3;
         if(this.anInt37 == 1) {
            this.aByteArray35 = new byte[var1 * var2];
         } else {
            this.aShortArray110 = new short[var1 * var2];
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected Object3D duplicateObject() {
      VertexArray var1 = (VertexArray)super.duplicateObject();
      if(this.anInt37 == 1) {
         var1.aByteArray35 = (byte[])this.aByteArray35.clone();
      } else {
         var1.aShortArray110 = (short[])this.aShortArray110.clone();
      }

      return var1;
   }

   public void set(int var1, int var2, short[] var3) {
      if(var3 == null) {
         throw new NullPointerException();
      } else if(this.anInt37 == 2 && var2 >= 0 && var3.length >= var2 * this.anInt40) {
         if(var1 >= 0 && var1 + var2 <= this.anInt39) {
            System.arraycopy(var3, 0, this.aShortArray110, var1 * this.anInt40, var2 * this.anInt40);
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void set(int var1, int var2, byte[] var3) {
      if(var3 == null) {
         throw new NullPointerException();
      } else if(this.anInt37 == 1 && var2 >= 0 && var3.length >= var2 * this.anInt40) {
         if(var1 >= 0 && var1 + var2 <= this.anInt39) {
            System.arraycopy(var3, 0, this.aByteArray35, var1 * this.anInt40, var2 * this.anInt40);
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getVertexCount() {
      return this.anInt39;
   }

   public int getComponentCount() {
      return this.anInt40;
   }

   public int getComponentType() {
      return this.anInt37;
   }

   public void get(int var1, int var2, short[] var3) {
      if(var3 == null) {
         throw new NullPointerException();
      } else if(this.anInt37 == 2 && var2 >= 0 && var3.length >= var2 * this.anInt40) {
         if(var1 >= 0 && var1 + var2 <= this.anInt39) {
            System.arraycopy(this.aShortArray110, var1 * this.anInt40, var3, 0, var2 * this.anInt40);
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void get(int var1, int var2, byte[] var3) {
      if(var3 == null) {
         throw new NullPointerException();
      } else if(this.anInt37 == 1 && var2 >= 0 && var3.length >= var2 * this.anInt40) {
         if(var1 >= 0 && var1 + var2 <= this.anInt39) {
            System.arraycopy(this.aByteArray35, var1 * this.anInt40, var3, 0, var2 * this.anInt40);
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public byte[] getByteValues() {
      return this.aByteArray35;
   }

   public short[] getShortValues() {
      return this.aShortArray110;
   }

   private boolean method93(VertexArray var1) {
      return var1 != null && this.anInt37 == var1.anInt37 && this.anInt40 == var1.anInt40 && this.anInt39 == var1.anInt39;
   }

   public void morph(VertexArray[] var1, VertexArray var2, float[] var3, float var4) {
      int var5;
      for(var5 = 0; var5 < var1.length; ++var5) {
         if(!this.method93(var1[var5])) {
            throw new IllegalStateException();
         }
      }

      float var6;
      int var7;
      if(this.anInt37 == 1) {
         for(var5 = 0; var5 < this.aByteArray35.length; ++var5) {
            var6 = 0.0F;

            for(var7 = 0; var7 < var1.length; ++var7) {
               var6 += (float)var1[var7].aByteArray35[var5] * var3[var7];
            }

            var6 += (float)var2.aByteArray35[var5] * var4;
            this.aByteArray35[var5] = (byte)G3DUtils.method607(var6);
         }
      } else {
         for(var5 = 0; var5 < this.aShortArray110.length; ++var5) {
            var6 = 0.0F;

            for(var7 = 0; var7 < var1.length; ++var7) {
               var6 += (float)var1[var7].aShortArray110[var5] * var3[var7];
            }

            var6 += (float)var2.aShortArray110[var5] * var4;
            this.aShortArray110[var5] = (short)G3DUtils.method607(var6);
         }
      }

   }

   public void morphColors(VertexArray[] var1, VertexArray var2, float[] var3, float var4) {
      int var5;
      for(var5 = 0; var5 < var1.length; ++var5) {
         if(!this.method93(var1[var5])) {
            throw new IllegalStateException();
         }
      }

      if(this.anInt37 == 1) {
         for(var5 = 0; var5 < this.aByteArray35.length; ++var5) {
            float var6 = 0.0F;

            for(int var7 = 0; var7 < var1.length; ++var7) {
               var6 += (float)((short)var1[var7].aByteArray35[var5] & 255) * var3[var7];
            }

            var6 += (float)(var2.aByteArray35[var5] & 255) * var4;
            this.aByteArray35[var5] = (byte)G3DUtils.method606((int)(var6 + 0.5F), 0, 255);
         }
      }

   }
}
