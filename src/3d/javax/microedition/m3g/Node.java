package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.a;
import emulator.graphics3D.b;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.RayIntersection;
import javax.microedition.m3g.SkinnedMesh;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.Transformable;

public abstract class Node extends Transformable {
   public static final int NONE = 144;
   public static final int ORIGIN = 145;
   public static final int X_AXIS = 146;
   public static final int Y_AXIS = 147;
   public static final int Z_AXIS = 148;
   Node aNode1303 = null;
   private boolean aBoolean36 = true;
   private boolean aBoolean190 = true;
   private float aFloat681 = 1.0F;
   private int anInt37 = -1;
   private int anInt39;
   private int anInt40;
   private Node aNode1304;
   private Node aNode1305;
   private boolean aBoolean684;
   protected Node m_duplicatedNode;

   Node() {
      this.anInt39 = this.anInt40 = 144;
      this.aNode1304 = this.aNode1305 = null;
      this.aBoolean684 = false;
   }

   public void setRenderingEnable(boolean var1) {
      this.aBoolean36 = var1;
   }

   public void setPickingEnable(boolean var1) {
      this.aBoolean190 = var1;
   }

   public void setScope(int var1) {
      this.anInt37 = var1;
   }

   public void setAlphaFactor(float var1) {
      if(var1 >= 0.0F && var1 <= 1.0F) {
         this.aFloat681 = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public boolean isRenderingEnabled() {
      return this.aBoolean36;
   }

   public boolean isPickingEnabled() {
      return this.aBoolean190;
   }

   public int getScope() {
      return this.anInt37;
   }

   public float getAlphaFactor() {
      return this.aFloat681;
   }

   public Node getParent() {
      return this.aNode1303;
   }

   public boolean getTransformTo(Node var1, Transform var2) {
      if(var1 != null && var2 != null) {
         Transform var3 = new Transform();
         Transform var4 = new Transform();
         Transform var5 = new Transform();
         Node var6 = this;
         Node var7 = var1;
         if(this.getRoot() != var1.getRoot()) {
            return false;
         } else {
            int var8 = var1.getDepth();

            int var9;
            for(var9 = this.getDepth(); var9 > var8; var6 = var6.aNode1303) {
               var6.getCompositeTransform(var5);
               var4.preMultiply(var5);
               --var9;
            }

            while(var8 > var9) {
               var7.getCompositeTransform(var5);
               var3.preMultiply(var5);
               --var8;
               var7 = var7.aNode1303;
            }

            while(var6 != var7) {
               var6.getCompositeTransform(var5);
               var4.preMultiply(var5);
               var6 = var6.aNode1303;
               var7.getCompositeTransform(var5);
               var3.preMultiply(var5);
               var7 = var7.aNode1303;
            }

            var3.getImpl().method445();
            var3.postMultiply(var4);
            var2.set(var3);
            return true;
         }
      } else {
         throw new NullPointerException();
      }
   }

   protected int getDepth() {
      Node var1 = this;

      int var2;
      for(var2 = 0; var1.aNode1303 != null; ++var2) {
         var1 = var1.aNode1303;
      }

      return var2;
   }

   protected boolean isParentOf(Node var1) {
      Node var10000 = var1;

      while(true) {
         Node var2 = var10000;
         if(var10000 == null) {
            return false;
         }

         if(this.equals(var2)) {
            return true;
         }

         var10000 = var2.getParent();
      }
   }

   protected boolean isDescendantOf(Node var1) {
      Node var10000 = this.aNode1303;

      while(true) {
         Node var2 = var10000;
         if(var10000 == null) {
            return false;
         }

         if(var1.equals(var2)) {
            return true;
         }

         var10000 = var2.getParent();
      }
   }

   public final void align(Node var1) {
      this.alignment(var1);
   }

   protected void alignment(Node var1) {
      Node var10000;
      Node var10001;
      if(var1 == null) {
         var10000 = this;
         var10001 = this;
      } else {
         var10000 = this;
         var10001 = var1;
      }

      var10000.computeAlignment(var10001, (b)null, (b)null, (b)null, (b)null);
   }

   protected void computeAlignment(Node var1, b var2, b var3, b var4, b var5) {
      Node var6 = this.getRoot();
      if(this.aNode1305 != null && (this.aNode1305.isDescendantOf(this) || this.aNode1305.getRoot() != var6)) {
         throw new IllegalStateException();
      } else if(this.aNode1304 == null || !this.aNode1304.isDescendantOf(this) && this.aNode1304.getRoot() == var6) {
         Transform var7 = new Transform();
         Transform var8 = new Transform();
         b var9 = new b();
         a var10 = new a(0.0F, 0.0F, 0.0F, 1.0F);
         float[] var11 = new float[3];
         this.getTranslation(var11);
         if(this.anInt40 != 144) {
            if(this.aNode1305 == null && var1 == this) {
               throw new IllegalStateException();
            }

            (this.aNode1305 == null?var1:this.aNode1305).getTransformTo(this.aNode1303, var7);
            var8.postTranslate(-var11[0], -var11[1], -var11[2]);
            var7.preMultiply(var8);
            method904(this.anInt40, var7, var2, var3, var4, var5, var9);
            var9.aFloat614 = 0.0F;
            var10.method413(b.ab611, var9, (b)null);
         }

         if(this.anInt39 != 144) {
            if(this.aNode1304 == null && var1 == this) {
               throw new IllegalStateException();
            }

            (this.aNode1304 == null?var1:this.aNode1304).getTransformTo(this.aNode1303, var7);
            var8.postTranslate(-var11[0], -var11[1], -var11[2]);
            var7.preMultiply(var8);
            if(this.anInt40 != 144) {
               var8.setIdentity();
               var8.postRotateQuat(var10.aFloat603, var10.aFloat604, var10.aFloat605, -var10.aFloat606);
               var7.preMultiply(var8);
            }

            method904(this.anInt39, var7, var2, var3, var4, var5, var9);
            var9.aFloat614 = 0.0F;
            if(this.anInt40 != 144) {
               a var13;
               (var13 = new a()).method413(b.ab609, var9, b.ab611);
               var10.method416(var13);
            } else {
               var10.method413(b.ab609, var9, (b)null);
            }
         }

         if(this.anInt40 != 144 || this.anInt39 != 144) {
            super.ana864.method406(var10);
         }

      } else {
         throw new IllegalStateException();
      }
   }

   private static void method904(int var0, Transform var1, b var2, b var3, b var4, b var5, b var6) {
      switch(var0) {
      case 145:
         var6.method423(var2 == null?b.ab613:var2);
         break;
      case 146:
         var6.method423(var3 == null?b.ab607:var3);
         break;
      case 147:
         var6.method423(var4 == null?b.ab609:var4);
         break;
      case 148:
         var6.method423(var5 == null?b.ab611:var5);
      }

      var1.getImpl().method440(var6);
   }

   public void setAlignment(Node var1, int var2, Node var3, int var4) {
      if(var2 >= 144 && var2 <= 148 && var4 >= 144 && var4 <= 148) {
         if(var1 == var3 && var2 == var4 && var4 != 144) {
            throw new IllegalArgumentException("(zRef == yRef) &&  (zTarget == yTarget != NONE)");
         } else if(var1 != this && var3 != this) {
            this.anInt40 = var2;
            this.anInt39 = var4;
            this.aNode1305 = var1;
            this.aNode1304 = var3;
         } else {
            throw new IllegalArgumentException("zRef or yRef is this Node");
         }
      } else {
         throw new IllegalArgumentException("yTarget or zTarget is not one of the symbolic constants");
      }
   }

   public int getAlignmentTarget(int var1) {
      if(var1 != 148 && var1 != 147) {
         throw new IllegalArgumentException("axis != Z_AXIS && axis != Y_AXIS");
      } else {
         return var1 == 148?this.anInt40:this.anInt39;
      }
   }

   public Node getAlignmentReference(int var1) {
      if(var1 != 148 && var1 != 147) {
         throw new IllegalArgumentException("axis != Z_AXIS && axis != Y_AXIS");
      } else {
         return var1 == 148?this.aNode1305:this.aNode1304;
      }
   }

   protected void updateProperty(int var1, float[] var2) {
      switch(var1) {
      case 256:
         this.aFloat681 = G3DUtils.method605(var2[0], 0.0F, 1.0F);
         return;
      case 269:
         this.aBoolean190 = var2[0] >= 0.5F;
         return;
      case 276:
         this.aBoolean36 = var2[0] >= 0.5F;
         return;
      default:
         super.updateProperty(var1, var2);
      }
   }

   protected Node getRoot() {
      Node var10000 = this;

      while(true) {
         Node var1 = var10000;
         if(var10000.aNode1303 == null) {
            return var1;
         }

         var10000 = var1.aNode1303;
      }
   }

   protected boolean isPickable(Node var1) {
      Node var10000 = this;

      while(true) {
         Node var2 = var10000;
         if(var10000 == null) {
            break;
         }

         if(!var2.aBoolean190) {
            return false;
         }

         if(var2 == var1) {
            break;
         }

         var10000 = var2.aNode1303;
      }

      return true;
   }

   protected abstract boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4);

   protected void enableBoneFlag() {
      this.aBoolean684 = true;
   }

   protected void setSkinnedMeshBone() {
      Node var10000 = this;

      while(true) {
         Node var1 = var10000;
         if(var10000 == null || var1 instanceof SkinnedMesh) {
            return;
         }

         var1.enableBoneFlag();
         var10000 = var1.getParent();
      }
   }

   protected boolean isSkinnedMeshBone() {
      return this.aBoolean684;
   }

   protected void updateAlignReferences() {
      Node var1;
      if(this.anInt40 != 144) {
         var1 = this.aNode1305.m_duplicatedNode;
         if(this.aNode1305 != null && var1 != null && var1.isDescendantOf(this.m_duplicatedNode.getRoot())) {
            this.m_duplicatedNode.aNode1305 = var1;
         }
      }

      if(this.anInt39 != 144) {
         var1 = this.aNode1304.m_duplicatedNode;
         if(this.aNode1304 != null && var1 != null && var1.isDescendantOf(this.m_duplicatedNode.getRoot())) {
            this.m_duplicatedNode.aNode1304 = var1;
         }
      }

   }

   protected void clearAlignReferences() {
      this.m_duplicatedNode = null;
   }

   protected Object3D duplicateObject() {
      Node var1;
      (var1 = (Node)super.duplicateObject()).aNode1303 = null;
      this.m_duplicatedNode = var1;
      return var1;
   }
}
