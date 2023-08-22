package javax.microedition.m3g;

import java.io.IOException;

/*
 * Created on 30.06.2005 by Stefan Haustein
 */

/**
 * @author Stefan Haustein
 */
public class VertexArray extends Object3D {

    int numComponents;
    int numVertices;
	int componentSize;
	byte[] data1;
	short[] data2;
	
	public VertexArray(int numVertices, int numComponents, int componentSize) {
        this.numVertices = numVertices;
        this.numComponents = numComponents;
		this.componentSize = componentSize;
		switch(componentSize){
		case 1: data1 = new byte[numVertices*numComponents];break;
		case 2: data2 = new short[numVertices*numComponents]; break;
		default: throw new IllegalArgumentException();
		}
	}

	VertexArray() {
    }

    public void set(int firstVertex, int numVertices, short[] vert) {
		System.arraycopy(vert, 0, data2, firstVertex, numVertices * numComponents );
	}

	public void set(int firstVertex, int numVertices, byte[] vert) {
		System.arraycopy(vert, 0, data1, firstVertex, numVertices * numComponents);
	}

//    @Override
    void load(M3gInputStream is) throws IOException {
        //Byte          componentSize;
        //Byte          componentCount;
        //Byte          encoding;
        //UInt16        vertexCount;
        //FOR each vertex...
//            IF componentSize==1, THEN
//                IF encoding==0, THEN
//                      Byte[componentCount] components;
//                ELSE IF encoding==1, THEN
//                      Byte[componentCount] componentDeltas;
//                END
//            ELSE
//                IF encoding==0, THEN
//                      Int16[componentCount] components;
//                ELSE IF encoding==1, THEN
//                      Int16[componentCount] componentDeltas;
//                END
//            END
//        END        
        
        _load(is);
        componentSize = is.read();
        numComponents = is.read();
        int encoding = is.read();
        numVertices = is.readUInt16();
        
        if(componentSize == 1){
            data1 = new byte[numVertices*numComponents];
        }
        else{
            data2 = new short[numVertices*numComponents];
        }
        
        int pos = 0;
        for(int i = 0; i < numVertices; i++){
            for(int j = 0; j < numComponents; j++){
                int v = componentSize == 1 ? is.read(): is.readInt16();
                if(i == 0 || encoding == 0){
                    if(componentSize == 1){
                        data1[pos] = (byte) v;
                    }
                    else{
                        data2[pos] = (short) v;
                    }
                }
                else{
                    if(componentSize == 1){
                        data1[pos] = (byte) (data1[pos-componentSize] + v);
                    }
                    else{
                        data2[pos] = (byte) (data2[pos-componentSize] + v);
                    }
                }
                pos++;
            }
        }
    }

//    @Override
    public Object3D duplicate() {
        VertexArray dup = new VertexArray(numVertices, numComponents, componentSize);
        if(componentSize == 1){
            System.arraycopy(data1, 0, dup.data1, 0, data1.length);
        }
        else{
            System.arraycopy(data2, 0, dup.data2, 0, data2.length);
        }

        return _duplicate(dup);
    }

//    @Override
    public int getReferences(Object3D[] refs) {
        return _getReferences(refs);
    }
	
}
