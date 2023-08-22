/*
 * Created on Oct 23, 2005 by Stefan Haustein
 */
package javax.microedition.m3g;

import java.io.IOException;

public class AnimationController extends Object3D {

	float speed = 1;
	float weight = 1;
	int activeIntervalStart;
	int activeIntervalEnd;
	float referenceSequenceTime;
	int referenceWorldTime;
	
//	@Override
	void load(M3gInputStream is) throws IOException {
		//Float32       speed;
		//Float32       weight;
		//Int32         activeIntervalStart;
		//Int32         activeIntervalEnd;
		//Float32       referenceSequenceTime;
		//Int32         referenceWorldTime;
		
		_load(is);
		
		speed = is.readFloat32();
		weight = is.readFloat32();
		activeIntervalStart = is.readInt32();
		activeIntervalEnd = is.readInt32();
		referenceSequenceTime = is.readFloat32();
		referenceWorldTime = is.readInt32();
	}

//	@Override
	public int getReferences(Object3D[] references) {
		return _getReferences(references);
	}

	
	public AnimationController(){
    //Creates a new AnimationController object.
	}

	public int 	getActiveIntervalEnd(){
//    Retrieves the ending time of the current active interval of this animation controller, in world time units.
		return activeIntervalEnd;
	}

    public int 	getActiveIntervalStart(){
//    Retrieves the starting time of the current active interval of this animation controller, in world time units.
		return activeIntervalStart;
	}

    
    /**

    ts = tsref + s (tw - twref)
    where
        ts = the computed sequence time
        tw = the given world time
        tsref = the reference sequence time
        twref = the reference world time
    s = the speed; sequence time per world time
    The reference point (twref, tsref) is specified with the setPosition method and the speed with the setSpeed method (note that setting the speed may also change the reference point).
    */        
    
    public float getPosition(int worldTime){
	    return referenceSequenceTime + speed * (worldTime - referenceWorldTime);
	}
	
    public float 	getSpeed(){
//    Retrieves the currently set playback speed of this animation controller.
		return speed;
	}
	
    public float 	getWeight(){
//     Retrieves the currently set blending weight for this animation controller.
		return weight;
	}
	
    public void setActiveInterval(int start, int end){
    //Sets the world time interval during which this animation controller is active.
		activeIntervalStart = start;
		activeIntervalEnd = end;
	}
	
    public void 	setPosition(float sequenceTime, int worldTime){
	    this.referenceSequenceTime = sequenceTime;
        this.referenceWorldTime = worldTime;
	}
	
    
    /** The reference point (twref, tsref) and speed (s) are updated based on the given world time and speed as follows:

    twref' = worldTime
    tsref' = getPosition(worldTime)
    s' = speed
    */
    
    public void 	setSpeed(float speed, int worldTime){
	    referenceWorldTime = worldTime;
        referenceSequenceTime = getPosition(worldTime);
        this.speed = speed;
	}
	
    public void 	setWeight(float weight){
    //Sets the blending weight for this animation controll
		this.weight = weight;
	}

//	@Override
	public Object3D duplicate() {
		AnimationController target = new AnimationController();
		_duplicate(target);
		target.setActiveInterval(activeIntervalStart, activeIntervalEnd);
		target.weight = weight;
		target.speed = speed;
		target.referenceSequenceTime = referenceSequenceTime;
		target.referenceWorldTime = referenceWorldTime;
		return target;
	}
	
}
