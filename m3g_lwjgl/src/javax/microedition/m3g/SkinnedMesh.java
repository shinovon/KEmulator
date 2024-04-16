package javax.microedition.m3g;

import emulator.graphics3D.m3g.e;
import emulator.graphics3D.m3g.h;
import java.util.Vector;

public class SkinnedMesh extends Mesh {
   Group aGroup1203;
   public Vector m_transforms;

   public SkinnedMesh(VertexBuffer var1, IndexBuffer var2, Appearance var3, Group var4) {
      super(var1, var2, var3);
      if(var4 == null) {
         throw new NullPointerException();
      } else if(!(var4 instanceof World) && var4.getParent() == null) {
         this.aGroup1203 = var4;
         this.aGroup1203.parent = this;
         this.addReference(this.aGroup1203);
         this.m_transforms = new Vector();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public SkinnedMesh(VertexBuffer var1, IndexBuffer[] var2, Appearance[] var3, Group var4) {
      super(var1, var2, var3);
      if(!(var4 instanceof World) && var4.getParent() == null) {
         this.aGroup1203 = var4;
         this.aGroup1203.parent = this;
         this.addReference(this.aGroup1203);
         this.m_transforms = new Vector();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Group getSkeleton() {
      return this.aGroup1203;
   }

   protected Object3D duplicateObject() {
      SkinnedMesh var1;
      Group var2 = (Group)(var1 = (SkinnedMesh)super.duplicateObject()).getSkeleton().duplicateObject();
      var1.removeReference(var1.aGroup1203);
      var2.parent = var1;
      var1.aGroup1203 = var2;
      var1.addReference(var2);
      var1.m_transforms = (Vector)this.m_transforms.clone();
      return var1;
   }

   public void addTransform(Node var1, int var2, int var3, int var4) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var1 != this.aGroup1203 && !var1.isDescendantOf(this.aGroup1203)) {
         throw new IllegalArgumentException();
      } else if(var2 > 0 && var4 > 0) {
         if(var3 >= 0 && var3 + var4 <= '\uffff') {
            int var5;
            for(var5 = 0; var5 < this.m_transforms.size() && var3 > ((h)this.m_transforms.elementAt(var5)).anInt1098; ++var5) {
               ;
            }

            Transform var6 = new Transform();
            if(!this.getTransformTo(var1, var6)) {
               throw new ArithmeticException();
            } else {
               h var7 = new h(var1, var6, var2, var3, var3 + var4 - 1);
               this.m_transforms.insertElementAt(var7, var5);
               var1.setSkinnedMeshBone();
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void getBoneTransform(Node var1, Transform var2) {
      if(var1 != null && var2 != null) {
         if(var1 != this.aGroup1203 && !var1.isDescendantOf(this.aGroup1203)) {
            throw new IllegalArgumentException();
         } else {
            h var3 = null;

            for(int var4 = 0; var4 < this.m_transforms.size(); ++var4) {
               if((var3 = (h)this.m_transforms.elementAt(var4)).aNode1095 == var1) {
                  var2.set(var3.aTransform1096);
                  return;
               }
            }

         }
      } else {
         throw new NullPointerException();
      }
   }

   public int getBoneVertices(Node var1, int[] var2, float[] var3) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var1 != this.aGroup1203 && !var1.isDescendantOf(this.aGroup1203)) {
         throw new IllegalArgumentException();
      } else {
         int var4 = 0;
         int var5;
         float[] var6 = new float[var5 = super.m_vertices.getVertexCount()];
         h var7 = null;

         int var8;
         for(var8 = 0; var8 < this.m_transforms.size(); ++var8) {
            if((var7 = (h)this.m_transforms.elementAt(var8)).aNode1095 == var1 && var7.anInt1098 < var5) {
               int var9 = Math.min(var5, var7.anInt1100 + 1);

               for(int var10 = var7.anInt1098; var10 < var9; ++var10) {
                  var6[var10] += (float)var7.anInt1097;
               }
            }
         }

         for(var8 = 0; var8 < var5; ++var8) {
            if(var6[var8] > 0.0F) {
               if(var2 != null) {
                  var2[var4] = var8;
               }

               if(var3 != null) {
                  var3[var4] = var6[var8];
               }

               ++var4;
            }
         }

         return var4;
      }
   }

   protected void alignment(Node var1) {
      super.alignment(var1);
      if(this.aGroup1203 != null) {
         this.aGroup1203.alignment(var1);
      }

   }

   protected boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4) {
      e.getInstance().method779(this);
      e.getInstance().method778();
      return super.rayIntersect(var1, var2, var3, var4, e.getInstance().aVertexBuffer1124);
   }

   public Vector getTransforms() {
      return m_transforms;
   }
}
