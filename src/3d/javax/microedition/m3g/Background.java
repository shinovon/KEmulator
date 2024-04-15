package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Object3D;

public class Background extends Object3D {
   public static final int BORDER = 32;
   public static final int REPEAT = 33;
   private boolean aBoolean36 = true;
   private boolean aBoolean190 = true;
   private int anInt37 = 0;
   private Image2D anImage2D157 = null;
   private int anInt39;
   private int anInt40;
   private int anInt150;
   private int anInt151;
   private int anInt152;
   private int anInt153;

   public Background() {
      this.anInt39 = this.anInt40 = 32;
   }

   public void setColorClearEnable(boolean var1) {
      this.aBoolean36 = var1;
   }

   public boolean isColorClearEnabled() {
      return this.aBoolean36;
   }

   public void setDepthClearEnable(boolean var1) {
      this.aBoolean190 = var1;
   }

   public boolean isDepthClearEnabled() {
      return this.aBoolean190;
   }

   public void setColor(int var1) {
      this.anInt37 = var1;
   }

   public int getColor() {
      return this.anInt37;
   }

   public void setImage(Image2D var1) {
      if(var1 != null && var1.getFormat() != 99 && var1.getFormat() != 100) {
         throw new IllegalArgumentException();
      } else {
         this.removeReference(this.anImage2D157);
         this.anImage2D157 = var1;
         this.addReference(this.anImage2D157);
         if(var1 != null) {
            this.anInt150 = 0;
            this.anInt151 = 0;
            this.anInt152 = var1.getWidth();
            this.anInt153 = var1.getHeight();
         }

      }
   }

   public Image2D getImage() {
      return this.anImage2D157;
   }

   public void setImageMode(int var1, int var2) {
      if(var1 != 32 && var1 != 33) {
         throw new IllegalArgumentException();
      } else if(var2 != 32 && var2 != 33) {
         throw new IllegalArgumentException();
      } else {
         this.anInt39 = var1;
         this.anInt40 = var2;
      }
   }

   public int getImageModeX() {
      return this.anInt39;
   }

   public int getImageModeY() {
      return this.anInt40;
   }

   public void setCrop(int var1, int var2, int var3, int var4) {
      if(var3 >= 0 && var4 >= 0) {
         this.anInt150 = var1;
         this.anInt151 = var2;
         this.anInt152 = var3;
         this.anInt153 = var4;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getCropX() {
      return this.anInt150;
   }

   public int getCropY() {
      return this.anInt151;
   }

   public int getCropWidth() {
      return this.anInt152;
   }

   public int getCropHeight() {
      return this.anInt153;
   }

   protected void updateProperty(int var1, float[] var2) {
      switch(var1) {
      case 256:
         this.anInt37 = this.anInt37 & 16777215 | G3DUtils.method603(var2) & -16777216;
         return;
      case 257:
      default:
         super.updateProperty(var1, var2);
         break;
      case 258:
         this.anInt37 = this.anInt37 & -16777216 | G3DUtils.method603(var2) & 16777215;
         return;
      case 259:
         this.anInt150 = G3DUtils.method607(var2[0]);
         this.anInt151 = G3DUtils.method607(var2[1]);
         if(var2.length > 2) {
            this.anInt152 = Math.max(G3DUtils.method607(var2[2]), 0);
            this.anInt153 = Math.max(G3DUtils.method607(var2[3]), 0);
            return;
         }
      }

   }
}
