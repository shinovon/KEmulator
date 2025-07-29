package com.nttdocomo.opt.ui;

import com.nttdocomo.ui.Image;
import com.nttdocomo.ui.UIException;

public class SpriteSet {
	private static int MAX_SPRITES = 32;
	Sprite[] spriteList;
	int numSprites;

	public SpriteSet(Sprite[] paramArrayOfSprite) {
		int i = paramArrayOfSprite.length;
		if ((i == 0) || (i > 32)) {
			throw new IllegalArgumentException();
		}
		this.numSprites = i;
		this.spriteList = paramArrayOfSprite;
	}

	public int getCount() {
		return this.numSprites;
	}

	public Sprite[] getSprites() {
		return this.spriteList;
	}

	public Sprite getSprite(int paramInt) {
		return this.spriteList[paramInt];
	}

	public void setCollisionAll() {
		for (int i = 0; i < this.numSprites; i++) {
			setCollisionOf(i);
		}
	}

	public void setCollisionOf(int paramInt) {
		this.spriteList[paramInt].flags = 0;
		if (this.spriteList[paramInt].image == null) {
			return;
		}
		try {
			int i = this.spriteList[paramInt].image.getWidth();
		} catch (UIException localUIException1) {
			return;
		}
		if (!this.spriteList[paramInt].visible) {
			return;
		}
		for (int j = 0; j < this.numSprites; j++) {
			if ((j != paramInt) && (this.spriteList[j].visible) && (this.spriteList[j].image != null)) {
				try {
					int k = this.spriteList[j].image.getWidth();
				} catch (UIException localUIException2) {
					continue;
				}
				if (hasIntersect(paramInt, j)) {
					this.spriteList[paramInt].flags |= 1 << j;
				}
			}
		}
	}

	public boolean isCollision(int paramInt1, int paramInt2) {
		if ((paramInt1 < 0) || (paramInt1 >= this.numSprites) || (paramInt2 < 0) || (paramInt2 >= this.numSprites)) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return (this.spriteList[paramInt1].flags & 1 << paramInt2) != 0;
	}

	public int getCollisionFlag(int paramInt) {
		return this.spriteList[paramInt].flags;
	}

	private boolean hasIntersect(int paramInt1, int paramInt2) {
		int i = this.spriteList[paramInt1].dx;
		int j = this.spriteList[paramInt1].dy;
		int k = i + this.spriteList[paramInt1].getWidth() - 1;
		int m = j + this.spriteList[paramInt1].getHeight() - 1;
		int n = this.spriteList[paramInt2].dx;
		int i1 = this.spriteList[paramInt2].dy;
		int i2 = n + this.spriteList[paramInt2].getWidth() - 1;
		int i3 = i1 + this.spriteList[paramInt2].getHeight() - 1;

		return (k >= n) && (i2 >= i) && (m >= i1) && (i3 >= j);
	}
}
