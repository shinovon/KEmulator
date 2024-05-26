package javax.microedition.lcdui.game;

import javax.microedition.lcdui.*;

public class LayerManager {
	private int size;
	private Layer[] layers;
	private int x;
	private int y;
	private int width;
	private int height;

	public LayerManager() {
		super();
		this.layers = new Layer[4];
		this.setViewWindow(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public void append(final Layer layer) {
		insert(layer, size);
	}

	public void insert(final Layer layer, final int n) {
		if (n < 0 || n > this.size) {
			throw new IndexOutOfBoundsException();
		}
		remove(layer);
		if (this.size == this.layers.length) {
			final Layer[] aLayerArray431 = new Layer[this.size + 4];
			System.arraycopy(this.layers, 0, aLayerArray431, 0, this.size);
			System.arraycopy(this.layers, n, aLayerArray431, n + 1, this.size - n);
			this.layers = aLayerArray431;
		} else {
			System.arraycopy(this.layers, n, this.layers, n + 1, this.size - n);
		}
		this.layers[n] = layer;
		++this.size;
	}

	public Layer getLayerAt(final int n) {
		if (n < 0 || n >= this.size) {
			throw new IndexOutOfBoundsException();
		}
		return this.layers[n];
	}

	public int getSize() {
		return this.size;
	}

	public void remove(final Layer layer) {
		if (layer == null) {
			throw new NullPointerException();
		}
		int anInt430 = this.size;
		while (--anInt430 >= 0) {
			if (this.layers[anInt430] == layer) {
				System.arraycopy(this.layers, anInt430 + 1, this.layers, anInt430, this.size - anInt430 - 1);
				this.layers[--this.size] = null;
				break;
			}
		}
	}

	public void paint(final Graphics graphics, final int n, final int n2) {
		final int clipX = graphics.getClipX();
		final int clipY = graphics.getClipY();
		final int clipWidth = graphics.getClipWidth();
		final int clipHeight = graphics.getClipHeight();
		graphics.translate(n - this.x, n2 - this.y);
		graphics.clipRect(this.x, this.y, this.width, this.height);
		int anInt430 = this.size;
		while (--anInt430 >= 0) {
			final Layer layer;
			if ((layer = this.layers[anInt430]).visible) {
				layer.paint(graphics);
			}
		}
		graphics.translate(-n + this.x, -n2 + this.y);
		graphics.setClip(clipX, clipY, clipWidth, clipHeight);
	}

	public void setViewWindow(final int anInt432, final int anInt433, final int anInt434, final int anInt435) {
		if (anInt434 < 0 || anInt435 < 0) {
			throw new IllegalArgumentException();
		}
		this.x = anInt432;
		this.y = anInt433;
		this.width = anInt434;
		this.height = anInt435;
	}

	private void method213(final Layer layer, final int n) {
		if (this.size == this.layers.length) {
			final Layer[] aLayerArray431 = new Layer[this.size + 4];
			System.arraycopy(this.layers, 0, aLayerArray431, 0, this.size);
			System.arraycopy(this.layers, n, aLayerArray431, n + 1, this.size - n);
			this.layers = aLayerArray431;
		} else {
			System.arraycopy(this.layers, n, this.layers, n + 1, this.size - n);
		}
		this.layers[n] = layer;
		++this.size;
	}
}
