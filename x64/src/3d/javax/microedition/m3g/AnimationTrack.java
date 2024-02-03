/*
 * Created on Oct 22, 2005 by Stefan Haustein
 */
package javax.microedition.m3g;

import java.io.IOException;

public class AnimationTrack extends Object3D {

	public static final int 	ALPHA =	256;
	public static final int 	AMBIENT_COLOR 	=257;
	public static final int 	COLOR 	=258;
	public static final int 	CROP 	=259;
	public static final int 	DENSITY 	=260;
	public static final int 	DIFFUSE_COLOR =	261;
	public static final int 	EMISSIVE_COLOR 	=262;
	public static final int 	FAR_DISTANCE 	=263;
	public static final int 	FIELD_OF_VIEW 	=264;
	public static final int 	INTENSITY 	=265;
	public static final int 	MORPH_WEIGHTS= 	266;
	public static final int 	NEAR_DISTANCE =	267;
	public static final int 	ORIENTATION 	=268;
	public static final int 	PICKABILITY =	269;
	public static final int 	SCALE 	=270;
	public static final int 	SHININESS 	=271;
	public static final int 	SPECULAR_COLOR 	=272;
	public static final int 	SPOT_ANGLE 	=273;
	public static final int 	SPOT_EXPONENT= 	274;
	public static final int 	TRANSLATION 	=275;
	public static final int 	VISIBILITY 	=276;
	
	
	private KeyframeSequence keyframeSequence;
	private int propertyID;
	private AnimationController animationController;
	
	AnimationTrack(){
	}
	
	public AnimationTrack(KeyframeSequence sequence, int property){
//    Creates an animation track with the given keyframe sequence targeting the given property.
		this.keyframeSequence = sequence;
		this.propertyID = property;
	}
	
	
	
	public AnimationController 	getController(){
		return animationController;
	}
	
	public KeyframeSequence 	getKeyframeSequence(){
//    Returns the keyframe sequence object which defines the keyframe values for this animation track.
		return keyframeSequence;
	}
	
	public int 	getTargetProperty(){
    return propertyID;
	}
	
	public void 	setController(AnimationController controller){
		//Specifies the animation controller to be used for controlling this animation track.
		this.animationController = controller;
	}
	

	void load(M3gInputStream is) throws IOException {
		
		//ObjectIndex   keyframeSequence;
		//ObjectIndex   animationController;
		//UInt32        propertyID;
		
		_load(is);
		keyframeSequence = (KeyframeSequence) is.readObject();
		animationController = (AnimationController) is.readObject();
		propertyID = is.readInt32();
        
        //dump();
	}
	

	public int getReferences(Object3D[] refs) {
		int count = _getReferences(refs);
		
		count = addRef(refs, count, keyframeSequence);
		count = addRef(refs, count, animationController);

		return count;
		
	}


	public Object3D duplicate() {
		AnimationTrack target = new AnimationTrack();
		target.keyframeSequence = keyframeSequence;
		target.animationController = animationController;
		target.propertyID = propertyID;
		return _duplicate(target);
	}


    /** Calculate animated property based on KeyFrameSequence and  */
    //TODO
    public float[] calcValue(int tw) {

        if(animationController == null 
                || (animationController.activeIntervalEnd 
                           != animationController.activeIntervalStart 
                    && (tw < animationController.activeIntervalStart 
                            || tw > animationController.activeIntervalEnd))){
            return null;
        }
        
        float ts = animationController.getPosition(tw);

       float[] value = keyframeSequence.calcValue(Math.round(ts));
       float w = animationController.getWeight();
       for(int i = 0; i < value.length; i++){
           value[i]*=w;
       }
       return value;
    }
	
	
}
