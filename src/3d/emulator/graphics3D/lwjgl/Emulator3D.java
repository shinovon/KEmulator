package emulator.graphics3D.lwjgl;

import emulator.graphics3D.*;

import java.awt.*;
import java.util.*;

import emulator.*;
import emulator.graphics3D.m3g.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;

import emulator.graphics2D.swt.Graphics2DSWT;

import java.awt.image.*;
import java.nio.*;
import javax.microedition.lcdui.Graphics;
import javax.microedition.m3g.*;
import javax.swing.JFrame;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public final class Emulator3D implements IGraphics3D {

	private static Emulator3D instance;
	private final LWJGLUtility memoryBuffers;
	private final RenderPipe renderPipe;
	private Object target;
	private boolean depthBufferEnabled;
	private int hints;
	private static int targetWidth;
	private static int targetHeight;
	private static Pbuffer pbufferContext;
	private static ByteBuffer buffer;
	private static BufferedImage awtBufferImage;
	private static ImageData swtBufferImage;
	public static final PaletteData swtPalleteData = new PaletteData(-16777216, 16711680, '\uff00');
	private static final Hashtable properties = new Hashtable();
	private static PixelFormat pixelFormat;
	private long wglContextHandle;
	private Image swtImage;
	private GC swtGC;
	private float depthRangeNear;
	private float depthRangeFar;
	private int viewportX;
	private int viewportY;
	private int viewportWidth;
	private int viewportHeight;

	private static final Vector<Integer> usedGLTextures = new Vector();
	private static final Vector<Integer> unusedGLTextures = new Vector();

	public static final int MaxViewportWidth = 2048;
	public static final int MaxViewportHeight = 2048;
	public static final int NumTextureUnits = 10;
	public static final int MaxTextureDimension = 1024;
	public static final int MaxSpriteCropDimension = 1024;
	public static final int MaxLights = 8;
	public static final int MaxTransformsPerVertex = 4;
	private boolean exiting;
	private String contextRes;
	private final Map<Integer, Image2D> texturesTable = new WeakHashMap<Integer, Image2D>();
	private static boolean useDisplay;
	private static boolean useWgl;

	private Canvas tmpCanvas;

	private Emulator3D() {
		instance = this;
		properties.put("supportAntialiasing", Boolean.TRUE);
		properties.put("supportTrueColor", Boolean.TRUE);
		properties.put("supportDithering", Boolean.TRUE);
		properties.put("supportMipmapping", Boolean.TRUE);
		properties.put("supportPerspectiveCorrection", Boolean.TRUE);
		properties.put("supportLocalCameraLighting", Boolean.TRUE);
		properties.put("maxLights", new Integer(MaxLights));
		properties.put("maxViewportWidth", new Integer(MaxViewportWidth));
		properties.put("maxViewportHeight", new Integer(MaxViewportHeight));
		properties.put("maxViewportDimension", new Integer(2048));
		properties.put("maxTextureDimension", new Integer(MaxTextureDimension));
		properties.put("maxSpriteCropDimension", new Integer(MaxSpriteCropDimension));
		properties.put("maxTransformsPerVertex", new Integer(MaxTransformsPerVertex));
		properties.put("numTextureUnits", new Integer(NumTextureUnits));
		properties.put("coreID", "@KEmulator LWJ-OpenGL-M3G @liang.wu");

		memoryBuffers = new LWJGLUtility();
		renderPipe = new RenderPipe();
	}

	public static Emulator3D getInstance() {
		if (instance == null) {
			instance = new Emulator3D();
		}

		return instance;
	}

	public static PixelFormat pixelFormat() {
		if (pixelFormat == null) {
			int samples = 4;
			int bpp = Emulator.getEmulator().getScreenDepth();

			while (true) {
				try {
					pixelFormat = new PixelFormat(bpp, 0, 24, 0, samples);
					new Pbuffer(1, 1, pixelFormat, null, null).destroy();
					break;
				} catch (Exception e) {
					if ((samples >>= 1) == 0) {
						try {
							pixelFormat = new PixelFormat(bpp, 0, 24, 0, 0);
							new Pbuffer(1, 1, pixelFormat, null, null).destroy();
						} catch (Exception e2) {
							pixelFormat = new PixelFormat();
						}
						break;
					}
				}
			}
		}

		return pixelFormat;
	}

	public synchronized final void bindTarget(Object target) {
		if (exiting) {
			throw new IllegalStateException("exiting");
		}
		int w;
		int h;

		if (target instanceof Graphics) {
			this.target = target;
			w = ((Graphics) this.target).getImage().getWidth();
			h = ((Graphics) this.target).getImage().getHeight();
		} else {
			if (!(target instanceof Image2D)) {
				throw new IllegalArgumentException();
			}

			this.target = target;
			w = ((Image2D) this.target).getWidth();
			h = ((Image2D) this.target).getHeight();
		}

		try {
			String s = w + "x" + h;
			if (useDisplay) {
				if (contextRes != null && !contextRes.equals(s)) {
					tmpCanvas.setBounds(0, 0, w, h);
				}
				contextRes = s;
				Display.makeCurrent();
			} else if (useWgl) {
				bindWgl(w, h);
			} else {
				try {
					if (contextRes != null && !contextRes.equals(s)) {
						pbufferContext.makeCurrent();
						releaseTextures();
						pbufferContext.destroy();
						pbufferContext = null;
					}
					if (pbufferContext == null) {
						pbufferContext = new Pbuffer(w, h, pixelFormat(), null);
						contextRes = s;
					}

					pbufferContext.makeCurrent();
				} catch (Exception e) {
					e.printStackTrace();
					try {
						// мега костыль
						JFrame tmpFrame = new JFrame();
						tmpCanvas = new Canvas();
						tmpCanvas.setBounds(0, 0, w, h);
						tmpFrame.add(tmpCanvas);
						tmpFrame.pack();
						tmpFrame.setVisible(true);

						Display.setParent(tmpCanvas);
						tmpFrame.setVisible(false);
						Display.create(pixelFormat());

						useDisplay = true;
					} catch (Exception e2) {
						e2.printStackTrace();
						if (Emulator.isX64()) throw e2;
						bindWgl(w, h);
					}
				}
			}

			if (targetWidth != w || targetHeight != h) {
				if (Settings.g2d == 1) {
					awtBufferImage = new BufferedImage(w, h, 4);
				} else {
					swtBufferImage = new ImageData(w, h, 32, swtPalleteData);
				}
				buffer = BufferUtils.createByteBuffer(w * h * 4);
				targetWidth = w;
				targetHeight = h;
			}

			GL11.glEnable(GL_SCISSOR_TEST);
			GL11.glEnable(GL_NORMALIZE);
			GL11.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		} catch (Exception e) {
			e.printStackTrace();
			this.target = null;
			throw new IllegalArgumentException();
		}
	}

	public synchronized void releaseTarget() {
		GL11.glFinish();
		this.swapBuffers();

		while (!unusedGLTextures.isEmpty())
			releaseTexture(unusedGLTextures.get(0));

		if (exiting) {
			while (!usedGLTextures.isEmpty())
				releaseTexture(usedGLTextures.get(0));
			return;
		}

		this.target = null;
		if (pbufferContext != null) {
			try {
				pbufferContext.releaseContext();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.releaseContext();
		}
	}

	public static boolean useGL11() {
		return useWgl;
	}

	private void bindWgl(int var1, int var2) throws Exception {
		if (this.swtImage == null || this.swtImage.getBounds().width != var1 || this.swtImage.getBounds().height != var2) {
			if (this.swtImage != null) {
				this.swtImage.dispose();
				this.swtGC.dispose();
				Emulator.getPlatform().deleteGLContext(wglContextHandle);
			}

			ImageData var3 = new ImageData(var1, var2, 32, swtPalleteData);
			this.swtImage = new Image(null, var3);
			this.swtGC = new GC(this.swtImage);
			this.wglContextHandle = Emulator.getPlatform().createGLContext(swtGC.handle, false);
			if (this.wglContextHandle == 0) {
				this.swtGC.dispose();
				this.swtImage.dispose();
				throw new IllegalArgumentException();
			}
		}

		if (!Emulator.getPlatform().isGLContextCurrent(swtImage.handle)) {
			Emulator.getPlatform().setGLContextCurrent(this.swtGC.handle, this.wglContextHandle);

			try {
				GLContext.useContext(this.swtImage);
			} catch (Exception var6) {
				throw new IllegalArgumentException();
			}
		}
		useWgl = true;
	}

	private void releaseContext() {
		try {
			Emulator.getPlatform().releaseGLContext(this.swtGC.handle);
		} catch (Throwable ignored) {}
		if (useDisplay) {
			try {
				Display.releaseContext();
			} catch (Exception ignored) {}
			return;
		}
		try {
			GLContext.useContext(null);
		} catch (Exception ignored) {}
	}

	public void swapBuffers() {
		swapBuffers(false, 0, 0, targetWidth, targetHeight);
	}

	public final void swapBuffers(boolean flip, int x, int y, int width, int height) {
		if (this.target != null) {
			if (this.target instanceof Image2D) {
				Image2D var9 = (Image2D) this.target;
				buffer.rewind();
				GL11.glReadPixels(x, y, width, height, 6408, 5121, buffer);
				byte[] var11 = new byte[width * height * 4];
				int w = width << 2;
				int off = var11.length - w;

				for (int i = height; i > 0; --i) {
					buffer.get(var11, off, w);
					off -= w;
				}

				if (var9.getFormat() == 100) {
					var9.set(x, y, var9.getWidth(), var9.getHeight(), var11);
				} else {
					byte[] data = new byte[var9.getWidth() * var9.getHeight() * 3];
					int var6 = var11.length - 1;

					for (int var7 = data.length - 1; var7 >= 0; data[var7--] = var11[var6--]) {
						--var6;
						data[var7--] = var11[var6--];
						data[var7--] = var11[var6--];
					}

					var9.set(x, y, var9.getWidth(), var9.getHeight(), data);
				}
			} else if (Settings.g2d == 0) {
				buffer.rewind();
				GL11.glReadPixels(x, y, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
				int w = swtBufferImage.width << 2;
				int off = swtBufferImage.data.length - w;

				for (int i = swtBufferImage.height; i > 0; --i) {
					buffer.get(swtBufferImage.data, off, w);
					off -= w;
				}

				Image var12 = new Image(null, swtBufferImage);
				((Graphics2DSWT) ((Graphics) this.target).getImpl()).gc().drawImage(var12, 0, 0);
				var12.dispose();
			} else {
				buffer.rewind();
				GL11.glReadPixels(x, y, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
				int[] data = ((DataBufferInt) awtBufferImage.getRaster().getDataBuffer()).getData();
				IntBuffer ib = buffer.asIntBuffer();
				int off;

				if (flip) {
					off = 0;
					for (int i = height; i > 0; --i) {
						ib.get(data, off, width);
						off += width;
					}
				} else {
					off = data.length - width;
					for (int i = height; i > 0; --i) {
						ib.get(data, off, width);
						off -= width;
					}
				}

				((emulator.graphics2D.awt.b) ((Graphics) this.target).getImpl()).g().drawImage(awtBufferImage, x, y, null);
			}
		}
	}

	public final void enableDepthBuffer(boolean enabled) {
		depthBufferEnabled = enabled;
	}

	public final boolean isDepthBufferEnabled() {
		return depthBufferEnabled;
	}

	public final void setHints(int hints) {
		this.hints = hints;

		if (target != null) {
			setHintsInternal();
		}

	}

	private void setHintsInternal() {
		boolean aa = (hints & Graphics3D.ANTIALIAS) != 0;

		if (Settings.m3gAA == Settings.AA_OFF) aa = false;
		else if (Settings.m3gAA == Settings.AA_ON) aa = true;

		if (aa) {
			GL11.glEnable(GL_POINT_SMOOTH);
			GL11.glEnable(GL_LINE_SMOOTH);
			GL11.glEnable(GL_POLYGON_SMOOTH);
			if (!useGL11())
				GL11.glEnable(GL_MULTISAMPLE);
		} else {
			GL11.glDisable(GL_POINT_SMOOTH);
			GL11.glDisable(GL_LINE_SMOOTH);
			GL11.glDisable(GL_POLYGON_SMOOTH);
			if (!useGL11())
				GL11.glDisable(GL_MULTISAMPLE);
		}

		if ((hints & Graphics3D.DITHER) != 0) {
			GL11.glEnable(GL_DITHER);
		} else {
			GL11.glDisable(GL_DITHER);
		}
	}

	public final int getHints() {
		return hints;
	}

	public final Hashtable getProperties() {
		return properties;
	}

	public final void setDepthRange(float near, float far) {
		depthRangeNear = near;
		depthRangeFar = far;
	}

	private void setupDepth() {
		GL11.glDepthRange(depthRangeNear, depthRangeFar);
	}

	public final void setViewport(int x, int y, int w, int h) {
		viewportX = x;
		viewportY = y;
		viewportWidth = w;
		viewportHeight = h;
	}

	private void setupViewport() {
		GL11.glViewport(viewportX, targetHeight - viewportY - viewportHeight, viewportWidth, viewportHeight);
		GL11.glScissor(viewportX, targetHeight - viewportY - viewportHeight, viewportWidth, viewportHeight);
	}

	public final void clearBackgound(Object bgObj) {
		Background bg = (Background) bgObj;

		setupViewport();
		setupDepth();

		GL11.glClearDepth(1);
		GL11.glDepthMask(true);
		GL11.glColorMask(true, true, true, true);

		int bgColor = bg != null && !Settings.xrayView ? bg.getColor() : 0;
		GL11.glClearColor(
				G3DUtils.getFloatColor(bgColor, 16),
				G3DUtils.getFloatColor(bgColor, 8),
				G3DUtils.getFloatColor(bgColor, 0),
				G3DUtils.getFloatColor(bgColor, 24)
		);

		if (bg != null && !Settings.xrayView) {
			int colorClear = bg.isColorClearEnabled() ? GL_COLOR_BUFFER_BIT : 0;
			int depthClear = depthBufferEnabled && bg.isDepthClearEnabled() ? GL_DEPTH_BUFFER_BIT : 0;
			GL11.glClear(colorClear | depthClear);

			drawBackgroundImage(bg);
		} else {
			GL11.glClear(GL_COLOR_BUFFER_BIT | (depthBufferEnabled ? GL_DEPTH_BUFFER_BIT : 0));
		}
	}

	private void drawBackgroundImage(Background bg) {
		if (bg == null || bg.getImage() == null || bg.getCropWidth() <= 0 || bg.getCropHeight() <= 0) return;

		GL11.glDisable(GL_LIGHTING);
		GL11.glDisable(GL_FOG);
		GL11.glDisable(GL_ALPHA_TEST);
		GL11.glDisable(GL_BLEND);

		GL11.glDepthFunc(GL_ALWAYS);
		GL11.glDepthMask(false);

		GL11.glMatrixMode(GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();

		GL11.glMatrixMode(GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();

		int imgGlFormat = bg.getImage().getFormat() == Image2D.RGB ? GL_RGB : GL_RGBA;
		int imgW = bg.getImage().getWidth();
		int imgH = bg.getImage().getHeight();

		float viewportW = viewportWidth;
		float viewportH = viewportHeight;

		float imgScaleX = viewportW / bg.getCropWidth();
		float imgScaleY = viewportH / bg.getCropHeight();

		//TODO: understand what's going on here
		float drawX = -viewportW * bg.getCropX() / bg.getCropWidth() - viewportW / 2;
		float drawY = viewportH * bg.getCropY() / bg.getCropHeight() + viewportH / 2;

		float offsetX = imgScaleX * imgW;
		float offsetY = imgScaleY * imgH;

		int repeatsX = 1;
		if (bg.getImageModeX() == Background.REPEAT) {
			if ((drawX %= offsetX) > 0) {
				drawX -= offsetX;
			}

			repeatsX = (int) (2.5f + viewportW / offsetX);
			drawX -= repeatsX / 2f * offsetX;
		}

		int repeatsY = 1;
		if (bg.getImageModeY() == Background.REPEAT) {
			drawY %= offsetY;
			repeatsY = (int) (2.5f + viewportH / offsetY);
			drawY += repeatsY / 2f * offsetY;
		}

		GL11.glPixelStorei(GL_UNPACK_ROW_LENGTH, imgW);
		GL11.glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
		GL11.glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);

		GL11.glPixelZoom(imgScaleX, -imgScaleY);
		ByteBuffer imgBuffer = memoryBuffers.getImageBuffer(bg.getImage().getImageData());

		for (int y = 0; y < repeatsY; y++) {
			for (int x = 0; x < repeatsX; x++) {
				GL11.glRasterPos4f(0, 0, 0, 1);
				GL11.glBitmap(0, 0, 0, 0, drawX + x * offsetX, drawY - y * offsetY, imgBuffer);
				GL11.glDrawPixels(imgW, imgH, imgGlFormat, GL_UNSIGNED_BYTE, imgBuffer);
			}
		}

		GL11.glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);

		GL11.glPopMatrix();
		GL11.glMatrixMode(GL_PROJECTION);
		GL11.glPopMatrix();
	}

	private void setupCamera() {
		if (CameraCache.camera != null) {
			Transform tmpMat = new Transform();

			CameraCache.camera.getProjection(tmpMat);
			tmpMat.transpose();
			GL11.glMatrixMode(GL_PROJECTION);
			GL11.glLoadMatrix(memoryBuffers.getFloatBuffer(((Transform3D) tmpMat.getImpl()).m_matrix));

			tmpMat.set(CameraCache.invCam);
			tmpMat.transpose();
			GL11.glMatrixMode(GL_MODELVIEW);
			GL11.glLoadMatrix(memoryBuffers.getFloatBuffer(((Transform3D) tmpMat.getImpl()).m_matrix));
		}
	}

	private void setupLights(Vector lights, Vector lightMats, int scope) {
		for (int i = 0; i < MaxLights; ++i) {
			GL11.glDisable(GL_LIGHT0 + i);
		}

		if (!useGL11()) {
			ARBColorBufferFloat.glClampColorARB(
					ARBColorBufferFloat.GL_CLAMP_VERTEX_COLOR_ARB,
					Settings.m3gDisableLightClamp ? GL_FALSE : GL_TRUE
			);
		}

		int usedLights = 0;
		Transform tmpMat = new Transform();

		for (int i = 0; i < lights.size() && usedLights < MaxLights; ++i) {
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
			GL11.glMultMatrix(memoryBuffers.getFloatBuffer(((Transform3D) tmpMat.getImpl()).m_matrix));

			int lightId = GL_LIGHT0 + usedLights;
			usedLights++;

			float[] lightColor = new float[]{0, 0, 0, 1}; //rgba

			//Set default light preferences?
			GL11.glLight(lightId, GL_AMBIENT, memoryBuffers.getFloatBuffer(lightColor));
			GL11.glLight(lightId, GL_DIFFUSE, memoryBuffers.getFloatBuffer(lightColor));
			GL11.glLight(lightId, GL_SPECULAR, memoryBuffers.getFloatBuffer(lightColor));

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

			GL11.glLight(lightId, GL_POSITION, memoryBuffers.getFloatBuffer(tmpLightPos));

			G3DUtils.fillFloatColor(lightColor, light.getColor());
			float lightIntensity = light.getIntensity();
			lightColor[0] *= lightIntensity;
			lightColor[1] *= lightIntensity;
			lightColor[2] *= lightIntensity;
			lightColor[3] = 1.0F;

			int lightMode = light.getMode();

			if (lightMode == Light.AMBIENT) {
				GL11.glLight(lightId, GL_AMBIENT, memoryBuffers.getFloatBuffer(lightColor));
			} else {
				GL11.glLight(lightId, GL_DIFFUSE, memoryBuffers.getFloatBuffer(lightColor));
				GL11.glLight(lightId, GL_SPECULAR, memoryBuffers.getFloatBuffer(lightColor));
			}

			if (lightMode == Light.SPOT) {
				GL11.glLight(lightId, GL_SPOT_DIRECTION, memoryBuffers.getFloatBuffer(LightsCache.NEGATIVE_Z_AXIS));
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

	public final void render(VertexBuffer vb, IndexBuffer ib, Appearance ap, Transform trans, int scope) {
		renderVertex(vb, ib, ap, trans, scope, 1.0F);
	}

	private void renderVertex(VertexBuffer vb, IndexBuffer ib, Appearance ap, Transform trans, int scope, float alphaFactor) {
		if ((CameraCache.camera.getScope() & scope) != 0) {
			setupViewport();
			setupDepth();
			setupCamera();
			setupLights(LightsCache.m_lights, LightsCache.m_lightsTransform, scope);

			if (trans != null) {
				Transform tmpMat = new Transform();
				tmpMat.set(trans);
				tmpMat.transpose();
				GL11.glMultMatrix(memoryBuffers.getFloatBuffer(((Transform3D) tmpMat.getImpl()).m_matrix));
			}

			setupAppearance(ap, false);
			draw(vb, ib, ap, alphaFactor);
		}
	}

	private void setupAppearance(Appearance ap, boolean spriteMode) {
		if (!spriteMode) {
			setupPolygonMode(ap.getPolygonMode());
		}

		setupCompositingMode(ap.getCompositingMode());
		if (!spriteMode) {
			setupMaterial(ap.getMaterial());
		}

		setupFog(ap.getFog());
	}

	private static void setupPolygonMode(PolygonMode pm) {
		if (pm == null) {
			pm = new PolygonMode();
		}

		GL11.glPolygonMode(GL_FRONT_AND_BACK, Settings.xrayView ? GL_LINE : GL_FILL);

		int var1 = pm.getCulling();
		if (var1 == PolygonMode.CULL_NONE) {
			GL11.glDisable(GL_CULL_FACE);
		} else {
			GL11.glEnable(GL_CULL_FACE);
			GL11.glCullFace(var1 == PolygonMode.CULL_FRONT ? GL_FRONT : GL_BACK);
		}

		GL11.glShadeModel(pm.getShading() == PolygonMode.SHADE_FLAT ? GL_FLAT : GL_SMOOTH);
		GL11.glFrontFace(pm.getWinding() == PolygonMode.WINDING_CW ? GL_CW : GL_CCW);
		GL11.glLightModelf(GL_LIGHT_MODEL_TWO_SIDE, pm.isTwoSidedLightingEnabled() ? 1.0F : 0.0F);
		GL11.glLightModelf(GL_LIGHT_MODEL_LOCAL_VIEWER, pm.isLocalCameraLightingEnabled() ? 1.0F : 0.0F);

		boolean persCorrect = pm.isPerspectiveCorrectionEnabled();
		if (Settings.m3gForcePerspectiveCorrection) persCorrect = true;

		GL11.glHint(GL_PERSPECTIVE_CORRECTION_HINT, persCorrect ? GL_NICEST : GL_FASTEST);
	}

	private void setupCompositingMode(CompositingMode cm) {
		if (cm == null) {
			cm = new CompositingMode();
		}

		if (depthBufferEnabled) {
			GL11.glEnable(GL_DEPTH_TEST);
		} else {
			GL11.glDisable(GL_DEPTH_TEST);
		}

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
			GL11.glDisable(Settings.xrayView ? GL_POLYGON_OFFSET_LINE : GL_POLYGON_OFFSET_FILL);
		} else {
			GL11.glEnable(Settings.xrayView ? GL_POLYGON_OFFSET_LINE : GL_POLYGON_OFFSET_FILL);
		}
	}

	private void setupMaterial(Material mat) {
		if (mat != null) {
			GL11.glEnable(GL_LIGHTING);
			float[] tmpCol = new float[4];

			G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.AMBIENT));
			GL11.glMaterial(GL_FRONT_AND_BACK, GL_AMBIENT, memoryBuffers.getFloatBuffer(tmpCol));

			G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.DIFFUSE));
			GL11.glMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE, memoryBuffers.getFloatBuffer(tmpCol));

			G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.EMISSIVE));
			GL11.glMaterial(GL_FRONT_AND_BACK, GL_EMISSION, memoryBuffers.getFloatBuffer(tmpCol));

			G3DUtils.fillFloatColor(tmpCol, mat.getColor(Material.SPECULAR));
			GL11.glMaterial(GL_FRONT_AND_BACK, GL_SPECULAR, memoryBuffers.getFloatBuffer(tmpCol));

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

	private void setupFog(Fog fog) {
		if (fog != null && !Settings.xrayView) {
			GL11.glEnable(GL_FOG);
			GL11.glFogi(GL_FOG_MODE, fog.getMode() == Fog.LINEAR ? GL_LINEAR : GL_EXP);

			float[] fogColor = new float[4];
			G3DUtils.fillFloatColor(fogColor, fog.getColor());
			fogColor[3] = 1.0F;
			GL11.glFog(GL_FOG_COLOR, memoryBuffers.getFloatBuffer(fogColor));

			GL11.glFogf(GL_FOG_START, fog.getNearDistance());
			GL11.glFogf(GL_FOG_END, fog.getFarDistance());
			GL11.glFogf(GL_FOG_DENSITY, fog.getDensity());
		} else {
			GL11.glDisable(GL_FOG);
		}
	}

	private void draw(VertexBuffer vb, IndexBuffer indices, Appearance ap, float alphaFactor) {
		VertexArray colors = vb.getColors();
		if (colors == null) {
			int col = vb.getDefaultColor();
			GL11.glColor4ub((byte) (col >> 16 & 255), (byte) (col >> 8 & 255), (byte) (col & 255), (byte) ((col >> 24 & 255) * alphaFactor));
			GL11.glDisableClientState(GL_COLOR_ARRAY);
		} else {
			GL11.glEnableClientState(GL_COLOR_ARRAY);
			if (colors.getComponentType() == 1) {
				byte[] colorsBArr = colors.getByteValues();
				GL11.glColorPointer(
						alphaFactor == 1.0F ? colors.getComponentCount() : 4,
						GL_UNSIGNED_BYTE,
						0,
						memoryBuffers.getColorBuffer(colorsBArr, alphaFactor, colors.getVertexCount())
				);
			}
		}

		VertexArray normals = vb.getNormals();
		if (normals != null && ap.getMaterial() != null) {
			GL11.glEnableClientState(GL_NORMAL_ARRAY);
			glEnable(GL_NORMALIZE);
			if (normals.getComponentType() == 1) {
				GL11.glNormalPointer(0, memoryBuffers.getNormalBuffer(normals.getByteValues()));
			} else {
				GLExtensions.glNormalPointer(0, memoryBuffers.getNormalBuffer(normals.getShortValues()));
			}
		} else {
			GL11.glDisableClientState(GL_NORMAL_ARRAY);
		}

		float[] scaleBias = new float[4];
		VertexArray positions = vb.getPositions(scaleBias);
		GL11.glEnableClientState(GL_VERTEX_ARRAY);
		if (positions.getComponentType() == 1) {
			byte[] posesBArr = positions.getByteValues();
			GL11.glVertexPointer(positions.getComponentCount(), 0, memoryBuffers.getVertexBuffer(posesBArr));
		} else {
			short[] posesSArr = positions.getShortValues();
			GL11.glVertexPointer(positions.getComponentCount(), 0, memoryBuffers.getVertexBuffer(posesSArr));
		}

		GL11.glMatrixMode(GL_MODELVIEW);
		GL11.glTranslatef(scaleBias[1], scaleBias[2], scaleBias[3]);
		GL11.glScalef(scaleBias[0], scaleBias[0], scaleBias[0]);

		TriangleStripArray triangleStripArray = (TriangleStripArray) indices;
		int stripCount = triangleStripArray.getStripCount();

		if (ap != null && !Settings.xrayView) {

			for (int i = 0; i < NumTextureUnits; ++i) {
				Texture2D texture2D = ap.getTexture(i);
				scaleBias[3] = 0.0F;
				VertexArray texCoords = vb.getTexCoords(i, scaleBias);

				if (texture2D == null || texCoords == null) continue;

				Image2D image2D = texture2D.getImage();

				if (!useGL11()) {
					GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
					GL13.glClientActiveTexture(GL13.GL_TEXTURE0 + i);
				}

				int id = image2D.getId();
				if (id == 0) {
					id = GL11.glGenTextures();
					image2D.setId(id);
					image2D.setLoaded(false);
					if (!usedGLTextures.contains(id))
						usedGLTextures.add(id);
					texturesTable.put(id, image2D);
				}

				GL11.glEnable(GL_TEXTURE_2D);
				GL11.glBindTexture(GL_TEXTURE_2D, id);

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
				GL11.glTexEnv(GL_TEXTURE_ENV, GL_TEXTURE_ENV_COLOR, memoryBuffers.getFloatBuffer(blendColor));

				if (!image2D.isLoaded()) {
					image2D.setLoaded(true);

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

					if (!useGL11())
						GL11.glTexParameteri(GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL_TRUE);

					GL11.glTexImage2D(GL_TEXTURE_2D, 0,
							texFormat, image2D.getWidth(), image2D.getHeight(), 0,
							texFormat, GL_UNSIGNED_BYTE,
							memoryBuffers.getImageBuffer(image2D.getImageData())
					);
				}

				GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
						texture2D.getWrappingS() == Texture2D.WRAP_CLAMP && !useGL11() ? GL_CLAMP_TO_EDGE : GL_REPEAT
				);
				GL11.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
						texture2D.getWrappingT() == Texture2D.WRAP_CLAMP && !useGL11() ? GL_CLAMP_TO_EDGE : GL_REPEAT
				);

				int levelFilter = texture2D.getLevelFilter();
				int imageFilter = texture2D.getImageFilter();

				if (useGL11() || Settings.m3gMipmapping == Settings.MIP_OFF) {
					levelFilter = Texture2D.FILTER_BASE_LEVEL;
					if (!useGL11()) glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 1);
				} else if (Settings.m3gMipmapping == Settings.MIP_LINEAR) {
					levelFilter = Texture2D.FILTER_NEAREST;
					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 1);
				} else if (Settings.m3gMipmapping == Settings.MIP_TRILINEAR) {
					levelFilter = Texture2D.FILTER_LINEAR;
					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 1);
				} else if (Settings.m3gMipmapping >= Settings.MIP_ANISO_2) {
					levelFilter = Texture2D.FILTER_LINEAR;
					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 2 << (Settings.m3gMipmapping - Settings.MIP_ANISO_2));
				}

				if (Settings.m3gTexFilter == Settings.TEX_FILTER_NEAREST) {
					imageFilter = Texture2D.FILTER_NEAREST;
				} else if (Settings.m3gTexFilter == Settings.TEX_FILTER_LINEAR) {
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
				GL11.glTexCoordPointer(texCoords.getComponentCount(), 0, texCoordBuffer);


				Transform tmpMat = new Transform();
				texture2D.getCompositeTransform(tmpMat);
				tmpMat.transpose();

				GL11.glMatrixMode(GL_TEXTURE);
				GL11.glLoadMatrix(memoryBuffers.getFloatBuffer(((Transform3D) tmpMat.getImpl()).m_matrix));
				GL11.glTranslatef(scaleBias[1], scaleBias[2], scaleBias[3]);
				GL11.glScalef(scaleBias[0], scaleBias[0], scaleBias[0]);
			}

			for (int i = 0; i < stripCount; ++i) {
				int[] indexStrip = triangleStripArray.getIndexStrip(i);
				GL11.glDrawElements(GL_TRIANGLE_STRIP, memoryBuffers.getElementsBuffer(indexStrip));
			}

			for (int i = 0; i < NumTextureUnits; ++i) {
				Texture2D tex = ap.getTexture(i);
				if (tex == null) continue;

				Image2D image2D = tex.getImage();

				if (image2D.getId() != 0) {
					if (!useGL11()) {
						GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
						GL13.glClientActiveTexture(GL13.GL_TEXTURE0 + i);
					}
					GL11.glBindTexture(GL_TEXTURE_2D, 0);
				}
			}
		} else {
			//xray
			for (int i = 0; i < stripCount; ++i) {
				int[] indexStrip = triangleStripArray.getIndexStrip(i);
				GL11.glDrawElements(GL_TRIANGLE_STRIP, memoryBuffers.getElementsBuffer(indexStrip));
			}
		}

		int err = GL11.glGetError();
		if (err != GL11.GL_NO_ERROR) {
			Emulator.getEmulator().getLogStream().println("GL Error: " + err + " " + Util.translateGLErrorString(err));
		}
		if (exiting) {
			releaseTarget();
		}
	}

	public synchronized void render(World world) {
		Transform camTrans = new Transform();
		world.getActiveCamera().getTransformTo(world, camTrans);
		CameraCache.setCamera(world.getActiveCamera(), camTrans);

		clearBackgound(world.getBackground());
		LightsCache.addLightsFromWorld(world);
		renderPipe.pushRenderNode(world, null);

		renderPushedNodes();
	}

	public synchronized void render(Node node, Transform transform) {
		renderPipe.pushRenderNode(node, transform);
		renderPushedNodes();
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
					VertexBuffer vb = MeshMorph.getInstance().getMorphedVertexBuffer(mesh);
					renderVertex(vb, indices, ap, ro.trans, mesh.getScope(), ro.alphaFactor);
				}
			} else {
				renderSprite((Sprite3D) ro.node, ro.trans, ro.alphaFactor);
			}
		}

		renderPipe.clear();
		MeshMorph.getInstance().clearCache();
	}

	private void renderSprite(Sprite3D var1, Transform var2, float alphaFactor) {
		float[] var3 = new float[]{0.0F, 0.0F, 0.0F, 1.0F};
		float[] var4 = new float[]{1.0F, 0.0F, 0.0F, 1.0F};
		float[] var5 = new float[]{0.0F, 1.0F, 0.0F, 1.0F};
		Transform var6;
		(var6 = new Transform(CameraCache.invCam)).postMultiply(var2);
		Transform3D impl = (Transform3D) var6.getImpl();
		impl.transform(var3);
		impl.transform(var4);
		impl.transform(var5);
		float[] var7 = new float[]{var3[0], var3[1], var3[2], var3[3]};
		Vector4f.mul(var3, 1.0F / var3[3]);
		Vector4f.mul(var4, 1.0F / var4[3]);
		Vector4f.mul(var5, 1.0F / var5[3]);
		Vector4f.sub(var4, var3);
		Vector4f.sub(var5, var3);
		float[] var8 = new float[]{Vector4f.length(var4), 0.0F, 0.0F, 0.0F};
		float[] var9 = new float[]{0.0F, Vector4f.length(var5), 0.0F, 0.0F};
		Vector4f.add(var8, var7);
		Vector4f.add(var9, var7);
		Transform var10 = new Transform();
		CameraCache.camera.getProjection(var10);
		impl = (Transform3D) var10.getImpl();
		impl.transform(var7);
		impl.transform(var8);
		impl.transform(var9);
		if (var7[3] > 0.0F && -var7[3] < var7[2] && var7[2] <= var7[3]) {
			Vector4f.mul(var7, 1.0F / var7[3]);
			Vector4f.mul(var8, 1.0F / var8[3]);
			Vector4f.mul(var9, 1.0F / var9[3]);
			Vector4f.sub(var8, var7);
			Vector4f.sub(var9, var7);
			boolean var11 = var1.isScaled();
			int[] var12;
			boolean var13 = (var12 = new int[]{var1.getCropX(), var1.getCropY(), var1.getCropWidth(), var1.getCropHeight()})[2] < 0;
			boolean var14 = var12[3] < 0;
			var12[2] = Math.abs(var12[2]);
			var12[3] = Math.abs(var12[3]);
			float var15 = 1.0F;
			float var16 = 1.0F;
			float var17 = (float) ((var13 ? var12[2] : -var12[2]) / 2);
			float var18 = (float) ((var14 ? -var12[3] : var12[3]) / 2);
			float var19;
			float var20;
			if (!var11) {
				if (var13) {
					var15 = -1.0F;
				}

				if (var14) {
					var16 = -1.0F;
				}

				var19 = (float) var12[2];
				var20 = (float) var12[3];
			} else {
				var15 = Vector4f.length(var8) * (float) this.viewportWidth * 0.5F;
				var16 = Vector4f.length(var9) * (float) this.viewportHeight * 0.5F;
				var19 = var15;
				var20 = var16;
				var17 = -var15 / 2.0F;
				var18 = var16 / 2.0F;
				if (var13) {
					var17 += var15;
				}

				if (var14) {
					var18 -= var16;
				}

				var15 /= var13 ? -((float) var12[2]) : (float) var12[2];
				var16 /= var14 ? -((float) var12[3]) : (float) var12[3];
			}

			int[] var21 = new int[4];
			if (G3DUtils.intersectRectangle(var12[0], var12[1], var12[2], var12[3], 0, 0, var1.getImage().getWidth(), var1.getImage().getHeight(), var21)) {
				float var10000;
				label96:
				{
					if (!var13) {
						var10000 = var17 - var15 * (float) (var12[0] - var21[0]);
					} else {
						if (var12[0] <= 0) {
							break label96;
						}

						var10000 = var17 + var15 * (float) (var12[0] - var21[0]);
					}

					var17 = var10000;
				}

				label90:
				{
					if (!var14) {
						var10000 = var18 + var16 * (float) (var12[1] - var21[1]);
					} else {
						if (var12[1] <= 0) {
							break label90;
						}

						var10000 = var18 - var16 * (float) (var12[1] - var21[1]);
					}

					var18 = var10000;
				}

				ByteBuffer var27;
				short var28;
				label84:
				{
					Transform var22;
					(var22 = new Transform()).postScale((float) this.viewportWidth / ((float) this.viewportWidth + var19), (float) this.viewportHeight / ((float) this.viewportHeight + var20), 1.0F);
					var22.postMultiply(var10);
					var10.set(var22);
					int var23 = (int) ((float) this.viewportX - var19 / 2.0F);
					int var24 = (int) ((float) this.viewportY - var20 / 2.0F);
					int var25 = (int) ((float) this.viewportWidth + var19);
					int var26 = (int) ((float) this.viewportHeight + var20);
					var10.transpose();
					var6.transpose();
					GL11.glViewport(var23, targetHeight - var24 - var26, var25, var26);
					GL11.glMatrixMode(5889);
					GL11.glLoadMatrix(memoryBuffers.getFloatBuffer(((Transform3D) var10.getImpl()).m_matrix));
					GL11.glMatrixMode(5888);
					GL11.glLoadMatrix(memoryBuffers.getFloatBuffer(((Transform3D) var6.getImpl()).m_matrix));
					GL11.glDisable(2896);
					var27 = memoryBuffers.getImageBuffer(var1.getImage().getImageData());
					GL11.glRasterPos4f(0.0F, 0.0F, 0.0F, 1.0F);
					GL11.glPixelStorei(3314, var1.getImage().getWidth());
					GL11.glPixelStorei(3315, var21[1]);
					GL11.glPixelStorei(3316, var21[0]);
					GL11.glBitmap(0, 0, 0.0F, 0.0F, var17, var18, var27);
					GL11.glPixelZoom(var15, -var16);
					var28 = 6407;
					short var29;
					switch (var1.getImage().getFormat()) {
						case 96:
							var29 = 6406;
							break;
						case 97:
							var29 = 6409;
							break;
						case 98:
							var29 = 6410;
							break;
						case 99:
							var29 = 6407;
							break;
						case 100:
							var29 = 6408;
							break;
						default:
							break label84;
					}

					var28 = var29;
				}

				this.setupAppearance(var1.getAppearance(), true);
				GL11.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) (255 * alphaFactor));
				GL11.glDisableClientState(GL_COLOR_ARRAY);

				GL11.glDrawPixels(var21[2], var21[3], var28, 5121, var27);
				GL11.glPixelStorei(3314, 0);
				GL11.glPixelStorei(3315, 0);
				GL11.glPixelStorei(3316, 0);
			}
		}
	}

	public final void v3bind(Graphics var1) {
	}

	public final void v3release(Graphics var1) {
	}

	public final void v3flush() {
	}

	public void finalizeTexture(Image2D image2D) {
		if (usedGLTextures.contains(image2D.getId()))
			usedGLTextures.removeElement(image2D.getId());

		if (!unusedGLTextures.contains(image2D.getId()))
			unusedGLTextures.add(image2D.getId());

		image2D.setId(0);
	}

	public synchronized void releaseTextures() {
		while (!usedGLTextures.isEmpty()) releaseTexture(usedGLTextures.get(0));
		while (!unusedGLTextures.isEmpty()) releaseTexture(unusedGLTextures.get(0));
	}

	public static void exit() {
		if (instance == null)
			return;
		Emulator3D inst = instance;
		if ((!useDisplay && pbufferContext == null) || inst.exiting) return;
		inst.exiting = true;
		synchronized (inst) {
			if (useDisplay) {
				try {
					Display.makeCurrent();
					inst.releaseTextures();
					Display.destroy();
				} catch (Exception ignored) {}
				return;
			}
			try {
				// try to make context current
				pbufferContext.makeCurrent();
				inst.releaseTextures();
				pbufferContext.destroy();
				pbufferContext = null;
			} catch (Exception ignored) {}
		}
	}

	public void invalidateTexture(Image2D image2D) {
		image2D.setLoaded(false);
	}

	private void releaseTexture(int id) {
		GL11.glDeleteTextures(id);
		usedGLTextures.removeElement(id);
		unusedGLTextures.removeElement(id);
		if (texturesTable.containsKey(id)) {
			Image2D img = texturesTable.get(id);
			if (img != null)
				img.setId(0);
		}
		texturesTable.remove(id);
	}
}