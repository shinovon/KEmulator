package com.nttdocomo.ui.maker;

import com.nttdocomo.ui.Canvas;
import emulator.Emulator;
import emulator.KeyMapping;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

public class CanvasImpl extends GameCanvas {
	Canvas c;
	static Image d;

	public CanvasImpl(Canvas var1) {
		super(false);
		this.c = var1;
	}

	static Image b() {
		if (d == null) {
			d = Image.createImage(Emulator.getEmulator().getScreen().getWidth(),
					Emulator.getEmulator().getScreen().getHeight());
		}

		return d;
	}

	public void paint(Graphics var1) {
		Image var2 = b();
		this.c.paint(new com.nttdocomo.ui.Graphics(var2.getGraphics()));
		var1.drawImage(var2, 0, 0, 0);
	}

	protected void keyReleased(int var1) {
		int keypadState = this.c.getKeypadState();
		int a = this.a(var1);
		this.c.processEvent(1, a);
		if (a == 16) {
			this.c._setKeypadState(keypadState & (~0x10000));
		} else if (a == 17) {
			this.c._setKeypadState(keypadState & (~0x20000));
		} else if (a == 18) {
			this.c._setKeypadState(keypadState & (~0x40000));
		} else if (a == 19) {
			this.c._setKeypadState(keypadState & (~0x80000));
		} else {
			this.c._setKeypadState(a);
		}
	}

	protected void keyPressed(int var1) {
		int keypadState = this.c.getKeypadState();
		int a = this.a(var1);
		this.c.processEvent(0, a);
		if (a == 16) {
			this.c._setKeypadState(keypadState | 0x10000);
		} else if (a == 17) {
			this.c._setKeypadState(keypadState | 0x20000);
		} else if (a == 18) {
			this.c._setKeypadState(keypadState | 0x40000);
		} else if (a == 19) {
			this.c._setKeypadState(keypadState | 0x80000);
		} else {
			this.c._setKeypadState(a);
		}
	}

	protected void keyRepeated(int var1) {
	}

	int a(int var1) {
		switch (var1) {
			case 35:
				return 11;
			case 36:
			case 37:
			case 38:
			case 39:
			case 40:
			case 41:
			case 43:
			case 44:
			case 45:
			case 46:
			case 47:
			default:
				if (var1 == KeyMapping.getArrowKeyFromDevice(GameCanvas.UP)) {
					return 17;
				} else if (var1 == KeyMapping.getArrowKeyFromDevice(GameCanvas.DOWN)) {
					return 19;
				} else if (var1 == KeyMapping.getArrowKeyFromDevice(GameCanvas.LEFT)) {
					return 16;
				} else if (var1 == KeyMapping.getArrowKeyFromDevice(GameCanvas.RIGHT)) {
					return 18;
				} else if (var1 == KeyMapping.getArrowKeyFromDevice(GameCanvas.FIRE)) {
					return 20;
				} else if (KeyMapping.isLeftSoft(var1)) {
					return 21;
				} else {
					if (KeyMapping.isRightSoft(var1)) {
						return 22;
					}

					return 0;
				}
			case 42:
				return 10;
			case 48:
				return 0;
			case 49:
				return 1;
			case 50:
				return 2;
			case 51:
				return 3;
			case 52:
				return 4;
			case 53:
				return 5;
			case 54:
				return 6;
			case 55:
				return 7;
			case 56:
				return 8;
			case 57:
				return 9;
		}
	}

	public Graphics createGraphics() {
		return getGraphics();
	}
}
