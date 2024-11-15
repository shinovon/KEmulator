package javax.microedition.lcdui.game;

public abstract class LayerExt extends Layer {

	public LayerExt() {
		super();
	}

	public LayerExt(final int n, final int n2) {
		super(n, n2);
	}

	public void setWidth(int i) {
		super._setWidth(i);
	}

	public void setHeight(int i) {
		super._setHeight(i);
	}

	public void setX(int i) {
		super.x = i;
	}

	public void setY(int i) {
		super.y = i;
	}
}
