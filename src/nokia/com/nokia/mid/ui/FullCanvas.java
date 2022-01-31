package com.nokia.mid.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

public abstract class FullCanvas extends Canvas {
	public static final int KEY_SOFTKEY1 = -6;
	public static final int KEY_SOFTKEY2 = -7;
	public static final int KEY_SEND = -10;
	public static final int KEY_END = -11;
	public static final int KEY_SOFTKEY3 = -5;
	public static final int KEY_UP_ARROW = -1;
	public static final int KEY_DOWN_ARROW = -2;
	public static final int KEY_LEFT_ARROW = -3;
	public static final int KEY_RIGHT_ARROW = -4;
	private static int sFullWidth = 0;
	private static int sFullHeight = 0;

	protected FullCanvas() {
		super.setFullScreenMode(true);
	}

	public void addCommand(Command aCmd) throws IllegalStateException {
		//throw new IllegalStateException("This method is not supported in com.nokia.mid.ui.FullCanvas");
	    super.addCommand(aCmd);
	}

	public void setCommandListener(CommandListener aL) throws IllegalStateException {
		//throw new IllegalStateException("This method is not supported in com.nokia.mid.ui.FullCanvas");
	    super.setCommandListener(aL);
	}

	public void setFullScreenMode(boolean aMode) {
		if (!aMode) {
			//throw new IllegalArgumentException("Not supported in com.nokia.mid.ui.FullCanvas");
		}
	}
}
