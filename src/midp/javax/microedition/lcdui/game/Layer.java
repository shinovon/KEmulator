package javax.microedition.lcdui.game;

import javax.microedition.lcdui.*;

public abstract class Layer {
	int x;
	int y;
	int width;
	int height;
	boolean visible;

	Layer() {
		super();
		this.visible = true;
	}

	Layer(final int n, final int n2) {
		super();
		this.visible = true;
		this._setWidth(n);
		this._setHeight(n2);
	}

	public void setPosition(final int anInt598, final int anInt599) {
		this.x = anInt598;
		this.y = anInt599;
	}

	public void move(final int n, final int n2) {
		this.x += n;
		this.y += n2;
	}

	public final int getX() {
		return this.x;
	}

	public final int getY() {
		return this.y;
	}

	public final int getWidth() {
		return this.width;
	}

	public final int getHeight() {
		return this.height;
	}

	public void setVisible(final boolean aBoolean599) {
		this.visible = aBoolean599;
	}

	public final boolean isVisible() {
		return this.visible;
	}

	public abstract void paint(final Graphics p0);

	final void _setWidth(final int anInt601) {
		if (anInt601 < 0) {
			throw new IllegalArgumentException();
		}
		this.width = anInt601;
	}

	final void _setHeight(final int anInt602) {
		if (anInt602 < 0) {
			throw new IllegalArgumentException();
		}
		this.height = anInt602;
	}
}
