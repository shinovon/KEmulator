package javax.microedition.m3g;

import emulator.graphics3D.Quaternion;

public abstract class Transformable extends Object3D {
	float[] scale = new float[3];
	float[] translation = new float[3];
	Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
	Transform transform = new Transform();

	Transformable() {
		this.scale[0] = this.scale[1] = this.scale[2] = 1.0F;
	}

	public void setOrientation(float angle, float ax, float ay, float az) {
		if (angle != 0.0F && ax == 0.0F && ay == 0.0F && az == 0.0F) {
			throw new IllegalArgumentException();
		} else {
			this.rotation.setAngleAxis(angle, ax, ay, az);
		}
	}

	public void getOrientation(float[] angleAxis) {
		if (angleAxis == null) {
			throw new NullPointerException();
		} else if (angleAxis.length < 4) {
			throw new IllegalArgumentException();
		} else {
			this.rotation.getAngleAxis(angleAxis);
		}
	}

	public void preRotate(float angle, float ax, float ay, float az) {
		if (angle != 0.0F && ax == 0.0F && ay == 0.0F && az == 0.0F) {
			throw new IllegalArgumentException();
		} else {
			Quaternion rotationDelta;
			(rotationDelta = new Quaternion()).setAngleAxis(angle, ax, ay, az);
			rotationDelta.mul(this.rotation);
			this.rotation.set(rotationDelta);
		}
	}

	public void postRotate(float angle, float ax, float ay, float az) {
		if (angle != 0.0F && ax == 0.0F && ay == 0.0F && az == 0.0F) {
			throw new IllegalArgumentException();
		} else {
			Quaternion rotationDelta;
			(rotationDelta = new Quaternion()).setAngleAxis(angle, ax, ay, az);
			this.rotation.mul(rotationDelta);
		}
	}

	public void setScale(float sx, float sy, float sz) {
		this.scale[0] = sx;
		this.scale[1] = sy;
		this.scale[2] = sz;
	}

	public void scale(float sx, float sy, float sz) {
		this.scale[0] *= sx;
		this.scale[1] *= sy;
		this.scale[2] *= sz;
	}

	public void getScale(float[] xyz) {
		if (xyz == null) {
			throw new NullPointerException();
		} else if (xyz.length < 3) {
			throw new IllegalArgumentException();
		} else {
			System.arraycopy(this.scale, 0, xyz, 0, 3);
		}
	}

	public void setTranslation(float tx, float ty, float tz) {
		this.translation[0] = tx;
		this.translation[1] = ty;
		this.translation[2] = tz;
	}

	public void translate(float tx, float ty, float tz) {
		this.translation[0] += tx;
		this.translation[1] += ty;
		this.translation[2] += tz;
	}

	public void getTranslation(float[] xyz) {
		if (xyz == null) {
			throw new NullPointerException();
		} else if (xyz.length < 3) {
			throw new IllegalArgumentException();
		} else {
			System.arraycopy(this.translation, 0, xyz, 0, 3);
		}
	}

	public void setTransform(Transform transform) {
		if (transform == null) {
			this.transform.setIdentity();
		} else {
			this.transform.set(transform);
		}
	}

	public void getTransform(Transform transform) {
		if (transform == null) {
			throw new NullPointerException();
		} else {
			transform.set(this.transform);
		}
	}

	public void getCompositeTransform(Transform result) {
		if (result == null) {
			throw new NullPointerException();
		} else {
			result.setIdentity();
			result.postTranslate(translation[0], translation[1], translation[2]);
			result.postRotateQuat(rotation.x, rotation.y, rotation.z, rotation.w);
			result.postScale(scale[0], scale[1], scale[2]);
			result.postMultiply(transform);
		}
	}

	protected void updateProperty(int property, float[] values) {
		switch (property) {
			case AnimationTrack.ORIENTATION:
				rotation.set(values);
				rotation.normalize();
				return;
			case AnimationTrack.SCALE:
				if (values.length == 1) {
					scale[0] = scale[1] = scale[2] = values[0];
				} else {
					scale[0] = values[0];
					scale[1] = values[1];
					scale[2] = values[2];
				}
				return;
			case AnimationTrack.TRANSLATION:
				translation[0] = values[0];
				translation[1] = values[1];
				translation[2] = values[2];
				return;
			default:
				super.updateProperty(property, values);
		}
	}

	protected Object3D duplicateObject() {
		Transformable copy;
		(copy = (Transformable) super.duplicateObject()).rotation = new Quaternion(this.rotation);
		copy.transform = new Transform(this.transform);
		copy.translation = (float[]) this.translation.clone();
		copy.scale = (float[]) this.scale.clone();
		return copy;
	}
}
