package emulator.graphics3D.m3g;

import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.Transform3D;
import emulator.graphics3D.lwjgl.Emulator3D;

import javax.microedition.m3g.*;
import java.util.Hashtable;
import java.util.Vector;

public final class MeshMorph {
	private static MeshMorph inst;
	private static MeshMorph viewInst;
	private Hashtable cacheTable = new Hashtable();
	public VertexBuffer morphed;
	private VertexArray[] tmpMorphTargets;
	private VertexArray positions;
	private VertexArray normals;
	private VertexArray colors;
	private VertexArray[] uvms;

	private float[] tmpPositions, tmpNormals;

	private float[] tmp = new float[4];
	private float[] tmpScaleBias = new float[4];
	private float[] maxPos = new float[3], minPos = new float[3];

	public static MeshMorph getInstance() {
		if (inst == null) {
			inst = new MeshMorph();
		}

		return inst;
	}

	public static MeshMorph getViewInstance() {
		if (viewInst == null) {
			viewInst = new MeshMorph();
		}

		return viewInst;
	}

	public final void clearCache() {
		cacheTable.clear();
	}

	public final VertexBuffer getMorphedVertexBuffer(Mesh mesh) {
		VertexBuffer cacheVB = (VertexBuffer) cacheTable.get(mesh);

		if (cacheVB != null) {
			morphed = cacheVB;
		} else {
			if (mesh instanceof MorphingMesh) {
				processMorph((MorphingMesh) mesh);
				cacheTable.put(mesh, morphed);

				return morphed;
			}

			if (mesh instanceof SkinnedMesh) {
				processSkinning((SkinnedMesh) mesh);
				cacheTable.put(mesh, morphed);

				return morphed;
			}

			morphed = mesh.getVertexBuffer();
		}

		return morphed;
	}

	private void processMorph(MorphingMesh mesh) {
		VertexBuffer meshVB = mesh.getVertexBuffer();
		setMorphVB(meshVB);

		int morphTargets = mesh.getMorphTargetCount();
		tmpMorphTargets = new VertexArray[morphTargets];

		float baseWeight = mesh.getBaseWeight();
		float[] weights = new float[morphTargets];
		mesh.getWeights(weights);

		if (positions != null) {
			int nullTargets = 0;

			for (int i = 0; i < morphTargets; ++i) {
				tmpMorphTargets[i] = mesh.getMorphTarget(i).getPositions((float[]) null);
				if (tmpMorphTargets[i] == null) nullTargets++;
			}

			if (nullTargets != morphTargets && nullTargets != 0) {
				throw new IllegalStateException();
			}

			if (nullTargets == 0) {
				positions.morph(tmpMorphTargets, meshVB.getPositions((float[]) null), weights, baseWeight);
			}
		} else {
			for (int i = 0; i < morphTargets; ++i) {
				if (mesh.getMorphTarget(i).getPositions((float[]) null) != null) {
					throw new IllegalStateException();
				}
			}
		}

		if (normals != null) {
			int nullTargets = 0;

			for (int i = 0; i < morphTargets; ++i) {
				tmpMorphTargets[i] = mesh.getMorphTarget(i).getNormals();
				if (tmpMorphTargets[i] == null) nullTargets++;
			}

			if (nullTargets != morphTargets && nullTargets != 0) {
				throw new IllegalStateException();
			}

			if (nullTargets == 0) {
				normals.morph(tmpMorphTargets, meshVB.getNormals(), weights, baseWeight);
			}
		} else {
			for (int i = 0; i < morphTargets; ++i) {
				if (mesh.getMorphTarget(i).getNormals() != null) {
					throw new IllegalStateException();
				}
			}
		}

		if (colors != null) {
			int nullTargets = 0;

			for (int i = 0; i < morphTargets; ++i) {
				tmpMorphTargets[i] = mesh.getMorphTarget(i).getColors();
				if (tmpMorphTargets[i] == null) nullTargets++;
			}

			if (nullTargets != morphTargets && nullTargets != 0) {
				throw new IllegalStateException();
			}

			if (nullTargets == 0) {
				colors.morphColors(tmpMorphTargets, meshVB.getColors(), weights, baseWeight);
			}
		} else {
			for (int i = 0; i < morphTargets; ++i) {
				if (mesh.getMorphTarget(i).getColors() != null) {
					throw new IllegalStateException();
				}
			}

			int defaultCol = meshVB.getDefaultColor();

			float a = baseWeight * (defaultCol >> 24 & 255);
			float r = baseWeight * (defaultCol >> 16 & 255);
			float g = baseWeight * (defaultCol >> 8 & 255);
			float b = baseWeight * (defaultCol & 255);

			for (int i = 0; i < morphTargets; ++i) {
				int col = mesh.getMorphTarget(i).getDefaultColor();

				a += weights[i] * (col >> 24 & 255);
				r += weights[i] * (col >> 16 & 255);
				g += weights[i] * (col >> 8 & 255);
				b += weights[i] * (col & 255);
			}


			defaultCol = (G3DUtils.limit(G3DUtils.round(a), 0, 255) << 24) |
					(G3DUtils.limit(G3DUtils.round(r), 0, 255) << 16) |
					(G3DUtils.limit(G3DUtils.round(g), 0, 255) << 8) |
					(G3DUtils.limit(G3DUtils.round(b), 0, 255) << 0);

			morphed.setDefaultColor(defaultCol);
		}

		for (int i = 0; i < uvms.length; ++i) {
			if (uvms[i] == null) {
				for (int t = 0; t < morphTargets; ++t) {
					if (mesh.getMorphTarget(t).getTexCoords(i, (float[]) null) != null) {
						throw new IllegalStateException();
					}
				}
			} else {
				int nullTargets = 0;

				for (int t = 0; t < morphTargets; ++t) {
					tmpMorphTargets[t] = mesh.getMorphTarget(t).getTexCoords(i, (float[]) null);
					if (tmpMorphTargets[t] == null) nullTargets++;
				}

				if (nullTargets != morphTargets && nullTargets != 0) {
					throw new IllegalStateException();
				}

				if (nullTargets == 0) {
					uvms[i].morph(tmpMorphTargets, meshVB.getTexCoords(i, (float[]) null), weights, baseWeight);
				}
			}
		}
	}

	private void setMorphVB(VertexBuffer vb) {
		morphed = (VertexBuffer) vb.duplicate();

		if (vb.getPositions((float[]) null) != null) {
			positions = (VertexArray) vb.getPositions(tmpScaleBias).duplicate();

			tmp[0] = tmpScaleBias[1];
			tmp[1] = tmpScaleBias[2];
			tmp[2] = tmpScaleBias[3];

			morphed.setPositions(positions, tmpScaleBias[0], tmp);
		} else {
			positions = null;
		}

		if (vb.getNormals() != null) {
			normals = (VertexArray) vb.getNormals().duplicate();
			morphed.setNormals(normals);
		} else {
			normals = null;
		}

		if (vb.getColors() != null) {
			colors = (VertexArray) vb.getColors().duplicate();
			morphed.setColors(colors);
		} else {
			colors = null;
		}

		uvms = new VertexArray[Emulator3D.NumTextureUnits];

		for (int i = 0; i < uvms.length; ++i) {
			if (vb.getTexCoords(i, (float[]) null) != null) {
				uvms[i] = (VertexArray) vb.getTexCoords(i, tmpScaleBias).duplicate();

				tmp[0] = tmpScaleBias[1];
				tmp[1] = tmpScaleBias[2];
				tmp[2] = tmpScaleBias[3];

				morphed.setTexCoords(i, uvms[i], tmpScaleBias[0], tmp);
			} else {
				uvms[i] = null;
			}
		}
	}

	private void setSkinVB(VertexBuffer vb) {
		morphed = (VertexBuffer) vb.duplicate();

		int vertexCount = vb.getPositions((float[]) null).getVertexCount();
		if (positions == null || positions.getVertexCount() != vertexCount) {
			positions = new VertexArray(vertexCount, 3, 2);
		}

		morphed.setPositions(positions, 1.0F, (float[]) null);
		if (tmpPositions == null || tmpPositions.length < vertexCount * 3) {
			tmpPositions = new float[vertexCount * 3];
		}

		if (vb.getNormals() != null) {
			vertexCount = vb.getNormals().getVertexCount();
			if (normals == null || normals.getVertexCount() != vertexCount) {
				normals = new VertexArray(vertexCount, 3, 2);
			}

			morphed.setNormals(normals);
			if (tmpNormals == null || tmpNormals.length < vertexCount * 3) {
				tmpNormals = new float[vertexCount * 3];
			}
		}

	}

	private void processSkinning(SkinnedMesh mesh) {
		VertexBuffer meshVB = mesh.getVertexBuffer();
		setSkinVB(meshVB);

		VertexArray meshPoses = meshVB.getPositions(tmpScaleBias);
		if (meshPoses == null) {
			throw new IllegalStateException();
		}

		VertexArray meshNorms = meshVB.getNormals();
		int vertexCount = meshVB.getVertexCount();
		Vector boneTransList = mesh.getTransforms();

		for (int i = 0; i < boneTransList.size(); i++) {
			BoneTransform weight = (BoneTransform) boneTransList.elementAt(i);

			if (!weight.bone.getTransformTo(mesh, weight.posTrans)) {
				throw new IllegalStateException();
			}
			weight.posTrans.postMultiply(weight.toBoneTrans);

			if (meshNorms != null) {
				weight.normTrans.set(weight.posTrans);
				((Transform3D) weight.normTrans.getImpl()).invert();
				weight.normTrans.transpose();
			}
		}

		int[] vtxBones = mesh.getVerticesBones();
		int[] vtxWeights = mesh.getVerticesWeights();

		short[] shortPoses = meshPoses.getShortValues();
		byte[] bytePoses = meshPoses.getByteValues();

		short[] shortNorms = meshNorms == null ? null : meshNorms.getShortValues();
		byte[] byteNorms = meshNorms == null ? null : meshNorms.getByteValues();
		short[] normsOut = meshNorms == null ? null : normals.getShortValues();

		maxPos[0] = maxPos[1] = maxPos[2] = -Float.MAX_VALUE;
		minPos[0] = minPos[1] = minPos[2] = Float.MAX_VALUE;

		for (int i = 0; i < vertexCount; i++) {
			tmpPositions[i * 3] = tmpPositions[i * 3 + 1] = tmpPositions[i * 3 + 2] = 0;
			if (meshNorms != null) {
				tmpNormals[i * 3] = tmpNormals[i * 3 + 1] = tmpNormals[i * 3 + 2] = 0;
			}

			float weightSumm = 0;

			for (int slot = 0; slot < Emulator3D.MaxTransformsPerVertex; slot++) {
				int boneTransId = vtxBones[i * Emulator3D.MaxTransformsPerVertex + slot];

				if (boneTransId == 0) break; //no more active bone slots

				BoneTransform boneTrans = (BoneTransform) boneTransList.elementAt(boneTransId - 1);

				int boneWeight = vtxWeights[i * Emulator3D.MaxTransformsPerVertex + slot];
				weightSumm += boneWeight;

				for (int axis = 0; axis < 3; axis++) {
					if (meshPoses.getComponentType() == 2) {
						tmp[axis] = shortPoses[i * 3 + axis];
					} else {
						tmp[axis] = bytePoses[i * 3 + axis];
					}
				}

				tmp[0] = tmp[0] * tmpScaleBias[0] + tmpScaleBias[1];
				tmp[1] = tmp[1] * tmpScaleBias[0] + tmpScaleBias[2];
				tmp[2] = tmp[2] * tmpScaleBias[0] + tmpScaleBias[3];
				tmp[3] = 1;

				boneTrans.posTrans.transform(tmp);

				tmpPositions[i * 3 + 0] += tmp[0] * boneWeight;
				tmpPositions[i * 3 + 1] += tmp[1] * boneWeight;
				tmpPositions[i * 3 + 2] += tmp[2] * boneWeight;

				if (meshNorms != null) {
					for (int axis = 0; axis < 3; ++axis) {
						if (meshNorms.getComponentType() == 2) {
							tmp[axis] = (shortNorms[i * 3 + axis] + 32768) / 65535f - 0.5f;
						} else {
							tmp[axis] = (byteNorms[i * 3 + axis] + 128) / 255f - 0.5f;
						}
					}

					tmp[3] = 0;

					boneTrans.normTrans.transform(this.tmp);

					tmpNormals[i * 3 + 0] += tmp[0] * boneWeight;
					tmpNormals[i * 3 + 1] += tmp[1] * boneWeight;
					tmpNormals[i * 3 + 2] += tmp[2] * boneWeight;
				}
			}

			for (int axis = 0; axis < 3; axis++) {
				float posVal;

				if (weightSumm != 0) {
					posVal = tmpPositions[i * 3 + axis];
					posVal /= weightSumm;
				} else {
					if (meshPoses.getComponentType() == 2) {
						posVal = shortPoses[i * 3 + axis];
					} else {
						posVal = bytePoses[i * 3 + axis];
					}

					posVal = posVal * tmpScaleBias[0] + tmpScaleBias[axis + 1];
				}

				tmpPositions[i * 3 + axis] = posVal;

				if (posVal > maxPos[axis]) maxPos[axis] = posVal;
				if (posVal < minPos[axis]) minPos[axis] = posVal;

				if (meshNorms != null) {
					float normVal;

					if (weightSumm != 0) {
						normVal = tmpNormals[i * 3 + axis] / weightSumm;
					} else {
						if (meshNorms.getComponentType() == 2) {
							normVal = (shortNorms[i * 3 + axis] + 32768) / 65535f - 0.5f;
						} else {
							normVal = (byteNorms[i * 3 + axis] + 128) / 255f - 0.5f;
						}
					}

					normsOut[i * 3 + axis] = (short) G3DUtils.round((normVal + 0.5f) * 65535 - 32768);
				}
			}
		}

		float maxAxisSize = 0.0F;

		for (int axis = 0; axis < 3; ++axis) {
			tmp[axis] = (minPos[axis] + maxPos[axis]) / 2;

			float tmpScale = (maxPos[axis] - minPos[axis]) / 2;
			if (maxAxisSize < tmpScale) maxAxisSize = tmpScale;
		}

		float scale = maxAxisSize != 0.0F ? maxAxisSize / 32767.0F : 1.0F;

		short[] posesOut = positions.getShortValues();

		for (int i = 0; i < vertexCount; i++) {
			posesOut[i * 3 + 0] = (short) G3DUtils.round((tmpPositions[i * 3 + 0] - tmp[0]) / scale);
			posesOut[i * 3 + 1] = (short) G3DUtils.round((tmpPositions[i * 3 + 1] - tmp[1]) / scale);
			posesOut[i * 3 + 2] = (short) G3DUtils.round((tmpPositions[i * 3 + 2] - tmp[2]) / scale);
		}

		morphed.setPositions(positions, scale, tmp);
		if (meshNorms != null) morphed.setNormals(normals);
	}
}
