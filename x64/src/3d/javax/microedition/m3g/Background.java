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
 */
public class Background extends Object3D {
    public static final int BORDER = 32;
    public static final int REPEAT = 33;

    int color;
    int cropHeight;
    int cropWidth;
    int cropX;
    int cropY;
    private int imageModeX;
    private int imageModeY;
    private Image2D image2D;
    private boolean colorClear = true;
    private boolean depthClear = true;
    
    //Retrieves the current background color.
    public int getColor(){
        return color;
    }
    
    //Gets the current cropping rectangle height within the source image.
    public int getCropHeight(){
        return cropHeight;
    }
    
    //Gets the current cropping rectangle width within the source image.
    public int getCropWidth() {
        return cropWidth;
    }
    
    //Retrieves the current cropping rectangle X offset relative to the source image top left corner.
    public int getCropX() {
        return cropX;
    }

    
    
    //Retrieves the current cropping rectangle Y offset relative to the source image top left corner.
    public int getCropY() {
        return cropY;
    }
    
    //Gets the current background image.
    public Image2D getImage() {
        return image2D;
    }
    
    //Gets the current background image repeat mode for the X dimension.
    public int getImageModeX() {
        return imageModeX;
    }
    
    //Gets the current background image repeat mode for the Y dimension.
    public int getImageModeY() {
        return imageModeY;
    }
    
    //Queries whether color buffer clearing is enabled.
    public boolean isColorClearEnabled() {
        return colorClear;
    }
    
    //Queries whether depth buffer clearing is enabled.
    public boolean isDepthClearEnabled() {
        return depthClear;
    }
    
    //Sets the background color.
    public void    setColor(int ARGB) {
        color = ARGB;
    }
    
    //Enables or disables color buffer clearing.
    public void    setColorClearEnable(boolean enable) {
        this.colorClear = enable;
    }
    
    //   Sets a cropping rectangle within the background image.
    public void    setCrop(int cropX, int cropY, int width, int height) {
        this.cropX = cropX;
        this.cropY = cropY;
        this.cropWidth = width;
        this.cropHeight = height;
    }
 
    //Enables or disables depth buffer clearing.
    public void    setDepthClearEnable(boolean enable) {
        depthClear = enable;
    }

    //Sets the background image, or switches from background image mode to background color mode.
    public void    setImage(Image2D image) {
        this.image2D = image;
        this.cropX = 0;
        this.cropY = 0;
        this.cropWidth = image.width;
        this.cropHeight =  image.height;
    }

  
    //Sets the background image repeat mode for the X and Y directions.
    public void    setImageMode(int modeX, int modeY) {
        this.imageModeX =modeX;
        this.imageModeY = modeY;
    }


	void load(M3gInputStream is) throws IOException {
		//		ColorRGBA     backgroundColor;
		//	ObjectIndex   backgroundImage;
		//Byte          backgroundImageModeX;
		//Byte          backgroundImageModeY;
		//Int32         cropX;
		//Int32         cropY;
		//Int32         cropWidth;
		//Int32         cropHeight;
		//Boolean       depthClearEnabled;
		//Boolean       colorClearEnabled;
		super._load(is);
        
		color = is.readColorRGBA();
		image2D = (Image2D) is.readObject();
		imageModeX = is.read();
		imageModeY = is.read();
		cropX = is.readInt32();
		cropY = is.readInt32();
		cropWidth = is.readInt32();
		cropHeight = is.readInt32();
		depthClear = is.readBoolean();
		colorClear = is.readBoolean();
		
	}


	public int getReferences(Object3D[] refs) {
		return _getReferences(refs);
	}

    void setProperty(int id, float [] value){
        switch(id){
        case AnimationTrack.ALPHA: 
            color = setAlpha(value, color);
            break;
        case AnimationTrack.COLOR:
            color = setColor(value, color);
            break;
        case AnimationTrack.CROP:
            cropX = Math.round(value[0]);
            cropY = Math.round(value[1]);
            if(value.length >= 4){
                cropWidth = Math.max(0, Math.round(value[2]));
                cropHeight = Math.max(0, Math.round(value[3]));
            }
        default:
            super.setProperty(id, value);
        }
    }
    
    

	public Object3D duplicate() {
		Background target = new Background();
		
		target.color = color;
		target.image2D = image2D;
		target.imageModeX = imageModeX;
		target.imageModeY = imageModeY;
		target.cropX = cropX;
		target.cropY = cropY;
		target.cropWidth = cropWidth;
		target.cropHeight = cropHeight;
		target.depthClear = depthClear;
		target.colorClear = colorClear;
		
		return _duplicate(target);
	}
}
