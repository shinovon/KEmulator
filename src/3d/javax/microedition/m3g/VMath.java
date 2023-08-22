/*
 * Created on Jan 27, 2006 by Stefan Haustein
 */
package javax.microedition.m3g;

/** Vector helper methods */

class VMath {

	/** Add two vectors, matrices or quaternions. */
	
	static float[] add(float[] a, float[] b){
		if(a.length != b.length) throw new IllegalArgumentException("Dimension mismatch");
		float[] result = new float[a.length];
		for(int i = 0; i < a.length; i++){
			result[i] = a[i]+b[i];
		}
		return result;
	}

	/** Subtract two vectors, matrices or quaternions. */

	static float[] sub(float[] a, float[] b) {
		if(a.length != b.length) throw new IllegalArgumentException("Dimension mismatch");
		float[] result = new float[a.length];
		for(int i = 0; i < a.length; i++){
			result[i] = a[i]-b[i];
		}
		return result;
	}

	/** Dot product of a transposed vector and a vector */
	    
	static float dot(float[] vt, float[] v){
	  	   //              0
	  	   //              1
	  	   //              2
	  	   //              3
	  	   // 0 1 2 3 
	
		float result = 0;
		if(vt.length != v.length) throw new IllegalArgumentException("Dimension Mismatch");
		  
		for(int i = 0; i < vt.length; i++){
			result += vt[i] * v[i];
		}
		  
		return result;
	}

	 /**
	   * Multiply a skalar and a vector or a matrix 
	   * */
	  
	  static float[] mul(float s, float[] v){
		  float[] result = new float[v.length];
		  for(int i=0; i<v.length;i++){
			  result[i] = s*v[i];
		  }
		  return result;
	  }

	  
	 /**
	   * Multiply a vector or a matrix and a skalar
	   * */
	  
	  static float[] mul(float[] v, float s){
		  float[] result = new float[v.length];
		  for(int i=0; i<v.length;i++){
			  result[i] = s*v[i];
		  }
		  return result;
	  }

	 /**
	   * Multiply a vector or a matrix and a skalar
	   * */
	  
	  static float[] div(float[] v, float s){
		  float[] result = new float[v.length];
		  for(int i=0; i<v.length;i++){
			  result[i] = v[i]/s;
		  }
		  return result;
	  }

	/** Computes the length of a vector or the absolute of a quaternion */
	
	static float abs(float[] v){
		float result = 0;
		for(int i = 0; i < v.length; i++){
			result += v[i]*v[i];
		}
		return (float) Math.sqrt(result);
	}

	
	static float[] crossProduct(float[] a, float[] b) {
		return new float[]{
				a[1]*b[2] - a[2]*b[1],
				a[2]*b[0] - a[0]*b[2],
				a[0]*b[1] - a[1]*b[0]};
	}

	static float[] normalize(float[] v) {
        return div(v, abs(v));
    }
	  
}
