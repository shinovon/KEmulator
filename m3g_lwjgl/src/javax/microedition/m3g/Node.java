package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.Quaternion;
import emulator.graphics3D.Vector4f;

public abstract class Node extends Transformable {
    public static final int NONE = 144;
    public static final int ORIGIN = 145;
    public static final int X_AXIS = 146;
    public static final int Y_AXIS = 147;
    public static final int Z_AXIS = 148;
    Node parent = null;
    private boolean renderingEnable = true;
    private boolean pickingEnable = true;
    private float alphaFactor = 1.0F;
    private int scope = -1;
    private int yTarget;
    private int zTarget;
    private Node yRef;
    private Node zRef;
    private boolean boneFlag;
    protected Node m_duplicatedNode;

    Node() {
        this.yTarget = this.zTarget = 144;
        this.yRef = this.zRef = null;
        this.boneFlag = false;
    }

    public void setRenderingEnable(boolean var1) {
        this.renderingEnable = var1;
    }

    public void setPickingEnable(boolean var1) {
        this.pickingEnable = var1;
    }

    public void setScope(int var1) {
        this.scope = var1;
    }

    public void setAlphaFactor(float var1) {
        if (var1 >= 0.0F && var1 <= 1.0F) {
            this.alphaFactor = var1;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public boolean isRenderingEnabled() {
        return this.renderingEnable;
    }

    public boolean isPickingEnabled() {
        return this.pickingEnable;
    }

    public int getScope() {
        return this.scope;
    }

    public float getAlphaFactor() {
        return this.alphaFactor;
    }

    public Node getParent() {
        return this.parent;
    }

    public boolean getTransformTo(Node target, Transform transform) {
        if (target == null || transform == null) {
            throw new NullPointerException();
        }

        Transform var3 = new Transform();
        Transform var4 = new Transform();
        Transform var5 = new Transform();
        Node var6 = this;
        Node var7 = target;

        if (getRoot() != target.getRoot()) {
            return false;
        } else {
            int var8 = target.getDepth();

            int var9;
            for (var9 = this.getDepth(); var9 > var8; var6 = var6.parent) {
                var6.getCompositeTransform(var5);
                var4.preMultiply(var5);
                --var9;
            }

            while (var8 > var9) {
                var7.getCompositeTransform(var5);
                var3.preMultiply(var5);
                --var8;
                var7 = var7.parent;
            }

            while (var6 != var7) {
                var6.getCompositeTransform(var5);
                var4.preMultiply(var5);
                var6 = var6.parent;
                var7.getCompositeTransform(var5);
                var3.preMultiply(var5);
                var7 = var7.parent;
            }

//                var3.getImpl_().method445();
            var3.getImpl_().invert();
            var3.postMultiply(var4);
            transform.set(var3);
            return true;
        }
    }

    protected int getDepth() {
        Node var1 = this;

        int var2;
        for (var2 = 0; var1.parent != null; ++var2) {
            var1 = var1.parent;
        }

        return var2;
    }

    protected boolean isParentOf(Node var1) {
        Node var10000 = var1;

        while (true) {
            Node var2 = var10000;
            if (var10000 == null) {
                return false;
            }

            if (this.equals(var2)) {
                return true;
            }

            var10000 = var2.getParent();
        }
    }

    protected boolean isDescendantOf(Node var1) {
        Node var10000 = this.parent;

        while (true) {
            Node var2 = var10000;
            if (var10000 == null) {
                return false;
            }

            if (var1.equals(var2)) {
                return true;
            }

            var10000 = var2.getParent();
        }
    }

    public final void align(Node var1) {
        this.alignment(var1);
    }

    protected void alignment(Node var1) {
        Node var10000;
        Node var10001;
        if (var1 == null) {
            var10000 = this;
            var10001 = this;
        } else {
            var10000 = this;
            var10001 = var1;
        }

        var10000.computeAlignment(var10001, (Vector4f) null, (Vector4f) null, (Vector4f) null, (Vector4f) null);
    }

    protected void computeAlignment(Node var1, Vector4f var2, Vector4f var3, Vector4f var4, Vector4f var5) {
        Node var6 = this.getRoot();
        if (this.zRef != null && (this.zRef.isDescendantOf(this) || this.zRef.getRoot() != var6)) {
            throw new IllegalStateException();
        } else if (this.yRef == null || !this.yRef.isDescendantOf(this) && this.yRef.getRoot() == var6) {
            Transform var7 = new Transform();
            Transform var8 = new Transform();
            Vector4f var9 = new Vector4f();
            Quaternion var10 = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
            float[] var11 = new float[3];
            this.getTranslation(var11);
            if (this.zTarget != 144) {
                if (this.zRef == null && var1 == this) {
                    throw new IllegalStateException();
                }

                (this.zRef == null ? var1 : this.zRef).getTransformTo(this.parent, var7);
                var8.postTranslate(-var11[0], -var11[1], -var11[2]);
                var7.preMultiply(var8);
                method904(this.zTarget, var7, var2, var3, var4, var5, var9);
                var9.w = 0.0F;
                var10.setRotation(Vector4f.Z_AXIS, var9, (Vector4f) null);
            }

            if (this.yTarget != 144) {
                if (this.yRef == null && var1 == this) {
                    throw new IllegalStateException();
                }

                (this.yRef == null ? var1 : this.yRef).getTransformTo(this.parent, var7);
                var8.postTranslate(-var11[0], -var11[1], -var11[2]);
                var7.preMultiply(var8);
                if (this.zTarget != 144) {
                    var8.setIdentity();
                    var8.postRotateQuat(var10.x, var10.y, var10.z, -var10.w);
                    var7.preMultiply(var8);
                }

                method904(this.yTarget, var7, var2, var3, var4, var5, var9);
                var9.w = 0.0F;
                if (this.zTarget != 144) {
                    Quaternion var13;
                    (var13 = new Quaternion()).setRotation(Vector4f.Y_AXIS, var9, Vector4f.Z_AXIS);
                    var10.mul(var13);
                } else {
                    var10.setRotation(Vector4f.Y_AXIS, var9, (Vector4f) null);
                }
            }

            if (this.zTarget != 144 || this.yTarget != 144) {
                super.ana864.set(var10);
            }

        } else {
            throw new IllegalStateException();
        }
    }

    private static void method904(int var0, Transform var1, Vector4f var2, Vector4f var3, Vector4f var4, Vector4f var5, Vector4f var6) {
        switch (var0) {
            case 145:
                var6.set(var2 == null ? Vector4f.ORIGIN : var2);
                break;
            case 146:
                var6.set(var3 == null ? Vector4f.X_AXIS : var3);
                break;
            case 147:
                var6.set(var4 == null ? Vector4f.Y_AXIS : var4);
                break;
            case 148:
                var6.set(var5 == null ? Vector4f.Z_AXIS : var5);
        }

        var1.getImpl_().transform(var6);
    }

    public void setAlignment(Node zRef, int zTarget, Node yRef, int yTarget) {
        if (zTarget < NONE || zTarget > Z_AXIS || yTarget < NONE || yTarget > Z_AXIS) {
            throw new IllegalArgumentException("yTarget or zTarget is not one of the symbolic constants");
        }

        if (zRef == yRef && zTarget == yTarget && yTarget != NONE) {
            throw new IllegalArgumentException("(zRef == yRef) &&  (zTarget == yTarget != NONE)");
        } else if (zRef == this || yRef == this) {
            throw new IllegalArgumentException("zRef or yRef is this Node");
        }

        this.zTarget = zTarget;
        this.yTarget = yTarget;
        this.zRef = zRef;
        this.yRef = yRef;
    }

    public int getAlignmentTarget(int var1) {
        if (var1 != 148 && var1 != 147) {
            throw new IllegalArgumentException("axis != Z_AXIS && axis != Y_AXIS");
        } else {
            return var1 == 148 ? this.zTarget : this.yTarget;
        }
    }

    public Node getAlignmentReference(int axis) {
        if (axis != 148 && axis != 147) {
            throw new IllegalArgumentException("axis != Z_AXIS && axis != Y_AXIS");
        } else {
            return axis == 148 ? this.zRef : this.yRef;
        }
    }

    protected void updateProperty(int var1, float[] var2) {
        switch (var1) {
            case 256:
                this.alphaFactor = G3DUtils.limit(var2[0], 0.0F, 1.0F);
                return;
            case 269:
                this.pickingEnable = var2[0] >= 0.5F;
                return;
            case 276:
                this.renderingEnable = var2[0] >= 0.5F;
                return;
            default:
                super.updateProperty(var1, var2);
        }
    }

    protected Node getRoot() {
        Node var10000 = this;

        while (true) {
            Node var1 = var10000;
            if (var10000.parent == null) {
                return var1;
            }

            var10000 = var1.parent;
        }
    }

    protected boolean isPickable(Node var1) {
        Node var10000 = this;

        while (true) {
            Node var2 = var10000;
            if (var10000 == null) {
                break;
            }

            if (!var2.pickingEnable) {
                return false;
            }

            if (var2 == var1) {
                break;
            }

            var10000 = var2.parent;
        }

        return true;
    }

    protected abstract boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4);

    protected void enableBoneFlag() {
        this.boneFlag = true;
    }

    protected void setSkinnedMeshBone() {
        Node var10000 = this;

        while (true) {
            Node var1 = var10000;
            if (var10000 == null || var1 instanceof SkinnedMesh) {
                return;
            }

            var1.enableBoneFlag();
            var10000 = var1.getParent();
        }
    }

    protected boolean isSkinnedMeshBone() {
        return this.boneFlag;
    }

    protected void updateAlignReferences() {
        Node var1;
        if (this.zTarget != 144) {
            var1 = this.zRef.m_duplicatedNode;
            if (this.zRef != null && var1 != null && var1.isDescendantOf(this.m_duplicatedNode.getRoot())) {
                this.m_duplicatedNode.zRef = var1;
            }
        }

        if (this.yTarget != 144) {
            var1 = this.yRef.m_duplicatedNode;
            if (this.yRef != null && var1 != null && var1.isDescendantOf(this.m_duplicatedNode.getRoot())) {
                this.m_duplicatedNode.yRef = var1;
            }
        }

    }

    protected void clearAlignReferences() {
        this.m_duplicatedNode = null;
    }

    protected Object3D duplicateObject() {
        Node var1;
        (var1 = (Node) super.duplicateObject()).parent = null;
        this.m_duplicatedNode = var1;
        return var1;
    }
}
