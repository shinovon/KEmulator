package com.nttdocomo.opt.ui;

import com.nttdocomo.ui.Image;

public class ImageMap {
	int cell_width;
	int cell_height;
	int map_width;
	int map_height;
	int win_x;
	int win_y;
	int win_height;
	int win_width;
	Image[] images;
	byte[] mapData;

	protected ImageMap() {
	}

	public ImageMap(int paramInt1, int paramInt2) {
		if ((paramInt1 < 0) || (paramInt2 < 0)) {
			throw new IllegalArgumentException();
		}
		this.cell_width = paramInt1;
		this.cell_height = paramInt2;
		this.map_width = 0;
		this.map_height = 0;
		this.win_x = 0;
		this.win_y = 0;
		this.win_width = 0;
		this.win_height = 0;
		this.images = null;
		this.mapData = null;
	}

	public ImageMap(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, Image[] paramArrayOfImage) {
		if ((paramArrayOfByte == null) || (paramArrayOfImage == null)) {
			throw new NullPointerException();
		}
		if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt3 < 0) || (paramInt4 < 0) || (paramArrayOfByte.length < paramInt4 * paramInt3)) {
			throw new IllegalArgumentException();
		}
		this.cell_width = paramInt1;
		this.cell_height = paramInt2;
		this.map_width = paramInt3;
		this.map_height = paramInt4;
		this.win_x = 0;
		this.win_y = 0;
		this.win_width = paramInt3;
		this.win_height = paramInt4;
		this.images = paramArrayOfImage;
		this.mapData = paramArrayOfByte;
	}

	public void setImageMap(int paramInt1, int paramInt2, byte[] paramArrayOfByte, Image[] paramArrayOfImage) {
		if ((paramArrayOfByte == null) || (paramArrayOfImage == null)) {
			throw new NullPointerException();
		}
		if ((paramInt1 < 0) || (paramInt2 < 0) || (paramArrayOfByte.length < paramInt2 * paramInt1)) {
			throw new IllegalArgumentException();
		}
		this.mapData = paramArrayOfByte;
		this.map_width = paramInt1;
		this.map_height = paramInt2;
		this.images = paramArrayOfImage;
		this.win_x = 0;
		this.win_y = 0;
		this.win_width = paramInt1;
		this.win_height = paramInt2;
	}

	public void setWindow(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt3 <= 0) || (paramInt4 <= 0) || (paramInt1 + paramInt3 > this.map_width) || (paramInt2 + paramInt4 > this.map_height)) {
			throw new IllegalArgumentException();
		}
		this.win_x = paramInt1;
		this.win_y = paramInt2;
		this.win_width = paramInt3;
		this.win_height = paramInt4;
	}

	public void setWindowLocation(int paramInt1, int paramInt2) {
		if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + this.win_width > this.map_width) || (paramInt2 + this.win_height > this.map_height)) {
			throw new IllegalArgumentException();
		}
		setWindow(paramInt1, paramInt2, this.win_width, this.win_height);
	}

	public void moveWindowLocation(int paramInt1, int paramInt2) {
		if ((paramInt1 + this.win_x < 0) || (paramInt2 + this.win_y < 0) || (paramInt1 + this.win_x > this.map_width) || (paramInt2 + this.win_y > this.map_height)) {
			throw new IllegalArgumentException();
		}
		setWindow(this.win_x + paramInt1, this.win_y + paramInt2, this.win_width, this.win_height);
	}
}
