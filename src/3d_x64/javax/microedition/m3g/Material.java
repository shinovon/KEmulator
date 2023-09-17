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
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Material extends Object3D {
    public static final int  AMBIENT = 1024;
    public static final int DIFFUSE = 2048;
    public static final int EMISSIVE =   4096;
    public static final int SPECULAR  =  8192;
    
    float shininess;
    boolean vertexColorTracking;
    int[] colors = new int[4];
    
    public Material()
    {
//    	vertex color tracking : false (disabled) 
//    	ambient color : 0x00333333 (0.2, 0.2, 0.2, 0.0) 
//    	diffuse color : 0xFFCCCCCC (0.8, 0.8, 0.8, 1.0) 
//    	emissive color : 0x00000000 (0.0, 0.0, 0.0, 0.0) 
//    	specular color : 0x00000000 (0.0, 0.0, 0.0, 0.0) 
//    	shininess : 0.0 

    	vertexColorTracking = false;
        setColor(Material.AMBIENT,  3355443);
        setColor(Material.DIFFUSE, -3355444);
        setColor(Material.EMISSIVE, 0);
        setColor(Material.SPECULAR, 0);
        shininess = 0;
    }
    
    //Gets the value of the specified color component of this Material.
    public int getColor(int target) {
        int idx = target / AMBIENT;
        return colors[idx == 8 ? 2 : idx-1];
    }

//    Gets the current shininess of this Material.
    public float   getShininess() {
        return shininess;
    }
    
//    Queries whether vertex color tracking is enabled.
    boolean isVertexColorTrackingEnabled() {
        return vertexColorTracking;
    }
    
    //Sets the given value to the specified color component(s) of this Material.
    public void    setColor(int target, int argb){
        int idx = target / AMBIENT;
        colors[idx == 8 ? 2 : idx-1] = argb;        
    }
    
    //Sets the shininess of this Material.
    public void    setShininess(float shininess) {
      this.shininess = shininess;  
    }
    
    //Enables or disables vertex color tracking.
    public void    setVertexColorTrackingEnable(boolean enable) {
        this.vertexColorTracking = enable;
    }

    

	void load(M3gInputStream is) throws IOException {
		//ColorRGB      ambientColor;
		//ColorRGBA     diffuseColor;
		//ColorRGB      emissiveColor;
		//ColorRGB      specularColor;
		//Float32       shininess;
		//Boolean       vertexColorTrackingEnabled;
		_load(is);
		for(int i = 0; i < 4; i++){
			colors [i] = i == 1 ? is.readColorRGBA() : is.readColorRGB();
		}
		shininess = is.readFloat32();
		vertexColorTracking = is.readBoolean();
	}


	public int getReferences(Object3D[] refs) {
		return _getReferences(refs);
	}


	public Object3D duplicate() {
		Material target = new Material();
		System.arraycopy(colors, 0, target.colors, 0, 4);
		target.shininess = shininess;
		target.vertexColorTracking = vertexColorTracking;
		return _duplicate(target);
	}
    
	void setProperty(int id, float [] value){
	    switch(id){
	    case AnimationTrack.ALPHA: 
	        setColor(DIFFUSE, setAlpha(value, getColor(DIFFUSE)));
	        break;
	    case AnimationTrack.AMBIENT_COLOR:
	        setColor(AMBIENT, setColor(value, getColor(AMBIENT)));
	        break;
        case AnimationTrack.DIFFUSE_COLOR:
            setColor(DIFFUSE, setColor(value, getColor(DIFFUSE)));
            break;
        case AnimationTrack.EMISSIVE_COLOR:
            setColor(EMISSIVE, setColor(value, getColor(EMISSIVE)));
            break;
        case AnimationTrack.SPECULAR_COLOR:
            setColor(SPECULAR, setColor(value, getColor(SPECULAR)));
            break;
	    default:
	        super.setProperty(id, value);
	    }
	}
}
