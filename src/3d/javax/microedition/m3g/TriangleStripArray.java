package javax.microedition.m3g;

import java.io.IOException;

public class TriangleStripArray extends IndexBuffer {


    /** implicit if null */
    int[] indices;
    int[] stripLenghts;
    //int startIndex;
    
    public TriangleStripArray(int startIndex, int[] stripLen) {
        this.stripLenghts = new int[stripLen.length];
        System.arraycopy(stripLen, 0, this.stripLenghts, 0, stripLen.length);
    		
        generateIndices(startIndex);
    }
    
    void generateIndices(int startIndex){
        
        int total = 0;
        for(int i = 0; i < stripLenghts.length; i++){
        		total += stripLenghts[i];
        }
        
        indices = new int[total];
        for(int i = 0; i < total; i++){
        		indices[i] = startIndex + i;
        }
        
    }

    public TriangleStripArray(int[] i, int[] stripLen) {
        indices = new int[i.length];
        System.arraycopy(i, 0, indices, 0, i.length);
        this.stripLenghts = new int[stripLen.length];
        System.arraycopy(stripLen, 0, this.stripLenghts, 0, stripLen.length);
    }

	    /** Used by loader */
    TriangleStripArray() {
    }


	void load(M3gInputStream is) throws IOException {
//        Byte       encoding;
//        IF encoding == 0, THEN
//            UInt32        startIndex;
//        ELSE IF encoding == 1, THEN
//            Byte          startIndex;
//        ELSE IF encoding == 2, THEN
//            UInt16        startIndex;
//        ELSE IF encoding == 128, THEN
//            UInt32[]      indices; 
//        ELSE IF encoding == 129, THEN
//            Byte[]        indices;
//        ELSE IF encoding == 130, THEN
//            UInt16[]      indices;
//        END
//        UInt32[]      stripLengths;
        
        _load(is);
        int encoding = is.read();
        int startIndex = -1;
        switch(encoding){
        case 0:
            startIndex = is.readInt32();
            break;
        case 1: 
            startIndex = is.read();
            break;
        case 2:
            startIndex = is.readUInt16();
            break;
        case 128:
            indices = is.readIntArray();
            break;
        case 129:
        case 130:
            int count = is.readInt32();
            indices = new int[count];
            for(int i = 0; i < count; i++){
                indices[i] = encoding == 129 ? is.read() : is.readUInt16();
            }
        }
        
        stripLenghts = is.readIntArray();
        if(indices == null){
        		generateIndices(startIndex);
        }
	}


	public Object3D duplicate() {
	    TriangleStripArray dup =  new TriangleStripArray(indices, stripLenghts);
                
        return _duplicate(dup);
	}


	public int getReferences(Object3D[] refs) {
        return _getReferences(refs);
	}
}
