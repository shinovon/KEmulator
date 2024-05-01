package javax.microedition.m3g;

import emulator.graphics3D.Vector4f;

import java.util.Vector;

public class Group extends Node {
    Vector children = new Vector();

    protected Object3D duplicateObject() {
        Group clone = (Group) super.duplicateObject();
        clone.children = (Vector) children.clone();

        for (int i = clone.getChildCount() - 1; i >= 0; --i) {
            Node oldChild = clone.getChild(i);
            Node newChild = (Node) clone.getChild(i).duplicateObject();

            clone.removeReference(oldChild);
            clone.addReference(newChild);

            newChild.parent = clone;
            clone.children.set(i, newChild);
        }

        return clone;
    }

    public void addChild(Node child) {
        if (child == null) {
            throw new NullPointerException();
        } else if (child == this) {
            throw new IllegalArgumentException("child is this Group");
        } else if (child instanceof World) {
            throw new IllegalArgumentException("child is a World node");
        } else if (child.parent != null && child.parent != this) {
            throw new IllegalArgumentException("child already has a parent other than this Group");
        } else if (child.isParentOf(this)) {
            throw new IllegalArgumentException("child is an ancestor of this Group");
        }

        if (!children.contains(child)) {
            children.add(child);
            child.parent = this;
            addReference(child);
        }
    }

    public void removeChild(Node child) {
        if (child != null) {
            if (child.isSkinnedMeshBone()) {
                throw new IllegalArgumentException();
            }

            if (children.contains(child)) {
                children.remove(child);
                child.parent = null;
                removeReference(child);
            }
        }
    }

    public int getChildCount() {
        return children.size();
    }

    public Node getChild(int index) {
        if (index < 0 || index >= getChildCount()) {
            throw new IndexOutOfBoundsException();
        }

        return (Node) children.get(index);
    }

    protected void alignment(Node reference) {
        super.alignment(reference);

        for (int i = 0; i < children.size(); ++i) {
            ((Node) children.get(i)).alignment(reference);
        }
    }

    public boolean pick(int scope, float x, float y, Camera camera, RayIntersection ri) {
        if (camera == null) {
            throw new NullPointerException();
        } else if (camera.getRoot() != getRoot()) {
            throw new IllegalStateException();
        }

        Vector4f rayStart = new Vector4f(2 * x - 1, 1 - 2 * y, -1, 1);
        Vector4f rayEnd = new Vector4f(2 * x - 1, 1 - 2 * y, 1, 1);

        Transform invProj = new Transform();
        camera.getProjection(invProj);
        invProj.getImpl_().invert();

        invProj.getImpl_().transform(rayStart);
        invProj.getImpl_().transform(rayEnd);
        rayStart.mul(1 / rayStart.w);
        rayEnd.mul(1 / rayEnd.w);

        float[] ray = new float[8];
        ray[6] = rayStart.z;
        ray[7] = rayEnd.z;

        Transform camToGroup = new Transform();
        camera.getTransformTo(this, camToGroup);
        camToGroup.getImpl_().transform(rayStart);
        camToGroup.getImpl_().transform(rayEnd);
        rayStart.mul(1 / rayStart.w);
        rayEnd.mul(1 / rayEnd.w);

        ray[0] = rayStart.x;
        ray[1] = rayStart.y;
        ray[2] = rayStart.z;
        ray[3] = rayEnd.x;
        ray[4] = rayEnd.y;
        ray[5] = rayEnd.z;

        if (ri == null) ri = new RayIntersection();

        ri.startPick(this, ray, x, y, camera);
        return rayIntersect(scope, ray, ri, camToGroup);
    }

    public boolean pick(int scope, float ox, float oy, float oz, float dx, float dy, float dz, RayIntersection ri) {
        if (dx == 0.0F && dy == 0.0F && dz == 0.0F) {
            throw new IllegalArgumentException();
        }

        float[] ray = new float[] {ox, oy, oz, ox + dx, oy + dy, oz + dz};

        Transform transform = new Transform();

        if (ri == null) ri = new RayIntersection();

        ri.startPick(this, ray, 0.0F, 0.0F, null);
        return rayIntersect(scope, ray, ri, transform);
    }

    protected boolean rayIntersect(int scope, float[] ray, RayIntersection ri, Transform transform) {
        boolean hit = false;
        Transform childTransform = new Transform();
        Transform tmpTrans = new Transform();

        for (int i = 0; i < children.size(); ++i) {
            Node child = (Node) children.get(i);

            if ((child.getScope() & scope) != 0 && child.isPickingEnabled()) {
                childTransform.set(transform);
                child.getCompositeTransform(tmpTrans);
                childTransform.postMultiply(tmpTrans);

                if (child.rayIntersect(scope, ray, ri, childTransform)) {
                    hit = true;
                }
            }
        }

        return hit;
    }

    protected void updateAlignReferences() {
        super.updateAlignReferences();

        for (int var1 = 0; var1 < this.children.size(); ++var1) {
            ((Node) this.children.get(var1)).updateAlignReferences();
        }

    }

    protected void clearAlignReferences() {
        super.clearAlignReferences();

        for (int var1 = 0; var1 < this.children.size(); ++var1) {
            ((Node) this.children.get(var1)).clearAlignReferences();
        }

    }
}
