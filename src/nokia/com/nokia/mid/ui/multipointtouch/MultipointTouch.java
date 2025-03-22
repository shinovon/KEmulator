package com.nokia.mid.ui.multipointtouch;

// TODO
public class MultipointTouch {
	public static final int POINTER_PRESSED = 1;
	public static final int POINTER_RELEASED = 2;
	public static final int POINTER_DRAGGED = 3;

	private static MultipointTouch instance;

	public static MultipointTouch getInstance() {
		if (instance == null) {
			instance = new MultipointTouch();
		}
		return instance;
	}

	static int getMaxPointers() {
		return 0;
	}

	static int getState(int pointerId) {
		return 0;
	}

	static int getX(int pointerId) {
		return 0;
	}

	static int getY(int pointerId) {
		return 0;
	}

	public void addMultipointTouchListener(MultipointTouchListener listener) {

	}

	public void removeMultipointTouchListener(MultipointTouchListener listener) {

	}

}
