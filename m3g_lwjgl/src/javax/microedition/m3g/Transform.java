package javax.microedition.m3g;

import emulator.graphics3D.Transform3D;

public class Transform {
	private Transform3D impl;

	public Transform() {
		this.impl = new Transform3D();
	}

	public Transform(Transform transform) {
		if (transform == null) {
			throw new NullPointerException();
		} else {
			this.impl = new Transform3D();
			this.impl.set(transform.impl);
		}
	}

	Transform3D getImpl_() {
		return this.impl;
	}

	public Object getImpl() {
		return this.impl;
	}

	public void setIdentity() {
		this.impl.setIdentity();
	}

	public void set(Transform transform) {
		if (transform == null) {
			throw new NullPointerException();
		} else {
			this.impl.set(transform.impl);
		}
	}

	public void set(float[] matrix) {
		if (matrix == null) {
			throw new NullPointerException();
		} else if (matrix.length < 16) {
			throw new IllegalArgumentException();
		} else {
			this.impl.set(matrix);
		}
	}

	public void get(float[] matrix) {
		if (matrix == null) {
			throw new NullPointerException();
		} else if (matrix.length < 16) {
			throw new IllegalArgumentException();
		} else {
			this.impl.get(matrix);
		}
	}

	public void invert() {
		this.impl.invert();
	}

	public void transpose() {
		this.impl.transpose();
	}

	public void postMultiply(Transform transform) {
		if (transform == null) {
			throw new NullPointerException();
		} else {
			this.impl.postMultiply(transform.impl, false);
		}
	}

	public void preMultiply(Transform transform) {
		this.impl.postMultiply(transform.impl, true);
	}

	public void postScale(float sx, float sy, float sz) {
		this.impl.postScale(sx, sy, sz);
	}

	public void postRotate(float angle, float ax, float ay, float az) {
		if (ax == 0.0F && ay == 0.0F && az == 0.0F && angle != 0.0F) {
			throw new IllegalArgumentException();
		} else {
			this.impl.postRotate(angle, ax, ay, az);
		}
	}

	public void postRotateQuat(float qx, float qy, float qz, float qw) {
		if (qx == 0.0F && qy == 0.0F && qz == 0.0F && qw == 0.0F) {
			throw new IllegalArgumentException();
		} else {
			this.impl.postRotateQuat(qx, qy, qz, qw);
		}
	}

	public void postTranslate(float tx, float ty, float tz) {
		this.impl.postTranslate(tx, ty, tz);
	}

	public void transform(VertexArray in, float[] out, boolean W) {
		if (in != null && out != null) {
			if (in.getComponentCount() != 4 && out.length >= 4 * in.getVertexCount()) {
				int w = W ? 1 : 0;
				int vertexCount = in.getVertexCount();
				int componentCount = in.getComponentCount();
				int outLength = vertexCount * 4;
				int outIndex = 0;
				int inIndex = 0;
				if (in.getComponentType() == 1) {
					byte[] bytes = new byte[vertexCount * componentCount];
					in.get(0, vertexCount, bytes);

					while (outIndex < outLength) {
						if (outIndex % 4 < componentCount) {
							out[outIndex++] = (float) bytes[inIndex++];
						} else {
							out[outIndex++] = (float) w;
						}
					}
				} else {
					short[] shorts = new short[vertexCount * componentCount];
					in.get(0, vertexCount, shorts);

					while (outIndex < outLength) {
						if (outIndex % 4 < componentCount) {
							out[outIndex++] = (float) shorts[inIndex++];
						} else {
							out[outIndex++] = (float) w;
						}
					}
				}

				this.impl.transform(out);
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			throw new NullPointerException();
		}
	}

	public void transform(float[] vectors) {
		if (vectors == null) {
			throw new NullPointerException();
		} else if (vectors.length % 4 != 0) {
			throw new IllegalArgumentException();
		} else {
			this.impl.transform(vectors);
		}
	}
}
