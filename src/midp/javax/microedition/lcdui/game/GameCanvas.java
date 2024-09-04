package javax.microedition.lcdui.game;

import emulator.*;

import javax.microedition.lcdui.*;

public abstract class GameCanvas extends Canvas {
	public static final int UP_PRESSED = 2;
	public static final int DOWN_PRESSED = 64;
	public static final int LEFT_PRESSED = 4;
	public static final int RIGHT_PRESSED = 32;
	public static final int FIRE_PRESSED = 256;
	public static final int GAME_A_PRESSED = 512;
	public static final int GAME_B_PRESSED = 1024;
	public static final int GAME_C_PRESSED = 2048;
	public static final int GAME_D_PRESSED = 4096;
	private Graphics graphics;

	protected GameCanvas(final boolean b) {
		super();
	}

	protected Graphics getGraphics() {
		return graphics = new Graphics(Emulator.getEmulator().getScreen().getBackBufferImage(), Emulator.getEmulator().getScreen().getXRayScreenImage());
	}

	public int getKeyStates() {
		return super.m_keyStates;
	}

	public void paint(final Graphics graphics) {
		graphics.getImpl().drawImage(Emulator.getEmulator().getScreen().getBackBufferImage(), 0, 0);
	}

	public void flushGraphics(final int x, final int y, final int w, final int h) {
		if (this != Emulator.getCurrentDisplay().getCurrent()) return;
		Displayable._checkForSteps(null);
		Displayable._fpsLimiter();
		paintSoftMenu(graphics == null ? getGraphics() : graphics);
		Emulator.getEventQueue().gameGraphicsFlush(x, y, w, h);
		Displayable._resetXRayGraphics();
	}

	public void flushGraphics() {
		if (this != Emulator.getCurrentDisplay().getCurrent()) return;
		Displayable._checkForSteps(null);
		Displayable._fpsLimiter();
		paintSoftMenu(graphics == null ? getGraphics() : graphics);
		Emulator.getEventQueue().gameGraphicsFlush();
		Displayable._resetXRayGraphics();
	}

	public void invokeSizeChanged(int w, int h) {
		if (graphics != null) {
			graphics.reset(Emulator.getEmulator().getScreen().getBackBufferImage(), Emulator.getEmulator().getScreen().getXRayScreenImage());
		}
		super.invokeSizeChanged(w, h);
	}
}
