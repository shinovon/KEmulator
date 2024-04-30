package javax.microedition.m3g;

import emulator.graphics3D.lwjgl.Emulator3D;
import emulator.graphics3D.m3g.MeshMorph;
import emulator.graphics3D.m3g.BoneTransform;

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

        Hashtable<Node, Node> oldToNewBone = new Hashtable<>();
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
        } else if (weight > 0 && numVertices > 0) {
            if (firstVertex < 0 && firstVertex + numVertices > 65535) {
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

            for (int i = firstVertex; i < firstVertex + numVertices; i++) {
                //find bone slot with minimal weight
                int minWeight = Integer.MAX_VALUE;
                int selSlot = -1;

                for (int slot = 0; slot < Emulator3D.MaxTransformsPerVertex; slot++) {
                    int slotWeight = vtxWeights[i * Emulator3D.MaxTransformsPerVertex + slot];

                    if (slotWeight < minWeight) {
                        minWeight = slotWeight;
                        selSlot = slot;

                        if (slotWeight == 0) break;
                    }
                }

                //selected slot weight should be less than current bone weight
                if (minWeight > weight) selSlot = -1;

                if (selSlot != -1) {
                    vtxBones[i * Emulator3D.MaxTransformsPerVertex + selSlot] = boneTransId + 1;
                    vtxWeights[i * Emulator3D.MaxTransformsPerVertex + selSlot] = weight;
                }
            }

            bone.setSkinnedMeshBone();
        } else {
            throw new IllegalArgumentException();
        }
    }

    protected void alignment(Node var1) {
        super.alignment(var1);
        if (this.skeleton != null) {
            this.skeleton.alignment(var1);
        }

    }

    protected boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4) {
        MeshMorph.getInstance().getMorphedVertexBuffer(this);
        MeshMorph.getInstance().clearCache();
        return super.rayIntersect(var1, var2, var3, var4, MeshMorph.getInstance().morphed);
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
}
