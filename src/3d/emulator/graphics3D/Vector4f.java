package emulator.graphics3D;

public final class Vector4f {
	public static final Vector4f X_AXIS = new Vector4f(1.0F, 0.0F, 0.0F, 0.0F);
	public static final Vector4f Y_AXIS = new Vector4f(0.0F, 1.0F, 0.0F, 0.0F);
	public static final Vector4f Z_AXIS = new Vector4f(0.0F, 0.0F, 1.0F, 0.0F);
	public static final Vector4f ORIGIN = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);

	public float x;
	public float y;
	public float z;
	public float w;

	public Vector4f() {
	}

	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4f(Vector4f vec) {
		x = vec.x;
		y = vec.y;
		z = vec.z;
		w = vec.w;
	}

	public Vector4f(float[] vec) {
		x = vec[0];
		y = vec[1];
		z = vec[2];
		w = vec[3];
	}

	public final void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public final void set(Vector4f vec) {
		x = vec.x;
		y = vec.y;
		z = vec.z;
		w = vec.w;
	}

	public final float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public final void mul(float factor) {
		x *= factor;
		y *= factor;
		z *= factor;
		w *= factor;
	}

	public final void add(Vector4f vec) {
		x += vec.x;
		y += vec.y;
		z += vec.z;
		w += vec.w;
	}

	public final void sub(Vector4f vec) {
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
		w -= vec.w;
	}

	public final void sub(Vector4f vec1, Vector4f vec2) {
		x = vec1.x - vec2.x;
		y = vec1.y - vec2.y;
		z = vec1.z - vec2.z;
		w = vec1.w - vec2.w;
	}

	public final float dot(Vector4f vec) {
		return x * vec.x + y * vec.y + z * vec.z + w * vec.w;
	}

	public final void cross(Vector4f vec1, Vector4f vec2) {
		x = vec1.y * vec2.z - vec1.z * vec2.y;
		y = vec1.z * vec2.x - vec1.x * vec2.z;
		z = vec1.x * vec2.y - vec1.y * vec2.x;
		w = 0;
	}

	public final boolean normalize() {
		float length = x * x + y * y + z * z + w * w;

		if (length < 1.0E-5F) return false;

		float invLen = 1.0F / (float) Math.sqrt(length);
		x *= invLen;
		y *= invLen;
		z *= invLen;
		w *= invLen;
		return true;
	}

	public static void mul(float[] vec, float factor) {
		vec[0] *= factor;
		vec[1] *= factor;
		vec[2] *= factor;
		vec[3] *= factor;
	}

	public static void sub(float[] a, float[] b) {
		a[0] -= b[0];
		a[1] -= b[1];
		a[2] -= b[2];
		a[3] -= b[3];
	}

	public static void add(float[] a, float[] b) {
		a[0] += b[0];
		a[1] += b[1];
		a[2] += b[2];
		a[3] += b[3];
	}

	public static float length(float[] vec) {
		return (float) Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
	}
}
