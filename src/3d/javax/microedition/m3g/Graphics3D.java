package javax.microedition.m3g;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import javax.media.opengl.*;

/**
 * @author hroriz
 */

public class Graphics3D {

    M3gRender m3gRender = new M3gRender();
	private int hints;

	private class ViewPort {

		private int height;

		private int width;

		private int x;
 
		private int y;
 
		public ViewPort(int pX, int pY, int pWidth, int pHeight) {
			setX(pX);
			setY(pY);
			setWidth(pWidth);
			setHeight(pHeight);
		}

		public int getHeight() {
			return this.height;
		}

		public int getWidth() {
			return this.width;
		}

		public int getX() {
			return this.x;
		}
 
		public int getY() {
			return this.y;
		}
 
		public void setHeight(int height) {
			this.height = height;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public void setX(int x) {
			this.x = x;
		}
 
		public void setY(int y) {
			this.y = y;
		}
	}

	public static final int ANTIALIAS = 2;

	public static final int DITHER = 4;

	public static final int OVERWRITE = 16;

	private static Hashtable properties = null;

	private static Graphics3D singleton;

	public static final int TRUE_COLOR = 8;

	// Retrieves the singleton Graphics3D instance that is associated with this
	// application.
	public static Graphics3D getInstance() {
        if (singleton == null)
        {
            singleton = new Graphics3D();
    		if (properties == null) {
    			properties = new Hashtable();
    			properties.put("supportAntialiasing", new Boolean(true));
    			properties.put("supportTrueColor", new Boolean(true));
    			properties.put("supportDithering", new Boolean(true));
    			properties.put("supportMipmapping", new Boolean(true));
    			properties.put("supportPerspectiveCorrection", new Boolean(false));
    			properties.put("supportLocalCameraLighting", new Boolean(false));
    			properties.put("maxLights", new Integer(8));
    			properties.put("maxViewportDimension", new Integer(2048));
    			properties.put("maxTextureDimension", new Integer(1024));
    			properties.put("maxSpriteCropDimension", new Integer(1024));
    			properties.put("maxTransformsPerVertex", new Integer(1000));
    			properties.put("numTextureUnits", new Integer(10));
                
    			properties.put("maxViewportHeight", new Integer(2048));
    			properties.put("maxViewportWidth", new Integer(2048));
                
                singleton.maxViewportHeight = ((Integer) properties.get("maxViewportHeight")).intValue();
                singleton.maxViewportWidth = ((Integer) properties.get("maxViewportWidth")).intValue();
                
    		}
        }
		return singleton;
	}

	// Retrieves implementation specific properties.
	public static java.util.Hashtable getProperties() {
		return properties;
	}

	private Camera currentCamera;


	// No light added in the beggining
	private int lastAddedLightIndex = -1;

	private Vector lightArray = new Vector();

	private int maxViewportHeight  ;

	private int maxViewportWidth   ;

	private Object renderingTarget = null;



    
    private Vector lightTransformArray = new Vector();


    Light getLight( int i )
    {
         if (i >= this.lightArray.size() )
             return null;
         return (Light) this.lightArray.get(i);
    } 
     
    Transform getLightTransformArray( int i )
    {        
        if (i >= this.lightTransformArray.size() )
            return null;
        return (Transform) this.lightTransformArray.get(i);
    }
    
	// Binds a Light to use in subsequent immediate mode rendering.
	public int addLight(Light light, Transform transform)
			throws NullPointerException {
		if (light == null) {
			throw new NullPointerException("light cannot be null");
		}

		this.lightArray.add(light);
        this.lightTransformArray.add(transform);
		this.lastAddedLightIndex = this.lightArray.indexOf(light);
		return this.lastAddedLightIndex;
	}

	// Binds the given Graphics or mutable Image2D as the rendering target of
	// this Graphics3D.
	public void bindTarget(java.lang.Object target)
			throws NullPointerException, IllegalStateException,
			IllegalArgumentException {

		// Validates the given target
		//validateTarget(target);
        
		//this.m3gRender.bindTarget(target, hints);
		//this.renderingTarget = target;
		bindTarget(target, true, 0);
	}

	// Binds the given Graphics or mutable Image2D as the rendering target of
	// this Graphics3D.
	public void bindTarget(java.lang.Object target, boolean depthBuffer,
			int hints) {

		// Validates the given target
		validateTarget(target);

		if (hints != 0) {
			if ((hints & ~(ANTIALIAS | DITHER | TRUE_COLOR | OVERWRITE)) != 0) {
				throw new IllegalArgumentException(
						"hints must be 0 or ANTIALIAS|DITHER|TRUE_COLOR|OVERWRITE");
			}
		}
		this.hints = hints;
        this.m3gRender.bindTarget(target, depthBuffer, hints);
		this.renderingTarget = target;
	}

	/**
	 * Clears the viewport as specified in the given Background object. If the
	 * background object is null, the default settings are used. That is, the
	 * color buffer is cleared to transparent black, and the depth buffer to the
	 * maximum depth value (1.0).
	 * 
	 * @param background -
	 *            a Background object defining which buffers to clear and how,
	 *            or null to use the default settings
	 * @throws IllegalArgumentException -
	 *             if the background image in background is not in the same
	 *             format as the currently bound rendering target
	 * @throws IllegalStateException -
	 *             if this Graphics3D does not have a rendering target
	 */
	public void clear(Background background) throws IllegalArgumentException,
			IllegalStateException {
           
        if (this.renderingTarget == null) 
        {
            throw new IllegalStateException("this Graphics3D does not gave a rendering target.");
            
        } else
        {
            this.m3gRender.clear( background) ;
        }
    

	}

	public Camera getCamera(Transform transform) {

		return this.currentCamera;
	}

	public int getLightCount() {
		return this.lightArray.size();
	}

	public Object getTarget() {
		return this.renderingTarget;
	}

	// Flushes the rendered 3D image to the currently bound target and then
	// releases the target.
	public void releaseTarget() {
		this.renderingTarget = null;
        this.m3gRender.releaseTarget();
	}

	// Renders the given Sprite3D, Mesh, or Group node with the given
	// transformation from local coordinates to world coordinates.
	public void render(Node node, Transform transform)
    {
        this.m3gRender.render(node, transform);
	}


	// Renders the given submesh with the given transformation from local
	// coordinates to world coordinates.
	public void render(VertexBuffer vertices, IndexBuffer triangles,
			Appearance appearance, Transform transform) {
		
        m3gRender.render( vertices, triangles, appearance, transform, -1 );
//		System.out.println("Graphics3D.render NYI");
	}

	// Renders the given submesh with the given scope and the given
	// transformation from local coordinates to world coordinates.
	public void render(VertexBuffer vertices, IndexBuffer triangles,
			Appearance appearance, Transform transform, int scope) {
		// TODO -NYI-MND- render(VertexBuffer vertices, IndexBuffer triangles,
		// Appearance appearance, Transform transform, int scope)
        
        m3gRender.render( vertices, triangles, appearance, transform, scope );
//		System.out.println("Graphics3D.render NYI");
	}



    
	// Renders an image of world as viewed by the active camera of that World.
	public void render(World world) {
//		 TODO -NYI-MND- render(World world)
//		System.out.println("Graphics3D.render NYI");
        
//        Camera camera = world.getActiveCamera();
////        setCamera(camera, camera.getTransformTo(world));
//        setCamera(camera, camera.transform); // TODO
//               
//        resetLights();
////        mountLights(world);
//        
//        clear(world.getBackground());
//        this.render( world, this.currentTransform);
        
        m3gRender.render( world );
	}

	// Clears the array of current Lights.
	public void resetLights() {
		this.lightArray = new Vector();
        this.lightTransformArray = new Vector();

		this.lastAddedLightIndex = -1;
	}

	// Sets the Camera to use in subsequent immediate mode rendering.
	public void setCamera(Camera camera, Transform transform) {
		this.currentCamera = camera;
        
        this.m3gRender.setCamera( camera,  transform);
        
	}

	// Specifies the mapping of depth values from normalized device coordinates
	// to window coordinates.
	public void setDepthRange(float near, float far) {
        this.m3gRender.setDepthRange(near, far);
	}

	// Binds or unbinds a Light for subsequent immediate mode rendering.
	public void setLight(int index, Light light, Transform transform) {
		if ((index < 0) || (index >= getLightCount())) {
			throw new IndexOutOfBoundsException("index out of bounds");
		}
		this.lightArray.removeElementAt(index);
		this.lightArray.insertElementAt(light, index);
        
        this.lightTransformArray.removeElementAt(index);
        this.lightTransformArray.insertElementAt(transform, index);
        
	}

	// Specifies a rectangular viewport on the currently bound rendering target.
	/**
	 * Specifies a rectangular viewport on the currently bound rendering target.
	 * The viewport is the area where the view of the current camera will
	 * appear. Any parts of the viewport that lie outside the boundaries of the
	 * target clipping rectangle are silently clipped off; however, this must
	 * simply discard the pixels without affecting projection. The viewport
	 * upper left corner (x, y) is given relative to the origin for a Graphics
	 * rendering target, or the upper left corner for an Image2D target. Refer
	 * to the class description for details.
	 * 
	 * The viewport mapping transforms vertices from normalized device
	 * coordinates (xndc, yndc) to window coordinates (xw, yw) as follows:
	 * 
	 * xw = 0.5 xndc w + ox yw = -0.5 yndc h + oy
	 * 
	 * where w and h are the width and height of the viewport, specified in
	 * pixels, and (ox, oy) is the center of the viewport, also in pixels. The
	 * center of the viewport is obtained from the (x, y) coordinates of the top
	 * left corner as follows:
	 * 
	 * ox = x + 0.5 w oy = y + 0.5 h
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @throws IllegalArgumentException
	 */
	public void setViewport(int x, int y, int width, int height)
			throws IllegalArgumentException {
		// TODO -NYI-MND- setViewport(int x, int y, int width, int height)
//		System.out.println("Graphics3D.setViewport NYI");

		if ((width <= 0) || (height <= 0)) {
			throw new IllegalArgumentException(
					"both width and height must be greater than 0.");
		} else if ((width > this.maxViewportWidth)
				|| (height > this.maxViewportHeight)) {
			throw new IllegalArgumentException(
					"both width and height must be smaller than the viewport's dimensions.");
		} else {

            m3gRender.setViewPort(x, y, width, height);
		}

	}

	private void validateTarget(Object target) {
//		System.out.println("Render target: "+target);
		
		if (target == null) {
			throw new NullPointerException("target cannot be null");
		}

	//	if (this.renderingTarget != null) {
	//		throw new IllegalStateException(
	//				"this Graphics3D already has a rendering target");
	//	}

		if (target instanceof Image2D) {
			Image2D t2d = (Image2D) target;

			if (!t2d.isMutable()) {
				throw new IllegalArgumentException("target not a mutable Image2D");
			}

			if ((t2d.getWidth() > this.maxViewportWidth)
					|| (t2d.getHeight() > this.maxViewportHeight)) {

				throw new IllegalArgumentException("target boundaries beyond Viewport");
			}

			if (t2d.getFormat() != Image2D.RGBA
					&& t2d.getFormat() != Image2D.RGB) {

				throw new IllegalArgumentException("target's internal format must be RGB or RGBA");
			}

		} else if (!(target instanceof Graphics)) {
			throw new IllegalArgumentException("target not a javax.microedition.lcdui.Graphics");
		} 
	}
}
