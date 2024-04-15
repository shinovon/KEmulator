package javax.microedition.m3g;

import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Object3D;

public class Image2D extends Object3D {
   public static final int ALPHA = 96;
   public static final int LUMINANCE = 97;
   public static final int LUMINANCE_ALPHA = 98;
   public static final int RGB = 99;
   public static final int RGBA = 100;
   private int anInt37;
   private int anInt39;
   private int anInt40;
   private byte[] aByteArray35;
   private boolean aBoolean36;
   private static byte[] aByteArray38;

   public Image2D(int var1, Object var2) {
      if(var2 == null) {
         throw new NullPointerException();
      } else if(!method14(var1)) {
         throw new IllegalArgumentException();
      } else if(var2 instanceof Image) {
         Image var3 = (Image)var2;
         this.anInt39 = var3.getWidth();
         this.anInt40 = var3.getHeight();
         this.aBoolean36 = false;
         this.anInt37 = var1;
         this.aByteArray35 = method15(var1, var3.getImpl().getData(), var3.isMutable());
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Image2D(int var1, int var2, int var3, byte[] var4) {
      if(var4 == null) {
         throw new NullPointerException();
      } else if(var2 > 0 && var3 > 0 && method14(var1)) {
         int var5 = var2 * var3 * method13(var1);
         if(var4.length < var5) {
            throw new IllegalArgumentException();
         } else {
            this.anInt39 = var2;
            this.anInt40 = var3;
            this.aBoolean36 = false;
            this.anInt37 = var1;
            this.aByteArray35 = new byte[var5];
            System.arraycopy(var4, 0, this.aByteArray35, 0, var5);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Image2D(int var1, int var2, int var3, byte[] var4, byte[] var5) {
      if(var4 != null && var5 != null) {
         int var6 = var2 * var3;
         if(var2 > 0 && var3 > 0 && method14(var1) && var4.length >= var6) {
            int var7 = method13(var1);
            if(var5.length < 256 * var7 && var5.length % var7 != 0) {
               throw new IllegalArgumentException();
            } else {
               this.anInt39 = var2;
               this.anInt40 = var3;
               this.aBoolean36 = false;
               this.anInt37 = var1;
               this.aByteArray35 = new byte[var6 * var7];

               for(int var8 = 0; var8 < var6; ++var8) {
                  System.arraycopy(var5, (var4[var8] & 255) * var7, this.aByteArray35, var8 * var7, var7);
               }

            }
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new NullPointerException();
      }
   }

   public Image2D(int var1, int var2, int var3) {
      if(var2 > 0 && var3 > 0 && method14(var1)) {
         this.anInt39 = var2;
         this.anInt40 = var3;
         this.aBoolean36 = true;
         this.anInt37 = var1;
         int var4 = var2 * method13(var1);
         this.aByteArray35 = new byte[var4 * var3];
         int var5;
         if(aByteArray38 == null || aByteArray38.length < var4) {
            aByteArray38 = new byte[var4];

            for(var5 = var4 - 1; var5 >= 0; --var5) {
               aByteArray38[var5] = -1;
            }
         }

         for(var5 = 0; var5 < var3; ++var5) {
            System.arraycopy(aByteArray38, 0, this.aByteArray35, var5 * var4, var4);
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   public void set(int var1, int var2, int var3, int var4, byte[] var5) {
      if(var5 == null) {
         throw new NullPointerException();
      } else if(this.aBoolean36 && var1 >= 0 && var2 >= 0 && var3 > 0 && var4 > 0 && var1 + var3 <= this.anInt39 && var2 + var4 <= this.anInt40) {
         int var6 = this.getBpp();
         if(var5.length < var3 * var4 * var6) {
            throw new IllegalArgumentException();
         } else {
            for(int var7 = 0; var7 < var4; ++var7) {
               System.arraycopy(var5, var7 * var3 * var6, this.aByteArray35, ((var2 + var7) * this.anInt39 + var1) * var6, var3 * var6);
            }

         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public boolean isMutable() {
      return this.aBoolean36;
   }

   public int getFormat() {
      return this.anInt37;
   }

   public int getWidth() {
      return this.anInt39;
   }

   public int getHeight() {
      return this.anInt40;
   }

   public int getBpp() {
      return method13(this.anInt37);
   }

   private static int method13(int var0) {
      switch(var0) {
      case 96:
      case 97:
         return 1;
      case 98:
         return 2;
      case 99:
         return 3;
      case 100:
         return 4;
      default:
         throw new IllegalArgumentException();
      }
   }

   private static boolean method14(int var0) {
      return var0 >= 96 && var0 <= 100;
   }

   public final byte[] getImageData() {
      return this.aByteArray35;
   }

   private static byte[] method15(int var0, int[] var1, boolean var2) {
      byte[] var3 = null;
      int var4 = var1.length;
      int var5;
      if(var2) {
         switch(var0) {
         case 96:
         case 97:
            var3 = new byte[var4];

            for(var5 = var4 - 1; var5 >= 0; --var5) {
               var3[var5] = (byte)(((var1[var5] >> 16 & 255) + (var1[var5] >> 8 & 255) + (var1[var5] & 255)) / 3 & 255);
            }

            return var3;
         case 98:
            var3 = new byte[var4 * 2];

            for(var5 = var4 - 1; var5 >= 0; --var5) {
               var3[var5 * 2] = (byte)(((var1[var5] >> 16 & 255) + (var1[var5] >> 8 & 255) + (var1[var5] & 255)) / 3 & 255);
               var3[var5 * 2 + 1] = -1;
            }

            return var3;
         case 99:
            var3 = new byte[var4 * 3];

            for(var5 = var4 - 1; var5 >= 0; --var5) {
               var3[var5 * 3] = (byte)(var1[var5] >> 16 & 255);
               var3[var5 * 3 + 1] = (byte)(var1[var5] >> 8 & 255);
               var3[var5 * 3 + 2] = (byte)(var1[var5] & 255);
            }

            return var3;
         case 100:
            var3 = new byte[var4 * 4];

            for(var5 = var4 - 1; var5 >= 0; --var5) {
               var3[var5 * 4] = (byte)(var1[var5] >> 16 & 255);
               var3[var5 * 4 + 1] = (byte)(var1[var5] >> 8 & 255);
               var3[var5 * 4 + 2] = (byte)(var1[var5] & 255);
               var3[var5 * 4 + 3] = -1;
            }
         }
      } else {
         switch(var0) {
         case 96:
            var3 = new byte[var4];

            for(var5 = var4 - 1; var5 >= 0; --var5) {
               var3[var5] = (byte)(var1[var5] >> 24 & 255);
            }

            return var3;
         case 97:
            var3 = new byte[var4];

            for(var5 = var4 - 1; var5 >= 0; --var5) {
               var3[var5] = (byte)(((var1[var5] >> 16 & 255) + (var1[var5] >> 8 & 255) + (var1[var5] & 255)) / 3 & 255);
            }

            return var3;
         case 98:
            var3 = new byte[var4 * 2];

            for(var5 = var4 - 1; var5 >= 0; --var5) {
               var3[var5 * 2] = (byte)(((var1[var5] >> 16 & 255) + (var1[var5] >> 8 & 255) + (var1[var5] & 255)) / 3 & 255);
               var3[var5 * 2 + 1] = (byte)(var1[var5] >> 24 & 255);
            }

            return var3;
         case 99:
            var3 = new byte[var4 * 3];

            for(var5 = var4 - 1; var5 >= 0; --var5) {
               var3[var5 * 3] = (byte)(var1[var5] >> 16 & 255);
               var3[var5 * 3 + 1] = (byte)(var1[var5] >> 8 & 255);
               var3[var5 * 3 + 2] = (byte)(var1[var5] & 255);
            }

            return var3;
         case 100:
            var3 = new byte[var4 * 4];

            for(var5 = var4 - 1; var5 >= 0; --var5) {
               var3[var5 * 4] = (byte)(var1[var5] >> 16 & 255);
               var3[var5 * 4 + 1] = (byte)(var1[var5] >> 8 & 255);
               var3[var5 * 4 + 2] = (byte)(var1[var5] & 255);
               var3[var5 * 4 + 3] = (byte)(var1[var5] >> 24 & 255);
            }
         }
      }

      return var3;
   }

   protected Object3D duplicateObject() {
      Image2D var1;
      (var1 = (Image2D)super.duplicateObject()).aByteArray35 = (byte[])this.aByteArray35.clone();
      return var1;
   }
}
