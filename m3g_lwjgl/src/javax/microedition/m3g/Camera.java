package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;

public class Camera extends Node {
	public static final int GENERIC = 48;
	public static final int PARALLEL = 49;
	public static final int PERSPECTIVE = 50;
	private int projectionType = 48;
	private Transform generic = new Transform();
	private float[] projection = new float[4];

	protected Object3D duplicateObject() {
		Camera copy;
		(copy = (Camera) super.duplicateObject()).generic = new Transform(this.generic);
		copy.projection = (float[]) this.projection.clone();
		return copy;
	}

	public Camera() {
		this.projection[0] = 2.0F;
		this.projection[1] = 1.0F;
		this.projection[2] = -1.0F;
		this.projection[3] = 1.0F;
	}

	public void setParallel(float height, float aspectRatio, float near, float far) {
		if (height > 0.0F && aspectRatio > 0.0F) {
			this.projection[0] = height;
			this.projection[1] = aspectRatio;
			this.projection[2] = near;
			this.projection[3] = far;
			this.projectionType = 49;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void setPerspective(float fovy, float aspectRatio, float near, float far) {
		if (fovy > 0.0F && fovy < 180.0F && aspectRatio > 0.0F && near > 0.0F && far > 0.0F) {
			this.projection[0] = fovy;
			this.projection[1] = aspectRatio;
			this.projection[2] = near;
			this.projection[3] = far;
			this.projectionType = 50;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void setGeneric(Transform transform) {
		if (transform == null) {
			throw new NullPointerException();
		} else {
			this.generic.set(transform);
			this.projectionType = 48;
		}
	}

	public int getProjection(Transform transform) {
		if (transform != null) {
			float height;
			float width;
			float depth;
			float[] matrix;
			if (this.projectionType == 49) {
				height = this.projection[0];
				width = this.projection[1] * height;
				if ((depth = this.projection[3] - this.projection[2]) == 0.0F) {
					throw new ArithmeticException("near == far");
				}

				(matrix = new float[16])[0] = 2.0F / width;
				matrix[5] = 2.0F / height;
				matrix[10] = -2.0F / depth;
				matrix[11] = -(this.projection[2] + this.projection[3]) / depth;
				matrix[15] = 1.0F;
				transform.set(matrix);
			} else if (this.projectionType == 50) {
				height = (float) Math.tan(Math.toRadians((double) (this.projection[0] / 2.0F)));
				width = this.projection[1] * height;
				if ((depth = this.projection[3] - this.projection[2]) == 0.0F) {
					throw new ArithmeticException("near == far");
				}

				(matrix = new float[16])[0] = 1.0F / width;
				matrix[5] = 1.0F / height;
				matrix[10] = -(this.projection[2] + this.projection[3]) / depth;
				matrix[11] = -2.0F * this.projection[2] * this.projection[3] / depth;
				matrix[14] = -1.0F;
				transform.set(matrix);
			} else {
				transform.set(this.generic);
			}
		}

		return this.projectionType;
	}

	public int getProjection(float[] params) {
		if (params != null && params.length < 4) {
			throw new IllegalArgumentException();
		} else {
			if (params != null && this.projectionType != 48) {
				System.arraycopy(this.projection, 0, params, 0, 4);
			}

			return this.projectionType;
		}
	}

	protected void updateProperty(int property, float[] values) {
		if (this.projectionType != 48) {
			switch (property) {
				case 263:
					this.projection[3] = this.projectionType != 50 ? values[0] : G3DUtils.limitPositive(values[0]);
					return;
				case 264:
					this.projection[0] = this.projectionType != 50 ? G3DUtils.limitPositive(values[0]) : G3DUtils.limit(values[0], 0.0F, 180.0F);
					return;
				case 265:
				case 266:
				default:
					break;
				case 267:
					this.projection[2] = this.projectionType != 50 ? values[0] : G3DUtils.limitPositive(values[0]);
					return;
			}
		}

		super.updateProperty(property, values);
	}

	protected boolean rayIntersect(int scope, float[] ray, RayIntersection ri, Transform transform) {
		return false;
	}
}
