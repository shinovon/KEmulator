package com.nec.graphics;

public final class SpriteSet {
	protected static int MAX_PRIORITY = 31;
	protected static int MAX_SPRITES = 32;
	protected int numSprites;
	protected Sprite[] spriteList;

	public SpriteSet(int num)
			throws IllegalArgumentException {
	}

	public SpriteSet(Sprite[] sprites, int num)
			throws NullPointerException, IllegalArgumentException {
	}

	public int getCount() {
		return 0;
	}

	public Sprite[] getSprites() {
		return null;
	}

	public Sprite getSprite(int index)
			throws ArrayIndexOutOfBoundsException {
		return null;
	}

	public void setCollisionAll()
			throws NullPointerException {
	}

	public void setCollisionOf(int i)
			throws NullPointerException, IllegalArgumentException {
	}

	public boolean isCollision(int index1, int index2)
			throws ArrayIndexOutOfBoundsException {
		return false;
	}

	public int getCollisionFlag(int index)
			throws ArrayIndexOutOfBoundsException {
		return 0;
	}

	public void setPriority(int i, int j)
			throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
	}
}
