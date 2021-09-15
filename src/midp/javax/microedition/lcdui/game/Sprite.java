package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;
import javax.microedition.lcdui.game.TiledLayer;

public class Sprite extends Layer {
   public static final int TRANS_NONE = 0;
   public static final int TRANS_ROT90 = 5;
   public static final int TRANS_ROT180 = 3;
   public static final int TRANS_ROT270 = 6;
   public static final int TRANS_MIRROR = 2;
   public static final int TRANS_MIRROR_ROT90 = 7;
   public static final int TRANS_MIRROR_ROT180 = 1;
   public static final int TRANS_MIRROR_ROT270 = 4;
   Image anImage265;
   int anInt269;
   int[] anIntArray266;
   int[] anIntArray267;
   int anInt270;
   int anInt271;
   int[] anIntArray268;
   private int anInt416;
   private boolean aBoolean407;
   int anInt272;
   int anInt273;
   int anInt274;
   int anInt408;
   int anInt409;
   int anInt410;
   int anInt411;
   int anInt412;
   int anInt413;
   int anInt414;
   int anInt415;

   public Sprite(Image var1) {
      super(var1.getWidth(), var1.getHeight());
      this.method201(var1, var1.getWidth(), var1.getHeight(), false);
      this.method202();
      this.method209(0);
   }

   public Sprite(Image var1, int var2, int var3) {
      super(var2, var3);
      if(var2 >= 1 && var3 >= 1 && var1.getWidth() % var2 == 0 && var1.getHeight() % var3 == 0) {
         this.method201(var1, var2, var3, false);
         this.method202();
         this.method209(0);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Sprite(Sprite var1) {
      super(var1 != null?var1.getWidth():0, var1 != null?var1.getHeight():0);
      if(var1 == null) {
         throw new NullPointerException();
      } else {
         this.anImage265 = Image.createImage(var1.anImage265);
         this.anInt269 = var1.anInt269;
         this.anIntArray266 = new int[this.anInt269];
         this.anIntArray267 = new int[this.anInt269];
         System.arraycopy(var1.anIntArray266, 0, this.anIntArray266, 0, var1.getRawFrameCount());
         System.arraycopy(var1.anIntArray267, 0, this.anIntArray267, 0, var1.getRawFrameCount());
         super.anInt598 = var1.getX();
         super.anInt600 = var1.getY();
         this.anInt272 = var1.anInt272;
         this.anInt273 = var1.anInt273;
         this.anInt274 = var1.anInt274;
         this.anInt408 = var1.anInt408;
         this.anInt409 = var1.anInt409;
         this.anInt410 = var1.anInt410;
         this.anInt270 = var1.anInt270;
         this.anInt271 = var1.anInt271;
         this.method209(var1.anInt411);
         this.setVisible(var1.isVisible());
         this.anIntArray268 = new int[var1.getFrameSequenceLength()];
         this.setFrameSequence(var1.anIntArray268);
         this.setFrame(var1.getFrame());
         this.setRefPixelPosition(var1.getRefPixelX(), var1.getRefPixelY());
      }
   }

   public void defineReferencePixel(int var1, int var2) {
      this.anInt272 = var1;
      this.anInt273 = var2;
   }

   public void setRefPixelPosition(int var1, int var2) {
      super.anInt598 = var1 - this.method206(this.anInt272, this.anInt273, this.anInt411);
      super.anInt600 = var2 - this.method208(this.anInt272, this.anInt273, this.anInt411);
   }

   public int getRefPixelX() {
      return super.anInt598 + this.method206(this.anInt272, this.anInt273, this.anInt411);
   }

   public int getRefPixelY() {
      return super.anInt600 + this.method208(this.anInt272, this.anInt273, this.anInt411);
   }

   public void setFrame(int var1) {
      if(var1 >= 0 && var1 < this.anIntArray268.length) {
         this.anInt416 = var1;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final int getFrame() {
      return this.anInt416;
   }

   public int getRawFrameCount() {
      return this.anInt269;
   }

   public int getFrameSequenceLength() {
      return this.anIntArray268.length;
   }

   public void nextFrame() {
      this.anInt416 = (this.anInt416 + 1) % this.anIntArray268.length;
   }

   public void prevFrame() {
      Sprite var10000;
      int var10001;
      if(this.anInt416 == 0) {
         var10000 = this;
         var10001 = this.anIntArray268.length;
      } else {
         var10000 = this;
         var10001 = this.anInt416;
      }

      var10000.anInt416 = var10001 - 1;
   }

   public final void paint(Graphics var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else {
         if(super.aBoolean599) {
            var1.drawRegion(this.anImage265, this.anIntArray266[this.anIntArray268[this.anInt416]], this.anIntArray267[this.anIntArray268[this.anInt416]], this.anInt270, this.anInt271, this.anInt411, super.anInt598, super.anInt600, 20);
         }

      }
   }

   public void setFrameSequence(int[] var1) {
      int var2;
      if(var1 == null) {
         this.anInt416 = 0;
         this.aBoolean407 = false;
         this.anIntArray268 = new int[this.anInt269];

         for(var2 = 0; var2 < this.anInt269; this.anIntArray268[var2] = var2++) {
            ;
         }

      } else if(var1.length < 1) {
         throw new IllegalArgumentException();
      } else {
         for(var2 = 0; var2 < var1.length; ++var2) {
            if(var1[var2] < 0 || var1[var2] >= this.anInt269) {
               throw new ArrayIndexOutOfBoundsException();
            }
         }

         this.aBoolean407 = true;
         this.anIntArray268 = new int[var1.length];
         System.arraycopy(var1, 0, this.anIntArray268, 0, var1.length);
         this.anInt416 = 0;
      }
   }

   public void setImage(Image var1, int var2, int var3) {
      if(var2 >= 1 && var3 >= 1 && var1.getWidth() % var2 == 0 && var1.getHeight() % var3 == 0) {
         int var4 = var1.getWidth() / var2 * (var1.getHeight() / var3);
         boolean var5 = true;
         if(var4 < this.anInt269) {
            var5 = false;
            this.aBoolean407 = false;
         }

         if(this.anInt270 == var2 && this.anInt271 == var3) {
            this.method201(var1, var2, var3, var5);
         } else {
            int var6 = super.anInt598 + this.method206(this.anInt272, this.anInt273, this.anInt411);
            int var7 = super.anInt600 + this.method208(this.anInt272, this.anInt273, this.anInt411);
            this.method335(var2);
            this.method336(var3);
            this.method201(var1, var2, var3, var5);
            this.method202();
            super.anInt598 = var6 - this.method206(this.anInt272, this.anInt273, this.anInt411);
            super.anInt600 = var7 - this.method208(this.anInt272, this.anInt273, this.anInt411);
            this.method210(this.anInt411);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void defineCollisionRectangle(int var1, int var2, int var3, int var4) {
      if(var3 >= 0 && var4 >= 0) {
         this.anInt274 = var1;
         this.anInt408 = var2;
         this.anInt409 = var3;
         this.anInt410 = var4;
         this.method209(this.anInt411);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setTransform(int var1) {
      this.method209(var1);
   }

   public final boolean collidesWith(Sprite var1, boolean var2) {
      if(var1.aBoolean599 && super.aBoolean599) {
         int var3 = var1.anInt598 + var1.anInt412;
         int var4 = var1.anInt600 + var1.anInt413;
         int var5 = var3 + var1.anInt414;
         int var6 = var4 + var1.anInt415;
         int var7 = super.anInt598 + this.anInt412;
         int var8 = super.anInt600 + this.anInt413;
         int var9 = var7 + this.anInt414;
         int var10 = var8 + this.anInt415;
         if(method203(var3, var4, var5, var6, var7, var8, var9, var10)) {
            if(var2) {
               if(this.anInt412 < 0) {
                  var7 = super.anInt598;
               }

               if(this.anInt413 < 0) {
                  var8 = super.anInt600;
               }

               if(this.anInt412 + this.anInt414 > super.anInt601) {
                  var9 = super.anInt598 + super.anInt601;
               }

               if(this.anInt413 + this.anInt415 > super.anInt602) {
                  var10 = super.anInt600 + super.anInt602;
               }

               if(var1.anInt412 < 0) {
                  var3 = var1.anInt598;
               }

               if(var1.anInt413 < 0) {
                  var4 = var1.anInt600;
               }

               if(var1.anInt412 + var1.anInt414 > var1.anInt601) {
                  var5 = var1.anInt598 + var1.anInt601;
               }

               if(var1.anInt413 + var1.anInt415 > var1.anInt602) {
                  var6 = var1.anInt600 + var1.anInt602;
               }

               if(!method203(var3, var4, var5, var6, var7, var8, var9, var10)) {
                  return false;
               } else {
                  int var11 = var7 < var3?var3:var7;
                  int var12 = var8 < var4?var4:var8;
                  int var13 = var9 < var5?var9:var5;
                  int var14 = var10 < var6?var10:var6;
                  int var15 = Math.abs(var13 - var11);
                  int var16 = Math.abs(var14 - var12);
                  int var17 = this.method205(var11, var12, var13, var14);
                  int var18 = this.method207(var11, var12, var13, var14);
                  int var19 = var1.method205(var11, var12, var13, var14);
                  int var20 = var1.method207(var11, var12, var13, var14);
                  return method204(var17, var18, var19, var20, this.anImage265, this.anInt411, var1.anImage265, var1.anInt411, var15, var16);
               }
            } else {
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public final boolean collidesWith(TiledLayer var1, boolean var2) {
      if(var1.aBoolean599 && super.aBoolean599) {
         int var3 = var1.anInt598;
         int var4 = var1.anInt600;
         int var5 = var3 + var1.anInt601;
         int var6 = var4 + var1.anInt602;
         int var7 = var1.getCellWidth();
         int var8 = var1.getCellHeight();
         int var9 = super.anInt598 + this.anInt412;
         int var10 = super.anInt600 + this.anInt413;
         int var11 = var9 + this.anInt414;
         int var12 = var10 + this.anInt415;
         int var13 = var1.getColumns();
         int var14 = var1.getRows();
         if(!method203(var3, var4, var5, var6, var9, var10, var11, var12)) {
            return false;
         } else {
            int var15 = var9 <= var3?0:(var9 - var3) / var7;
            int var16 = var10 <= var4?0:(var10 - var4) / var8;
            int var17 = var11 < var5?(var11 - 1 - var3) / var7:var13 - 1;
            int var18 = var12 < var6?(var12 - 1 - var4) / var8:var14 - 1;
            int var19;
            int var20;
            if(!var2) {
               for(var19 = var16; var19 <= var18; ++var19) {
                  for(var20 = var15; var20 <= var17; ++var20) {
                     if(var1.getCell(var20, var19) != 0) {
                        return true;
                     }
                  }
               }

               return false;
            } else {
               if(this.anInt412 < 0) {
                  var9 = super.anInt598;
               }

               if(this.anInt413 < 0) {
                  var10 = super.anInt600;
               }

               if(this.anInt412 + this.anInt414 > super.anInt601) {
                  var11 = super.anInt598 + super.anInt601;
               }

               if(this.anInt413 + this.anInt415 > super.anInt602) {
                  var12 = super.anInt600 + super.anInt602;
               }

               if(!method203(var3, var4, var5, var6, var9, var10, var11, var12)) {
                  return false;
               } else {
                  var15 = var9 <= var3?0:(var9 - var3) / var7;
                  var16 = var10 <= var4?0:(var10 - var4) / var8;
                  var17 = var11 < var5?(var11 - 1 - var3) / var7:var13 - 1;
                  var18 = var12 < var6?(var12 - 1 - var4) / var8:var14 - 1;
                  var20 = (var19 = var16 * var8 + var4) + var8;

                  for(int var21 = var16; var21 <= var18; var20 += var8) {
                     int var22;
                     int var23 = (var22 = var15 * var7 + var3) + var7;

                     for(int var24 = var15; var24 <= var17; var23 += var7) {
                        int var25;
                        if((var25 = var1.getCell(var24, var21)) != 0) {
                           int var26 = var9 < var22?var22:var9;
                           int var27 = var10 < var19?var19:var10;
                           int var28 = var11 < var23?var11:var23;
                           int var29 = var12 < var20?var12:var20;
                           int var30;
                           if(var26 > var28) {
                              var30 = var28;
                              var28 = var26;
                              var26 = var30;
                           }

                           if(var27 > var29) {
                              var30 = var29;
                              var29 = var27;
                              var27 = var30;
                           }

                           var30 = var28 - var26;
                           int var31 = var29 - var27;
                           int var32 = this.method205(var26, var27, var28, var29);
                           int var33 = this.method207(var26, var27, var28, var29);
                           int var34 = var1.anIntArray266[var25] + (var26 - var22);
                           int var35 = var1.anIntArray267[var25] + (var27 - var19);
                           if(method204(var32, var33, var34, var35, this.anImage265, this.anInt411, var1.anImage265, 0, var30, var31)) {
                              return true;
                           }
                        }

                        ++var24;
                        var22 += var7;
                     }

                     ++var21;
                     var19 += var8;
                  }

                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   public final boolean collidesWith(Image var1, int var2, int var3, boolean var4) {
      if(!super.aBoolean599) {
         return false;
      } else {
         int var7 = var2 + var1.getWidth();
         int var8 = var3 + var1.getHeight();
         int var9 = super.anInt598 + this.anInt412;
         int var10 = super.anInt600 + this.anInt413;
         int var11 = var9 + this.anInt414;
         int var12 = var10 + this.anInt415;
         if(method203(var2, var3, var7, var8, var9, var10, var11, var12)) {
            if(var4) {
               if(this.anInt412 < 0) {
                  var9 = super.anInt598;
               }

               if(this.anInt413 < 0) {
                  var10 = super.anInt600;
               }

               if(this.anInt412 + this.anInt414 > super.anInt601) {
                  var11 = super.anInt598 + super.anInt601;
               }

               if(this.anInt413 + this.anInt415 > super.anInt602) {
                  var12 = super.anInt600 + super.anInt602;
               }

               if(!method203(var2, var3, var7, var8, var9, var10, var11, var12)) {
                  return false;
               } else {
                  int var13 = var9 < var2?var2:var9;
                  int var14 = var10 < var3?var3:var10;
                  int var15 = var11 < var7?var11:var7;
                  int var16 = var12 < var8?var12:var8;
                  int var17 = Math.abs(var15 - var13);
                  int var18 = Math.abs(var16 - var14);
                  int var19 = this.method205(var13, var14, var15, var16);
                  int var20 = this.method207(var13, var14, var15, var16);
                  int var21 = var13 - var2;
                  int var22 = var14 - var3;
                  return method204(var19, var20, var21, var22, this.anImage265, this.anInt411, var1, 0, var17, var18);
               }
            } else {
               return true;
            }
         } else {
            return false;
         }
      }
   }

   private void method201(Image var1, int var2, int var3, boolean var4) {
      int var5 = var1.getWidth();
      int var6 = var1.getHeight();
      int var7 = var5 / var2;
      int var8 = var6 / var3;
      this.anImage265 = var1;
      this.anInt270 = var2;
      this.anInt271 = var3;
      this.anInt269 = var7 * var8;
      this.anIntArray266 = new int[this.anInt269];
      this.anIntArray267 = new int[this.anInt269];
      if(!var4) {
         this.anInt416 = 0;
      }

      if(!this.aBoolean407) {
         this.anIntArray268 = new int[this.anInt269];
      }

      int var9 = 0;
      int var10000 = 0;

      while(true) {
         int var10 = var10000;
         if(var10000 >= var6) {
            return;
         }

         var10000 = 0;

         while(true) {
            int var11 = var10000;
            if(var10000 >= var5) {
               var10000 = var10 + var3;
               break;
            }

            this.anIntArray266[var9] = var11;
            this.anIntArray267[var9] = var10;
            if(!this.aBoolean407) {
               this.anIntArray268[var9] = var9;
            }

            ++var9;
            var10000 = var11 + var2;
         }
      }
   }

   private void method202() {
      this.anInt274 = 0;
      this.anInt408 = 0;
      this.anInt409 = super.anInt601;
      this.anInt410 = super.anInt602;
   }

   private static boolean method203(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      return var4 < var2 && var5 < var3 && var6 > var0 && var7 > var1;
   }

   private static boolean method204(int var0, int var1, int var2, int var3, Image var4, int var5, Image var6, int var7, int var8, int var9) {
      int var10;
      int[] var11 = new int[var10 = var9 * var8];
      int[] var12 = new int[var10];
      int var13;
      int var14;
      int var15;
      int var10000;
      int[] var10001;
      byte var10002;
      int var10003;
      int var10004;
      int var10005;
      int var10006;
      int var10007;
      Image var25;
      if(0 != (var5 & 4)) {
         if(0 != (var5 & 1)) {
            var13 = -var9;
            var10000 = var10 - var9;
         } else {
            var13 = var9;
            var10000 = 0;
         }

         var14 = var10000;
         if(0 != (var5 & 2)) {
            var15 = -1;
            var14 += var9 - 1;
         } else {
            var15 = 1;
         }

         var25 = var4;
         var10001 = var11;
         var10002 = 0;
         var10003 = var9;
         var10004 = var0;
         var10005 = var1;
         var10006 = var9;
         var10007 = var8;
      } else {
         if(0 != (var5 & 1)) {
            var14 = var10 - var8;
            var10000 = -var8;
         } else {
            var14 = 0;
            var10000 = var8;
         }

         var15 = var10000;
         if(0 != (var5 & 2)) {
            var13 = -1;
            var14 += var8 - 1;
         } else {
            var13 = 1;
         }

         var25 = var4;
         var10001 = var11;
         var10002 = 0;
         var10003 = var8;
         var10004 = var0;
         var10005 = var1;
         var10006 = var8;
         var10007 = var9;
      }

      var25.getRGB(var10001, var10002, var10003, var10004, var10005, var10006, var10007);
      int var16;
      int var17;
      int var18;
      if(0 != (var7 & 4)) {
         if(0 != (var7 & 1)) {
            var16 = -var9;
            var10000 = var10 - var9;
         } else {
            var16 = var9;
            var10000 = 0;
         }

         var17 = var10000;
         if(0 != (var7 & 2)) {
            var18 = -1;
            var17 += var9 - 1;
         } else {
            var18 = 1;
         }

         var25 = var6;
         var10001 = var12;
         var10002 = 0;
         var10003 = var9;
         var10004 = var2;
         var10005 = var3;
         var10006 = var9;
         var10007 = var8;
      } else {
         if(0 != (var7 & 1)) {
            var17 = var10 - var8;
            var10000 = -var8;
         } else {
            var17 = 0;
            var10000 = var8;
         }

         var18 = var10000;
         if(0 != (var7 & 2)) {
            var16 = -1;
            var17 += var8 - 1;
         } else {
            var16 = 1;
         }

         var25 = var6;
         var10001 = var12;
         var10002 = 0;
         var10003 = var8;
         var10004 = var2;
         var10005 = var3;
         var10006 = var8;
         var10007 = var9;
      }

      var25.getRGB(var10001, var10002, var10003, var10004, var10005, var10006, var10007);
      int var19 = 0;
      int var20 = var14;

      for(int var21 = var17; var19 < var9; ++var19) {
         int var22 = 0;
         int var23 = var20;

         for(int var24 = var21; var22 < var8; ++var22) {
            if((var11[var23] & -16777216) != 0 && (var12[var24] & -16777216) != 0) {
               return true;
            }

            var23 += var13;
            var24 += var16;
         }

         var20 += var15;
         var21 += var18;
      }

      return false;
   }

   private int method205(int var1, int var2, int var3, int var4) {
      int var5 = 0;
      int var10000;
      int var10001;
      switch(this.anInt411) {
      case 0:
      case 1:
         var10000 = var1;
         var10001 = super.anInt598;
         break;
      case 2:
      case 3:
         var10000 = super.anInt598 + super.anInt601;
         var10001 = var3;
         break;
      case 4:
      case 5:
         var10000 = var2;
         var10001 = super.anInt600;
         break;
      case 6:
      case 7:
         var10000 = super.anInt600 + super.anInt602;
         var10001 = var4;
         break;
      default:
         return var5 + this.anIntArray266[this.anIntArray268[this.anInt416]];
      }

      var5 = var10000 - var10001;
      return var5 + this.anIntArray266[this.anIntArray268[this.anInt416]];
   }

   private int method207(int var1, int var2, int var3, int var4) {
      int var5 = 0;
      int var10000;
      int var10001;
      switch(this.anInt411) {
      case 0:
      case 2:
         var10000 = var2;
         var10001 = super.anInt600;
         break;
      case 1:
      case 3:
         var10000 = super.anInt600 + super.anInt602;
         var10001 = var4;
         break;
      case 4:
      case 6:
         var10000 = var1;
         var10001 = super.anInt598;
         break;
      case 5:
      case 7:
         var10000 = super.anInt598 + super.anInt601;
         var10001 = var3;
         break;
      default:
         return var5 + this.anIntArray267[this.anIntArray268[this.anInt416]];
      }

      var5 = var10000 - var10001;
      return var5 + this.anIntArray267[this.anIntArray268[this.anInt416]];
   }

   private void method209(int var1) {
      super.anInt598 = super.anInt598 + this.method206(this.anInt272, this.anInt273, this.anInt411) - this.method206(this.anInt272, this.anInt273, var1);
      super.anInt600 = super.anInt600 + this.method208(this.anInt272, this.anInt273, this.anInt411) - this.method208(this.anInt272, this.anInt273, var1);
      this.method210(var1);
      this.anInt411 = var1;
   }

   private void method210(int var1) {
      switch(var1) {
      case 0:
         this.anInt412 = this.anInt274;
         this.anInt413 = this.anInt408;
         this.anInt414 = this.anInt409;
         this.anInt415 = this.anInt410;
         super.anInt601 = this.anInt270;
         super.anInt602 = this.anInt271;
         return;
      case 1:
         this.anInt413 = this.anInt271 - (this.anInt408 + this.anInt410);
         this.anInt412 = this.anInt274;
         this.anInt414 = this.anInt409;
         this.anInt415 = this.anInt410;
         super.anInt601 = this.anInt270;
         super.anInt602 = this.anInt271;
         return;
      case 2:
         this.anInt412 = this.anInt270 - (this.anInt274 + this.anInt409);
         this.anInt413 = this.anInt408;
         this.anInt414 = this.anInt409;
         this.anInt415 = this.anInt410;
         super.anInt601 = this.anInt270;
         super.anInt602 = this.anInt271;
         return;
      case 3:
         this.anInt412 = this.anInt270 - (this.anInt409 + this.anInt274);
         this.anInt413 = this.anInt271 - (this.anInt410 + this.anInt408);
         this.anInt414 = this.anInt409;
         this.anInt415 = this.anInt410;
         super.anInt601 = this.anInt270;
         super.anInt602 = this.anInt271;
         return;
      case 4:
         this.anInt413 = this.anInt274;
         this.anInt412 = this.anInt408;
         this.anInt415 = this.anInt409;
         this.anInt414 = this.anInt410;
         super.anInt601 = this.anInt271;
         super.anInt602 = this.anInt270;
         return;
      case 5:
         this.anInt412 = this.anInt271 - (this.anInt410 + this.anInt408);
         this.anInt413 = this.anInt274;
         this.anInt415 = this.anInt409;
         this.anInt414 = this.anInt410;
         super.anInt601 = this.anInt271;
         super.anInt602 = this.anInt270;
         return;
      case 6:
         this.anInt412 = this.anInt408;
         this.anInt413 = this.anInt270 - (this.anInt409 + this.anInt274);
         this.anInt415 = this.anInt409;
         this.anInt414 = this.anInt410;
         super.anInt601 = this.anInt271;
         super.anInt602 = this.anInt270;
         return;
      case 7:
         this.anInt412 = this.anInt271 - (this.anInt410 + this.anInt408);
         this.anInt413 = this.anInt270 - (this.anInt409 + this.anInt274);
         this.anInt415 = this.anInt409;
         this.anInt414 = this.anInt410;
         super.anInt601 = this.anInt271;
         super.anInt602 = this.anInt270;
         return;
      default:
         throw new IllegalArgumentException();
      }
   }

   final int method206(int var1, int var2, int var3) {
      boolean var4 = false;
      int var5;
      switch(var3) {
      case 0:
         var5 = var1;
         break;
      case 1:
         var5 = var1;
         break;
      case 2:
         var5 = this.anInt270 - var1 - 1;
         break;
      case 3:
         var5 = this.anInt270 - var1 - 1;
         break;
      case 4:
         var5 = var2;
         break;
      case 5:
         var5 = this.anInt271 - var2 - 1;
         break;
      case 6:
         var5 = var2;
         break;
      case 7:
         var5 = this.anInt271 - var2 - 1;
         break;
      default:
         throw new IllegalArgumentException();
      }

      return var5;
   }

   final int method208(int var1, int var2, int var3) {
      boolean var4 = false;
      int var5;
      switch(var3) {
      case 0:
         var5 = var2;
         break;
      case 1:
         var5 = this.anInt271 - var2 - 1;
         break;
      case 2:
         var5 = var2;
         break;
      case 3:
         var5 = this.anInt271 - var2 - 1;
         break;
      case 4:
         var5 = var1;
         break;
      case 5:
         var5 = var1;
         break;
      case 6:
         var5 = this.anInt270 - var1 - 1;
         break;
      case 7:
         var5 = this.anInt270 - var1 - 1;
         break;
      default:
         throw new IllegalArgumentException();
      }

      return var5;
   }
}



/*
public class Sprite extends Layer
{
    public static final int TRANS_NONE = 0;
    public static final int TRANS_ROT90 = 5;
    public static final int TRANS_ROT180 = 3;
    public static final int TRANS_ROT270 = 6;
    public static final int TRANS_MIRROR = 2;
    public static final int TRANS_MIRROR_ROT90 = 7;
    public static final int TRANS_MIRROR_ROT180 = 1;
    public static final int TRANS_MIRROR_ROT270 = 4;
    Image anImage265;
    int anInt269;
    int[] anIntArray266;
    int[] anIntArray267;
    int anInt270;
    int anInt271;
    int[] anIntArray268;
    private int anInt416;
    private boolean aBoolean407;
    int anInt272;
    int anInt273;
    int anInt274;
    int anInt408;
    int anInt409;
    int anInt410;
    int anInt411;
    int anInt412;
    int anInt413;
    int anInt414;
    int anInt415;
    
    public Sprite(final Image image) {
        super(image.getWidth(), image.getHeight());
        this.method201(image, image.getWidth(), image.getHeight(), false);
        this.method202();
        this.method209(0);
    }
    
    public Sprite(final Image image, final int n, final int n2) {
        super(n, n2);
        if (n < 1 || n2 < 1 || image.getWidth() % n != 0 || image.getHeight() % n2 != 0) {
            throw new IllegalArgumentException();
        }
        this.method201(image, n, n2, false);
        this.method202();
        this.method209(0);
    }
    
    public Sprite(final Sprite sprite) {
        super((sprite != null) ? sprite.getWidth() : 0, (sprite != null) ? sprite.getHeight() : 0);
        if (sprite == null) {
            throw new NullPointerException();
        }
        this.anImage265 = Image.createImage(sprite.anImage265);
        this.anInt269 = sprite.anInt269;
        this.anIntArray266 = new int[this.anInt269];
        this.anIntArray267 = new int[this.anInt269];
        System.arraycopy(sprite.anIntArray266, 0, this.anIntArray266, 0, sprite.getRawFrameCount());
        System.arraycopy(sprite.anIntArray267, 0, this.anIntArray267, 0, sprite.getRawFrameCount());
        super.anInt598 = sprite.getX();
        super.anInt600 = sprite.getY();
        this.anInt272 = sprite.anInt272;
        this.anInt273 = sprite.anInt273;
        this.anInt274 = sprite.anInt274;
        this.anInt408 = sprite.anInt408;
        this.anInt409 = sprite.anInt409;
        this.anInt410 = sprite.anInt410;
        this.anInt270 = sprite.anInt270;
        this.anInt271 = sprite.anInt271;
        this.method209(sprite.anInt411);
        this.setVisible(sprite.isVisible());
        this.anIntArray268 = new int[sprite.getFrameSequenceLength()];
        this.setFrameSequence(sprite.anIntArray268);
        this.setFrame(sprite.getFrame());
        this.setRefPixelPosition(sprite.getRefPixelX(), sprite.getRefPixelY());
    }
    
    public void defineReferencePixel(final int anInt272, final int anInt273) {
        this.anInt272 = anInt272;
        this.anInt273 = anInt273;
    }
    
    public void setRefPixelPosition(final int n, final int n2) {
        super.anInt598 = n - this.method206(this.anInt272, this.anInt273, this.anInt411);
        super.anInt600 = n2 - this.method208(this.anInt272, this.anInt273, this.anInt411);
    }
    
    public int getRefPixelX() {
        return super.anInt598 + this.method206(this.anInt272, this.anInt273, this.anInt411);
    }
    
    public int getRefPixelY() {
        return super.anInt600 + this.method208(this.anInt272, this.anInt273, this.anInt411);
    }
    
    public void setFrame(final int anInt416) {
        if (anInt416 < 0 || anInt416 >= this.anIntArray268.length) {
            throw new IndexOutOfBoundsException();
        }
        this.anInt416 = anInt416;
    }
    
    public final int getFrame() {
        return this.anInt416;
    }
    
    public int getRawFrameCount() {
        return this.anInt269;
    }
    
    public int getFrameSequenceLength() {
        return this.anIntArray268.length;
    }
    
    public void nextFrame() {
        this.anInt416 = (this.anInt416 + 1) % this.anIntArray268.length;
    }
    
    public void prevFrame() {
        Sprite sprite;
        int n;
        if (this.anInt416 == 0) {
            sprite = this;
            n = this.anIntArray268.length;
        }
        else {
            sprite = this;
            n = this.anInt416;
        }
        sprite.anInt416 = n - 1;
    }
    
    public final void paint(final Graphics graphics) {
        if (graphics == null) {
            throw new NullPointerException();
        }
        if (super.aBoolean599) {
            graphics.drawRegion(this.anImage265, this.anIntArray266[this.anIntArray268[this.anInt416]], this.anIntArray267[this.anIntArray268[this.anInt416]], this.anInt270, this.anInt271, this.anInt411, super.anInt598, super.anInt600, 20);
        }
    }
    
    public void setFrameSequence(final int[] array) {
        if (array == null) {
            this.anInt416 = 0;
            this.aBoolean407 = false;
            this.anIntArray268 = new int[this.anInt269];
            for (int i = 0; i < this.anInt269; ++i) {
                this.anIntArray268[i] = i;
            }
            return;
        }
        if (array.length < 1) {
            throw new IllegalArgumentException();
        }
        for (int j = 0; j < array.length; ++j) {
            if (array[j] < 0 || array[j] >= this.anInt269) {
                throw new ArrayIndexOutOfBoundsException();
            }
        }
        this.aBoolean407 = true;
        System.arraycopy(array, 0, this.anIntArray268 = new int[array.length], 0, array.length);
        this.anInt416 = 0;
    }
    
    public void setImage(final Image image, final int n, final int n2) {
        if (n < 1 || n2 < 1 || image.getWidth() % n != 0 || image.getHeight() % n2 != 0) {
            throw new IllegalArgumentException();
        }
        final int n3 = image.getWidth() / n * (image.getHeight() / n2);
        boolean b = true;
        if (n3 < this.anInt269) {
            b = false;
            this.aBoolean407 = false;
        }
        if (this.anInt270 != n || this.anInt271 != n2) {
            final int n4 = super.anInt598 + this.method206(this.anInt272, this.anInt273, this.anInt411);
            final int n5 = super.anInt600 + this.method208(this.anInt272, this.anInt273, this.anInt411);
            this.method335(n);
            this.method336(n2);
            this.method201(image, n, n2, b);
            this.method202();
            super.anInt598 = n4 - this.method206(this.anInt272, this.anInt273, this.anInt411);
            super.anInt600 = n5 - this.method208(this.anInt272, this.anInt273, this.anInt411);
            this.method210(this.anInt411);
            return;
        }
        this.method201(image, n, n2, b);
    }
    
    public void defineCollisionRectangle(final int anInt274, final int anInt275, final int anInt276, final int anInt277) {
        if (anInt276 < 0 || anInt277 < 0) {
            throw new IllegalArgumentException();
        }
        this.anInt274 = anInt274;
        this.anInt408 = anInt275;
        this.anInt409 = anInt276;
        this.anInt410 = anInt277;
        this.method209(this.anInt411);
    }
    
    public void setTransform(final int n) {
        this.method209(n);
    }
    
    public final boolean collidesWith(final Sprite sprite, final boolean b) {
        if (!sprite.aBoolean599 || !super.aBoolean599) {
            return false;
        }
        int anInt598 = sprite.anInt598 + sprite.anInt412;
        int anInt599 = sprite.anInt600 + sprite.anInt413;
        int n = anInt598 + sprite.anInt414;
        int n2 = anInt599 + sprite.anInt415;
        int anInt600 = super.anInt598 + this.anInt412;
        int anInt601 = super.anInt600 + this.anInt413;
        int n3 = anInt600 + this.anInt414;
        int n4 = anInt601 + this.anInt415;
        if (!method203(anInt598, anInt599, n, n2, anInt600, anInt601, n3, n4)) {
            return false;
        }
        if (!b) {
            return true;
        }
        if (this.anInt412 < 0) {
            anInt600 = super.anInt598;
        }
        if (this.anInt413 < 0) {
            anInt601 = super.anInt600;
        }
        if (this.anInt412 + this.anInt414 > super.anInt601) {
            n3 = super.anInt598 + super.anInt601;
        }
        if (this.anInt413 + this.anInt415 > super.anInt602) {
            n4 = super.anInt600 + super.anInt602;
        }
        if (sprite.anInt412 < 0) {
            anInt598 = sprite.anInt598;
        }
        if (sprite.anInt413 < 0) {
            anInt599 = sprite.anInt600;
        }
        if (sprite.anInt412 + sprite.anInt414 > sprite.anInt601) {
            n = sprite.anInt598 + sprite.anInt601;
        }
        if (sprite.anInt413 + sprite.anInt415 > sprite.anInt602) {
            n2 = sprite.anInt600 + sprite.anInt602;
        }
        if (!method203(anInt598, anInt599, n, n2, anInt600, anInt601, n3, n4)) {
            return false;
        }
        final int n5 = (anInt600 < anInt598) ? anInt598 : anInt600;
        final int n6 = (anInt601 < anInt599) ? anInt599 : anInt601;
        final int n7 = (n3 < n) ? n3 : n;
        final int n8 = (n4 < n2) ? n4 : n2;
        return method204(this.method205(n5, n6, n7, n8), this.method207(n5, n6, n7, n8), sprite.method205(n5, n6, n7, n8), sprite.method207(n5, n6, n7, n8), this.anImage265, this.anInt411, sprite.anImage265, sprite.anInt411, Math.abs(n7 - n5), Math.abs(n8 - n6));
    }
    
    public final boolean collidesWith(final TiledLayer tiledLayer, final boolean b) {
        if (!tiledLayer.aBoolean599 || !super.aBoolean599) {
            return false;
        }
        final int anInt598 = tiledLayer.anInt598;
        final int anInt599 = tiledLayer.anInt600;
        final int n = anInt598 + tiledLayer.anInt601;
        final int n2 = anInt599 + tiledLayer.anInt602;
        final int cellWidth = tiledLayer.getCellWidth();
        final int cellHeight = tiledLayer.getCellHeight();
        int anInt600 = super.anInt598 + this.anInt412;
        int anInt601 = super.anInt600 + this.anInt413;
        int n3 = anInt600 + this.anInt414;
        int n4 = anInt601 + this.anInt415;
        final int columns = tiledLayer.getColumns();
        final int rows = tiledLayer.getRows();
        if (!method203(anInt598, anInt599, n, n2, anInt600, anInt601, n3, n4)) {
            return false;
        }
        final int n5 = (anInt600 <= anInt598) ? 0 : ((anInt600 - anInt598) / cellWidth);
        final int n6 = (anInt601 <= anInt599) ? 0 : ((anInt601 - anInt599) / cellHeight);
        final int n7 = (n3 < n) ? ((n3 - 1 - anInt598) / cellWidth) : (columns - 1);
        final int n8 = (n4 < n2) ? ((n4 - 1 - anInt599) / cellHeight) : (rows - 1);
        if (!b) {
            for (int i = n6; i <= n8; ++i) {
                for (int j = n5; j <= n7; ++j) {
                    if (tiledLayer.getCell(j, i) != 0) {
                        return true;
                    }
                }
            }
            return false;
        }
        if (this.anInt412 < 0) {
            anInt600 = super.anInt598;
        }
        if (this.anInt413 < 0) {
            anInt601 = super.anInt600;
        }
        if (this.anInt412 + this.anInt414 > super.anInt601) {
            n3 = super.anInt598 + super.anInt601;
        }
        if (this.anInt413 + this.anInt415 > super.anInt602) {
            n4 = super.anInt600 + super.anInt602;
        }
        if (!method203(anInt598, anInt599, n, n2, anInt600, anInt601, n3, n4)) {
            return false;
        }
        final int n9 = (anInt600 <= anInt598) ? 0 : ((anInt600 - anInt598) / cellWidth);
        final int n10 = (anInt601 <= anInt599) ? 0 : ((anInt601 - anInt599) / cellHeight);
        final int n11 = (n3 < n) ? ((n3 - 1 - anInt598) / cellWidth) : (columns - 1);
        int n14;
        for (int n12 = (n4 < n2) ? ((n4 - 1 - anInt599) / cellHeight) : (rows - 1), n13 = (n14 = n10 * cellHeight + anInt599) + cellHeight, k = n10; k <= n12; ++k, n14 += cellHeight, n13 += cellHeight) {
            int n16;
            for (int n15 = (n16 = n9 * cellWidth + anInt598) + cellWidth, l = n9; l <= n11; ++l, n16 += cellWidth, n15 += cellWidth) {
                final int cell;
                if ((cell = tiledLayer.getCell(l, k)) != 0) {
                    int n17 = (anInt600 < n16) ? n16 : anInt600;
                    int n18 = (anInt601 < n14) ? n14 : anInt601;
                    int n19 = (n3 < n15) ? n3 : n15;
                    int n20 = (n4 < n13) ? n4 : n13;
                    if (n17 > n19) {
                        final int n21 = n19;
                        n19 = n17;
                        n17 = n21;
                    }
                    if (n18 > n20) {
                        final int n22 = n20;
                        n20 = n18;
                        n18 = n22;
                    }
                    if (method204(this.method205(n17, n18, n19, n20), this.method207(n17, n18, n19, n20), tiledLayer.anIntArray266[cell] + (n17 - n16), tiledLayer.anIntArray267[cell] + (n18 - n14), this.anImage265, this.anInt411, tiledLayer.anImage265, 0, n19 - n17, n20 - n18)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public final boolean collidesWith(final Image image, final int n, final int n2, final boolean b) {
        if (!super.aBoolean599) {
            return false;
        }
        final int n3 = n + image.getWidth();
        final int n4 = n2 + image.getHeight();
        int anInt598 = super.anInt598 + this.anInt412;
        int anInt599 = super.anInt600 + this.anInt413;
        int n5 = anInt598 + this.anInt414;
        int n6 = anInt599 + this.anInt415;
        if (!method203(n, n2, n3, n4, anInt598, anInt599, n5, n6)) {
            return false;
        }
        if (!b) {
            return true;
        }
        if (this.anInt412 < 0) {
            anInt598 = super.anInt598;
        }
        if (this.anInt413 < 0) {
            anInt599 = super.anInt600;
        }
        if (this.anInt412 + this.anInt414 > super.anInt601) {
            n5 = super.anInt598 + super.anInt601;
        }
        if (this.anInt413 + this.anInt415 > super.anInt602) {
            n6 = super.anInt600 + super.anInt602;
        }
        if (!method203(n, n2, n3, n4, anInt598, anInt599, n5, n6)) {
            return false;
        }
        final int n7 = (anInt598 < n) ? n : anInt598;
        final int n8 = (anInt599 < n2) ? n2 : anInt599;
        final int n9 = (n5 < n3) ? n5 : n3;
        final int n10 = (n6 < n4) ? n6 : n4;
        return method204(this.method205(n7, n8, n9, n10), this.method207(n7, n8, n9, n10), n7 - n, n8 - n2, this.anImage265, this.anInt411, image, 0, Math.abs(n9 - n7), Math.abs(n10 - n8));
    }
    
    private void method201(final Image anImage265, final int anInt270, final int anInt271, final boolean b) {
        final int width = anImage265.getWidth();
        final int height = anImage265.getHeight();
        final int n = width / anInt270;
        final int n2 = height / anInt271;
        this.anImage265 = anImage265;
        this.anInt270 = anInt270;
        this.anInt271 = anInt271;
        this.anInt269 = n * n2;
        this.anIntArray266 = new int[this.anInt269];
        this.anIntArray267 = new int[this.anInt269];
        if (!b) {
            this.anInt416 = 0;
        }
        if (!this.aBoolean407) {
            this.anIntArray268 = new int[this.anInt269];
        }
        int n3 = 0;
        int n5;
        int n4 = n5 = 0;
        while (true) {
            final int n6 = n5;
            if (n4 >= height) {
                break;
            }
            int n8;
            int n7 = n8 = 0;
            while (true) {
                final int n9 = n8;
                if (n7 >= width) {
                    break;
                }
                this.anIntArray266[n3] = n9;
                this.anIntArray267[n3] = n6;
                if (!this.aBoolean407) {
                    this.anIntArray268[n3] = n3;
                }
                ++n3;
                n7 = (n8 = n9 + anInt270);
            }
            n4 = (n5 = n6 + anInt271);
        }
    }
    
    private void method202() {
        this.anInt274 = 0;
        this.anInt408 = 0;
        this.anInt409 = super.anInt601;
        this.anInt410 = super.anInt602;
    }
    
    private static boolean method203(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        return n5 < n3 && n6 < n4 && n7 > n && n8 > n2;
    }
    
    private static boolean method204(final int n, final int n2, final int n3, final int n4, final Image image, final int n5, final Image image2, final int n6, final int n7, final int n8) {
        final int n9;
        final int[] array = new int[n9 = n8 * n7];
        final int[] array2 = new int[n9];
        int n10;
        int n12;
        int n13;
        Image image3;
        int[] array3;
        int n14;
        int n15;
        int n16;
        int n17;
        int n18;
        int n19;
        if (0x0 != (n5 & 0x4)) {
            int n11;
            if (0x0 != (n5 & 0x1)) {
                n10 = -n8;
                n11 = n9 - n8;
            }
            else {
                n10 = n8;
                n11 = 0;
            }
            n12 = n11;
            if (0x0 != (n5 & 0x2)) {
                n13 = -1;
                n12 += n8 - 1;
            }
            else {
                n13 = 1;
            }
            image3 = image;
            array3 = array;
            n14 = 0;
            n15 = n8;
            n16 = n;
            n17 = n2;
            n18 = n8;
            n19 = n7;
        }
        else {
            int n20;
            if (0x0 != (n5 & 0x1)) {
                n12 = n9 - n7;
                n20 = -n7;
            }
            else {
                n12 = 0;
                n20 = n7;
            }
            n13 = n20;
            if (0x0 != (n5 & 0x2)) {
                n10 = -1;
                n12 += n7 - 1;
            }
            else {
                n10 = 1;
            }
            image3 = image;
            array3 = array;
            n14 = 0;
            n15 = n7;
            n16 = n;
            n17 = n2;
            n18 = n7;
            n19 = n8;
        }
        image3.getRGB(array3, n14, n15, n16, n17, n18, n19);
        int n21;
        int n23;
        int n24;
        Image image4;
        int[] array4;
        int n25;
        int n26;
        int n27;
        int n28;
        int n29;
        int n30;
        if (0x0 != (n6 & 0x4)) {
            int n22;
            if (0x0 != (n6 & 0x1)) {
                n21 = -n8;
                n22 = n9 - n8;
            }
            else {
                n21 = n8;
                n22 = 0;
            }
            n23 = n22;
            if (0x0 != (n6 & 0x2)) {
                n24 = -1;
                n23 += n8 - 1;
            }
            else {
                n24 = 1;
            }
            image4 = image2;
            array4 = array2;
            n25 = 0;
            n26 = n8;
            n27 = n3;
            n28 = n4;
            n29 = n8;
            n30 = n7;
        }
        else {
            int n31;
            if (0x0 != (n6 & 0x1)) {
                n23 = n9 - n7;
                n31 = -n7;
            }
            else {
                n23 = 0;
                n31 = n7;
            }
            n24 = n31;
            if (0x0 != (n6 & 0x2)) {
                n21 = -1;
                n23 += n7 - 1;
            }
            else {
                n21 = 1;
            }
            image4 = image2;
            array4 = array2;
            n25 = 0;
            n26 = n7;
            n27 = n3;
            n28 = n4;
            n29 = n7;
            n30 = n8;
        }
        image4.getRGB(array4, n25, n26, n27, n28, n29, n30);
        int i = 0;
        int n32 = n12;
        int n33 = n23;
        while (i < n8) {
            int j = 0;
            int n34 = n32;
            int n35 = n33;
            while (j < n7) {
                if ((array[n34] & 0xFF000000) != 0x0 && (array2[n35] & 0xFF000000) != 0x0) {
                    return true;
                }
                n34 += n10;
                n35 += n21;
                ++j;
            }
            n32 += n13;
            n33 += n24;
            ++i;
        }
        return false;
    }
    
    private int method205(final int n, final int n2, final int n3, final int n4) {
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        switch (this.anInt411) {
            case 0:
            case 1: {
                n6 = n;
                n7 = super.anInt598;
                break;
            }
            case 2:
            case 3: {
                n6 = super.anInt598 + super.anInt601;
                n7 = n3;
                break;
            }
            case 4:
            case 5: {
                n6 = n2;
                n7 = super.anInt600;
                break;
            }
            case 6:
            case 7: {
                n6 = super.anInt600 + super.anInt602;
                n7 = n4;
                break;
            }
            default: {
                return n5 + this.anIntArray266[this.anIntArray268[this.anInt416]];
            }
        }
        n5 = n6 - n7;
        return n5 + this.anIntArray266[this.anIntArray268[this.anInt416]];
    }
    
    private int method207(final int n, final int n2, final int n3, final int n4) {
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        switch (this.anInt411) {
            case 0:
            case 2: {
                n6 = n2;
                n7 = super.anInt600;
                break;
            }
            case 1:
            case 3: {
                n6 = super.anInt600 + super.anInt602;
                n7 = n4;
                break;
            }
            case 4:
            case 6: {
                n6 = n;
                n7 = super.anInt598;
                break;
            }
            case 5:
            case 7: {
                n6 = super.anInt598 + super.anInt601;
                n7 = n3;
                break;
            }
            default: {
                return n5 + this.anIntArray267[this.anIntArray268[this.anInt416]];
            }
        }
        n5 = n6 - n7;
        return n5 + this.anIntArray267[this.anIntArray268[this.anInt416]];
    }
    
    private void method209(final int anInt411) {
        super.anInt598 = super.anInt598 + this.method206(this.anInt272, this.anInt273, this.anInt411) - this.method206(this.anInt272, this.anInt273, anInt411);
        super.anInt600 = super.anInt600 + this.method208(this.anInt272, this.anInt273, this.anInt411) - this.method208(this.anInt272, this.anInt273, anInt411);
        this.method210(anInt411);
        this.anInt411 = anInt411;
    }
    
    private void method210(final int n) {
        switch (n) {
            case 0: {
                this.anInt412 = this.anInt274;
                this.anInt413 = this.anInt408;
                this.anInt414 = this.anInt409;
                this.anInt415 = this.anInt410;
                super.anInt601 = this.anInt270;
                super.anInt602 = this.anInt271;
            }
            case 2: {
                this.anInt412 = this.anInt270 - (this.anInt274 + this.anInt409);
                this.anInt413 = this.anInt408;
                this.anInt414 = this.anInt409;
                this.anInt415 = this.anInt410;
                super.anInt601 = this.anInt270;
                super.anInt602 = this.anInt271;
            }
            case 1: {
                this.anInt413 = this.anInt271 - (this.anInt408 + this.anInt410);
                this.anInt412 = this.anInt274;
                this.anInt414 = this.anInt409;
                this.anInt415 = this.anInt410;
                super.anInt601 = this.anInt270;
                super.anInt602 = this.anInt271;
            }
            case 5: {
                this.anInt412 = this.anInt271 - (this.anInt410 + this.anInt408);
                this.anInt413 = this.anInt274;
                this.anInt415 = this.anInt409;
                this.anInt414 = this.anInt410;
                super.anInt601 = this.anInt271;
                super.anInt602 = this.anInt270;
            }
            case 3: {
                this.anInt412 = this.anInt270 - (this.anInt409 + this.anInt274);
                this.anInt413 = this.anInt271 - (this.anInt410 + this.anInt408);
                this.anInt414 = this.anInt409;
                this.anInt415 = this.anInt410;
                super.anInt601 = this.anInt270;
                super.anInt602 = this.anInt271;
            }
            case 6: {
                this.anInt412 = this.anInt408;
                this.anInt413 = this.anInt270 - (this.anInt409 + this.anInt274);
                this.anInt415 = this.anInt409;
                this.anInt414 = this.anInt410;
                super.anInt601 = this.anInt271;
                super.anInt602 = this.anInt270;
            }
            case 7: {
                this.anInt412 = this.anInt271 - (this.anInt410 + this.anInt408);
                this.anInt413 = this.anInt270 - (this.anInt409 + this.anInt274);
                this.anInt415 = this.anInt409;
                this.anInt414 = this.anInt410;
                super.anInt601 = this.anInt271;
                super.anInt602 = this.anInt270;
            }
            case 4: {
                this.anInt413 = this.anInt274;
                this.anInt412 = this.anInt408;
                this.anInt415 = this.anInt409;
                this.anInt414 = this.anInt410;
                super.anInt601 = this.anInt271;
                super.anInt602 = this.anInt270;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    final int method206(final int n, final int n2, final int n3) {
        int n4 = 0;
        switch (n3) {
            case 0: {
                n4 = n;
                break;
            }
            case 2: {
                n4 = this.anInt270 - n - 1;
                break;
            }
            case 1: {
                n4 = n;
                break;
            }
            case 5: {
                n4 = this.anInt271 - n2 - 1;
                break;
            }
            case 3: {
                n4 = this.anInt270 - n - 1;
                break;
            }
            case 6: {
                n4 = n2;
                break;
            }
            case 7: {
                n4 = this.anInt271 - n2 - 1;
                break;
            }
            case 4: {
                n4 = n2;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return n4;
    }
    
    final int method208(final int n, final int n2, final int n3) {
        int n4 = 0;
        switch (n3) {
            case 0: {
                n4 = n2;
                break;
            }
            case 2: {
                n4 = n2;
                break;
            }
            case 1: {
                n4 = this.anInt271 - n2 - 1;
                break;
            }
            case 5: {
                n4 = n;
                break;
            }
            case 3: {
                n4 = this.anInt271 - n2 - 1;
                break;
            }
            case 6: {
                n4 = this.anInt270 - n - 1;
                break;
            }
            case 7: {
                n4 = this.anInt270 - n - 1;
                break;
            }
            case 4: {
                n4 = n;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return n4;
    }
}*/
