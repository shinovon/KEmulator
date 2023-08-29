/*
 * Created on 27/12/2005
 *
 * Copyright (c) 2005, Funda��o Des. Paulo Feitoza. All rights reserved.
 * 
 */
package javax.microedition.m3g;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;

/**
 * [Description for RenderClear]
 *
 * @author Rodrigo Cal (rcal@fpf.br) 
 */
public class RenderClear {

    
    static void renderClear( GLAutoDrawable drawable, boolean depthBuffer, int hints, Background background)
    {
        GL2 gl = drawable.getGL().getGL2();
        if (background == null) {
            gl.glClearDepth(1.0d);
            gl.glDepthMask(true);
            gl.glColorMask(true, true, true, true);
            gl.glClearColor(0,0,0, 0);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT /*| (depthBuffer ? GL.GL_DEPTH_BUFFER_BIT : 0)*/);
        
          //  gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
            return;
        }
        //else {
            gl.glClearDepth(1.0d);
            gl.glDepthMask(true);
            gl.glColorMask(true, true, true, true);
        	if(background.isColorClearEnabled()){
                int color = background.getColor();
                int alfa  = (color >> 24) & 255 ;
                int red   = (color >> 16) & 255 ;
                int green = (color >> 8 ) & 255 ;
                int blue  = (color      ) & 255 ;
                
                gl.glClearColor(red/255.0f, green/255.0f, blue/255.0f, alfa/255.0f);

        	}
                

            gl.glClear((background.isColorClearEnabled() ? GL.GL_COLOR_BUFFER_BIT : 0) |
                    (background.isDepthClearEnabled() ? GL.GL_DEPTH_BUFFER_BIT : 0));
        	
//       	System.out.println("Clear -- bg image: "+background.getImage());
        	
        	if(background.getImage() != null){
            	renderBackground(drawable, background);
        	}
        //}
/*        else {
        	gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        	gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
                
//                // TODO verify
//                Image2D image2D = background.getImage();
//                int data[] = image2D.rgba;
//                gl.glDrawPixels(0,0, image2D.getWidth(), image2D.getHeight(), data);
//                
                
        } */
    }
    
    
    static void renderBackground( GLAutoDrawable drawable, Background background) {
      
      VertexArray vertexArrayPoints = new VertexArray(4, 3, 2);
      vertexArrayPoints.set(
              0,
              4, 
              new short[]
                       {
                         (short) (-1),(short) (-1),               1,
                         (short) ( 1),(short) (-1),               1,
                         (short) (-1),(short) ( 1),               1,
                         (short) ( 1),(short) ( 1),               1                           
                       }
              );
      
      VertexArray vertexArrayNormals = new VertexArray(4,3,2);
      vertexArrayNormals.set(
              0,
              4,
              new short[]
                       {
                          (short) (-1),(short) (-1),     -2,
                          (short) ( 1),(short) (-1),     -2,
                          (short) (-1),(short) ( 1),     -2,
                          (short) ( 1),(short) ( 1),     -2
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
      vertexBuffer.setPositions ( vertexArrayPoints, 1, null    );
      vertexBuffer.setNormals   ( vertexArrayNormals            );
      vertexBuffer.setTexCoords ( 0, vertexArrayTexture, 1, new float[]
                                                                      {
    		  (float)background.getCropX()/background.getCropWidth(),
    		  (float)background.getCropY()/background.getCropHeight(),
    		  0}
      );
      vertexBuffer.setColors(vertexArrayColors); 
      
      TriangleStripArray triangleStripArray  = new TriangleStripArray(new int[]{0,1,2, 3,1,2}, new int[]{3,3});
      
      Image2D image2D =  background.getImage();
        
      Texture2D texture2D = new Texture2D(image2D);
 
      setMode(texture2D, background);
      texture2D.setScale( 
    		  (float)background.getCropWidth()/image2D.getWidth(),
    		  (float)background.getCropHeight()/image2D.getHeight(),
    		  1
    		  );
       
      Appearance appearance = new Appearance();
      CompositingMode compositingMode = new CompositingMode();
      compositingMode.setBlending(CompositingMode.REPLACE);
      compositingMode.setAlphaWriteEnable(false);
      appearance.setCompositingMode(compositingMode);
      
      PolygonMode polygonMode = new PolygonMode(); 
      polygonMode.setCulling(PolygonMode.CULL_NONE);
      appearance.setPolygonMode(polygonMode);
      appearance.setTexture(0, texture2D );
      
      Transform transform = new Transform();

      RenderVertices.render(drawable, vertexBuffer, triangleStripArray, appearance, transform, transform);
 
    }
    
    
    static void setMode(Texture2D texture2D, Background background)
    {
        int typeX = background.getImageModeX();
        int typeY = background.getImageModeY();
        
        switch(background.getImageModeX())
        {
            case Background.BORDER:
                typeX = Texture2D.WRAP_CLAMP;
                texture2D.specialCaseBackgroundS = true;
                break;

            case Background.REPEAT:
                typeX = Texture2D.WRAP_REPEAT;
                break;
        }
        
        switch(background.getImageModeY())
        {
            case Background.BORDER:
                typeY = Texture2D.WRAP_CLAMP;
                texture2D.specialCaseBackgroundT = true;
                break;

            case Background.REPEAT:
                typeY = Texture2D.WRAP_REPEAT;
                break;
        }
        
        texture2D.setWrapping(typeX, typeY);
    }
}
