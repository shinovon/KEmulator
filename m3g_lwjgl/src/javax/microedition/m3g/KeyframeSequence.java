package javax.microedition.m3g;

import emulator.graphics3D.Quaternion;

public class KeyframeSequence extends Object3D {
	public static final int CONSTANT = 192;
	public static final int LOOP = 193;
	public static final int LINEAR = 176;
	public static final int SLERP = 177;
	public static final int SPLINE = 178;
	public static final int SQUAD = 179;
	public static final int STEP = 180;
	private int keyframeCount;
	private int componentCount;
	private int interpolationType;
	private float[][] aFloatArrayArray144;
	private int[] keyframes;
	private int validRangeFirst;
	private int validRangeLast;
	private int duration;
	private int repeatMode;
	private boolean dirty;
	private float[][] inTangent;
	private float[][] outTangent;
	private Quaternion[] a;
	private Quaternion[] b;

	protected Object3D duplicateObject() {
		KeyframeSequence var1;
		(var1 = (KeyframeSequence) super.duplicateObject()).keyframes = (int[]) this.keyframes.clone();
		var1.aFloatArrayArray144 = new float[this.keyframeCount][this.componentCount];
		int var2;
		if (this.interpolationType == 179) {
			for (var2 = 0; var2 < this.a.length; ++var2) {
				var1.a[var2] = new Quaternion(this.a[var2]);
				var1.b[var2] = new Quaternion(this.b[var2]);
			}

			for (var2 = 0; var2 < this.keyframeCount; ++var2) {
				var1.aFloatArrayArray144[var2] = (float[]) this.aFloatArrayArray144[var2].clone();
			}
		} else if (this.interpolationType == 178) {
			var1.inTangent = new float[this.keyframeCount][this.componentCount];
			var1.outTangent = new float[this.keyframeCount][this.componentCount];

			for (var2 = 0; var2 < this.keyframeCount; ++var2) {
				var1.inTangent[var2] = (float[]) this.inTangent[var2].clone();
				var1.outTangent[var2] = (float[]) this.outTangent[var2].clone();
				var1.aFloatArrayArray144[var2] = (float[]) this.aFloatArrayArray144[var2].clone();
			}
		}

		return var1;
	}

	public KeyframeSequence(int numKeyframes, int numComponents, int interpolation) {
		if (numKeyframes >= 1 && numComponents >= 1 && checkInterpolation(interpolation)) {
			if ((interpolation == SLERP || interpolation == SQUAD) && numComponents != 4) {
				throw new IllegalArgumentException();
			} else {
				this.keyframeCount = numKeyframes;
				this.componentCount = numComponents;
				this.interpolationType = interpolation;
				this.aFloatArrayArray144 = new float[numKeyframes][numComponents];
				this.keyframes = new int[numKeyframes];
				this.repeatMode = CONSTANT;
				this.validRangeFirst = 0;
				this.validRangeLast = this.keyframeCount - 1;
				this.duration = 0;
				this.dirty = false;
				if (this.interpolationType == SPLINE) {
					this.inTangent = new float[numKeyframes][numComponents];
					this.outTangent = new float[numKeyframes][numComponents];
				} else if (this.interpolationType == SQUAD) {
					this.a = new Quaternion[numKeyframes];
					this.b = new Quaternion[numKeyframes];

					for (int var4 = 0; var4 < numKeyframes; ++var4) {
						this.a[var4] = new Quaternion();
						this.b[var4] = new Quaternion();
					}
				}
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	private static boolean checkInterpolation(int var0) {
		return var0 >= LINEAR && var0 <= STEP;
	}

	public int getComponentCount() {
		return this.componentCount;
	}

	public int getKeyframeCount() {
		return this.keyframeCount;
	}

	public int getInterpolationType() {
		return this.interpolationType;
	}

	public void setKeyframe(int var1, int var2, float[] var3) {
		if (var3 == null) {
			throw new NullPointerException();
		} else if (var1 >= 0 && var1 < this.keyframeCount) {
			if (var2 >= 0 && var3.length >= this.componentCount) {
				this.keyframes[var1] = var2;
				float[] var4 = this.aFloatArrayArray144[var1];
				if (this.interpolationType != 177 && this.interpolationType != 179) {
					System.arraycopy(var3, 0, var4, 0, var4.length);
				} else {
					Quaternion var5;
					(var5 = new Quaternion(var3[0], var3[1], var3[2], var3[3])).normalize();
					var4[0] = var5.x;
					var4[1] = var5.y;
					var4[2] = var5.z;
					var4[3] = var5.w;
				}

				this.dirty = false;
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public int getKeyframe(int var1, float[] var2) {
		if (var1 >= 0 && var1 < this.keyframeCount) {
			if (var2 != null && var2.length < this.componentCount) {
				throw new IllegalArgumentException();
			} else {
				if (var2 != null) {
					System.arraycopy(this.aFloatArrayArray144[var1], 0, var2, 0, this.componentCount);
				}

				return this.keyframes[var1];
			}
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public void setValidRange(int var1, int var2) {
		if (var1 >= 0 && var1 < this.keyframeCount && var2 >= 0 && var2 < this.keyframeCount) {
			this.validRangeFirst = var1;
			this.validRangeLast = var2;
			this.dirty = false;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public int getValidRangeFirst() {
		return this.validRangeFirst;
	}

	public int getValidRangeLast() {
		return this.validRangeLast;
	}

	public void setDuration(int var1) {
		this.duration = var1;
	}

	public int getDuration() {
		return this.duration;
	}

	public void setRepeatMode(int var1) {
		if (var1 != 192 && var1 != 193) {
			throw new IllegalArgumentException();
		} else {
			this.repeatMode = var1;
			if (this.dirty) {
				this.updateTangents();
			}

		}
	}

	public int getRepeatMode() {
		return this.repeatMode;
	}

	protected int getSampleFrame(float var1, float[] var2) {
		if (!this.dirty) {
			this.validate();
		}

		float var4;
		if (this.repeatMode == LOOP) {
			if ((var1 = var1 < 0.0F ? var1 % (float) this.duration + (float) this.duration : var1 % (float) this.duration) < (float) this.keyframes[this.validRangeFirst]) {
				var1 += (float) this.duration;
			}
		} else {
			float[] var6;
			if (var1 < (float) this.keyframes[this.validRangeFirst]) {
				System.arraycopy(var6 = this.aFloatArrayArray144[this.validRangeFirst], 0, var2, 0, var6.length);
				if ((var4 = (float) this.keyframes[this.validRangeFirst] - var1) <= 2.14748365E9F) {
					return (int) var4;
				}

				return Integer.MAX_VALUE;
			}

			if (var1 >= (float) this.keyframes[this.validRangeLast]) {
				System.arraycopy(var6 = this.aFloatArrayArray144[this.validRangeLast], 0, var2, 0, var6.length);
				return Integer.MAX_VALUE;
			}
		}

		int var10000 = this.validRangeFirst;

		while (true) {
			int var3 = var10000;
			if (var10000 == this.validRangeLast || (float) this.keyframes[this.keyframeAfter(var3)] > var1) {
				if (var1 - (float) this.keyframes[var3] >= 1.0E-5F && this.interpolationType != STEP) {
					var4 = (var1 - (float) this.keyframes[var3]) / (float) this.timeDelta(var3);
					int var5 = this.keyframeAfter(var3);
					switch (this.interpolationType) {
						case LINEAR:
							this.linearInterp(var2, var4, var3, var5);
							break;
						case SLERP:
							this.slerpInterp(var2, var4, var3, var5);
							break;
						case SPLINE:
							this.splineInterp(var2, var4, var3, var5);
							break;
						case SQUAD:
							this.squadInterp(var2, var4, var3, var5);
							break;
						default:
							throw new Error("Invalid type for interpolation!");
					}

					return 1;
				} else {
					System.arraycopy(this.aFloatArrayArray144[var3], 0, var2, 0, this.componentCount);
					return this.interpolationType != STEP ? 1 : (int) ((float) this.timeDelta(var3) - (var1 - (float) this.keyframes[var3]));
				}
			}

			var10000 = this.keyframeAfter(var3);
		}
	}

	private void validate() {
		if (this.duration <= 0) {
			throw new IllegalStateException();
		} else {
			int var10000 = this.validRangeFirst;

			while (true) {
				int var1 = var10000;
				if (var10000 == this.validRangeLast) {
					this.dirty = true;
					this.updateTangents();
					return;
				}

				int var2 = var1 >= this.keyframes.length - 1 ? 0 : var1 + 1;
				if (this.keyframes[var2] < this.keyframes[var1] || this.keyframes[var2] > this.duration) {
					throw new IllegalStateException();
				}

				var10000 = var2;
			}
		}
	}

	private final void linearInterp(float[] out, float weight, int var3, int var4) {
		float[] frameA = this.aFloatArrayArray144[var3];
		float[] frameB = this.aFloatArrayArray144[var4];

		for (int i = 0; i < out.length; ++i) {
			out[i] = frameA[i] + weight * (frameB[i] - frameA[i]);
		}

	}

	private final void splineInterp(float[] out, float weight, int var3, int var4) {
		float[] frameA = this.aFloatArrayArray144[var3];
		float[] frameB = this.aFloatArrayArray144[var4];

		for (int i = 0; i < out.length; ++i) {
			float a = this.outTangent[var3][i];
			float b = this.inTangent[var4][i];
			out[i] = spline(weight, frameA[i], frameB[i], a, b);
		}
	}

	private static float spline(float weight, float a, float b, float var3, float var4) {
		float weight2 = weight * weight;
		float weight3 = weight2 * weight;
		return (2.0F * weight3 - 3.0F * weight2 + 1.0F) * a + (-2.0F * weight3 + 3.0F * weight2) * b + (weight3 - 2.0F * weight2 + weight) * var3 + (weight3 - weight2) * var4;
	}

	private final void precalculateTangents() {
		int var1 = this.validRangeFirst;

		do {
			float[] var2 = this.aFloatArrayArray144[this.keyframeBefore(var1)];
			float[] var3 = this.aFloatArrayArray144[this.keyframeAfter(var1)];
			float var4 = this.incomingTangentScale(var1);
			float var5 = this.outgoingTangentScale(var1);

			for (int var6 = 0; var6 < this.componentCount; ++var6) {
				this.inTangent[var1][var6] = 0.5F * (var3[var6] - var2[var6]) * var4;
				this.outTangent[var1][var6] = 0.5F * (var3[var6] - var2[var6]) * var5;
			}
		} while ((var1 = this.keyframeAfter(var1)) != this.validRangeFirst);

	}

	private final void slerpInterp(float[] var1, float var2, int var3, int var4) {
		if (var1.length != 4) {
			throw new Error("Invalid keyframe type");
		} else {
			Quaternion var5 = new Quaternion(this.aFloatArrayArray144[var3]);
			Quaternion var6 = new Quaternion(this.aFloatArrayArray144[var4]);
			Quaternion var7;
			(var7 = new Quaternion()).slerp(var2, var5, var6);
			var1[0] = var7.x;
			var1[1] = var7.y;
			var1[2] = var7.z;
			var1[3] = var7.w;
		}
	}

	private final void squadInterp(float[] var1, float var2, int var3, int var4) {
		if (var1.length != 4) {
			throw new Error("Invalid keyframe type");
		} else {
			Quaternion var5 = new Quaternion(this.aFloatArrayArray144[var3]);
			Quaternion var6 = new Quaternion(this.aFloatArrayArray144[var4]);
			Quaternion var7;
			(var7 = new Quaternion()).squad(var2, var5, this.a[var3], this.b[var4], var6);
			var1[0] = var7.x;
			var1[1] = var7.y;
			var1[2] = var7.z;
			var1[3] = var7.w;
		}
	}

	private final void precalculateAB() {
		Quaternion var1 = new Quaternion();
		Quaternion var2 = new Quaternion();
		Quaternion var3 = new Quaternion();
		Quaternion var4 = new Quaternion();
		Quaternion var5 = new Quaternion();
		Quaternion var6 = new Quaternion();
		Quaternion var7 = new Quaternion();
		int var8 = this.validRangeFirst;

		do {
			var3.set(this.aFloatArrayArray144[this.keyframeBefore(var8)]);
			var1.set(this.aFloatArrayArray144[var8]);
			var2.set(this.aFloatArrayArray144[this.keyframeAfter(var8)]);
			var4.set(this.aFloatArrayArray144[this.keyframeAfter(this.keyframeAfter(var8))]);
			var7.logDiff(var1, var2);
			var6.logDiff(var3, var1);
			var7.add(var6);
			var7.mul(0.5F);
			var5.set(var7);
			var5.mul(this.outgoingTangentScale(var8));
			var6.logDiff(var1, var2);
			var5.sub(var6);
			var5.mul(0.5F);
			var6.exp(var5);
			this.a[var8].set(var1);
			this.a[var8].mul(var6);
			var5.set(var7);
			var5.mul(this.incomingTangentScale(var8));
			var6.logDiff(var3, var1);
			var6.sub(var5);
			var6.mul(0.5F);
			var6.exp(var6);
			this.b[var8].set(var1);
			this.b[var8].mul(var6);
		} while ((var8 = this.keyframeAfter(var8)) != this.validRangeFirst);

	}

	private float incomingTangentScale(int var1) {
		if (this.repeatMode != 192 || var1 != this.validRangeFirst && var1 != this.validRangeLast) {
			int var2 = this.keyframeBefore(var1);
			return 2.0F * (float) this.timeDelta(var2) / (float) (this.timeDelta(var1) + this.timeDelta(var2));
		} else {
			return 0.0F;
		}
	}

	private float outgoingTangentScale(int var1) {
		if (this.repeatMode != 192 || var1 != this.validRangeFirst && var1 != this.validRangeLast) {
			int var2 = this.keyframeBefore(var1);
			return 2.0F * (float) this.timeDelta(var1) / (float) (this.timeDelta(var2) + this.timeDelta(var1));
		} else {
			return 0.0F;
		}
	}

	private void updateTangents() {
		if (!this.dirty) {
			throw new Error();
		} else if (this.interpolationType == 178) {
			this.precalculateTangents();
		} else {
			if (this.interpolationType == 179) {
				this.precalculateAB();
			}

		}
	}

	private int keyframeAfter(int var1) {
		return var1 == this.validRangeLast ? this.validRangeFirst : (var1 == this.keyframes.length - 1 ? 0 : var1 + 1);
	}

	private int keyframeBefore(int var1) {
		return var1 == this.validRangeFirst ? this.validRangeLast : (var1 == 0 ? this.keyframes.length - 1 : var1 - 1);
	}

	private int timeDelta(int var1) {
		return var1 == this.validRangeLast ? this.duration - this.keyframes[this.validRangeLast] + this.keyframes[this.validRangeFirst] : this.keyframes[this.keyframeAfter(var1)] - this.keyframes[var1];
	}
}
