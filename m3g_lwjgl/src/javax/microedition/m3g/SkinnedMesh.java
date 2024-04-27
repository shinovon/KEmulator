package javax.microedition.m3g;

import emulator.graphics3D.lwjgl.Emulator3D;
import emulator.graphics3D.m3g.MeshMorph;
import emulator.graphics3D.m3g.BoneTransform;

import java.util.Hashtable;
import java.util.Vector;

public class SkinnedMesh extends Mesh {
    Group skeleton;

    public Vector<BoneTransform> m_transforms;
    public BoneTransform[] vtxBones;
    public int[] vtxWeights;

    public SkinnedMesh(VertexBuffer var1, IndexBuffer var2, Appearance var3, Group var4) {
        super(var1, var2, var3);
        if (var4 == null) {
            throw new NullPointerException();
        } else if (!(var4 instanceof World) && var4.getParent() == null) {
            this.skeleton = var4;
            this.skeleton.parent = this;
            this.addReference(this.skeleton);
            this.m_transforms = new Vector();

            vtxBones = new BoneTransform[var1.getVertexCount() * Emulator3D.MaxTransformsPerVertex];
            vtxWeights = new int[var1.getVertexCount() * Emulator3D.MaxTransformsPerVertex];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public SkinnedMesh(VertexBuffer var1, IndexBuffer[] var2, Appearance[] var3, Group var4) {
        super(var1, var2, var3);
        if (!(var4 instanceof World) && var4.getParent() == null) {
            this.skeleton = var4;
            this.skeleton.parent = this;
            this.addReference(this.skeleton);
            this.m_transforms = new Vector();

            vtxBones = new BoneTransform[var1.getVertexCount() * Emulator3D.MaxTransformsPerVertex];
            vtxWeights = new int[var1.getVertexCount() * Emulator3D.MaxTransformsPerVertex];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Group getSkeleton() {
        return this.skeleton;
    }

    protected Object3D duplicateObject() {
        SkinnedMesh var1;
        Group var2 = (Group) (var1 = (SkinnedMesh) super.duplicateObject()).getSkeleton().duplicateObject();
        var1.removeReference(var1.skeleton);
        var2.parent = var1;
        var1.skeleton = var2;
        var1.addReference(var2);
        var1.m_transforms = (Vector) this.m_transforms.clone();
        return var1;
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

            for (int i = 0; i < m_transforms.size(); i++) {
                BoneTransform tmpBoneTrans = m_transforms.elementAt(i);

                if (tmpBoneTrans.bone == bone) {
                    boneTrans = tmpBoneTrans;
                    break;
                }
            }

            if (boneTrans == null) {
                Transform toBoneTrans = new Transform();
                if (!getTransformTo(bone, toBoneTrans)) {
                    throw new ArithmeticException();
                }

                boneTrans = new BoneTransform(bone, toBoneTrans);
                m_transforms.add(boneTrans);
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
                    vtxBones[i * Emulator3D.MaxTransformsPerVertex + selSlot] = boneTrans;
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
        return m_transforms;
    }

    public Object[] getVerticesBones() {
        return vtxBones;
    }

    public int[] getVerticesWeights() {
        return vtxWeights;
    }
}
