/*
 * Created on Oct 22, 2005 by Stefan Haustein
 */
package javax.microedition.m3g;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.microedition.lcdui.Image;

public class Loader {

	static private byte[] FILE_IDENTIFIER = { 
		(byte)0xAB, 0x4A, 0x53, 0x52, 0x31, 0x38, 0x34, (byte)0xBB, 0x0D, 0x0A, 0x1A, 0x0A };

	private int verMajor;
	private int verMinor;
	boolean hasExternalReferences;
	private int totalFileSize;
	private int approximateContentSize;
	private String authoringField;
	Vector objects = new Vector();
    Hashtable roots = new Hashtable();
    String uri;
	
	
	public static Object3D[] load(byte[] data, int offset){
//    Deserializes Object3D instances from the given byte array, starting at the given offset.
		try{
			return new Loader(null).load(new ByteArrayInputStream(data, offset, data.length-offset));
		} catch (IOException e) {
			throw new RuntimeException("Error loading m3g file from byte array: "+e.toString());
		}
	}
	
	
	public static Object3D[] load(String name){
//    Deserializes Object3D instances from the named resource.
		try {
			InputStream inputStream = Loader.class.getResourceAsStream(name);
			if (inputStream == null) {
				inputStream = openInputStream(name);
			}
			return new Loader(name).load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException("Error loading m3g file from "+name+": "+e.toString());
		}
	}

	static InputStream openInputStream(final String s) throws IOException {
		return Helpers.openInputStream(s);
	}

	private Loader(String uri){
		this.uri = uri;
	}
	
	
	
	private Object3D[] load(InputStream ris) throws IOException {
		PushbackInputStream pis = new PushbackInputStream(ris);
		
		int c = pis.read();
		pis.unread(c);
		
		if(c == 137){
			Image image = Image.createImage(pis);

			Image2D i2d = new Image2D(Image2D.RGBA, image);
			
			return new Object3D[]{ i2d};
		}
		
		M3gInputStream is = new M3gInputStream(this,pis);
		for(int i = 0; i < FILE_IDENTIFIER.length; i++){
			if((byte) is.read() != FILE_IDENTIFIER[i]){
				throw new RuntimeException("File Identifier mismatch at position "+i);
			}
		}
		
		// read sections
		while(true){
			
			//Byte   CompressionScheme       1
			//UInt32 TotalSectionLength      4
			//UInt32 UncompressedLength      4
			//Byte[] Objects
			//UInt32 Checksum                4
			
			int compressionScheme = is.read();
			if(compressionScheme == -1) break;
			
			int totalSectionLength = is.readInt32();
			int uncompressedLength = is.readInt32();
			
			byte[] data = new byte[totalSectionLength - 13];
			
			is.readFully(data);
			
			byte[] uncompressed;
			
			switch(compressionScheme){
			case 0:
				uncompressed = data;
				break;
			case 1:
                try{
                    Inflater decompresser = new Inflater();
                    decompresser.setInput(data, 0, data.length);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                
                    byte[] buf = new byte[8096];
                    while(!decompresser.finished()){
                        baos.write(buf, 0, decompresser.inflate(buf));
                    }                
                    uncompressed = baos.toByteArray(); 
                 } catch (DataFormatException e) {
                    throw new IOException(e.toString());
                 }
                break;
                    default:
				throw new RuntimeException("Unsupported compression scheme: "+compressionScheme);
			}
			
			readSection(new M3gInputStream(this, new ByteArrayInputStream(uncompressed)));
			
			
			int checksum = is.readInt32();
		}
        
        
        Object3D[] result = new Object3D[roots.size()];
        Enumeration e = roots.elements();
        for(int i = 0; i < result.length; i++){
            result[i] = (Object3D) e.nextElement();
        }
		return result;
	}
	
	Object3D createObject(int type){
	switch(type){
	case 1: return new AnimationController();
	case 2: return new AnimationTrack();
	case 3: return new Appearance();
	case 4: return new Background();
	case 5: return new Camera();
    case 6: return new CompositingMode();
    case 7: return new Fog();
    case 8: return new PolygonMode();
    case 9: return new Group();
    case 10: return new Image2D();
    case 11: return new TriangleStripArray();
    case 12: return new Light();
    case 13: return new Material();
    case 14: return new Mesh();
    case 15: return new MorphingMesh();
    case 16: return new SkinnedMesh();
    case 17: return new Texture2D();
    case 18: return new Sprite3D();
    case 19: return new KeyframeSequence();
    case 20: return new VertexArray();
    case 21: return new VertexBuffer();
    case 22: return new World();
    
	default: throw new RuntimeException("Unrecognized object type:"+type);
	}
	}
	
	
	private void readSection(M3gInputStream is) throws IOException{
        
		Object3D object;
		while(true){
			// Byte   ObjectType
			// UInt32 Length
		    // Byte[] Data
			
			int objectType = is.read();
			if(objectType == -1) break;
			int length = is.readInt32();
			
			switch(objectType){
			case 0: 
				//Byte[2] VersionNumber
				//Boolean hasExternalReferences
				//UInt32  TotalFileSize
				//UInt32  ApproximateContentSize
				//String  AuthoringField
				
				verMajor = is.read();
				verMinor = is.read();
				hasExternalReferences = is.readBoolean();
				totalFileSize = is.readInt32();
				approximateContentSize = is.readInt32();
				authoringField = is.readString();
				
				objects.addElement("Header");
				
				break;
			case 255:
				String uri = is.readString();
				uri = concatPath(this.uri, uri);
				
				
				object = new Loader(uri).load(openInputStream(uri))[0];

                objects.addElement(object);
                object.id = objects.size();
                roots.put(new Integer(object.id), object);
				
				break;
			default:
//                System.out.println("Reading object type: "+objectType);
				object = createObject(objectType);
                 objects.addElement(object);
                 object.id = objects.size();
				object.load(is);
                 roots.put(new Integer(object.id), object);
//				System.out.println("loaded: "+object+": "+object.getProperties());   
			}


		}
	}
	private static String concatPath(String base, String file) {
		if (base == null || file.startsWith("/") || file.startsWith("\\")
				|| (file.length() >= 2 && file.charAt(1) == ':') || isUrl(file))
			return file;

		int cut = Math.max(base.lastIndexOf('/'), base.lastIndexOf('\\'));

		return base.substring(0, cut + 1) + file;
	}


	private static boolean isUrl(String path) {
		String test = path.toLowerCase();
		return test.startsWith("http://") || test.startsWith("https://") || test.startsWith("file:");
	}

}
