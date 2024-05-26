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

		Transform tmpTrans = new Transform();
		Transform transThis = new Transform();
		Transform transTarget = new Transform();

		if (getRoot() != target.getRoot()) {
			return false;
		} else {

			Node rootThis = this, rootTarget = target;
			int depthThis = getDepth(), depthTarget = target.getDepth();

			while (depthThis > depthTarget) {
				rootThis.getCompositeTransform(tmpTrans);
				transThis.preMultiply(tmpTrans);
				depthThis--;
				rootThis = rootThis.parent;
			}

			while (depthTarget > depthThis) {
				rootTarget.getCompositeTransform(tmpTrans);
				transTarget.preMultiply(tmpTrans);
				depthTarget--;
				rootTarget = rootTarget.parent;
			}

			while (rootThis != rootTarget) {
				rootThis.getCompositeTransform(tmpTrans);
				transThis.preMultiply(tmpTrans);
				rootThis = rootThis.parent;
				rootTarget.getCompositeTransform(tmpTrans);
				transTarget.preMultiply(tmpTrans);
				rootTarget = rootTarget.parent;
			}

//                var3.getImpl_().method445();
			transTarget.getImpl_().invert();
			transTarget.postMultiply(transThis);
			transform.set(transTarget);
			return true;
		}
	}

	protected int getDepth() {
		Node node = this;

		int depth = 0;
		while (node.parent != null) {
			node = node.parent;
			depth++;
		}

		return depth;
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

	public final void align(Node reference) {
		alignment(reference);
	}

	protected void alignment(Node reference) {
		if (reference == null) {
			reference = this;
		}

		computeAlignment(reference, (Vector4f) null, (Vector4f) null, (Vector4f) null, (Vector4f) null);
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
				transformTarget(this.zTarget, var7, var2, var3, var4, var5, var9);
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

				transformTarget(this.yTarget, var7, var2, var3, var4, var5, var9);
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
				super.rotation.set(var10);
			}

		} else {
			throw new IllegalStateException();
		}
	}

	private static void transformTarget(int var0, Transform var1, Vector4f var2, Vector4f var3, Vector4f var4, Vector4f var5, Vector4f var6) {
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

	protected void updateProperty(int property, float[] values) {
		switch (property) {
			case 256:
				this.alphaFactor = G3DUtils.limit(values[0], 0.0F, 1.0F);
				return;
			case 269:
				this.pickingEnable = values[0] >= 0.5F;
				return;
			case 276:
				this.renderingEnable = values[0] >= 0.5F;
				return;
			default:
				super.updateProperty(property, values);
		}
	}

	protected Node getRoot() {
		Node node = this;

		while (true) {
			if (node.parent == null) {
				return node;
			}
			node = node.parent;
		}
	}

    /*protected boolean isPickable(Node var1) {
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
    }*/

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
