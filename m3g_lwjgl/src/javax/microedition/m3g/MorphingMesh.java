package javax.microedition.m3g;

import emulator.graphics3D.m3g.MeshMorph;

public class MorphingMesh extends Mesh {
	private VertexBuffer[] morphTargets;
	private float[] weights;
	public float m_baseWeight;

	public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer triangles, Appearance appearance) {
		super(base, triangles, appearance);
		if (targets == null) {
			throw new NullPointerException();
		} else {
			this.morphTargets = new VertexBuffer[targets.length];

			for (int i = targets.length - 1; i >= 0; --i) {
				if (targets[i] == null) {
					throw new NullPointerException();
				}

				if (targets[i].getVertexCount() == 0) {
					throw new IllegalArgumentException("targets is empty");
				}

				this.morphTargets[i] = targets[i];
				this.addReference(this.morphTargets[i]);
			}

			this.weights = new float[this.morphTargets.length];
			this.m_baseWeight = 1.0F;
		}
	}

	public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer[] triangles, Appearance[] appearances) {
		super(base, triangles, appearances);
		this.morphTargets = new VertexBuffer[targets.length];

		for (int i = targets.length - 1; i >= 0; --i) {
			if (targets[i] == null) {
				throw new NullPointerException();
			}

			if (targets[i].getVertexCount() == 0) {
				throw new IllegalArgumentException("targets is empty");
			}

			this.morphTargets[i] = targets[i];
			this.addReference(this.morphTargets[i]);
		}

		this.weights = new float[this.morphTargets.length];
		this.m_baseWeight = 1.0F;
	}

	protected Object3D duplicateObject() {
		MorphingMesh copy;
		(copy = (MorphingMesh) super.duplicateObject()).weights = (float[]) this.weights.clone();
		copy.morphTargets = (VertexBuffer[]) this.morphTargets.clone();
		return copy;
	}

	public VertexBuffer getMorphTarget(int index) {
		if (index >= 0 && index < this.morphTargets.length) {
			return this.morphTargets[index];
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public int getMorphTargetCount() {
		return this.morphTargets.length;
	}

	public void setWeights(float[] weights) {
		if (weights == null) {
			throw new NullPointerException();
		} else if (weights.length < this.weights.length) {
			throw new IllegalArgumentException();
		} else {
			this.m_baseWeight = 1.0F;

			for (int i = 0; i < this.weights.length; ++i) {
				this.weights[i] = weights[i];
				this.m_baseWeight -= weights[i];
			}

		}
	}

	public void getWeights(float[] weights) {
		if (weights == null) {
			throw new NullPointerException();
		} else if (weights.length < this.weights.length) {
			throw new IllegalArgumentException();
		} else {
			System.arraycopy(this.weights, 0, weights, 0, this.weights.length);
		}
	}

	protected void updateProperty(int property, float[] values) {
		switch (property) {
			case 266:
				this.m_baseWeight = 1.0F;

				for (int i = 0; i < this.weights.length; ++i) {
					if (i < values.length) {
						this.weights[i] = values[i];
						this.m_baseWeight -= values[i];
					} else {
						this.weights[i] = 0.0F;
					}
				}

				return;
			default:
				super.updateProperty(property, values);
		}
	}

	protected boolean rayIntersect(int scope, float[] ray, RayIntersection ri, Transform transform) {
		MeshMorph.getInstance().getMorphedVertexBuffer(this);
		MeshMorph.getInstance().clearCache();
		return super.rayIntersect(scope, ray, ri, transform, MeshMorph.getInstance().morphed);
	}

	public float getBaseWeight() {
		return m_baseWeight;
	}
}
