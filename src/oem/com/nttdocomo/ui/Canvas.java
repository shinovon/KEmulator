package com.nttdocomo.ui;

import com.nttdocomo.opt.ui.j3d.Micro3DGraphics;
import com.nttdocomo.ui.maker.*;

import javax.microedition.lcdui.*;

public abstract class Canvas extends Frame {
	boolean b;
	public static final int IME_COMMITTED = 0;
	public static final int IME_CANCELED = 1;
	private int keypadState;

	public Canvas() {
		this.a = (Displayable) new CanvasImpl(this);
	}

	public Graphics getGraphics() {
		return new Micro3DGraphics(this, ((CanvasImpl) this.a).createGraphics());
	}

	public abstract void paint(final Graphics p0);

	public void repaint() {
		repaint(0, 0, getWidth(), getHeight());
	}

	public void repaint(final int n, final int n2, final int n3, final int n4) {
		this.b = true;
		((CanvasImpl) this.a).repaint(n, n2, n3, n4);
		this.b = false;
	}

	public void processEvent(final int n, final int n2) {
	}

	public void _setKeypadState(int n) {
		keypadState = n;
	}

	public int getKeypadState() {
		return keypadState;
	}

	public void imeOn(final String s, final int n, final int n2) {
	}

	public void processIMEEvent(final int n, final String s) {
	}
}
