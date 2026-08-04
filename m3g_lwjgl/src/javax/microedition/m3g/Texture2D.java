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

	public Texture2D(Image2D var1) {
		this.setImage(var1);
		this.wrappingS = 241;
		this.wrappingT = 241;
		this.levelFilter = 208;
		this.imageFilter = 210;
		this.blending = 227;
		this.blendColor = 0;
	}

	private static boolean isPow2(int var0) {
		return (var0 & var0 - 1) == 0;
	}

	public void setImage(Image2D var1) {
		if (var1 == null) {
			throw new NullPointerException();
		} else {
			int var2 = var1.getWidth();
			int var3 = var1.getHeight();
			if (isPow2(var2) && isPow2(var3)) {
				if (var2 <= Emulator3D.MaxTextureDimension && var3 <= Emulator3D.MaxTextureDimension) {
					this.removeReference(this.image);
					this.image = var1;
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

	public void setFiltering(int var1, int var2) {
		if (var1 != 208 && var1 != 209 && var1 != 210) {
			throw new IllegalArgumentException();
		} else if (var2 != 209 && var2 != 210) {
			throw new IllegalArgumentException();
		} else {
			this.levelFilter = var1;
			this.imageFilter = var2;
		}
	}

	public int getLevelFilter() {
		return this.levelFilter;
	}

	public int getImageFilter() {
		return this.imageFilter;
	}

	public void setWrapping(int var1, int var2) {
		if (var1 != 240 && var1 != 241) {
			throw new IllegalArgumentException();
		} else if (var2 != 240 && var2 != 241) {
			throw new IllegalArgumentException();
		} else {
			this.wrappingS = var1;
			this.wrappingT = var2;
		}
	}

	public int getWrappingS() {
		return this.wrappingS;
	}

	public int getWrappingT() {
		return this.wrappingT;
	}

	public void setBlending(int var1) {
		if (var1 >= 224 && var1 <= 228) {
			this.blending = var1;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int getBlending() {
		return this.blending;
	}

	public void setBlendColor(int var1) {
		this.blendColor = var1;
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
