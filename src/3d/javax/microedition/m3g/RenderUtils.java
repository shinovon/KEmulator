/*
 * Created on 22/12/2005
 *
 * Copyright (c) 2005, Funda��o Des. Paulo Feitoza. All rights reserved.
 * 
 */
package javax.microedition.m3g;

import java.util.HashMap;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * [Description for RenderUtils]
 *
 * @author Rodrigo Cal (rcal@fpf.br) 
 */
class RenderUtils {
 
    static Transform settingCamera(GL2 gl, Transform transform, Transform camGenericMatrix )
    {
        if (camGenericMatrix == null)
        {
            throw new RuntimeException("no camera"); //$NON-NLS-1$
        }
        Transform transformTemp = new Transform(camGenericMatrix);
        transformTemp.postMultiply(transform);
        transform = transformTemp;
 
        if ((transform != null)&&(transform.matrix != null))
        { 
            
//          transform.matrix[0]  *=  1; transform.matrix[1]  *=  1; transform.matrix[2]  *=  1; transform.matrix[3]  *=  1;  
//          transform.matrix[4]  *= -1; transform.matrix[5]  *= -1; transform.matrix[6]  *= -1; transform.matrix[7]  *= -1; 
//          transform.matrix[8]  *=  1; transform.matrix[9]  *=  1; transform.matrix[10] *=  1; transform.matrix[11] *=  1;  
//          transform.matrix[12] *=  1; transform.matrix[13] *=  1; transform.matrix[14] *=  1; transform.matrix[15] *=  1;
            
            transform.transpose();        
      
            gl.glMatrixMode(GL2.GL_PROJECTION);
//            gl.glMatrixMode(GL.GL_TEXTURE);            
//            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadMatrixf(transform.matrix, 0);
        }
        return transform;
    }
    
    static Transform getTransform(Transform transform , Object3D object3D) 
    {
        if (transform == null)
            transform = new Transform();
        
        Transform returnTransform = transform;
        if (object3D instanceof Transformable) 
        {
            Transformable transformable = (Transformable) object3D;
            
            Transform ct = new Transform();
            transformable.getCompositeTransform(ct);
            
            Transform nt = new Transform(transform);
            nt.postMultiply(ct);
            
            returnTransform = nt;
            /*
            Transform newTransform = new Transform(transform);
    
            
            float[] translation = new float[3]; 
            transformable.getTranslation(translation);
            newTransform.postTranslate(translation[0], translation[1], translation[2] );
            
            float[] scale = new float[3]; 
            transformable.getScale(scale);
            newTransform.postScale( scale[0], scale[1], scale[2] );

            float[] rotate = new float[4]; 
            transformable.getOrientation(rotate);
            newTransform.postRotate( rotate[0], rotate[1], rotate[2], rotate[3]);
    
     //      newTransform.postMultiply(transformable.rotation);
            
            Transform extractTransformableObj = new Transform(transform);
            transformable.getTransform(extractTransformableObj);    
            newTransform.postMultiply(extractTransformableObj);            
            
            returnTransform = newTransform;
            */
        }
        return returnTransform;
    }
         
    
//    <Object3D, Transform[]>hash
    static void getHashTransformSkinnedMesh(Object3D skeleton, Transform transformPre, Transform transformPos, HashMap hash)
    {
    	Transform[] group = new Transform[2];
    	group[0] = getTransform(transformPre, skeleton);
    	
    	if (skeleton instanceof Node)
		{
			Node node = (Node) skeleton;
//			if (node.baseSkeleton == null)
//				return;
			group[1] = getTransform(transformPos, node.baseSkeleton);
		}
    	
    	hash.put(skeleton, group);
    	
        int numReferences = skeleton.getReferences(null);
        if (numReferences > 0)
        {
           Object3D[] objArray = new Object3D[numReferences];
           skeleton.getReferences(objArray);
           
           for (int i = 0; i < numReferences; i++)
           {
               Object3D object3D = objArray[i];
               getHashTransformSkinnedMesh(object3D, group[0], group[1], hash);
           }
        }
    }
    
    static Transform getTransformSkinnedMesh(HashMap hash, Object3D joint)
    {
    	Transform group[] =  (Transform[]) hash.get(joint);
        Transform transform1 = new Transform(group[0]);
        Transform transform2 = new Transform(group[1]);
        
        transform2.invert();
        transform1.postMultiply(transform2);
  
        return transform1; 
         
    }
    
    static Transform getTransformSkinnedMesh(Object3D skeleton, Object3D joint, Transform transform, boolean type)
    {
    	Transform transformAdd ;
    	
    	if (type)
    	{
        	transformAdd = getTransform(transform, skeleton);
    	}
    	else
    	{
    		if (skeleton instanceof Node)
    		{
    			Node group = (Node) skeleton;

    			if (group.baseSkeleton == null)
    				return null;
    			
    			transformAdd = getTransform(transform, group.baseSkeleton);
    		}
    		else
    			return null;
    	}
    	
    	
       
       if (skeleton == joint)
       {        
           return transformAdd;
       }
       else
       {
//    		if (skeleton instanceof Node)
//    		{
//    			Node group = (Node) skeleton;
//	            float[] scale = new float[3]; 
//	            group.getScale(scale);
//	            transformAdd.postScale( scale[0], scale[1], scale[2] );
//    		}    	   
       }
       
       int numReferences = skeleton.getReferences(null);
       if (numReferences > 0)
       {
          Object3D[] objArray = new Object3D[numReferences];
          skeleton.getReferences(objArray);
          
          for (int i = 0; i < numReferences; i++)
          {
              Object3D object3D = objArray[i];
              Transform t = getTransformSkinnedMesh(object3D, joint, transformAdd, type);
              if (t != null)
              {
            	  return t;
              }
               
          }
       }
      
       return null; 
    }

    
    static Transform getTransformSkinnedMesh(SkinnedMesh skinnedMesh, Object3D joint)
    {
        Transform transform1 = RenderUtils.getTransformSkinnedMesh(skinnedMesh.getSkeleton(), joint, new Transform(), true );
        Transform transform2 = RenderUtils.getTransformSkinnedMesh(skinnedMesh.getSkeleton(), joint, new Transform(), false );
                
        transform2.invert();
        transform1.postMultiply(transform2);

        return transform1; 
    }
    
    static boolean getTransformCamera(Object3D ref, Object3D target, Vector transformResult )
    {
        
       Transform transformAdd = getTransform(new Transform(), ref);
   
       transformResult.addElement(transformAdd);
       
       
       boolean isTarget = false;
       
       if (ref == target)
       {          
           transformResult.removeElement(ref);
           isTarget = true;
       }

       int numReferences = ref.getReferences(null);
       if (numReferences > 0)
       {
          Object3D[] objArray = new Object3D[numReferences];
          ref.getReferences(objArray);
          
          for (int i = 0; i < numReferences; i++)
          {
              Object3D object3D = objArray[i];
              if (isTarget)
              {
                  if (object3D == target)
                  {     
                      continue;
                  }
              }
              if (getTransformCamera(object3D, target, transformResult ))
              {
                  isTarget = true;
              }
               
          }
       }
       if (!isTarget)
       {
           transformResult.removeElement(transformAdd);
       }
       return isTarget; 
    }
    
    

    
    static VertexArray getVertexArray(VertexArray base, int[] data )
    {
        VertexArray vertexArray = new VertexArray(base.numVertices, base.numComponents, base.componentSize);
        switch(base.componentSize)
        {
            case 1:
                
                vertexArray.data1 = new byte[base.data1.length];
                
                for (int j = 0; j < vertexArray.data1.length; j++)
                {
                    vertexArray.data1[j] = (byte)data[j];
                }
                
                break;

            case 2:                
                vertexArray.data2 = new short[base.data2.length];
                
                for (int j = 0; j < vertexArray.data2.length; j++)
                {
                    vertexArray.data2[j] = (short)data[j];
                }
                
                break;
        }
        return vertexArray;
    }
    
    static int[] getIntArray(VertexArray i)
    {
        int[] values = null;
        
        if (i != null)
        {
            switch(i.componentSize)
            {
                case 1:                
                    values = new int[i.data1.length];
                    for (int j = 0; j < i.data1.length; j++)
                    {
                        values[j] =  i.data1[j];
                    }
                    
                    break;
    
                case 2:                
                    values = new int[i.data2.length];
                    for (int j = 0; j < i.data2.length; j++)
                    {
                        values[j] = i.data2[j];
                    }
                    break;
    
            }
        }
        
        return values;
    }
  
    static double[] getDoubleArray(VertexArray i)
    {
        double[] values = null;
        
        if (i != null)
        {
            switch(i.componentSize)
            {
                case 1:                
                    values = new double[i.data1.length];
                    for (int j = 0; j < i.data1.length; j++)
                    {
                        values[j] =  i.data1[j];
                    }
                    
                    break;
    
                case 2:                
                    values = new double[i.data2.length];
                    for (int j = 0; j < i.data2.length; j++)
                    {
                        values[j] = i.data2[j];
                    }
                    break;
    
            }
        }
        
        return values;
    }
    
    static VertexBuffer getMorphingMesh(MorphingMesh morphingMesh)
    {
        VertexBuffer vb = morphingMesh.getVertexBuffer();        
        VertexBufferDouble vertexBufferDouble = new VertexBufferDouble(vb);        
        double[] positions = vertexBufferDouble.positionsExtras;         
        
        int[] pointsBase = getIntArray( morphingMesh.vertices.positions );
         
//        int positions[] = new int[pointsBase.length];
 
        int positionsArray[][] = new int[morphingMesh.targets.length][];
        for (int i = 0; i < morphingMesh.targets.length; i++)
        {
            positionsArray[i] = getIntArray( morphingMesh.targets[i].positions);
        }
        
        for (int index = 0; index < pointsBase.length; index++)
        {   // R = B + sum [ wi (Ti - B) ]
            
            int sum = 0;
            for (int i = 0; i < morphingMesh.targets.length; i++)
            {
                sum += morphingMesh.weights[i] * (positionsArray[i][index] - pointsBase[index] ) ;
            }
            
            positions[index] = pointsBase[index] + sum;
        }
        

//        VertexBuffer vertexBuffer = new VertexBuffer ();
//        vertexBuffer.setPositions( getVertexArray(morphingMesh.vertices.positions, positions), morphingMesh.vertices.positionScale, (float[])morphingMesh.vertices.positionBias.clone()); // TODO constants 
//        vertexBuffer.setNormals  ( morphingMesh.vertices.getNormals() );
//        vertexBuffer.setColors   ( morphingMesh.vertices.getColors()  );
        
        
        return vertexBufferDouble;
    };   
    
    static void resetTranslate(Transform transform)
    {
        transform.matrix[3] = 0;
        transform.matrix[7] = 0;
        transform.matrix[11] = 0;
    }
    
    static void invertScale(Transform transform)
    { 
        float sxn = getTranslateX(transform);
        float syn = getTranslateY(transform);
        float szn = getTranslateZ(transform);

        transform.postScale(
            1/sxn,
            1/syn,
            1/szn
            );        

        transform.postScale(
            1/sxn,
            1/syn,
            1/szn
            );
    }
    
    
    static void normalizeScale(Transform transform)
    {
        float sxn = getTranslateX(transform);
        float syn = getTranslateY(transform);
        float szn = getTranslateZ(transform);

        transform.postScale(
            1/sxn,
            1/syn,
            1/szn
            );
    }
    
    static float getTranslateX(Transform transform)
    {
        float sxn = (float)Math.sqrt( transform.matrix[0] * transform.matrix[0] + transform.matrix[4]*transform.matrix[4] + transform.matrix[8] *transform.matrix[8] );
        return sxn;
    }
    
    static float getTranslateY(Transform transform)
    {
        float syn = (float)Math.sqrt( transform.matrix[1] * transform.matrix[1] + transform.matrix[5]*transform.matrix[5] + transform.matrix[9] *transform.matrix[9] );
        return syn;
    }
    static float getTranslateZ(Transform transform)
    {
        float szn = (float)Math.sqrt( transform.matrix[2] * transform.matrix[2] + transform.matrix[6]*transform.matrix[6] + transform.matrix[10]*transform.matrix[10] );
        return szn;
    }
 
}
class TransformMode
{
    static final int REF_MULT = 0;
    static final int POS_MULT = 1;
    Transform transform;
    int mode = 0;
}
