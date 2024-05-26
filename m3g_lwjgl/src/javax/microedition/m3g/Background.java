package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;

public class Background extends Object3D {
	public static final int BORDER = 32;
	public static final int REPEAT = 33;
	private boolean colorClear = true;
	private boolean depthClear = true;
	private int color = 0;
	private Image2D image = null;
	private int imageModeX;
	private int imageModeY;
	private int cropX;
	private int cropY;
	private int cropWidth;
	private int cropHeight;

	public Background() {
		this.imageModeX = this.imageModeY = 32;
	}

	public void setColorClearEnable(boolean var1) {
		this.colorClear = var1;
	}

	public boolean isColorClearEnabled() {
		return this.colorClear;
	}

	public void setDepthClearEnable(boolean var1) {
		this.depthClear = var1;
	}

	public boolean isDepthClearEnabled() {
		return this.depthClear;
	}

	public void setColor(int var1) {
		this.color = var1;
	}

	public int getColor() {
		return this.color;
	}

	public void setImage(Image2D var1) {
		if (var1 != null && var1.getFormat() != 99 && var1.getFormat() != 100) {
			throw new IllegalArgumentException();
		} else {
			this.removeReference(this.image);
			this.image = var1;
			this.addReference(this.image);
			if (var1 != null) {
				this.cropX = 0;
				this.cropY = 0;
				this.cropWidth = var1.getWidth();
				this.cropHeight = var1.getHeight();
			}

		}
	}

	public Image2D getImage() {
		return this.image;
	}

	public void setImageMode(int var1, int var2) {
		if (var1 != 32 && var1 != 33) {
			throw new IllegalArgumentException();
		} else if (var2 != 32 && var2 != 33) {
			throw new IllegalArgumentException();
		} else {
			this.imageModeX = var1;
			this.imageModeY = var2;
		}
	}

	public int getImageModeX() {
		return this.imageModeX;
	}

	public int getImageModeY() {
		return this.imageModeY;
	}

	public void setCrop(int var1, int var2, int var3, int var4) {
		if (var3 >= 0 && var4 >= 0) {
			this.cropX = var1;
			this.cropY = var2;
			this.cropWidth = var3;
			this.cropHeight = var4;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int getCropX() {
		return this.cropX;
	}

	public int getCropY() {
		return this.cropY;
	}

	public int getCropWidth() {
		return this.cropWidth;
	}

	public int getCropHeight() {
		return this.cropHeight;
	}

	protected void updateProperty(int var1, float[] var2) {
		switch (var1) {
			case 256:
				this.color = this.color & 16777215 | G3DUtils.getIntColor(var2) & -16777216;
				return;
			case 257:
			default:
				super.updateProperty(var1, var2);
				break;
			case 258:
				this.color = this.color & -16777216 | G3DUtils.getIntColor(var2) & 16777215;
				return;
			case 259:
				this.cropX = G3DUtils.round(var2[0]);
				this.cropY = G3DUtils.round(var2[1]);
				if (var2.length > 2) {
					this.cropWidth = Math.max(G3DUtils.round(var2[2]), 0);
					this.cropHeight = Math.max(G3DUtils.round(var2[3]), 0);
					return;
				}
		}

	}
}
