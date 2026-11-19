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
		this.imageModeX = this.imageModeY = BORDER;
	}

	public void setColorClearEnable(boolean enable) {
		this.colorClear = enable;
	}

	public boolean isColorClearEnabled() {
		return this.colorClear;
	}

	public void setDepthClearEnable(boolean enable) {
		this.depthClear = enable;
	}

	public boolean isDepthClearEnabled() {
		return this.depthClear;
	}

	public void setColor(int ARGB) {
		this.color = ARGB;
	}

	public int getColor() {
		return this.color;
	}

	public void setImage(Image2D image) {
		if (image != null && image.getFormat() != Image2D.RGB && image.getFormat() != Image2D.RGBA) {
			throw new IllegalArgumentException();
		} else {
			this.removeReference(this.image);
			this.image = image;
			this.addReference(this.image);
			if (image != null) {
				this.cropX = 0;
				this.cropY = 0;
				this.cropWidth = image.getWidth();
				this.cropHeight = image.getHeight();
			}

		}
	}

	public Image2D getImage() {
		return this.image;
	}

	public void setImageMode(int modeX, int modeY) {
		if (modeX != BORDER && modeX != REPEAT) {
			throw new IllegalArgumentException();
		} else if (modeY != BORDER && modeY != REPEAT) {
			throw new IllegalArgumentException();
		} else {
			this.imageModeX = modeX;
			this.imageModeY = modeY;
		}
	}

	public int getImageModeX() {
		return this.imageModeX;
	}

	public int getImageModeY() {
		return this.imageModeY;
	}

	public void setCrop(int cropX, int cropY, int width, int height) {
		if (width >= 0 && height >= 0) {
			this.cropX = cropX;
			this.cropY = cropY;
			this.cropWidth = width;
			this.cropHeight = height;
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

	protected void updateProperty(int property, float[] value) {
		switch (property) {
		case AnimationTrack.ALPHA:
			this.color = this.color & 0xFFFFFF | G3DUtils.getIntColor(value) & 0xFF000000;
			return;
		case AnimationTrack.AMBIENT_COLOR:
		default:
			super.updateProperty(property, value);
			break;
		case AnimationTrack.COLOR:
			this.color = this.color & 0xFF000000 | G3DUtils.getIntColor(value) & 0xFFFFFF;
			return;
		case AnimationTrack.CROP:
			this.cropX = G3DUtils.round(value[0]);
			this.cropY = G3DUtils.round(value[1]);
			if (value.length > 2) {
				this.cropWidth = Math.max(G3DUtils.round(value[2]), 0);
				this.cropHeight = Math.max(G3DUtils.round(value[3]), 0);
				return;
			}
		}

	}
}
