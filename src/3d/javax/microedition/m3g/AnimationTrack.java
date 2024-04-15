package javax.microedition.m3g;

import javax.microedition.m3g.AnimationController;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Fog;
import javax.microedition.m3g.KeyframeSequence;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.MorphingMesh;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.Sprite3D;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.Transformable;
import javax.microedition.m3g.VertexBuffer;

public class AnimationTrack extends Object3D {
   public static final int ALPHA = 256;
   public static final int AMBIENT_COLOR = 257;
   public static final int COLOR = 258;
   public static final int CROP = 259;
   public static final int DENSITY = 260;
   public static final int DIFFUSE_COLOR = 261;
   public static final int EMISSIVE_COLOR = 262;
   public static final int FAR_DISTANCE = 263;
   public static final int FIELD_OF_VIEW = 264;
   public static final int INTENSITY = 265;
   public static final int MORPH_WEIGHTS = 266;
   public static final int NEAR_DISTANCE = 267;
   public static final int ORIENTATION = 268;
   public static final int PICKABILITY = 269;
   public static final int SCALE = 270;
   public static final int SHININESS = 271;
   public static final int SPECULAR_COLOR = 272;
   public static final int SPOT_ANGLE = 273;
   public static final int SPOT_EXPONENT = 274;
   public static final int TRANSLATION = 275;
   public static final int VISIBILITY = 276;
   private KeyframeSequence aKeyframeSequence901;
   private AnimationController anAnimationController902;
   private int anInt37;

   public AnimationTrack(KeyframeSequence var1, int var2) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(!method14(var2)) {
         throw new IllegalArgumentException();
      } else if(!method595(var2, var1.getComponentCount())) {
         throw new IllegalArgumentException();
      } else {
         this.aKeyframeSequence901 = var1;
         this.anInt37 = var2;
         this.anAnimationController902 = null;
         this.addReference(this.aKeyframeSequence901);
      }
   }

   protected boolean checkCompatible(Object3D var1) {
      boolean var2;
      boolean var3;
      label89: {
         var2 = false;
         Object3D var10000;
         switch(this.anInt37) {
         case 256:
            var2 = var1 instanceof Node || var1 instanceof Background || var1 instanceof Material || var1 instanceof VertexBuffer;
            return var2;
         case 257:
            var3 = var1 instanceof Material;
            break label89;
         case 258:
            var2 = var1 instanceof Light || var1 instanceof Background || var1 instanceof Fog || var1 instanceof Texture2D || var1 instanceof VertexBuffer;
            return var2;
         case 259:
            var2 = var1 instanceof Sprite3D || var1 instanceof Background;
            return var2;
         case 260:
            var3 = var1 instanceof Fog;
            break label89;
         case 261:
            var3 = var1 instanceof Material;
            break label89;
         case 262:
            var3 = var1 instanceof Material;
            break label89;
         case 263:
            var2 = var1 instanceof Camera || var1 instanceof Fog;
            return var2;
         case 264:
            var3 = var1 instanceof Camera;
            break label89;
         case 265:
            var3 = var1 instanceof Light;
            break label89;
         case 266:
            var3 = var1 instanceof MorphingMesh;
            break label89;
         case 267:
            var2 = var1 instanceof Camera || var1 instanceof Fog;
            return var2;
         case 268:
            var3 = var1 instanceof Transformable;
            break label89;
         case 269:
            var10000 = var1;
            break;
         case 270:
            var3 = var1 instanceof Transformable;
            break label89;
         case 271:
            var3 = var1 instanceof Material;
            break label89;
         case 272:
            var3 = var1 instanceof Material;
            break label89;
         case 273:
            var3 = var1 instanceof Light;
            break label89;
         case 274:
            var3 = var1 instanceof Light;
            break label89;
         case 275:
            var3 = var1 instanceof Transformable;
            break label89;
         case 276:
            var10000 = var1;
            break;
         default:
            return var2;
         }

         var3 = var10000 instanceof Node;
      }

      var2 = var3;
      return var2;
   }

   private static boolean method595(int var0, int var1) {
      boolean var2 = false;
      boolean var10000;
      switch(var0) {
      case 256:
         var2 = var1 == 1;
         return var2;
      case 257:
         var2 = var1 == 3;
         return var2;
      case 258:
         var2 = var1 == 3;
         return var2;
      case 259:
         var2 = var1 == 2 || var1 == 4;
         return var2;
      case 260:
         var2 = var1 == 1;
         return var2;
      case 261:
         var2 = var1 == 3;
         return var2;
      case 262:
         var2 = var1 == 3;
         return var2;
      case 263:
         var2 = var1 == 1;
         return var2;
      case 264:
         var2 = var1 == 1;
         return var2;
      case 265:
         var2 = var1 == 1;
         return var2;
      case 266:
         var10000 = true;
         break;
      case 267:
         var2 = var1 == 1;
         return var2;
      case 268:
         var2 = var1 == 4;
         return var2;
      case 269:
         var2 = var1 == 1;
         return var2;
      case 270:
         var2 = var1 == 1 || var1 == 3;
         return var2;
      case 271:
         var2 = var1 == 1;
         return var2;
      case 272:
         var2 = var1 == 3;
         return var2;
      case 273:
         var2 = var1 == 1;
         return var2;
      case 274:
         var2 = var1 == 1;
         return var2;
      case 275:
         var2 = var1 == 3;
         return var2;
      case 276:
         var10000 = var1 == 1;
         break;
      default:
         return var2;
      }

      var2 = var10000;
      return var2;
   }

   private static boolean method14(int var0) {
      return var0 >= 256 && var0 <= 276;
   }

   public void setController(AnimationController var1) {
      this.removeReference(this.anAnimationController902);
      this.anAnimationController902 = var1;
      this.addReference(this.anAnimationController902);
   }

   public AnimationController getController() {
      return this.anAnimationController902;
   }

   public KeyframeSequence getKeyframeSequence() {
      return this.aKeyframeSequence901;
   }

   public int getTargetProperty() {
      return this.anInt37;
   }

   protected void getContribution(int var1, float[] var2, float[] var3) {
      if(this.anAnimationController902 != null && this.anAnimationController902.isActive(var1)) {
         float[] var4 = new float[this.aKeyframeSequence901.getComponentCount()];
         float var5 = this.anAnimationController902.getPosition(var1);
         int var6 = this.aKeyframeSequence901.getSampleFrame(var5, var4);
         var3[1] = (float)Math.min(var6, this.anAnimationController902.timeToDeactivation(var1));
         float var7 = this.anAnimationController902.getWeight();

         for(int var8 = 0; var8 < var4.length; ++var8) {
            var2[var8] += var4[var8] * var7;
         }

         var3[0] = var7;
      } else {
         var3[0] = 0.0F;
         var3[1] = (float)Math.max(1, this.anAnimationController902 == null?Integer.MAX_VALUE:this.anAnimationController902.timeToActivation(var1));
      }
   }
}
