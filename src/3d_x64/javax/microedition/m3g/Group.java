/*
 * Created on Oct 23, 2005 by Stefan Haustein
 */
package javax.microedition.m3g;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class Group extends Node {

	Vector children = new Vector();
	
	public Group(){
		//Constructs a new Group node and initializes it with an empty list of children.
	}
 

	public void 	addChild(Node child){
//    Adds the given node to this Group, potentially changing the order and indices of the previously added children.
		children.addElement(child);
		child.parent = this;
	}
	
	public Node 	getChild(int index){
 //   Gets a child by index.
		return(Node) children.elementAt(index);
	}

	public int 	getChildCount(){
//  	  Gets the number of children in this Group.
		return children.size();
	}

	
	public boolean 	pick(int scope, float x, float y, Camera camera, RayIntersection ri){

		// The pick ray is cast towards infinity from the given point p on the near clipping plane, 
		// through a point p' on the far clipping plane. The exact procedure of deriving the pick ray origin 
		// and direction from the given point (x, y) and the given projection matrix P is as follows.
		
		// In normalized device coordinates (NDC), the viewport spans the range [-1, 1] in each dimension (X, Y and Z). 
		// Points that lie on the near plane have a Z coordinate of -1 in NDC; points on the far plane have a Z of 1. 
		// The normalized device coordinates of p and p' are, therefore:
		// pndc = (2x-1, 1-2y, -1, 1)T
		// p'ndc = (2x-1, 1-2y, 1, 1)T 
		
		float[] p = {2*x-1, 1-2*y, -1, 1};
		float[] p_ = {2*x-1, 1-2*y, 1, 1};
		
		// Note that the Y coordinate is inverted when going from NDC to viewport or vice versa, 
		// as the viewport upper left corner maps to (-1, 1) in NDC 
		// (see also the viewport transformation equation in Graphics3D.setViewport). 
		// Applying the inverse projection matrix on the pick points, we obtain their positions in camera space:
		// pc = P^-1 pndc
		// p'c = P^-1 p'ndc 
		
		Transform p1 = new Transform();
		camera.getProjection(p1);
		
		p1.invert();
		
		p1.transform(p);
		p1.transform(p_);

		// We then scale the resultant homogeneous points such that their W components are equal to 1; that might not otherwise be the case after the inverse projection. Formally, denoting the W components of the near and far points by w and w', the final camera space coordinates are obtained as follows:
		//p = pc / w
		// p' = p'c / w' 
		
		for(int i = 0; i < 3; i++){
			p[i] /= p[3];
			p_[i] /= p_[3];
		}
		p[3] = 1;
		p_[3] = 1;
		
		//The origin of the pick ray in camera coordinates is then p while its direction vector is p' - p.
		//Finally, the pick ray is transformed from camera space to the coordinate system of this Group. 
		// That ray is used in the actual intersection tests, and is also the one that is returned by 
		// the getRay method in RayIntersection.
		
		Transform ct = new Transform();
		camera.getTransformTo(this, ct);
		
		ct.transform(p);
		ct.transform(p_);
		
		p_ = VMath.sub(p_, p);
		
		return pick(new Transform(), scope, p, p_, camera, ri, -1) != -1;

	}

	public boolean 	pick(int scope, float ox, float oy, float oz, float dx, float dy, float dz, RayIntersection ri){
		Transform t = new Transform();
		getCompositeTransform(t);
		return pick(t, scope, new float[]{ox, oy, oz}, new float[]{dx, dy, dz}, null, ri, -1) != -1;
	}




	public void 	removeChild(Node child){
		if(children.remove(child)){
			child.parent = null;
		}
	}	
	
	
	void load(M3gInputStream is) throws IOException {
		_load(is);
		Object[] ch = is.readObjectArray();
		for(int i = 0; i < ch.length; i++){
			children.addElement(ch[i]);
		}
	}
    
    
    Object3D _duplicate(Object3D dup){
        Group target = (Group) dup;

        for(int i = 0; i < getChildCount(); i++){
            target.addChild((Node) getChild(i).duplicate());
        }
        
        return target;
    }
    


	public Object3D duplicate() {
		Group target = new Group();
		return _duplicate(target);
	}


	public int getReferences(Object3D[] refs) {
		int count = _getReferences(refs);
		for(int i = 0; i < children.size(); i++){
			count = addRef(refs, count, getChild(i));
		}
		return count;
	}

	
	Hashtable getProperties(){
	    Hashtable props = super.getProperties();
        for(int i = 0; i < children.size(); i++){
            props.put("child "+i, children.elementAt(i));
        }
        return props;
    }


	float pick(Transform transform, int scope, float[] r1, float[] r2, Camera cam, RayIntersection ri, float bestDistance) {
		
		Transform ct = new Transform();
		for(int i = 0; i < getChildCount(); i++){
			Node child = getChild(i);
			Transform t = new Transform(transform);
			child.getCompositeTransform(ct);
			t.postMultiply(ct);
			float di = child.pick(transform, scope, r1, r2, cam, ri, bestDistance);
			if(di != -1){
				bestDistance = di;
			}
		}
		return bestDistance;
	}
	
}
