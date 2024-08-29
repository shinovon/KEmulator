package com.sprintpcs.lcdui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class ExternalCanvas extends Canvas {
	public static final int AUTO_LCD = 27;
	public static final int EXTERNAL_A = 82;
	public static final int EXTERNAL_B = 83;
	public static final int EXTERNAL_C = 84;
	public static final int EXTERNAL_D = 85;
	public static final int KEY_FAST_FORWARD = 86;
	public static final int KEY_MUTE = 87;
	public static final int KEY_PAUSE = 88;
	public static final int KEY_PLAY = 89;
	public static final int KEY_PLAY_PAUSE = 90;
	public static final int KEY_REWIND = 91;
	public static final int KEY_STOP = 92;
	public static final int KEY_TRACK_BACK = 94;
	public static final int KEY_TRACK_FORWARD = 93;
	public static final int PRIMARY_LCD = 28;
	public static final int SECONDARY_LCD = 29;

	public void setLCD(int mode) {}

	protected void paint(Graphics g) {}
}
