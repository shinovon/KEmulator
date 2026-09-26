package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.lwjgl.Emulator3D;

public class Texture2D extends Transformable {
	public static final int FILTER_BASE_LEVEL = 208;
	public static final int FILTER_LINEAR = 209;
	public static final int FILTER_NEAREST = 210;
	public static final int FUNC_ADD = 224;
	public static final int FUNC_BLEND = 225;
	public static final int FUNC_DECAL = 226;
	public static final int FUNC_MODULATE = 227;
	public static final int FUNC_REPLACE = 228;
	public static final int WRAP_CLAMP = 240;
	public static final int WRAP_REPEAT = 241;
	private Image2D image;
	private int wrappingS;
	private int wrappingT;
	private int levelFilter;
	private int imageFilter;
	private int blending;
	private int blendColor;

	public Texture2D(Image2D image) {
		this.setImage(image);
		this.wrappingS = 241;
		this.wrappingT = 241;
		this.levelFilter = 208;
		this.imageFilter = 210;
		this.blending = 227;
		this.blendColor = 0;
	}

	private static boolean isPow2(int value) {
		return (value & value - 1) == 0;
	}

	public void setImage(Image2D image) {
		if (image == null) {
			throw new NullPointerException();
		} else {
			int width = image.getWidth();
			int height = image.getHeight();
			if (isPow2(width) && isPow2(height)) {
				if (width <= Emulator3D.MaxTextureDimension && height <= Emulator3D.MaxTextureDimension) {
					this.removeReference(this.image);
					this.image = image;
					this.addReference(this.image);
				} else {
					throw new IllegalArgumentException("the width or height of image exceeds the MaxTextureDimension :" + Emulator3D.MaxTextureDimension);
				}
			} else {
				throw new IllegalArgumentException("the width or height of image is not a positive power of two");
			}
		}
	}

	public Image2D getImage() {
		return this.image;
	}

	public void setFiltering(int levelFilter, int imageFilter) {
		if (levelFilter != 208 && levelFilter != 209 && levelFilter != 210) {
			throw new IllegalArgumentException();
		} else if (imageFilter != 209 && imageFilter != 210) {
			throw new IllegalArgumentException();
		} else {
			this.levelFilter = levelFilter;
			this.imageFilter = imageFilter;
		}
	}

	public int getLevelFilter() {
		return this.levelFilter;
	}

	public int getImageFilter() {
		return this.imageFilter;
	}

	public void setWrapping(int wrapS, int wrapT) {
		if (wrapS != 240 && wrapS != 241) {
			throw new IllegalArgumentException();
		} else if (wrapT != 240 && wrapT != 241) {
			throw new IllegalArgumentException();
		} else {
			this.wrappingS = wrapS;
			this.wrappingT = wrapT;
		}
	}

	public int getWrappingS() {
		return this.wrappingS;
	}

	public int getWrappingT() {
		return this.wrappingT;
	}

	public void setBlending(int func) {
		if (func >= 224 && func <= 228) {
			this.blending = func;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int getBlending() {
		return this.blending;
	}

	public void setBlendColor(int RGB) {
		this.blendColor = RGB;
	}

	public int getBlendColor() {
		return this.blendColor;
	}

	protected void updateProperty(int property, float[] values) {
		switch (property) {
			case 258:
				this.blendColor = G3DUtils.getIntColor(values);
				return;
			default:
				super.updateProperty(property, values);
		}
	}
}
