package com.nokia.mid.ui;

public abstract class CanvasItem {
	Object iParent;
	int iHeight;
	int iWidth;
	int iPositionX;
	int iPositionY;
	boolean iVisible;
	static final String ERROR_NOT_A_VALID_PARENT_OBJECT = "The object is not a valid parent object";
	static final String ERROR_SETTING_PARENT_OBJECT = "Error setting the parent object";
	static final String ERROR_GIVEN_ARGUMENTS_NOT_VALID = "The given arguments are not valid";
	static final String ERROR_NO_PARENT = "Parent has not been set";

	public void setParent(Object parent) {
		iParent = parent;
	}

	public synchronized Object getParent() {
		return this.iParent;
	}

	public void setSize(int width, int height) {
		iWidth = width;
		iHeight = height;
	}

	public int getHeight() {
		return this.iHeight;
	}

	public int getWidth() {
		return this.iWidth;
	}

	public void setPosition(int x, int y) {
	}

	public int getPositionX() {
		return this.iPositionX;
	}

	public int getPositionY() {
		return this.iPositionY;
	}

	public void setVisible(boolean visible) {
		iVisible = visible;
	}

	public boolean isVisible() {
		return this.iVisible;
	}

	public void setZPosition(int z) {
	}

	public int getZPosition() {
		return 0;
	}

	void checkParent() {
		if (this.iParent == null) {
			throw new IllegalStateException("Parent has not been set");
		}
	}
}