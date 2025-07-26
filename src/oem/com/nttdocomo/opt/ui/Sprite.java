package com.nttdocomo.opt.ui;

import com.nttdocomo.ui.Image;

public class Sprite {
	int flags;
	Image image;
	boolean visible;
	int sx;
	int sy;
	int dx;
	int dy;
	int width;
	int height;
	boolean explicitSize;
	int flipMode;
	int renderMode = -65536;

	protected Sprite() {
	}

	public Sprite(Image paramImage) {
		this.image = paramImage;
		this.visible = true;
	}

	public Sprite(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		if ((paramInt3 < 0) || (paramInt4 < 0)) {
			throw new IllegalArgumentException();
		}
		this.sx = paramInt1;
		this.sy = paramInt2;
		this.width = paramInt3;
		this.height = paramInt4;
		this.explicitSize = true;
		this.flags = 0;
		this.image = paramImage;
		this.visible = true;
	}

	public void setLocation(int paramInt1, int paramInt2) {
		this.dx = paramInt1;
		this.dy = paramInt2;
	}

	public void setImage(Image paramImage) {
		if (paramImage == null) {
			throw new NullPointerException();
		}
		this.image = paramImage;
		this.explicitSize = false;
		this.visible = true;
	}

	public void setImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		if ((paramInt3 < 0) || (paramInt4 < 0)) {
			throw new IllegalArgumentException();
		}
		if (paramImage == null) {
			throw new NullPointerException();
		}
		this.image = paramImage;
		this.sx = paramInt1;
		this.sy = paramInt2;
		this.width = paramInt3;
		this.height = paramInt4;
		this.explicitSize = true;
		this.visible = true;
	}

	public void setVisible(boolean paramBoolean) {
		this.visible = paramBoolean;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public int getX() {
		return this.dx;
	}

	public int getY() {
		return this.dy;
	}

	public int getWidth() {
		if (this.explicitSize) {
			return this.width;
		}
		return this.image.getWidth();
	}

	public int getHeight() {
		if (this.explicitSize) {
			return this.height;
		}
		return this.image.getHeight();
	}
}
