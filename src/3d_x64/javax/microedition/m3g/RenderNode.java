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
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;

/**
 * [Description for RenderNode]
 *
 */
class RenderNode {

    static void render(GLAutoDrawable drawable, Object3D obj, Transform transform, Transform camGenericMatrix, int alphaMode )
    {
        if (obj instanceof Node)
        {
            if (!((Node)obj).isRenderingEnabled())
                return; // not must rendering
        }
        
       processObject(drawable, obj, transform, camGenericMatrix, alphaMode);
       
       int numReferences = obj.getReferences(null);
       if (numReferences > 0)
       {
          Object3D[] objArray = new Object3D[numReferences];
          obj.getReferences(objArray);
          for (int i = 0; i < numReferences; i++)
          {
              Object3D object3D = objArray[i];
              Transform transformAdd = RenderUtils.getTransform(transform, object3D);
              render(drawable, objArray[i], transformAdd, camGenericMatrix, alphaMode);  
          }
       }
    }

    /**
     */
    private static void processMesh(GLAutoDrawable drawable, Mesh mesh, Transform transform , Transform camGenericMatrix, int alphaMode) 
    {
        VertexBuffer vb = mesh.getVertexBuffer();
        Appearance   ap = null;
        IndexBuffer  ib = null;
       
        for (int i = 0; i < mesh.submeshes.length; i++)
        {
            ap = mesh.getAppearance(i);
            
            boolean opaque = ap.getCompositingMode().getBlending() == CompositingMode.REPLACE;
            
            if((opaque && ((alphaMode & M3gRender.SURFACE_OPAQUE) != 0))
            		|| (!opaque && ((alphaMode & M3gRender.SURFACE_TRANSPARENT) != 0))){
                ib = mesh.getIndexBuffer(i);            
            	RenderVertices.render(drawable, vb, ib, ap, transform, mesh.scope , camGenericMatrix);
            }
        }
    }
    
    private static void processSkinnedMesh(GLAutoDrawable drawable, SkinnedMesh skinnedMesh, Transform transform, Transform camGenericMatrix, int alphaMode) {
      
        VertexBuffer vb = skinnedMesh.getVertexBuffer();
       
        VertexBufferDouble vertexBufferDouble = new VertexBufferDouble(vb);
        
        double[] positions = vertexBufferDouble.positionsExtras;         
       
        for (int i = 0; i < positions.length; i+=3)
        {
        	positions[ i ] = positions[ i ] * vertexBufferDouble.positionScale + vertexBufferDouble.positionBias[0];
        	positions[i+1] = positions[i+1] * vertexBufferDouble.positionScale + vertexBufferDouble.positionBias[1];
        	positions[i+2] = positions[i+2] * vertexBufferDouble.positionScale + vertexBufferDouble.positionBias[2];
        }
        
        int size = skinnedMesh.transformations.size();

        RecSkinnedMeshTransformation array[] = new RecSkinnedMeshTransformation[size] ;
        
        HashMap hashMap = new HashMap();
        RenderUtils.getHashTransformSkinnedMesh(skinnedMesh.getSkeleton(), new Transform(), new Transform(), hashMap );
        
        for (int i = 0 ; i < size; i++)
        {
        	SkinnedMesh.Transformation transformation = (SkinnedMesh.Transformation) skinnedMesh.transformations.get(i);
        	array[i] = new RecSkinnedMeshTransformation();
        	array[i].transformation = transformation;
        	array[i].transform = RenderUtils.getTransformSkinnedMesh( hashMap, transformation.bone);
        }
        
        double originalPosition[] = new double[3];

        for (int i = 0; i < positions.length / 3 ; i++)
        {
        	int index = i*3; 
            originalPosition[0] = positions[index  ];
            originalPosition[1] = positions[index+1];
            originalPosition[2] = positions[index+2];
            
            Vector  calculatePositon = new Vector ();
            
            for (int j = 0; j < array.length; j++ )
            {
            	
            	SkinnedMesh.Transformation transformation = array[j].transformation;
            	if (
            			(i >=  transformation.firstVertex)  &&
            			(i < (transformation.firstVertex + transformation.vertexCount))  
            			)

            	{
	                positions[index  ] = originalPosition[0];
	                positions[index+1] = originalPosition[1];
	                positions[index+2] = originalPosition[2];
	                
	                Transform transformRotate = array[j].transform;
	                applyTransform(i, positions, transformRotate);   
	                
	                double vetPosition[] = new double[4];
	                vetPosition[0] = positions[index  ];
	                vetPosition[1] = positions[index+1];
	                vetPosition[2] = positions[index+2];
	                vetPosition[3] = transformation.weight;
	                calculatePositon.add(vetPosition);
            	}
                
            }
            
            double vetMidPosition[] = new double[3];
            double total = 0;
            for (int j = 0; j < calculatePositon.size(); j++ )
            {               
                double vetPosition[] = (double[]) calculatePositon.get(j);
                double weight = vetPosition[3];
                total += weight;
                vetMidPosition[0] += vetPosition[0]*weight;
                vetMidPosition[1] += vetPosition[1]*weight;
                vetMidPosition[2] += vetPosition[2]*weight;
            }                
                              
            vetMidPosition[0] /= total;
            vetMidPosition[1] /= total;
            vetMidPosition[2] /= total;
               
            if (calculatePositon.size() > 0 )
            {
                positions[i*3  ] = vetMidPosition[0];  
                positions[i*3+1] = vetMidPosition[1];
                positions[i*3+2] = vetMidPosition[2];
            }
            
        }
        
        vertexBufferDouble.positionScale   = 1;
        vertexBufferDouble.positionBias[0] = 0;
        vertexBufferDouble.positionBias[1] = 0;
        vertexBufferDouble.positionBias[2] = 0;
        
        
        Mesh mesh = new Mesh(vertexBufferDouble, skinnedMesh.submeshes, skinnedMesh.appearances );
        RenderNode.processMesh(drawable, mesh, transform, camGenericMatrix, alphaMode );
    }
    
    
    private static void applyTransform(int i, double[] pos, Transform transform)
    {      
        float[] mat  = transform.matrix;
 
        int count = i*3;       
        {     
            double tx = (double)( mat[ 3] ) ; 
            double ty = (double)( mat[ 7] ) ;
            double tz = (double)( mat[11] ) ;
            
            double x = (double)(pos[count  ]);
            double y = (double)(pos[count+1]);
            double z = (double)(pos[count+2]);
            
            pos[count  ] = (double)((mat[0] * x + mat[1] * y + mat[ 2] * z ) + tx)  ;
            pos[count+1] = (double)((mat[4] * x + mat[5] * y + mat[ 6] * z ) + ty) ;
            pos[count+2] = (double)((mat[8] * x + mat[9] * y + mat[10] * z ) + tz);
        }     
    }
    

    /**
     */
    private static void processMorphingMesh(GLAutoDrawable drawable, MorphingMesh morphingMesh, Transform transform, Transform camGenericMatrix, int alphaMode) 
    {
        
        VertexBuffer vb = RenderUtils.getMorphingMesh (morphingMesh);;
        Appearance   ap = null;
        IndexBuffer  ib = null;
        
        for (int i = 0; i < morphingMesh.submeshes.length; i++)
        {
            ap = morphingMesh.getAppearance(i);
            
            boolean opaque = ap.getCompositingMode().getBlending() == CompositingMode.REPLACE;
            
            if((opaque && ((alphaMode & M3gRender.SURFACE_OPAQUE) != 0))
            		|| (!opaque && ((alphaMode & M3gRender.SURFACE_TRANSPARENT) != 0))){

                ib = morphingMesh.getIndexBuffer(i);            
            	RenderVertices.render(drawable, vb,ib,ap, transform, morphingMesh.getScope(), camGenericMatrix);
            }
        }
    }
   
    private static void processSprite3D(GLAutoDrawable drawable, Sprite3D sprite3D, Transform transform, Transform camGenericMatrix, int alphaMode) 
    {
 
        boolean opaque = sprite3D.getAppearance().getCompositingMode().getBlending() == CompositingMode.REPLACE;
        
        if(!((opaque && ((alphaMode & M3gRender.SURFACE_OPAQUE) != 0))
        		|| (!opaque && ((alphaMode & M3gRender.SURFACE_TRANSPARENT) != 0)))){
        	return;
        }

    	
//        sprite3D.setAlignment(null, Node.Z_AXIS, null, Node.Y_AXIS); // TODO 
//        sprite3D.align(null);
        
        VertexArray vertexArrayPoints = new VertexArray(4, 3, 2);
        vertexArrayPoints.set(
                0,
                4, 
                new short[]
                         {
                           (short) (-sprite3D.getCropWidth()/2),(short) (-sprite3D.getCropHeight()/2),              0,
                           (short) ( sprite3D.getCropWidth()/2),(short) (-sprite3D.getCropHeight()/2),              0,
                           (short) (-sprite3D.getCropWidth()/2),(short) ( sprite3D.getCropHeight()/2),              0,
                           (short) ( sprite3D.getCropWidth()/2),(short) ( sprite3D.getCropHeight()/2),              0                           
                         }
                );
        
        VertexArray vertexArrayNormals = new VertexArray(4,3,2);
        vertexArrayNormals.set(
                0,
                4,
                new short[]
                         {
                                                          0,                                0,             -2,
                            (short) sprite3D.getCropWidth(),                                0,             -2,
                                                          0, (short) sprite3D.getCropHeight(),             -2,
                            (short) sprite3D.getCropWidth(), (short) sprite3D.getCropHeight(),             -2       
                         }
                );
        
        
        VertexArray vertexArrayTexture = new VertexArray(4,2,1);
        vertexArrayTexture.set(
                0,
                4,
                new byte[] 
                        {
                            0,  1,
                            1,  1,
                            0,  0,
                            1,  0 
                        }
                );
        
        
        
        VertexArray vertexArrayColors = new VertexArray(4,3,1);
        vertexArrayColors.set(
                0,
                4,
                new byte[]
                         { 
                                   0,         0,   (byte)255,
                           (byte)255, (byte)255,   (byte)0,
                           (byte)255,         0,           0,
                                   0, (byte)255,           0
                         }
                );
        
        
        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.setPositions ( 
            vertexArrayPoints,
            1,
            new float[]{
                sprite3D.getCropX()-sprite3D.getCropWidth()/2,
                sprite3D.getCropY()-sprite3D.getCropHeight()/2,
                0
                }   
            );
        
        vertexBuffer.setNormals   ( vertexArrayNormals            );
        vertexBuffer.setTexCoords ( 0, vertexArrayTexture, 1, new float[]{0,0,0});
        vertexBuffer.setColors(vertexArrayColors); 
        
        TriangleStripArray triangleStripArray  = new TriangleStripArray(new int[]{0,1,2, 3,1,2}, new int[]{3,3});
        
        
        Texture2D texture2D  = new Texture2D(sprite3D.getImage());

        Appearance appearance = new Appearance();
        PolygonMode polygonMode = new PolygonMode(); 
        polygonMode.setCulling(PolygonMode.CULL_NONE);
        appearance.setPolygonMode(polygonMode);
        appearance.setTexture(0, texture2D );
        
//        Material material = new Material();
//        material.setColor(Material.AMBIENT,  255&(255<<8)&(255<<16) );
//        material.setColor(Material.EMISSIVE, 255&(255<<8)&(255<<16) );
//        material.setColor(Material.DIFFUSE,  255&(255<<8)&(255<<16) );
//        material.setColor(Material.SPECULAR, 255&(255<<8)&(255<<16) );
//        appearance.setMaterial(material);
        
//        sprite3D.getAppearance().getCompositingMode().setDepthWriteEnable(true);
//        sprite3D.getAppearance().getCompositingMode().setDepthTestEnable(true);
//        sprite3D.getAppearance().getCompositingMode().setAlphaWriteEnable(false);
 
             
        appearance.setCompositingMode(  sprite3D.getAppearance().getCompositingMode() );
        appearance.setFog(  sprite3D.getAppearance().getFog() );
        
        float val =  sprite3D.getCropWidth() > sprite3D.getCropHeight() ? sprite3D.getCropWidth(): sprite3D.getCropHeight();
        
        Camera camera = Graphics3D.getInstance().m3gRender.getCamera();
        sprite3D.setAlignment(camera, Node.Z_AXIS, null, Node.NONE);
        sprite3D.align(camera);
        
        Transform transformAdd = RenderUtils.getTransform(transform, sprite3D);
        Transformable transformable = sprite3D;
        
        float[] scale = new float[3]; 
        transformable.getScale(scale);
        
        transformAdd.postScale(
            (1/val)/(scale[0]), 
            (1/val)/(scale[1]),
            (1/val)/(scale[2]) 
                          );
        
        float mat[] = new float[16];
        transformAdd.get(mat);

//        transformAdd.matrix[12] = 0;
//        transformAdd.matrix[13] = 0;
//        transformAdd.matrix[14] = 0;
        
        RenderVertices.render(drawable, vertexBuffer, triangleStripArray, appearance, transformAdd, sprite3D.getScope(), camGenericMatrix);
          

    }
        
    /**
     * @param object3D
     * @param transform
     */
    private static void processObject(GLAutoDrawable drawable, Object3D object3D, Transform transform, Transform camGenericMatrix, int alphaMode) {
    	
    	if (object3D instanceof SkinnedMesh) {
    		processSkinnedMesh(drawable, (SkinnedMesh)object3D, transform, camGenericMatrix, alphaMode);
    	}
    	else if (object3D instanceof MorphingMesh) {
    		processMorphingMesh(drawable, (MorphingMesh)object3D, transform, camGenericMatrix, alphaMode);
    	}
    	else if (object3D instanceof Mesh) {
    		processMesh(drawable, (Mesh)object3D, transform, camGenericMatrix, alphaMode);
    	} 
    	else  if (object3D instanceof Sprite3D) {
    		processSprite3D (drawable, (Sprite3D)object3D, transform, camGenericMatrix, alphaMode);
    	}        
    }
    
}

class RecSkinnedMeshTransformation
{
    SkinnedMesh.Transformation transformation;     
    Transform transform;
}

 
