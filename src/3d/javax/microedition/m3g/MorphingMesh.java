package javax.microedition.m3g;

import emulator.graphics3D.m3g.e;

public class MorphingMesh extends Mesh {
   private VertexBuffer[] aVertexBufferArray937;
   private float[] weights;
   public float m_baseWeight;

   public MorphingMesh(VertexBuffer var1, VertexBuffer[] var2, IndexBuffer var3, Appearance var4) {
      super(var1, var3, var4);
      if(var2 == null) {
         throw new NullPointerException();
      } else {
         this.aVertexBufferArray937 = new VertexBuffer[var2.length];

         for(int var5 = var2.length - 1; var5 >= 0; --var5) {
            if(var2[var5] == null) {
               throw new NullPointerException();
            }

            if(var2[var5].getVertexCount() == 0) {
               throw new IllegalArgumentException("targets is empty");
            }

            this.aVertexBufferArray937[var5] = var2[var5];
            this.addReference(this.aVertexBufferArray937[var5]);
         }

         this.weights = new float[this.aVertexBufferArray937.length];
         this.m_baseWeight = 1.0F;
      }
   }

   public MorphingMesh(VertexBuffer var1, VertexBuffer[] var2, IndexBuffer[] var3, Appearance[] var4) {
      super(var1, var3, var4);
      this.aVertexBufferArray937 = new VertexBuffer[var2.length];

      for(int var5 = var2.length - 1; var5 >= 0; --var5) {
         if(var2[var5] == null) {
            throw new NullPointerException();
         }

         if(var2[var5].getVertexCount() == 0) {
            throw new IllegalArgumentException("targets is empty");
         }

         this.aVertexBufferArray937[var5] = var2[var5];
         this.addReference(this.aVertexBufferArray937[var5]);
      }

      this.weights = new float[this.aVertexBufferArray937.length];
      this.m_baseWeight = 1.0F;
   }

   protected Object3D duplicateObject() {
      MorphingMesh var1;
      (var1 = (MorphingMesh)super.duplicateObject()).weights = (float[])this.weights.clone();
      var1.aVertexBufferArray937 = (VertexBuffer[])this.aVertexBufferArray937.clone();
      return var1;
   }

   public VertexBuffer getMorphTarget(int var1) {
      if(var1 >= 0 && var1 < this.aVertexBufferArray937.length) {
         return this.aVertexBufferArray937[var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getMorphTargetCount() {
      return this.aVertexBufferArray937.length;
   }

   public void setWeights(float[] var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var1.length < this.weights.length) {
         throw new IllegalArgumentException();
      } else {
         this.m_baseWeight = 1.0F;

         for(int var2 = 0; var2 < this.weights.length; ++var2) {
            this.weights[var2] = var1[var2];
            this.m_baseWeight -= var1[var2];
         }

      }
   }

   public void getWeights(float[] var1) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(var1.length < this.weights.length) {
         throw new IllegalArgumentException();
      } else {
         System.arraycopy(this.weights, 0, var1, 0, this.weights.length);
      }
   }

   protected void updateProperty(int var1, float[] var2) {
      switch(var1) {
      case 266:
         this.m_baseWeight = 1.0F;

         for(int var3 = 0; var3 < this.weights.length; ++var3) {
            if(var3 < var2.length) {
               this.weights[var3] = var2[var3];
               this.m_baseWeight -= var2[var3];
            } else {
               this.weights[var3] = 0.0F;
            }
         }

         return;
      default:
         super.updateProperty(var1, var2);
      }
   }

   protected boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4) {
      e.getInstance().method779(this);
      e.getInstance().method778();
      return super.rayIntersect(var1, var2, var3, var4, e.getInstance().aVertexBuffer1124);
   }
}
