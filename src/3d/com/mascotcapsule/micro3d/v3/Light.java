package com.mascotcapsule.micro3d.v3;

public class Light {
    private Vector3D jdField_a_of_type_ComMascotcapsuleMicro3dV3Vector3D;
    private int jdField_a_of_type_Int;
    private int b;

    public Light() {
        this.jdField_a_of_type_ComMascotcapsuleMicro3dV3Vector3D = new Vector3D(0, 0, 4096);
        this.jdField_a_of_type_Int = 4096;
        this.b = 0;
    }

    public Light(Vector3D paramVector3D, int paramInt1, int paramInt2) {
        this.jdField_a_of_type_ComMascotcapsuleMicro3dV3Vector3D = paramVector3D;
        this.jdField_a_of_type_Int = paramInt1;
        this.b = paramInt2;
    }

    /**
     * @deprecated
     */
    public final int getDirIntensity() {
        return getParallelLightIntensity();
    }

    public final int getParallelLightIntensity() {
        return this.jdField_a_of_type_Int;
    }

    /**
     * @deprecated
     */
    public final void setDirIntensity(int paramInt) {
        setParallelLightIntensity(paramInt);
    }

    public final void setParallelLightIntensity(int paramInt) {
        this.jdField_a_of_type_Int = paramInt;
    }

    /**
     * @deprecated
     */
    public final int getAmbIntensity() {
        return getAmbientIntensity();
    }

    public final int getAmbientIntensity() {
        return this.b;
    }

    /**
     * @deprecated
     */
    public final void setAmbIntensity(int paramInt) {
        setAmbientIntensity(paramInt);
    }

    public final void setAmbientIntensity(int paramInt) {
        this.b = paramInt;
    }

    /**
     * @deprecated
     */
    public final Vector3D getDirection() {
        return getParallelLightDirection();
    }

    public final Vector3D getParallelLightDirection() {
        return this.jdField_a_of_type_ComMascotcapsuleMicro3dV3Vector3D;
    }

    /**
     * @deprecated
     */
    public final void setDirection(Vector3D paramVector3D) {
        setParallelLightDirection(paramVector3D);
    }

    public final void setParallelLightDirection(Vector3D paramVector3D) {
    }
}
