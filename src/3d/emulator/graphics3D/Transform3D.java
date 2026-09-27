package emulator.graphics3D;

public final class Transform3D {
	public float[] m_matrix = new float[16];
	private static final float[] defaultMatrix = new float[]{1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};

	public Transform3D() {
		this.setIdentity();
	}

	public final void setIdentity() {
		System.arraycopy(defaultMatrix, 0, this.m_matrix, 0, 16);
	}

	public final void get(float[] matrix) {
		System.arraycopy(this.m_matrix, 0, matrix, 0, 16);
	}

	public final void set(float[] matrix) {
		System.arraycopy(matrix, 0, this.m_matrix, 0, 16);
	}

	public final void set(Transform3D transform) {
		System.arraycopy(transform.m_matrix, 0, this.m_matrix, 0, 16);
	}

	public final void invert() {
//        if (!G3DUtils.Invert4x4(this.m_matrix, 1.0E-10F)) {
		if (!G3DUtils.Invert4x4(this.m_matrix)) {
			throw new ArithmeticException();
		}
	}

	public final void transpose() {
		this.swapAt(1, 4);
		this.swapAt(2, 8);
		this.swapAt(3, 12);
		this.swapAt(7, 13);
		this.swapAt(11, 14);
		this.swapAt(6, 9);
	}

	private void swapAt(int a, int b) {
		float tmp = this.m_matrix[a];
		this.m_matrix[a] = this.m_matrix[b];
		this.m_matrix[b] = tmp;
	}

	public final void postMultiply(Transform3D transform, boolean preMultiply) {
		float[] result = new float[16];
		float[] left = (preMultiply ? this : transform).m_matrix;
		float[] right = (preMultiply ? transform : this).m_matrix;

		for (int row = 0; row < 4; ++row) {
			for (int column = 0; column < 4; ++column) {
				int columnOffset = column << 2;
				result[columnOffset + row] += left[0 + row] * right[columnOffset + 0];
				result[columnOffset + row] += left[4 + row] * right[columnOffset + 1];
				result[columnOffset + row] += left[8 + row] * right[columnOffset + 2];
				result[columnOffset + row] += left[12 + row] * right[columnOffset + 3];
			}
		}

		System.arraycopy(result, 0, this.m_matrix, 0, 16);
	}

	public final void postRotate(float angle, float ax, float ay, float az) {
		Quaternion rotation;
		(rotation = new Quaternion()).setAngleAxis(angle, ax, ay, az);
		this.postRotateQuat(rotation.x, rotation.y, rotation.z, rotation.w);
	}

	public final void postRotateQuat(float qx, float qy, float qz, float qw) {
		Quaternion quat;
		(quat = new Quaternion(qx, qy, qz, qw)).normalize();
		Transform3D rotation;
		float[] matrix = (rotation = new Transform3D()).m_matrix;
		float xx = quat.x * quat.x;
		float xy = quat.x * quat.y;
		float xz = quat.x * quat.z;
		float xw = quat.x * quat.w;
		float yy = quat.y * quat.y;
		float yz = quat.y * quat.z;
		float yw = quat.y * quat.w;
		float zz = quat.z * quat.z;
		float zw = quat.z * quat.w;
		matrix[0] = 1.0F - 2.0F * (yy + zz);
		matrix[1] = 2.0F * (xy - zw);
		matrix[2] = 2.0F * (xz + yw);
		matrix[4] = 2.0F * (xy + zw);
		matrix[5] = 1.0F - 2.0F * (xx + zz);
		matrix[6] = 2.0F * (yz - xw);
		matrix[8] = 2.0F * (xz - yw);
		matrix[9] = 2.0F * (yz + xw);
		matrix[10] = 1.0F - 2.0F * (xx + yy);
		this.postMultiply(rotation, false);
	}

	public final void postScale(float sx, float sy, float sz) {
		this.m_matrix[0] *= sx;
		this.m_matrix[1] *= sy;
		this.m_matrix[2] *= sz;
		this.m_matrix[4] *= sx;
		this.m_matrix[5] *= sy;
		this.m_matrix[6] *= sz;
		this.m_matrix[8] *= sx;
		this.m_matrix[9] *= sy;
		this.m_matrix[10] *= sz;
		this.m_matrix[12] *= sx;
		this.m_matrix[13] *= sy;
		this.m_matrix[14] *= sz;
	}

	public final void postTranslate(float tx, float ty, float tz) {
		this.m_matrix[3] += this.m_matrix[0] * tx + this.m_matrix[1] * ty + this.m_matrix[2] * tz;
		this.m_matrix[7] += this.m_matrix[4] * tx + this.m_matrix[5] * ty + this.m_matrix[6] * tz;
		this.m_matrix[11] += this.m_matrix[8] * tx + this.m_matrix[9] * ty + this.m_matrix[10] * tz;
		this.m_matrix[15] += this.m_matrix[12] * tx + this.m_matrix[13] * ty + this.m_matrix[14] * tz;
	}

	private void vecTransform(float[] vec, int i) {
		float x = this.m_matrix[0] * vec[i + 0] + this.m_matrix[1] * vec[i + 1] + this.m_matrix[2] * vec[i + 2] + this.m_matrix[3] * vec[i + 3];
		float y = this.m_matrix[4] * vec[i + 0] + this.m_matrix[5] * vec[i + 1] + this.m_matrix[6] * vec[i + 2] + this.m_matrix[7] * vec[i + 3];
		float z = this.m_matrix[8] * vec[i + 0] + this.m_matrix[9] * vec[i + 1] + this.m_matrix[10] * vec[i + 2] + this.m_matrix[11] * vec[i + 3];
		float w = this.m_matrix[12] * vec[i + 0] + this.m_matrix[13] * vec[i + 1] + this.m_matrix[14] * vec[i + 2] + this.m_matrix[15] * vec[i + 3];

		vec[i + 0] = x;
		vec[i + 1] = y;
		vec[i + 2] = z;
		vec[i + 3] = w;
	}

	public final void transform(float[] vectors) {
		for (int i = 0; i < vectors.length; i += 4) {
			this.vecTransform(vectors, i);
		}
	}

	public final void transform(Vector4f vector) {
		float x = this.m_matrix[0] * vector.x + this.m_matrix[1] * vector.y + this.m_matrix[2] * vector.z + this.m_matrix[3] * vector.w;
		float y = this.m_matrix[4] * vector.x + this.m_matrix[5] * vector.y + this.m_matrix[6] * vector.z + this.m_matrix[7] * vector.w;
		float z = this.m_matrix[8] * vector.x + this.m_matrix[9] * vector.y + this.m_matrix[10] * vector.z + this.m_matrix[11] * vector.w;
		float w = this.m_matrix[12] * vector.x + this.m_matrix[13] * vector.y + this.m_matrix[14] * vector.z + this.m_matrix[15] * vector.w;
		vector.x = x;
		vector.y = y;
		vector.z = z;
		vector.w = w;
	}
}
