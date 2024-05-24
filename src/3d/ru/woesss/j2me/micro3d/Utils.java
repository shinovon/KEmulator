/*
 * Copyright 2020-2023 Yury Kharchenko
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

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import emulator.graphics3D.Transform3D;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;

public class Utils {
	static final String TAG = "micro3d";

	private static ByteBuffer buffer;
	private static BufferedImage awtBufferImage;
	private static ImageData swtBufferImage;
	public static final PaletteData swtPalleteData = new PaletteData(-16777216, 16711680, '\uff00');
	private static int targetWidth;
	private static int targetHeight;

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
		for (int index : indices) {
			buffer.put(vertices.get(index * 3));
			buffer.put(vertices.get(index * 3 + 1));
			buffer.put(vertices.get(index * 3 + 2));
		}
		buffer.rewind();
	}

	static void transform(FloatBuffer srcVertices, FloatBuffer dstVertices,
								 FloatBuffer srcNormals, FloatBuffer dstNormals,
								 ByteBuffer boneMatrices, float[] actionMatrices) {
		float[] srcVert = new float[srcVertices.capacity()];
		float[] dstVert = new float[dstVertices.capacity()];

		for (int i = 0; i < srcVert.length; i++) srcVert[i] = srcVertices.get(i);

		float[] srcNorm = null;
		float[] dstNorm = null;
		if (srcNormals != null) {
			srcNorm = new float[srcNormals.capacity()];
			dstNorm = new float[dstNormals.capacity()];

			for (int i = 0; i < srcNorm.length; i++) srcNorm[i] = srcNormals.get(i);
		}

		int bonesLen = boneMatrices.capacity() / (14 * 4);
		Bone[] bones = new Bone[bonesLen];

		boneMatrices.rewind();

		// read bones
		for (int i = 0; i < bonesLen; i++) {
			Bone bone = bones[i] = new Bone();
			bone.length = boneMatrices.getInt((i*14) * 4);
			bone.parent = boneMatrices.getInt((i*14 + 1) * 4);
			Matrix mtx = bone.matrix = new Matrix();
			mtx.m00 = boneMatrices.getFloat((i*14 + 2) * 4);
			mtx.m01 = boneMatrices.getFloat((i*14 + 3) * 4);
			mtx.m02 = boneMatrices.getFloat((i*14 + 4) * 4);
			mtx.m03 = boneMatrices.getFloat((i*14 + 5) * 4);
			mtx.m10 = boneMatrices.getFloat((i*14 + 6) * 4);
			mtx.m11 = boneMatrices.getFloat((i*14 + 7) * 4);
			mtx.m12 = boneMatrices.getFloat((i*14 + 8) * 4);
			mtx.m13 = boneMatrices.getFloat((i*14 + 9) * 4);
			mtx.m20 = boneMatrices.getFloat((i*14 + 10) * 4);
			mtx.m21 = boneMatrices.getFloat((i*14 + 11) * 4);
			mtx.m22 = boneMatrices.getFloat((i*14 + 12) * 4);
			mtx.m23 = boneMatrices.getFloat((i*14 + 13) * 4);
		}

		int actionsLen = 0;
		Matrix[] actions = null;
		if (actionMatrices != null) {
			actionsLen = actionMatrices.length / 12;
			actions = new Matrix[actionsLen];
			for (int i = 0; i < actionsLen; i++) {
				Matrix mtx = actions[i] = new Matrix();
				mtx.m00 = actionMatrices[i*12];
				mtx.m01 = actionMatrices[i*12 + 1];
				mtx.m02 = actionMatrices[i*12 + 2];
				mtx.m03 = actionMatrices[i*12 + 3];
				mtx.m10 = actionMatrices[i*12 + 4];
				mtx.m11 = actionMatrices[i*12 + 5];
				mtx.m12 = actionMatrices[i*12 + 6];
				mtx.m13 = actionMatrices[i*12 + 7];
				mtx.m20 = actionMatrices[i*12 + 8];
				mtx.m21 = actionMatrices[i*12 + 9];
				mtx.m22 = actionMatrices[i*12 + 10];
				mtx.m23 = actionMatrices[i*12 + 11];
			}
		}
		int k = 0;

		Matrix[] tmp = new Matrix[bonesLen];
		for (int i = 0; i < bonesLen; i++) tmp[i] = new Matrix();

		for (int i = 0; i < bonesLen; i++) {
			Bone bone = bones[i];
			int parent = bone.parent;
			Matrix matrix = tmp[i];
			if (parent == -1) {
				matrix = bone.matrix;
			} else {
				matrix.multiply(tmp[parent], bone.matrix);
			}
			if (i < actionsLen) {
				matrix.multiply(actions[i]);
			}
			int boneLen = bone.length;
			for (int j = 0; j < boneLen; j++) {
				matrix.transformPoint(dstVert, srcVert, k);

				if (srcNormals != null) {
					matrix.transformVector(dstNorm, srcNorm, k);
				}
				++k;
			}
		}

		// write buffers
		boneMatrices.rewind();
		for (int i = 0; i < bonesLen; i++) {
			Bone bone = bones[i];
			boneMatrices.putInt(bone.length).putInt(bone.parent);

			Matrix mtx = bone.matrix;
			boneMatrices.putFloat(mtx.m00).putFloat(mtx.m01).putFloat(mtx.m02).putFloat(mtx.m03)
					.putFloat(mtx.m10).putFloat(mtx.m11).putFloat(mtx.m12).putFloat(mtx.m13)
					.putFloat(mtx.m20).putFloat(mtx.m21).putFloat(mtx.m22).putFloat(mtx.m23);
		}
		boneMatrices.rewind();

		dstVertices.rewind();
		for (float f : dstVert) {
			dstVertices.put(f);
		}
		dstVertices.rewind();

		if (dstNormals != null) {
			dstNormals.rewind();
			for (float f : dstNorm) {
				dstNormals.put(f);
			}
			dstNormals.rewind();
		}
	}

	// structs
	static class Matrix {
		float m00 = 1f;
		float m01;
		float m02;
		float m03;
		float m10;
		float m11 = 1f;
		float m12;
		float m13;
		float m20;
		float m21;
		float m22 = 1f;
		float m23;

		void multiply(Matrix lm, Matrix rm) {
			float l00 = lm.m00;
			float l01 = lm.m01;
			float l02 = lm.m02;
			float l10 = lm.m10;
			float l11 = lm.m11;
			float l12 = lm.m12;
			float l20 = lm.m20;
			float l21 = lm.m21;
			float l22 = lm.m22;
			float r00 = rm.m00;
			float r01 = rm.m01;
			float r02 = rm.m02;
			float r03 = rm.m03;
			float r10 = rm.m10;
			float r11 = rm.m11;
			float r12 = rm.m12;
			float r13 = rm.m13;
			float r20 = rm.m20;
			float r21 = rm.m21;
			float r22 = rm.m22;
			float r23 = rm.m23;

			m00 = l00 * r00 + l01 * r10 + l02 * r20;
			m01 = l00 * r01 + l01 * r11 + l02 * r21;
			m02 = l00 * r02 + l01 * r12 + l02 * r22;
			m03 = l00 * r03 + l01 * r13 + l02 * r23 + lm.m03;
			m10 = l10 * r00 + l11 * r10 + l12 * r20;
			m11 = l10 * r01 + l11 * r11 + l12 * r21;
			m12 = l10 * r02 + l11 * r12 + l12 * r22;
			m13 = l10 * r03 + l11 * r13 + l12 * r23 + lm.m13;
			m20 = l20 * r00 + l21 * r10 + l22 * r20;
			m21 = l20 * r01 + l21 * r11 + l22 * r21;
			m22 = l20 * r02 + l21 * r12 + l22 * r22;
			m23 = l20 * r03 + l21 * r13 + l22 * r23 + lm.m23;
		}

		void multiply(Matrix rm) {
			multiply(this, rm);
		}

		void transformPoint(float[] dst, float[] src, int idx) {
			transformVector(dst, src, idx);
			dst[idx*3] += m03;
			dst[idx*3 + 1] += m13;
			dst[idx*3 + 2] += m23;
		}

		void transformVector(float[] dst, float[] src, int idx) {
			float x = src[idx*3];
			float y = src[idx*3 + 1];
			float z = src[idx*3 + 2];
			dst[idx*3] = x * m00 + y * m01 + z * m02;
			dst[idx*3 + 1] = x * m10 + y * m11 + z * m12;
			dst[idx*3 + 2] = x * m20 + y * m21 + z * m22;
		}

		Transform3D toTransform3D() {
			Transform3D t = new Transform3D();
			t.set(new float[] {
					m00, m01, m02, m03,
					m10, m11, m12, m13,
					m20, m21, m22, m23,
					0f, 0f, 0f, 1f
			});

			return t;
		}
	}

	static class Bone {
		int length;
		int parent;
		Matrix matrix;
	}
}