package com.mascotcapsule.micro3d.v3;

public class Effect3D {
	public static final int NORMAL_SHADING = 0;
	public static final int TOON_SHADING = 1;
	private Light jdField_a_of_type_ComMascotcapsuleMicro3dV3Light;
	private int jdField_a_of_type_Int;
	private boolean jdField_a_of_type_Boolean;
	private Texture jdField_a_of_type_ComMascotcapsuleMicro3dV3Texture;
	private int b;
	private int c;
	private int d;

	public Effect3D() {
	}

	public Effect3D(Light paramLight, int paramInt, boolean paramBoolean, Texture paramTexture) {
		if ((paramInt != 0) && (paramInt != 1)) {
			throw new NullPointerException();
		}
		if ((paramTexture != null) && (jdField_a_of_type_Boolean)) {
			throw new NullPointerException();
		}
		jdField_a_of_type_ComMascotcapsuleMicro3dV3Light = paramLight;
		jdField_a_of_type_Int = paramInt;
		jdField_a_of_type_Boolean = paramBoolean;
		jdField_a_of_type_ComMascotcapsuleMicro3dV3Texture = paramTexture;
	}

	public final Light getLight() {
		return jdField_a_of_type_ComMascotcapsuleMicro3dV3Light;
	}

	public final void setLight(Light paramLight) {
		jdField_a_of_type_ComMascotcapsuleMicro3dV3Light = paramLight;
	}

	public final int getShading() {
		return getShadingType();
	}

	public final int getShadingType() {
		return jdField_a_of_type_Int;
	}

	public final void setShading(int paramInt) {
		setShadingType(paramInt);
	}

	public final void setShadingType(int paramInt) {
		if ((paramInt != 0) && (paramInt != 1)) {
			throw new NullPointerException();
		}
		jdField_a_of_type_Int = paramInt;
	}

	public final int getThreshold() {
		return getToonThreshold();
	}

	public final int getToonThreshold() {
		return b;
	}

	public final int getThresholdHigh() {
		return getToonHigh();
	}

	public final int getToonHigh() {
		return c;
	}

	public final int getThresholdLow() {
		return getToonLow();
	}

	public final int getToonLow() {
		return d;
	}

	public final void setThreshold(int paramInt1, int paramInt2, int paramInt3) {
		setToonParams(paramInt1, paramInt2, paramInt3);
	}

	public final void setToonParams(int paramInt1, int paramInt2, int paramInt3) {
		if ((paramInt1 < 0) || (paramInt1 > 255)) {
			throw new NullPointerException();
		}
		if ((paramInt2 < 0) || (paramInt2 > 255)) {
			throw new NullPointerException();
		}
		if ((paramInt3 < 0) || (paramInt3 > 255)) {
			throw new NullPointerException();
		}
		b = paramInt1;
		c = paramInt2;
		d = paramInt3;
	}

	public final boolean isSemiTransparentEnabled() {
		return isTransparency();
	}

	public final boolean isTransparency() {
		return jdField_a_of_type_Boolean;
	}

	public final void setSemiTransparentEnabled(boolean paramBoolean) {
		setTransparency(paramBoolean);
	}

	public final void setTransparency(boolean paramBoolean) {
		jdField_a_of_type_Boolean = paramBoolean;
	}

	public final Texture getSphereMap() {
		return getSphereTexture();
	}

	public final Texture getSphereTexture() {
		return jdField_a_of_type_ComMascotcapsuleMicro3dV3Texture;
	}

	public final void setSphereMap(Texture paramTexture) {
		setSphereTexture(paramTexture);
	}

	public final void setSphereTexture(Texture paramTexture) {
		if ((paramTexture != null) && (jdField_a_of_type_Boolean)) {
			throw new NullPointerException();
		}
		jdField_a_of_type_ComMascotcapsuleMicro3dV3Texture = paramTexture;
	}
}