/*
 * Created on Oct 22, 2005 by Stefan Haustein
 */
package javax.microedition.m3g;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

class M3gInputStream extends InputStream {
	
	Loader loader;
	InputStream is;
	
	M3gInputStream(Loader loader, InputStream is){
		this.loader = loader;
		this.is = is;
	}
	
	public int read() throws IOException{
		return is.read();
	}
	
	public int readUInt16() throws IOException {
		int i = read();
		return i | (read() << 8);
	}

	public int readInt16() throws IOException {
		int i = readUInt16();
		return ((i & 32768) != 0) ? 0x0ffff0000 | i : i;
	}
	
	public int readInt32() throws IOException {
		int i = readUInt16();
		return i | (readUInt16() << 16);
	}

	public void readFully(byte[] data) throws IOException {
		int pos = 0;
		while(pos < data.length){
			int count = is.read(data, pos, data.length - pos);
			if(count <= 0){
				throw new IOException("Unexpected EOF");
			}
			pos += count;
		}
	}

	public String readString() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while(true){
			int i = is.read();
			if(i <= 0) break;
			baos.write(i);
		}
		return new String(baos.toByteArray());
	}

	public boolean readBoolean() throws IOException {
		int i = read();
		switch(i){
		case 0: return false;
		case 1: return true;
		default: throw new RuntimeException("Illegal boolean value: "+i);
		}
	}

	public Object[] readObjectArray() throws IOException {
		int count = readInt32();
//        System.out.println("obj arr count: "+count);
		Object[] data = new Object[count];
		for(int i = 0; i < count; i++){
			data[i] = readObject();
		}
		return data;
	}

	public byte[] readByteArray() throws IOException {
		int count = readInt32();
		byte[] data = new byte[count];
		readFully(data);
		return data;
	}

	public float readFloat32() throws IOException {
		return Float.intBitsToFloat(readInt32());
	}

	public Object readObject() throws IOException {
        int id = readInt32();
        if(id == 0) return null;
        Integer ido = new Integer(id);
        if(loader.roots.get(ido) != null){
            loader.roots.remove(ido);
        }
		return loader.objects.elementAt(id-1);
        
	}

	public int readColorRGBA() throws IOException {
		int r = is.read();
		int g = is.read();
		int b = is.read();
		int a = is.read();
		
		return (a << 24) | (r << 16) | (g << 8) | b;

	}

	public float[] readVector() throws IOException {
		float[] v = new float[3];
		for(int i = 0; i < 3; i++){
			v[i] = readFloat32();
		}
		return v;
	}
	
	
	public float[] readMatrix() throws IOException {
		float[] m = new float[16];
		for(int i = 0; i < 16; i++){
			m[i] = readFloat32();
		}
		return m;
	}

	public int readColorRGB() throws IOException {
		int r = is.read();
		int g = is.read();
		int b = is.read();
		
		return  (r << 16) | (g << 8) | b;
	}

    public int[] readIntArray() throws IOException {
        int count = readInt32();
        int[] data = new int[count];
        for(int i = 0; i < data.length; i++){
            data[i] = readInt32();
        }
        return data;
    }
	

	

}
