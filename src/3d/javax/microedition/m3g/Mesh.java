/*
 * Created on Oct 23, 2005 by Stefan Haustein
 */
package javax.microedition.m3g;

import java.io.IOException;

public class Mesh extends Node {

	VertexBuffer vertices;
	IndexBuffer[] submeshes;
	Appearance[] appearances;
	
	
	public Mesh(VertexBuffer vertices, IndexBuffer[] submeshes, Appearance[] appearances){

		if(vertices == null) throw new NullPointerException();

		for(int i = 0; i < submeshes.length; i++){
			if(submeshes[i] == null) throw new NullPointerException();
		}

		if(submeshes.length == 0 || (appearances != null && appearances.length < submeshes.length)) {
			throw new IllegalArgumentException();
		}
		
		this.vertices = vertices;
		this.submeshes = new IndexBuffer[submeshes.length];
		this.appearances = new Appearance[submeshes.length];
		
		System.arraycopy(submeshes, 0, this.submeshes, 0, submeshes.length);
		if(appearances != null){
			System.arraycopy(appearances, 0, this.appearances, 0, appearances.length);
		}
		
	}
	
	public	Mesh(VertexBuffer vertices, IndexBuffer submesh, Appearance appearance){
		
		if(vertices == null || submesh == null) throw new NullPointerException();

		
		this.vertices = vertices;
		this.submeshes = new IndexBuffer[]{submesh};
		this.appearances = new Appearance[]{appearance};
	}
	
    /** Used by loader */
	Mesh() {
    }

    public Appearance 	getAppearance(int index){
		//Gets the current Appearance of the specified submesh.
		return appearances[index];
	}
	
	public IndexBuffer 	getIndexBuffer(int index){
    //Retrieves the submesh at the given index.
		return submeshes[index];
	}
	
	public int 	getSubmeshCount(){
		//Gets the number of submeshes in this Mesh.
		return submeshes.length;
	}
	
	public VertexBuffer 	getVertexBuffer(){
//    	Gets the vertex buffer of this Mesh.
		return vertices;
	}
	
	public void 	setAppearance(int index, Appearance appearance){
    	//Sets the Appearance for the specified submesh.
		appearances[index] = appearance;
	}
	

	void load(M3gInputStream is) throws IOException {
		//ObjectIndex   vertexBuffer;
		//UInt32        submeshCount;
        //FOR each submesh...
		//    ObjectIndex   indexBuffer;
		//    ObjectIndex   appearance;
		//END		
		_load(is);
		vertices = (VertexBuffer) is.readObject();
		int count = is.readInt32();
		submeshes = new IndexBuffer[count];
		appearances = new Appearance[count];
		for(int i = 0; i < count; i++){
			submeshes[i] = (IndexBuffer) is.readObject();
			appearances[i] = (Appearance) is.readObject();
		}
	}


	public Object3D duplicate() {
		Mesh target = new Mesh(vertices, submeshes, appearances);
		return _duplicate(target);
	}


	public int getReferences(Object3D[] refs) {
		int count = _getReferences(refs);
		count = addRef(refs, count, vertices);
		for(int i = 0; i < submeshes.length; i++){
			count = addRef(refs, count, submeshes[i]);
			count = addRef(refs, count, appearances[i]);
		}
		return count;
	}

	
	float pick(Transform transform, int scope, float[] r1, float[] r2, Camera cam, RayIntersection ri, float bestDistance) {
	
		
	     Transform t = new Transform(transform);
	     	  
	     float[] scaleBias = new float[4];
	     VertexArray points = vertices.getPositions(scaleBias);
	     
	     float[] p = new float[points.numVertices * 4];
	     
	     t.postTranslate(scaleBias[1], scaleBias[2], scaleBias[3]);
	     t.postScale(scaleBias[0], scaleBias[0], scaleBias[0]);
	     t.transform(points, p, true);
	  
		for(int i = 0; i < getSubmeshCount(); i++){
			TriangleStripArray tsa = (TriangleStripArray) submeshes[i];
			
			float[] p1 = new float[3];
			float[] p2 = new float[3]; 
			System.arraycopy(p, 0, p1, 0, 3);
			System.arraycopy(p, 4, p2, 0, 3);
			float[] p3 = new float[3];

			for(int j = 2; j < tsa.indices.length; j++){
				
				System.arraycopy(p, 4*j, p3, 0, 3);
				
				   // Returns in (fX, fY) the location on the plane (P1,P2,P3) of the intersection with the ray (R1, R2)
				   // First compute the axes
//				   V1 = P2 - P1;
//				   V2 = P3 - P1;
//				   V3 = CrossProduct ( V1, V2); 

				float[] v1 = VMath.sub(p2, p1);
				float[] v2 = VMath.sub(p3, p1);
				float[] v3 = VMath.crossProduct(v1, v2);
				
				
				   // Project ray points R1 and R2 onto the axes of the plane. (This is equivalent to a rotation.)
//				   vRotRay1 = CVector3 ( Dot (V1, R1-P1 ), Dot (V2, R1-P1 ), Dot (V3, R1-P1 ) );
//				   vRotRay2 = CVector3 ( Dot (V1, R2-P1 ), Dot (V2, R2-P1 ), Dot (V3, R2-P1 ) ); 

				
				float[] r1_p1 = VMath.sub(r1, p1);
				float[] r2_p1 = VMath.sub(r2, p1);
				
				float[] vRotRay1 = new float []{
						VMath.dot(v1, r1_p1),
						VMath.dot(v2, r1_p1),
						VMath.dot(v3, r1_p1)};
						
				float[] vRotRay2 = new float []{
						VMath.dot(v1, r2_p1),
						VMath.dot(v2, r2_p1),
						VMath.dot(v3, r2_p1)};

				   // Return now if ray will never intersect plane (they're parallel)
//				   if (vRotRay1.z == vRotRay2.z) return FALSE;

				if(vRotRay1[2] != vRotRay2[2]){
				
				   // Find 2D plane coordinates (fX, fY) that the ray interesects with
//				   float fPercent = vRotRay1.z / (vRotRay2.z-vRotRay1.z);
//				   vIntersect2d = vRotRay1 + (vRotRay1-vRotRay2) * fPercent;
//				   fX = vIntersect2d.x;
//				   fY = vIntersect2d.y;

				   // Note that to find the 3D point on the world-space ray use this
				   // vInstersect = R1 + (R1-R2) * fPercent;

					float distance = vRotRay1[2] / (vRotRay2[2]-vRotRay1[2]);

					if(bestDistance == -1 || distance < bestDistance){
						if(ri != null) {
					
							ri.distance = vRotRay1[2] / (vRotRay2[2]-vRotRay1[2]);
							ri.intersected = this;
							ri.submeshIndex = i;
							System.arraycopy(r1, 0, ri.ray, 0, 3);
							System.arraycopy(r2, 0, ri.ray, 3, 3);
						}
					
						bestDistance = distance;
					}
				}
				
				v1=v2;
				v2=v3;
				
			}
			
		}
		
		return bestDistance;
	}
}
