package javax.microedition.m3g;

import javax.microedition.m3g.CompositingMode;
import javax.microedition.m3g.Fog;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.Texture2D;

public class Appearance extends Object3D {
   private int anInt37 = 0;
   private PolygonMode aPolygonMode1010 = null;
   private CompositingMode aCompositingMode1011 = null;
   private Material aMaterial1012 = null;
   private Fog aFog1013 = null;
   private Texture2D[] aTexture2DArray1014;

   public Appearance() {
      this.aTexture2DArray1014 = new Texture2D[Graphics3D.NumTextureUnits];
   }

   protected Object3D duplicateObject() {
      Appearance var1;
      (var1 = (Appearance)super.duplicateObject()).aTexture2DArray1014 = (Texture2D[])this.aTexture2DArray1014.clone();
      return var1;
   }

   public void setLayer(int var1) {
      if(var1 >= -63 && var1 <= 63) {
         this.anInt37 = var1;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getLayer() {
      return this.anInt37;
   }

   public void setFog(Fog var1) {
      this.removeReference(this.aFog1013);
      this.aFog1013 = var1;
      this.addReference(this.aFog1013);
   }

   public Fog getFog() {
      return this.aFog1013;
   }

   public void setPolygonMode(PolygonMode var1) {
      this.removeReference(this.aPolygonMode1010);
      this.aPolygonMode1010 = var1;
      this.addReference(this.aPolygonMode1010);
   }

   public PolygonMode getPolygonMode() {
      return this.aPolygonMode1010;
   }

   public void setCompositingMode(CompositingMode var1) {
      this.removeReference(this.aCompositingMode1011);
      this.aCompositingMode1011 = var1;
      this.addReference(this.aCompositingMode1011);
   }

   public CompositingMode getCompositingMode() {
      return this.aCompositingMode1011;
   }

   public void setTexture(int var1, Texture2D var2) {
      if(var1 >= 0 && var1 < Graphics3D.NumTextureUnits) {
         this.removeReference(this.aTexture2DArray1014[var1]);
         this.aTexture2DArray1014[var1] = var2;
         this.addReference(this.aTexture2DArray1014[var1]);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public Texture2D getTexture(int var1) {
      if(var1 >= 0 && var1 < Graphics3D.NumTextureUnits) {
         return this.aTexture2DArray1014[var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setMaterial(Material var1) {
      this.removeReference(this.aMaterial1012);
      this.aMaterial1012 = var1;
      this.addReference(this.aMaterial1012);
   }

   public Material getMaterial() {
      return this.aMaterial1012;
   }
}
