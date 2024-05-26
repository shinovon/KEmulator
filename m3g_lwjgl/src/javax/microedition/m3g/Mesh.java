package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.Vector4f;
import emulator.graphics3D.lwjgl.Emulator3D;

public class Mesh extends Node {
	protected VertexBuffer vertices;
	private IndexBuffer[] submeshes;
	private Appearance[] appearances;

	public Mesh(VertexBuffer vertices, IndexBuffer submesh, Appearance appearance) {
		if (vertices == null || submesh == null) {
			throw new NullPointerException();
		}

		this.vertices = vertices;
		this.submeshes = new IndexBuffer[1];
		this.submeshes[0] = submesh;
		this.appearances = new Appearance[1];
		if (appearance != null) {
			this.appearances[0] = appearance;
			addReference(this.appearances[0]);
		}

		addReference(this.vertices);
		addReference(this.submeshes[0]);
	}

	public Mesh(VertexBuffer vertices, IndexBuffer[] submeshes, Appearance[] appearances) {
		if (vertices == null || submeshes == null) {
			throw new NullPointerException();
		} else if (submeshes.length == 0 || (appearances != null && appearances.length < submeshes.length)) {
			throw new IllegalArgumentException();
		}

		this.vertices = vertices;
		this.submeshes = new IndexBuffer[submeshes.length];
		this.appearances = new Appearance[submeshes.length];

		for (int i = 0; i < submeshes.length; i++) {
			if (submeshes[i] == null) {
				throw new NullPointerException();
			}

			this.submeshes[i] = submeshes[i];
			addReference(this.submeshes[i]);

			if (appearances != null) {
				this.appearances[i] = appearances[i];
				addReference(this.appearances[i]);
			}
		}

		addReference(this.vertices);
	}

	protected Object3D duplicateObject() {
		Mesh clone = (Mesh) super.duplicateObject();
		clone.submeshes = (IndexBuffer[]) submeshes.clone();
		clone.appearances = (Appearance[]) appearances.clone();

		return clone;
	}

	public void setAppearance(int index, Appearance ap) {
		if (index < 0 || index >= submeshes.length) {
			throw new IndexOutOfBoundsException();
		}

		removeReference(appearances[index]);
		appearances[index] = ap;
		addReference(appearances[index]);
	}

	public Appearance getAppearance(int index) {
		if (index < 0 || index >= submeshes.length) {
			throw new IndexOutOfBoundsException();
		}

		return appearances[index];
	}

	public IndexBuffer getIndexBuffer(int index) {
		if (index < 0 || index >= submeshes.length) {
			throw new IndexOutOfBoundsException();
		}

		return submeshes[index];
	}

	public VertexBuffer getVertexBuffer() {
		return vertices;
	}

	public int getSubmeshCount() {
		return submeshes.length;
	}

	protected boolean rayIntersect(int scope, float[] ray, RayIntersection ri, Transform transform) {
		return this.rayIntersect(scope, ray, ri, transform, this.vertices);
	}

	protected boolean rayIntersect(int scope, float[] ray, RayIntersection ri, Transform transform, VertexBuffer vb) {
		if ((scope & getScope()) == 0) return false;

		if (vb != null && this.appearances != null && this.submeshes != null) {
			if (vb.getPositions((float[]) null) == null) {
				throw new IllegalStateException("No vertex positions");
			} else {
				boolean var6 = false;
				Vector4f var7 = new Vector4f(ray[0], ray[1], ray[2], 1.0F);
				Vector4f var8 = new Vector4f(ray[3], ray[4], ray[5], 1.0F);
				Transform var9;
				(var9 = new Transform()).set(transform);
				var9.getImpl_().invert();
				var9.getImpl_().transform(var7);
				var9.getImpl_().transform(var8);
				var7.mul(1.0F / var7.w);
				var8.mul(1.0F / var8.w);
				var8.sub(var7);
				Vector4f vtxA = new Vector4f();
				Vector4f vtxB = new Vector4f();
				Vector4f vtxC = new Vector4f();
				Vector4f var13 = new Vector4f();
				Vector4f var14 = new Vector4f();
				Transform var15 = new Transform();
				int[] triIndices = new int[4];
				float[] texS = new float[Emulator3D.NumTextureUnits];
				float[] texT = new float[Emulator3D.NumTextureUnits];
				float[] normal = null;

				for (int submesh = 0; submesh < this.submeshes.length; ++submesh) {
					if (this.appearances[submesh] != null && this.submeshes[submesh] != null) {
						int var21;
						if (this.appearances[submesh].getPolygonMode() != null) {
							label120:
							{
								var21 = this.appearances[submesh].getPolygonMode().getWinding() != 168 ? 1 : 0;
								int var10000;
								switch (this.appearances[submesh].getPolygonMode().getCulling()) {
									case 161:
										var10000 = var21 ^ 1;
										break;
									case 162:
										var10000 = 2;
										break;
									default:
										break label120;
								}

								var21 = var10000;
							}
						} else {
							var21 = 0;
						}

						TriangleStripArray tsa = (TriangleStripArray) this.submeshes[submesh];

						for (int i = 0; tsa.getIndices(i, triIndices); i++) {
							int vtxCount = vb.getVertexCount();

							if (triIndices[0] >= vtxCount || triIndices[1] >= vtxCount || triIndices[2] >= vtxCount) {
								throw new IllegalStateException("Index overflow: (" + triIndices[0] + ", " + triIndices[1] + ", " + triIndices[2] + ") >=" + vtxCount);
							}

							if (triIndices[0] < 0 || triIndices[1] < 0 || triIndices[2] < 0) {
								throw new IllegalStateException("Index underflow");
							}

							vb.getVertex(triIndices[0], vtxA);
							vb.getVertex(triIndices[1], vtxB);
							vb.getVertex(triIndices[2], vtxC);

							if (G3DUtils.intersectTriangle(var7, var8, vtxA, vtxB, vtxC, var14, triIndices[3] ^ var21) && ri.testDistance(var14.x)) {
								if (vb.getNormalVertex(triIndices[0], vtxA)) {
									vb.getNormalVertex(triIndices[1], vtxB);
									vb.getNormalVertex(triIndices[2], vtxC);

									normal = new float[3];

									normal[0] = vtxA.x * (1.0F - (var14.y + var14.z)) + vtxB.x * var14.y + vtxC.x * var14.z;
									normal[1] = vtxA.y * (1.0F - (var14.y + var14.z)) + vtxB.y * var14.y + vtxC.y * var14.z;
									normal[2] = vtxA.z * (1.0F - (var14.y + var14.z)) + vtxB.z * var14.y + vtxC.z * var14.z;
								}

								for (int var25 = 0; var25 < texS.length; ++var25) {
									int var10001;
									float var10002;
									float[] var26;
									if (vb.getTexVertex(triIndices[0], var25, vtxA)) {
										vb.getTexVertex(triIndices[1], var25, vtxB);
										vb.getTexVertex(triIndices[2], var25, vtxC);
										var13.x = vtxA.x * (1.0F - (var14.y + var14.z)) + vtxB.x * var14.y + vtxC.x * var14.z;
										var13.y = vtxA.y * (1.0F - (var14.y + var14.z)) + vtxB.y * var14.y + vtxC.y * var14.z;
										var13.z = 0.0F;
										var13.w = 1.0F;
										if (this.appearances[submesh] != null && this.appearances[submesh].getTexture(var25) != null) {
											this.appearances[submesh].getTexture(var25).getCompositeTransform(var15);
											var15.getImpl_().transform(var13);
											var13.mul(1.0F / var13.w);
										}

										texT[var25] = var13.x;
										var26 = texS;
										var10001 = var25;
										var10002 = var13.y;
									} else {
										texT[var25] = 0.0F;
										var26 = texS;
										var10001 = var25;
										var10002 = 0.0F;
									}

									var26[var10001] = var10002;
								}

								if (ri.endPick(var14.x, texT, texS, submesh, this, normal)) {
									var6 = true;
								}
							}
						}
					}
				}

				return var6;
			}
		} else {
			return false;
		}
	}
}
