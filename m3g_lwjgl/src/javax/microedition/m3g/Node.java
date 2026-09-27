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

	public void setRenderingEnable(boolean enable) {
		this.renderingEnable = enable;
	}

	public void setPickingEnable(boolean enable) {
		this.pickingEnable = enable;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public void setAlphaFactor(float alphaFactor) {
		if (alphaFactor >= 0.0F && alphaFactor <= 1.0F) {
			this.alphaFactor = alphaFactor;
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

	protected boolean isParentOf(Node node) {
		Node next = node;

		while (true) {
			Node current = next;
			if (next == null) {
				return false;
			}

			if (this.equals(current)) {
				return true;
			}

			next = current.getParent();
		}
	}

	protected boolean isDescendantOf(Node ancestor) {
		Node next = this.parent;

		while (true) {
			Node current = next;
			if (next == null) {
				return false;
			}

			if (ancestor.equals(current)) {
				return true;
			}

			next = current.getParent();
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

	protected void computeAlignment(Node reference, Vector4f origin, Vector4f xAxis, Vector4f yAxis, Vector4f zAxis) {
		Node root = this.getRoot();
		if (this.zRef != null && (this.zRef.isDescendantOf(this) || this.zRef.getRoot() != root)) {
			throw new IllegalStateException();
		} else if (this.yRef == null || !this.yRef.isDescendantOf(this) && this.yRef.getRoot() == root) {
			Transform targetTransform = new Transform();
			Transform tmpTransform = new Transform();
			Vector4f targetVector = new Vector4f();
			Quaternion alignRotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
			float[] localTranslation = new float[3];
			this.getTranslation(localTranslation);
			if (this.zTarget != 144) {
				if (this.zRef == null && reference == this) {
					throw new IllegalStateException();
				}

				(this.zRef == null ? reference : this.zRef).getTransformTo(this.parent, targetTransform);
				tmpTransform.postTranslate(-localTranslation[0], -localTranslation[1], -localTranslation[2]);
				targetTransform.preMultiply(tmpTransform);
				transformTarget(this.zTarget, targetTransform, origin, xAxis, yAxis, zAxis, targetVector);
				targetVector.w = 0.0F;
				alignRotation.setRotation(Vector4f.Z_AXIS, targetVector, (Vector4f) null);
			}

			if (this.yTarget != 144) {
				if (this.yRef == null && reference == this) {
					throw new IllegalStateException();
				}

				(this.yRef == null ? reference : this.yRef).getTransformTo(this.parent, targetTransform);
				tmpTransform.postTranslate(-localTranslation[0], -localTranslation[1], -localTranslation[2]);
				targetTransform.preMultiply(tmpTransform);
				if (this.zTarget != 144) {
					tmpTransform.setIdentity();
					tmpTransform.postRotateQuat(alignRotation.x, alignRotation.y, alignRotation.z, -alignRotation.w);
					targetTransform.preMultiply(tmpTransform);
				}

				transformTarget(this.yTarget, targetTransform, origin, xAxis, yAxis, zAxis, targetVector);
				targetVector.w = 0.0F;
				if (this.zTarget != 144) {
					Quaternion yRotation;
					(yRotation = new Quaternion()).setRotation(Vector4f.Y_AXIS, targetVector, Vector4f.Z_AXIS);
					alignRotation.mul(yRotation);
				} else {
					alignRotation.setRotation(Vector4f.Y_AXIS, targetVector, (Vector4f) null);
				}
			}

			if (this.zTarget != 144 || this.yTarget != 144) {
				super.rotation.set(alignRotation);
			}

		} else {
			throw new IllegalStateException();
		}
	}

	private static void transformTarget(int target, Transform transform, Vector4f origin, Vector4f xAxis, Vector4f yAxis, Vector4f zAxis, Vector4f result) {
		switch (target) {
			case 145:
				result.set(origin == null ? Vector4f.ORIGIN : origin);
				break;
			case 146:
				result.set(xAxis == null ? Vector4f.X_AXIS : xAxis);
				break;
			case 147:
				result.set(yAxis == null ? Vector4f.Y_AXIS : yAxis);
				break;
			case 148:
				result.set(zAxis == null ? Vector4f.Z_AXIS : zAxis);
		}

		transform.getImpl_().transform(result);
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

	public int getAlignmentTarget(int axis) {
		if (axis != 148 && axis != 147) {
			throw new IllegalArgumentException("axis != Z_AXIS && axis != Y_AXIS");
		} else {
			return axis == 148 ? this.zTarget : this.yTarget;
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

    /*protected boolean isPickable(Node stopNode) {
        Node next = this;

        while (true) {
            Node current = next;
            if (next == null) {
                break;
            }

            if (!current.pickingEnable) {
                return false;
            }

            if (current == stopNode) {
                break;
            }

            next = current.parent;
        }

        return true;
    }*/

	protected abstract boolean rayIntersect(int scope, float[] ray, RayIntersection ri, Transform transform);

	protected void enableBoneFlag() {
		this.boneFlag = true;
	}

	protected void setSkinnedMeshBone() {
		Node next = this;

		while (true) {
			Node current = next;
			if (next == null || current instanceof SkinnedMesh) {
				return;
			}

			current.enableBoneFlag();
			next = current.getParent();
		}
	}

	protected boolean isSkinnedMeshBone() {
		return this.boneFlag;
	}

	protected void updateAlignReferences() {
		Node duplicatedRef;
		if (this.zTarget != 144) {
			duplicatedRef = this.zRef.m_duplicatedNode;
			if (this.zRef != null && duplicatedRef != null && duplicatedRef.isDescendantOf(this.m_duplicatedNode.getRoot())) {
				this.m_duplicatedNode.zRef = duplicatedRef;
			}
		}

		if (this.yTarget != 144) {
			duplicatedRef = this.yRef.m_duplicatedNode;
			if (this.yRef != null && duplicatedRef != null && duplicatedRef.isDescendantOf(this.m_duplicatedNode.getRoot())) {
				this.m_duplicatedNode.yRef = duplicatedRef;
			}
		}

	}

	protected void clearAlignReferences() {
		this.m_duplicatedNode = null;
	}

	protected Object3D duplicateObject() {
		Node copy;
		(copy = (Node) super.duplicateObject()).parent = null;
		this.m_duplicatedNode = copy;
		return copy;
	}
}
