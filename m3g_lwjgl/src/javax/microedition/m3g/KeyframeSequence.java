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
		KeyframeSequence copy;
		(copy = (KeyframeSequence) super.duplicateObject()).keyframes = (int[]) this.keyframes.clone();
		copy.aFloatArrayArray144 = new float[this.keyframeCount][this.componentCount];
		int i;
		if (this.interpolationType == 179) {
			for (i = 0; i < this.a.length; ++i) {
				copy.a[i] = new Quaternion(this.a[i]);
				copy.b[i] = new Quaternion(this.b[i]);
			}

			for (i = 0; i < this.keyframeCount; ++i) {
				copy.aFloatArrayArray144[i] = (float[]) this.aFloatArrayArray144[i].clone();
			}
		} else if (this.interpolationType == 178) {
			copy.inTangent = new float[this.keyframeCount][this.componentCount];
			copy.outTangent = new float[this.keyframeCount][this.componentCount];

			for (i = 0; i < this.keyframeCount; ++i) {
				copy.inTangent[i] = (float[]) this.inTangent[i].clone();
				copy.outTangent[i] = (float[]) this.outTangent[i].clone();
				copy.aFloatArrayArray144[i] = (float[]) this.aFloatArrayArray144[i].clone();
			}
		}

		return copy;
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

					for (int i = 0; i < numKeyframes; ++i) {
						this.a[i] = new Quaternion();
						this.b[i] = new Quaternion();
					}
				}
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	private static boolean checkInterpolation(int interpolation) {
		return interpolation >= LINEAR && interpolation <= STEP;
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

	public void setKeyframe(int index, int time, float[] value) {
		if (value == null) {
			throw new NullPointerException();
		} else if (index >= 0 && index < this.keyframeCount) {
			if (time >= 0 && value.length >= this.componentCount) {
				this.keyframes[index] = time;
				float[] keyframe = this.aFloatArrayArray144[index];
				if (this.interpolationType != 177 && this.interpolationType != 179) {
					System.arraycopy(value, 0, keyframe, 0, keyframe.length);
				} else {
					Quaternion orientation;
					(orientation = new Quaternion(value[0], value[1], value[2], value[3])).normalize();
					keyframe[0] = orientation.x;
					keyframe[1] = orientation.y;
					keyframe[2] = orientation.z;
					keyframe[3] = orientation.w;
				}

				this.dirty = false;
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public int getKeyframe(int index, float[] value) {
		if (index >= 0 && index < this.keyframeCount) {
			if (value != null && value.length < this.componentCount) {
				throw new IllegalArgumentException();
			} else {
				if (value != null) {
					System.arraycopy(this.aFloatArrayArray144[index], 0, value, 0, this.componentCount);
				}

				return this.keyframes[index];
			}
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public void setValidRange(int first, int last) {
		if (first >= 0 && first < this.keyframeCount && last >= 0 && last < this.keyframeCount) {
			this.validRangeFirst = first;
			this.validRangeLast = last;
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

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getDuration() {
		return this.duration;
	}

	public void setRepeatMode(int mode) {
		if (mode != 192 && mode != 193) {
			throw new IllegalArgumentException();
		} else {
			this.repeatMode = mode;
			if (this.dirty) {
				this.updateTangents();
			}

		}
	}

	public int getRepeatMode() {
		return this.repeatMode;
	}

	protected int getSampleFrame(float time, float[] sample) {
		if (!this.dirty) {
			this.validate();
		}

		float delta;
		if (this.repeatMode == LOOP) {
			if ((time = time < 0.0F ? time % (float) this.duration + (float) this.duration : time % (float) this.duration) < (float) this.keyframes[this.validRangeFirst]) {
				time += (float) this.duration;
			}
		} else {
			float[] keyframe;
			if (time < (float) this.keyframes[this.validRangeFirst]) {
				System.arraycopy(keyframe = this.aFloatArrayArray144[this.validRangeFirst], 0, sample, 0, keyframe.length);
				if ((delta = (float) this.keyframes[this.validRangeFirst] - time) <= 2.14748365E9F) {
					return (int) delta;
				}

				return Integer.MAX_VALUE;
			}

			if (time >= (float) this.keyframes[this.validRangeLast]) {
				System.arraycopy(keyframe = this.aFloatArrayArray144[this.validRangeLast], 0, sample, 0, keyframe.length);
				return Integer.MAX_VALUE;
			}
		}

		int nextIndex = this.validRangeFirst;

		while (true) {
			int frame = nextIndex;
			if (nextIndex == this.validRangeLast || (float) this.keyframes[this.keyframeAfter(frame)] > time) {
				if (time - (float) this.keyframes[frame] >= 1.0E-5F && this.interpolationType != STEP) {
					delta = (time - (float) this.keyframes[frame]) / (float) this.timeDelta(frame);
					int nextFrame = this.keyframeAfter(frame);
					switch (this.interpolationType) {
						case LINEAR:
							this.linearInterp(sample, delta, frame, nextFrame);
							break;
						case SLERP:
							this.slerpInterp(sample, delta, frame, nextFrame);
							break;
						case SPLINE:
							this.splineInterp(sample, delta, frame, nextFrame);
							break;
						case SQUAD:
							this.squadInterp(sample, delta, frame, nextFrame);
							break;
						default:
							throw new Error("Invalid type for interpolation!");
					}

					return 1;
				} else {
					System.arraycopy(this.aFloatArrayArray144[frame], 0, sample, 0, this.componentCount);
					return this.interpolationType != STEP ? 1 : (int) ((float) this.timeDelta(frame) - (time - (float) this.keyframes[frame]));
				}
			}

			nextIndex = this.keyframeAfter(frame);
		}
	}

	private void validate() {
		if (this.duration <= 0) {
			throw new IllegalStateException();
		} else {
			int nextIndex = this.validRangeFirst;

			while (true) {
				int frame = nextIndex;
				if (nextIndex == this.validRangeLast) {
					this.dirty = true;
					this.updateTangents();
					return;
				}

				int next = frame >= this.keyframes.length - 1 ? 0 : frame + 1;
				if (this.keyframes[next] < this.keyframes[frame] || this.keyframes[next] > this.duration) {
					throw new IllegalStateException();
				}

				nextIndex = next;
			}
		}
	}

	private final void linearInterp(float[] out, float weight, int indexA, int indexB) {
		float[] frameA = this.aFloatArrayArray144[indexA];
		float[] frameB = this.aFloatArrayArray144[indexB];

		for (int i = 0; i < out.length; ++i) {
			out[i] = frameA[i] + weight * (frameB[i] - frameA[i]);
		}

	}

	private final void splineInterp(float[] out, float weight, int indexA, int indexB) {
		float[] frameA = this.aFloatArrayArray144[indexA];
		float[] frameB = this.aFloatArrayArray144[indexB];

		for (int i = 0; i < out.length; ++i) {
			float a = this.outTangent[indexA][i];
			float b = this.inTangent[indexB][i];
			out[i] = spline(weight, frameA[i], frameB[i], a, b);
		}
	}

	private static float spline(float weight, float a, float b, float tangentA, float tangentB) {
		float weight2 = weight * weight;
		float weight3 = weight2 * weight;
		return (2.0F * weight3 - 3.0F * weight2 + 1.0F) * a + (-2.0F * weight3 + 3.0F * weight2) * b + (weight3 - 2.0F * weight2 + weight) * tangentA + (weight3 - weight2) * tangentB;
	}

	private final void precalculateTangents() {
		int frame = this.validRangeFirst;

		do {
			float[] prevValue = this.aFloatArrayArray144[this.keyframeBefore(frame)];
			float[] nextValue = this.aFloatArrayArray144[this.keyframeAfter(frame)];
			float inScale = this.incomingTangentScale(frame);
			float outScale = this.outgoingTangentScale(frame);

			for (int i = 0; i < this.componentCount; ++i) {
				this.inTangent[frame][i] = 0.5F * (nextValue[i] - prevValue[i]) * inScale;
				this.outTangent[frame][i] = 0.5F * (nextValue[i] - prevValue[i]) * outScale;
			}
		} while ((frame = this.keyframeAfter(frame)) != this.validRangeFirst);

	}

	private final void slerpInterp(float[] out, float weight, int indexA, int indexB) {
		if (out.length != 4) {
			throw new Error("Invalid keyframe type");
		} else {
			Quaternion quatA = new Quaternion(this.aFloatArrayArray144[indexA]);
			Quaternion quatB = new Quaternion(this.aFloatArrayArray144[indexB]);
			Quaternion result;
			(result = new Quaternion()).slerp(weight, quatA, quatB);
			out[0] = result.x;
			out[1] = result.y;
			out[2] = result.z;
			out[3] = result.w;
		}
	}

	private final void squadInterp(float[] out, float weight, int indexA, int indexB) {
		if (out.length != 4) {
			throw new Error("Invalid keyframe type");
		} else {
			Quaternion quatA = new Quaternion(this.aFloatArrayArray144[indexA]);
			Quaternion quatB = new Quaternion(this.aFloatArrayArray144[indexB]);
			Quaternion result;
			(result = new Quaternion()).squad(weight, quatA, this.a[indexA], this.b[indexB], quatB);
			out[0] = result.x;
			out[1] = result.y;
			out[2] = result.z;
			out[3] = result.w;
		}
	}

	private final void precalculateAB() {
		Quaternion current = new Quaternion();
		Quaternion next = new Quaternion();
		Quaternion previous = new Quaternion();
		Quaternion afterNext = new Quaternion();
		Quaternion scaledTangent = new Quaternion();
		Quaternion tmp = new Quaternion();
		Quaternion tangent = new Quaternion();
		int frame = this.validRangeFirst;

		do {
			previous.set(this.aFloatArrayArray144[this.keyframeBefore(frame)]);
			current.set(this.aFloatArrayArray144[frame]);
			next.set(this.aFloatArrayArray144[this.keyframeAfter(frame)]);
			afterNext.set(this.aFloatArrayArray144[this.keyframeAfter(this.keyframeAfter(frame))]);
			tangent.logDiff(current, next);
			tmp.logDiff(previous, current);
			tangent.add(tmp);
			tangent.mul(0.5F);
			scaledTangent.set(tangent);
			scaledTangent.mul(this.outgoingTangentScale(frame));
			tmp.logDiff(current, next);
			scaledTangent.sub(tmp);
			scaledTangent.mul(0.5F);
			tmp.exp(scaledTangent);
			this.a[frame].set(current);
			this.a[frame].mul(tmp);
			scaledTangent.set(tangent);
			scaledTangent.mul(this.incomingTangentScale(frame));
			tmp.logDiff(previous, current);
			tmp.sub(scaledTangent);
			tmp.mul(0.5F);
			tmp.exp(tmp);
			this.b[frame].set(current);
			this.b[frame].mul(tmp);
		} while ((frame = this.keyframeAfter(frame)) != this.validRangeFirst);

	}

	private float incomingTangentScale(int frame) {
		if (this.repeatMode != 192 || frame != this.validRangeFirst && frame != this.validRangeLast) {
			int prevFrame = this.keyframeBefore(frame);
			return 2.0F * (float) this.timeDelta(prevFrame) / (float) (this.timeDelta(frame) + this.timeDelta(prevFrame));
		} else {
			return 0.0F;
		}
	}

	private float outgoingTangentScale(int frame) {
		if (this.repeatMode != 192 || frame != this.validRangeFirst && frame != this.validRangeLast) {
			int prevFrame = this.keyframeBefore(frame);
			return 2.0F * (float) this.timeDelta(frame) / (float) (this.timeDelta(prevFrame) + this.timeDelta(frame));
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

	private int keyframeAfter(int frame) {
		return frame == this.validRangeLast ? this.validRangeFirst : (frame == this.keyframes.length - 1 ? 0 : frame + 1);
	}

	private int keyframeBefore(int frame) {
		return frame == this.validRangeFirst ? this.validRangeLast : (frame == 0 ? this.keyframes.length - 1 : frame - 1);
	}

	private int timeDelta(int frame) {
		return frame == this.validRangeLast ? this.duration - this.keyframes[this.validRangeLast] + this.keyframes[this.validRangeFirst] : this.keyframes[this.keyframeAfter(frame)] - this.keyframes[frame];
	}
}
