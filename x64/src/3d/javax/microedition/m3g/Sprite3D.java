/*
 * Created on 14/12/2005
 *
 * Copyright (c) 2005, Funda��o Des. Paulo Feitoza. All rights reserved.
 * 
 */
package javax.microedition.m3g;

import java.io.IOException;

 
public class Sprite3D extends Node {
 
    boolean scaled;
    Image2D image;
    Appearance appearance;
    int cropX;
    int cropY;
    int cropW;
    int cropH;
    
    
    //   Constructs a new Sprite3D with the given scaling mode, image and appearance. 
    public Sprite3D(boolean scaled, Image2D image, Appearance appearance) {
        this.scaled = scaled;
        this.image = image;
        this.appearance = appearance;
        cropW = image.getWidth();
        cropH = image.getHeight();
    }   
    
    public Sprite3D() {
    }

    //  Gets the current Appearance of this Sprite3D. 
    public Appearance getAppearance() {
        return appearance;
    } 
    
    //  Gets the current cropping rectangle height within the source image.  
    public int getCropHeight() {
        return cropH;
    }
    
    //Gets the current cropping rectangle width within the source image.  
    public int getCropWidth()     {
        return cropW;
    }
    
    //Retrieves the current cropping rectangle X offset relative to the source image top left corner. 
    public int getCropX() {
        // TODO Auto-generated method stub
        return cropX;
    }
    
    //    Retrieves the current cropping rectangle Y offset relative to the source image top left corner. 
    public int getCropY() 
    {
        // TODO Auto-generated method stub
        return cropY;
    }
    
    //    Gets the current Sprite3D image. 
    public Image2D getImage() {
        // TODO Auto-generated method stub
        return image;
    } 
    
    //    Returns the automatic scaling status of this Sprite3D.  
    public boolean isScaled() {
        return scaled;
    }
    
    // Sets the Appearance of this Sprite3D.  
    public void setAppearance(Appearance appearance)     {
        this.appearance = appearance;
    } 
    
    //    Sets a cropping rectangle within the source image.  
    public void setCrop(int cropX, int cropY, int width, int height)     {
        this.cropX = cropX;
        this.cropY = cropY;
        this.cropW = width;
        this.cropH = height;
    } 
    
    //    Sets the sprite image to display.  
    public void setImage(Image2D image) {
        this.image = image;
    }

    
    /**
     * @see javax.microedition.m3g.Object3D#load(javax.microedition.m3g.M3gInputStream)
     */
    void load(M3gInputStream is) throws IOException {
        _load(is);
        image = (Image2D) is.readObject();
        appearance = (Appearance) is.readObject();
        scaled = is.readBoolean();
        cropX = is.readInt32();
        cropY = is.readInt32();
        cropW = is.readInt32();
        cropH = is.readInt32();
    }

    /**
     * @see javax.microedition.m3g.Object3D#duplicate()
     */
    public Object3D duplicate() {
        Sprite3D copy = new Sprite3D(scaled, image, appearance);
        copy.setCrop(cropX, cropY, cropW, cropH);
        return _duplicate(copy);
    }

    /**
     * @see javax.microedition.m3g.Object3D#getReferences(javax.microedition.m3g.Object3D[])
     */
    public int getReferences(Object3D[] refs) {
        int count = _getReferences(refs);
        count = addRef(refs, count, image);
        count = addRef(refs, count, appearance);
        return count;
    }

    
    void setProperty(int id, float [] value){
        switch(id){
        case AnimationTrack.CROP:
            cropX = Math.round(value[0]);
            cropY = Math.round(value[1]);
            if(value.length >= 4){
                cropW = Math.round(value[2]);
                cropH = Math.round(value[3]);
            }
        default:
            super.setProperty(id, value);
        }
    }

		
    
}


