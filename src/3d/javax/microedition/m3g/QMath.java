/*
 * Created on Jan 27, 2006 by Stefan Haustein
 */
package javax.microedition.m3g;

/** Quaternion calculation utility methods */

class QMath {

	/** Package-visible static helper method that multiplies two quaternions. Source:
	   *  http://en.wikipedia.org/wiki/Quaternion */
	  
	  	static float[] mul(float[] p, float[] q){
	  		float a = p[0];
	  		float b = p[1];
	  		float c = p[2];
	  		float d = p[3];
	  		
	  		float t = q[0];
	  		float x = q[1];
	  		float y = q[2];
	  		float z = q[3];
	  		
	  		return new float[]{
	  			a*t - b*x - c*y - d*z,
	  			b*t + a*x + c*z - d*y,
	  			c*t + a*y + d*x - b*z,
	  			d*t + a*z + b*y - c*x};
	  	}

	/** package visible static helper method that returns the inverse of a quaternion.
	 * Source: http://de.wikipedia.org/wiki/Quaternion */
	
	static float[] inverse(float[] q){
		float a = q[0];
		float b = q[1];
		float c = q[2];
		float d = q[3];
		
		float div = a*a + b*b + c*c + d*d;
		
		return new float[]{a/div, -b/div, -c/div, -d/div};
	}

	/** Computes the quaternion logarithm */
	
	static float[] log(float[] q){
		return VMath.add(
				new float[]{(float) Math.log(VMath.abs(q)), 0, 0, 0},
				VMath.mul(QMath.arg(q), QMath.sgn(QMath.vector(q))));
	}

	static float[] exp(float[] p){
		float[] u = QMath.vector(p);
		return VMath.mul(
				(float) Math.exp(p[0]), 
				VMath.add(
					new float[]{(float) Math.cos(VMath.abs(u)), 0,0,0},
					VMath.mul((float) Math.sin(VMath.abs(u)), QMath.sgn(u))));
	}

	static float arg(float[] p){
	  return (float) Math.acos(p[0] / VMath.abs(p));
	}

	static float[] vector(float[] q){
		return new float[]{0, q[1], q[2], q[3]};
	}

	static float[] sgn(float[] q){
		float abs = VMath.abs(q);
		return new float[]{q[0]/abs, q[1]/abs, q[2]/abs, q[3]/abs};
	}

	static float[] slerp(float s, float[] p, float[] q) {
		// mostly inspired by http://number-none.com/product/Understanding%20Slerp,%20Then%20Not%20Using%20It/
		
		float a = (float) Math.acos(Math.min(1, Math.max(-1, VMath.dot(p, q))));
		
		return VMath.add(
				VMath.mul((float) (Math.sin((1-s)*a)/ Math.sin(a)), p),
				VMath.mul((float) (Math.sin(s*a)    / Math.sin(a)), q));
	}

}
