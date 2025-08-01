package com.nttdocomo.opt.ui.j3d;

public class PrimitiveArray {

	private final int type;
	private final int param;
	private final int num;

	public PrimitiveArray(int type, int param, int n) {
		this.type = type;
		this.param = param;
		this.num = n;
	}

	public int getType() {
		return type;
	}

	public int getParam() {
		return param;
	}

	public int size() {
		return num;
	}

	public int[] getVertexArray() {
		return null;
	}

	public int[] getColorArray() {
		return null;
	}

	public int[] getTextureCoordArray() {
		return null;
	}

	public int[] getPointSpriteArray() {
		return null;
	}
}
