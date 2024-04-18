package emulator.graphics3D.m3g;

import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.Transform3D;
import emulator.graphics3D.lwjgl.Emulator3D;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.MorphingMesh;
import javax.microedition.m3g.SkinnedMesh;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;

public final class MeshMorph {
    private static MeshMorph inst;
    private Hashtable cacheTable = new Hashtable();
    private Hashtable aHashtable1129 = new Hashtable();
    public VertexBuffer morphed;
    private VertexArray[] aVertexArrayArray1125;
    private VertexArray aVertexArray1126;
    private VertexArray aVertexArray1130;
    private VertexArray aVertexArray1133;
    private VertexArray[] aVertexArrayArray1131;
    private float[] aFloatArray1127;
    private float[] aFloatArray1132 = new float[4];
    private float[] aFloatArray1134 = new float[4];
    private float[] aFloatArray1135 = new float[3];
    private float[] aFloatArray1136 = new float[3];

    public static MeshMorph getInstance() {
        if (inst == null) {
            inst = new MeshMorph();
        }

        return inst;
    }

    public final void clearCache() {
        this.cacheTable.clear();
    }

    public final VertexBuffer getMorphedVertexBuffer(Mesh var1) {
        MeshMorph var10000;
        VertexBuffer var10001;
        VertexBuffer var2;
        if ((var2 = (VertexBuffer) this.cacheTable.get(var1)) != null) {
            var10000 = this;
            var10001 = var2;
        } else {
            if (var1 instanceof MorphingMesh) {
                this.method780((MorphingMesh) var1);
                this.cacheTable.put(var1, this.morphed);
                return this.morphed;
            }

            if (var1 instanceof SkinnedMesh) {
                this.method782((SkinnedMesh) var1);
                this.cacheTable.put(var1, this.morphed);
                return this.morphed;
            }

            var10000 = this;
            var10001 = var1.getVertexBuffer();
        }

        var10000.morphed = var10001;
        return this.morphed;
    }

    private void method780(MorphingMesh var1) {
        VertexBuffer var2 = var1.getVertexBuffer();
        int var3 = var1.getMorphTargetCount();
        this.method781(var2);
        this.aVertexArrayArray1125 = new VertexArray[var3];
        float[] var4 = new float[var3];
        var1.getWeights(var4);
        float var5 = var1.getBaseWeight();
        int var6;
        int var7;
        if (this.aVertexArray1126 != null) {
            var6 = 0;

            for (var7 = 0; var7 < var3; ++var7) {
                this.aVertexArrayArray1125[var7] = var1.getMorphTarget(var7).getPositions((float[]) null);
                if (this.aVertexArrayArray1125[var7] == null) {
                    ++var6;
                }
            }

            if (var6 != var3 && var6 != 0) {
                throw new IllegalStateException();
            }

            if (var6 == 0) {
                this.aVertexArray1126.morph(this.aVertexArrayArray1125, var2.getPositions((float[]) null), var4, var5);
            }
        } else {
            for (var6 = 0; var6 < var3; ++var6) {
                if (var1.getMorphTarget(var6).getPositions((float[]) null) != null) {
                    throw new IllegalStateException();
                }
            }
        }

        if (this.aVertexArray1130 != null) {
            var6 = 0;

            for (var7 = 0; var7 < var3; ++var7) {
                this.aVertexArrayArray1125[var7] = var1.getMorphTarget(var7).getNormals();
                if (this.aVertexArrayArray1125[var7] == null) {
                    ++var6;
                }
            }

            if (var6 != var3 && var6 != 0) {
                throw new IllegalStateException();
            }

            if (var6 == 0) {
                this.aVertexArray1130.morph(this.aVertexArrayArray1125, var2.getNormals(), var4, var5);
            }
        } else {
            for (var6 = 0; var6 < var3; ++var6) {
                if (var1.getMorphTarget(var6).getNormals() != null) {
                    throw new IllegalStateException();
                }
            }
        }

        if (this.aVertexArray1133 != null) {
            var6 = 0;

            for (var7 = 0; var7 < var3; ++var7) {
                this.aVertexArrayArray1125[var7] = var1.getMorphTarget(var7).getColors();
                if (this.aVertexArrayArray1125[var7] == null) {
                    ++var6;
                }
            }

            if (var6 != var3 && var6 != 0) {
                throw new IllegalStateException();
            }

            if (var6 == 0) {
                this.aVertexArray1133.morphColors(this.aVertexArrayArray1125, var2.getColors(), var4, var5);
            }
        } else {
            for (var6 = 0; var6 < var3; ++var6) {
                if (var1.getMorphTarget(var6).getColors() != null) {
                    throw new IllegalStateException();
                }
            }

            var6 = var2.getDefaultColor();
            float var13 = var5 * (float) (var6 >> 24 & 255);
            float var8 = var5 * (float) (var6 >> 16 & 255);
            float var9 = var5 * (float) (var6 >> 8 & 255);
            float var10 = var5 * (float) (var6 & 255);

            for (int var11 = 0; var11 < var3; ++var11) {
                var6 = var1.getMorphTarget(var11).getDefaultColor();
                var13 += var4[var11] * (float) (var6 >> 24 & 255);
                var8 += var4[var11] * (float) (var6 >> 16 & 255);
                var9 += var4[var11] * (float) (var6 >> 8 & 255);
                var10 += var4[var11] * (float) (var6 & 255);
            }

            var6 = ((G3DUtils.limit((int) (var13 + 0.5F), 0, 255) << 8 | G3DUtils.limit((int) (var8 + 0.5F), 0, 255)) << 8 | G3DUtils.limit((int) (var9 + 0.5F), 0, 255)) << 8 | G3DUtils.limit((int) (var10 + 0.5F), 0, 255);
            this.morphed.setDefaultColor(var6);
        }

        for (var6 = 0; var6 < this.aVertexArrayArray1131.length; ++var6) {
            if (this.aVertexArrayArray1131[var6] == null) {
                for (var7 = 0; var7 < var3; ++var7) {
                    if (var1.getMorphTarget(var7).getTexCoords(var6, (float[]) null) != null) {
                        throw new IllegalStateException();
                    }
                }
            } else {
                var7 = 0;

                for (int var12 = 0; var12 < var3; ++var12) {
                    this.aVertexArrayArray1125[var12] = var1.getMorphTarget(var12).getTexCoords(var6, (float[]) null);
                    if (this.aVertexArrayArray1125[var12] == null) {
                        ++var7;
                    }
                }

                if (var7 != var3 && var7 != 0) {
                    throw new IllegalStateException();
                }

                if (var7 == 0) {
                    this.aVertexArrayArray1131[var6].morph(this.aVertexArrayArray1125, var2.getTexCoords(var6, (float[]) null), var4, var5);
                }
            }
        }

    }

    private void method781(VertexBuffer var1) {
        this.morphed = (VertexBuffer) var1.duplicate();
        if (var1.getPositions((float[]) null) != null) {
            this.aVertexArray1126 = (VertexArray) var1.getPositions(this.aFloatArray1134).duplicate();
            this.aFloatArray1132[0] = this.aFloatArray1134[1];
            this.aFloatArray1132[1] = this.aFloatArray1134[2];
            this.aFloatArray1132[2] = this.aFloatArray1134[3];
            this.morphed.setPositions(this.aVertexArray1126, this.aFloatArray1134[0], this.aFloatArray1132);
        } else {
            this.aVertexArray1126 = null;
        }

        if (var1.getNormals() != null) {
            this.aVertexArray1130 = (VertexArray) var1.getNormals().duplicate();
            this.morphed.setNormals(this.aVertexArray1130);
        } else {
            this.aVertexArray1130 = null;
        }

        if (var1.getColors() != null) {
            this.aVertexArray1133 = (VertexArray) var1.getColors().duplicate();
            this.morphed.setColors(this.aVertexArray1133);
        } else {
            this.aVertexArray1133 = null;
        }

        this.aVertexArrayArray1131 = new VertexArray[Emulator3D.NumTextureUnits];

        for (int var2 = 0; var2 < this.aVertexArrayArray1131.length; ++var2) {
            if (var1.getTexCoords(var2, (float[]) null) != null) {
                this.aVertexArrayArray1131[var2] = (VertexArray) var1.getTexCoords(var2, this.aFloatArray1134).duplicate();
                this.aFloatArray1132[0] = this.aFloatArray1134[1];
                this.aFloatArray1132[1] = this.aFloatArray1134[2];
                this.aFloatArray1132[2] = this.aFloatArray1134[3];
                this.morphed.setTexCoords(var2, this.aVertexArrayArray1131[var2], this.aFloatArray1134[0], this.aFloatArray1132);
            } else {
                this.aVertexArrayArray1131[var2] = null;
            }
        }

    }

    private void method784(VertexBuffer var1) {
        this.morphed = (VertexBuffer) var1.duplicate();
        int var3 = var1.getPositions((float[]) null).getVertexCount();
        if (this.aVertexArray1126 == null || this.aVertexArray1126.getVertexCount() != var3) {
            this.aVertexArray1126 = new VertexArray(var3, 3, 2);
        }

        this.morphed.setPositions(this.aVertexArray1126, 1.0F, (float[]) null);
        if (this.aFloatArray1127 == null || this.aFloatArray1127.length < var3 * 3) {
            this.aFloatArray1127 = new float[var3 * 3];
        }

        if (var1.getNormals() != null) {
            var3 = var1.getNormals().getVertexCount();
            if (this.aVertexArray1130 == null || this.aVertexArray1130.getVertexCount() != var3) {
                this.aVertexArray1130 = new VertexArray(var3, 3, 2);
            }

            this.morphed.setNormals(this.aVertexArray1130);
        }

    }

    private void method782(SkinnedMesh var1) {
        VertexBuffer var2 = var1.getVertexBuffer();
        this.method784(var2);
        VertexArray var3;
        if ((var3 = var2.getPositions(this.aFloatArray1134)) == null) {
            throw new IllegalStateException();
        } else {
            VertexArray var4 = var2.getNormals();
            int var5 = var2.getVertexCount();
            Vector var6 = var1.getTransforms();

            for (int var7 = 0; var7 < var6.size(); ++var7) {
                WeightedTransform var8;
                if ((var8 = (WeightedTransform) var6.elementAt(var7)).m_firstVertex < 0 || var8.m_lastVertex >= var5) {
                    throw new IllegalStateException();
                }

                WeightedTransform var9;
                if ((var9 = (WeightedTransform) this.aHashtable1129.get(var8.m_bone)) != null) {
                    var8.m_positionTransform.set(var9.m_positionTransform);
                    if (var4 != null) {
                        var8.m_normalTransform.set(var9.m_normalTransform);
                    }
                } else {
                    var8.m_bone.getTransformTo(var1, var8.m_positionTransform);
                    var8.m_positionTransform.postMultiply(var8.m_toBoneTransform);
                    if (var4 != null) {
                        var8.m_normalTransform.set(var8.m_positionTransform);
                        ((Transform3D) var8.m_normalTransform.getImpl()).method445();
                        var8.m_normalTransform.transpose();
                    }

                    this.aHashtable1129.put(var8.m_bone, var8);
                }
            }

            this.aHashtable1129.clear();
            short[] var23 = var3.getShortValues();
            byte[] var24 = var3.getByteValues();
            short[] var25 = this.aVertexArray1126.getShortValues();
            short[] var10 = var4 == null ? null : var4.getShortValues();
            byte[] var11 = var4 == null ? null : var4.getByteValues();
            short[] var12 = var4 == null ? null : this.aVertexArray1130.getShortValues();
            int var13 = 0;
            int var14 = 0;
            int var15 = -1;
            float var16 = 0.0F;
            this.aFloatArray1135[0] = this.aFloatArray1135[1] = this.aFloatArray1135[2] = -2.14748365E9F;
            this.aFloatArray1136[0] = this.aFloatArray1136[1] = this.aFloatArray1136[2] = 2.14748365E9F;

            int var17;
            int var26;
            for (var17 = 0; var17 < var5; ++var17) {
                WeightedTransform var18;
                while (var6.size() > var15 + 1 && (var18 = (WeightedTransform) var6.elementAt(var15 + 1)).m_firstVertex <= var17) {
                    var13 += var18.m_weight;
                    ++var14;
                    ++var15;
                }

                var26 = 0;
                float[] var10000;
                int var10001;
                short var10002;
                int var19;
                int var10003;
                float[] var29;
                if (var14 <= 0) {
                    for (var19 = 0; var19 < 3; ++var19) {
                        if (var3.getComponentType() == 2) {
                            var10000 = this.aFloatArray1127;
                            var10001 = var17 * 3 + var19;
                            var10002 = var23[var17 * 3 + var19];
                        } else {
                            var10000 = this.aFloatArray1127;
                            var10001 = var17 * 3 + var19;
                            var10002 = (short) (var24[var17 * 3 + var19] * 257);
                        }

                        var10000[var10001] = (float) var10002;
                        this.aFloatArray1127[var17 * 3 + var19] = this.aFloatArray1127[var17 * 3 + var19] * this.aFloatArray1134[0] + this.aFloatArray1134[var19 + 1];
                        var10000 = this.aFloatArray1135;
                        if (this.aFloatArray1135[var19] <= this.aFloatArray1127[var17 * 3 + var19]) {
                            var29 = this.aFloatArray1127;
                            var10003 = var17 * 3 + var19;
                        } else {
                            var29 = this.aFloatArray1135;
                            var10003 = var19;
                        }

                        var10000[var19] = var29[var10003];
                        var10000 = this.aFloatArray1136;
                        if (this.aFloatArray1136[var19] >= this.aFloatArray1127[var17 * 3 + var19]) {
                            var29 = this.aFloatArray1127;
                            var10003 = var17 * 3 + var19;
                        } else {
                            var29 = this.aFloatArray1136;
                            var10003 = var19;
                        }

                        var10000[var19] = var29[var10003];
                        if (var12 != null) {
                            short[] var30;
                            if (var4.getComponentType() == 2) {
                                var30 = var12;
                                var10001 = var17 * 3 + var19;
                                var10002 = var10[var17 * 3 + var19];
                            } else {
                                var30 = var12;
                                var10001 = var17 * 3 + var19;
                                var10002 = (short) (var11[var17 * 3 + var19] * 257);
                            }

                            var30[var10001] = var10002;
                        }
                    }
                } else {
                    this.aFloatArray1127[var17 * 3] = this.aFloatArray1127[var17 * 3 + 1] = this.aFloatArray1127[var17 * 3 + 2] = 0.0F;
                    if (var12 != null) {
                        var12[var17 * 3] = var12[var17 * 3 + 1] = var12[var17 * 3 + 2] = 0;
                    }

                    if (var13 != 0 && this.aFloatArray1134[0] != 0.0F) {
                        for (var19 = 0; var19 <= var15; ++var19) {
                            WeightedTransform var20;
                            if ((var20 = (WeightedTransform) var6.elementAt(var19)).m_lastVertex >= var17) {
                                if (var20.m_lastVertex == var17) {
                                    --var14;
                                    var26 += var20.m_weight;
                                }

                                float var21 = (float) var20.m_weight / (float) var13;

                                int var22;
                                for (var22 = 0; var22 < 3; ++var22) {
                                    if (var3.getComponentType() == 2) {
                                        var10000 = this.aFloatArray1132;
                                        var10001 = var22;
                                        var10002 = var23[var17 * 3 + var22];
                                    } else {
                                        var10000 = this.aFloatArray1132;
                                        var10001 = var22;
                                        var10002 = var24[var17 * 3 + var22];
                                    }

                                    var10000[var10001] = (float) var10002;
                                }

                                this.aFloatArray1132[0] = var21 * (this.aFloatArray1132[0] * this.aFloatArray1134[0] + this.aFloatArray1134[1]);
                                this.aFloatArray1132[1] = var21 * (this.aFloatArray1132[1] * this.aFloatArray1134[0] + this.aFloatArray1134[2]);
                                this.aFloatArray1132[2] = var21 * (this.aFloatArray1132[2] * this.aFloatArray1134[0] + this.aFloatArray1134[3]);
                                this.aFloatArray1132[3] = var21;
                                var20.m_positionTransform.transform(this.aFloatArray1132);
                                this.aFloatArray1127[var17 * 3] += this.aFloatArray1132[0];
                                this.aFloatArray1127[var17 * 3 + 1] += this.aFloatArray1132[1];
                                this.aFloatArray1127[var17 * 3 + 2] += this.aFloatArray1132[2];

                                for (var22 = 0; var22 < 3; ++var22) {
                                    var10000 = this.aFloatArray1135;
                                    if (this.aFloatArray1135[var22] <= this.aFloatArray1127[var17 * 3 + var22]) {
                                        var29 = this.aFloatArray1127;
                                        var10003 = var17 * 3 + var22;
                                    } else {
                                        var29 = this.aFloatArray1135;
                                        var10003 = var22;
                                    }

                                    var10000[var22] = var29[var10003];
                                    var10000 = this.aFloatArray1136;
                                    if (this.aFloatArray1136[var22] >= this.aFloatArray1127[var17 * 3 + var22]) {
                                        var29 = this.aFloatArray1127;
                                        var10003 = var17 * 3 + var22;
                                    } else {
                                        var29 = this.aFloatArray1136;
                                        var10003 = var22;
                                    }

                                    var10000[var22] = var29[var10003];
                                }

                                if (var12 != null) {
                                    for (var22 = 0; var22 < 3; ++var22) {
                                        float var31;
                                        float var32;
                                        if (var4.getComponentType() == 2) {
                                            var10000 = this.aFloatArray1132;
                                            var10001 = var22;
                                            var31 = (float) var10[var17 * 3 + var22] + 0.5F;
                                            var32 = 32767.5F;
                                        } else {
                                            var10000 = this.aFloatArray1132;
                                            var10001 = var22;
                                            var31 = (float) var11[var17 * 3 + var22] + 0.5F;
                                            var32 = 127.5F;
                                        }

                                        var10000[var10001] = var31 / var32;
                                    }

                                    this.aFloatArray1132[0] *= var21;
                                    this.aFloatArray1132[1] *= var21;
                                    this.aFloatArray1132[2] *= var21;
                                    this.aFloatArray1132[3] = 0.0F;
                                    var20.m_normalTransform.transform(this.aFloatArray1132);
                                    var12[var17 * 3] += (short) G3DUtils.round(this.aFloatArray1132[0] * 32767.5F - 0.5F);
                                    var12[var17 * 3 + 1] += (short) G3DUtils.round(this.aFloatArray1132[1] * 32767.5F - 0.5F);
                                    var12[var17 * 3 + 2] += (short) G3DUtils.round(this.aFloatArray1132[2] * 32767.5F - 0.5F);
                                }
                            }
                        }

                        var13 -= var26;
                    }
                }
            }

            for (var17 = 0; var17 < 3; ++var17) {
                this.aFloatArray1132[var17] = (this.aFloatArray1136[var17] + this.aFloatArray1135[var17]) / 2.0F;
                float var28 = (this.aFloatArray1135[var17] - this.aFloatArray1136[var17]) / 2.0F;
                var16 = var16 <= var28 ? var28 : var16;
            }

            float var27 = var16 != 0.0F ? var16 / 32767.0F : 1.0F;

            for (var26 = 0; var26 < var5; ++var26) {
                var25[var26 * 3] = (short) G3DUtils.round((this.aFloatArray1127[var26 * 3] - this.aFloatArray1132[0]) / var27);
                var25[var26 * 3 + 1] = (short) G3DUtils.round((this.aFloatArray1127[var26 * 3 + 1] - this.aFloatArray1132[1]) / var27);
                var25[var26 * 3 + 2] = (short) G3DUtils.round((this.aFloatArray1127[var26 * 3 + 2] - this.aFloatArray1132[2]) / var27);
            }

            this.morphed.setPositions(this.aVertexArray1126, var27, this.aFloatArray1132);
        }
    }
}
