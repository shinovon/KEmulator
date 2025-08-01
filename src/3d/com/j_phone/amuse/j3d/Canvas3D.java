package com.j_phone.amuse.j3d;

import com.jblend.ui.SequenceInterface;
import emulator.Emulator;
import emulator.graphics2D.IImage;
import ru.woesss.j2me.micro3d.MathUtil;
import ru.woesss.j2me.micro3d.Render;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public abstract class Canvas3D extends Canvas implements SequenceInterface {

	private Texture texture;
	private Graphics graphics;

	private AffineTrans affine;
	private int scaleX, scaleY, centerX, centerY;
	private int clipX, clipY, clipWidth, clipHeight;

	public Canvas3D() {
		scaleX = 512;
		scaleY = 512;
		clipWidth = getWidth();
		clipHeight = getHeight();
		setAffineTrans(null);
	}

	public void setAffineTrans(AffineTrans t) {
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

	public void setTexture(Texture texture) {
		this.texture = texture;
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

		getViewTrans(affine, render.getViewMatrix());
		render.setCenter(centerX, centerY);
		render.setOrthographicScale(scaleX, scaleY);

		if (texture != null) render.setTexture(texture.impl);
		if (figure != null) render.drawFigure(figure.impl);

		render.release();
	}

	public void setClipRect(int x,
							int y,
							int width,
							int height) {
		this.clipX = x;
		this.clipY = y;
		this.clipWidth = width;
		this.clipHeight = height;
	}

	public void sequenceStart() {
	}

	public void sequenceStop() {
	}

	private static void getViewTrans(AffineTrans a, float[] out) {
		out[0] = a.m00 * MathUtil.TO_FLOAT; out[3] = a.m01 * MathUtil.TO_FLOAT; out[6] = a.m02 * MathUtil.TO_FLOAT; out[ 9] = a.m03;
		out[1] = a.m10 * MathUtil.TO_FLOAT; out[4] = a.m11 * MathUtil.TO_FLOAT; out[7] = a.m12 * MathUtil.TO_FLOAT; out[10] = a.m13;
		out[2] = a.m20 * MathUtil.TO_FLOAT; out[5] = a.m21 * MathUtil.TO_FLOAT; out[8] = a.m22 * MathUtil.TO_FLOAT; out[11] = a.m23;
	}

}
