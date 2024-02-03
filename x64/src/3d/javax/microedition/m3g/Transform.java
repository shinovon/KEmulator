/*
 * Created on 15.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package javax.microedition.m3g;

/**
 * @author stefan.haustein

 */
public class Transform {
    
    static final float [] IDENTITY = {
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1};
    
    float[] matrix = new float[16];
    
    /** Package-visible static helper method that multiplies two 4x4 matrices. */

    static float[] mulMM(float[] m1, float[] m2){
    	
    		if(m1.length != 16 || m2.length != 16){
    			throw new IllegalArgumentException();
    		}
    	
        float[] result = new float[16];
        for(int i = 0; i < 4; i++){     
            for(int j = 0; j < 4; j++){ 
                float rij = 0;
                for(int k = 0; k < 4; k++){
                    rij += m1[j*4+k] * m2[i+4*k];
                }
//                result[i*4+j] = rij;
               result[i+j*4] = rij;
            }
        }
        return result;
    }

    
    /** 
     * Multiply a transposed 3-vector and the given 4th component and the 4x4 matix,
     * yielding a 4-vector 
     * */
    
    static float[] mulVM(float[] vt, float vt3, float[] m){
    	  //          0  1  2  3 
    	  //          4  5  6  7
    	  //          8  9 10 11
    	  //         12 13 14 15
    	  // 0 1 2 3 
    	
    	  return new float[] {vt[0]*m[0] + vt[1]*m[4] + vt[2]*m[ 8] + vt3*m[12],
		  	         		  vt[0]*m[1] + vt[1]*m[5] + vt[2]*m[ 9] + vt3*m[13],
		  	         		  vt[0]*m[2] + vt[1]*m[6] + vt[2]*m[10] + vt3*m[14],
		  	         		  vt[0]*m[3] + vt[1]*m[7] + vt[2]*m[11] + vt3*m[15]};
    	
    }
    
    
    /** Package-visible static helper method that multiplies a transposed 4-vector 
     * and a matrix, yielding a 4-vector */

    static float[] mulVM(float[] vt, float[] m){
  
      if(vt.length != 4){
    	  	throw new IllegalArgumentException();
      }
      
      return mulVM(vt, vt[3], m); 	
  }
    
    
    static float[] mulMV(float[] m, float[] v){
    	if(m.length != 16 || v.length != 4) throw new IllegalArgumentException();
    	return mulMV(m, v, v[3]);
    }
	
    static float[] mulMV(float[] m, float[] v, float v3){

      //              0
      //              1
      //              2
      //              3
	  //  0  1  2  3 
	  //  4  5  6  7
	  //  8  9 10 11
	  // 12 13 14 15
	  // 
    
      return new float[]{
    	  m[ 0]*v[0] + m[ 1]*v[1] + m[ 2]*v[2] + m[ 3]*v3,
    	  m[ 4]*v[0] + m[ 5]*v[1] + m[ 6]*v[2] + m[ 7]*v3,
    	  m[ 8]*v[0] + m[ 9]*v[1] + m[10]*v[2] + m[11]*v3,
    	  m[12]*v[0] + m[13]*v[1] + m[14]*v[2] + m[15]*v3};
    }
    
    
  
    //    Constructs a new Transform object and initializes it to the 4x4 identity matrix.
    public Transform() {
        setIdentity();
    }
    
   // Constructs a new Transform object and initializes it by copying in the contents of the given Transform.
    public Transform(Transform transform){
        System.arraycopy(transform.matrix, 0, matrix, 0, 16); 
    }

    //     Retrieves the contents of this transformation as a 16-element float array.
    public void    get(float[] matrix) {
        System.arraycopy(this.matrix, 0, matrix, 0, 16);
    }
    
    /** Helper for invert */
    
    private static void transform(int line0, float factor, int line1, float[] m1, float[] m2){
        // System.out.println("add "+factor+" * line "+line1 +" to line "+line0);
        
        line0 *= 4;
        line1 *= 4;
        for(int i = 0; i < 4; i++){
            m1[line0+i] += factor * m1[line1+i];
            m2[line0+i] += factor * m2[line1+i];
        }   
    }
    
    
    void dump(String label){
        dump(label, matrix, null);
    }
        
    static void dump(String label, float[] matrix, float[] m2){
        
        System.out.println(label);
        
        for(int i = 0; i < 4; i++){
            StringBuffer line = new StringBuffer();
            for (int j = 0; j < 4; j++){
                line.append('\t');
                line.append(matrix[i*4+j]);
            }
            
            if(m2 != null){
                for (int j = 0; j < 4; j++){
                    line.append('\t');
                    line.append(m2[i*4+j]);
                }                
            }
            System.out.println(line.toString());
        }
    }
    
    
    //Inverts this matrix, if possible.
    public void invert() {
        
        float[] result = new float[16];
        System.arraycopy(IDENTITY, 0, result, 0, 16);
        
        for(int i = 0; i < 4; i++){
            int i4 = i*4;
            
            // make sure[i,i] is != 0
            
            for(int j = 0; matrix[i4+i] == 0 && j < 4; j++){
                if(j != i && matrix[j*4+i] != 0){
                    transform(i, 1, j, matrix, result);
                }
            }
            
            // ensure tailing 0s
            
            for(int j = 0; j < i; j++){
                if(matrix[i4+j] != 0){
                    transform(i, -matrix[i4+j]/matrix[j*4+j], j, matrix, result);
                }
            }

            if(matrix[i4+i] == 0){
                throw new IllegalArgumentException("Not invertable");
            }

 //           dump("row "+i+" leading zeros", matrix, result);
        }
        
        for(int i = 3; i >= 0; i--){
            int i4 = i*4;
            if(matrix[i4+i] != 1){
                float f = matrix[i4+i];
                matrix[i4+i] = 1;
                for(int j = 0; j < 4; j++){
                    result[i4+j] /= f;
                    if(j > i){
                        matrix[i4+j] /= f;
                    }
                }
            }

//            dump("row "+i+" leading 1", matrix, result);
            
            for(int j = i+1; j < 4; j++){
                if(matrix[i*4+j] != 0){
                    transform(i, -matrix[i*4+j], j, matrix, result);
                }
            }

//            dump("row "+i+" tailing 0", matrix, result);

        }

        matrix = result;
    }

    // Multiplies this transformation from the right by the given transformation.

    public     void    postMultiply(Transform transform) {
        matrix = mulMM(matrix, transform.matrix);
    }

    //  Multiplies this transformation from the right by the given rotation matrix, specified in axis-angle form.
    public     void    postRotate(float angle, float x, float y, float z){
        angle = (float)Math.toRadians(angle);
        
        float len = (float) Math.sqrt(x*x+y*y+z*z);
        if(len != 1){
            x /= len;
            y /= len;
            z /= len;
        }
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        
        float[] r = {
                x*x*(1-c)+c ,   x*y*(1-c)-z*s,   x*z*(1-c)+y*s,   0,
                y*x*(1-c)+z*s,   y*y*(1-c)+c,    y*z*(1-c)-x*s,   0,
                x*z*(1-c)-y*s,   y*z*(1-c)+x*s,   z*z*(1-c)+c,    0,
                     0,            0   ,         0,       1};

        matrix = mulMM(matrix, r);
    }
    
    
    
    
//Multiplies this transformation from the right by the given rotation matrix, specified in quaternion form.
    public void    postRotateQuat(float x, float y, float z, float w) {
        
       float len = (float) Math.sqrt(x*x+y*y+z*z+w*w);
       if(len != 1){
           x /= len;
           y /= len;
           z /= len;
           w /= len;
       }
       
       float[] r = {  
               1 - (2*y*y + 2*z*z),      2*x*y - 2*z*w,       2*x*z+2*y*w,   0,
                    2*x*y + 2*z*w,  1 - (2*x*x + 2*z*z),      2*y*z-2*x*w,   0,
                    2*x*z - 2*y*w,       2*y*z + 2*x*w,  1 - (2*x*x+2*y*y),  0,
                                0,                   0,                 0,   1.0f};
       matrix = mulMM(matrix, r);
    }
    
    //Multiplies this transformation from the right by the given scale matrix.
    public void    postScale(float sx, float sy, float sz) {
        float[] s = {
        sx,  0,   0,   0,
        0,   sy,  0,   0,
        0,   0,   sz,  0,
        0,   0,   0,   1};
        
        matrix = mulMM(matrix, s);
    }
    
    //Multiplies this transformation from the right by the given translation matrix.
    public void    postTranslate(float tx, float ty, float tz) {
        float[] t = {
                1, 0, 0, tx,
                0, 1, 0, ty,
                0, 0, 1, tz,
                0, 0, 0, 1};
 
        matrix = mulMM(matrix, t);
    }

    //Sets this transformation by copying from the given 16-element float array.
    public void    set(float[] matrix) {
        System.arraycopy(matrix, 0, this.matrix, 0, 16);
    }

    //Sets this transformation by copying the contents of the given Transform.
    public void    set(Transform transform) {
    	if(transform == null){ // TODO: Quick workaround for wap3 cars problem
    		setIdentity();
    	}
    	else {
    		System.arraycopy(transform.matrix, 0, this.matrix, 0, 16);
    	}
    }
    
    //Replaces this transformation with the 4x4 identity matrix.
    public void    setIdentity() {
        System.arraycopy(IDENTITY, 0, matrix, 0, 16);
    }
    

    /** multiplies the transposed vector with the given matrix
     * 
     * @param vect
     */
    
    
    
    //Multiplies the given array of 4D vectors or a single 3D vector  with this matrix.
    
    private void transform(float[] v, int len){
    	
    	
    		
        if((len & 3) != 0) throw new IllegalArgumentException();
        
        for(int i = 0; i < len; i += 4){

            //              0
            //              1
            //              2
            //              3
        	//  0  1  2  3 
      	  	//  4  5  6  7
      	  	//  8  9 10 11
      	  	// 12 13 14 15
      	  	// 

        	float v0 = v[i];
        	float v1 = v[i+1];
        	float v2 = v[i+2];
        	float v3 = v[i+3];
        	
        	v[i]   = matrix[ 0]*v0 + matrix[ 1]*v1 + matrix[ 2]*v2 + matrix[ 3]*v3;
          	v[i+1] = matrix[ 4]*v0 + matrix[ 5]*v1 + matrix[ 6]*v2 + matrix[ 7]*v3;
          	v[i+2] = matrix[ 8]*v0 + matrix[ 9]*v1 + matrix[10]*v2 + matrix[11]*v3;
          	v[i+3] = matrix[12]*v0 + matrix[13]*v1 + matrix[14]*v2 + matrix[15]*v3;
        	
        }

    }
    
    
    public void  transform(float[] vectors) {
        transform(vectors, vectors.length);
    }
    
    //Multiplies the elements of the given VertexArray with this matrix, storing the transformed values in a float array.
    public void    transform(VertexArray in, float[] out, boolean W) {
        int c = in.numComponents;
        int len = in.numVertices;

        float fill = W ? 1 : 0;

        int s = in.componentSize;
        int op = 0;
        int ip = 0;

        for(int i = 0; i < len; i++){
            for(int j = 0; j < c; j++){
                out[op++] = s == 2 ? in.data2[ip] : in.data1[ip];
                ip++;
            }
            // fill components c..2 with 0
            for(int j = c; j < 3; j++){
                out[op++] = 0;
            }
            // fill W component (3) with fill value
            out[op++] = fill;
        }
        
        transform(out, len*4);
    }
    
    //Transposes this matrix.
    public void    transpose() {
       
        for(int i= 1; i < 4; i++){
            for(int j = 0; j < i; j++){
                float f = matrix[i*4+j];
                matrix[i*4+j] = matrix[j*4+i];
                matrix[j*4+i] = f;
            }
        }

    }

    
    

    public boolean equals(Object o){
        if(!(o instanceof Transform)) return false;
        
        float[] m = ((Transform) o).matrix;
        
        for(int i = 0; i < 16; i++){
//          a relative epsilon would be better of course...
            if(Math.abs(m[i] - matrix[i]) > 0.001) return false; 
        }
        
        
        return true;
    }

    
    public String toString(){
        StringBuffer buf = new StringBuffer("( ");
        
        for(int i = 0; i < 16; i++){
            buf.append(matrix[i]);
            if(i == 15){
                buf.append(" )");
            }
            else if(i % 4 == 3){
                buf.append(" | ");
            }
            else{
                buf.append(' ');
            }
                
        }
        
        return buf.toString();
    }

    
}