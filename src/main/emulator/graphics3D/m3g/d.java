package emulator.graphics3D.m3g;

import java.util.Vector;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.SkinnedMesh;
import javax.microedition.m3g.Sprite3D;
import javax.microedition.m3g.Transform;

public final class d {
   private static d ad1145;
   private static d ad1148;
   private Node aNode1146;
   private Vector aVector1147 = new Vector();

   public static d method788() {
      if(ad1145 == null) {
         ad1145 = new d();
      }

      return ad1145;
   }

   public static d method796() {
      if(ad1148 == null) {
         ad1148 = new d();
      }

      return ad1148;
   }

   private void method789(c var1) {
      Object var2 = null;

      int var3;
      for(var3 = 0; var3 < this.aVector1147.size() && ((c)this.aVector1147.get(var3)).anInt1144 <= var1.anInt1144; ++var3) {
         ;
      }

      this.aVector1147.insertElementAt(var1, var3);
   }

   public final int method790() {
      return this.aVector1147.size();
   }

   public final void method791() {
      this.aVector1147.clear();
      this.aNode1146 = null;
   }

   public final c method792(int var1) {
      return (c)this.aVector1147.get(var1);
   }

   public final boolean method793(Node var1) {
      Node var10000 = var1;

      while(true) {
         Node var2 = var10000;
         if(var10000 == null) {
            break;
         }

         if(!var2.isRenderingEnabled()) {
            return false;
         }

         if(this.aNode1146 == null || var2 == this.aNode1146) {
            break;
         }

         var10000 = var2.getParent();
      }

      return true;
   }

   public final float method794(Node var1) {
      float var2 = var1.getAlphaFactor();
      Node var10000 = var1;

      while(true) {
         Node var3 = var10000;
         if(var10000.getParent() == null || var1 == this.aNode1146) {
            return var2;
         }

         var2 *= var3.getAlphaFactor();
         var10000 = var3.getParent();
      }
   }

   public final void method795(Node var1, Transform var2) {
      this.aNode1146 = var1;
      this.method797(var1, var2);
   }

   private void method797(Node var1, Transform var2) {
      if(this.method793(var1)) {
         if(var2 == null) {
            var2 = new Transform();
         }

         if(var1 instanceof Sprite3D) {
            Sprite3D var9;
            if((var9 = (Sprite3D)var1).getAppearance() != null && var9.getCropWidth() != 0 && var9.getCropHeight() != 0) {
               this.method789(new c(var1, var2, 0, this));
            }
         } else {
            if(var1 instanceof Mesh) {
               int var3 = ((Mesh)var1).getSubmeshCount();

               for(int var4 = 0; var4 < var3; ++var4) {
                  if(((Mesh)var1).getAppearance(var4) != null) {
                     this.method789(new c(var1, var2, var4, this));
                  }
               }

               if(var1 instanceof SkinnedMesh) {
                  Group var10 = ((SkinnedMesh)var1).getSkeleton();
                  Transform var5 = new Transform();
                  Transform var6 = new Transform(var2);
                  var10.getCompositeTransform(var5);
                  var6.postMultiply(var5);
                  this.method797(((SkinnedMesh)var1).getSkeleton(), var6);
                  return;
               }
            } else if(var1 instanceof Group) {
               Transform var8 = new Transform();
               Transform var11 = new Transform(var2);
               Group var12 = (Group)var1;

               for(int var7 = 0; var7 < var12.getChildCount(); ++var7) {
                  Node var13;
                  (var13 = var12.getChild(var7)).getCompositeTransform(var8);
                  var8.preMultiply(var11);
                  this.method797(var13, var8);
               }
            }

         }
      }
   }
}
