/*
 * Created on 15.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package javax.microedition.m3g;

import java.io.IOException;
import java.util.Vector;




/**
 * @author stefan.haustein
 *
 * @API JSR184
 */
public class VertexBuffer extends Object3D{
    
    float[] positionBias = new float[3];
    int defaultColor;
    float positionScale = 1;
    VertexArray colors;
    VertexArray normals;
    VertexArray positions;
    Vector textureArrays = new Vector();
    
    //  Gets the current color array, or null if per-vertex colors are not set.
    public VertexArray getColors() {
        return colors;
    }
    
    public int getDefaultColor() {
        return defaultColor;
    }
    
    public VertexArray getNormals() {
        return normals;
    }
    
    // Returns the current vertex position array, or null if positions are not set.
    public VertexArray getPositions(float[] scaleBias) {
        if(scaleBias != null){
            scaleBias[0] = positionScale;
            System.arraycopy(positionBias, 0, scaleBias, 1, 3);
        }
        return positions;
    }
    
    //Gets the current texture coordinate array for the specified texturing unit, or null if texture coordinates for that unit are not set.
    public VertexArray getTexCoords(int index, float[] scaleBias) {
        return ((TextureCoordinate) textureArrays.elementAt(index)).get(scaleBias);
    }
        
    public int getVertexCount() {
        
        if(positions != null) return positions.numVertices;
        if(colors != null) return colors.numVertices;
        if(normals != null) return normals.numVertices;

        return 0;
    }
    
    public void setColors(VertexArray colors) {
        this.colors = colors;
    }
    
    //Sets the color to use in absence of per-vertex colors.
    public void setDefaultColor(int ARGB) {
        this.defaultColor = ARGB;
    }
    
    //Sets the normal vectors for this VertexBuffer.
    public void setNormals(VertexArray normals) {
        this.normals = normals;
    }

    //Sets the vertex positions for this VertexBuffer.
    
    // TODO scale and bias are not taken into account....!
    public void setPositions(VertexArray positions, float scale, float[] bias) {
        this.positions = positions;
        this.positionScale = scale;
        if(bias == null){
            this.positionBias = new float[3];
        }
        else{
            System.arraycopy(bias, 0, this.positionBias, 0, 3);
        }
        this.positionScale = positionScale;
    }
    
    public void setTexCoords(int index, VertexArray texCoords, float scale, float[] bias) {
        while(textureArrays.size() <= index){
            textureArrays.addElement(null);
        }
        textureArrays.setElementAt(new TextureCoordinate(texCoords, scale, bias), index);
    }


	void load(M3gInputStream is) throws IOException {
//        ColorRGBA     defaultColor;
//        ObjectIndex   positions;
//        Float32[3]    positionBias;
//        Float32       positionScale;
//        ObjectIndex   normals;
//        ObjectIndex   colors;
//        UInt32        texcoordArrayCount;
//        FOR each texture coordinate array...
//               ObjectIndex   texCoords;
//               Float32[3]    texCoordBias;
//               Float32       texCoordScale;
//        END
        
        _load(is);
	    defaultColor = is.readColorRGBA();
        positions = (VertexArray) is.readObject();
        for(int i = 0; i < 3; i++){
            positionBias[i] = is.readFloat32();
        }
        positionScale = is.readFloat32();
        normals = (VertexArray) is.readObject();
        colors = (VertexArray) is.readObject();
        int texcoordArrayCount = is.readInt32();
        float[] texCoordBiasBuf = new float[3];
        for(int i = 0; i < texcoordArrayCount; i++){
            VertexArray texCoords = (VertexArray) is.readObject();
            for(int j = 0; j < 3; j++){
                texCoordBiasBuf[j] = is.readFloat32();
            }
            float texCoordScale = is.readFloat32();
            textureArrays.addElement(
                    new TextureCoordinate(texCoords, texCoordScale, texCoordBiasBuf));
        }
        
	}


	public Object3D duplicate() {
        VertexBuffer dup = new VertexBuffer();
        dup.defaultColor = defaultColor;
        dup.colors = colors;
        dup.positions = positions;
        dup.normals = normals;
        System.arraycopy(positionBias, 0, dup.positionBias,0, 3);
        dup.positionScale = positionScale;
        for(int i = 0; i < textureArrays.size(); i++){
            TextureCoordinate tc = (TextureCoordinate) textureArrays.elementAt(i);
            dup.textureArrays.addElement(tc == null ? null : new TextureCoordinate(
                    tc.vertices, tc.scale, tc.bias));
        }
        return _duplicate(dup);
	}


	public int getReferences(Object3D[] refs) {
	    int count = _getReferences(refs);
        count = addRef(refs, count, positions);
        count = addRef(refs, count, normals);
        count = addRef(refs, count, colors);
        for(int i = 0; i < textureArrays.size(); i++){
            count = addRef(refs, count, ((TextureCoordinate) textureArrays.elementAt(i)).vertices);
        }
        return count;
	}
    
    void setProperty(int id, float [] value){
        switch(id){
        case AnimationTrack.ALPHA: 
            defaultColor = setAlpha(value, defaultColor);
            break;
        case AnimationTrack.COLOR:
            defaultColor = setColor(value, defaultColor);
            break;
        default:
            super.setProperty(id, value);
        }
    }
    
}
