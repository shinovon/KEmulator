package javax.microedition.m3g;

import java.io.IOException;

public class Fog extends Object3D {
	
	public static final int 	EXPONENTIAL= 	80;
	public static final int 	LINEAR 	=81;
	
	private int color;
	private int mode;
	private float density;
	private float near;
	private float far;

	public Fog(){
		//Constructs a new Fog object with default values.
	}

	public int 	getColor(){
//    Retrieves the current color of this Fog.
		return color;
	}
	
	public	float 	getDensity(){
//    Retrieves the fog density of exponential fog.
		return density;
	}
	
	public float 	getFarDistance(){
		//Retrieves the linear fog far distance.
		return far;
	}
	
	public int 	getMode(){
		//Retrieves the current fog mode.
		return mode;
	}
	
	public float 	getNearDistance(){
		//    Retrieves the linear fog near distance.
		return near;
	}
	
	public void 	setColor(int RGB){
    	//Sets the color of this Fog.
		color = RGB;
	}
	
	public void 	setDensity(float density){
    	//Sets the fog density for exponential fog.
		this.density = density;
	}
		
	public void 	setLinear(float near, float far){
		//    Sets the near and far distances for linear fog.
		this.near = near;
		this.far = far;
	}
		
	public void 	setMode(int mode){
     //Sets the fog mode to either linear or exponential.
		this.mode = mode;
	}
	

	void load(M3gInputStream is) throws IOException {
		//ColorRGB      color;
		//Byte          mode;
		//IF mode==EXPONENTIAL, THEN
		//    Float32       density;
		//ELSE IF mode==LINEAR, THEN
		//    Float32       near;
		//    Float32       far;
		//END
		
		_load(is);
		color = is.readColorRGB();
		mode = is.read();
		switch(mode){
		case EXPONENTIAL:
			density = is.readFloat32();
			break;
		case LINEAR:
			near = is.readFloat32();
			far = is.readFloat32();
			break;
		default:
			throw new RuntimeException("Illegal fog mode: "+mode);
		}
	}


	public int getReferences(Object3D[] refs) {
		return _getReferences(refs);
	}


	public Object3D duplicate() {
		Fog target = new Fog();
		target.color = color;
		target.mode = mode;
		target.density = density;
		target.near = near;
		target.far = far;
		return _duplicate(target);
	}
    
    void setProperty(int id, float [] value){
        switch(id){
        case AnimationTrack.COLOR:
            color = setColor(value, color);
            break;
        case AnimationTrack.DENSITY:
            density = Math.max(0, value[0]);
            break;
        case AnimationTrack.FAR_DISTANCE:
            far = value[0];
            break;
        default:
            super.setProperty(id, value);
        }
    }

    
}
