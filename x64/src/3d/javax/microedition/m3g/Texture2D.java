package javax.microedition.m3g;

import java.io.IOException;

public class Texture2D extends Transformable {

    public static final int FILTER_BASE_LEVEL =  208;
    public static final int FILTER_LINEAR =  209;
    public static final int FILTER_NEAREST = 210;
    public static final int FUNC_ADD  =  224;
    public static final int FUNC_BLEND = 225;
    public static final int FUNC_DECAL=  226;
    public static final int FUNC_MODULATE =  227;
    public static final int FUNC_REPLACE  =  228;
    public static final int WRAP_CLAMP = 240;
    public static final int WRAP_REPEAT = 241;
        
    Image2D image;
    int blendColor = 0;
    int blending = FUNC_MODULATE;
    int levelFilter = FILTER_BASE_LEVEL;
    int imageFilter = FILTER_NEAREST;
    int wrappingS = WRAP_REPEAT;
    int wrappingT = WRAP_REPEAT;
	boolean specialCaseBackgroundT = false;
	boolean specialCaseBackgroundS = false;
    
    public Texture2D(Image2D image2D) {
        this.image = image2D;
    }

    
    /** Used by loader */
    Texture2D() {
    }


    public int getBlendColor() {
    //    Returns the current texture blend color for this Texture2D.
        return blendColor;
    }
    
    public int getBlending() {
    //    Returns the current texture blend mode for this Texture2D.
        return blending;
    }
    
    //   Retrieves the current base level (full size) texture image.
    public Image2D getImage() {
        return image;
    }
    
    
//  Returns the current texture wrapping mode for the S texture coordinate.
    public int getWrappingS() {
        return wrappingS;
    }

//  Returns the current texture wrapping mode for the T texture coordinate.
    public int getWrappingT() {
        return wrappingT;
    }

//  Sets the texture blend color for this Texture2D.
    public void    setBlendColor(int RGB) {
        this.blendColor = RGB;
    }
        
    public  void setBlending(int func) {
       this.blending = func;
//    Selects the texture blend mode, or blend function, for this Texture2D.
    }
    
     public void  setFiltering(int levelFilter, int imageFilter) {
         this.levelFilter = levelFilter;
         this.imageFilter = imageFilter;
//    Selects the filtering mode for this Texture2D.
     }
     
     //    Sets the given Image2D as the texture image of this Texture2D.
     public void setImage(Image2D image) {
         this.image = image;
     }

     public void  setWrapping(int wrapS, int wrapT) {
         wrappingS = wrapS;
         wrappingT = wrapT;
     }


	void load(M3gInputStream is) throws IOException {
        
        //ObjectIndex   image;
        //ColorRGB      blendColor;
        //Byte          blending;
        //Byte          wrappingS;
        //Byte          wrappingT;
        //Byte          levelFilter;
        //Byte          imageFilter;

        _load(is);
        image = (Image2D) is.readObject();
        blendColor = is.readColorRGB();
        blending = is.read();
        wrappingS =is.read();
        wrappingT = is.read();
        levelFilter = is.read();
        imageFilter = is.read();
	}

	public int getReferences(Object3D[] refs) {
		int count = _getReferences(refs);
        count = addRef(refs, count, image);
        return count;
	}



	public Object3D duplicate() {
        Texture2D t = new Texture2D(image);
        t.blendColor = blendColor;
        t.blending = blending;
        t.wrappingS = wrappingS;
        t.wrappingT = wrappingT;
        t.levelFilter = levelFilter;
        t.imageFilter = imageFilter;
        return _duplicate(t);
	}
    
    void setProperty(int id, float [] value){
        switch(id){
        case AnimationTrack.COLOR:
            blendColor = setColor(value, blendColor);
            break;
        default:
            super.setProperty(id, value);
        }
    }
   
    
}