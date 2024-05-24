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
    }

    public final Light getLight() {
        return this.jdField_a_of_type_ComMascotcapsuleMicro3dV3Light;
    }

    public final void setLight(Light paramLight) {
        this.jdField_a_of_type_ComMascotcapsuleMicro3dV3Light = paramLight;
    }

    /**
     * @deprecated
     */
    public final int getShading() {
        return getShadingType();
    }

    public final int getShadingType() {
        return this.jdField_a_of_type_Int;
    }

    /**
     * @deprecated
     */
    public final void setShading(int paramInt) {
        setShadingType(paramInt);
    }

    public final void setShadingType(int paramInt) {
    }

    /**
     * @deprecated
     */
    public final int getThreshold() {
        return getToonThreshold();
    }

    public final int getToonThreshold() {
        return this.b;
    }

    /**
     * @deprecated
     */
    public final int getThresholdHigh() {
        return getToonHigh();
    }

    public final int getToonHigh() {
        return this.c;
    }

    /**
     * @deprecated
     */
    public final int getThresholdLow() {
        return getToonLow();
    }

    public final int getToonLow() {
        return this.d;
    }

    /**
     * @deprecated
     */
    public final void setThreshold(int paramInt1, int paramInt2, int paramInt3) {
    }

    public final void setToonParams(int paramInt1, int paramInt2, int paramInt3) {
    }

    /**
     * @deprecated
     */
    public final boolean isSemiTransparentEnabled() {
        return isTransparency();
    }

    public final boolean isTransparency() {
        return this.jdField_a_of_type_Boolean;
    }

    /**
     * @deprecated
     */
    public final void setSemiTransparentEnabled(boolean paramBoolean) {
        setTransparency(paramBoolean);
    }

    public final void setTransparency(boolean paramBoolean) {
        this.jdField_a_of_type_Boolean = paramBoolean;
    }

    /**
     * @deprecated
     */
    public final Texture getSphereMap() {
        return getSphereTexture();
    }

    public final Texture getSphereTexture() {
        return this.jdField_a_of_type_ComMascotcapsuleMicro3dV3Texture;
    }

    /**
     * @deprecated
     */
    public final void setSphereMap(Texture paramTexture) {
        setSphereTexture(paramTexture);
    }

    public final void setSphereTexture(Texture paramTexture) {
    }
}
