package javax.microedition.m3g;


import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.lcdui.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Image2D extends Object3D {

    public static final int ALPHA = 96;
    public static final int LUMINANCE = 97;
    public static final int LUMINANCE_ALPHA = 98;
    public static final int RGB = 99;
    public static final int RGBA = 100;
    
    int format;
    boolean mutable;
    int width;
    int height;
 
    /** LUMINANCE or LUMINANCE_ALPHA format */
    byte[] luminance;

    /** ALPHA or LUMINANCE_ALPHA format */
    byte[] alpha;
    
    /** RGB or RGBA format */
    int[] rgba;
    

    int data[]; 
    
    
    void buildDataJOGL()
    {
        switch(format){
            case ALPHA:                                
                data = new int[getWidth() * getHeight()];
                System.arraycopy(alpha, 0, data, 0, data.length);                
                break;        
                
            case LUMINANCE:
             //   luminance = new byte[width*height];
                data = new int[getWidth() * getHeight()];
                System.arraycopy(luminance, 0, data, 0, data.length);
                break;
                
            case LUMINANCE_ALPHA:
              //  luminance = new byte[width*height];
              //  alpha = new byte[width*height];
                
                data = new int[getWidth() * getHeight()];
                System.arraycopy(alpha, 0, data, 0, data.length); //TODO
//                System.arraycopy(luminance, 0, data, 0, data.length);
                break;
            case RGBA:
            case RGB:
                data = new int[getWidth() * getHeight() * 4];
                int count = 0;
                for (int i = 0; i < rgba.length; i++ )
                {
                    int red   = (rgba[i] >>  0) & 255;
                    int green = (rgba[i] >>  8) & 255;
                    int blue  = (rgba[i] >> 16) & 255;
                    int alfa  = (rgba[i] >> 24) & 255;
                     
                    data[i] = (blue | (green<<8) | (red << 16) | (alfa<<24));
                     
//                    data[i] = rgba[i];
//                    data[count++] = (rgba[i] >> 16) & 255;
//                    data[count++] = (rgba[i] >>  8) & 255;
//                    data[count++] = (rgba[i] >>  0) & 255;
//                    data[count++] = (rgba[i] >> 24) & 255; 
                } 
                break;
            default:
                throw new IllegalArgumentException("Illegal image format: "+format);
            }
    }

    
    int[] getDataJOGL()
    {
        return data; 
    }
    
    
    /** Converts the indexed format to */
    private static byte[] getData(int format, byte[] indices, byte[] palette){
        if(palette.length == 0) return indices;
        int size;
        switch(format){
        case ALPHA: 
        case LUMINANCE: size = 1; break;
        case LUMINANCE_ALPHA: size = 2; break;
        case RGB: size = 3; break;
        case RGBA: size = 4; break;
        default:
            throw new IllegalArgumentException();
        }
        
        byte[] data = new byte[indices.length * size];
        for(int i = 0; i < indices.length; i++){
            System.arraycopy(palette, (((int)indices[i])&255)*size, data, i*size, size);
        }
        return data;
    }

    
    public Image2D(int format, int width, int height) {
        init(format, width, height);
        mutable = true;
        buildDataJOGL();
    }
    
    
    
    public Image2D(int format, int width, int height, byte[] data) {
        init(format, width, height);
        set(0, 0, width, height, data);
        buildDataJOGL();
    }

    
    
    public Image2D(int format, int width, int height, byte[] indices, byte[] palette) {
        this(format, width, height, getData(format, indices, palette));
        buildDataJOGL();
    }
    
    
    public Image2D(int type, Object imageObj) {
    	
    	
        Image img = (Image) imageObj;

//    	System.out.println("Image2D from imageObj");
//    	ImageIcon icon = new ImageIcon(img._image);
//    	
//    	JFrame frame = new JFrame("test");
//    	frame.add(new JLabel(icon));
//    	frame.pack();
//    	frame.show();
//    	
        
        init(type, img.getWidth(), img.getHeight());
        switch(format){
        case RGB:
        case RGBA:
            img.getRGB(rgba, 0, width, 0, 0, width, height);
            break;
        default:
            throw new RuntimeException("lumination/alpha cannot be constructed from MIDP image");
        }
        buildDataJOGL();
    }

    /** Used by loader */
    Image2D() {
    }


    void init(int format, int width, int height){
        this.format = format;
        switch(format){
        case ALPHA:
            alpha = new byte[width*height];
            break;        
        case LUMINANCE:
            luminance = new byte[width*height];
            break;
        case LUMINANCE_ALPHA:
            luminance = new byte[width*height];
            alpha = new byte[width*height];
            break;
        case RGBA:
        case RGB:
            rgba = new int[width*height];
            break;
        default:
            throw new IllegalArgumentException("Illegal image format: "+format);
        }
        this.width = width;
        this.height = height;
    }
    
    
    public int getFormat() {
        return format;
    }
    
    public int getHeight() {
        return height;
    }



    public int getWidth() {
        return width;
    }

    boolean isMutable() {
       return mutable;
    }
    

    public void set(int x0, int y0, int w, int h, byte[] bData) {
        int spos = 0;
        
        for(int y = y0; y < y0+h; y++){
            int dpos = y * width + x0;
            
            switch(format){
            case ALPHA:
                System.arraycopy(bData, spos, alpha, dpos, w);
                break;
            
            case LUMINANCE:
                System.arraycopy(bData, spos, luminance, dpos, w);
                break;
            
            case LUMINANCE_ALPHA:
                for(int x = 0; x < w; x++){
                    luminance[dpos] = bData[spos++];
                    alpha[dpos] = bData[spos++];
                    dpos++;
                }
                break;
           
            case RGB:
            case RGBA:
                for(int x = 0; x < w; x++){
                    int red   = ((int) bData[spos++]) & 255;
                    int green = ((int) bData[spos++]) & 255;
                    int blue  = ((int) bData[spos++]) & 255;
                    
                    int c = 
                        (red   << 16) |
                        (green << 8) |
                        (blue);

                    c |= format == RGB ? 0x0ff000000 
                            : (((int) bData[spos++]) << 24);
                    
                    rgba[dpos++] = c;
                }
                break;
                
            default:
                throw new IllegalArgumentException();
            }
        }
        buildDataJOGL();
    }
    
//    @Override
    void load(M3gInputStream is) throws IOException {
    //Byte          format;
    //Boolean       isMutable;
    //UInt32        width;
    //UInt32        height;
    //IF isMutable==false, THEN
    //    Byte[]        palette;
    //    Byte[]        pixels;
    //END
        _load(is);
        format = is.read();
        mutable = is.readBoolean();
        width = is.readInt32();
        height = is.readInt32();
        init(format, width, height);

        if(!mutable){
            byte[] palette = is.readByteArray();
            byte[] data = is.readByteArray();
            if(palette.length != 0){
                data = getData(format, data, palette);
            }
            set(0, 0, width, height, data);
        }
    }

//    @Override
    public int getReferences(Object3D[] refs) {
        return _getReferences(refs);
    }

//    @Override
    public Object3D duplicate() {
        Image2D target = new Image2D(format, width, height);
        target.mutable = mutable;
        
        if(rgba != null){
            System.arraycopy(rgba, 0, target.rgba, 0, rgba.length);
        }

        if(alpha != null){
            System.arraycopy(alpha, 0, target.alpha, 0, alpha.length);
        }

        if(luminance != null){
            System.arraycopy(luminance, 0, target.luminance, 0, luminance.length);
        }
        
        return _duplicate(target);
    }


    public void getPixels(byte[] array) {
        // TODO
    }

    public int size() {
//        if(rgba != null) {
//            return rgba.length * 4;
//        }
        return width * height * 4;
    }
}
