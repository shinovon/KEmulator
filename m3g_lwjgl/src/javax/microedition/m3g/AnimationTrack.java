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
	private int targetProperty;

	public AnimationTrack(KeyframeSequence sequence, int property) {
		if (sequence == null) {
			throw new NullPointerException();
		} else if (!isValidPropertyId(property)) {
			throw new IllegalArgumentException();
		} else if (!isValidComponentCount(property, sequence.getComponentCount())) {
			throw new IllegalArgumentException();
		}

		keyframeSequence = sequence;
		targetProperty = property;
		controller = null;
		addReference(keyframeSequence);
	}

	protected boolean checkCompatible(Object3D o) {
		switch (this.targetProperty) {
			case ALPHA:
				return o instanceof Node || o instanceof Background || o instanceof Material || o instanceof VertexBuffer;
			case AMBIENT_COLOR:
				return o instanceof Material;
			case COLOR:
				return o instanceof Light || o instanceof Background || o instanceof Fog || o instanceof Texture2D || o instanceof VertexBuffer;
			case CROP:
				return o instanceof Sprite3D || o instanceof Background;
			case DENSITY:
				return o instanceof Fog;
			case DIFFUSE_COLOR:
				return o instanceof Material;
			case EMISSIVE_COLOR:
				return o instanceof Material;
			case FAR_DISTANCE:
				return o instanceof Camera || o instanceof Fog;
			case FIELD_OF_VIEW:
				return o instanceof Camera;
			case INTENSITY:
				return o instanceof Light;
			case MORPH_WEIGHTS:
				return o instanceof MorphingMesh;
			case NEAR_DISTANCE:
				return o instanceof Camera || o instanceof Fog;
			case ORIENTATION:
				return o instanceof Transformable;
			case PICKABILITY:
				return o instanceof Node;
			case SCALE:
				return o instanceof Transformable;
			case SHININESS:
				return o instanceof Material;
			case SPECULAR_COLOR:
				return o instanceof Material;
			case SPOT_ANGLE:
				return o instanceof Light;
			case SPOT_EXPONENT:
				return o instanceof Light;
			case TRANSLATION:
				return o instanceof Transformable;
			case VISIBILITY:
				return o instanceof Node;
			default:
				return false;
		}
	}

	private static boolean isValidPropertyId(int property) {
		return property >= ALPHA && property <= VISIBILITY;
	}

	private static boolean isValidComponentCount(int property, int componentCount) {
		switch (property) {
			case ALPHA:
				return componentCount == 1;
			case AMBIENT_COLOR:
				return componentCount == 3;
			case COLOR:
				return componentCount == 3;
			case CROP:
				return componentCount == 2 || componentCount == 4;
			case DENSITY:
				return componentCount == 1;
			case DIFFUSE_COLOR:
				return componentCount == 3;
			case EMISSIVE_COLOR:
				return componentCount == 3;
			case FAR_DISTANCE:
				return componentCount == 1;
			case FIELD_OF_VIEW:
				return componentCount == 1;
			case INTENSITY:
				return componentCount == 1;
			case MORPH_WEIGHTS:
				return true;
			case NEAR_DISTANCE:
				return componentCount == 1;
			case ORIENTATION:
				return componentCount == 4;
			case PICKABILITY:
				return componentCount == 1;
			case SCALE:
				return componentCount == 1 || componentCount == 3;
			case SHININESS:
				return componentCount == 1;
			case SPECULAR_COLOR:
				return componentCount == 3;
			case SPOT_ANGLE:
				return componentCount == 1;
			case SPOT_EXPONENT:
				return componentCount == 1;
			case TRANSLATION:
				return componentCount == 3;
			case VISIBILITY:
				return componentCount == 1;
		}

		return false;
	}

	public void setController(AnimationController controller) {
		removeReference(this.controller);
		this.controller = controller;
		addReference(controller);
	}

	public AnimationController getController() {
		return controller;
	}

	public KeyframeSequence getKeyframeSequence() {
		return keyframeSequence;
	}

	public int getTargetProperty() {
		return targetProperty;
	}

	protected void getContribution(int worldTime, float[] contribution, float[] weightTime) {
		if (controller != null && controller.isActive(worldTime)) {
			float[] components = new float[keyframeSequence.getComponentCount()];
			float keyframePos = controller.getPosition(worldTime);
			int timeToKeyframeEnd = keyframeSequence.getSampleFrame(keyframePos, components);
			float weight = controller.getWeight();

			for (int i = 0; i < components.length; ++i) {
				contribution[i] += components[i] * weight;
			}

			weightTime[0] = weight;
			weightTime[1] = (float) Math.min(timeToKeyframeEnd, controller.timeToDeactivation(worldTime));
		} else {
			weightTime[0] = 0.0F;
			weightTime[1] = (float) Math.max(1, controller == null ? Integer.MAX_VALUE : controller.timeToActivation(worldTime));
		}
	}
}
