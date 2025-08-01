package emulator.graphics3D.view;

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

	public final void setXray(boolean var1) {
		this.xray = var1;
	}

	public final void setViewport(int var1, int var2) {
		this.viewportWidth = var1;
		this.viewportHeight = var2;
	}

	private void setupViewport() {
		GL11.glViewport(0, 0, this.viewportWidth, this.viewportHeight);
		GL11.glScissor(0, 0, this.viewportWidth, this.viewportHeight);
	}

	private void setupDepth() {
		GL11.glDepthRange((double) this.depthRangeNear, (double) this.depthRangeFar);
	}

	public final void clearBackground(Background var1) {
		this.setupViewport();
		this.setupDepth();
		GL11.glClearDepth(1.0D);
		GL11.glDepthMask(true);
		GL11.glColorMask(true, true, true, true);
		int var10000 = var1 != null && !this.xray ? var1.getColor() : 0;
		GL11.glClearColor(G3DUtils.getFloatColor(var10000, 16), G3DUtils.getFloatColor(var10000, 8), G3DUtils.getFloatColor(var10000, 0), G3DUtils.getFloatColor(var10000, 24));
		GL11.glClear(16640);
		if (var1 != null && !this.xray) {
			GL11.glClear(var1.isColorClearEnabled() ? 16384 : 0);
			this.method385(var1);
		} else {
			GL11.glClear(GL_COLOR_BUFFER_BIT);
		}
	}

	private void method385(Background var1) {
		if (var1 != null && var1.getImage() != null && var1.getCropWidth() > 0 && var1.getCropHeight() > 0) {
			GL11.glDisable(2896);
			GL11.glDisable(2912);
			int var2 = var1.getImage().getFormat() == 99 ? 6407 : 6408;
			int var3 = var1.getImage().getWidth();
			int var4 = var1.getImage().getHeight();
			GL11.glMatrixMode(5889);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(5888);
			GL11.glLoadIdentity();
			float var5 = (float) this.viewportWidth;
			float var6 = (float) this.viewportHeight;
			float var7 = var5 / (float) var1.getCropWidth();
			float var8 = var6 / (float) var1.getCropHeight();
			float var9 = var7 * (float) var3;
			float var10 = var8 * (float) var4;
			float var11 = -var5 * (float) var1.getCropX() / (float) var1.getCropWidth() - var5 / 2.0F;
			float var12 = var6 * (float) var1.getCropY() / (float) var1.getCropHeight() + var6 / 2.0F;
			int var13 = 1;
			int var14 = 1;
			if (var1.getImageModeX() == 33) {
				if ((var11 %= var9) > 0.0F) {
					var11 -= var9;
				}

				var13 = (int) (2.5F + var5 / var9);
				var11 -= (float) (var13 / 2) * var9;
			}

			if (var1.getImageModeY() == 33) {
				var12 %= var10;
				var14 = (int) (2.5F + var6 / var10);
				var12 += (float) (var14 / 2) * var10;
			}

			GL11.glPixelStorei(3314, var3);
			GL11.glPixelStorei(3315, 0);
			GL11.glPixelStorei(3316, 0);
			GL11.glDepthFunc(519);
			GL11.glDepthMask(false);
			GL11.glPixelZoom(var7, -var8);
			ByteBuffer var15 = memoryBuffers.getImageBuffer(var1.getImage().getImageData());

			for (int var16 = 0; var16 < var14; ++var16) {
				for (int var17 = 0; var17 < var13; ++var17) {
					GL11.glRasterPos4f(0.0F, 0.0F, 0.0F, 1.0F);
					GL11.glBitmap(0, 0, 0.0F, 0.0F, var11 + (float) var17 * var9, var12 - (float) var16 * var10, var15);
					GL11.glDrawPixels(var3, var4, var2, 5121, var15);
				}
			}

			GL11.glPixelStorei(3314, 0);
		}

	}

	public final void method368(Node var1, Transform var2) {
		if (var1 == null) {
			throw new NullPointerException();
		} else if (!(var1 instanceof Sprite3D) && !(var1 instanceof Mesh) && !(var1 instanceof Group)) {
			throw new IllegalArgumentException();
		} else {
			renderPipe.pushRenderNode(var1, var2 == null ? new Transform() : var2);
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

	private void renderVertex(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int scope, float alphaFactor) {
		if ((camera.getScope() & scope) != 0) {
			this.setupViewport();
			this.setupDepth();
			setupCamera();
			setupLights(lights, lightsTransforms, scope);
			if (var4 != null) {
				Transform var7;
				(var7 = new Transform()).set(var4);
				var7.transpose();
				GL11.glMultMatrixf(memoryBuffers.getFloatBuffer(((Transform3D) var7.getImpl()).m_matrix));
			}

			this.setupAppearance(var3, false);
			this.draw(var1, var2, var3, alphaFactor);
		}
	}

	private void renderSprite(Sprite3D var1, Transform var2, float alphaFactor) {
		Vector4f var3 = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
		Vector4f var4 = new Vector4f(1.0F, 0.0F, 0.0F, 1.0F);
		Vector4f var5 = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
		Transform var6;
		(var6 = new Transform(cameraTransform)).postMultiply(var2);
		Transform3D impl = (Transform3D) var6.getImpl();
		impl.transform(var3);
		impl.transform(var4);
		impl.transform(var5);
		Vector4f var7 = new Vector4f(var3);
		var3.mul(1.0F / var3.w);
		var4.mul(1.0F / var4.w);
		var5.mul(1.0F / var5.w);
		var4.sub(var3);
		var5.sub(var3);
		Vector4f var8 = new Vector4f(var4.length(), 0.0F, 0.0F, 0.0F);
		Vector4f var9 = new Vector4f(0.0F, var5.length(), 0.0F, 0.0F);
		var8.add(var7);
		var9.add(var7);
		Transform var10 = new Transform();
		camera.getProjection(var10);
		impl = (Transform3D) var10.getImpl();
		impl.transform(var7);
		impl.transform(var8);
		impl.transform(var9);
		if (var7.w > 0.0F && -var7.w < var7.z && var7.z <= var7.w) {
			var7.mul(1.0F / var7.w);
			var8.mul(1.0F / var8.w);
			var9.mul(1.0F / var9.w);
			var8.sub(var7);
			var9.sub(var7);
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
				var15 = var8.length() * (float) this.viewportWidth * 0.5F;
				var16 = var9.length() * (float) this.viewportHeight * 0.5F;
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
					int var23 = (int) (0F - var19 / 2.0F);
					int var24 = (int) (0F - var20 / 2.0F);
					int var25 = (int) ((float) this.viewportWidth + var19);
					int var26 = (int) ((float) this.viewportHeight + var20);
					var10.transpose();
					var6.transpose();
					GL11.glViewport(var23, viewportHeight - var24 - var26, var25, var26);
					GL11.glMatrixMode(5889);
					GL11.glLoadMatrixf(memoryBuffers.getFloatBuffer(((Transform3D) var10.getImpl()).m_matrix));
					GL11.glMatrixMode(5888);
					GL11.glLoadMatrixf(memoryBuffers.getFloatBuffer(((Transform3D) var6.getImpl()).m_matrix));
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
			int var8 = bufferImage.width << 2;
			int var10 = bufferImage.data.length - var8;

			for (int i = bufferImage.height; i > 0; --i) {
				buffer.get(bufferImage.data, var10, var8);
				var10 -= var8;
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

	public static void setCamera(Camera var0, Transform var1) {
		if (var1 != null) {
			cameraTransform.set(var1);
			((Transform3D) cameraTransform.getImpl()).invert();
		} else {
			cameraTransform.setIdentity();
		}

		camera = var0;
	}

	public static int method381(Light var0, Transform var1) {
		if (var0 == null) {
			throw new NullPointerException();
		} else {
			lights.add(var0);
			if (var1 == null) {
				lightsTransforms.add(new Transform());
			} else {
				lightsTransforms.add(new Transform(var1));
			}

			return lights.size();
		}
	}

	public static void method388() {
		lights.clear();
		lightsTransforms.clear();
	}

	public final void method374(World var1) {
		method388();
		this.method365(var1, var1);
	}

	private void method365(World var1, Group var2) {
		Transform var3 = new Transform();

		for (int var4 = 0; var4 < var2.getChildCount(); ++var4) {
			Node var5;
			if ((var5 = var2.getChild(var4)) instanceof Light && var5.getTransformTo(var1, var3)) {
				lights.add(var5);
				lightsTransforms.add(new Transform(var3));
			} else if (var5 instanceof Group) {
				this.method365(var1, (Group) var5);
			}
		}

	}

	private void setupAppearance(Appearance var1, boolean var3) {
		if (!var3) {
			this.setupPolygonMode(var1.getPolygonMode());
		}

		this.setupCompositingMode(var1.getCompositingMode());
		if (!var3) {
			setupMaterial(var1.getMaterial());
		}

		this.setupFog(var1.getFog());
	}

	//Settings.xrayView -> xray
	private void setupPolygonMode(PolygonMode pm) {
		if (pm == null) {
			pm = new PolygonMode();
		}

		GL11.glPolygonMode(GL_FRONT_AND_BACK, xray ? GL_LINE : GL_FILL);

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
			IntBuffer var10 = BufferUtils.createIntBuffer(Emulator3D.NumTextureUnits);
			GL11.glGenTextures(var10);

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
				GL11.glBindTexture(GL_TEXTURE_2D, var10.get(i));

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

				if (useGL11() || Settings.m3gMipmapping == Settings.MIP_OFF || true) {
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
					if (GL11.glIsTexture(var10.get(i))) {
						GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
						GL13.glClientActiveTexture(GL13.GL_TEXTURE0 + i);
						GL11.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
						GL11.glDisable(GL_TEXTURE_2D);
					}
				}
			}

			GL11.glDeleteTextures(var10);
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
					Settings.m3gDisableLightClamp ? GL_FALSE : GL_TRUE
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

	public final void drawGrid(float var1) {
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
		float var2 = var1 * 5.0F;
		boolean var3 = true;
		GL11.glMatrixMode(5888);
		GL11.glBegin(7);
		float var10000 = -var2;

		while (true) {
			float var4 = var10000;
			if (var10000 >= var2) {
				GL11.glEnd();
				return;
			}

			var10000 = -var2;

			while (true) {
				float var5 = var10000;
				if (var10000 >= var2) {
					var3 = !var3;
					var10000 = var4 + var1;
					break;
				}

				if (var3) {
					GL11.glVertex3f(var4, 0.0F, var5);
					GL11.glVertex3f(var4 + var1, 0.0F, var5);
					GL11.glVertex3f(var4 + var1, 0.0F, var5 + var1);
					GL11.glVertex3f(var4, 0.0F, var5 + var1);
				}

				var3 = !var3;
				var10000 = var5 + var1;
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
