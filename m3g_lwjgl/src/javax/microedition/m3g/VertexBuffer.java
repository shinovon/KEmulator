package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.Vector4f;
import emulator.graphics3D.lwjgl.Emulator3D;

public class VertexBuffer extends Object3D {

	private int vertexCount = 0;
	private VertexArray positions = null;
	private VertexArray normals = null;
	private VertexArray[] uvms;
	private VertexArray colors = null;

	private int defaultColor = -1;

	private float[] posScaleBias;
	private float[][] uvScaleBias;

	private int arraysCount = 0;

	public VertexBuffer() {
		this.uvms = new VertexArray[Emulator3D.NumTextureUnits];
		this.posScaleBias = new float[]{1, 0, 0, 0};
		this.uvScaleBias = new float[Emulator3D.NumTextureUnits][4];
	}

	protected Object3D duplicateObject() {
		VertexBuffer clone = (VertexBuffer) super.duplicateObject();
		clone.uvms = (VertexArray[]) uvms.clone();
		clone.posScaleBias = (float[]) posScaleBias.clone();
		clone.uvScaleBias = new float[Emulator3D.NumTextureUnits][4];

		for (int i = 0; i < Emulator3D.NumTextureUnits; ++i) {
			clone.uvScaleBias[i] = (float[]) uvScaleBias[i].clone();
		}

		return clone;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void setPositions(VertexArray newPoses, float scale, float[] bias) {
		if (newPoses != null && newPoses.getComponentCount() != 3) {
			throw new IllegalArgumentException();
		} else if (newPoses != null && bias != null && bias.length < 3) {
			throw new IllegalArgumentException();
		} else if (newPoses != null && vertexCount != 0 && newPoses.getVertexCount() != vertexCount) {
			throw new IllegalArgumentException();
		} else {
			removeReference(positions);

			if (newPoses != null) {
				if (positions == null) arraysCount++;

				vertexCount = newPoses.getVertexCount();
				positions = newPoses;

				posScaleBias[0] = scale;
				if (bias != null) {
					System.arraycopy(bias, 0, posScaleBias, 1, 3);
				} else {
					posScaleBias[1] = 0.0F;
					posScaleBias[2] = 0.0F;
					posScaleBias[3] = 0.0F;
				}
			} else if (positions != null) {
				positions = null;
				arraysCount--;
				vertexCount = arraysCount > 0 ? vertexCount : 0;
			}

			addReference(positions);
		}
	}

	public void setTexCoords(int index, VertexArray newUvm, float scale, float[] bias) {
		if (newUvm != null && newUvm.getComponentCount() != 2 && newUvm.getComponentCount() != 3) {
			throw new IllegalArgumentException();
		} else if (newUvm != null && vertexCount != 0 && newUvm.getVertexCount() != vertexCount) {
			throw new IllegalArgumentException();
		} else if (newUvm != null && bias != null && bias.length < newUvm.getComponentCount()) {
			throw new IllegalArgumentException();
		} else if (index >= 0 && index < Emulator3D.NumTextureUnits) {
			removeReference(uvms[index]);

			if (newUvm != null) {
				if (uvms[index] == null) arraysCount++;

				vertexCount = newUvm.getVertexCount();
				uvms[index] = newUvm;

				uvScaleBias[index][0] = scale;
				uvScaleBias[index][1] = 0.0F;
				uvScaleBias[index][2] = 0.0F;
				uvScaleBias[index][3] = 0.0F;
				if (bias != null) {
					System.arraycopy(bias, 0, uvScaleBias[index], 1, newUvm.getComponentCount());
				}
			} else if (uvms[index] != null) {
				uvms[index] = null;
				arraysCount--;
				vertexCount = arraysCount > 0 ? vertexCount : 0;
			}

			addReference(uvms[index]);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public void setNormals(VertexArray newNorms) {
		if (newNorms != null && newNorms.getComponentCount() != 3) {
			throw new IllegalArgumentException();
		} else if (newNorms != null && vertexCount != 0 && newNorms.getVertexCount() != vertexCount) {
			throw new IllegalArgumentException();
		} else {
			removeReference(normals);

			if (newNorms != null) {
				if (normals == null) arraysCount++;

				vertexCount = newNorms.getVertexCount();
				normals = newNorms;
			} else if (normals != null) {
				normals = null;
				arraysCount--;
				vertexCount = arraysCount > 0 ? vertexCount : 0;
			}

			addReference(normals);
		}
	}

	public void setColors(VertexArray newCols) {
		if (newCols != null && newCols.getComponentType() != 1) {
			throw new IllegalArgumentException();
		} else if (newCols != null && newCols.getComponentCount() != 3 && newCols.getComponentCount() != 4) {
			throw new IllegalArgumentException();
		} else if (newCols != null && vertexCount != 0 && newCols.getVertexCount() != vertexCount) {
			throw new IllegalArgumentException();
		} else {
			removeReference(colors);

			if (newCols != null) {
				if (colors == null) arraysCount++;

				vertexCount = newCols.getVertexCount();
				colors = newCols;
			} else if (colors != null) {
				colors = null;
				arraysCount--;
				vertexCount = arraysCount > 0 ? vertexCount : 0;
			}

			addReference(colors);
		}
	}

	public VertexArray getPositions(float[] scaleBias) {
		if (scaleBias != null && scaleBias.length < 4) {
			throw new IllegalArgumentException();
		} else {
			if (positions != null && scaleBias != null) {
				System.arraycopy(posScaleBias, 0, scaleBias, 0, 4);
			}

			return positions;
		}
	}

	public VertexArray getTexCoords(int index, float[] scaleBias) {
		if (index >= 0 && index < Emulator3D.NumTextureUnits) {

			if (uvms[index] != null && scaleBias != null) {
				if (scaleBias.length < uvms[index].getComponentCount() + 1) {
					throw new IllegalArgumentException();
				}

				System.arraycopy(uvScaleBias[index], 0, scaleBias, 0, uvms[index].getComponentCount() + 1);
			}

			return uvms[index];
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public VertexArray getNormals() {
		return normals;
	}

	public VertexArray getColors() {
		return colors;
	}

	public void setDefaultColor(int col) {
		defaultColor = col;
	}

	public int getDefaultColor() {
		return defaultColor;
	}

	protected void updateProperty(int property, float[] value) {
		switch (property) {
			case AnimationTrack.ALPHA:
				defaultColor &= 0xffffff;
				defaultColor |= G3DUtils.getIntColor(value) & 0xff000000;
				return;
			case AnimationTrack.COLOR:
				defaultColor &= 0xff000000;
				defaultColor |= G3DUtils.getIntColor(value) & 0xffffff;
				return;
			default:
				super.updateProperty(property, value);
		}
	}

	protected boolean getNormalVertex(int vertexIndex, Vector4f vec) {
		if (normals == null) {
			return false;
		} else {
			float nx, ny, nz;

			if (normals.getComponentType() == 1) {
				byte[] tmpVec = new byte[3];
				normals.get(vertexIndex, 1, tmpVec);

				nx = tmpVec[0];
				ny = tmpVec[1];
				nz = tmpVec[2];
			} else {
				short[] tmpVec = new short[3];
				normals.get(vertexIndex, 1, tmpVec);

				nx = tmpVec[0];
				ny = tmpVec[1];
				nz = tmpVec[2];
			}

			vec.set(nx, ny, nz, 1.0F);
			return true;
		}
	}

	protected void getVertex(int vertexIndex, Vector4f vec) {
		float x, y, z;

		if (this.positions.getComponentType() == 1) {
			byte[] tmpVec = new byte[3];
			positions.get(vertexIndex, 1, tmpVec);

			x = tmpVec[0];
			y = tmpVec[1];
			z = tmpVec[2];
		} else {
			short[] tmpVec = new short[3];
			positions.get(vertexIndex, 1, tmpVec);

			x = tmpVec[0];
			y = tmpVec[1];
			z = tmpVec[2];
		}

		x *= posScaleBias[0];
		y *= posScaleBias[0];
		z *= posScaleBias[0];

		x += posScaleBias[1];
		y += posScaleBias[2];
		z += posScaleBias[3];

		vec.set(x, y, z, 1.0F);
	}

	protected boolean getTexVertex(int vertexIndex, int texSlot, Vector4f vec) {
		if (uvms[texSlot] == null) {
			return false;
		} else {
			float x, y, z;

			int componentCount = this.uvms[texSlot].getComponentCount();

			if (uvms[texSlot].getComponentType() == 1) {
				byte[] tmpVec = new byte[componentCount];
				uvms[texSlot].get(vertexIndex, 1, tmpVec);

				x = tmpVec[0];
				y = tmpVec[1];
				z = componentCount == 3 ? (float) tmpVec[2] : 0.0F;
			} else {
				short[] tmpVec = new short[componentCount];
				uvms[texSlot].get(vertexIndex, 1, tmpVec);

				x = tmpVec[0];
				y = tmpVec[1];
				z = componentCount == 3 ? (float) tmpVec[2] : 0.0F;
			}

			x *= uvScaleBias[texSlot][0];
			y *= uvScaleBias[texSlot][0];
			z *= uvScaleBias[texSlot][0];

			x += uvScaleBias[texSlot][1];
			y += uvScaleBias[texSlot][2];
			z += uvScaleBias[texSlot][3];

			vec.set(x, y, z, 1.0F);
			return true;
		}
	}
}
