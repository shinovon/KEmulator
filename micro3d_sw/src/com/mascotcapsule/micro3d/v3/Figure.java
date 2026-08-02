/*
 * MIT License
 * Copyright (c) 2026 Yury Kharchenko
 * Copyright (c) 2026 Roman Lahin
 */

package com.mascotcapsule.micro3d.v3;

import com.mascotcapsule.micro3d.v3.ActionTable.Action;
import emulator.custom.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Figure {
	private static final int[] POOL_NORMALS = {0, 0, 4096, 0, 0, -4096, 0, 0};
	private static final int[] SIZES_VTX = {8, 10, 13, 16};

	static final int MAT_COLORKEY = 1;
	static final int MAT_BLEND_HALF = 2;
	static final int MAT_BLEND_ADD = 4;
	static final int MAT_BLEND_SUB = MAT_BLEND_HALF | MAT_BLEND_ADD;
	static final int MAT_UNUSED_1 = 8; //Unused in mcv3
	static final int MAT_DOUBLE_FACE = 0x10;
	static final int MAT_LIGHTING = 0x20;
	static final int MAT_SPECULAR = 0x40; //Env mapping, actually
	static final int MAT_FLAT_NORMAL = 0x80; //First vertex should be used for lighting and env mapping, not supported in mcv3
	static final int MAT_ZSORT_NEAR = 0x100;
	static final int MAT_ZSORT_FAR = 0x200;
	
	static final int MAT_BLEND_MASK = MAT_BLEND_SUB;
	static final int MAT_ZSORT_MASK = MAT_ZSORT_NEAR | MAT_ZSORT_FAR;
	static final int MAT_MASK = 
			MAT_COLORKEY | MAT_BLEND_MASK | MAT_DOUBLE_FACE | MAT_LIGHTING |
			MAT_SPECULAR | MAT_FLAT_NORMAL | MAT_ZSORT_MASK;

	//Geometry data
	short[] vertices, normals;
	int numVertices;

	//Internal representation of polygon data
	//Poly C3: [mat (10 bits)], [color idx (8 bit)], [v0], [v1], [v2]
	//Poly C4: [mat (10 bits)], [color idx (8 bit)], [v0], [v1], [v2], [v3]
	//Poly T3: [mat (10 bits)], [v0], [v1], [v2], [au, av], [bu, bv], [cu, cv]
	//Poly T4: [mat (10 bits)], [v0], [v1], [v2], [v3], [au, av], [bu, bv], [cu, cv], [du, dv]
	static final int TRI_C_STRIDE = 5, QUAD_C_STRIDE = 6;
	static final int TRI_T_STRIDE = 7, QUAD_T_STRIDE = 9;
	
	short[] polyC3, polyC4, polyT3, polyT4;
	int numPolyC3, numPolyC4, numPolyT3, numPolyT4;
	
	//Color palette (ARGB format)
	int[] colors;
	int allMatsAnd = 0xffffffff, allMatsOr = 0x00000000;

	static class Bone {
		int startVertex;
		int numVertices;
		int parentIndex;
		AffineTrans localTrans;
	}
	Bone[] bones;
	int numBones;

	ActionTable actionTable;
	Action activeAction;
	int actionFrame;

	Texture[] textures;
	Texture[] tmpTexArray = new Texture[1];
	int textureIndex = -1;

	//[pattern [0-32]][texture id + 1 (colored pols is 0)][poly3 idx start, poly4 idx start, poly3 cnt, poly4 cnt]
	int[][][] patterns;
	int selectedPattern;

	public Figure(byte[] b) {
		if (b == null) throw new NullPointerException();
		loadMBAC(b);
	}

	public Figure(String name) throws IOException {
		if (name == null) throw new NullPointerException();

		InputStream is = ResourceManager.getResourceAsStream(name);
		if (is == null) throw new IOException("Resource not found: " + name);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[Math.max(1024, is.available())];

		int len;
		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}

		is.close();

		loadMBAC(baos.toByteArray());
	}

	private final void loadMBAC(byte[] data) {
		Loader loader = new Loader(data);

		try {
			if (loader.readUByte() != 'M' || loader.readUByte() != 'B') {
				throw new RuntimeException("Not a MBAC file");
			}

			int version = loader.readUByte();
			if (loader.readUByte() != 0 || version < 2 || version > 5) {
				throw new RuntimeException("Unsupported MBAC version: " + version);
			}

			int vertexFormat = 1;
			int normalFormat = 0;
			int polygonFormat = 1;
			int boneFormat = 1;

			if (version > 3) {
				vertexFormat = loader.readUByte();
				normalFormat = loader.readUByte();
				polygonFormat = loader.readUByte();
				boneFormat = loader.readUByte();
			}

			if (boneFormat != 1) {
				throw new RuntimeException("Unexpected bone format: " + boneFormat);
			}

			numVertices = loader.readUShort();
			numPolyT3 = loader.readUShort();
			numPolyT4 = loader.readUShort();
			numBones = loader.readUShort();

			numPolyC3 = 0;
			numPolyC4 = 0;
			int numTextures = 1;
			int numPatterns = 1;
			int numColors = 0;

			if (polygonFormat >= 3) {
				numPolyC3 = loader.readUShort();
				numPolyC4 = loader.readUShort();
				numTextures = loader.readUShort();
				numPatterns = loader.readUShort();
				numColors = loader.readUShort();
			}

			if (numVertices > 21845 || numTextures > 16 || numPatterns > 33 || numColors > 256) {
				throw new RuntimeException(
						"MBAC format error:\n" +
						"numVertices=" + numVertices + " numTextures=" + numTextures + " " +
						"numPatterns=" + numPatterns + " numColors=" + numColors + "\n"
				);
			}

			//Patterns
			patterns = new int[numPatterns][(numTextures + 1)][4];
			if (version == 5) {
				int c3Offset = 0, c4Offset = 0;
				int t3Offset = 0, t4Offset = 0;
				
				for (int i = 0; i < numPatterns; i++) {
					int[][] pattern = patterns[i];
					pattern[0][0] = c3Offset;
					pattern[0][1] = c4Offset;
					pattern[0][2] = loader.readUShort();
					pattern[0][3] = loader.readUShort();
					
					c3Offset += pattern[0][2];
					c4Offset += pattern[0][3];

					for (int j = 1; j <= numTextures; j++) {
						pattern[j][0] = t3Offset;
						pattern[j][1] = t4Offset;
						pattern[j][2] = loader.readUShort();
						pattern[j][3] = loader.readUShort();

						t3Offset += pattern[j][2];
						t4Offset += pattern[j][3];
					}
				}
			} else {
				patterns[0] = new int[][] {
					new int[]{0, 0, numPolyC3, numPolyC4}, 
					new int[]{0, 0, numPolyT3, numPolyT4}
				};
			}

			//Vertices
			vertices = new short[numVertices * 3];

			if (vertexFormat == 1) readVerticesV1(loader);
			else if (vertexFormat == 2) readVerticesV2(loader);
			else throw new RuntimeException("Unexpected vertexFormat: " + vertexFormat);
			loader.clearCache();

			//Normals
			if (normalFormat != 0) {
				normals = new short[numVertices * 3];

				if (normalFormat == 1) readNormalsV1(loader);
				else if (normalFormat == 2) readNormalsV2(loader);
				else throw new RuntimeException("Unsupported normalFormat: " + normalFormat);
			}
			loader.clearCache();

			//Colored polygons
			if (numPolyC3 + numPolyC4 > 0) {
				polyC3 = new short[numPolyC3 * TRI_C_STRIDE];
				polyC4 = new short[numPolyC4 * QUAD_C_STRIDE];
				colors = new int[numColors];
				
				readPolyC(loader, numPolyC3, numPolyC4, numColors);
			}

			//Textured polygons
			polyT3 = new short[numPolyT3 * TRI_T_STRIDE];
			polyT4 = new short[numPolyT4 * QUAD_T_STRIDE];

			if(numPolyT3 + numPolyT4 > 0) {
				switch (polygonFormat) {
					case 1: readPolyV1(loader, numPolyT3, numPolyT4); break;
					case 2: readPolyV2and3(loader, numPolyT3, numPolyT4, 2); break;
					case 3: readPolyV2and3(loader, numPolyT3, numPolyT4, 3); break;
					default: throw new RuntimeException("Unexpected polygonFormat: " + polygonFormat);
				}
			}
			loader.clearCache();

			//Bones
			readBones(loader);

		} catch (IOException e) {
			throw new RuntimeException("MBAC parse error: " + e.getMessage());
		}
	}

	private final void readVerticesV1(Loader loader) throws IOException {
		for (int i = 0; i < numVertices * 3; i++) {
			vertices[i] = loader.readShort();
		}
	}

	private final void readVerticesV2(Loader loader) throws IOException {
		for(int vIdx = 0; vIdx < numVertices * 3;) {
			int chunk = loader.readUBits(8);
			int type = chunk >> 6;
			int size = SIZES_VTX[type];
			int count = (chunk & 0x3F) + 1;

			for (int i = 0; i < count; i++, vIdx += 3) {
				vertices[vIdx    ] = (short) loader.readBits(size);
				vertices[vIdx + 1] = (short) loader.readBits(size);
				vertices[vIdx + 2] = (short) loader.readBits(size);
			}
		}
	}

	private final void readNormalsV1(Loader loader) throws IOException {
		Vector3D tmpVec = new Vector3D();

		for (int i = 0; i < numVertices; i++) {
			tmpVec.set(loader.readShort(), loader.readShort(), loader.readShort());
			tmpVec.unit();

			vertices[i * 3    ] = (short) tmpVec.x;
			vertices[i * 3 + 1] = (short) tmpVec.y;
			vertices[i * 3 + 2] = (short) tmpVec.z;
		}
	}

	private final void readNormalsV2(Loader loader) throws IOException {
		for (int i = 0; i < numVertices; i++) {
			int x = loader.readUBits(7);
			int y, z;

			if (x == 64) {
				int type = loader.readUBits(3);
				z = POOL_NORMALS[type];
				y = POOL_NORMALS[type + 1];
				x = POOL_NORMALS[type + 2];
			} else {
				//Restore sign and expand to 12 bits
				x = (x << 25) >> 19;
				y = (loader.readUBits(7) << 25) >> 19;
				
				int sign = loader.readUBits(1);
				int dq = 4096 * 4096 - x * x - y * y;
				z = dq > 0 ? (int) (Math.sqrt(dq) + 0.5) : 0;
				if (sign == 1) z = -z;
			}

			normals[i * 3    ] = (short) x;
			normals[i * 3 + 1] = (short) y;
			normals[i * 3 + 2] = (short) z;
		}
	}
	
	private final boolean isPolyFlat(int a, int b, int c, int d) {
		if (normals == null) return false;
		
		int x1 = normals[a * 3	];
		int y1 = normals[a * 3 + 1];
		int z1 = normals[a * 3 + 2];
		
		int x2 = normals[b * 3	];
		int y2 = normals[b * 3 + 1];
		int z2 = normals[b * 3 + 2];
		
		if (x1 != x2 || y1 != y2 || z1 != z2) return false;
		
		x2 = normals[c * 3	];
		y2 = normals[c * 3 + 1];
		z2 = normals[c * 3 + 2];
		
		if (x1 != x2 || y1 != y2 || z1 != z2) return false;
		
		if (d >= 0) {
			x2 = normals[d * 3	];
			y2 = normals[d * 3 + 1];
			z2 = normals[d * 3 + 2];

			if (x1 != x2 || y1 != y2 || z1 != z2) return false;
		}
		
		return true;
	}

	private final void readPolyC(Loader loader, int numC3, int numC4, int numColors) throws IOException {
		int matBitSize = loader.readUByte();
		int vertexIdxSize = loader.readUByte();
		int colorBitSize = loader.readUByte();
		int colorIdxBitSize = loader.readUByte();
		loader.readUByte(); //Unused in mcv3

		//Read color palette
		int maxColorV = (1 << colorBitSize) - 1;

		for (int i = 0; i < numColors; i++) {
			int r = (255 * loader.readUBits(colorBitSize)) / maxColorV;
			int g = (255 * loader.readUBits(colorBitSize)) / maxColorV;
			int b = (255 * loader.readUBits(colorBitSize)) / maxColorV;

			colors[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
		}

		//Read polygons
		int offset = 0;

		for (int i = 0; i < numC3; i++) {
			int material = loader.readUBits(matBitSize) << 1;
			if ((material & 0xFC09) != 0) {
				throw new RuntimeException("Unexpected material: " + material);
			}
			
			int a = loader.readUBits(vertexIdxSize);
			int b = loader.readUBits(vertexIdxSize);
			int c = loader.readUBits(vertexIdxSize);
			
			int colorIdx = loader.readUBits(colorIdxBitSize);

			addColorTriangle(offset, material, a, b, c, colorIdx);
			offset += TRI_C_STRIDE;
		}

		offset = 0;

		for (int i = 0; i < numC4; i++) {
			int material = loader.readUBits(matBitSize) << 1;
			if ((material & 0xFC09) != 0) {
				throw new RuntimeException("Unexpected material: " + material);
			}
			
			int a = loader.readUBits(vertexIdxSize);
			int b = loader.readUBits(vertexIdxSize);
			int c = loader.readUBits(vertexIdxSize);
			int d = loader.readUBits(vertexIdxSize);
			
			int colorIdx = loader.readUBits(colorIdxBitSize);

			addColorQuad(offset, material, a, b, c, d, colorIdx);
			offset += QUAD_C_STRIDE;
		}
	}

	private final void addColorTriangle(int offset, int mat, int a, int b, int c, int colorIdx) {
		if (isPolyFlat(a, b, c, -1)) mat |= Figure.MAT_FLAT_NORMAL;
		
		polyC3[offset] = (short) (mat & 0x3f6);
		polyC3[offset + 1] = (short) colorIdx;
		polyC3[offset + 2] = (short) a;
		polyC3[offset + 3] = (short) b;
		polyC3[offset + 4] = (short) c;

		allMatsAnd &= mat;
		allMatsOr |= mat;
	}

	private final void addColorQuad(int offset, int mat, int a, int b, int c, int d, int colorIdx) {
		if (isPolyFlat(a, b, c, d)) mat |= Figure.MAT_FLAT_NORMAL;
		
		polyC4[offset] = (short) (mat & 0x3f6);
		polyC4[offset + 1] = (short) colorIdx;
		polyC4[offset + 2] = (short) a;
		polyC4[offset + 3] = (short) b;
		polyC4[offset + 4] = (short) c;
		polyC4[offset + 5] = (short) d;

		allMatsAnd &= mat;
		allMatsOr |= mat;
	}

	private final void readPolyV1(Loader loader, int numT3, int numT4) throws IOException {
		int offset = 0;

		for (int i = 0; i < numT3; i++) {
			int material = loader.readUShort();
			if ((material & 0xFFF9) != 0) {
				throw new IOException("Unexpected material: " + material);
			}
			
			//All flags except for colorkey and doubleface are ignored when importing v1 polys in mcv3
			material = 
					((material & 4) != 0 ? MAT_DOUBLE_FACE : 0) | 
					((material & 2) != 0 ? MAT_COLORKEY : 0);

			int a = loader.readUShort();
			int b = loader.readUShort();
			int c = loader.readUShort();

			int u0 = loader.readByte() & 0xFF, v0 = loader.readByte() & 0xFF;
			int u1 = loader.readByte() & 0xFF, v1 = loader.readByte() & 0xFF;
			int u2 = loader.readByte() & 0xFF, v2 = loader.readByte() & 0xFF;

			addTexTriangle(offset, material, a, b, c, u0, v0, u1, v1, u2, v2);
			offset += TRI_T_STRIDE;
		}
		
		offset = 0;

		for (int i = 0; i < numT4; i++) {
			int material = loader.readUShort();
			if ((material & 0xFFF8) != 0 || (material & 1) == 0) {
				throw new IOException("Unexpected material: " + material);
			}
			
			material = 
					((material & 4) != 0 ? MAT_DOUBLE_FACE : 0) | 
					((material & 2) != 0 ? MAT_COLORKEY : 0);

			int a = loader.readUShort();
			int b = loader.readUShort();
			int c = loader.readUShort();
			int d = loader.readUShort();

			int uA = loader.readByte() & 0xFF, vA = loader.readByte() & 0xFF;
			int uB = loader.readByte() & 0xFF, vB = loader.readByte() & 0xFF;
			int uC = loader.readByte() & 0xFF, vC = loader.readByte() & 0xFF;
			int uD = loader.readByte() & 0xFF, vD = loader.readByte() & 0xFF;

			addTexQuad(offset, material, a, b, c, d, uA, vA, uB, vB, uC, vC, uD, vD);
			offset += QUAD_T_STRIDE;
		}
	}

	private final void readPolyV2and3(Loader loader, int numT3, int numT4, int version) throws IOException {
		int matBitSize, vertexIdxSize, uvBitSize;
		int supportedMatMask;
		
		if (version == 2) {
			matBitSize = loader.readUByte();
			vertexIdxSize = loader.readUByte();
			uvBitSize = 7;
			
			supportedMatMask = 0xFF88;
		} else {
			matBitSize = loader.readUBits(8);
			vertexIdxSize = loader.readUBits(8);
			uvBitSize = loader.readUBits(8);
			loader.readUBits(8); //Unused in mcv3
			
			supportedMatMask = 0xFC08;
		}
		
		int offset = 0;

		for (int i = 0; i < numT3; i++) {
			int material = loader.readUBits(matBitSize);
			if ((material & supportedMatMask) != 0) {
				throw new IOException("Unexpected material: " + material);
			}
			
			int a = loader.readUBits(vertexIdxSize);
			int b = loader.readUBits(vertexIdxSize);
			int c = loader.readUBits(vertexIdxSize);

			int u0 = loader.readUBits(uvBitSize), v0 = loader.readUBits(uvBitSize);
			int u1 = loader.readUBits(uvBitSize), v1 = loader.readUBits(uvBitSize);
			int u2 = loader.readUBits(uvBitSize), v2 = loader.readUBits(uvBitSize);

			addTexTriangle(offset, material, a, b, c, u0, v0, u1, v1, u2, v2);
			offset += TRI_T_STRIDE;
		}
		
		offset = 0;

		for (int i = 0; i < numT4; i++) {
			int material = loader.readUBits(matBitSize);
			if ((material & supportedMatMask) != 0) {
				throw new IOException("Unexpected material: " + material);
			}
			
			int a = loader.readUBits(vertexIdxSize);
			int b = loader.readUBits(vertexIdxSize);
			int c = loader.readUBits(vertexIdxSize);
			int d = loader.readUBits(vertexIdxSize);

			int uA = loader.readUBits(uvBitSize), vA = loader.readUBits(uvBitSize);
			int uB = loader.readUBits(uvBitSize), vB = loader.readUBits(uvBitSize);
			int uC = loader.readUBits(uvBitSize), vC = loader.readUBits(uvBitSize);
			int uD = loader.readUBits(uvBitSize), vD = loader.readUBits(uvBitSize);

			addTexQuad(offset, material, a, b, c, d, uA, vA, uB, vB, uC, vC, uD, vD);
			offset += QUAD_T_STRIDE;
		}
	}

	private final void addTexTriangle(
			int offset, int mat, 
			int a, int b, int c, 
			int au, int av, 
			int bu, int bv, 
			int cu, int cv
	) {
		if (isPolyFlat(a, b, c, -1)) mat |= Figure.MAT_FLAT_NORMAL;
		
		polyT3[offset] = (short) (mat & MAT_MASK);
		polyT3[offset + 1] = (short) a;
		polyT3[offset + 2] = (short) b;
		polyT3[offset + 3] = (short) c;
		polyT3[offset + 4] = (short) ((au << 8) | av);
		polyT3[offset + 5] = (short) ((bu << 8) | bv);
		polyT3[offset + 6] = (short) ((cu << 8) | cv);

		allMatsAnd &= mat;
		allMatsOr |= mat;
	}

	private final void addTexQuad(
			int offset, int mat, 
			int a, int b, int c, int d,
			int au, int av, 
			int bu, int bv, 
			int cu, int cv,
			int du, int dv
	) {
		if (isPolyFlat(a, b, c, d)) mat |= Figure.MAT_FLAT_NORMAL;
		
		polyT4[offset] = (short) (mat & MAT_MASK);
		polyT4[offset + 1] = (short) a;
		polyT4[offset + 2] = (short) b;
		polyT4[offset + 3] = (short) c;
		polyT4[offset + 4] = (short) d;
		polyT4[offset + 5] = (short) ((au << 8) | av);
		polyT4[offset + 6] = (short) ((bu << 8) | bv);
		polyT4[offset + 7] = (short) ((cu << 8) | cv);
		polyT4[offset + 8] = (short) ((du << 8) | dv);

		allMatsAnd &= mat;
		allMatsOr |= mat;
	}

	private final void readBones(Loader loader) throws IOException {
		bones = new Bone[numBones];
		
		int vertexOffset = 0;
		int[] tmpTrans = new int[12];

		for (int i = 0; i < numBones; i++) {
			Bone bone = new Bone();
			bones[i] = bone;

			bone.numVertices = loader.readUShort();
			bone.parentIndex = loader.readShort();
			bone.startVertex = vertexOffset;

			for (int j = 0; j < 12; j++) {
				tmpTrans[j] = loader.readShort();
			}

			bone.localTrans = new AffineTrans(tmpTrans);
			if (bone.localTrans.isIdentity()) bone.localTrans = null;

			vertexOffset += bone.numVertices;
		}
	}

	public final void dispose() {
		vertices = null; normals = null; 
		polyC3 = null; polyC4 = null;
		polyT3 = null; polyT4 = null;
		colors = null;
		
		bones = null;
		
		actionTable = null;
		activeAction = null;

		textures = null;
		tmpTexArray = null;
		
		patterns = null;
	}

	public final void setPosture(ActionTable act, int action, int frame) {
		if (act == null) throw new NullPointerException();
		if (action < 0 || action >= act.getNumActions()) {
			throw new IllegalArgumentException();
		}

		actionTable = act;
		activeAction = act.actions[action];
		actionFrame = frame < 0 ? 0 : frame;
		selectedPattern = activeAction.getPattern(frame, selectedPattern);
	}

	final void updateBoneTrans(int boneIdx, AffineTrans viewTrans, AffineTrans[] tmpTrans) {
		Bone bone = bones[boneIdx];
		AffineTrans localTrans = bone.localTrans;
		AffineTrans globalTrans = tmpTrans[boneIdx];
		
		if (activeAction != null) {
			activeAction.updateBoneAnim(boneIdx, actionFrame, localTrans, globalTrans);
			localTrans = globalTrans;
		}

		if (bone.parentIndex != -1) {
			if (localTrans != null) globalTrans.mulA2(tmpTrans[bone.parentIndex], localTrans);
			else globalTrans.set(tmpTrans[bone.parentIndex]);
		} else {
			if (localTrans != null) {
				if (viewTrans != null) globalTrans.mulA2(viewTrans, localTrans);
				else globalTrans.set(localTrans);
			} else {
				if (viewTrans != null) globalTrans.set(viewTrans);
				else globalTrans.setIdentity();
			}
		}
	}


	public final Texture getTexture() {
		return textureIndex < 0 ? null : textures[textureIndex];
	}

	public final void setTexture(Texture t)  {
		if (t == null) throw new NullPointerException();
		if (!t.isForModel) throw new IllegalArgumentException();

		if(textures == null || textures.length != 1) textures = tmpTexArray;

		textures[0] = t;
		textureIndex = 0;
	}

	public final void setTexture(Texture[] t) {
		if (t == null) throw new NullPointerException();
		if (t.length == 0) throw new IllegalArgumentException();

		for (int i = 0; i < t.length; i++) {
			Texture tex = t[i];

			if (tex == null) throw new NullPointerException();
			if (!tex.isForModel) throw new IllegalArgumentException();
		}

		textures = t;
		textureIndex = -1;
	}

	public final int getNumTextures() {
		return textures == null ? 0 : textures.length;
	}

	public final void selectTexture(int idx) {
		if (idx < 0 || idx >= getNumTextures()) {
			throw new IllegalArgumentException();
		}

		textureIndex = idx;
	}

	public final int getNumPattern() { return patterns.length; }
	public final void setPattern(int idx) { this.selectedPattern = idx; }
}