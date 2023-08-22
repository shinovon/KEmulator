/*
 * Created on 22/12/2005
 *
 * Copyright (c) 2005, Funda��o Des. Paulo Feitoza. All rights reserved.
 * 
 */
package javax.microedition.m3g;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.glu.GLU;

import static javax.media.opengl.GL2.*;


/**
 * [Description for RenderVertexs]
 *
 * @author Rodrigo Cal (rcal@fpf.br) 
 */
class RenderVertices {


    static void render(
        GLAutoDrawable drawable, 
        VertexBuffer vertices,
        IndexBuffer triangles, 
        Appearance appearance, 
        Transform transform, 
        int scope,
        Transform camGenericMatrix
        )
    {
        int scopeCamera = Graphics3D.getInstance().m3gRender.getCamera().getScope();
        
        if ((scopeCamera & scope) == 0)
            return;
        
        render(drawable, vertices, triangles, appearance, transform, camGenericMatrix);
    }

    static void render(
        GLAutoDrawable drawable, 
        VertexBuffer vertices,
        IndexBuffer triangles, 
        Appearance appearance, 
        Transform transform,         
        Transform camGenericMatrix
        )
    {
       
        
        GL2 gl = drawable.getGL().getGL2();
                                      
        RenderUtils.settingCamera(gl, transform, camGenericMatrix);
        setAppearance(drawable, appearance);
        
        float[] positionScaleBias = new float[4];
        VertexArray vertexArrayPositions = vertices.getPositions(positionScaleBias);
        VertexArray vertexArrayNormals   = vertices.getNormals();
        VertexArray vertexArrayColor     = vertices.getColors();
        
        int[][] texCoords = new int[vertices.textureArrays.size()][];
        float[][] scaleBias = new float[vertices.textureArrays.size()][];
        Transform[] transformTexCoords = new Transform[scaleBias.length];
        
        for ( int k = 0 ; k < vertices.textureArrays.size() ; k++)
        {
            scaleBias[k] = new float[4];
            
            scaleBias[k][0] = 1;
            scaleBias[k][1] = 0;
            scaleBias[k][2] = 0;
            scaleBias[k][3] = 0;            
            texCoords[k] = RenderUtils.getIntArray(vertices.getTexCoords(k,scaleBias[k]));
            
            Texture2D texture2D = appearance.getTexture(k);
            
            transformTexCoords[k] = RenderUtils.getTransform(new Transform(), texture2D);;
            transformTexCoords[k].postTranslate(scaleBias[k][1], scaleBias[k][2], scaleBias[k][3]);
            transformTexCoords[k].postScale(scaleBias[k][0], scaleBias[k][0], scaleBias[k][0]);
        }
        int countIndices = 0;
        TriangleStripArray triangleStripArray = (TriangleStripArray) triangles;
        for (int i = 0; i < triangleStripArray.stripLenghts.length; i++)
        {
            gl.glBegin(GL_TRIANGLE_STRIP);
            
            int stripLenghts = triangleStripArray.stripLenghts[i];
            for (int j = 0; j < stripLenghts ; j++)
            {                 
                
                int index = triangleStripArray.indices[countIndices++];
                
                if (vertices instanceof VertexBufferDouble)
                {
                    VertexBufferDouble vertexBufferDouble = (VertexBufferDouble)vertices;
                    double[] normals = vertexBufferDouble.normalsExtras;
                    if (normals != null)
                        gl.glNormal3d(normals[index*3], normals[index*3+1], normals[index*3+2]);
                }
                else {
                    if (vertexArrayNormals != null) {
                        switch (vertexArrayNormals.componentSize) {
                            case 1:
                            {
                                byte[] normals = vertexArrayNormals.data1;
                                if (normals != null)
                                    gl.glNormal3d(normals[index*3], normals[index*3+1], normals[index*3+2]);
                                break;
                            }
                            case 2:
                            {
                                short[] normals = vertexArrayNormals.data2;
                                if (normals != null)
                                    gl.glNormal3d(normals[index*3], normals[index*3+1], normals[index*3+2]);
                                break;
                            }
                        }
                    }
                }
                
                
                if (vertexArrayColor != null)
                {    
                    boolean hasAlfa = false;
                    if ( vertexArrayColor != null)
                        hasAlfa = vertices.getColors().numComponents == 4;
                    
                    switch (vertexArrayColor.componentSize)
                    {
                        case 1:
                        {
                            byte[] colors = vertexArrayColor.data1;
                            if (colors != null){                               
                                
                                if (hasAlfa) {
                                    int red   = (colors[index*4]);
                                    int green = (colors[index*4+1]);
                                    int blue  = (colors[index*4+2]);
                                    int alfa  = (colors[index*4+3]);
                                    
                                    red     = (red   < 0? red    + 256: red     )/2;
                                    green   = (green < 0? green  + 256: green   )/2;
                                    blue    = (blue  < 0? blue   + 256: blue    )/2;
                                    alfa    = (alfa  < 0? alfa   + 256: alfa    )/2;
                                    
                                    gl.glColor4b((byte)(red), (byte)(green), (byte)(blue), (byte)(alfa));
                                }
                                else
                                {
                                    int red   = (colors[index*3]);
                                    int green = (colors[index*3+1]);
                                    int blue  = (colors[index*3+2]);
                                    
                                    red     = (red   < 0? red    + 256: red     )/2;
                                    green   = (green < 0? green  + 256: green   )/2;
                                    blue    = (blue  < 0? blue   + 256: blue    )/2;
                                    gl.glColor3b((byte)(red), (byte)(green), (byte)(blue));
                                }
                            }
                            break;
                        }
                        case 2:
                        {
                            short[] colors = vertexArrayColor.data2;
                            if (colors != null) {
                                if (hasAlfa) {
                                    int red   = (colors[index*4]);
                                    int green = (colors[index*4+1]);
                                    int blue  = (colors[index*4+2]);
                                    int alfa  = (colors[index*4+3]);
                                    
                                    red     = (red   < 0? red    + 256: red     )/2;
                                    green   = (green < 0? green  + 256: green   )/2;
                                    blue    = (blue  < 0? blue   + 256: blue    )/2;
                                    alfa    = (alfa  < 0? alfa   + 256: alfa    )/2;
                                    
                                    gl.glColor4b((byte)(red), (byte)(green), (byte)(blue), (byte)(alfa));
                                }
                                else {
                                    int red   = (colors[index*3]);
                                    int green = (colors[index*3+1]);
                                    int blue  = (colors[index*3+2]);
                                    
                                    red     = (red   < 0? red    + 256: red     )/2;
                                    green   = (green < 0? green  + 256: green   )/2;
                                    blue    = (blue  < 0? blue   + 256: blue    )/2;
                                    gl.glColor3b((byte)(red), (byte)(green), (byte)(blue));
                                }
                            }
                            break;
                        }
                    }       
                }
                else{
                    gl.glColor3b((byte)(127), (byte)(127), (byte)(127));
                }
                   
                
                for ( int k = 0 ; k < vertices.textureArrays.size() ; k++)
                {
                    if (k>=1)
                        continue;
                    if ( vertices.getTexCoords(k,null) != null)
                    switch ( vertices.getTexCoords(k,null).numComponents )
                    {
                        case 2:

                            if (transformTexCoords[k] != null)
                            {
                                float[] mat  = transformTexCoords[k].matrix;

                                double tx = mat[ 3];
                                double ty = mat[ 7];
                                
                                double x = texCoords[k][index*2];
                                double y = texCoords[k][index*2+1];
                                double z = 0;
                                 
                                double rx = ((mat[0] * x + mat[1] * y + mat[ 2] * z ))+tx;
                                double ry = ((mat[4] * x + mat[5] * y + mat[ 6] * z ))+ty;
                                
                                gl.glTexCoord2d(
                                		rx, 
                                		ry
                                		);
                           
                            }
                            else
                            {
                                gl.glTexCoord2f(
                                    texCoords[k][index*2]   * scaleBias[k][0] + scaleBias[k][1],
                                    texCoords[k][index*2+1] * scaleBias[k][0] + scaleBias[k][2]
                                                                         );
                            }
                            
                            break;
                             
                        case 3:
                            
                            if (transformTexCoords[k] != null)
                            {
                                float[] mat  = transformTexCoords[k].matrix;
                                
                                double tx = mat[ 3];
                                double ty = mat[ 7];
                                double tz = mat[11];
                                
                                double x = texCoords[k][index*3];
                                double y = texCoords[k][index*3+1];
                                double z = texCoords[k][index*3+2];
                                 
                                double rx = (mat[0] * x + mat[1] * y + mat[ 2] * z ) + tx;
                                double ry = (mat[4] * x + mat[5] * y + mat[ 6] * z ) + ty;
                                double rz = (mat[8] * x + mat[9] * y + mat[10] * z ) + tz;
                                
                                gl.glTexCoord3d(
                                    rx * scaleBias[k][0] ,
                                    ry * scaleBias[k][0] ,
                                    rz * scaleBias[k][0] );
                           
                            }
                            else
                            {
                                gl.glTexCoord3f(
                                    texCoords[k][index*3]   * scaleBias[k][0] + scaleBias[k][1],
                                    texCoords[k][index*3+1] * scaleBias[k][0] + scaleBias[k][2],
                                    texCoords[k][index*3+2] * scaleBias[k][0] + scaleBias[k][3]
                                                 );
                            }
                            break;
                    }
                }
                
            
                if (vertices instanceof VertexBufferDouble)
                {
                    VertexBufferDouble vertexBufferDouble = (VertexBufferDouble)vertices;
                    double[] positions = vertexBufferDouble.positionsExtras;
                    gl.glVertex3d(
                        positions[index*3  ] * positionScaleBias[0] + positionScaleBias[1],
                        positions[index*3+1] * positionScaleBias[0] + positionScaleBias[2],
                        positions[index*3+2] * positionScaleBias[0] + positionScaleBias[3]);
                }
                else
                {
                    switch (vertexArrayPositions.componentSize)
                    {
                        case 1:
                        {
                            byte[] positions = vertexArrayPositions.data1;
                            gl.glVertex3d(
                                positions[index*3  ] * positionScaleBias[0] + positionScaleBias[1],
                                positions[index*3+1] * positionScaleBias[0] + positionScaleBias[2],
                                positions[index*3+2] * positionScaleBias[0] + positionScaleBias[3]);
                            break;
                        }
                        case 2:
                        {
                            short[] positions = vertexArrayPositions.data2;
                            gl.glVertex3d( 
                                positions[index*3  ] * positionScaleBias[0] + positionScaleBias[1],
                                positions[index*3+1] * positionScaleBias[0] + positionScaleBias[2],
                                positions[index*3+2] * positionScaleBias[0] + positionScaleBias[3]);
                            break;
                        }
                    }
                }
            }
            gl.glEnd();
        }
        gl.glDisable(GL_TEXTURE_2D);  
    }
        
    static void setAppearance(GLAutoDrawable drawable, Appearance appearance)
    {
        GL2 gl = drawable.getGL().getGL2();
        
        setAppearanceCompositingMode(gl, appearance.getCompositingMode());
        setAppearanceMaterial(gl, appearance.getMaterial());
        setAppearancePolygonMode(gl, appearance.getPolygonMode());    
        setAppearanceFog(gl, appearance.getFog());
        setAppearanceTexture(drawable, appearance);
    }
         
    static void setAppearanceMaterial(GL2 gl, Material material)
    {
         
      if (material != null)
      {
          gl.glEnable(GL_LIGHTING);
          if (material.isVertexColorTrackingEnabled())
          {
              gl.glEnable (GL_COLOR_MATERIAL) ; 
          } 
          else
          {
              gl.glDisable (GL_COLOR_MATERIAL) ;              
          }
                   
          
          gl.glMaterialf (  GL_FRONT_AND_BACK, GL_SHININESS,  material.getShininess() );
          gl.glMaterialfv(  GL_FRONT_AND_BACK, GL_AMBIENT,   getColorArray(material.getColor(Material.AMBIENT) ), 0);
          gl.glMaterialfv(  GL_FRONT_AND_BACK, GL_DIFFUSE,   getColorArray(material.getColor(Material.DIFFUSE) ), 0);
          gl.glMaterialfv(  GL_FRONT_AND_BACK, GL_SPECULAR,  getColorArray(material.getColor(Material.SPECULAR)), 0);
          gl.glMaterialfv(  GL_FRONT_AND_BACK, GL_EMISSION,  getColorArray(material.getColor(Material.EMISSIVE)), 0);
      } 
      else {
          gl.glDisable(GL_LIGHTING);
      }
    
    }
    
    static float[] getColorArray(long color)
    {
        float[] data = new float[4];
 
                      
//      ARGB - color for the target property (or properties) in 0xAARRGGBB format 
        
        data[0] = ((color >> 16 ) & 255) / 255f ; //red;
        data[1] = ((color >> 8  ) & 255) / 255f ; //green;
        data[2] = ((color       ) & 255) / 255f ; //blue;
        data[3] = ((color >> 24 ) & 255) / 255f ; //alfa;
        
        
        return data;
    }
    
    static void setAppearancePolygonMode(GL2 gl, PolygonMode polygonMode ) {
      
      if (polygonMode != null )
      {
//          polygonMode.isTwoSidedLightingEnabled()
          switch (polygonMode.getCulling()) {
              case PolygonMode.CULL_BACK:
                  gl.glEnable(GL_CULL_FACE);
                  gl.glCullFace(GL_BACK );
                  break;
    
              case PolygonMode.CULL_FRONT:
                  gl.glEnable(GL_CULL_FACE);
                  gl.glCullFace(GL_FRONT );
                  break;
              
              case PolygonMode.CULL_NONE:
                  gl.glDisable(GL_CULL_FACE);
                  break;
          }
          
          switch (polygonMode.getShading()) {
              case PolygonMode.SHADE_FLAT:
                  gl.glShadeModel(GL_FLAT);
                  break;
    
              case PolygonMode.SHADE_SMOOTH:
                  gl.glShadeModel(GL_SMOOTH);
                  break;
          }
                
          
          switch (polygonMode.getWinding()) {
              case PolygonMode.WINDING_CW:
                  gl.glFrontFace (GL_CW );
                  break;
    
              case PolygonMode.WINDING_CCW:
                  gl.glFrontFace (GL_CCW);
                  break;
          }
      }
      else {
          gl.glEnable(GL_CULL_FACE);
          gl.glCullFace(GL_BACK );
      }
    }
    
    static void setAppearanceFog(GL2 gl, Fog fog) {
        
        if (fog != null)
        {
            switch(fog.getMode())
            {
                case Fog.LINEAR:
                {
                    gl.glFogf(GL_FOG_MODE, GL_LINEAR );
                    break;
                }
                case Fog.EXPONENTIAL:
                {
                    gl.glFogf(GL_FOG_MODE, GL_EXP );
                    break;
                }
            };
            
            gl.glFogf(GL_FOG_DENSITY, fog.getDensity() );
            gl.glFogf(GL_FOG_START, fog.getNearDistance() );
            gl.glFogf(GL_FOG_END, fog.getFarDistance() );
            gl.glFogf(GL_FOG_COLOR, fog.getColor() );
            
        }
    }
    
    static void setAppearanceCompositingMode(GL gl, CompositingMode compositingMode)
    {
      //  gl.glBlendEquation(GL_FUNC_ADD);
        gl.glDisable(GL_ALPHA_TEST);
        gl.glEnable(GL_BLEND);
        
        if (compositingMode != null){       
        	
            switch(compositingMode.getBlending()) {
                case CompositingMode.REPLACE:
                    gl.glBlendFunc(GL_ONE, GL_ZERO);
                    break;

                case CompositingMode.ALPHA_ADD:
                    gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);  
                    break;

                case CompositingMode.ALPHA:
                    gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );    
                    break;
                
                case CompositingMode.MODULATE:
                    gl.glBlendFunc(GL_DST_COLOR, GL_ZERO );       
                    break;
 
                case CompositingMode.MODULATE_X2:
                    gl.glBlendFunc(GL_DST_COLOR, GL_SRC_COLOR  );
                    break;
                
                default:
                    gl.glBlendFunc(GL_ONE, GL_ZERO);        
            } 
            
            if (compositingMode.isDepthTestEnabled()) {
                gl.glEnable (GL_DEPTH_TEST) ; 
                gl.glDepthFunc(GL_LEQUAL);
            } 
            else {
                gl.glDisable (GL_DEPTH_TEST) ;              
            }
            
            gl.glDepthMask (compositingMode.isDepthWriteEnabled());
            boolean cw = compositingMode.isColorWriteEnabled();
            gl.glColorMask(cw, cw, cw, true|
            		compositingMode.isAlphaWriteEnabled());
            
 //           gl.glEnable(GL_ALPHA_TEST);
 //            gl.glAlphaFunc(GL_GEQUAL, compositingMode.getAlphaThreshold() );
            gl.glPolygonOffset(compositingMode.getDepthOffsetFactor(), compositingMode.getDepthOffsetUnits());
        }
        else {
        	gl.glColorMask(true, true, true, true);
        	gl.glDepthMask(true);
            //default
            gl.glBlendFunc(GL_ONE, GL_ZERO);
            gl.glEnable (GL_DEPTH_TEST) ; 
            gl.glDepthFunc(GL_LEQUAL);
//            gl.glEnable (GL_BLEND) ;
//            gl.glDisable (GL_ALPHA_TEST) ; 
//  //          gl.glAlphaFunc(GL_GEQUAL, 1);            
        } 
//        gl.glAlphaFunc(GL_GEQUAL, 1);   
       
    }
    
    static void setAppearanceTexture(GLAutoDrawable drawable, Appearance appearance) {
        Texture2D texture2D = appearance.getTexture(0);
        int count = 0;
        while (texture2D != null)
        {
            setAppearanceTexture2D(drawable, texture2D);
            count++;
            texture2D = appearance.getTexture(count);
        }
    }
    
    static void setAppearanceTexture2D(GLAutoDrawable drawable, Texture2D texture2D ) {
        if (texture2D.getImage() == null) {
            throw new java.lang.IllegalStateException("No image2d in texture"); //$NON-NLS-1$
        }
        
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU(); // JSRCHG drawable.getGLU();
        
        setTextureColor(gl, texture2D);
        setTextureBlending(gl, texture2D);
        setTextureWrapping(gl, texture2D);
        setTextureLevelFilterAndImageFilter(gl, texture2D);
        addTextureFormat(gl, glu, texture2D);
                 
        gl.glEnable(GL_TEXTURE_2D);  
    }
    
    static void setTextureColor(GL2 gl,Texture2D texture2D) {
        
        int color = texture2D.getBlendColor();
        float envColor[]  = new float[]
            {
                (((color >>  0) & 255)/256f) ,
                (((color >>  8) & 255)/256f) ,
                (((color >> 16) & 255)/256f) ,
                (((color >> 24) & 255)/256f) 
            };

        gl.glTexEnvfv(GL_TEXTURE_ENV, GL_TEXTURE_ENV_COLOR, envColor, 0); 
    }
    
    static void setTextureBlending(GL2 gl,Texture2D texture2D) {

    	//gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);  
        switch( texture2D.getBlending())  {
            
            case Texture2D.FUNC_BLEND:
                gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_BLEND);
                break;

            case Texture2D.FUNC_ADD:
                gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_ADD);
                break;

            case Texture2D.FUNC_DECAL:
                gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
                break;

            case Texture2D.FUNC_MODULATE:
                gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);                
                break;
            
            
            case Texture2D.FUNC_REPLACE:
            default:    
                gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
                            
        } 
    }
    
    static void setTextureWrapping(GL gl,Texture2D texture2D)
    {
        switch(texture2D.getWrappingS())
        {
            case Texture2D.WRAP_CLAMP:
            {
                gl.glTexParameterf(GL_TEXTURE_2D,  GL_TEXTURE_WRAP_S, GL_CLAMP); 
                break;
            }
            
            case Texture2D.WRAP_REPEAT:
            {
                gl.glTexParameterf(GL_TEXTURE_2D,  GL_TEXTURE_WRAP_S, GL_REPEAT);  
                break;
            }
            
            default:
            {
                gl.glTexParameterf(GL_TEXTURE_2D,  GL_TEXTURE_WRAP_S, GL_REPEAT);  
                break;
            }
        }
        
        
        switch(texture2D.getWrappingT())
        {
            case Texture2D.WRAP_CLAMP:
                gl.glTexParameterf(GL_TEXTURE_2D,  GL_TEXTURE_WRAP_T, GL_CLAMP); 
                break;
            
            case Texture2D.WRAP_REPEAT:
                gl.glTexParameterf(GL_TEXTURE_2D,  GL_TEXTURE_WRAP_T, GL_REPEAT);  
                break;
            
            default:
                gl.glTexParameterf(GL_TEXTURE_2D,  GL_TEXTURE_WRAP_T, GL_REPEAT);  
                break;
        }
        
        if (texture2D.specialCaseBackgroundS)
	        gl.glTexParameterf(GL_TEXTURE_2D,  GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);  

        if (texture2D.specialCaseBackgroundT)
        	gl.glTexParameterf(GL_TEXTURE_2D,  GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
    }
    
    static void setTextureLevelFilterAndImageFilter(GL gl,Texture2D texture2D){



        switch(texture2D.levelFilter) {
            case Texture2D.FILTER_BASE_LEVEL: {
                switch(texture2D.imageFilter){                 
                    case Texture2D.FILTER_LINEAR:
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_LINEAR);
                        break;
                    
                    case Texture2D.FILTER_NEAREST:
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_NEAREST);
                        break;

                    default:
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_LINEAR);
                        break;
                }                
                break;
            }
            
            case Texture2D.FILTER_LINEAR: {
                switch(texture2D.imageFilter)  {                 
                    case Texture2D.FILTER_LINEAR: 
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_LINEAR_MIPMAP_LINEAR);
                        break;
                    
                    case Texture2D.FILTER_NEAREST: 
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_NEAREST_MIPMAP_LINEAR);
                        break;

                    default: 
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_LINEAR_MIPMAP_LINEAR);
                        break;
                }  
                break;
            }
            
            case Texture2D.FILTER_NEAREST: {
                switch(texture2D.imageFilter) {                 
                    case Texture2D.FILTER_LINEAR:
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_LINEAR_MIPMAP_NEAREST);
                        break;
                    
                    case Texture2D.FILTER_NEAREST:
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_NEAREST_MIPMAP_NEAREST);
                        break;

                    default:
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_LINEAR_MIPMAP_NEAREST);
                        break;

                }                  
                break;
            }

            default:  {
                
                switch(texture2D.imageFilter) {                 
                    case Texture2D.FILTER_LINEAR: 
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_LINEAR_MIPMAP_LINEAR);
                        break;
                    
                    case Texture2D.FILTER_NEAREST:
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_NEAREST_MIPMAP_LINEAR);
                        break;

                    default:
                        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_LINEAR_MIPMAP_LINEAR);
                        break;

                }                  
                break;
            }
        } 
        
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_LINEAR);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,  GL_NEAREST );

    }
    
    static void addTextureFormat(GL gl, GLU glu, Texture2D texture2D)
    {
        int data[] = texture2D.image.getDataJOGL();
//        if (data == null)
//            data = this.data;
            
//      gl.glTexImage2D(
//      GL_TEXTURE_2D, 
//      nivel, // nivel 
//      GL_RGBA, // internalformat : GL_ALPHA, GL_ALPHA4, GL_ALPHA8, GL_ALPHA12, GL_ALPHA16, GL_LUMINANCE, GL_LUMINANCE4, GL_LUMINANCE8, GL_LUMINANCE12, GL_LUMINANCE16, GL_LUMINANCE_ALPHA, GL_LUMINANCE4_ALPHA4, GL_LUMINANCE6_ALPHA2, GL_LUMINANCE8_ALPHA8, GL_LUMINANCE12_ALPHA4, GL_LUMINANCE12_ALPHA12, GL_LUMINANCE16_ALPHA16, GL_INTENSITY, GL_INTENSITY4, GL_INTENSITY8, GL_INTENSITY12, GL_INTENSITY16, GL_R3_G3_B2, GL_RGB, GL_RGB4, GL_RGB5, GL_RGB8, GL_RGB10, GL_RGB12, GL_RGB16, GL_RGBA, GL_RGBA2, GL_RGBA4, GL_RGB5_A1, GL_RGBA8, GL_RGB10_A2, GL_RGBA12, or GL_RGBA16
//      texture2D.getImage().getWidth(), //width
//      texture2D.getImage().getHeight(), //height
//      0, // border 0..1
//      GL_RGBA, //GL_COLOR_INDEX, GL_RED, GL_GREEN, GL_BLUE, GL_ALPHA, GL_RGB, GL_RGBA, GL_LUMINANCE, and GL_LUMINANCE_ALPHA
//      GL_UNSIGNED_BYTE, //GL_UNSIGNED_BYTE, GL_BYTE, GL_BITMAP, GL_UNSIGNED_SHORT, GL_SHORT, GL_UNSIGNED_INT, GL_INT, and GL_FLOAT.
//      texture2D.getImage().rgba
//      );
        
        int internalformat = 3;
        switch ( texture2D.getImage().getFormat() ) {
            case Image2D.ALPHA:
                internalformat = GL_ALPHA;
                break;

            case Image2D.LUMINANCE:
                internalformat = GL_LUMINANCE;
                break;

            case Image2D.LUMINANCE_ALPHA:
                internalformat = GL_LUMINANCE_ALPHA;
                break;

            case Image2D.RGB:
                internalformat = GL_RGB;
                break;
            
            case Image2D.RGBA:
                internalformat = GL_RGBA;
                break;
        } 
         
//        IntBuffer buffer = 
//        IntBuffer buffer = BufferUtil.newIntBuffer(data.length); //= new Buffer();
       IntBuffer buffer = IntBuffer.wrap(data);
        
        gl.glTexImage2D(
            GL_TEXTURE_2D, 
            0, // nivel 
            internalformat, // internalformat : GL_ALPHA, GL_ALPHA4, GL_ALPHA8, GL_ALPHA12, GL_ALPHA16, GL_LUMINANCE, GL_LUMINANCE4, GL_LUMINANCE8, GL_LUMINANCE12, GL_LUMINANCE16, GL_LUMINANCE_ALPHA, GL_LUMINANCE4_ALPHA4, GL_LUMINANCE6_ALPHA2, GL_LUMINANCE8_ALPHA8, GL_LUMINANCE12_ALPHA4, GL_LUMINANCE12_ALPHA12, GL_LUMINANCE16_ALPHA16, GL_INTENSITY, GL_INTENSITY4, GL_INTENSITY8, GL_INTENSITY12, GL_INTENSITY16, GL_R3_G3_B2, GL_RGB, GL_RGB4, GL_RGB5, GL_RGB8, GL_RGB10, GL_RGB12, GL_RGB16, GL_RGBA, GL_RGBA2, GL_RGBA4, GL_RGB5_A1, GL_RGBA8, GL_RGB10_A2, GL_RGBA12, or GL_RGBA16
            texture2D.getImage().getWidth(), //width
            texture2D.getImage().getHeight(), //height
            0, // border 0..1
            GL_RGBA, //GL_COLOR_INDEX, GL_RED, GL_GREEN, GL_BLUE, GL_ALPHA, GL_RGB, GL_RGBA, GL_LUMINANCE, and GL_LUMINANCE_ALPHA
            GL_UNSIGNED_BYTE, //GL_UNSIGNED_BYTE, GL_BYTE, GL_BITMAP, GL_UNSIGNED_SHORT, GL_SHORT, GL_UNSIGNED_INT, GL_INT, and GL_FLOAT.
            buffer // JSRCHG data
            );
        
//        buffer.get(data);
        
  
        
        
//        glu.gluBuild2DMipmaps(
//            GL_TEXTURE_2D,
//            internalformat, // internalformat 
//            texture2D.getImage().getWidth(), //width
//            texture2D.getImage().getHeight(),//height
//            GL_RGBA,
//            GL_UNSIGNED_INT, 
//            data
//            ); 
    }

}
