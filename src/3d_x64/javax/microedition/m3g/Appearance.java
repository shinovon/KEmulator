/*
 * Created on 15.06.2005 by Stefan Haustein
 */
package javax.microedition.m3g;

import java.io.IOException;
import java.util.Vector;

/**
 * @author stefan.haustein
 *
 */
public class Appearance extends Object3D {
    private CompositingMode compositingMode;
    private Fog fog;
    private int layer;
    private Material material;
    private PolygonMode polygonMode;
    private Vector textures = new Vector();
    
//  Returns the current CompositingMode for this Appearance.
    public CompositingMode getCompositingMode() {
        return compositingMode;
    }
    
    //Returns the current fogging attributes for this Appearance.
    public Fog getFog() {
        return fog;
    }
//    Gets the current rendering layer for this Appearance.

    public int getLayer() {
        return layer;
    }

    //    Returns the current Material for this Appearance.
    public Material    getMaterial() {
       return material;
    }
    
    //Returns the current PolygonMode for this Appearance.
    public PolygonMode getPolygonMode() {
        return polygonMode;
    }

    //    Retrieves the current texture image and its attributes for the given texturing unit.
    public Texture2D   getTexture(int index) {
        return index >= textures.size() ? null : (Texture2D) textures.elementAt(index);
    }
    
    //    Sets the CompositingMode to use for this Appearance.
    public void    setCompositingMode(CompositingMode compositingMode) {
        this.compositingMode = compositingMode;
    }
    
    //Sets the fogging attributes to use for this Appearance.
    public void setFog(Fog fog) {
        this.fog = fog;
    }
    
    //        Sets the rendering layer for this Appearance.
    public void    setLayer(int layer) {
        this.layer = layer;
    }

    //        Sets the Material to use when lighting this Appearance.
    public void    setMaterial(Material material) {
        this.material = material;
    }
    
    //        Sets the PolygonMode to use for this Appearance.
    public void    setPolygonMode(PolygonMode polygonMode) {
        this.polygonMode = polygonMode;
    }

//    Sets the texture image and its attributes for the given texturing unit.


	public void    setTexture(int index, Texture2D texture) {
        while(textures.size()<=index){
            textures.addElement(null);
        }
        textures.setElementAt(texture, index);
    }


	void load(M3gInputStream is) throws IOException {
		//Byte          layer;
		//ObjectIndex   compositingMode;
		//ObjectIndex   fog;
		//ObjectIndex   polygonMode;
		//ObjectIndex   material;
		//ObjectIndex[] textures;

		_load(is);
		layer = is.read();
		compositingMode = (CompositingMode) is.readObject();
		fog = (Fog) is.readObject();
		polygonMode = (PolygonMode) is.readObject();
		material = (Material) is.readObject();
		
		Object[] t = is.readObjectArray();
		for(int i = 0; i < t.length; i++){
			setTexture(i, (Texture2D) t[i]);
		}

	}

	public int getReferences(Object3D[] refs) {
		int count = _getReferences(refs);
		count = addRef(refs, count, compositingMode);
		count = addRef(refs, count, fog);
		count = addRef(refs, count, polygonMode);
		count = addRef(refs, count, material);
		for(int i = 0; i < textures.size(); i++){
			count = addRef(refs, count,(Texture2D) getTexture(i));
		}
		return count;
	}


	public Object3D duplicate() {
		Appearance target = new Appearance();
		target.layer = layer;
		target.compositingMode = compositingMode;
		target.fog = fog;
		target.polygonMode = polygonMode;
		target.material = material;
		for(int i = 0; i < textures.size(); i++){
			target.setTexture(i, getTexture(i));
		}
		return _duplicate(target);
    }
}
