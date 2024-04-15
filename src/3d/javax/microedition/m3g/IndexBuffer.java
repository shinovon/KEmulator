package javax.microedition.m3g;

import javax.microedition.m3g.Object3D;

public abstract class IndexBuffer extends Object3D {
   int[] anIntArray145;

   public int getIndexCount() {
      return this.anIntArray145 != null?this.anIntArray145.length:0;
   }

   public void getIndices(int[] var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var1.length < this.getIndexCount()) {
         throw new IllegalArgumentException();
      } else {
         if(this.anIntArray145 != null) {
            System.arraycopy(this.anIntArray145, 0, var1, 0, this.anIntArray145.length);
         }

      }
   }
}
