/*
 * MIT License
 * Copyright (c) 2026 Yury Kharchenko
 * Copyright (c) 2026 Roman Lahin
 */

package com.mascotcapsule.micro3d.v3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ActionTable {
	Action[] actions;

	public ActionTable(byte[] data) {
		if (data == null) throw new NullPointerException();
		loadMtraData(data);
	}

	public ActionTable(String name) throws IOException {
		if (name == null) throw new NullPointerException();
		
		InputStream is = getClass().getResourceAsStream(name);
		if (is == null) throw new IOException("Resource not found: " + name);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[Math.max(1024, is.available())];
		
		int len;
		while ((len = is.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		
		is.close();
		
		loadMtraData(baos.toByteArray());
	}
	
	private final void loadMtraData(byte[] bytes) {
		Loader loader = new Loader(bytes);

		if (loader.readUByte() != 'M' || loader.readUByte() != 'T') {
			throw new RuntimeException("Not a MTRA file");
		}

		int version = loader.readUByte();
		if (loader.readUByte() != 0 || version < 2 || version > 5) {
			throw new RuntimeException("Unsupported version: " + version);
		}

		int numActions = loader.readUShort();
		int numBones = loader.readUShort();
		
		actions = new Action[numActions];
		
		//Number of bones by transform types
		int[] transTypeCounts = new int[8];
		for (int i = 0; i < 8; i++) {
			transTypeCounts[i] = loader.readUShort();
		}

		int dataSize = loader.readInt();
		//dataSize and transTypeCounts may be used for allocate memory and verify data

		for (int action = 0; action < numActions; action++) {
			int keyframes = loader.readUShort();
			Action act = new Action(keyframes, numBones);
			actions[action] = act;

			for (int bone = 0; bone < numBones; bone++) {
				act.boneAnims[bone] = readBoneAnim(loader);
			}
			
			if (version < 5) continue;
			
			//Dynamic polygons chunk
			int count = loader.readUShort();
			int[] dynamicPolys = new int[count * 2];
			actions[action].dynamicPolys = dynamicPolys;
			
			for (int j = 0; j < count; j++) {
				int frame = loader.readUShort();
				int pattern = loader.readInt();
				
				dynamicPolys[j * 2] = frame;
				dynamicPolys[j * 2 + 1] = pattern;
			}
		}
	}
	
	private final BoneAnim readBoneAnim(Loader loader) {
		int type = loader.readUByte();
		BoneAnim boneAnim = new BoneAnim(type);
		
		switch (type) {
			case 0:
				int[] tmpTrans = new int[12];
				for(int i = 0; i < 12; i++) tmpTrans[i] = loader.readShort();
				boneAnim.transform = new AffineTrans(tmpTrans);
				break;
			case 1:
				//Identity transform
				break;
			case 2: {
				boneAnim.translate = readFrames3D(loader);
				boneAnim.scale = readFrames3D(loader);
				boneAnim.rotate = readFrames3D(loader);
				boneAnim.roll = readFrames1D(loader);
				break;
			}
			case 3: {
				boneAnim.translate = readFrames3DConst(loader);
				boneAnim.rotate = readFrames3D(loader);
				boneAnim.roll = readFrames1DConst(loader);
				break;
			}
			case 4: {
				boneAnim.rotate = readFrames3D(loader);
				boneAnim.roll = readFrames1D(loader);
				break;
			}
			case 5: {
				boneAnim.rotate = readFrames3D(loader);
				break;
			}
			case 6: {
				boneAnim.translate = readFrames3D(loader);
				boneAnim.rotate = readFrames3D(loader);
				boneAnim.roll = readFrames1D(loader);
				break;
			}
			default:
				throw new RuntimeException("Invalid animation type: " + type);
		}
		
		return boneAnim;
	}
	
	private final short[] readFrames3D(Loader loader) {
		int count = loader.readUShort();
		short[] frames = new short[count * 4];
		
		//Keyframe, X, Y, Z
		for (int j = 0; j < count * 4; j += 4) {
			frames[j] = loader.readShort();
			frames[j + 1] = loader.readShort();
			frames[j + 2] = loader.readShort();
			frames[j + 3] = loader.readShort();
		}
		
		return frames;
	}
	
	private final short[] readFrames3DConst(Loader loader) {
		return new short[] {loader.readShort(), loader.readShort(), loader.readShort()};
	}
	
	private final short[] readFrames1D(Loader loader) {
		int count = loader.readUShort();
		short[] frames = new short[count * 2];
		
		//Keyframe, X
		for (int j = 0; j < count * 2; j += 2) {
			frames[j] = loader.readShort();
			frames[j + 1] = loader.readShort();
		}
		
		return frames;
	}
	
	private final short[] readFrames1DConst(Loader loader) {
		return new short[] {loader.readShort()};
	}

	public final void dispose() {
		actions = null;
	}

	public final int getNumAction() {
		return actions.length;
	}

	public final int getNumActions() {
		return actions.length;
	}

	public final int getNumFrame(int idx) {
		return getNumFrames(idx);
	}

	public final int getNumFrames(int idx) {
		if (idx < 0 || idx >= actions.length) {
			throw new IllegalArgumentException();
		}
		
		return actions[idx].keyFrames << 16;
	}

	static class Action {
		int keyFrames;
		BoneAnim[] boneAnims;
		int[] dynamicPolys;
		
		Action(int keyFrames, int bones) { 
			this.keyFrames = keyFrames; 
			boneAnims = new BoneAnim[bones];
		}
		
		final void updateBoneAnim(int boneIdx, int frame, AffineTrans localTrans, AffineTrans resultTrans) {
			if (boneIdx >= boneAnims.length) return;
			
			frame >>= 4; //Reduce precision to fp12 to avoid overflow
			boneAnims[boneIdx].setFrame(frame, localTrans, resultTrans);
		}
	
		final int getPattern(int frame, int defValue) {
			int[] dynamic = dynamicPolys;
			if (dynamic == null) return defValue;

			int iFrame = frame < 0 ? 0 : frame >> 16;
			for (int i = dynamic.length - 2; i >= 0; i -= 2) {
				if (dynamic[i] <= iFrame) return dynamic[i + 1];
			}

			return defValue;
		}
	}

	static class BoneAnim {
		static Vector3D tmp = new Vector3D();
		static AffineTrans tmpTrans = new AffineTrans();
		
		int type;
		AffineTrans transform;
		short[] translate, scale, rotate, roll;
		
		BoneAnim(int type) { this.type = type; }
		
		final void interp3D(int keyframe, short[] buffer, Vector3D v) {
			int keyframInt = keyframe >> 12;
			int keysCount = buffer.length >> 2;
			
			int max = keysCount - 1;
			
			if (keyframInt >= (buffer[max << 2] & 0xffff)) {
				max <<= 2;
				v.x = buffer[max + 1];
				v.y = buffer[max + 2];
				v.z = buffer[max + 3];
				return;
			}
			
			for (int i = (max << 2) - 4; i >= 0; i -= 4) {
				final int prevKey = buffer[i] & 0xffff;
				if (prevKey > keyframInt) continue;
				
				if (prevKey == keyframInt) {
					v.x = buffer[i + 1];
					v.y = buffer[i + 2];
					v.z = buffer[i + 3];
					return;
				}
				
				int nextKey = buffer[i + 4] & 0xffff;
				int delta = (keyframe - (prevKey << 12)) / (nextKey - prevKey);
				
				v.x = buffer[i + 1] + (((buffer[i + 5] - buffer[i + 1]) * delta) >> 12);
				v.y = buffer[i + 2] + (((buffer[i + 6] - buffer[i + 2]) * delta) >> 12);
				v.z = buffer[i + 3] + (((buffer[i + 7] - buffer[i + 3]) * delta) >> 12);
				return;
			}
		}
		
		final int interp1D(int keyframe, short[] buffer) {
			int keyframInt = keyframe >> 12;
			int keysCount = buffer.length >> 1;
			
			final int max = keysCount - 1;
			
			if (keyframInt >= (buffer[max << 1] & 0xffff)) {
				return buffer[(max << 1) + 1];
			}
			
			for (int i = (max << 1) - 2; i >= 0; i -= 2) {
				final int prevKey = buffer[i] & 0xffff;
				if (prevKey > keyframInt) continue;
				
				if (prevKey == keyframInt) {
					return buffer[i + 1];
				}
				
				int nextKey = buffer[i + 2] & 0xffff;
				int delta = (keyframe - (prevKey << 12)) / (nextKey - prevKey);
				
				return buffer[i + 1] + (((buffer[i + 3] - buffer[i + 1]) * delta) >> 12);
			}
		
			return 0;
		}
		
		final void setFrame(int frame, AffineTrans localTrans, AffineTrans resultTrans) {
			Vector3D tmp = BoneAnim.tmp;
			AffineTrans tmpTrans = BoneAnim.tmpTrans;
			if (localTrans == null) tmpTrans = resultTrans;
			
			switch (type) {
				case 0: {
					if (localTrans != null) resultTrans.mul(localTrans, transform);
					else resultTrans.set(transform);
					return;
				}
				case 1: {
					if (localTrans != null) resultTrans.set(localTrans);
					else resultTrans.setIdentity();
					return;
				}
				case 2: {
					//Translate
					interp3D(frame, translate, tmp);
					tmpTrans.m03 = tmp.x;
					tmpTrans.m13 = tmp.y;
					tmpTrans.m23 = tmp.z;

					//Rotate
					interp3D(frame, rotate, tmp);
					rotate(tmpTrans, tmp);

					//Roll
					roll(tmpTrans, interp1D(frame, roll));

					//Scale
					interp3D(frame, scale, tmp);
					tmpTrans.scale(tmp);
					break;
				}
				case 3: {
					//Translate (for all frames)
					tmpTrans.m03 = translate[0];
					tmpTrans.m13 = translate[1];
					tmpTrans.m23 = translate[2];

					//Rotate
					interp3D(frame, rotate, tmp);
					rotate(tmpTrans, tmp);

					//Roll (for all frames)
					roll(tmpTrans, roll[0]);
					break;
				}
				case 4: {
					//Translate
					tmpTrans.m03 = tmpTrans.m13 = tmpTrans.m23 = 0;
					
					//Rotate
					interp3D(frame, rotate, tmp);
					rotate(tmpTrans, tmp);

					//Roll
					roll(tmpTrans, interp1D(frame, roll));
					break;
				}
				case 5: {
					//Translate
					tmpTrans.m03 = tmpTrans.m13 = tmpTrans.m23 = 0;
					
					//Rotate
					interp3D(frame, rotate, tmp);
					rotate(tmpTrans, tmp);
					break;
				}
				case 6: {
					//Translate
					interp3D(frame, translate, tmp);
					tmpTrans.m03 = tmp.x;
					tmpTrans.m13 = tmp.y;
					tmpTrans.m23 = tmp.z;

					//Rotate
					interp3D(frame, rotate, tmp);
					rotate(tmpTrans, tmp);

					//Roll
					roll(tmpTrans, interp1D(frame, roll));
					break;
				}
			}
			
			if (localTrans != null) resultTrans.mul(localTrans, tmpTrans);
		}

		private final void rotate(AffineTrans trans, Vector3D v) {
			//Normalize direction vector
			v.unit();
			int x = v.x, y = v.y, z = v.z;

			int xx = (x * x + 2048) >> 12;
			int yy = (y * y + 2048) >> 12;
			
			if (xx > 0 || yy > 0) {
				int a = ((4096 - z) << 12) / (yy + xx);
				int b = (a * -((x * y + 2048) >> 12)) >> 12;
				
				trans.m00 = z + ((yy * a + 2048) >> 12);
				trans.m01 = b;
				trans.m02 = x;
				trans.m10 = b;
				trans.m11 = z + ((xx * a + 2048) >> 12);
				trans.m12 = y;
				trans.m20 = -x;
				trans.m21 = -y;
			} else {
				trans.m00 = 4096;
				trans.m01 = 0;
				trans.m02 = 0;
				trans.m10 = 0;
				trans.m11 = z;
				trans.m12 = 0;
				trans.m20 = 0;
				trans.m21 = 0;
			}
			
			trans.m22 = z;
		}

		private final void roll(AffineTrans trans, int angle) {
			int s = Util3D.sin(angle);
			int c = Util3D.cos(angle);

			int m00 = trans.m00;
			int m01 = trans.m01;
			int m10 = trans.m10;
			int m11 = trans.m11;
			int m20 = trans.m20;
			int m21 = trans.m21;

			trans.m00 = (m00 * c + m01 * s + 2048) >> 12;
			trans.m01 = (m01 * c - m00 * s + 2048) >> 12;
			trans.m10 = (m10 * c + m11 * s + 2048) >> 12;
			trans.m11 = (m11 * c - m10 * s + 2048) >> 12;
			trans.m20 = (m20 * c + m21 * s + 2048) >> 12;
			trans.m21 = (m21 * c - m20 * s + 2048) >> 12;
		}
	}
}