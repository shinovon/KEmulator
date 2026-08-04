package com.nttdocomo.opt.ui.j3d;

import com.nttdocomo.opt.ui.Graphics2;
import com.nttdocomo.ui.Frame;
import com.nttdocomo.ui.ogl.DirectBuffer;
import com.nttdocomo.ui.ogl.GraphicsOGL;
import emulator.Emulator;
import emulator.graphics2D.IImage;
import ru.woesss.j2me.micro3d.MathUtil;
import ru.woesss.j2me.micro3d.Render;

import javax.microedition.lcdui.Graphics;

public class Micro3DGraphics extends Graphics2 implements Graphics3D, GraphicsOGL {
	private Texture sphereTexture;
	private Graphics graphics;

	private AffineTrans affine;
	private int scaleX, scaleY, centerX, centerY;
	private int clipX, clipY, clipWidth, clipHeight;
	private boolean blend;
	private boolean sphereMap;
	private boolean light;
	private Vector3D directionLightVector;
	private int ambientLight;
	private int directionLight;
	private int near, far, angle;
	private int projection;

	public Micro3DGraphics(Frame owner, Graphics a) {
		super(owner, a);
	}

	public void setViewTrans(AffineTrans t) {
		if (t == null) {
			t = new AffineTrans(4096, 0, 0, 0, 0, 4096, 0, 0, 0, 0, 4096, 0);
		}
		affine = t;
	}

	public void setScreenScale(int x_scale,
							   int y_scale) {
		scaleX = x_scale;
		scaleY = y_scale;
	}

	public void setScreenCenter(int cx,
								int cy) {
		centerX = cx;
		centerY = cy;
	}

	public void drawFigure(Figure figure) {
		IImage img = Emulator.getEmulator().getScreen().getBackBufferImage();

		if (graphics == null) {
			graphics = new Graphics(img, null);
		} else {
			graphics._reset(img, null);
		}
		graphics.setClip(clipX, clipY, clipWidth, clipHeight);

		Render render = Render.getRender();
		render.bind(graphics, true);

		int attrs = render.getAttributes();

		getViewTrans(affine, render.getViewMatrix());
		render.setCenter(centerX, centerY);
		render.setOrthographicScale(scaleX, scaleY);

		if (blend) {
			attrs |= com.mascotcapsule.micro3d.v3.Graphics3D.ENV_ATTR_SEMI_TRANSPARENT;
		} else {
			attrs &= ~com.mascotcapsule.micro3d.v3.Graphics3D.ENV_ATTR_SEMI_TRANSPARENT;
		}

		if (sphereMap) {
			attrs |= com.mascotcapsule.micro3d.v3.Graphics3D.ENV_ATTR_SPHERE_MAP;
		} else {
			attrs &= ~com.mascotcapsule.micro3d.v3.Graphics3D.ENV_ATTR_SPHERE_MAP;
		}
		render.setAttribute(attrs);
		if (sphereTexture != null) {
			render.setSphereTexture(sphereTexture.impl);
			render.setToonParam(sphereTexture.toonThreshold, sphereTexture.toonHigh, sphereTexture.toonLow);
		} else {
			render.setToonParam(0, 0, 0);
		}
		if (light) {
			if (directionLightVector != null) {
				render.setLight(ambientLight, directionLight, directionLightVector.x, directionLightVector.y, directionLightVector.z);
			} else {
				render.setLight(ambientLight, 0, 0, 0, 0);
			}
		} else {
			render.setLight(4096, 0, 0, 0, 4096);
		}
		if (projection == 2) {
			render.setPerspectiveFov(near, far, angle);
		}
		if (figure != null) render.drawFigure(figure.impl);

		render.release();
	}

	public void executeCommandList(int[] a) {
		IImage img = Emulator.getEmulator().getScreen().getBackBufferImage();

		if (graphics == null) {
			graphics = new Graphics(img, null);
		} else {
			graphics._reset(img, null);
		}
		graphics.setClip(clipX, clipY, clipWidth, clipHeight);

		Render render = Render.getRender();
		render.bind(graphics, true);

		int attrs = render.getAttributes();
		getViewTrans(affine, render.getViewMatrix());
		render.setCenter(centerX, centerY);
		render.setOrthographicScale(scaleX, scaleY);

		if (blend) {
			attrs |= com.mascotcapsule.micro3d.v3.Graphics3D.ENV_ATTR_SEMI_TRANSPARENT;
		} else {
			attrs &= ~com.mascotcapsule.micro3d.v3.Graphics3D.ENV_ATTR_SEMI_TRANSPARENT;
		}

		if (sphereMap) {
			attrs |= com.mascotcapsule.micro3d.v3.Graphics3D.ENV_ATTR_SPHERE_MAP;
		} else {
			attrs &= ~com.mascotcapsule.micro3d.v3.Graphics3D.ENV_ATTR_SPHERE_MAP;
		}
		render.setAttribute(attrs);
		if (sphereTexture != null) {
			render.setSphereTexture(sphereTexture.impl);
			render.setToonParam(sphereTexture.toonThreshold, sphereTexture.toonHigh, sphereTexture.toonLow);
		} else {
			render.setToonParam(0, 0, 0);
		}
		if (light) {
			if (directionLightVector != null) {
				render.setLight(ambientLight, directionLight, directionLightVector.x, directionLightVector.y, directionLightVector.z);
			} else {
				render.setLight(ambientLight, 0, 0, 0, 0);
			}
		} else {
			render.setLight(4096, 0, 0, 0, 4096);
		}
		if (projection == 2) {
			render.setPerspectiveFov(near, far, angle);
		}
		render.drawCommandList(a);

		render.release();
	}

	public void setSphereTexture(Texture paramTexture) {
		sphereTexture = paramTexture;
	}

	public void enableLight(boolean paramBoolean) {
		light = paramBoolean;
	}

	public void enableSphereMap(boolean paramBoolean) {
		sphereMap = paramBoolean;
	}

	public void setAmbientLight(int paramInt) {
		ambientLight = paramInt;
	}

	public void setDirectionLight(Vector3D paramVector3D, int paramInt) {
		directionLightVector = paramVector3D;
		directionLight = paramInt;
	}

	public void enableSemiTransparent(boolean paramBoolean) {
		blend = paramBoolean;
	}

	public void setClipRect3D(int x,
							  int y,
							  int width,
							  int height) {
		this.clipX = x;
		this.clipY = y;
		this.clipWidth = width;
		this.clipHeight = height;
	}

	public final void setPerspective(int zNear, int zFar, int angle) {
		if ((zNear >= zFar) || (zNear < 1) || (zNear > 32766) || (zFar > 32767) || (angle < 1) || (angle > 2047)) {
			throw new IllegalArgumentException("zNear=" + zNear + ", zFar=" + zFar + ", angle=" + angle);
		}
		this.near = zNear;
		this.far = zFar;
		this.angle = angle;
		this.projection = 2;
	}

	private static void getViewTrans(AffineTrans a, float[] out) {
		out[0] = a.m00 * MathUtil.TO_FLOAT; out[3] = a.m01 * MathUtil.TO_FLOAT; out[6] = a.m02 * MathUtil.TO_FLOAT; out[ 9] = a.m03;
		out[1] = a.m10 * MathUtil.TO_FLOAT; out[4] = a.m11 * MathUtil.TO_FLOAT; out[7] = a.m12 * MathUtil.TO_FLOAT; out[10] = a.m13;
		out[2] = a.m20 * MathUtil.TO_FLOAT; out[5] = a.m21 * MathUtil.TO_FLOAT; out[8] = a.m22 * MathUtil.TO_FLOAT; out[11] = a.m23;
	}

	public void beginDrawing() {

	}

	public void endDrawing() {

	}

	public void glActiveTexture(int paramInt) {

	}

	public void glAlphaFunc(int paramInt, float paramFloat) {

	}

	public void glBindTexture(int paramInt1, int paramInt2) {

	}

	public void glBlendFunc(int paramInt1, int paramInt2) {

	}

	public void glClear(int paramInt) {

	}

	public void glClearColor(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {

	}

	public void glClearDepthf(float paramFloat) {

	}

	public void glClearStencil(int paramInt) {

	}

	public void glClientActiveTexture(int paramInt) {

	}

	public void glColor4f(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {

	}

	public void glColorMask(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4) {

	}

	public void glColorPointer(int paramInt1, int paramInt2, int paramInt3, DirectBuffer paramDirectBuffer) {

	}

	public void glCompressedTexImage2D(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, DirectBuffer paramDirectBuffer) {

	}

	public void glCompressedTexSubImage2D(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, DirectBuffer paramDirectBuffer) {

	}

	public void glCopyTexImage2D(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {

	}

	public void glCopyTexSubImage2D(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {

	}

	public void glCullFace(int paramInt) {

	}

	public void glDeleteTextures(int paramInt, int[] paramArrayOfInt) {

	}

	public void glDepthFunc(int paramInt) {

	}

	public void glDepthMask(boolean paramBoolean) {

	}

	public void glDepthRangef(float paramFloat1, float paramFloat2) {

	}

	public void glDisable(int paramInt) {

	}

	public void glDisableClientState(int paramInt) {

	}

	public void glDrawArrays(int paramInt1, int paramInt2, int paramInt3) {

	}

	public void glDrawElements(int paramInt1, int paramInt2, int paramInt3, DirectBuffer paramDirectBuffer) {

	}

	public void glEnable(int paramInt) {

	}

	public void glEnableClientState(int paramInt) {

	}

	public void glFlush() {

	}

	public void glFogf(int paramInt, float paramFloat) {

	}

	public void glFogfv(int paramInt, float[] paramArrayOfFloat) {

	}

	public void glFrontFace(int paramInt) {

	}

	public void glFrustumf(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) {

	}

	public void glGenTextures(int paramInt, int[] paramArrayOfInt) {

	}

	public int glGetError() {
		return 0;
	}

	public void glGetIntegerv(int paramInt, int[] paramArrayOfInt) {

	}

	public void glHint(int paramInt1, int paramInt2) {

	}

	public void glLightModelf(int paramInt, float paramFloat) {

	}

	public void glLightModelfv(int paramInt, float[] paramArrayOfFloat) {

	}

	public void glLightf(int paramInt1, int paramInt2, float paramFloat) {

	}

	public void glLightfv(int paramInt1, int paramInt2, float[] paramArrayOfFloat) {

	}

	public void glLineWidth(float paramFloat) {

	}

	public void glLoadIdentity() {

	}

	public void glLoadMatrixf(float[] paramArrayOfFloat) {

	}

	public void glLogicOp(int paramInt) {

	}

	public void glMaterialf(int paramInt1, int paramInt2, float paramFloat) {

	}

	public void glMaterialfv(int paramInt1, int paramInt2, float[] paramArrayOfFloat) {

	}

	public void glMatrixMode(int paramInt) {

	}

	public void glMultMatrixf(float[] paramArrayOfFloat) {

	}

	public void glMultiTexCoord4f(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {

	}

	public void glNormal3f(float paramFloat1, float paramFloat2, float paramFloat3) {

	}

	public void glNormalPointer(int paramInt1, int paramInt2, DirectBuffer paramDirectBuffer) {

	}

	public void glOrthof(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) {

	}

	public void glPixelStorei(int paramInt1, int paramInt2) {

	}

	public void glPointSize(float paramFloat) {

	}

	public void glPolygonOffset(float paramFloat1, float paramFloat2) {

	}

	public void glPopMatrix() {

	}

	public void glPushMatrix() {

	}

	public void glRotatef(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {

	}

	public void glSampleCoverage(float paramFloat, boolean paramBoolean) {

	}

	public void glScalef(float paramFloat1, float paramFloat2, float paramFloat3) {

	}

	public void glScissor(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {

	}

	public void glShadeModel(int paramInt) {

	}

	public void glStencilFunc(int paramInt1, int paramInt2, int paramInt3) {

	}

	public void glStencilMask(int paramInt) {

	}

	public void glStencilOp(int paramInt1, int paramInt2, int paramInt3) {

	}

	public void glTexCoordPointer(int paramInt1, int paramInt2, int paramInt3, DirectBuffer paramDirectBuffer) {

	}

	public void glTexEnvf(int paramInt1, int paramInt2, float paramFloat) {

	}

	public void glTexEnvfv(int paramInt1, int paramInt2, float[] paramArrayOfFloat) {

	}

	public void glTexImage2D(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, DirectBuffer paramDirectBuffer) {

	}

	public void glTexParameterf(int paramInt1, int paramInt2, float paramFloat) {

	}

	public void glTexSubImage2D(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, DirectBuffer paramDirectBuffer) {

	}

	public void glTranslatef(float paramFloat1, float paramFloat2, float paramFloat3) {

	}

	public void glVertexPointer(int paramInt1, int paramInt2, int paramInt3, DirectBuffer paramDirectBuffer) {

	}

	public void glViewport(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {

	}
}
