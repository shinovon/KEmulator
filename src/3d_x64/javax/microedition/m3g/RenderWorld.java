/*
 * Created on 22/12/2005
 *
 * Copyright (c) 2005, Funda��o Des. Paulo Feitoza. All rights reserved.
 * 
 */
package javax.microedition.m3g;

import java.util.Vector;

import javax.media.opengl.GL;

/**
 * [Description for RenderNode]
 *
 */
class RenderWorld {

    static Transform getTransformCamera(World world)
    { 
        Vector vet = new Vector();
        if ( !RenderUtils.getTransformCamera(world, world.getActiveCamera(), vet))
        {
            throw new IllegalStateException ("World has no active camera, or the active camera is not in that world"); //$NON-NLS-1$
        }
        
        Transform transform = new Transform();
        for (int i = 0 ; i < vet.size(); i++ )
        {
            Transform transformTemp = (Transform) vet.elementAt(i);
            transform.postMultiply(transformTemp);
        }     
//        Transform transformCamera = RenderUtils.getTransform(new Transform(), world.getActiveCamera());
//        transformCamera.invert();
//        transform.postMultiply(transformCamera);
        
//        Transform transformCamera = new Transform();
//        world.getActiveCamera().getTransform(transformCamera);
//        transformCamera.invert();
//        transform.postMultiply(transformCamera);
//        transform = transformCamera
        return transform;    
       
    }    
 
    static void addLights(GL gl, Object3D obj, Transform transform)
    {
        Transform transformAdd = RenderUtils.getTransform(transform, obj);
        if (obj instanceof Light )
        {     
            
            Graphics3D.getInstance().addLight((Light) obj,transform);
//            g.addLight((Light)obj, transformAdd);
//            gl.glLightf()
//            gl.glLightf(transform.matrix[3], transform.matrix[7], transform.matrix[11]);
  
        }
         
        int numReferences = obj.getReferences(null);
        if (numReferences > 0)
        {
           Object3D[] objArray = new Object3D[numReferences];
           obj.getReferences(objArray);
           for (int i = 0; i < numReferences; i++)
           {
               Object3D object3D = objArray[i];
               addLights(gl, object3D, transformAdd);  
           }
        }
    }
    
    
    
    static void settingCamera(Object3D obj, Transform transform)
    {
         
        Transform transformAdd = RenderUtils.getTransform(transform, obj);

        if (obj instanceof Camera )
        {     
            Camera camera = (Camera) obj; 
            camera.setTransform(transform);
        }
         
        int numReferences = obj.getReferences(null);
        if (numReferences > 0)
        {
           Object3D[] objArray = new Object3D[numReferences];
           obj.getReferences(objArray);
           for (int i = 0; i < numReferences; i++)
           {
               Object3D object3D = objArray[i];
               settingCamera( object3D, transformAdd);  
           }
        }
    }
    
}
