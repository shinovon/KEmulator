package com.nokia.mid.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public abstract class CanvasGraphicsItem extends CanvasItem {
	Object iToolkit;
	private CanvasGraphicsItemPainter iItemPainter;

	public CanvasGraphicsItem(int width, int height) {
		if ((width < 1) || (height < 1)) {
			throw new IllegalArgumentException("The given arguments are not valid");
		}

		this.iWidth = width;
		this.iHeight = height;

		this.iParent = null;

		this.iVisible = false;
	}

	public void setParent(Object parent) {
		if (((parent != null) && (!(parent instanceof Canvas)))
				|| ((parent != null) && (this.iParent != null) && (this.iParent != parent))) {
			throw new IllegalArgumentException("The object is not a valid parent object");
		}
		if (parent != this.iParent) {
			if (iParent != null) {
				((Canvas) iParent)._removeNokiaCanvasItem(this);
			}
			this.iParent = parent;
			if (iParent != null) {
				((Canvas) iParent)._addNokiaCanvasItem(this);
			}
		}
	}

	public void setSize(int width, int height) {
		if ((width < 1) || (height < 1)) {
			throw new IllegalArgumentException("The given arguments are not valid");
		}
		this.iWidth = width;
		this.iHeight = height;
	}

	public void setPosition(int x, int y) {
		this.iPositionX = x;
		this.iPositionY = y;
	}

	public void setVisible(boolean visible) {
		this.iVisible = visible;
	}

	public void setZPosition(int z) {
		if (z < 0) {
			throw new IllegalArgumentException();
		}
	}

	public int getZPosition() {
		return -1;
	}

	CanvasGraphicsItem() {
	}

	protected abstract void paint(Graphics paramGraphics);

	public final void repaint(int x, int y, int width, int height) {
		this.iItemPainter.Repaint(x, y, width, height);
	}

	public final void repaint() {
		this.iItemPainter.Repaint(0, 0, getWidth(), getHeight());
	}

	final void registeredFinalize() {

	}

	public void _invokePaint(Graphics aGraphics) {
		paint(aGraphics);
	}
}
