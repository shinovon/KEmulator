package com.nokia.mid.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CustomItem;
import org.eclipse.swt.widgets.Control;

public class TactileFeedback {
	public static final int FEEDBACK_STYLE_BASIC = 1;
	public static final int FEEDBACK_STYLE_SENSITIVE = 2;

	public TactileFeedback() {
	}

	public void directFeedback(int style) throws IllegalArgumentException {

	}

	public boolean isTouchFeedbackSupported() {
		return false;
	}

	public void registerFeedbackArea(Object uiObject, int id, int x, int y, int width, int height, int style)
			throws IllegalArgumentException {
	}

	public void unregisterFeedbackArea(Object uiObject, int id) throws IllegalArgumentException {
	}

	public void removeFeedbackForComponent(Object uiObject) {
	}

	public void moveFeedbackAreaToFirstPriority(Object uiObject, int id) {
	}

	private boolean isValidControlType(Object obj) {
		if ((!(obj instanceof Canvas)) && (!(obj instanceof CustomItem)) && (!(obj instanceof Control))) {
			return false;
		}
		return true;
	}

	private int getControlHandle(Object uiObject) {
		return 0;
	}

	private int getControlType(Object uiObject) {
		if ((uiObject instanceof Canvas)) {
			return 1;
		}
		if ((uiObject instanceof CustomItem)) {
			return 2;
		}
		if ((uiObject instanceof Control)) {
			return 3;
		}
		return 0;
	}
}