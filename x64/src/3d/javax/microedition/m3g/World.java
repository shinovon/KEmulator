package javax.microedition.m3g;

import java.io.IOException;

public class World extends Group {

    Camera activeCamera;
    Background background;
    
    public World() {
//    Creates an empty World with default values.
    }
    
    public Camera  getActiveCamera() {
//    Gets the currently active camera.
        return activeCamera;
    }
    
public Background  getBackground() {
 //   Retrieves the background settings of this World.
    return background;
}

public void    setActiveCamera(Camera camera) {
//    Sets the Camera to use when rendering this World.
    this.activeCamera = camera;
}

public void    setBackground(Background background) {
//    Sets the Background object for this World.
    this.background = background;
}  
    void load(M3gInputStream is) throws IOException{
       super.load(is);
       //ObjectIndex   activeCamera;
       //ObjectIndex   background;
       activeCamera = (Camera) is.readObject();
       background = (Background) is.readObject();
    }
    
    
    public int getReferences(Object3D[] refs){
        int count = super.getReferences(refs);
        count = addRef(refs, count, activeCamera);
        count = addRef(refs, count, background);
        return count;
    }
    
    public Object3D duplicate(){
        World dup = new World();
        dup.activeCamera = activeCamera;
        dup.background = background;
        return _duplicate(dup);
    }
}
