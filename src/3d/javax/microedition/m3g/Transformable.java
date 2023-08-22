/*
 * Created on Oct 23, 2005 by Stefan Haustein
 */
package javax.microedition.m3g;

import java.io.IOException;

public abstract class Transformable extends Object3D {

	float[] translation = {0, 0, 0};
	float[] scale = {1, 1, 1};
//	float orientationAngle = 0;
//	float[] orientationAxis = {0, 0, 1};
    Transform rotation = new Transform();
	Transform transform = new Transform();
	
	
	public void 	getCompositeTransform(Transform result){
	    result.set(new float[]{
                1, 0, 0, translation[0], 
                0, 1, 0, translation[1], 
                0, 0, 1, translation[2],
                0, 0, 0, 1});
        
        float[] rotate = new float[4]; 
        getOrientation(rotate);
        result.postRotate( rotate[0], rotate[1], rotate[2], rotate[3]);
        
        result.postScale(scale[0], scale[1], scale[2]);
        result.postMultiply(transform);
	}
	
	public void 	getOrientation(float[] angleAxis){
//	    Retrieves the orientation component of this Transformable.

        // extract quaternion from rotation matrix

        float[] m = rotation.matrix;
        
        float m00 = m[0];
        float m01 = m[1];
        float m02 = m[2];
        // 
        float m10 = m[4];
        float m11 = m[5];
        float m12 = m[6];
        // 
        float m20 = m[8];
        float m21 = m[9];
        float m22 = m[10];
        
        float w = (float) Math.sqrt(1+m00+m11+m22)/2;
        float w4 = 4*w;
        float []quat = { 
                w,
                (m21-m12)/w4,
                (m02-m20)/w4,
                (m10-m01)/w4
        };
        
//        quat = VMath.normalize(quat); // id. for quat and vect.

        w = quat[0];
        float x = quat[1];
        float y = quat[2];
        float z = quat[3];
        
        // get angleAxis from quaternion
        double s = Math.sqrt(1-w*w);
        
        if (s != 0)
        {        
            angleAxis[0] = (float) Math.toDegrees(2*Math.acos(w));
            angleAxis[1] = (float) (x/s);
            angleAxis[2] = (float) (y/s);
            angleAxis[3] = (float) (z/s);
        }
        else
        {
            angleAxis[0] = (float) 0;
            angleAxis[1] = (float) 0;
            angleAxis[2] = (float) 0;
            angleAxis[3] = (float) 1;            
        }
        
        if (Float.isNaN(angleAxis[0]))
            angleAxis[0] = 0;
        
        if (Float.isNaN(angleAxis[1]))
            angleAxis[1] = 0;

        if (Float.isNaN(angleAxis[2]))
            angleAxis[2] = 0;

        if (Float.isNaN(angleAxis[3]))
            angleAxis[3] = 0;

        if (Float.isInfinite(angleAxis[0]))
            angleAxis[0] = 0;

        if (Float.isInfinite(angleAxis[1]))
            angleAxis[1] = 1;

        if (Float.isInfinite(angleAxis[2]))
            angleAxis[2] = 1;

        if (Float.isInfinite(angleAxis[3]))
            angleAxis[3] = 1;

        if (            (angleAxis[1] == 0)&&
                        (angleAxis[2] == 0)&&
                        (angleAxis[3] == 0)
                        )
        {
            angleAxis[0] = (float) 0;
            angleAxis[1] = (float) 0;
            angleAxis[2] = (float) 0;
            angleAxis[3] = (float) 1;            
        }
        
	}
	
	public void 	getScale(float[] xyz){
		//Retrieves the scale component of this Transformable.
		System.arraycopy(scale,0,xyz,0,3);
	}

    public void 	getTransform(Transform transform){
		//Retrieves the matrix component of this Transformable.
		transform.set(this.transform);
	}
	
    public void 	getTranslation(float[] xyz){
    //Retrieves the translation component of this Transformable.
		System.arraycopy(translation, 0, xyz, 0, 3);
	}
	
	
    public void 	postRotate(float angle, float ax, float ay, float az){
        rotation.postRotate(angle, ax, ay, az);
	}
	
    public void 	preRotate(float angle, float ax, float ay, float az){
        Transform result = new Transform();
        result.postRotate(angle, ax, ay, az);
        result.postMultiply(rotation);
        rotation = result;
	}
	
    public void 	scale(float sx, float sy, float sz){
		//    Multiplies the current scale component by the given scale factors.
		scale[0] *= sx;
		scale[1] *= sy;
		scale[2] *= sz;
	}
		
    public void 	setOrientation(float angle, float ax, float ay, float az){
			//Sets the orientation component of this Transformable.
        
        rotation = new Transform();
        rotation.postRotate(angle, ax, ay, az);

//        float[] go = new float[4];
//        getOrientation(go);
//        
//        System.out.println("set: "+angle+", "+ax+", "+ay+", "+az);
//        System.out.println("get: "+go[0]+", "+go[1]+", "+go[2]+", "+go[3]);
        
/*        
        orientationAngle = angle;
        orientationAxis[0] =ax;
        orientationAxis[1] = ay;
        orientationAxis[2] = az;*/
    }
		
    public void 	setScale(float sx, float sy, float sz){
    //Sets the scale component of this Transformable.
        scale[0] =sx;
        scale[1] =sy;
        scale[2] = sz;
    }
		
    public void	setTransform(Transform transform){
//    Sets the matrix component of this Transformable by copying in the given Transform.
        this.transform.set(transform);
    }
		
    public void setTranslation(float tx, float ty, float tz){
//    Sets the translation component of this Transformable.
        translation[0] = tx;
        translation[1] = ty;
        translation[2] = tz;
    }
		
    public void translate(float tx, float ty, float tz){
    //Adds the given offset to the current translation component.
        translation[0] += tx;
        translation[1] += ty;
        translation[2] += tz;
    }
	
	void _load(M3gInputStream is) throws IOException{
		super._load(is);
		
		//Boolean       hasComponentTransform;
		//IF hasComponentTransform==TRUE, THEN
		//    Vector3D      translation;
		//    Vector3D      scale;
		//    Float32       orientationAngle;
		//    Vector3D      orientationAxis;
		//END
		//Boolean       hasGeneralTransform;
		//IF hasGeneralTransform==TRUE, THEN
		//    Matrix        transform;
		//END
		
		if(is.readBoolean()){
			translation = is.readVector();
			scale = is.readVector();
			float orientationAngle = is.readFloat32();
			float[] orientationAxis = is.readVector();
            setOrientation(orientationAngle, orientationAxis[0], orientationAxis[1], orientationAxis[2]);
		}
		
		if(is.readBoolean()){
			transform.set(is.readMatrix());
		}
		
	}
    
    
    
    void setProperty(int id, float [] value){
        switch(id){
        case AnimationTrack.ORIENTATION:
            rotation = new Transform();
            rotation.postRotateQuat(value[0], value[1], value[2], value[3]);
            break;
        case AnimationTrack.SCALE:
            if(value.length < 3){
                scale[0] = scale[1] = scale[2] = value[0];
            }
            else{
                System.arraycopy(value, 0, scale, 0, 3);
            }
            break;
        case AnimationTrack.TRANSLATION:
            System.arraycopy(value, 0, translation, 0, 3);
            break;
        default:
            super.setProperty(id, value);
        }
    }
    
	
}
