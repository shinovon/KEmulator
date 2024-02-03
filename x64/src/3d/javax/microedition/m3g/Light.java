/*
 * Created on 15.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package javax.microedition.m3g;

import java.io.IOException;

//import com.sun.org.apache.bcel.internal.generic.LNEG;

/**
 * @author stefan haustein
 
 */
public class Light extends Node {

	public static final int 	AMBIENT 	=128;
	public static final int 	DIRECTIONAL =	129;
	public static final int 	OMNI 	=130;
	public static final int 	SPOT 	=131;
	
	float       attenuationConstant;
	float       attenuationLinear;
	float       attenuationQuadratic;
	int      	color;
	int         mode;
	float       intensity;
	float       spotAngle;
	float       spotExponent;

	public Light(){
		//Constructs a new Light with default values.

		mode = DIRECTIONAL;
		color = 0x00FFFFFF; 
		intensity = 1.0f; 
		
//		attenuation = (1, 0, 0);
		attenuationConstant = 1;
		attenuationLinear = 0;
		attenuationQuadratic = 0;
		
		spotAngle = 45 ; 
		spotExponent = 0.0f; 

	}
	
	public int 	getColor(){
    //Retrieves the current color of this Light.
		return color;
	}
	
public float 	getConstantAttenuation(){
//    Retrieves the current constant attenuation coefficient for this Light.
	return attenuationConstant;
}

public float 	getIntensity(){
//    Retrieves the current intensity of this Light.
	return intensity;
}

public float 	getLinearAttenuation(){
//    Retrieves the current linear attenuation coefficient for this Light.
	return attenuationLinear;
}

public int 	getMode(){
//    Retrieves the current type of this Light.
	return mode;
}

public float 	getQuadraticAttenuation(){
//    Retrieves the current quadratic attenuation coefficient for this Light.
	return attenuationQuadratic;
}

public float 	getSpotAngle(){
//    Retrieves the current spot angle of this Light.
	return spotAngle;
}

public float 	getSpotExponent(){
//    Retrieves the current spot exponent for this Light.
	return spotExponent;
}

public void 	setAttenuation(float constant, float linear, float quadratic){
//    Sets the attenuation coefficients for this Light.
	this.attenuationConstant = constant;
	this.attenuationLinear = linear;
	this.attenuationQuadratic = quadratic;
}

public void 	setColor(int RGB){
 //   Sets the color of this Light.
	this.color = RGB;
}

public void 	setIntensity(float intensity){
//    Sets the intensity of this Light.
	this.intensity = intensity;
}

public void 	setMode(int mode){
//    Sets the type of this Light.
	this.mode = mode;
}

public void 	setSpotAngle(float angle){
//    Sets the spot cone angle for this Light.
	this.spotAngle = angle;
}

public void 	setSpotExponent(float exponent){
//    Sets the spot exponent for this Light.
	this.spotExponent = exponent;
}


	void load(M3gInputStream is) throws IOException {
		//Float32       attenuationConstant;
		//Float32       attenuationLinear;
		//Float32       attenuationQuadratic;
		//ColorRGB      color;
		//Byte          mode;
		//Float32       intensity;
		//Float32       spotAngle;
		//Float32       spotExponent;
		
		_load(is);
		attenuationConstant = is.readFloat32();
		attenuationLinear = is.readFloat32();
		attenuationQuadratic = is.readFloat32();
		color = is.readColorRGB();
		mode = is.read();
		intensity = is.readFloat32();
		spotAngle = is.readFloat32();
		spotExponent = is.readFloat32();
	}


	public Object3D duplicate() {
		Light target = new Light();
		target.setAttenuation(attenuationConstant, attenuationLinear, attenuationQuadratic);
		target.color = color;
		target.mode = mode;
		target.intensity =intensity;
		target.spotAngle = spotAngle;
		target.spotExponent = spotExponent;
		return _duplicate(target);
	}

    
	public int getReferences(Object3D[] refs) {
	    return _getReferences(refs);
	}
    
    void setProperty(int id, float [] value){
        switch(id){
        case AnimationTrack.COLOR:
            color = setColor(value, color);
            break;
        case AnimationTrack.INTENSITY:
            intensity = value[0];
            break;
        case AnimationTrack.SPOT_ANGLE:
            spotAngle = Math.max(0, Math.min(90, value[0]));
            break;
        case AnimationTrack.SPOT_EXPONENT:
            spotAngle = Math.max(0, Math.min(128, value[0]));
            break;
        default:
            super.setProperty(id, value);
        }
    }

}
