package javax.microedition.m3g;

import emulator.graphics3D.lwjgl.Emulator3D;

public class Appearance extends Object3D {
   private int layer = 0;
   private PolygonMode polygonMode = null;
   private CompositingMode compositingMode = null;
   private Material material = null;
   private Fog fog = null;
   private Texture2D[] textures;

   public Appearance() {
      this.textures = new Texture2D[Emulator3D.NumTextureUnits];
   }

   protected Object3D duplicateObject() {
      Appearance var1;
      (var1 = (Appearance)super.duplicateObject()).textures = (Texture2D[])this.textures.clone();
      return var1;
   }

   public void setLayer(int var1) {
      if(var1 >= -63 && var1 <= 63) {
         this.layer = var1;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getLayer() {
      return this.layer;
   }

   public void setFog(Fog var1) {
      this.removeReference(this.fog);
      this.fog = var1;
      this.addReference(this.fog);
   }

   public Fog getFog() {
      return this.fog;
   }

   public void setPolygonMode(PolygonMode var1) {
      this.removeReference(this.polygonMode);
      this.polygonMode = var1;
      this.addReference(this.polygonMode);
   }

   public PolygonMode getPolygonMode() {
      return this.polygonMode;
   }

   public void setCompositingMode(CompositingMode var1) {
      this.removeReference(this.compositingMode);
      this.compositingMode = var1;
      this.addReference(this.compositingMode);
   }

   public CompositingMode getCompositingMode() {
      return this.compositingMode;
   }

   public void setTexture(int var1, Texture2D var2) {
      if(var1 >= 0 && var1 < Emulator3D.NumTextureUnits) {
         this.removeReference(this.textures[var1]);
         this.textures[var1] = var2;
         this.addReference(this.textures[var1]);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public Texture2D getTexture(int var1) {
      if(var1 >= 0 && var1 < Emulator3D.NumTextureUnits) {
         return this.textures[var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setMaterial(Material var1) {
      this.removeReference(this.material);
      this.material = var1;
      this.addReference(this.material);
   }

   public Material getMaterial() {
      return this.material;
   }
}
