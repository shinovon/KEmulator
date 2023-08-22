/*
 * Created on 15.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package javax.microedition.m3g;

import java.io.IOException;

/**
 * @author stefan.haustein
 */
public class Camera extends Node {

	public static final int 	GENERIC =	48;
	public static final int 	PARALLEL =	49;
	public static final int 	PERSPECTIVE =	50;
	
    int projectionType;
    Transform projectionMatrix = new Transform();
    float fovy;
    float aspectRatio;
    float near;
    float far;

    public Camera(){
    	//Constructs a new Camera node with default values.
    }
    
    public int 	getProjection(float[] params){
    	//Gets the current projection parameters and type.
    	
    	if(projectionType != GENERIC){
    		params[0] = fovy;
    		params[1] = aspectRatio;
    		params[2] = near;
    		params[3] = far;
    	}
    	return projectionType;
    	
    }
    	
    public int 	getProjection(Transform transform){
    	//Gets the current projection matrix and type.
        transform.set(projectionMatrix);
    	    return projectionType;
    }
    
    public void 	setGeneric(Transform transform){
    	    //Sets the given 4x4 transformation as the current projection matrix.
        projectionMatrix.set(transform);
        projectionType = GENERIC;
    }
    
    
    /**
       h = height (= fovy)
       w = aspectRatio * h
       d = far - near

      Denoting the width, height and depth of the view volume by w, h and d, respectively, the parallel projection matrix P is constructed as follows.

       2/w       0          0           0
         0       2/h         0           0
         0        0        -2/d   -(near+far)/d
         0        0          0           1
      */

    public void 	setParallel(float h, float aspectRatio, float near, float far){
        this.projectionType = PARALLEL;
        this.fovy = h;
        this.aspectRatio = aspectRatio;
        this.near = near;
        this.far = far;
        float w = aspectRatio * h;
        float d = far-near;
        float[] transform = {
            2/w, 0, 0, 0,
            0, 2/h, 0, 0,
            0, 0, -2/d, -(near+far)/d,
            0, 0, 0, 1
        };
        projectionMatrix.set(transform);
    	// TODO
    }
    
    /**
     h = tan(fovy/2)
     w = aspectRatio * h
     d = far - near

     1/w         0            0               0
     0         1/h           0               0
     0          0      -(near+far)/d   -2*near*far/d
     0          0           -1               0
     */
    
    public void 	setPerspective(float fovy, float aspectRatio, float near, float far){
        this.projectionType = PARALLEL;
        this.fovy = fovy;
        this.aspectRatio = aspectRatio;
        this.near = near;
        this.far = far;
        
        float h = (float) Math.tan(Math.toRadians(fovy/2));
        float w = aspectRatio * h;
        float d = far-near;
        
        float[] matrix = {
                1/w, 0, 0, 0,
                0, 1/h, 0, 0,
                0, 0, -(near+far)/d, -2*near*far/d,
                0, 0, -1, 0};

        projectionMatrix.set(matrix);
    }
	

    
    

	void load(M3gInputStream is) throws IOException {
		
		//Byte          projectionType;
		//IF projectionType==GENERIC, THEN
		//    Matrix        projectionMatrix;
		//ELSE
		//    Float32       fovy;
		//    Float32       AspectRatio;
		//    Float32       near;
		//    Float32       far;
		//END

		_load(is);
		projectionType = is.read();
		if(projectionType == GENERIC){
			projectionMatrix = new Transform();
			projectionMatrix.set(is.readMatrix());
		}
		else{
			fovy = is.readFloat32();
			aspectRatio = is.readFloat32();
			near = is.readFloat32();
			far = is.readFloat32();
			if(projectionType == PARALLEL){
				setParallel(fovy, aspectRatio, near, far);
			}
			else{
				setPerspective(fovy, aspectRatio, near, far);
			}
		}
		
		
	}


	public int getReferences(Object3D[] refs) {
		return _getReferences(refs);
	}


	public Object3D duplicate() {
		Camera clone = new Camera();
		switch(projectionType){
		case GENERIC:
			clone.setGeneric(projectionMatrix);
			break;
		case PARALLEL:
			clone.setParallel(fovy, aspectRatio, near, far);
			break;
			
		case PERSPECTIVE:
			clone.setPerspective(fovy, aspectRatio, near, far);
			break;	
		}
		
		return _duplicate(clone);
	}

    void setProperty(int id, float [] value){
        switch(id){
        case AnimationTrack.FAR_DISTANCE:
            far = value[0];
            break;
        case AnimationTrack.FIELD_OF_VIEW:
            fovy = value[0];
            break;
        case AnimationTrack.NEAR_DISTANCE:
            near = value[0];
        default:
            super.setProperty(id, value);
        }
        
        switch(projectionType){
        case PARALLEL:
            setParallel(fovy, aspectRatio, near, far);
            break;
            
        case PERSPECTIVE:
            setPerspective(fovy, aspectRatio, near, far);
            break;  
        }
    }


    
}


