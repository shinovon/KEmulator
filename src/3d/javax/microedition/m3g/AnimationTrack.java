package javax.microedition.m3g;

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
   private KeyframeSequence keyframeSequence;
   private AnimationController controller;
   private int target;

   public AnimationTrack(KeyframeSequence var1, int var2) {
      if(var1 == null) {
         throw new NullPointerException();
      } else if(!method14(var2)) {
         throw new IllegalArgumentException();
      } else if(!method595(var2, var1.getComponentCount())) {
         throw new IllegalArgumentException();
      } else {
         this.keyframeSequence = var1;
         this.target = var2;
         this.controller = null;
         this.addReference(this.keyframeSequence);
      }
   }

   protected boolean checkCompatible(Object3D o) {
      switch(this.target) {
      case 256:
         return o instanceof Node || o instanceof Background || o instanceof Material || o instanceof VertexBuffer;
      case 257:
         return o instanceof Material;
      case 258:
         return o instanceof Light || o instanceof Background || o instanceof Fog || o instanceof Texture2D || o instanceof VertexBuffer;
      case 259:
         return o instanceof Sprite3D || o instanceof Background;
      case 260:
         return o instanceof Fog;
      case 261:
         return o instanceof Material;
      case 262:
         return o instanceof Material;
      case 263:
         return o instanceof Camera || o instanceof Fog;
      case 264:
         return o instanceof Camera;
      case 265:
         return o instanceof Light;
      case 266:
         return o instanceof MorphingMesh;
      case 267:
         return o instanceof Camera || o instanceof Fog;
      case 268:
         return o instanceof Transformable;
      case 269:
         return o instanceof Node;
      case 270:
         return o instanceof Transformable;
      case 271:
         return o instanceof Material;
      case 272:
         return o instanceof Material;
      case 273:
         return o instanceof Light;
      case 274:
         return o instanceof Light;
      case 275:
         return o instanceof Transformable;
      case 276:
         return o instanceof Node;
      default:
         return false;
      }
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
      this.removeReference(this.controller);
      this.controller = var1;
      this.addReference(this.controller);
   }

   public AnimationController getController() {
      return this.controller;
   }

   public KeyframeSequence getKeyframeSequence() {
      return this.keyframeSequence;
   }

   public int getTargetProperty() {
      return this.target;
   }

   protected void getContribution(int var1, float[] var2, float[] var3) {
      if(this.controller != null && this.controller.isActive(var1)) {
         float[] var4 = new float[this.keyframeSequence.getComponentCount()];
         float var5 = this.controller.getPosition(var1);
         int var6 = this.keyframeSequence.getSampleFrame(var5, var4);
         var3[1] = (float)Math.min(var6, this.controller.timeToDeactivation(var1));
         float var7 = this.controller.getWeight();

         for(int var8 = 0; var8 < var4.length; ++var8) {
            var2[var8] += var4[var8] * var7;
         }

         var3[0] = var7;
      } else {
         var3[0] = 0.0F;
         var3[1] = (float)Math.max(1, this.controller == null?Integer.MAX_VALUE:this.controller.timeToActivation(var1));
      }
   }
}
