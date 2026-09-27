package emulator.graphics3D;

public class G3DUtils {

//    public static native boolean intelSSE_Invert4x4(float[] var0, float var1);

	public static boolean Invert4x4(float[] matrix) {
		float[] pairs = new float[12];
		float[] transposed = new float[16];
		float[] cofactors = new float[16];

		for (int i = 0; i < 4; ++i) {
			transposed[i + 0] = matrix[i * 4 + 0];
			transposed[i + 4] = matrix[i * 4 + 1];
			transposed[i + 8] = matrix[i * 4 + 2];
			transposed[i + 12] = matrix[i * 4 + 3];
		}

		pairs[0] = transposed[10] * transposed[15];
		pairs[1] = transposed[11] * transposed[14];
		pairs[2] = transposed[9] * transposed[15];
		pairs[3] = transposed[11] * transposed[13];
		pairs[4] = transposed[9] * transposed[14];
		pairs[5] = transposed[10] * transposed[13];
		pairs[6] = transposed[8] * transposed[15];
		pairs[7] = transposed[11] * transposed[12];
		pairs[8] = transposed[8] * transposed[14];
		pairs[9] = transposed[10] * transposed[12];
		pairs[10] = transposed[8] * transposed[13];
		pairs[11] = transposed[9] * transposed[12];
		cofactors[0] = pairs[0] * transposed[5] + pairs[3] * transposed[6] + pairs[4] * transposed[7];
		cofactors[0] -= pairs[1] * transposed[5] + pairs[2] * transposed[6] + pairs[5] * transposed[7];
		cofactors[1] = pairs[1] * transposed[4] + pairs[6] * transposed[6] + pairs[9] * transposed[7];
		cofactors[1] -= pairs[0] * transposed[4] + pairs[7] * transposed[6] + pairs[8] * transposed[7];
		cofactors[2] = pairs[2] * transposed[4] + pairs[7] * transposed[5] + pairs[10] * transposed[7];
		cofactors[2] -= pairs[3] * transposed[4] + pairs[6] * transposed[5] + pairs[11] * transposed[7];
		cofactors[3] = pairs[5] * transposed[4] + pairs[8] * transposed[5] + pairs[11] * transposed[6];
		cofactors[3] -= pairs[4] * transposed[4] + pairs[9] * transposed[5] + pairs[10] * transposed[6];
		cofactors[4] = pairs[1] * transposed[1] + pairs[2] * transposed[2] + pairs[5] * transposed[3];
		cofactors[4] -= pairs[0] * transposed[1] + pairs[3] * transposed[2] + pairs[4] * transposed[3];
		cofactors[5] = pairs[0] * transposed[0] + pairs[7] * transposed[2] + pairs[8] * transposed[3];
		cofactors[5] -= pairs[1] * transposed[0] + pairs[6] * transposed[2] + pairs[9] * transposed[3];
		cofactors[6] = pairs[3] * transposed[0] + pairs[6] * transposed[1] + pairs[11] * transposed[3];
		cofactors[6] -= pairs[2] * transposed[0] + pairs[7] * transposed[1] + pairs[10] * transposed[3];
		cofactors[7] = pairs[4] * transposed[0] + pairs[9] * transposed[1] + pairs[10] * transposed[2];
		cofactors[7] -= pairs[5] * transposed[0] + pairs[8] * transposed[1] + pairs[11] * transposed[2];
		pairs[0] = transposed[2] * transposed[7];
		pairs[1] = transposed[3] * transposed[6];
		pairs[2] = transposed[1] * transposed[7];
		pairs[3] = transposed[3] * transposed[5];
		pairs[4] = transposed[1] * transposed[6];
		pairs[5] = transposed[2] * transposed[5];
		pairs[6] = transposed[0] * transposed[7];
		pairs[7] = transposed[3] * transposed[4];
		pairs[8] = transposed[0] * transposed[6];
		pairs[9] = transposed[2] * transposed[4];
		pairs[10] = transposed[0] * transposed[5];
		pairs[11] = transposed[1] * transposed[4];
		cofactors[8] = pairs[0] * transposed[13] + pairs[3] * transposed[14] + pairs[4] * transposed[15];
		cofactors[8] -= pairs[1] * transposed[13] + pairs[2] * transposed[14] + pairs[5] * transposed[15];
		cofactors[9] = pairs[1] * transposed[12] + pairs[6] * transposed[14] + pairs[9] * transposed[15];
		cofactors[9] -= pairs[0] * transposed[12] + pairs[7] * transposed[14] + pairs[8] * transposed[15];
		cofactors[10] = pairs[2] * transposed[12] + pairs[7] * transposed[13] + pairs[10] * transposed[15];
		cofactors[10] -= pairs[3] * transposed[12] + pairs[6] * transposed[13] + pairs[11] * transposed[15];
		cofactors[11] = pairs[5] * transposed[12] + pairs[8] * transposed[13] + pairs[11] * transposed[14];
		cofactors[11] -= pairs[4] * transposed[12] + pairs[9] * transposed[13] + pairs[10] * transposed[14];
		cofactors[12] = pairs[2] * transposed[10] + pairs[5] * transposed[11] + pairs[1] * transposed[9];
		cofactors[12] -= pairs[4] * transposed[11] + pairs[0] * transposed[9] + pairs[3] * transposed[10];
		cofactors[13] = pairs[8] * transposed[11] + pairs[0] * transposed[8] + pairs[7] * transposed[10];
		cofactors[13] -= pairs[6] * transposed[10] + pairs[9] * transposed[11] + pairs[1] * transposed[8];
		cofactors[14] = pairs[6] * transposed[9] + pairs[11] * transposed[11] + pairs[3] * transposed[8];
		cofactors[14] -= pairs[10] * transposed[11] + pairs[2] * transposed[8] + pairs[7] * transposed[9];
		cofactors[15] = pairs[10] * transposed[10] + pairs[4] * transposed[8] + pairs[9] * transposed[9];
		cofactors[15] -= pairs[8] * transposed[9] + pairs[11] * transposed[10] + pairs[5] * transposed[8];
		float determinant = transposed[0] * cofactors[0] + transposed[1] * cofactors[1] + transposed[2] * cofactors[2] + transposed[3] * cofactors[3];
		determinant = 1.0F / determinant;

		for (int i = 0; i < 16; ++i) {
			matrix[i] = cofactors[i] * determinant;
		}

		return true;
	}

	public static float getFloatColor(int color, int shift) {
		return (float) (color >> shift & 255) / 255.0F;
	}

	public static void fillFloatColor(float[] rgba, int argb) {
		rgba[0] = (float) (argb >> 16 & 255) / 255.0F;
		rgba[1] = (float) (argb >> 8 & 255) / 255.0F;
		rgba[2] = (float) (argb & 255) / 255.0F;
		rgba[3] = (float) (argb >> 24 & 255) / 255.0F;
	}

	public static int getIntColor(float[] rgba) {
		float[] alphaSource;
		int blue;
		byte alphaIndex;
		int green;
		int red;
		int alpha;
		if (rgba.length == 1) {
			blue = 255;
			green = 255;
			red = 255;
			alphaSource = rgba;
			alphaIndex = 0;
		} else {
			red = (int) (limit(rgba[0]) * 255.0F + 0.5F);
			green = (int) (limit(rgba[1]) * 255.0F + 0.5F);
			blue = (int) (limit(rgba[2]) * 255.0F + 0.5F);
			if (rgba.length != 4) {
				alpha = 255;
				return (alpha << 24) + (red << 16) + (green << 8) + blue;
			}

			alphaSource = rgba;
			alphaIndex = 3;
		}

		alpha = (int) (limit(alphaSource[alphaIndex]) * 255.0F + 0.5F);
		return (alpha << 24) + (red << 16) + (green << 8) + blue;
	}

	public static float limitPositive(float value) {
		return value >= 0.0F ? value : 0.0F;
	}

	public static float limit(float value) {
		return value >= 0.0F ? (value <= 1.0F ? value : 1.0F) : 0.0F;
	}

	public static float limit(float value, float min, float max) {
		return value >= min ? (value <= max ? value : max) : min;
	}

	public static int limit(int value, int min, int max) {
		return value >= min ? (value <= max ? value : max) : min;
	}

	public static int round(float value) {
		return value >= 0.0F ? (int) (value + 0.5F) : (int) (value - 0.5F);
	}

	public static final boolean intersectRectangle(int x1, int y1, int width1, int height1, int x2, int y2, int width2, int height2, int[] result) {
		int start = x1 < x2 ? x2 : x1;
		int endOrigin;
		int endLength;
		if (x1 + width1 > x2 + width2) {
			endOrigin = x2;
			endLength = width2;
		} else {
			endOrigin = x1;
			endLength = width1;
		}

		int end;
		if ((end = endOrigin + endLength) - start < 0) {
			return false;
		} else {
			result[0] = start;
			result[2] = end - start;
			start = y1 < y2 ? y2 : y1;
			if (y1 + height1 > y2 + height2) {
				endOrigin = y2;
				endLength = height2;
			} else {
				endOrigin = y1;
				endLength = height1;
			}

			if ((end = endOrigin + endLength) - start < 0) {
				return false;
			} else {
				result[1] = start;
				result[3] = end - start;
				return true;
			}
		}
	}

	public static final boolean intersectTriangle(Vector4f rayOrigin, Vector4f rayDirection, Vector4f vertex0, Vector4f vertex1, Vector4f vertex2, Vector4f result, int cullMode) {
		Vector4f edge1 = new Vector4f();
		Vector4f edge2 = new Vector4f();
		Vector4f tVec = new Vector4f();
		Vector4f pVec = new Vector4f();
		Vector4f qVec = new Vector4f();
		edge1.sub(vertex1, vertex0);
		edge2.sub(vertex2, vertex0);
		pVec.cross(rayDirection, edge2);
		float determinant = edge1.dot(pVec);
		if (cullMode == 0 && determinant <= 0.0F) {
			return false;
		} else if (cullMode == 1 && determinant >= 0.0F) {
			return false;
		} else if (determinant > -1.0E-5F && determinant < 1.0E-5F) {
			return false;
		} else {
			float invDeterminant = 1.0F / determinant;
			tVec.sub(rayOrigin, vertex0);
			result.y = tVec.dot(pVec) * invDeterminant;
			if (result.y >= 0.0F && result.y <= 1.0F) {
				qVec.cross(tVec, edge1);
				result.z = rayDirection.dot(qVec) * invDeterminant;
				if (result.z >= 0.0F && result.y + result.z <= 1.0F) {
					result.x = edge2.dot(qVec) * invDeterminant;
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
}
