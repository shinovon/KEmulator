package javax.microedition.m3g;

import emulator.graphics3D.lwjgl.Emulator3D;
import emulator.graphics3D.m3g.BoneTransform;
import emulator.graphics3D.m3g.MeshMorph;

import java.util.Hashtable;
import java.util.Vector;

public class SkinnedMesh extends Mesh {
	Group skeleton;

	public Vector<BoneTransform> boneTransList;
	public int[] vtxBones; //0 - no bone, 1+ - bone from list
	public int[] vtxWeights;

	public SkinnedMesh(VertexBuffer vertices, IndexBuffer submesh, Appearance appearance, Group skeleton) {
		super(vertices, submesh, appearance);
		if (skeleton == null) {
			throw new NullPointerException();
		} else if (skeleton instanceof World || skeleton.getParent() != null) {
			throw new IllegalArgumentException();
		}

		this.skeleton = skeleton;
		this.skeleton.parent = this;
		this.addReference(this.skeleton);
		this.boneTransList = new Vector();

		vtxBones = new int[vertices.getVertexCount() * Emulator3D.MaxTransformsPerVertex];
		vtxWeights = new int[vertices.getVertexCount() * Emulator3D.MaxTransformsPerVertex];
	}

	public SkinnedMesh(VertexBuffer vertices, IndexBuffer[] submeshes, Appearance[] appearances, Group skeleton) {
		super(vertices, submeshes, appearances);
		if (skeleton == null) {
			throw new NullPointerException();
		} else if (skeleton instanceof World || skeleton.getParent() != null) {
			throw new IllegalArgumentException();
		}

		this.skeleton = skeleton;
		this.skeleton.parent = this;
		this.addReference(this.skeleton);
		this.boneTransList = new Vector();

		vtxBones = new int[vertices.getVertexCount() * Emulator3D.MaxTransformsPerVertex];
		vtxWeights = new int[vertices.getVertexCount() * Emulator3D.MaxTransformsPerVertex];
	}

	public Group getSkeleton() {
		return this.skeleton;
	}

	protected Object3D duplicateObject() {
		SkinnedMesh clone = (SkinnedMesh) super.duplicateObject();

		Group newSkeleton = (Group) clone.getSkeleton().duplicateObject();
		clone.removeReference(clone.skeleton);
		clone.addReference(newSkeleton);
		clone.skeleton = newSkeleton;
		newSkeleton.parent = clone;

		Hashtable<Node, Node> oldToNewBone = new Hashtable();
		getOldToNewBonesMapping(oldToNewBone, skeleton, newSkeleton);

		clone.boneTransList = new Vector();

		for (int i = 0; i < boneTransList.size(); i++) {
			BoneTransform oldBoneTrans = boneTransList.elementAt(i);
			BoneTransform newBoneTrans = new BoneTransform(oldToNewBone.get(oldBoneTrans.bone), oldBoneTrans.toBoneTrans);

			clone.boneTransList.add(newBoneTrans);
		}

		clone.vtxBones = vtxBones.clone();
		clone.vtxWeights = vtxWeights.clone();

		return clone;
	}

	private void getOldToNewBonesMapping(Hashtable map, Node oldNode, Node newNode) {
		map.put(oldNode, newNode);

		if (oldNode instanceof Group) {
			Group oldGroup = (Group) oldNode;
			Group newGroup = (Group) newNode;

			for (int i = 0; i < oldGroup.getChildCount(); i++) {
				Node oldChild = oldGroup.getChild(i);
				Node newChild = newGroup.getChild(i);

				getOldToNewBonesMapping(map, oldChild, newChild);
			}
		} else if (oldNode instanceof SkinnedMesh) {
			Group oldSkeleton = ((SkinnedMesh) oldNode).getSkeleton();
			Group newSkeleton = ((SkinnedMesh) newNode).getSkeleton();

			getOldToNewBonesMapping(map, oldSkeleton, newSkeleton);
		}
	}

	public void addTransform(Node bone, int weight, int firstVertex, int numVertices) {
		if (bone == null) {
			throw new NullPointerException();
		} else if (bone != skeleton && !bone.isDescendantOf(skeleton)) {
			throw new IllegalArgumentException();
		} else if (weight <= 0 || numVertices <= 0) {
			throw new IllegalArgumentException();
		}  else if (firstVertex < 0 || firstVertex + numVertices > 65535) {
			throw new IndexOutOfBoundsException();
		}

		BoneTransform boneTrans = null;
		int boneTransId = -1;

		for (int i = 0; i < boneTransList.size(); i++) {
			BoneTransform tmpBoneTrans = boneTransList.elementAt(i);

			if (tmpBoneTrans.bone == bone) {
				boneTrans = tmpBoneTrans;
				boneTransId = i;
				break;
			}
		}

		if (boneTrans == null) {
			Transform toBoneTrans = new Transform();
			if (!getTransformTo(bone, toBoneTrans)) {
				throw new ArithmeticException();
			}

			boneTrans = new BoneTransform(bone, toBoneTrans);
			boneTransList.add(boneTrans);
			boneTransId = boneTransList.size() - 1;
		}

		int transPerVtx = Emulator3D.MaxTransformsPerVertex;

		for (int i = firstVertex; i < firstVertex + numVertices; i++) {
			//find slot with minimal weight or with the same bone id
			int minWeight = Integer.MAX_VALUE;
			int selSlot = -1;

			for (int slot = 0; slot < transPerVtx; slot++) {
				int slotWeight = vtxWeights[i * transPerVtx + slot];

				if (vtxBones[i * transPerVtx + slot] == boneTransId + 1) {
					selSlot = slot;
					break;
				}

				if (slotWeight <= minWeight) {
					minWeight = slotWeight;
					selSlot = slot;

					if (slotWeight == 0) break;
				}
			}

			//add transform
			if (vtxBones[i * transPerVtx + selSlot] == boneTransId + 1) {
				vtxWeights[i * transPerVtx + selSlot] += weight;
			} else {
				//selected slot weight should be less than current bone weight
				if (minWeight > weight) selSlot = -1;

				if (selSlot != -1) {
					vtxBones[i * transPerVtx + selSlot] = boneTransId + 1;
					vtxWeights[i * transPerVtx + selSlot] = weight;
				}
			}
		}

		bone.setSkinnedMeshBone();
	}

	protected void alignment(Node reference) {
		super.alignment(reference);
		skeleton.alignment(reference);
	}

	protected boolean rayIntersect(int scope, float[] ray, RayIntersection ri, Transform transform) {
		MeshMorph.getInstance().getMorphedVertexBuffer(this);
		MeshMorph.getInstance().clearCache();
		return super.rayIntersect(scope, ray, ri, transform, MeshMorph.getInstance().morphed) ||
				skeleton.rayIntersect(scope, ray, ri, transform);
	}

	public Vector getTransforms() {
		return boneTransList;
	}

	public int[] getVerticesBones() {
		return vtxBones;
	}

	public int[] getVerticesWeights() {
		return vtxWeights;
	}

	public void getBoneTransform(Node bone, Transform transform) {
		if (bone == null) {
			throw new NullPointerException("bone");
		}
		if (transform == null) {
			throw new NullPointerException("transform");
		}
		if (bone != skeleton && !bone.isDescendantOf(skeleton)) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < boneTransList.size(); i++) {
			BoneTransform boneTrans = boneTransList.elementAt(i);

			if (boneTrans.bone == bone) {
				transform.set(boneTrans.toBoneTrans);
				return;
			}
		}
	}

	public int getBoneVertices(Node bone, int[] indices, float[] weights) {
		if (bone == null) {
			throw new NullPointerException("bone");
		}
		if (bone != skeleton && !bone.isDescendantOf(skeleton)) {
			throw new IllegalArgumentException();
		}

		int boneCount = boneTransList.size();

		int boneIndex;
		for (boneIndex = 0; boneIndex < boneCount; boneIndex++) {
			if (boneTransList.elementAt(boneIndex).bone == bone) {
				break;
			}
		}

		if (boneIndex >= boneCount) {
			return 0;
		}

		int numVertices = vertices.getVertexCount();
		int count = 0;

		final int transPerVtx = Emulator3D.MaxTransformsPerVertex;
		for (int i = 0; i < numVertices; i++) {
			int weight = 0;
			int sum = 0;

			for (int slot = 0; slot < transPerVtx; slot++) {
				int tmpWeight = vtxWeights[i * transPerVtx + slot];

				if (vtxBones[i * transPerVtx + slot] == boneIndex + 1) {
					weight = tmpWeight;
				}

				sum += tmpWeight;
			}

			if (weight > 0) {
				if (indices != null && weights != null) {
					if(indices.length <= count || weights.length <= count) {
						throw new IllegalArgumentException();
					}

					indices[count] = i;
					weights[count] = (float) weight / sum;
				}

				count++;
			}
		}

		return count;
	}
}
