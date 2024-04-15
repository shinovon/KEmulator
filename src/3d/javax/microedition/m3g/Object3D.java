package javax.microedition.m3g;

import java.util.Vector;
import javax.microedition.m3g.AnimationTrack;
import javax.microedition.m3g.Node;

public abstract class Object3D implements Cloneable {
   int anInt869 = 0;
   Object anObject870 = null;
   Vector aVector871 = new Vector();
   Vector aVector872 = new Vector();

   public final int animate(int var1) {
      int var2 = this.animation(var1);

      for(int var3 = 0; var3 < this.aVector872.size(); ++var3) {
         var2 = Math.min(var2, ((Object3D)this.aVector872.get(var3)).animate(var1));
      }

      return var2;
   }

   protected final int animation(int var1) {
      int var2 = Integer.MAX_VALUE;
      int var3 = 0;

      while(var3 < this.aVector871.size()) {
         AnimationTrack var4;
         int var5 = (var4 = (AnimationTrack)this.aVector871.elementAt(var3)).getTargetProperty();
         float[] var6 = new float[var4.getKeyframeSequence().getComponentCount()];
         float[] var7 = new float[2];
         float var8 = 0.0F;

         do {
            var4.getContribution(var1, var6, var7);
            var8 += var7[0];
            var2 = Math.min(var2, (int)var7[1]);
            ++var3;
         } while(var3 != this.aVector871.size() && (var4 = (AnimationTrack)this.aVector871.elementAt(var3)).getTargetProperty() == var5);

         if(var8 > 0.0F) {
            this.updateProperty(var5, var6);
         }
      }

      return var2;
   }

   protected void updateProperty(int var1, float[] var2) {
      throw new Error("Invalid animation target property!");
   }

   public final Object3D duplicate() {
      Object3D var1 = this.duplicateObject();
      if(this instanceof Node) {
         Node var2;
         (var2 = (Node)this).updateAlignReferences();
         var2.clearAlignReferences();
      }

      return var1;
   }

   protected Object3D duplicateObject() {
      Object3D var1 = null;

      try {
         (var1 = (Object3D)this.clone()).aVector872 = (Vector)this.aVector872.clone();
         var1.aVector871 = (Vector)this.aVector871.clone();
      } catch (Exception var2) {
         ;
      }

      return var1;
   }

   public Object3D find(int var1) {
      if(this.anInt869 == var1) {
         return this;
      } else {
         Object3D var2 = null;

         for(int var3 = 0; var3 < this.aVector872.size() && (var2 = ((Object3D)this.aVector872.get(var3)).find(var1)) == null; ++var3) {
            ;
         }

         return var2;
      }
   }

   public int getReferences(Object3D[] var1) {
      if(var1 != null && var1.length < this.getReferences((Object3D[])null)) {
         throw new IllegalArgumentException();
      } else {
         if(var1 != null) {
            for(int var2 = 0; var2 < this.aVector872.size(); ++var2) {
               var1[var2] = (Object3D)this.aVector872.get(var2);
            }
         }

         return this.aVector872.size();
      }
   }

   public void setUserID(int var1) {
      this.anInt869 = var1;
   }

   public int getUserID() {
      return this.anInt869;
   }

   public void setUserObject(Object var1) {
      this.anObject870 = var1;
   }

   public Object getUserObject() {
      return this.anObject870;
   }

   public void addAnimationTrack(AnimationTrack var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(!this.aVector871.contains(var1) && var1.checkCompatible(this)) {
         int var2 = var1.getKeyframeSequence().getComponentCount();
         int var3 = var1.getTargetProperty();

         for(int var4 = 0; var4 < this.aVector871.size(); ++var4) {
            AnimationTrack var5;
            if((var5 = (AnimationTrack)this.aVector871.get(var4)).getTargetProperty() > var3) {
               this.aVector871.insertElementAt(var1, var4);
               this.addReference(var1);
               return;
            }

            if(var5.getTargetProperty() == var3 && var5.getKeyframeSequence().getComponentCount() != var2) {
               throw new IllegalArgumentException();
            }
         }

         this.aVector871.addElement(var1);
         this.addReference(var1);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public AnimationTrack getAnimationTrack(int var1) {
      if(var1 >= 0 && var1 < this.aVector871.size()) {
         return (AnimationTrack)this.aVector871.elementAt(var1);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void removeAnimationTrack(AnimationTrack var1) {
      if(this.aVector871.contains(var1)) {
         this.aVector871.remove(var1);
         this.removeReference(var1);
      }

   }

   public int getAnimationTrackCount() {
      return this.aVector871.size();
   }

   protected void addReference(Object3D var1) {
      if(var1 != null) {
         this.aVector872.add(var1);
      }
   }

   protected void removeReference(Object3D var1) {
      if(var1 != null) {
         this.aVector872.remove(var1);
      }
   }
}
