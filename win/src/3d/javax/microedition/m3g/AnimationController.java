package javax.microedition.m3g;

import javax.microedition.m3g.Object3D;

public class AnimationController extends Object3D {
   private int anInt37 = 0;
   private int anInt39 = 0;
   private int anInt40 = 0;
   private float aFloat681 = 0.0F;
   private float aFloat682 = 1.0F;
   private float aFloat683 = 1.0F;

   public void setActiveInterval(int var1, int var2) {
      if(var1 > var2) {
         throw new IllegalArgumentException();
      } else {
         this.anInt37 = var1;
         this.anInt39 = var2;
      }
   }

   public int getActiveIntervalStart() {
      return this.anInt37;
   }

   public int getActiveIntervalEnd() {
      return this.anInt39;
   }

   public void setSpeed(float var1, int var2) {
      this.aFloat681 = this.getPosition(var2);
      this.anInt40 = var2;
      this.aFloat683 = var1;
   }

   public float getSpeed() {
      return this.aFloat683;
   }

   public void setPosition(float var1, int var2) {
      this.aFloat681 = var1;
      this.anInt40 = var2;
   }

   public float getPosition(int var1) {
      return this.aFloat681 + this.aFloat683 * (float)(var1 - this.anInt40);
   }

   public int getRefWorldTime() {
      return this.anInt40;
   }

   public void setWeight(float var1) {
      if(var1 < 0.0F) {
         throw new IllegalArgumentException();
      } else {
         this.aFloat682 = var1;
      }
   }

   public float getWeight() {
      return this.aFloat682;
   }

   protected boolean isActive(int var1) {
      return this.anInt37 == this.anInt39?true:var1 >= this.anInt37 && var1 < this.anInt39;
   }

   protected int timeToActivation(int var1) {
      return var1 < this.anInt37?this.anInt37 - var1:(var1 >= this.anInt39?Integer.MAX_VALUE:0);
   }

   protected int timeToDeactivation(int var1) {
      return var1 < this.anInt39?this.anInt39 - var1:Integer.MAX_VALUE;
   }
}
