package com.nttdocomo.opt.ui.j3d;

import com.nttdocomo.opt.ui.Graphics2;
import com.nttdocomo.ui.Frame;
import emulator.Emulator;
import emulator.graphics2D.IImage;
import ru.woesss.j2me.micro3d.MathUtil;
import ru.woesss.j2me.micro3d.Render;

import javax.microedition.lcdui.Graphics;

public class Micro3DGraphics extends Graphics2 implements Graphics3D {
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
}
