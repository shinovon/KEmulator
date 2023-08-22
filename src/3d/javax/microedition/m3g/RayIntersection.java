/*
 * Created on Oct 23, 2005 by Stefan Haustein
 */
package javax.microedition.m3g;


public class RayIntersection {

    // This is just a storage class for call-by-reference 
    // value return from pick
    // members are set directly in Node.pick();
    
	
	float[] textureS = new float[10];
	float[] textureT = new float[10];
	
    Node intersected;
    float distance;
    float normalX;
    float normalY;
    float normalZ = 1;
    float[] ray = new float[6]; 
    float dz = 1;
    int submeshIndex = 0;

	public RayIntersection()
	{

		ray[0] = 0.f;
		ray[1] = 0.f;
		ray[2] = 0.f;
		ray[3] = 0.f;
		ray[4] = 0.f;
		ray[5] = 1.f;
	}
    
	public float 	getDistance(){
	    return distance;
	}
	
	public Node 	getIntersected(){
	    return intersected;
	}
	
	public float 	getNormalX(){
	    return normalX;
	}
	
	public float getNormalY(){
        return normalY;
	}
	
	public float 	getNormalZ(){
	    return normalZ;
	}

	public void 	getRay(float[] ray){
//    Retrieves the origin (ox oy oz) and direction (dx dy dz) of the pick ray, in that order.
		System.arraycopy(this.ray, 0, ray, 0, 6);

	}

    public int 	getSubmeshIndex(){
        return submeshIndex;
    }

	public float getTextureS(int index)
	{
		if (index < 0 || index >= textureS.length)
		{
			//return 0;
			throw new IndexOutOfBoundsException();
		}

		return textureS[index];
	}

	public float getTextureT(int index)
	{
		if (index < 0 || index >= textureT.length)
		{
		//	return 0;
			throw new IndexOutOfBoundsException();
		}

		return textureT[index];
	}

}
