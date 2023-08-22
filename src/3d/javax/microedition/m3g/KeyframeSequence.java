/*
 * Created on Oct 23, 2005 by Stefan Haustein
 */
package javax.microedition.m3g;

import java.io.IOException;
import java.util.Hashtable;

public class KeyframeSequence extends Object3D {

	static final boolean NO_QUAT = true;
	
	static final float[] H = new float[] { 
		 2, -2,  1,  1,
		-3,  3, -2, -1,
		 0,  0,  1,  0,
		 1,  0,  0,  0
	};
	
	public static final int 	LINEAR =	176;
	public static final int 	SLERP =	177;
	public static final int 	SPLINE =	178;
	public static final int 	SQUAD =	179;
	public static final int 	STEP 	=180;

    public static final int     CONSTANT =  192;
    public static final int     LOOP =  193;

    
	int interpolation;
	int repeatMode;
	int duration;
	int validRangeFirst;
	int validRangeLast;
	int componentCount;
	int keyframeCount;
	int validCount;
	int[] times;
	float[] values;
	
	
	public KeyframeSequence(int numKeyframes, int numComponents, int interpolation){
		//Constructs a new keyframe sequence with specified interpolation method, number of components per keyframe, and number of keyframes.
		this.keyframeCount = numKeyframes;
		this.componentCount = numComponents;
		this.interpolation = interpolation;
		times = new int[keyframeCount];
		values = new float[keyframeCount * componentCount];
        validRangeLast = numKeyframes-1;
        validCount = keyframeCount;
        
//        System.out.println("Animation interpolation: "+interpolation);
		
        
        if((interpolation == SLERP || interpolation == SQUAD) && numComponents != 4){
        	throw new IllegalArgumentException("SLERP and SQUAD interpolations must operate on quaternions (numComponents must be 4)");
        }
	}
		
    /** Used by loader */
    KeyframeSequence() {
    }

    public int getDuration(){
    	//Gets the duration of this sequence.
		return duration;
	}
	
	public int getRepeatMode(){
//    Retrieves the current repeat mode of this KeyframeSequence.
		return repeatMode;
	}
	
	public void setDuration(int duration){
    	//Sets the duration of this sequence in sequence time units.
		this.duration = duration;
	}

	public void 	setKeyframe(int index, int time, float[] value){
    	//Sets the time position and value of the specified keyframe.
		times[index]= time;
		System.arraycopy(value, 0, values, index*componentCount, componentCount);
	}
	
	public void 	setRepeatMode(int mode){
    //Sets the repeat mode of this KeyframeSequence.
		this.repeatMode = mode;
	}
	
	public void 	setValidRange(int first, int last){
    //Selects the range of keyframes that are included in the animation. 
		this.validRangeFirst = first;
		this.validRangeLast = last;
		
		if(validRangeFirst == validRangeLast){
			validCount = keyframeCount;
		}
		else if(validRangeFirst <= validRangeLast){
			validCount = validRangeLast - validRangeFirst + 1;
		}
		else {
			validCount = validRangeLast + 1 + times.length-validRangeFirst;
		}
	}
	

	void load(M3gInputStream is) throws IOException {
		//Byte          interpolation;
		//Byte          repeatMode;
		//Byte          encoding;
		//UInt32        duration;
		//UInt32        validRangeFirst;
		//UInt32        validRangeLast;
		//UInt32        componentCount;
		//UInt32        keyframeCount;
		//IF encoding == 0
		//   FOR each key frame...
		//        Int32                   time;
		//        Float32[componentCount] vectorValue;
		//   END
		// ELSE IF encoding == 1
		//    Float32[componentCount] vectorBias;
		//    Float32[componentCount] vectorScale;
		//    FOR each key frame...
		//        Int32                time;
		//        Byte[componentCount] vectorValue;
		// END
		//ELSE IF encoding == 2
		//    Float32[componentCount] vectorBias;
		//    Float32[componentCount] vectorScale;
		//    FOR each key frame...
		//        Int32                  time;
		//        UInt16[componentCount] vectorValue;
		//    END
		// END
		
        _load(is);
		this.interpolation = is.read();
		this.repeatMode = is.read();
		
		int encoding = is.read();
		
		this.duration = is.readInt32();
		int first = is.readInt32();
		int last = is.readInt32();
		this.componentCount = is.readInt32();
		this.keyframeCount = is.readInt32();

		
		
		times = new int[keyframeCount];
		values = new float[keyframeCount * componentCount];
		
		if(encoding == 0){
			for(int i = 0; i < keyframeCount; i++){
				times[i] = is.readInt32();
				for(int j = 0; j < componentCount; j++){
					values[i*componentCount+j] = is.readFloat32();
				}
			}
		}
		else if(encoding <= 2){
			float[] bias = new float[componentCount];
			float[] scale = new float[componentCount];
			for(int i = 0; i < componentCount; i++){
				bias[i] = is.readFloat32();
			}
			for(int i = 0; i < componentCount; i++){
				scale[i] = is.readFloat32();
			}
			for(int i = 0; i < keyframeCount; i++){
				times[i] = is.readInt32();
				for(int j = 0; j < componentCount; j++){
					float n = encoding == 1 ? is.read()/255.0f: is.readUInt16()/65535.0f;
					values[i*componentCount+j] = n*scale[j]+bias[j];
				}
			}
			
		}
		else{
			throw new RuntimeException("Illegal encoding: "+encoding);
		}
        
//		System.out.println("Animation interpolation: "+interpolation);
		
		setValidRange(first, last);
        //dump();
	}


	public int getReferences(Object3D[] references) {
		// Auto-generated method stub
		return _getReferences(references);
	}


	public Object3D duplicate() {
        KeyframeSequence target = new KeyframeSequence(keyframeCount, componentCount, interpolation);
        target.validRangeFirst = validRangeFirst;
        target.validRangeLast = validRangeLast;
        System.arraycopy(values, 0, target.values, 0, values.length);
        
        return _duplicate(target);
	}

    
	/** Map logical index to actual array index, taking validRangeFirst and last into account */
	
	int realindex(int i){
		while(i < 0){
			i += validCount;
		}

		i = i % validCount;
		
		i += validRangeFirst;
		
		if(i >= times.length){
			i -= times.length;
		}
		
		return i;
	}
	
	
	int t(int i){
		int t0 = 0;
		while(i >= validCount){
			i -= validCount;
			t0 += duration;
		}
		while(i < 0){
			i += validCount;
			t0 -= duration;
		}
		return times[realindex(i)]+t0;
	}
	
	
	float v(int i, int j){
		return values[realindex(i)*componentCount + j];
	}


	float Fp(int i){
		if(repeatMode == CONSTANT && (i <= 0 || i >= validCount-1)){
			return 0;
		}
		return 2 * (t(i) - t(i-1)) / (t(i+1)-t(i-1));
	}

	
	float Fm(int i){
		if(repeatMode == CONSTANT && (i <= 0 || i >= validCount-1)){
			return 0;
		}
		return 2 * (t(i+1) - t(i)) / (t(i+1)-t(i-1));
	}
	
	
	float[]q(int i){
		return new float[]{v(i,0), v(i,1), v(i, 2), v(i, 3)};
	}
	
    float[] calcValue(int t) {
    
    		if(NO_QUAT && (interpolation == SLERP || interpolation == SQUAD)){
//    			System.out.println("Debug: Using SPLINE instead of SLERP or SQUAD");
    			interpolation = SPLINE;
    		}
    	
        float[] result = new float[componentCount];
        
        if(repeatMode == LOOP){
            t = t % duration;
        }
     
        
        int ip1 = 0;

        // find end range: time that is greater than t
        
        while(true){
            if(t(ip1) > t) break;
            if(ip1 == validCount-1 && repeatMode == CONSTANT){
            		System.arraycopy(values, realindex(ip1) * componentCount, result, 0, componentCount);
            		return result;
            }
            
         	ip1++;
        }
        
        
        if(repeatMode == CONSTANT && ip1 == 0){
            System.arraycopy(values, realindex(ip1) * componentCount, result, 0, componentCount);
            return result;
        }
        
        int i = ip1-1;
    
        float t_ip1 = t(ip1);
        float t_i = t(i);
        
        float s = (t - t_i) / (t_ip1 - t_i);

        switch(interpolation){
        case STEP:
            System.arraycopy(values, i*componentCount, result, 0, componentCount);
            break;
         
        case SPLINE:{
        		
        	float SH[] = Transform.mulVM(new float[]{s*s*s, s*s, s, 1}, H);
        		
        	float Fm_i = Fm(i);
        	float Fp_ip1 = Fp(i+1);
        		
        	for(int j = 0; j < componentCount; j++){
        		float T_i = (v(i+1, j) - v(i-1, j)) / 2;
        		float T_ip1 = (v(i+2, j) - v(i, j)) / 2;
        		float T0_i = Fm_i * T_i;
        		float T1_ip1 = Fp_ip1 * T_ip1;
        			
        		result[j] = VMath.dot(SH, 
        				new float[]{v(i,j), v(i+1,j), T0_i, T1_ip1});
        	}	
        	break;
        }
        		
        	
        case SLERP: {
        	
        	result = QMath.slerp(s, q(0), q(1));
        	break;
    	}
            
        case SQUAD:{
        	float[] log1 = QMath.log(QMath.mul(QMath.inverse(q(i)), q(i+1)));
        	float[] log2 = QMath.log(QMath.mul(QMath.inverse(q(i-1)), q(i)));
        	float[] T_i = VMath.mul(0.5f, VMath.add(log1, log2));
        	
        	float[] log3 = QMath.log(QMath.mul(QMath.inverse(q(i+1)), q(i+2)));
        	float[] log4 = log1;
        	float[] T_ip1 = VMath.mul(0.5f, VMath.add(log3, log4));
        	
        	float[] T0_i = VMath.mul(Fm(i), T_i);
        	float[] T1_ip1 = VMath.mul(Fp(i+1), T_ip1);
        	
        	float[] a_i = QMath.mul(q(i), 
        			QMath.exp(VMath.mul(0.5f, VMath.sub(T0_i, log1))));
        	float[] b_ip1 = QMath.mul(q(i+1),
        			QMath.exp(VMath.mul(0.5f, VMath.sub(log4, T1_ip1))));
        	
        	result = QMath.slerp(2*s*(1-s), 
        				QMath.slerp(s, q(i), q(i+1)), 
        				QMath.slerp(s, a_i, b_ip1));
        	break;
        }
            
        case LINEAR:

            for(int j = 0; j < componentCount; j++){
                result[j] = (1-s)*v(i,j) + s*v(ip1,j);
            }
            break;
            
        default:
        		throw new RuntimeException("Illegal Interpolation type:" + interpolation);
        }
        
        
        for(int j = 0; j < result.length; j++){
        		float f = result[j];
        		if(Float.isInfinite(f) || Float.isNaN(f)){
//        			System.out.println("Invalid value: "+f+ " index: "+j +"/"+componentCount);
//        			System.out.println(" - interpolation: "+interpolation+" repeatMode: "+repeatMode+ " keyFrameCount: "+keyframeCount);
//        			System.out.println(" - keyframeCount: "+keyframeCount+ " validRange: "+validRangeFirst+"..."+validRangeLast+" validCount: "+validCount);
//        			System.out.println(" - t: "+t + " i: "+i + " s:"+s);

        			result[j] = 0;
        		}
        }
        
        return result;
    }
    
    Hashtable getProperties(){
        Hashtable props = super.getProperties();
        props.put("interpolation", new Integer(interpolation));
        return props;
    }

}
