package javax.microedition.m3g;

import java.io.IOException;

public abstract class Node extends Transformable {
    Node baseSkeleton = null;
    
	public static final int 	NONE =	144;
	public static final int 	ORIGIN= 	145;
	public static final int 	X_AXIS =	146;
	public static final int 	Y_AXIS =	147;
	public static final int 	Z_AXIS =	148;
	
	boolean enableRendering = true;
	boolean enablePicking;
	float alphaFactor;
	int scope = -1;
	int zTarget;
	int yTarget;
	Node zReference;
	Node yReference;
	Node parent;
	
	
	static float[] getTargetVector(int axis){
		return new float[]{
				axis == X_AXIS ? 1 : 0, 
				axis == Y_AXIS ? 1 : 0,
			    axis == Z_AXIS ? 1 : 0,
			    0};
	}
	
	
	void _load(M3gInputStream is) throws IOException{
		//Boolean       enableRendering;
		//Boolean       enablePicking;
		//Byte          alphaFactor;
		//UInt32        scope;
		//Boolean       hasAlignment;
		//IF hasAlignment==TRUE, THEN
		//  Byte          zTarget;
		//  Byte          yTarget;
	    //  ObjectIndex   zReference;
	    //  ObjectIndex   yReference;
		//END
		
		super._load(is);
		enableRendering = is.readBoolean();
		enablePicking = is.readBoolean();
		alphaFactor = is.read() / 255.0f;
		scope = is.readInt32();
		if(is.readBoolean()){
			zTarget = is.read();
			yTarget = is.read();
			zReference = (Node) is.readObject();
			yReference = (Node) is.readObject();
		}
	}
	
	
	public void 	align(Node reference){
		
		rotation = new Transform();
		Transform Rz = new Transform();

		if(zTarget != NONE){
			// Formally, let us denote by tZ and tY the Z and Y alignment target vectors, 

			float[] tZ = getTargetVector(zTarget);

			// transformed from their respective reference nodes to A; note that axis 
			// targets transform as vectors, and origin targets as points. 
		

			if(reference == null && zReference == null){
				transform.transform(tZ);
			}
			else {
				Transform t = new Transform();
				(reference == null ? zReference : reference).getTransformTo(this, t);
				t.transform(tZ);
			}
			
			// The axis for the first rotation Rz is then the cross product of the 
			// local Z axis of A and the target vector:
			//
			// aZ = (0 0 1)T × tZ 

			float[] aZ = VMath.crossProduct(new float[]{0, 0, 1}, tZ);
		
			// and the rotation angle can be computed via the dot product of the two. 
		
			float a = VMath.dot(new float[]{0, 0, 1, 0}, tZ);
		
			Rz.postRotate(a, aZ[0], aZ[1], aZ[2]);
		}
		
		if(yTarget != NONE){
			float[] tY = getTargetVector(yTarget);

			if(reference == null && yReference == null){
				transform.transform(tY);
			}
			else {
				Transform t =  new Transform();
				(reference == null ? yReference : reference).getTransformTo(this, t);
				t.transform(tY);
			}

			// Rotating by Rz takes us to a new coordinate frame B where tY is expressed as:
			//
			//		tY' = RZ^-1 × tY 
		
			Transform Rz_1 = new Transform(Rz);
			Rz_1.invert();
		
			Rz_1.transform(tY);
		
			//	The axis for the second rotation RY is the local Z axis of B, 
		
			// float[] aY = new float[]{0, 0, 1};
		
			// and the angle is the angle between the local Y axis and the projection of tY' on the XY plane. 
			// The final alignment rotation A is then:
			// A = RZ RY 
		
			float a= (float) Math.toDegrees(Math.atan2(tY[0], tY[1]));
		
			Rz.postRotate(a, 0, 0, 1);
		}
		
		rotation = Rz;
	 }
	 
	
	public  float 	getAlphaFactor(){
    // Retrieves the alpha factor of this Node.
		return alphaFactor;
	}
		
     public Node 	getParent(){
    	return parent;
     }
     
     public int 	getScope(){
    	 //Retrieves the scope of this Node.
    	 return scope;
     }
     
     public boolean getTransformTo(Node target, Transform transform){
     //Gets the composite transformation from this node to the given node.
    	 return getTransformTo(target, null, transform);
     }
     
     
     boolean getTransformTo(Node target, Node exclude, Transform transform){
         //Gets the composite transformation from this node to the given node.
    	 if(target == this){
    		 transform.set(Transform.IDENTITY);
    		 return true;
    	 }
    	 
    	 if(parent != null && exclude != parent){
    		 if(parent.getTransformTo(target, this, transform)){
    			 Transform ct = new Transform();
              //  the composite transformation from this node to its parent
              // is equal to the node transformation of this node
    			 getCompositeTransform(ct);
    			 transform.postMultiply(ct);  // descending from the topmost parent
    			 return true;
    		 }
    	 }

    	 int rc = getReferences(null);
    	 Object3D[] refs = new Object3D[rc];
    	 getReferences(refs);
    	 for(int i = 0; i < rc; i++){
    		 if(refs[i] == exclude || !(refs[i] instanceof Node)){
    			 continue;
    		 }
    		 Node n = (Node) refs[i];
    		 
    		 if(n.getTransformTo(target, this, transform)){
    			 Transform ct = new Transform();
              // the composite transformation from this node to its child is equal 
              // to the inverse of the node transformation of the child.
    			 n.getCompositeTransform(ct);
    			 ct.invert();
    			 transform.postMultiply(ct); //  
    			 //ct.postMultiply(transform);   //  
    			 //transform.set(ct);            //
    			 return true;
    		 }
    	 }
    	 return false;
     }


     
     
     
     public boolean 	isPickingEnabled(){
//     Retrieves the picking enable flag of this Node.
    	 return enablePicking;
     }
     
     public boolean 	isRenderingEnabled(){
    	 //Retrieves the rendering enable flag of this Node.
    	 return enableRendering;
     }
     
     public void 	setAlignment(Node zRef, int zTarget, Node yRef, int yTarget){
    	 //Sets this node to align with the given other node(s), or disables alignment.
         this.zReference = zRef;
    	     this.zTarget = zTarget;
    	     this.yReference = yRef;
    	     this.yTarget = yTarget;
     }
     
     public void 	setAlphaFactor(float alphaFactor){
     //Sets the alpha factor for this Node.
    	 this.alphaFactor =alphaFactor;
     }
     
     public void 	setPickingEnable(boolean enable){
     //Sets the picking enable flag of this Node.
    	 this.enablePicking = enable;
     }
     
     public void 	setRenderingEnable(boolean enable){
//     Sets the rendering enable flag of this Node.
    	 this.enableRendering = enable;
     }
     
     public void 	setScope(int scope){
//     Sets the scope of this node.
    	 this.scope = scope;
     }
     
     void setProperty(int id, float[] value){
         switch(id){
         case AnimationTrack.ALPHA:
             alphaFactor = Math.max(0, Math.min(1, value[0]));
             break;
         case AnimationTrack.PICKABILITY:
             enablePicking = value[0] >= 0.5f;
             break;
         case AnimationTrack.VISIBILITY:
             enableRendering = value[0] >= 0.5f;
             break;
         default:
             super.setProperty(id, value);
         }
     }
     
     Object3D _duplicate(Object3D t){
    	 Node target = (Node) t;
    	 
    	target.enableRendering = enableRendering;
    	target.enablePicking = enablePicking;
    	target.alphaFactor=alphaFactor;
    	target.scope = scope;
    	target.zTarget = zTarget;
    	target.yTarget = yTarget;
    	target.zReference = zReference;
    	target.yReference = yReference;
    	return super._duplicate(target);
     }
     
     
     float pick(Transform transform, int scope, float[] r1, float[] r2, Camera cam, RayIntersection ri, float bestDistance) {
    	 return -1;
     }
 	


         
}
