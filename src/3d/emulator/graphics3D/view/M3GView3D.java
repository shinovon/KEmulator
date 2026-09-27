package emulator.graphics3D.view;

import emulator.AppSettings;
import emulator.Emulator;
import emulator.Settings;
import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.Transform3D;
import emulator.graphics3D.Vector4f;
import emulator.graphics3D.lwjgl.Emulator3D;
import emulator.graphics3D.lwjgl.GLCanvasUtil;
import emulator.graphics3D.lwjgl.LWJGLUtil;
import emulator.graphics3D.m3g.LightsCache;
import emulator.graphics3D.m3g.MeshMorph;
import emulator.graphics3D.m3g.RenderObject;
import emulator.graphics3D.m3g.RenderPipe;
import emulator.ui.swt.SWTFrontend;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import javax.microedition.m3g.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public final class M3GView3D implements PaintListener, Runnable {
	private static M3GView3D instance;
	private LWJGLUtil memoryBuffers;
	private RenderPipe renderPipe;
	private boolean xray;
	private int viewportWidth;
	private int viewportHeight;
	private float depthRangeNear;
	private float depthRangeFar;
	private static Camera camera;
	private static Transform cameraTransform = new Transform();
	private static Vector lights = new Vector();
	private static Vector lightsTransforms = new Vector();
	private static Canvas canvas;
	private static ByteBuffer buffer;
	private static ImageData bufferImage;
	private static GLCapabilities capabilities;
	private static long window;
	private boolean paintListenerSet;

	private M3GView3D() {
		instance = this;
		this.depthRangeNear = 0.0F;
		this.depthRangeFar = 1.0F;
		memoryBuffers = new LWJGLUtil();
		renderPipe = new RenderPipe();
	}

	public static M3GView3D getViewInstance() {
		if (instance == null) {
			instance = new M3GView3D();
		}

		return instance;
	}

	public boolean isRenderInvisibleNodes() {
		return renderPipe.isRenderInvisibleNodes();
	}

	public void setRenderInvisibleNodes(boolean render) {
		renderPipe.setRenderInvisibleNodes(render);
	}

	public final void setXray(boolean xray) {
		this.xray = xray;
	}

	public final void setViewport(int width, int height) {
		this.viewportWidth = width;
		this.viewportHeight = height;
	}

	private void setupViewport() {
		GL11.glViewport(0, 0, this.viewportWidth, this.viewportHeight);
		GL11.glScissor(0, 0, this.viewportWidth, this.viewportHeight);
	}

	private void setupDepth() {
		GL11.glDepthRange((double) this.depthRangeNear, (double) this.depthRangeFar);
	}

	public final void clearBackground(Background background) {
		this.setupViewport();
		this.setupDepth();
		GL11.glClearDepth(1.0D);
		GL11.glDepthMask(true);
		GL11.glColorMask(true, true, true, true);
		int clearColor = background != null && !this.xray ? background.getColor() : 0;
		GL11.glClearColor(G3DUtils.getFloatColor(clearColor, 16), G3DUtils.getFloatColor(clearColor, 8), G3DUtils.getFloatColor(clearColor, 0), G3DUtils.getFloatColor(clearColor, 24));
		GL11.glClear(16640);
		if (background != null && !this.xray) {
			GL11.glClear(background.isColorClearEnabled() ? 16384 : 0);
			this.drawBackgroundImage(background);
		} else {
			GL11.glClear(GL_COLOR_BUFFER_BIT);
		}
	}

	private void drawBackgroundImage(Background background) {
		if (background != null && background.getImage() != null && background.getCropWidth() > 0 && background.getCropHeight() > 0) {
			GL11.glDisable(2896);
			GL11.glDisable(2912);
			int pixelFormat = background.getImage().getFormat() == 99 ? 6407 : 6408;
			int imageWidth = background.getImage().getWidth();
			int imageHeight = background.getImage().getHeight();
			GL11.glMatrixMode(5889);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(5888);
			GL11.glLoadIdentity();
			float viewWidth = (float) this.viewportWidth;
			float viewHeight = (float) this.viewportHeight;
			float zoomX = viewWidth / (float) background.getCropWidth();
			float zoomY = viewHeight / (float) background.getCropHeight();
			float tileWidth = zoomX * (float) imageWidth;
			float tileHeight = zoomY * (float) imageHeight;
			float startX = -viewWidth * (float) background.getCropX() / (float) background.getCropWidth() - viewWidth / 2.0F;
			float startY = viewHeight * (float) background.getCropY() / (float) background.getCropHeight() + viewHeight / 2.0F;
			int tilesX = 1;
			int tilesY = 1;
			if (background.getImageModeX() == 33) {
				if ((startX %= tileWidth) > 0.0F) {
					startX -= tileWidth;
				}

				tilesX = (int) (2.5F + viewWidth / tileWidth);
				startX -= (float) (tilesX / 2) * tileWidth;
			}

			if (background.getImageModeY() == 33) {
				startY %= tileHeight;
				tilesY = (int) (2.5F + viewHeight / tileHeight);
				startY += (float) (tilesY / 2) * tileHeight;
			}

			GL11.glPixelStorei(3314, imageWidth);
			GL11.glPixelStorei(3315, 0);
			GL11.glPixelStorei(3316, 0);
			GL11.glDepthFunc(519);
			GL11.glDepthMask(false);
			GL11.glPixelZoom(zoomX, -zoomY);
			ByteBuffer pixels = memoryBuffers.getImageBuffer(background.getImage().getImageData());

			for (int tileY = 0; tileY < tilesY; ++tileY) {
				for (int tileX = 0; tileX < tilesX; ++tileX) {
					GL11.glRasterPos4f(0.0F, 0.0F, 0.0F, 1.0F);
					GL11.glBitmap(0, 0, 0.0F, 0.0F, startX + (float) tileX * tileWidth, startY - (float) tileY * tileHeight, pixels);
					GL11.glDrawPixels(imageWidth, imageHeight, pixelFormat, 5121, pixels);
				}
			}

			GL11.glPixelStorei(3314, 0);
		}

	}

	public final void render(Node node, Transform transform) {
		if (node == null) {
			throw new NullPointerException();
		} else if (!(node instanceof Sprite3D) && !(node instanceof Mesh) && !(node instanceof Group)) {
			throw new IllegalArgumentException();
		} else {
			renderPipe.pushRenderNode(node, transform == null ? new Transform() : transform);
			this.renderPushedNodes();
		}
	}

	private void renderPushedNodes() {
		renderPipe.sortNodes();

		for (int i = 0; i < renderPipe.getSize(); i++) {
			RenderObject ro = renderPipe.getRenderObj(i);

			if (ro.node instanceof Mesh) {
				Mesh mesh = (Mesh) ro.node;
				IndexBuffer indices = mesh.getIndexBuffer(ro.submeshIndex);
				Appearance ap = mesh.getAppearance(ro.submeshIndex);

				if (indices != null && ap != null) {
					VertexBuffer vb = MeshMorph.getViewInstance().getMorphedVertexBuffer(mesh);
					renderVertex(vb, indices, ap, ro.trans, mesh.getScope(), ro.alphaFactor);
				}
			} else {
				renderSprite((Sprite3D) ro.node, ro.trans, ro.alphaFactor);
			}
		}

		renderPipe.clear();
		MeshMorph.getViewInstance().clearCache();
	}

	private void renderVertex(VertexBuffer vertexBuffer, IndexBuffer indexBuffer, Appearance appearance, Transform transform, int scope, float alphaFactor) {
		if ((camera.getScope() & scope) != 0) {
			this.setupViewport();
			this.setupDepth();
			setupCamera();
			setupLights(lights, lightsTransforms, scope);
			if (transform != null) {
				Transform modelTransform;
				(modelTransform = new Transform()).set(transform);
				modelTransform.transpose();
				GL11.glMultMatrixf(memoryBuffers.getFloatBuffer(((Transform3D) modelTransform.getImpl()).m_matrix));
			}

			this.setupAppearance(appearance, false);
			this.draw(vertexBuffer, indexBuffer, appearance, alphaFactor);
		}
	}

	private void renderSprite(Sprite3D sprite, Transform transform, float alphaFactor) {
		Vector4f origin = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
		Vector4f unitX = new Vector4f(1.0F, 0.0F, 0.0F, 1.0F);
		Vector4f unitY = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
		Transform modelView;
		(modelView = new Transform(cameraTransform)).postMultiply(transform);
		Transform3D impl = (Transform3D) modelView.getImpl();
		impl.transform(origin);
		impl.transform(unitX);
		impl.transform(unitY);
		Vector4f center = new Vector4f(origin);
		origin.mul(1.0F / origin.w);
		unitX.mul(1.0F / unitX.w);
		unitY.mul(1.0F / unitY.w);
		unitX.sub(origin);
		unitY.sub(origin);
		Vector4f scaleX = new Vector4f(unitX.length(), 0.0F, 0.0F, 0.0F);
		Vector4f scaleY = new Vector4f(0.0F, unitY.length(), 0.0F, 0.0F);
		scaleX.add(center);
		scaleY.add(center);
		Transform projection = new Transform();
		camera.getProjection(projection);
		impl = (Transform3D) projection.getImpl();
		impl.transform(center);
		impl.transform(scaleX);
		impl.transform(scaleY);
		if (center.w > 0.0F && -center.w < center.z && center.z <= center.w) {
			center.mul(1.0F / center.w);
			scaleX.mul(1.0F / scaleX.w);
			scaleY.mul(1.0F / scaleY.w);
			scaleX.sub(center);
			scaleY.sub(center);
			boolean scaled = sprite.isScaled();
			int[] crop;
			boolean flipX = (crop = new int[]{sprite.getCropX(), sprite.getCropY(), sprite.getCropWidth(), sprite.getCropHeight()})[2] < 0;
			boolean flipY = crop[3] < 0;
			crop[2] = Math.abs(crop[2]);
			crop[3] = Math.abs(crop[3]);
			float zoomX = 1.0F;
			float zoomY = 1.0F;
			float rasterOffsetX = (float) ((flipX ? crop[2] : -crop[2]) / 2);
			float rasterOffsetY = (float) ((flipY ? -crop[3] : crop[3]) / 2);
			float spriteWidth;
			float spriteHeight;
			if (!scaled) {
				if (flipX) {
					zoomX = -1.0F;
				}

				if (flipY) {
					zoomY = -1.0F;
				}

				spriteWidth = (float) crop[2];
				spriteHeight = (float) crop[3];
			} else {
				zoomX = scaleX.length() * (float) this.viewportWidth * 0.5F;
				zoomY = scaleY.length() * (float) this.viewportHeight * 0.5F;
				spriteWidth = zoomX;
				spriteHeight = zoomY;
				rasterOffsetX = -zoomX / 2.0F;
				rasterOffsetY = zoomY / 2.0F;
				if (flipX) {
					rasterOffsetX += zoomX;
				}

				if (flipY) {
					rasterOffsetY -= zoomY;
				}

				zoomX /= flipX ? -((float) crop[2]) : (float) crop[2];
				zoomY /= flipY ? -((float) crop[3]) : (float) crop[3];
			}

			int[] clippedCrop = new int[4];
			if (G3DUtils.intersectRectangle(crop[0], crop[1], crop[2], crop[3], 0, 0, sprite.getImage().getWidth(), sprite.getImage().getHeight(), clippedCrop)) {
				float rasterOffset;
				label96:
				{
					if (!flipX) {
						rasterOffset = rasterOffsetX - zoomX * (float) (crop[0] - clippedCrop[0]);
					} else {
						if (crop[0] <= 0) {
							break label96;
						}

						rasterOffset = rasterOffsetX + zoomX * (float) (crop[0] - clippedCrop[0]);
					}

					rasterOffsetX = rasterOffset;
				}

				label90:
				{
					if (!flipY) {
						rasterOffset = rasterOffsetY + zoomY * (float) (crop[1] - clippedCrop[1]);
					} else {
						if (crop[1] <= 0) {
							break label90;
						}

						rasterOffset = rasterOffsetY - zoomY * (float) (crop[1] - clippedCrop[1]);
					}

					rasterOffsetY = rasterOffset;
				}

				ByteBuffer pixels;
				short pixelFormat;
				label84:
				{
					Transform viewportScale;
					(viewportScale = new Transform()).postScale((float) this.viewportWidth / ((float) this.viewportWidth + spriteWidth), (float) this.viewportHeight / ((float) this.viewportHeight + spriteHeight), 1.0F);
					viewportScale.postMultiply(projection);
					projection.set(viewportScale);
					int extendedX = (int) (0F - spriteWidth / 2.0F);
					int extendedY = (int) (0F - spriteHeight / 2.0F);
					int extendedWidth = (int) ((float) this.viewportWidth + spriteWidth);
					int extendedHeight = (int) ((float) this.viewportHeight + spriteHeight);
					projection.transpose();
					modelView.transpose();
					GL11.glViewport(extendedX, viewportHeight - extendedY - extendedHeight, extendedWidth, extendedHeight);
					GL11.glMatrixMode(5889);
					GL11.glLoadMatrixf(memoryBuffers.getFloatBuffer(((Transform3D) projection.getImpl()).m_matrix));
					GL11.glMatrixMode(5888);
					GL11.glLoadMatrixf(memoryBuffers.getFloatBuffer(((Transform3D) modelView.getImpl()).m_matrix));
					GL11.glDisable(2896);
					pixels = memoryBuffers.getImageBuffer(sprite.getImage().getImageData());
					GL11.glRasterPos4f(0.0F, 0.0F, 0.0F, 1.0F);
					GL11.glPixelStorei(3314, sprite.getImage().getWidth());
					GL11.glPixelStorei(3315, clippedCrop[1]);
					GL11.glPixelStorei(3316, clippedCrop[0]);
					GL11.glBitmap(0, 0, 0.0F, 0.0F, rasterOffsetX, rasterOffsetY, pixels);
					GL11.glPixelZoom(zoomX, -zoomY);
					pixelFormat = 6407;
					short format;
					switch (sprite.getImage().getFormat()) {
						case 96:
							format = 6406;
							break;
						case 97:
							format = 6409;
							break;
						case 98:
							format = 6410;
							break;
						case 99:
							format = 6407;
							break;
						case 100:
							format = 6408;
							break;
						default:
							break label84;
					}

					pixelFormat = format;
				}

				this.setupAppearance(sprite.getAppearance(), true);
				GL11.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (255 * alphaFactor));
				GL11.glDisableClientState(GL_COLOR_ARRAY);

				GL11.glDrawPixels(clippedCrop[2], clippedCrop[3], pixelFormat, 5121, pixels);
				GL11.glPixelStorei(3314, 0);
				GL11.glPixelStorei(3315, 0);
				GL11.glPixelStorei(3316, 0);
			}
		}
	}

	public final boolean init(Canvas canvas) {
		M3GView3D.canvas = canvas;

		try {
			GLCanvasUtil.makeCurrent(canvas);
			getCapabilities();
		} catch (Exception e) {
			e.printStackTrace();
			if (window == 0) {
				if (!glfwInit())
					return false;

				glfwDefaultWindowHints();
				glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
				glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

				window = glfwCreateWindow(400, 300, "M3GView", 0, 0);
				if (window == 0)
					return false;
			}

			glfwMakeContextCurrent(window);
			getCapabilities();

			SWTFrontend.getDisplay().syncExec(this);
		}
		hints();

		return true;
	}

	private void getCapabilities() {
		if (capabilities == null) {
			capabilities = GL.createCapabilities();
			return;
		}
		try {
			capabilities = GL.getCapabilities();
		} catch (Exception e) {
			capabilities = GL.createCapabilities();
		}
	}

	private void hints() {
		GL11.glEnable(GL_SCISSOR_TEST);
		GL11.glEnable(GL_NORMALIZE);
		GL11.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		GL11.glDisable(GL_POINT_SMOOTH);
		GL11.glDisable(GL_LINE_SMOOTH);
		GL11.glDisable(GL_POLYGON_SMOOTH);
		GL11.glEnable(GL_DITHER);
	}

	public final void setCurrent(int w, int h) throws Exception {
		if (viewportHeight != w || viewportHeight != h) {
			viewportWidth = w;
			viewportHeight = h;
			if (window != 0) {
				bufferImage = new ImageData(w, h, 32, Emulator3D.swtPalleteData);
				buffer = BufferUtils.createByteBuffer(w * h * 4);
				glfwSetWindowSize(window, w, h);
			}
		}
	}


	public void paintControl(PaintEvent p) {
		GC gc = p.gc;
		if (bufferImage != null) {
			Image img = new Image(null, bufferImage);
			gc.drawImage(img, 0, 0);
			img.dispose();
		}
	}

	public void swapBuffers() {
		GL11.glFinish();
		if (window != 0) {
			buffer.rewind();
			GL11.glReadPixels(0, 0, viewportWidth, viewportHeight, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			int rowBytes = bufferImage.width << 2;
			int offset = bufferImage.data.length - rowBytes;

			for (int i = bufferImage.height; i > 0; --i) {
				buffer.get(bufferImage.data, offset, rowBytes);
				offset -= rowBytes;
			}
		}
		SWTFrontend.getDisplay().syncExec(this);
	}

	public void run() {
		if (window != 0 && !paintListenerSet) {
			paintListenerSet = true;
			canvas.addPaintListener(this);
			return;
		}
		try {
			if (canvas.isDisposed()) return;
			if (window == 0) {
				GLCanvasUtil.swapBuffers(canvas);
			}
			canvas.redraw();
		} catch (Exception ignored) {}
	}

	public static void releaseContext() {
		if (window != 0) {
			glfwMakeContextCurrent(0);
			return;
		}
		try {
			GLCanvasUtil.releaseContext(canvas);
		} catch (Exception ignored) {}
	}

	public static void setCamera(Camera cam, Transform transform) {
		if (transform != null) {
			cameraTransform.set(transform);
			((Transform3D) cameraTransform.getImpl()).invert();
		} else {
			cameraTransform.setIdentity();
		}

		camera = cam;
	}

	public static int addLight(Light light, Transform transform) {
		if (light == null) {
			throw new NullPointerException();
		} else {
			lights.add(light);
			if (transform == null) {
				lightsTransforms.add(new Transform());
			} else {
				lightsTransforms.add(new Transform(transform));
			}

			return lights.size();
		}
	}

	public static void resetLights() {
		lights.clear();
		lightsTransforms.clear();
	}

	public final void collectWorldLights(World world) {
		resetLights();
		this.collectLights(world, world);
	}

	private void collectLights(World world, Group group) {
		Transform transform = new Transform();

		for (int i = 0; i < group.getChildCount(); ++i) {
			Node child;
			if ((child = group.getChild(i)) instanceof Light && child.getTransformTo(world, transform)) {
				lights.add(child);
				lightsTransforms.add(new Transform(transform));
			} else if (child instanceof Group) {
				this.collectLights(world, (Group) child);
			}
		}

	}

	private void setupAppearance(Appearance appearance, boolean isSprite) {
		if (!isSprite) {
			this.setupPolygonMode(appearance.getPolygonMode());
		}

		this.setupCompositingMode(appearance.getCompositingMode());
		if (!isSprite) {
			setupMaterial(appearance.getMaterial());
		}

		this.setupFog(appearance.getFog());
	}

	//Settings.xrayView -> xray
	private void setupPolygonMode(PolygonMode pm) {
		if (pm == null) {
			pm = new PolygonMode();
		}

		GL11.glPolygonMode(GL_FRONT_AND_BACK, xray ? GL_LINE : GL_FILL);

		int culling = pm.getCulling();
		if (culling == PolygonMode.CULL_NONE) {
			GL11.glDisable(GL_CULL_FACE);
		} else {
			GL11.glEnable(GL_CULL_FACE);
			GL11.glCullFace(culling == PolygonMode.CULL_FRONT ? GL_FRONT : GL_BACK);
		}

		GL11.glShadeModel(pm.getShading() == PolygonMode.SHADE_FLAT ? GL_FLAT : GL_SMOOTH);
		GL11.glFrontFace(pm.getWinding() == PolygonMode.WINDING_CW ? GL_CW : GL_CCW);
		GL11.glLightModelf(GL_LIGHT_MODEL_TWO_SIDE, pm.isTwoSidedLightingEnabled() ? 1.0F : 0.0F);
		GL11.glLightModelf(GL_LIGHT_MODEL_LOCAL_VIEWER, pm.isLocalCameraLightingEnabled() ? 1.0F : 0.0F);

		boolean persCorrect = pm.isPerspectiveCorrectionEnabled();
		if (AppSettings.m3gForcePerspectiveCorrection) persCorrect = true;

		GL11.glHint(GL_PERSPECTIVE_CORRECTION_HINT, persCorrect ? GL_NICEST : GL_FASTEST);
	}

	//Settings.xrayView -> xray
	//depthBufferEnabled = true
	private void setupCompositingMode(CompositingMode cm) {
		if (cm == null) {
			cm = new CompositingMode();
		}

		GL11.glEnable(GL_DEPTH_TEST);

		GL11.glDepthMask(cm.isDepthWriteEnabled());
		GL11.glDepthFunc(cm.isDepthTestEnabled() ? GL_LEQUAL : GL_ALWAYS);
		GL11.glColorMask(cm.isColorWriteEnabled(), cm.isColorWriteEnabled(), cm.isColorWriteEnabled(), cm.isAlphaWriteEnabled());

		GL11.glAlphaFunc(GL_GEQUAL, cm.getAlphaThreshold());
		if (cm.getAlphaThreshold() == 0.0F) {
			GL11.glDisable(GL_ALPHA_TEST);
		} else {
			GL11.glEnable(GL_ALPHA_TEST);
		}

		if (cm.getBlending() == CompositingMode.REPLACE) {
			GL11.glDisable(GL_BLEND);
		} else {
			GL11.glEnable(GL_BLEND);
		}

		switch (cm.getBlending()) {
			case CompositingMode.ALPHA:
				GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				break;
			case CompositingMode.ALPHA_ADD:
				GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
				break;
			case CompositingMode.MODULATE:
				GL11.glBlendFunc(GL_DST_COLOR, GL_ZERO);
				break;
			case CompositingMode.MODULATE_X2:
				GL11.glBlendFunc(GL_DST_COLOR, GL_SRC_COLOR);
				break;
			case CompositingMode.REPLACE:
				GL11.glBlendFunc(GL_ONE, GL_ZERO);
				break;
			default:
				break;
		}

		GL11.glPolygonOffset(cm.getDepthOffsetFactor(), cm.getDepthOffsetUnits());
		if (cm.getDepthOffsetFactor() == 0.0F && cm.getDepthOffsetUnits() == 0.0F) {
			GL11.glDisable(xray ? GL_POLYGON_OFFSET_LINE : GL_POLYGON_OFFSET_FILL);
		} else {
			GL11.glEnable(xray ? GL_POLYGON_OFFSET_LINE : GL_POLYGON_OFFSET_FILL);
		}
	}

	//LWJGLUtility.getFloatBuffer -> a.getFloatBuffer
	private void setupMaterial(Material mat) {
		if (mat != null) {
			GL11.glEnable(GL_LIGHTING);
			float[] tmpCol = new float[4];

			G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.AMBIENT));
			GL11.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, memoryBuffers.getFloatBuffer(tmpCol));

			G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.DIFFUSE));
			GL11.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, memoryBuffers.getFloatBuffer(tmpCol));

			G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.EMISSIVE));
			GL11.glMaterialfv(GL_FRONT_AND_BACK, GL_EMISSION, memoryBuffers.getFloatBuffer(tmpCol));

			G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.SPECULAR));
			GL11.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, memoryBuffers.getFloatBuffer(tmpCol));

			GL11.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, mat.getShininess());

			if (mat.isVertexColorTrackingEnabled()) {
				GL11.glEnable(GL_COLOR_MATERIAL);
				GL11.glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
			} else {
				GL11.glDisable(GL_COLOR_MATERIAL);
			}
		} else {
			GL11.glDisable(GL_LIGHTING);
		}
	}

	//Settings.xrayView -> xray
	//LWJGLUtility.getFloatBuffer -> a.method401
	private void setupFog(Fog fog) {
		if (fog != null && !xray) {
			GL11.glEnable(GL_FOG);
			GL11.glFogi(GL_FOG_MODE, fog.getMode() == Fog.LINEAR ? GL_LINEAR : GL_EXP);

			float[] fogColor = new float[4];
			G3DUtils.fillFloatColor(fogColor, fog.getColor());
			fogColor[3] = 1.0F;
			GL11.glFogfv(GL_FOG_COLOR, memoryBuffers.getFloatBuffer(fogColor));

			GL11.glFogf(GL_FOG_START, fog.getNearDistance());
			GL11.glFogf(GL_FOG_END, fog.getFarDistance());
			GL11.glFogf(GL_FOG_DENSITY, fog.getDensity());
		} else {
			GL11.glDisable(GL_FOG);
		}
	}

	private void draw(VertexBuffer vertexBuffer, IndexBuffer indexBuffer, Appearance appearance, float alphaFactor) {
		VertexArray colors = vertexBuffer.getColors();
		if (colors == null) {
			int col = vertexBuffer.getDefaultColor();
			GL11.glColor4ub((byte) (col >> 16 & 255), (byte) (col >> 8 & 255), (byte) (col & 255), (byte) ((int) ((float) (col >> 24 & 255) * alphaFactor)));
			GL11.glDisableClientState(GL_COLOR_ARRAY);
		} else {
			GL11.glEnableClientState(GL_COLOR_ARRAY);
			if (colors.getComponentType() == 1) {
				byte[] colorsBArr = colors.getByteValues();
				GL11.glColorPointer(alphaFactor == 1.0F ? colors.getComponentCount() : 4, GL_UNSIGNED_BYTE, 0, memoryBuffers.getColorBuffer(colorsBArr, alphaFactor, colors.getVertexCount()));
			}
		}

		VertexArray normals = vertexBuffer.getNormals();
		if (normals != null && appearance.getMaterial() != null) {
			GL11.glEnableClientState(GL_NORMAL_ARRAY);
			glEnable(GL_NORMALIZE);
			if (normals.getComponentType() == 1) {
				GL11.glNormalPointer(GL_BYTE, 0, memoryBuffers.getNormalBuffer(normals.getByteValues()));
			} else {
				GL11.glNormalPointer(GL_SHORT, 0, memoryBuffers.getNormalBuffer(normals.getShortValues()));
			}
		} else {
			GL11.glDisableClientState(GL_NORMAL_ARRAY);
		}

		float[] scaleBias = new float[4];
		VertexArray positions = vertexBuffer.getPositions(scaleBias);
		GL11.glEnableClientState(GL_VERTEX_ARRAY);
		if (positions.getComponentType() == 1) {
			byte[] posesBArr = positions.getByteValues();
			GL11.glVertexPointer(positions.getComponentCount(), GL_SHORT, 0, memoryBuffers.getVertexBuffer(posesBArr));
		} else {
			short[] posesSArr = positions.getShortValues();
			GL11.glVertexPointer(positions.getComponentCount(), GL_SHORT, 0, memoryBuffers.getVertexBuffer(posesSArr));
		}

		GL11.glMatrixMode(GL_MODELVIEW);
		GL11.glTranslatef(scaleBias[1], scaleBias[2], scaleBias[3]);
		GL11.glScalef(scaleBias[0], scaleBias[0], scaleBias[0]);

		TriangleStripArray triangleStripArray = (TriangleStripArray) indexBuffer;
		int stripCount = triangleStripArray.getStripCount();

		if (appearance != null && !this.xray) {
			IntBuffer textureIds = BufferUtils.createIntBuffer(Emulator3D.NumTextureUnits);
			GL11.glGenTextures(textureIds);

			for (int i = 0; i < Emulator3D.NumTextureUnits; ++i) {
				Texture2D texture2D = appearance.getTexture(i);
				VertexArray texCoords = vertexBuffer.getTexCoords(i, scaleBias);

				if (texture2D == null || texCoords == null) continue;

				Image2D image2D = texture2D.getImage();
				scaleBias[3] = 0.0F;
				if (!useGL11()) {
					GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
					GL13.glClientActiveTexture(GL13.GL_TEXTURE0 + i);
				}

				GL11.glEnable(GL_TEXTURE_2D);
				GL11.glBindTexture(GL_TEXTURE_2D, textureIds.get(i));

				int blendMode = 0;
				switch (texture2D.getBlending()) {
					case Texture2D.FUNC_ADD:
						blendMode = GL_ADD;
						break;
					case Texture2D.FUNC_BLEND:
						blendMode = GL_BLEND;
						break;
					case Texture2D.FUNC_DECAL:
						blendMode = GL_DECAL;
						break;
					case Texture2D.FUNC_MODULATE:
						blendMode = GL_MODULATE;
						break;
					case Texture2D.FUNC_REPLACE:
						blendMode = GL_REPLACE;
						break;
					default:
						break;
				}

				GL11.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, blendMode);

				float[] blendColor = new float[4];
				G3DUtils.fillFloatColor(blendColor, texture2D.getBlendColor());
				blendColor[3] = 1.0F;
				GL11.glTexEnvfv(GL_TEXTURE_ENV, GL_TEXTURE_ENV_COLOR, memoryBuffers.getFloatBuffer(blendColor));

				short texFormat = GL_RGB;
				switch (image2D.getFormat()) {
					case Image2D.ALPHA:
						texFormat = GL_ALPHA;
						break;
					case Image2D.LUMINANCE:
						texFormat = GL_LUMINANCE;
						break;
					case Image2D.LUMINANCE_ALPHA:
						texFormat = GL_LUMINANCE_ALPHA;
						break;
					case Image2D.RGB:
						texFormat = GL_RGB;
						break;
					case Image2D.RGBA:
						texFormat = GL_RGBA;
				}

                /*if (!useGL11() && capabilities.OpenGL14)
                    GL11.glTexParameteri(GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL_TRUE);*/

				GL11.glTexImage2D(GL_TEXTURE_2D, 0,
						texFormat, image2D.getWidth(), image2D.getHeight(), 0,
						texFormat, GL_UNSIGNED_BYTE,
						memoryBuffers.getImageBuffer(image2D.getImageData())
				);

				GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
						texture2D.getWrappingS() == Texture2D.WRAP_CLAMP && !useGL11() ? GL_CLAMP_TO_EDGE : GL_REPEAT
				);
				GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
						texture2D.getWrappingT() == Texture2D.WRAP_CLAMP && !useGL11() ? GL_CLAMP_TO_EDGE : GL_REPEAT
				);

				int levelFilter = texture2D.getLevelFilter();
				int imageFilter = texture2D.getImageFilter();

				if (useGL11() || AppSettings.m3gMipmapping == AppSettings.MIP_OFF || true) {
					levelFilter = Texture2D.FILTER_BASE_LEVEL;
					if (!useGL11()) glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 1);
				} else if (AppSettings.m3gMipmapping == AppSettings.MIP_LINEAR) {
					levelFilter = Texture2D.FILTER_NEAREST;
					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 1);
				} else if (AppSettings.m3gMipmapping == AppSettings.MIP_TRILINEAR) {
					levelFilter = Texture2D.FILTER_LINEAR;
					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 1);
				} else if (AppSettings.m3gMipmapping >= AppSettings.MIP_ANISO_2) {
					levelFilter = Texture2D.FILTER_LINEAR;
					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 2 << (AppSettings.m3gMipmapping - AppSettings.MIP_ANISO_2));
				}

				if (AppSettings.m3gTexFilter == AppSettings.TEX_FILTER_NEAREST) {
					imageFilter = Texture2D.FILTER_NEAREST;
				} else if (AppSettings.m3gTexFilter == AppSettings.TEX_FILTER_LINEAR) {
					imageFilter = Texture2D.FILTER_LINEAR;
				}

				int magFilter = 0, minFilter = 0;

				if (imageFilter == Texture2D.FILTER_NEAREST) {
					minFilter = magFilter = GL_NEAREST;

					if (levelFilter == Texture2D.FILTER_NEAREST) minFilter = GL_NEAREST_MIPMAP_NEAREST;
					else if (levelFilter == Texture2D.FILTER_LINEAR) minFilter = GL_NEAREST_MIPMAP_LINEAR;
				} else if (imageFilter == Texture2D.FILTER_LINEAR) {
					minFilter = magFilter = GL_LINEAR;

					if (levelFilter == Texture2D.FILTER_NEAREST) minFilter = GL_LINEAR_MIPMAP_NEAREST;
					else if (levelFilter == Texture2D.FILTER_LINEAR) minFilter = GL_LINEAR_MIPMAP_LINEAR;
				}

				GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
				GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
				GL11.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

				ShortBuffer texCoordBuffer;
				if (texCoords.getComponentType() == 1) {
					texCoordBuffer = memoryBuffers.getTexCoordBuffer(texCoords.getByteValues(), i);
				} else {
					texCoordBuffer = memoryBuffers.getTexCoordBuffer(texCoords.getShortValues(), i);
				}
				GL11.glTexCoordPointer(texCoords.getComponentCount(), GL_SHORT, 0, texCoordBuffer);

				Transform tmpMat = new Transform();
				texture2D.getCompositeTransform(tmpMat);
				tmpMat.transpose();

				GL11.glMatrixMode(GL_TEXTURE);
				GL11.glLoadMatrixf(memoryBuffers.getFloatBuffer(((Transform3D) tmpMat.getImpl()).m_matrix));
				GL11.glTranslatef(scaleBias[1], scaleBias[2], scaleBias[3]);
				GL11.glScalef(scaleBias[0], scaleBias[0], scaleBias[0]);
			}

			for (int i = 0; i < stripCount; ++i) {
				int[] indexStrip = triangleStripArray.getIndexStrip(i);
				GL11.glDrawElements(GL_TRIANGLE_STRIP, memoryBuffers.getElementsBuffer(indexStrip));
			}

			if (!useGL11()) {
				for (int i = 0; i < Emulator3D.NumTextureUnits; ++i) {
					if (GL11.glIsTexture(textureIds.get(i))) {
						GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
						GL13.glClientActiveTexture(GL13.GL_TEXTURE0 + i);
						GL11.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
						GL11.glDisable(GL_TEXTURE_2D);
					}
				}
			}

			GL11.glDeleteTextures(textureIds);
		} else {
			//xray
			for (int i = 0; i < stripCount; ++i) {
				int[] indexStrip = triangleStripArray.getIndexStrip(i);
				GL11.glDrawElements(GL_TRIANGLE_STRIP, memoryBuffers.getElementsBuffer(indexStrip));
			}
		}
		int err = GL11.glGetError();
		if (err != GL11.GL_NO_ERROR) {
			Emulator.getEmulator().getLogStream().println("M3GView GL Error: " + err);
		}
	}

	private static boolean useGL11() {
		return !capabilities.OpenGL12;
//        return useSoftwareWgl;
	}

	//CameraCache.camera -> camera
	//LWJGLUtility.getFloatBuffer -> a.method401
	private void setupCamera() {
		if (camera != null) {
			Transform tmpMat = new Transform();

			camera.getProjection(tmpMat);
			tmpMat.transpose();
			GL11.glMatrixMode(GL_PROJECTION);
			GL11.glLoadMatrixf(memoryBuffers.getFloatBuffer(((Transform3D) tmpMat.getImpl()).m_matrix));

			tmpMat.set(cameraTransform);
			tmpMat.transpose();
			GL11.glMatrixMode(GL_MODELVIEW);
			GL11.glLoadMatrixf(memoryBuffers.getFloatBuffer(((Transform3D) tmpMat.getImpl()).m_matrix));
		}
	}

	//LWJGLUtility.getFloatBuffer -> a.method401
	private void setupLights(Vector lights, Vector lightMats, int scope) {
		for (int i = 0; i < Emulator3D.MaxLights; ++i) {
			GL11.glDisable(GL_LIGHT0 + i);
		}

		if (!useGL11() && capabilities.GL_ARB_color_buffer_float) {
			ARBColorBufferFloat.glClampColorARB(
					ARBColorBufferFloat.GL_CLAMP_VERTEX_COLOR_ARB,
					AppSettings.m3gDisableLightClamp ? GL_FALSE : GL_TRUE
			);
		}

		int usedLights = 0;
		Transform tmpMat = new Transform();

		for (int i = 0; i < lights.size() && usedLights < Emulator3D.MaxLights; ++i) {
			Light light = (Light) lights.get(i);

			if (light == null || (light.getScope() & scope) == 0 || !renderPipe.isVisible(light)) {
				continue;
			}

			Transform lightMat = (Transform) lightMats.get(i);

			if (lightMat != null) {
				tmpMat.set(lightMat);
			} else {
				tmpMat.setIdentity();
			}
			tmpMat.transpose();

			GL11.glPushMatrix();
			GL11.glMatrixMode(GL_MODELVIEW);
			GL11.glMultMatrixf(memoryBuffers.getFloatBuffer(((Transform3D) tmpMat.getImpl()).m_matrix));

			int lightId = GL_LIGHT0 + usedLights;
			usedLights++;

			float[] lightColor = new float[]{0, 0, 0, 1}; //rgba

			//Set default light preferences?
			GL11.glLightfv(lightId, GL_AMBIENT, memoryBuffers.getFloatBuffer(lightColor));
			GL11.glLightfv(lightId, GL_DIFFUSE, memoryBuffers.getFloatBuffer(lightColor));
			GL11.glLightfv(lightId, GL_SPECULAR, memoryBuffers.getFloatBuffer(lightColor));

			GL11.glLightf(lightId, GL_CONSTANT_ATTENUATION, 1.0F);
			GL11.glLightf(lightId, GL_LINEAR_ATTENUATION, 0.0F);
			GL11.glLightf(lightId, GL_QUADRATIC_ATTENUATION, 0.0F);
			GL11.glLightf(lightId, GL_SPOT_CUTOFF, 180.0F);
			GL11.glLightf(lightId, GL_SPOT_EXPONENT, 0.0F);

			float[] tmpLightPos;
			if (light.getMode() == Light.DIRECTIONAL) {
				tmpLightPos = LightsCache.POSITIVE_Z_AXIS; //light direction!
			} else {
				tmpLightPos = LightsCache.LOCAL_ORIGIN;
			}

			GL11.glLightfv(lightId, GL_POSITION, memoryBuffers.getFloatBuffer(tmpLightPos));

			G3DUtils.fillFloatColor(lightColor, light.getColor());
			float lightIntensity = light.getIntensity();
			lightColor[0] *= lightIntensity;
			lightColor[1] *= lightIntensity;
			lightColor[2] *= lightIntensity;
			lightColor[3] = 1.0F;

			int lightMode = light.getMode();

			if (lightMode == Light.AMBIENT) {
				GL11.glLightfv(lightId, GL_AMBIENT, memoryBuffers.getFloatBuffer(lightColor));
			} else {
				GL11.glLightfv(lightId, GL_DIFFUSE, memoryBuffers.getFloatBuffer(lightColor));
				GL11.glLightfv(lightId, GL_SPECULAR, memoryBuffers.getFloatBuffer(lightColor));
			}

			if (lightMode == Light.SPOT) {
				GL11.glLightfv(lightId, GL_SPOT_DIRECTION, memoryBuffers.getFloatBuffer(LightsCache.NEGATIVE_Z_AXIS));
				GL11.glLightf(lightId, GL_SPOT_CUTOFF, light.getSpotAngle());
				GL11.glLightf(lightId, GL_SPOT_EXPONENT, light.getSpotExponent());
			}

			if (lightMode == Light.SPOT || lightMode == Light.OMNI) {
				GL11.glLightf(lightId, GL_CONSTANT_ATTENUATION, light.getConstantAttenuation());
				GL11.glLightf(lightId, GL_LINEAR_ATTENUATION, light.getLinearAttenuation());
				GL11.glLightf(lightId, GL_QUADRATIC_ATTENUATION, light.getQuadraticAttenuation());
			}

			GL11.glEnable(lightId);
			GL11.glPopMatrix();
		}
	}

	public final void drawGrid(float cellSize) {
		this.setupViewport();
		this.setupDepth();
		setupCamera();
		GL11.glPolygonMode(1032, 6914);
		GL11.glDisable(2884);
		GL11.glShadeModel(7425);
		GL11.glFrontFace(2305);
		GL11.glEnable(2929);
		GL11.glDepthFunc(519);
		GL11.glDisable(3008);
		GL11.glDisable(3042);
		GL11.glDisable('\u8037');
		GL11.glDisable(2896);
		GL11.glDisable(2912);
		GL11.glColor4ub((byte) 70, (byte) 121, (byte) -80, (byte) -1);
		GL11.glDisableClientState('\u8076');
		GL11.glDisableClientState('\u8075');
		float halfExtent = cellSize * 5.0F;
		boolean filled = true;
		GL11.glMatrixMode(5888);
		GL11.glBegin(7);
		float next = -halfExtent;

		while (true) {
			float x = next;
			if (next >= halfExtent) {
				GL11.glEnd();
				return;
			}

			next = -halfExtent;

			while (true) {
				float z = next;
				if (next >= halfExtent) {
					filled = !filled;
					next = x + cellSize;
					break;
				}

				if (filled) {
					GL11.glVertex3f(x, 0.0F, z);
					GL11.glVertex3f(x + cellSize, 0.0F, z);
					GL11.glVertex3f(x + cellSize, 0.0F, z + cellSize);
					GL11.glVertex3f(x, 0.0F, z + cellSize);
				}

				filled = !filled;
				next = z + cellSize;
			}
		}
	}

	public final void drawAxis() {
		this.setupViewport();
		this.setupDepth();
		setupCamera();
		GL11.glPolygonMode(1032, 6914);
		GL11.glDisable(2884);
		GL11.glShadeModel(7425);
		GL11.glFrontFace(2305);
		GL11.glEnable(2929);
		GL11.glDepthFunc(519);
		GL11.glDisable(3008);
		GL11.glDisable(3042);
		GL11.glDisable('\u8037');
		GL11.glDisable(2896);
		GL11.glDisable(2912);
		GL11.glDisableClientState('\u8076');
		GL11.glDisableClientState('\u8075');
		GL11.glMatrixMode(5888);
		GL11.glColor4ub((byte) -1, (byte) 0, (byte) 0, (byte) -1);
		GL11.glBegin(1);
		GL11.glVertex3f(0.0F, 0.0F, 0.0F);
		GL11.glVertex3f(1.0F, 0.0F, 0.0F);
		GL11.glEnd();
		GL11.glBegin(6);
		GL11.glVertex3f(1.3F, 0.0F, 0.0F);
		GL11.glVertex3f(1.0F, 0.1F, 0.1F);
		GL11.glVertex3f(1.0F, -0.1F, 0.1F);
		GL11.glVertex3f(1.0F, -0.1F, -0.1F);
		GL11.glVertex3f(1.0F, 0.1F, -0.1F);
		GL11.glVertex3f(1.0F, 0.1F, 0.1F);
		GL11.glEnd();
		GL11.glColor4ub((byte) 0, (byte) -1, (byte) 0, (byte) -1);
		GL11.glBegin(1);
		GL11.glVertex3f(0.0F, 0.0F, 0.0F);
		GL11.glVertex3f(0.0F, 1.0F, 0.0F);
		GL11.glEnd();
		GL11.glBegin(6);
		GL11.glVertex3f(0.0F, 1.3F, 0.0F);
		GL11.glVertex3f(0.1F, 1.0F, 0.1F);
		GL11.glVertex3f(-0.1F, 1.0F, 0.1F);
		GL11.glVertex3f(-0.1F, 1.0F, -0.1F);
		GL11.glVertex3f(0.1F, 1.0F, -0.1F);
		GL11.glVertex3f(0.1F, 1.0F, 0.1F);
		GL11.glEnd();
		GL11.glColor4ub((byte) 0, (byte) 0, (byte) -1, (byte) -1);
		GL11.glBegin(1);
		GL11.glVertex3f(0.0F, 0.0F, 0.0F);
		GL11.glVertex3f(0.0F, 0.0F, 1.0F);
		GL11.glEnd();
		GL11.glBegin(6);
		GL11.glVertex3f(0.0F, 0.0F, 1.3F);
		GL11.glVertex3f(0.1F, 0.1F, 1.0F);
		GL11.glVertex3f(-0.1F, 0.1F, 1.0F);
		GL11.glVertex3f(-0.1F, -0.1F, 1.0F);
		GL11.glVertex3f(0.1F, -0.1F, 1.0F);
		GL11.glVertex3f(0.1F, 0.1F, 1.0F);
		GL11.glEnd();
	}
}
