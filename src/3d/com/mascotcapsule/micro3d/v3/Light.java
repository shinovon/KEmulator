package com.mascotcapsule.micro3d.v3;

public class Light {
	private Vector3D jdField_a_of_type_ComMascotcapsuleMicro3dV3Vector3D;
	private int jdField_a_of_type_Int;
	private int b;

	public Light() {
		jdField_a_of_type_ComMascotcapsuleMicro3dV3Vector3D = new Vector3D(0, 0, 4096);
		jdField_a_of_type_Int = 4096;
		b = 0;
	}

	public Light(Vector3D paramVector3D, int paramInt1, int paramInt2) {
		if (paramVector3D == null) {
			throw new NullPointerException();
		}
		jdField_a_of_type_ComMascotcapsuleMicro3dV3Vector3D = paramVector3D;
		jdField_a_of_type_Int = paramInt1;
		b = paramInt2;
	}

	public final int getDirIntensity() {
		return getParallelLightIntensity();
	}

	public final int getParallelLightIntensity() {
		return jdField_a_of_type_Int;
	}

	public final void setDirIntensity(int paramInt) {
		setParallelLightIntensity(paramInt);
	}

	public final void setParallelLightIntensity(int paramInt) {
		jdField_a_of_type_Int = paramInt;
	}

	public final int getAmbIntensity() {
		return getAmbientIntensity();
	}

	public final int getAmbientIntensity() {
		return b;
	}

	public final void setAmbIntensity(int paramInt) {
		setAmbientIntensity(paramInt);
	}

	public final void setAmbientIntensity(int paramInt) {
		b = paramInt;
	}

	public final Vector3D getDirection() {
		return getParallelLightDirection();
	}

	public final Vector3D getParallelLightDirection() {
		return jdField_a_of_type_ComMascotcapsuleMicro3dV3Vector3D;
	}

	public final void setDirection(Vector3D paramVector3D) {
		setParallelLightDirection(paramVector3D);
	}

	public final void setParallelLightDirection(Vector3D paramVector3D) {
		if (paramVector3D == null) {
			throw new NullPointerException();
		}
		jdField_a_of_type_ComMascotcapsuleMicro3dV3Vector3D = paramVector3D;
	}
}