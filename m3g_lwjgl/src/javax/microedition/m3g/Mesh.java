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
				boolean hit = false;
				Vector4f rayOrigin = new Vector4f(ray[0], ray[1], ray[2], 1.0F);
				Vector4f rayDirection = new Vector4f(ray[3], ray[4], ray[5], 1.0F);
				Transform inverseTransform;
				(inverseTransform = new Transform()).set(transform);
				inverseTransform.getImpl_().invert();
				inverseTransform.getImpl_().transform(rayOrigin);
				inverseTransform.getImpl_().transform(rayDirection);
				rayOrigin.mul(1.0F / rayOrigin.w);
				rayDirection.mul(1.0F / rayDirection.w);
				rayDirection.sub(rayOrigin);
				Vector4f vtxA = new Vector4f();
				Vector4f vtxB = new Vector4f();
				Vector4f vtxC = new Vector4f();
				Vector4f texCoord = new Vector4f();
				Vector4f hitInfo = new Vector4f();
				Transform textureTransform = new Transform();
				int[] triIndices = new int[4];
				float[] texS = new float[Emulator3D.NumTextureUnits];
				float[] texT = new float[Emulator3D.NumTextureUnits];
				float[] normal = null;

				for (int submesh = 0; submesh < this.submeshes.length; ++submesh) {
					if (this.appearances[submesh] != null && this.submeshes[submesh] != null) {
						int cullMode;
						if (this.appearances[submesh].getPolygonMode() != null) {
							label120:
							{
								cullMode = this.appearances[submesh].getPolygonMode().getWinding() != 168 ? 1 : 0;
								int resolvedCullMode;
								switch (this.appearances[submesh].getPolygonMode().getCulling()) {
									case 161:
										resolvedCullMode = cullMode ^ 1;
										break;
									case 162:
										resolvedCullMode = 2;
										break;
									default:
										break label120;
								}

								cullMode = resolvedCullMode;
							}
						} else {
							cullMode = 0;
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

							if (G3DUtils.intersectTriangle(rayOrigin, rayDirection, vtxA, vtxB, vtxC, hitInfo, triIndices[3] ^ cullMode) && ri.testDistance(hitInfo.x)) {
								if (vb.getNormalVertex(triIndices[0], vtxA)) {
									vb.getNormalVertex(triIndices[1], vtxB);
									vb.getNormalVertex(triIndices[2], vtxC);

									normal = new float[3];

									normal[0] = vtxA.x * (1.0F - (hitInfo.y + hitInfo.z)) + vtxB.x * hitInfo.y + vtxC.x * hitInfo.z;
									normal[1] = vtxA.y * (1.0F - (hitInfo.y + hitInfo.z)) + vtxB.y * hitInfo.y + vtxC.y * hitInfo.z;
									normal[2] = vtxA.z * (1.0F - (hitInfo.y + hitInfo.z)) + vtxB.z * hitInfo.y + vtxC.z * hitInfo.z;
								}

								for (int unit = 0; unit < texS.length; ++unit) {
									int targetIndex;
									float targetValue;
									float[] targetArray;
									if (vb.getTexVertex(triIndices[0], unit, vtxA)) {
										vb.getTexVertex(triIndices[1], unit, vtxB);
										vb.getTexVertex(triIndices[2], unit, vtxC);
										texCoord.x = vtxA.x * (1.0F - (hitInfo.y + hitInfo.z)) + vtxB.x * hitInfo.y + vtxC.x * hitInfo.z;
										texCoord.y = vtxA.y * (1.0F - (hitInfo.y + hitInfo.z)) + vtxB.y * hitInfo.y + vtxC.y * hitInfo.z;
										texCoord.z = 0.0F;
										texCoord.w = 1.0F;
										if (this.appearances[submesh] != null && this.appearances[submesh].getTexture(unit) != null) {
											this.appearances[submesh].getTexture(unit).getCompositeTransform(textureTransform);
											textureTransform.getImpl_().transform(texCoord);
											texCoord.mul(1.0F / texCoord.w);
										}

										texT[unit] = texCoord.x;
										targetArray = texS;
										targetIndex = unit;
										targetValue = texCoord.y;
									} else {
										texT[unit] = 0.0F;
										targetArray = texS;
										targetIndex = unit;
										targetValue = 0.0F;
									}

									targetArray[targetIndex] = targetValue;
								}

								if (ri.endPick(hitInfo.x, texT, texS, submesh, this, normal)) {
									hit = true;
								}
							}
						}
					}
				}

				return hit;
			}
		} else {
			return false;
		}
	}
}
