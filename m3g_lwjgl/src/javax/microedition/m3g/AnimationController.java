package javax.microedition.m3g;

public class AnimationController extends Object3D {
   private int activeIntervalStart = 0;
   private int activeIntervalEnd = 0;
   private int refWorldTime = 0;
   private float aFloat681 = 0.0F;
   private float weight = 1.0F;
   private float speed = 1.0F;

   public void setActiveInterval(int var1, int var2) {
      if(var1 > var2) {
         throw new IllegalArgumentException();
      } else {
         this.activeIntervalStart = var1;
         this.activeIntervalEnd = var2;
      }
   }

   public int getActiveIntervalStart() {
      return this.activeIntervalStart;
   }

   public int getActiveIntervalEnd() {
      return this.activeIntervalEnd;
   }

   public void setSpeed(float var1, int var2) {
      this.aFloat681 = this.getPosition(var2);
      this.refWorldTime = var2;
      this.speed = var1;
   }

   public float getSpeed() {
      return this.speed;
   }

   public void setPosition(float var1, int var2) {
      this.aFloat681 = var1;
      this.refWorldTime = var2;
   }

   public float getPosition(int var1) {
      return this.aFloat681 + this.speed * (float)(var1 - this.refWorldTime);
   }

   public int getRefWorldTime() {
      return this.refWorldTime;
   }

   public void setWeight(float var1) {
      if(var1 < 0.0F) {
         throw new IllegalArgumentException();
      } else {
         this.weight = var1;
      }
   }

   public float getWeight() {
      return this.weight;
   }

   protected boolean isActive(int var1) {
      return this.activeIntervalStart == this.activeIntervalEnd ?true:var1 >= this.activeIntervalStart && var1 < this.activeIntervalEnd;
   }

   protected int timeToActivation(int var1) {
      return var1 < this.activeIntervalStart ?this.activeIntervalStart - var1:(var1 >= this.activeIntervalEnd ?Integer.MAX_VALUE:0);
   }

   protected int timeToDeactivation(int var1) {
      return var1 < this.activeIntervalEnd ?this.activeIntervalEnd - var1:Integer.MAX_VALUE;
   }
}
