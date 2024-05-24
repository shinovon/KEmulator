/*
 * Copyright 2020-2024 Yury Kharchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.woesss.j2me.micro3d;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Utils {
	static final String TAG = "micro3d";

	static void getSpriteVertex(float[] vertex, float angle, float halfW, float halfH) {
		angle *= MathUtil.TO_RADIANS;
		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);
		float x = vertex[0];
		float y = vertex[1];
		float z = vertex[2];
		float w = vertex[3];
		vertex[0] = -halfW * cos + halfH * -sin + x;
		vertex[1] = -halfW * sin + halfH *  cos + y;
		vertex[2] = z;
		vertex[3] = w;
		float bx = -halfW * cos + -halfH * -sin + x;
		float by = -halfW * sin + -halfH *  cos + y;
		vertex[4] = bx;
		vertex[5] = by;
		vertex[6] = z;
		vertex[7] = w;
		float cx = halfW * cos + halfH * -sin + x;
		float cy = halfW * sin + halfH *  cos + y;
		vertex[8] = cx;
		vertex[9] = cy;
		vertex[10] = z;
		vertex[11] = w;
		vertex[12] = cx;
		vertex[13] = cy;
		vertex[14] = z;
		vertex[15] = w;
		vertex[16] = bx;
		vertex[17] = by;
		vertex[18] = z;
		vertex[19] = w;
		vertex[20] = halfW * cos + -halfH * -sin + x;
		vertex[21] = halfW * sin + -halfH *  cos + y;
		vertex[22] = z;
		vertex[23] = w;
	}

	public static String getVersion() {
		return "asd";
	}

	public static void multiplyMV(float[] v, float[] m) {
		float x = v[4];
		float y = v[5];
		float z = v[6];
		float w = v[7];
		v[0] = x * m[0] + y * m[4] + z * m[ 8] + w * m[12];
		v[1] = x * m[1] + y * m[5] + z * m[ 9] + w * m[13];
		v[2] = x * m[2] + y * m[6] + z * m[10] + w * m[14];
		v[3] = x * m[3] + y * m[7] + z * m[11] + w * m[15];
	}

	static void fillBuffer(FloatBuffer buffer, FloatBuffer vertices, int[] indices) {
		buffer.rewind();
		float[] arr = new float[3];
		for (int index : indices) {
			vertices.position(index * 3);
			vertices.get(arr);
			buffer.put(arr);
		}
	}

	static void transform(FloatBuffer srcVertices, FloatBuffer dstVertices,
						  FloatBuffer srcNormals, FloatBuffer dstNormals,
						  ByteBuffer bonesBuf, float[] actionMatrices) {
		srcVertices.rewind();
		dstVertices.rewind();
		if (srcNormals != null) {
			srcNormals.rewind();
			dstNormals.rewind();
		}
		int bonesLen = bonesBuf.capacity() / ((12 + 2) * 4);
		int actionsLen = 0;
		if (actionMatrices != null) {
			actionsLen = actionMatrices.length / 12;
		}
		IntBuffer bones = bonesBuf.asIntBuffer();
		FloatBuffer boneMatrix = bonesBuf.asFloatBuffer();
		float[] boneMatrixTmp = new float[bonesLen * 12];
		float[] tmp = new float[12];
		for (int i = 0; i < bonesLen; ++i) {
			bones.position(i * 14);
			int boneLen = bones.get();
			int parent = bones.get();
			boneMatrix.position(i * 14 + 2);
			if (parent == -1) {
				boneMatrix.get(boneMatrixTmp, i * 12, 12);
			} else {
				boneMatrix.get(tmp);
				multiplyMM(boneMatrixTmp, i * 12, boneMatrixTmp, parent * 12, tmp, 0);
			}
			if (i < actionsLen) {
				multiplyMM(boneMatrixTmp, i * 12, boneMatrixTmp, i * 12, actionMatrices, i * 12);
			}
			for (int j = 0; j < boneLen; ++j) {
				multiplyMV(dstVertices, srcVertices, boneMatrixTmp, i * 12);

				if (srcNormals != null) {
					multiplyMN(dstNormals, srcNormals, boneMatrixTmp, i * 12);
				}
			}
		}
	}

	private static void multiplyMM(float[] resM, int resOff, float[] lm, int lOff, float[] rm, int rOff) {
		float l00 = lm[lOff     ];
		float l01 = lm[lOff +  1];
		float l02 = lm[lOff +  2];
		float l03 = lm[lOff +  3];
		float l04 = lm[lOff +  4];
		float l05 = lm[lOff +  5];
		float l06 = lm[lOff +  6];
		float l07 = lm[lOff +  7];
		float l08 = lm[lOff +  8];
		float l09 = lm[lOff +  9];
		float l10 = lm[lOff + 10];
		float l11 = lm[lOff + 11];
		float r00 = rm[rOff     ];
		float r01 = rm[rOff +  1];
		float r02 = rm[rOff +  2];
		float r03 = rm[rOff +  3];
		float r04 = rm[rOff +  4];
		float r05 = rm[rOff +  5];
		float r06 = rm[rOff +  6];
		float r07 = rm[rOff +  7];
		float r08 = rm[rOff +  8];
		float r09 = rm[rOff +  9];
		float r10 = rm[rOff + 10];
		float r11 = rm[rOff + 11];

		resM[resOff     ] = l00 * r00 + l01 * r04 + l02 * r08;
		resM[resOff +  1] = l00 * r01 + l01 * r05 + l02 * r09;
		resM[resOff +  2] = l00 * r02 + l01 * r06 + l02 * r10;
		resM[resOff +  3] = l00 * r03 + l01 * r07 + l02 * r11 + l03;
		resM[resOff +  4] = l04 * r00 + l05 * r04 + l06 * r08;
		resM[resOff +  5] = l04 * r01 + l05 * r05 + l06 * r09;
		resM[resOff +  6] = l04 * r02 + l05 * r06 + l06 * r10;
		resM[resOff +  7] = l04 * r03 + l05 * r07 + l06 * r11 + l07;
		resM[resOff +  8] = l08 * r00 + l09 * r04 + l10 * r08;
		resM[resOff +  9] = l08 * r01 + l09 * r05 + l10 * r09;
		resM[resOff + 10] = l08 * r02 + l09 * r06 + l10 * r10;
		resM[resOff + 11] = l08 * r03 + l09 * r07 + l10 * r11 + l11;
	}

	private static void multiplyMV(FloatBuffer dst, FloatBuffer src, float[] m, int mOffset) {
		float x = src.get();
		float y = src.get();
		float z = src.get();
		dst.put(x * m[mOffset    ] + y * m[mOffset + 1] + z * m[mOffset +  2] + m[mOffset +  3]);
		dst.put(x * m[mOffset + 4] + y * m[mOffset + 5] + z * m[mOffset +  6] + m[mOffset +  7]);
		dst.put(x * m[mOffset + 8] + y * m[mOffset + 9] + z * m[mOffset + 10] + m[mOffset + 11]);
	}

	private static void multiplyMN(FloatBuffer dst, FloatBuffer src, float[] m, int mOffset) {
		float x = src.get();
		float y = src.get();
		float z = src.get();
		dst.put(x * m[mOffset    ] + y * m[mOffset + 1] + z * m[mOffset +  2]);
		dst.put(x * m[mOffset + 4] + y * m[mOffset + 5] + z * m[mOffset +  6]);
		dst.put(x * m[mOffset + 8] + y * m[mOffset + 9] + z * m[mOffset + 10]);
	}
}
