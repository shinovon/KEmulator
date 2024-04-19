package javax.microedition.m3g;

import emulator.graphics3D.Vector4f;

import java.util.Vector;

public class Group extends Node {
    Vector aVector931 = new Vector();

    protected Object3D duplicateObject() {
        Group var1;
        (var1 = (Group) super.duplicateObject()).aVector931 = (Vector) this.aVector931.clone();

        for (int var2 = var1.getChildCount() - 1; var2 >= 0; --var2) {
            var1.removeReference(var1.getChild(var2));
            Node var3;
            (var3 = (Node) var1.getChild(var2).duplicateObject()).parent = var1;
            var1.aVector931.set(var2, var3);
            var1.addReference(var3);
        }

        return var1;
    }

    public void addChild(Node var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else if (var1 == this) {
            throw new IllegalArgumentException("child is this Group");
        } else if (var1 instanceof World) {
            throw new IllegalArgumentException("child is a World node");
        } else if (var1.parent != null && var1.parent != this) {
            throw new IllegalArgumentException("child already has a parent other than this Group");
        } else if (var1.isParentOf(this)) {
            throw new IllegalArgumentException("child is an ancestor of this Group");
        } else {
            if (!this.aVector931.contains(var1)) {
                this.aVector931.add(var1);
            }

            var1.parent = this;
            this.addReference(var1);
        }
    }

    public void removeChild(Node var1) {
        if (var1 != null) {
            if (var1.isSkinnedMeshBone()) {
                throw new IllegalArgumentException();
            } else {
                var1.parent = null;
                if (this.aVector931.contains(var1)) {
                    this.aVector931.remove(var1);
                    this.removeReference(var1);
                }
            }
        }
    }

    public int getChildCount() {
        return this.aVector931.size();
    }

    public Node getChild(int var1) {
        if (var1 >= 0 && var1 < this.getChildCount()) {
            return (Node) this.aVector931.get(var1);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    protected void alignment(Node var1) {
        super.alignment(var1);

        for (int var2 = 0; var2 < this.aVector931.size(); ++var2) {
            ((Node) this.aVector931.get(var2)).alignment(var1);
        }

    }

    public boolean pick(int var1, float var2, float var3, Camera var4, RayIntersection var5) {
        if (var4 == null) {
            throw new NullPointerException();
        } else if (var4.getRoot() != this.getRoot()) {
            throw new IllegalStateException();
        } else {
            Vector4f var7 = new Vector4f(2.0F * var2 - 1.0F, 1.0F - 2.0F * var3, 1.0F, 1.0F);
            Vector4f var8 = new Vector4f(2.0F * var2 - 1.0F, 1.0F - 2.0F * var3, -1.0F, 1.0F);
            Transform var9 = new Transform();
            var4.getProjection(var9);
            var9.getImpl_().method445();
            var9.getImpl_().transform(var8);
            var8.mul(1.0F / var8.w);
            var9.getImpl_().transform(var7);
            var7.mul(1.0F / var7.w);
            float[] var10;
            (var10 = new float[8])[6] = var8.z;
            var10[7] = var7.z;
            Transform var11 = new Transform();
            var4.getTransformTo(this, var11);
            var11.getImpl_().transform(var8);
            var8.mul(1.0F / var8.w);
            var11.getImpl_().transform(var7);
            var7.mul(1.0F / var7.w);
            var10[0] = var8.x;
            var10[1] = var8.y;
            var10[2] = var8.z;
            var10[3] = var7.x;
            var10[4] = var7.y;
            var10[5] = var7.z;
            if (var5 == null) {
                var5 = new RayIntersection();
            }

            var5.startPick(this, var10, var2, var3, var4);
            var11.setIdentity();
            return this.rayIntersect(var1, var10, var5, var11);
        }
    }

    public boolean pick(int var1, float var2, float var3, float var4, float var5, float var6, float var7, RayIntersection var8) {
        if (var5 == 0.0F && var6 == 0.0F && var7 == 0.0F) {
            throw new IllegalArgumentException();
        } else {
            Transform var9 = new Transform();
            float[] var10;
            (var10 = new float[6])[0] = var2;
            var10[1] = var3;
            var10[2] = var4;
            var10[3] = var2 + var5;
            var10[4] = var3 + var6;
            var10[5] = var4 + var7;
            if (var8 == null) {
                var8 = new RayIntersection();
            }

            var8.startPick(this, var10, 0.0F, 0.0F, (Camera) null);
            return this.rayIntersect(var1, var10, var8, var9);
        }
    }

    protected boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4) {
        boolean var5 = false;
        Transform var6 = new Transform();
        Transform var7 = new Transform();

        for (int var8 = 0; var8 < this.aVector931.size(); ++var8) {
            Node var9;
            if (((var9 = (Node) this.aVector931.get(var8)) instanceof Group || (var9.getScope() & var1) != 0) && var9.isPickable(var3.getRoot())) {
                var6.set(var4);
                var9.getCompositeTransform(var7);
                var6.postMultiply(var7);
                if (var9.rayIntersect(var1, var2, var3, var6)) {
                    var5 = true;
                }
            }
        }

        return var5;
    }

    protected void updateAlignReferences() {
        super.updateAlignReferences();

        for (int var1 = 0; var1 < this.aVector931.size(); ++var1) {
            ((Node) this.aVector931.get(var1)).updateAlignReferences();
        }

    }

    protected void clearAlignReferences() {
        super.clearAlignReferences();

        for (int var1 = 0; var1 < this.aVector931.size(); ++var1) {
            ((Node) this.aVector931.get(var1)).clearAlignReferences();
        }

    }
}
