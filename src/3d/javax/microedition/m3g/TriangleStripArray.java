package javax.microedition.m3g;

public class TriangleStripArray extends IndexBuffer {
   private int[] anIntArray1009;

   protected Object3D duplicateObject() {
      TriangleStripArray var1;
      (var1 = (TriangleStripArray)super.duplicateObject()).indices = (int[])super.indices.clone();
      var1.anIntArray1009 = (int[])this.anIntArray1009.clone();
      return var1;
   }

   public int getStripCount() {
      return this.anIntArray1009.length;
   }

   public int[] getIndexStrip(int var1) {
      if(var1 >= 0 && var1 < this.anIntArray1009.length) {
         int var2 = 0;

         for(int var3 = 0; var3 < var1; ++var3) {
            var2 += this.anIntArray1009[var3];
         }

         int[] var4 = new int[this.anIntArray1009[var1]];
         if(this.anIntArray1009 != null) {
            System.arraycopy(super.indices, var2, var4, 0, this.anIntArray1009[var1]);
         }

         return var4;
      } else {
         return null;
      }
   }

   protected boolean getIndices(int var1, int[] var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < this.anIntArray1009.length; ++var4) {
         if(var1 < this.anIntArray1009[var4] - 2) {
            var2[0] = super.indices[var3 + var1 + 0];
            var2[1] = super.indices[var3 + var1 + 1];
            var2[2] = super.indices[var3 + var1 + 2];
            var2[3] = var1 & 1;
            return true;
         }

         var1 -= this.anIntArray1009[var4] - 2;
         var3 += this.anIntArray1009[var4];
      }

      return false;
   }

   public TriangleStripArray(int var1, int[] var2) {
      if(var2 == null) {
         throw new NullPointerException();
      } else if(var2.length == 0) {
         throw new IllegalArgumentException();
      } else {
         int var3 = 0;

         int var4;
         for(var4 = var2.length - 1; var4 >= 0; --var4) {
            if(var2[var4] < 3) {
               throw new IllegalArgumentException();
            }

            var3 += var2[var4];
         }

         if(var1 + var3 > '\uffff') {
            throw new IllegalArgumentException();
         } else {
            super.indices = new int[var3];

            for(var4 = 0; var4 < var3; ++var4) {
               super.indices[var4] = var1 + var4;
            }

            this.anIntArray1009 = new int[var2.length];
            System.arraycopy(var2, 0, this.anIntArray1009, 0, var2.length);
         }
      }
   }

   public TriangleStripArray(int[] var1, int[] var2) {
      if(var1 != null && var2 != null) {
         if(var2.length != 0 && var1.length >= 3) {
            int var3 = 0;

            int var4;
            for(var4 = var2.length - 1; var4 >= 0; --var4) {
               if(var2[var4] < 3 || var2[var4] > '\uffff') {
                  throw new IllegalArgumentException();
               }

               var3 += var2[var4];
            }

            if(var1.length < var3) {
               throw new IllegalArgumentException();
            } else {
               for(var4 = var3 - 1; var4 >= 0; --var4) {
                  if(var1[var4] < 0 || var1[var4] > '\uffff') {
                     throw new IllegalArgumentException();
                  }
               }

               super.indices = new int[var3];
               System.arraycopy(var1, 0, super.indices, 0, var3);
               this.anIntArray1009 = new int[var2.length];
               System.arraycopy(var2, 0, this.anIntArray1009, 0, var2.length);
            }
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new NullPointerException();
      }
   }
}
