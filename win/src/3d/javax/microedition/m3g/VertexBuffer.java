package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.b;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.VertexArray;

public class VertexBuffer extends Object3D {
   private int anInt37 = -1;
   private int anInt39 = 0;
   private int anInt40 = 0;
   private VertexArray aVertexArray733 = null;
   private VertexArray aVertexArray736 = null;
   private VertexArray aVertexArray737 = null;
   private VertexArray[] aVertexArrayArray734;
   private float[] aFloatArray735;
   private float[][] aFloatArrayArray144;

   public VertexBuffer() {
      this.aVertexArrayArray734 = new VertexArray[Graphics3D.NumTextureUnits];
      this.aFloatArray735 = new float[4];
      this.aFloatArray735[0] = 1.0F;
      this.aFloatArrayArray144 = new float[Graphics3D.NumTextureUnits][4];
   }

   protected Object3D duplicateObject() {
      VertexBuffer var1;
      (var1 = (VertexBuffer)super.duplicateObject()).aVertexArrayArray734 = (VertexArray[])this.aVertexArrayArray734.clone();
      var1.aFloatArray735 = (float[])this.aFloatArray735.clone();
      var1.aFloatArrayArray144 = new float[Graphics3D.NumTextureUnits][4];

      for(int var2 = 0; var2 < Graphics3D.NumTextureUnits; ++var2) {
         var1.aFloatArrayArray144[var2] = (float[])this.aFloatArrayArray144[var2].clone();
      }

      return var1;
   }

   public int getVertexCount() {
      return this.anInt39;
   }

   public void setPositions(VertexArray var1, float var2, float[] var3) {
      if(var1 != null && var1.getComponentCount() != 3) {
         throw new IllegalArgumentException();
      } else if(var1 != null && var3 != null && var3.length < 3) {
         throw new IllegalArgumentException();
      } else if(var1 != null && this.anInt39 != 0 && var1.getVertexCount() != this.anInt39) {
         throw new IllegalArgumentException();
      } else {
         this.removeReference(this.aVertexArray733);
         if(var1 != null) {
            if(this.aVertexArray733 == null) {
               ++this.anInt40;
            }

            this.anInt39 = var1.getVertexCount();
            this.aVertexArray733 = var1;
            this.aFloatArray735[0] = var2;
            if(var3 != null) {
               System.arraycopy(var3, 0, this.aFloatArray735, 1, 3);
            } else {
               this.aFloatArray735[1] = 0.0F;
               this.aFloatArray735[2] = 0.0F;
               this.aFloatArray735[3] = 0.0F;
            }
         } else if(this.aVertexArray733 != null) {
            this.aVertexArray733 = null;
            --this.anInt40;
            this.anInt39 = this.anInt40 > 0?this.anInt39:0;
         }

         this.addReference(this.aVertexArray733);
      }
   }

   public void setTexCoords(int var1, VertexArray var2, float var3, float[] var4) {
      if(var2 != null && var2.getComponentCount() != 2 && var2.getComponentCount() != 3) {
         throw new IllegalArgumentException();
      } else if(var2 != null && this.anInt39 != 0 && var2.getVertexCount() != this.anInt39) {
         throw new IllegalArgumentException();
      } else if(var2 != null && var4 != null && var4.length < var2.getComponentCount()) {
         throw new IllegalArgumentException();
      } else if(var1 >= 0 && var1 < Graphics3D.NumTextureUnits) {
         this.removeReference(this.aVertexArrayArray734[var1]);
         if(var2 != null) {
            if(this.aVertexArrayArray734[var1] == null) {
               ++this.anInt40;
            }

            this.anInt39 = var2.getVertexCount();
            this.aVertexArrayArray734[var1] = var2;
            this.aFloatArrayArray144[var1][0] = var3;
            this.aFloatArrayArray144[var1][1] = 0.0F;
            this.aFloatArrayArray144[var1][2] = 0.0F;
            this.aFloatArrayArray144[var1][3] = 0.0F;
            if(var4 != null) {
               System.arraycopy(var4, 0, this.aFloatArrayArray144[var1], 1, var2.getComponentCount());
            }
         } else if(this.aVertexArrayArray734[var1] != null) {
            this.aVertexArrayArray734[var1] = null;
            --this.anInt40;
            this.anInt39 = this.anInt40 > 0?this.anInt39:0;
         }

         this.addReference(this.aVertexArrayArray734[var1]);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setNormals(VertexArray var1) {
      if(var1 != null && var1.getComponentCount() != 3) {
         throw new IllegalArgumentException();
      } else if(var1 != null && this.anInt39 != 0 && var1.getVertexCount() != this.anInt39) {
         throw new IllegalArgumentException();
      } else {
         this.removeReference(this.aVertexArray736);
         if(var1 != null) {
            if(this.aVertexArray736 == null) {
               ++this.anInt40;
            }

            this.anInt39 = var1.getVertexCount();
            this.aVertexArray736 = var1;
         } else if(this.aVertexArray736 != null) {
            this.aVertexArray736 = null;
            --this.anInt40;
            this.anInt39 = this.anInt40 > 0?this.anInt39:0;
         }

         this.addReference(this.aVertexArray736);
      }
   }

   public void setColors(VertexArray var1) {
      if(var1 != null && var1.getComponentType() != 1) {
         throw new IllegalArgumentException();
      } else if(var1 != null && var1.getComponentCount() != 3 && var1.getComponentCount() != 4) {
         throw new IllegalArgumentException();
      } else if(var1 != null && this.anInt39 != 0 && var1.getVertexCount() != this.anInt39) {
         throw new IllegalArgumentException();
      } else {
         this.removeReference(this.aVertexArray737);
         if(var1 != null) {
            if(this.aVertexArray737 == null) {
               ++this.anInt40;
            }

            this.anInt39 = var1.getVertexCount();
            this.aVertexArray737 = var1;
         } else if(this.aVertexArray737 != null) {
            this.aVertexArray737 = null;
            --this.anInt40;
            this.anInt39 = this.anInt40 > 0?this.anInt39:0;
         }

         this.addReference(this.aVertexArray737);
      }
   }

   public VertexArray getPositions(float[] var1) {
      if(var1 != null && var1.length < 4) {
         throw new IllegalArgumentException();
      } else {
         if(this.aVertexArray733 != null && var1 != null) {
            System.arraycopy(this.aFloatArray735, 0, var1, 0, 4);
         }

         return this.aVertexArray733;
      }
   }

   public VertexArray getTexCoords(int var1, float[] var2) {
      if(var1 >= 0 && var1 < Graphics3D.NumTextureUnits) {
         if(this.aVertexArrayArray734[var1] != null && var2 != null) {
            if(var2.length < this.aVertexArrayArray734[var1].getComponentCount() + 1) {
               throw new IllegalArgumentException();
            }

            System.arraycopy(this.aFloatArrayArray144[var1], 0, var2, 0, this.aVertexArrayArray734[var1].getComponentCount() + 1);
         }

         return this.aVertexArrayArray734[var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public VertexArray getNormals() {
      return this.aVertexArray736;
   }

   public VertexArray getColors() {
      return this.aVertexArray737;
   }

   public void setDefaultColor(int var1) {
      this.anInt37 = var1;
   }

   public int getDefaultColor() {
      return this.anInt37;
   }

   protected void updateProperty(int var1, float[] var2) {
      switch(var1) {
      case 256:
         this.anInt37 &= 16777215;
         this.anInt37 |= G3DUtils.method603(var2) & -16777216;
         return;
      case 258:
         this.anInt37 &= -16777216;
         this.anInt37 |= G3DUtils.method603(var2) & 16777215;
         return;
      default:
         super.updateProperty(var1, var2);
      }
   }

   protected boolean getNormalVertex(int var1, b var2) {
      if(this.aVertexArray736 == null) {
         return false;
      } else {
         b var10000;
         float var10001;
         float var10002;
         short var10003;
         if(this.aVertexArray736.getComponentType() == 1) {
            byte[] var3 = new byte[3];
            this.aVertexArray736.get(var1, 1, var3);
            var10000 = var2;
            var10001 = (float)var3[0];
            var10002 = (float)var3[1];
            var10003 = var3[2];
         } else {
            short[] var4 = new short[3];
            this.aVertexArray736.get(var1, 1, var4);
            var10000 = var2;
            var10001 = (float)var4[0];
            var10002 = (float)var4[1];
            var10003 = var4[2];
         }

         var10000.method422(var10001, var10002, (float)var10003, 1.0F);
         return true;
      }
   }

   protected void getVertex(int var1, b var2) {
      byte[] var3 = new byte[3];
      short[] var4 = new short[3];
      short var10000;
      float var5;
      float var6;
      if(this.aVertexArray733.getComponentType() == 1) {
         this.aVertexArray733.get(var1, 1, var3);
         var5 = (float)var3[0];
         var6 = (float)var3[1];
         var10000 = var3[2];
      } else {
         this.aVertexArray733.get(var1, 1, var4);
         var5 = (float)var4[0];
         var6 = (float)var4[1];
         var10000 = var4[2];
      }

      float var7 = (float)var10000;
      var5 *= this.aFloatArray735[0];
      var6 *= this.aFloatArray735[0];
      var7 *= this.aFloatArray735[0];
      var5 += this.aFloatArray735[1];
      var6 += this.aFloatArray735[2];
      var7 += this.aFloatArray735[3];
      var2.method422(var5, var6, var7, 1.0F);
   }

   protected boolean getTexVertex(int var1, int var2, b var3) {
      if(this.aVertexArrayArray734[var2] == null) {
         return false;
      } else {
         int var4;
         byte[] var5 = new byte[var4 = this.aVertexArrayArray734[var2].getComponentCount()];
         short[] var6 = new short[var4];
         float var7;
         float var8;
         float var9;
         if(this.aVertexArrayArray734[var2].getComponentType() == 1) {
            this.aVertexArrayArray734[var2].get(var1, 1, var5);
            var7 = (float)var5[0];
            var8 = (float)var5[1];
            var9 = var4 == 3?(float)var5[2]:0.0F;
         } else {
            this.aVertexArrayArray734[var2].get(var1, 1, var6);
            var7 = (float)var6[0];
            var8 = (float)var6[1];
            var9 = var4 == 3?(float)var6[2]:0.0F;
         }

         var7 *= this.aFloatArrayArray144[var2][0];
         var8 *= this.aFloatArrayArray144[var2][0];
         var7 += this.aFloatArrayArray144[var2][1];
         var8 += this.aFloatArrayArray144[var2][2];
         var9 += this.aFloatArrayArray144[var2][3];
         var3.method422(var7, var8, var9, 1.0F);
         return true;
      }
   }
}
