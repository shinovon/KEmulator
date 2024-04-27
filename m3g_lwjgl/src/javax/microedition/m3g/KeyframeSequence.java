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
    private boolean aBoolean36;
    private float[][] aFloatArrayArray147;
    private float[][] aFloatArrayArray149;
    private Quaternion[] anaArray146;
    private Quaternion[] anaArray148;

    protected Object3D duplicateObject() {
        KeyframeSequence var1;
        (var1 = (KeyframeSequence) super.duplicateObject()).keyframes = (int[]) this.keyframes.clone();
        var1.aFloatArrayArray144 = new float[this.keyframeCount][this.componentCount];
        int var2;
        if (this.interpolationType == 179) {
            for (var2 = 0; var2 < this.anaArray146.length; ++var2) {
                var1.anaArray146[var2] = new Quaternion(this.anaArray146[var2]);
                var1.anaArray148[var2] = new Quaternion(this.anaArray148[var2]);
            }

            for (var2 = 0; var2 < this.keyframeCount; ++var2) {
                var1.aFloatArrayArray144[var2] = (float[]) this.aFloatArrayArray144[var2].clone();
            }
        } else if (this.interpolationType == 178) {
            var1.aFloatArrayArray147 = new float[this.keyframeCount][this.componentCount];
            var1.aFloatArrayArray149 = new float[this.keyframeCount][this.componentCount];

            for (var2 = 0; var2 < this.keyframeCount; ++var2) {
                var1.aFloatArrayArray147[var2] = (float[]) this.aFloatArrayArray147[var2].clone();
                var1.aFloatArrayArray149[var2] = (float[]) this.aFloatArrayArray149[var2].clone();
                var1.aFloatArrayArray144[var2] = (float[]) this.aFloatArrayArray144[var2].clone();
            }
        }

        return var1;
    }

    public KeyframeSequence(int numKeyframes, int numComponents, int interpolation) {
        if (numKeyframes >= 1 && numComponents >= 1 && method14(interpolation)) {
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
                this.aBoolean36 = false;
                if (this.interpolationType == SPLINE) {
                    this.aFloatArrayArray147 = new float[numKeyframes][numComponents];
                    this.aFloatArrayArray149 = new float[numKeyframes][numComponents];
                } else if (this.interpolationType == SQUAD) {
                    this.anaArray146 = new Quaternion[numKeyframes];
                    this.anaArray148 = new Quaternion[numKeyframes];

                    for (int var4 = 0; var4 < numKeyframes; ++var4) {
                        this.anaArray146[var4] = new Quaternion();
                        this.anaArray148[var4] = new Quaternion();
                    }
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static boolean method14(int var0) {
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

                this.aBoolean36 = false;
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
            this.aBoolean36 = false;
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
            if (this.aBoolean36) {
                this.method122();
            }

        }
    }

    public int getRepeatMode() {
        return this.repeatMode;
    }

    protected int getSampleFrame(float var1, float[] var2) {
        if (!this.aBoolean36) {
            this.method110();
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
            if (var10000 == this.validRangeLast || (float) this.keyframes[this.method13(var3)] > var1) {
                if (var1 - (float) this.keyframes[var3] >= 1.0E-5F && this.interpolationType != STEP) {
                    var4 = (var1 - (float) this.keyframes[var3]) / (float) this.method120(var3);
                    int var5 = this.method13(var3);
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
                    return this.interpolationType != STEP ? 1 : (int) ((float) this.method120(var3) - (var1 - (float) this.keyframes[var3]));
                }
            }

            var10000 = this.method13(var3);
        }
    }

    private void method110() {
        if (this.duration <= 0) {
            throw new IllegalStateException();
        } else {
            int var10000 = this.validRangeFirst;

            while (true) {
                int var1 = var10000;
                if (var10000 == this.validRangeLast) {
                    this.aBoolean36 = true;
                    this.method122();
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
            float a = this.aFloatArrayArray149[var3][i];
            float b = this.aFloatArrayArray147[var4][i];
            out[i] = spline(weight, frameA[i], frameB[i], a, b);
        }
    }

    private static float spline(float weight, float a, float b, float var3, float var4) {
        float weight2 = weight * weight;
        float weight3 = weight2 * weight;
        return (2.0F * weight3 - 3.0F * weight2 + 1.0F) * a + (-2.0F * weight3 + 3.0F * weight2) * b + (weight3 - 2.0F * weight2 + weight) * var3 + (weight3 - weight2) * var4;
    }

    private final void method115() {
        int var1 = this.validRangeFirst;

        do {
            float[] var2 = this.aFloatArrayArray144[this.method117(var1)];
            float[] var3 = this.aFloatArrayArray144[this.method13(var1)];
            float var4 = this.method113(var1);
            float var5 = this.method116(var1);

            for (int var6 = 0; var6 < this.componentCount; ++var6) {
                this.aFloatArrayArray147[var1][var6] = 0.5F * (var3[var6] - var2[var6]) * var4;
                this.aFloatArrayArray149[var1][var6] = 0.5F * (var3[var6] - var2[var6]) * var5;
            }
        } while ((var1 = this.method13(var1)) != this.validRangeFirst);

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
            (var7 = new Quaternion()).squad(var2, var5, this.anaArray146[var3], this.anaArray148[var4], var6);
            var1[0] = var7.x;
            var1[1] = var7.y;
            var1[2] = var7.z;
            var1[3] = var7.w;
        }
    }

    private final void method119() {
        Quaternion var1 = new Quaternion();
        Quaternion var2 = new Quaternion();
        Quaternion var3 = new Quaternion();
        Quaternion var4 = new Quaternion();
        Quaternion var5 = new Quaternion();
        Quaternion var6 = new Quaternion();
        Quaternion var7 = new Quaternion();
        int var8 = this.validRangeFirst;

        do {
            var3.set(this.aFloatArrayArray144[this.method117(var8)]);
            var1.set(this.aFloatArrayArray144[var8]);
            var2.set(this.aFloatArrayArray144[this.method13(var8)]);
            var4.set(this.aFloatArrayArray144[this.method13(this.method13(var8))]);
            var7.logDiff(var1, var2);
            var6.logDiff(var3, var1);
            var7.add(var6);
            var7.mul(0.5F);
            var5.set(var7);
            var5.mul(this.method116(var8));
            var6.logDiff(var1, var2);
            var5.sub(var6);
            var5.mul(0.5F);
            var6.exp(var5);
            this.anaArray146[var8].set(var1);
            this.anaArray146[var8].mul(var6);
            var5.set(var7);
            var5.mul(this.method113(var8));
            var6.logDiff(var3, var1);
            var6.sub(var5);
            var6.mul(0.5F);
            var6.exp(var6);
            this.anaArray148[var8].set(var1);
            this.anaArray148[var8].mul(var6);
        } while ((var8 = this.method13(var8)) != this.validRangeFirst);

    }

    private float method113(int var1) {
        if (this.repeatMode != 192 || var1 != this.validRangeFirst && var1 != this.validRangeLast) {
            int var2 = this.method117(var1);
            return 2.0F * (float) this.method120(var2) / (float) (this.method120(var1) + this.method120(var2));
        } else {
            return 0.0F;
        }
    }

    private float method116(int var1) {
        if (this.repeatMode != 192 || var1 != this.validRangeFirst && var1 != this.validRangeLast) {
            int var2 = this.method117(var1);
            return 2.0F * (float) this.method120(var1) / (float) (this.method120(var2) + this.method120(var1));
        } else {
            return 0.0F;
        }
    }

    private void method122() {
        if (!this.aBoolean36) {
            throw new Error();
        } else if (this.interpolationType == 178) {
            this.method115();
        } else {
            if (this.interpolationType == 179) {
                this.method119();
            }

        }
    }

    private int method13(int var1) {
        return var1 == this.validRangeLast ? this.validRangeFirst : (var1 == this.keyframes.length - 1 ? 0 : var1 + 1);
    }

    private int method117(int var1) {
        return var1 == this.validRangeFirst ? this.validRangeLast : (var1 == 0 ? this.keyframes.length - 1 : var1 - 1);
    }

    private int method120(int var1) {
        return var1 == this.validRangeLast ? this.duration - this.keyframes[this.validRangeLast] + this.keyframes[this.validRangeFirst] : this.keyframes[this.method13(var1)] - this.keyframes[var1];
    }
}
