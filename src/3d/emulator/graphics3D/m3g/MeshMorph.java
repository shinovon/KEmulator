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
    private static MeshMorph viewInst;
    private Hashtable cacheTable = new Hashtable();
    private Hashtable aHashtable1129 = new Hashtable();
    public VertexBuffer morphed;
    private VertexArray[] tmpMorphTargets;
    private VertexArray positions;
    private VertexArray normals;
    private VertexArray colors;
    private VertexArray[] uvms;
    private float[] tmpPositions;
    private float[] tmpBias = new float[4];
    private float[] tmpScaleBias = new float[4];
    private float[] maxPos = new float[3];
    private float[] minPos = new float[3];

    public static MeshMorph getInstance() {
        if (inst == null) {
            inst = new MeshMorph();
        }

        return inst;
    }

    public static MeshMorph getViewInstance() {
        if (viewInst == null) {
            viewInst = new MeshMorph();
        }

        return viewInst;
    }

    public final void clearCache() {
        cacheTable.clear();
    }

    public final VertexBuffer getMorphedVertexBuffer(Mesh mesh) {
        VertexBuffer cacheVB = (VertexBuffer) cacheTable.get(mesh);

        if (cacheVB != null) {
            morphed = cacheVB;
        } else {
            if (mesh instanceof MorphingMesh) {
                processMorph((MorphingMesh) mesh);
                cacheTable.put(mesh, morphed);

                return morphed;
            }

            if (mesh instanceof SkinnedMesh) {
                processSkinning((SkinnedMesh) mesh);
                cacheTable.put(mesh, morphed);

                return morphed;
            }

            morphed = mesh.getVertexBuffer();
        }

        return morphed;
    }

    private void processMorph(MorphingMesh mesh) {
        VertexBuffer meshVB = mesh.getVertexBuffer();
        setMorphVB(meshVB);

        int morphTargets = mesh.getMorphTargetCount();
        tmpMorphTargets = new VertexArray[morphTargets];

        float baseWeight = mesh.getBaseWeight();
        float[] weights = new float[morphTargets];
        mesh.getWeights(weights);

        if (positions != null) {
            int nullTargets = 0;

            for (int i = 0; i < morphTargets; ++i) {
                tmpMorphTargets[i] = mesh.getMorphTarget(i).getPositions((float[]) null);
                if (tmpMorphTargets[i] == null) nullTargets++;
            }

            if (nullTargets != morphTargets && nullTargets != 0) {
                throw new IllegalStateException();
            }

            if (nullTargets == 0) {
                positions.morph(tmpMorphTargets, meshVB.getPositions((float[]) null), weights, baseWeight);
            }
        } else {
            for (int i = 0; i < morphTargets; ++i) {
                if (mesh.getMorphTarget(i).getPositions((float[]) null) != null) {
                    throw new IllegalStateException();
                }
            }
        }

        if (normals != null) {
            int nullTargets = 0;

            for (int i = 0; i < morphTargets; ++i) {
                tmpMorphTargets[i] = mesh.getMorphTarget(i).getNormals();
                if (tmpMorphTargets[i] == null) nullTargets++;
            }

            if (nullTargets != morphTargets && nullTargets != 0) {
                throw new IllegalStateException();
            }

            if (nullTargets == 0) {
                normals.morph(tmpMorphTargets, meshVB.getNormals(), weights, baseWeight);
            }
        } else {
            for (int i = 0; i < morphTargets; ++i) {
                if (mesh.getMorphTarget(i).getNormals() != null) {
                    throw new IllegalStateException();
                }
            }
        }

        if (colors != null) {
            int nullTargets = 0;

            for (int i = 0; i < morphTargets; ++i) {
                tmpMorphTargets[i] = mesh.getMorphTarget(i).getColors();
                if (tmpMorphTargets[i] == null) nullTargets++;
            }

            if (nullTargets != morphTargets && nullTargets != 0) {
                throw new IllegalStateException();
            }

            if (nullTargets == 0) {
                colors.morphColors(tmpMorphTargets, meshVB.getColors(), weights, baseWeight);
            }
        } else {
            for (int i = 0; i < morphTargets; ++i) {
                if (mesh.getMorphTarget(i).getColors() != null) {
                    throw new IllegalStateException();
                }
            }

            int defaultCol = meshVB.getDefaultColor();

            float a = baseWeight * (defaultCol >> 24 & 255);
            float r = baseWeight * (defaultCol >> 16 & 255);
            float g = baseWeight * (defaultCol >> 8 & 255);
            float b = baseWeight * (defaultCol & 255);

            for (int i = 0; i < morphTargets; ++i) {
                int col = mesh.getMorphTarget(i).getDefaultColor();

                a += weights[i] * (col >> 24 & 255);
                r += weights[i] * (col >> 16 & 255);
                g += weights[i] * (col >> 8 & 255);
                b += weights[i] * (col & 255);
            }


            defaultCol = (G3DUtils.limit(G3DUtils.round(a), 0, 255) << 24) |
                (G3DUtils.limit(G3DUtils.round(r), 0, 255) << 16) |
                (G3DUtils.limit(G3DUtils.round(g), 0, 255) << 8) |
                (G3DUtils.limit(G3DUtils.round(b), 0, 255) << 0);

            morphed.setDefaultColor(defaultCol);
        }

        for (int i = 0; i < uvms.length; ++i) {
            if (uvms[i] == null) {
                for (int t = 0; t < morphTargets; ++t) {
                    if (mesh.getMorphTarget(t).getTexCoords(i, (float[]) null) != null) {
                        throw new IllegalStateException();
                    }
                }
            } else {
                int nullTargets = 0;

                for (int t = 0; t < morphTargets; ++t) {
                    tmpMorphTargets[t] = mesh.getMorphTarget(t).getTexCoords(i, (float[]) null);
                    if (tmpMorphTargets[t] == null) nullTargets++;
                }

                if (nullTargets != morphTargets && nullTargets != 0) {
                    throw new IllegalStateException();
                }

                if (nullTargets == 0) {
                    uvms[i].morph(tmpMorphTargets, meshVB.getTexCoords(i, (float[]) null), weights, baseWeight);
                }
            }
        }
    }

    private void setMorphVB(VertexBuffer vb) {
        morphed = (VertexBuffer) vb.duplicate();

        if (vb.getPositions((float[]) null) != null) {
            positions = (VertexArray) vb.getPositions(tmpScaleBias).duplicate();

            tmpBias[0] = tmpScaleBias[1];
            tmpBias[1] = tmpScaleBias[2];
            tmpBias[2] = tmpScaleBias[3];

            morphed.setPositions(positions, tmpScaleBias[0], tmpBias);
        } else {
            positions = null;
        }

        if (vb.getNormals() != null) {
            normals = (VertexArray) vb.getNormals().duplicate();
            morphed.setNormals(normals);
        } else {
            normals = null;
        }

        if (vb.getColors() != null) {
            colors = (VertexArray) vb.getColors().duplicate();
            morphed.setColors(colors);
        } else {
            colors = null;
        }

        uvms = new VertexArray[Emulator3D.NumTextureUnits];

        for (int i = 0; i < uvms.length; ++i) {
            if (vb.getTexCoords(i, (float[]) null) != null) {
                uvms[i] = (VertexArray) vb.getTexCoords(i, tmpScaleBias).duplicate();

                tmpBias[0] = tmpScaleBias[1];
                tmpBias[1] = tmpScaleBias[2];
                tmpBias[2] = tmpScaleBias[3];

                morphed.setTexCoords(i, uvms[i], tmpScaleBias[0], tmpBias);
            } else {
                uvms[i] = null;
            }
        }
    }

    private void setSkinVB(VertexBuffer vb) {
        morphed = (VertexBuffer) vb.duplicate();

        int vertexCount = vb.getPositions((float[]) null).getVertexCount();
        if (positions == null || positions.getVertexCount() != vertexCount) {
            positions = new VertexArray(vertexCount, 3, 2);
        }

        morphed.setPositions(positions, 1.0F, (float[]) null);
        if (tmpPositions == null || tmpPositions.length < vertexCount * 3) {
            tmpPositions = new float[vertexCount * 3];
        }

        if (vb.getNormals() != null) {
            vertexCount = vb.getNormals().getVertexCount();
            if (normals == null || normals.getVertexCount() != vertexCount) {
                normals = new VertexArray(vertexCount, 3, 2);
            }

            morphed.setNormals(normals);
        }

    }

    private void processSkinning(SkinnedMesh mesh) {
        VertexBuffer meshVB = mesh.getVertexBuffer();
        setSkinVB(meshVB);

        VertexArray meshPoses = meshVB.getPositions(tmpScaleBias);
        if (meshPoses == null) {
            throw new IllegalStateException();
        } else {
            VertexArray var4 = meshVB.getNormals();
            int vertexCount = meshVB.getVertexCount();
            Vector var6 = mesh.getTransforms();

            for (int var7 = 0; var7 < var6.size(); ++var7) {
                WeightedTransform var8;
                if ((var8 = (WeightedTransform) var6.elementAt(var7)).m_firstVertex < 0 || var8.m_lastVertex >= vertexCount) {
                    throw new IllegalStateException();
                }

                WeightedTransform var9;
                if ((var9 = (WeightedTransform) this.aHashtable1129.get(var8.m_bone)) != null) {
                    var8.m_positionTransform.set(var9.m_positionTransform);
                    if (var4 != null) {
                        var8.m_normalTransform.set(var9.m_normalTransform);
                    }
                } else {
                    var8.m_bone.getTransformTo(mesh, var8.m_positionTransform);
                    var8.m_positionTransform.postMultiply(var8.m_toBoneTransform);
                    if (var4 != null) {
                        var8.m_normalTransform.set(var8.m_positionTransform);
//                        ((Transform3D) var8.m_normalTransform.getImpl()).method445();
                        ((Transform3D) var8.m_normalTransform.getImpl()).invert();
                        var8.m_normalTransform.transpose();
                    }

                    this.aHashtable1129.put(var8.m_bone, var8);
                }
            }

            this.aHashtable1129.clear();
            short[] var23 = meshPoses.getShortValues();
            byte[] var24 = meshPoses.getByteValues();
            short[] positionsVals = this.positions.getShortValues();
            short[] var10 = var4 == null ? null : var4.getShortValues();
            byte[] var11 = var4 == null ? null : var4.getByteValues();
            short[] var12 = var4 == null ? null : this.normals.getShortValues();
            int var13 = 0;
            int var14 = 0;
            int var15 = -1;
            float maxAxisSize = 0.0F;

            this.maxPos[0] = this.maxPos[1] = this.maxPos[2] = -Float.MAX_VALUE;
            this.minPos[0] = this.minPos[1] = this.minPos[2] = Float.MAX_VALUE;

            int var17;
            int var26;
            for (var17 = 0; var17 < vertexCount; ++var17) {
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
                        if (meshPoses.getComponentType() == 2) {
                            var10000 = this.tmpPositions;
                            var10001 = var17 * 3 + var19;
                            var10002 = var23[var17 * 3 + var19];
                        } else {
                            var10000 = this.tmpPositions;
                            var10001 = var17 * 3 + var19;
                            var10002 = (short) (var24[var17 * 3 + var19] * 257);
                        }

                        var10000[var10001] = (float) var10002;
                        this.tmpPositions[var17 * 3 + var19] = this.tmpPositions[var17 * 3 + var19] * this.tmpScaleBias[0] + this.tmpScaleBias[var19 + 1];
                        var10000 = this.maxPos;
                        if (this.maxPos[var19] <= this.tmpPositions[var17 * 3 + var19]) {
                            var29 = this.tmpPositions;
                            var10003 = var17 * 3 + var19;
                        } else {
                            var29 = this.maxPos;
                            var10003 = var19;
                        }

                        var10000[var19] = var29[var10003];
                        var10000 = this.minPos;
                        if (this.minPos[var19] >= this.tmpPositions[var17 * 3 + var19]) {
                            var29 = this.tmpPositions;
                            var10003 = var17 * 3 + var19;
                        } else {
                            var29 = this.minPos;
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
                    this.tmpPositions[var17 * 3] = this.tmpPositions[var17 * 3 + 1] = this.tmpPositions[var17 * 3 + 2] = 0.0F;
                    if (var12 != null) {
                        var12[var17 * 3] = var12[var17 * 3 + 1] = var12[var17 * 3 + 2] = 0;
                    }

                    if (var13 != 0 && this.tmpScaleBias[0] != 0.0F) {
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
                                    if (meshPoses.getComponentType() == 2) {
                                        var10000 = this.tmpBias;
                                        var10001 = var22;
                                        var10002 = var23[var17 * 3 + var22];
                                    } else {
                                        var10000 = this.tmpBias;
                                        var10001 = var22;
                                        var10002 = var24[var17 * 3 + var22];
                                    }

                                    var10000[var10001] = (float) var10002;
                                }

                                this.tmpBias[0] = var21 * (this.tmpBias[0] * this.tmpScaleBias[0] + this.tmpScaleBias[1]);
                                this.tmpBias[1] = var21 * (this.tmpBias[1] * this.tmpScaleBias[0] + this.tmpScaleBias[2]);
                                this.tmpBias[2] = var21 * (this.tmpBias[2] * this.tmpScaleBias[0] + this.tmpScaleBias[3]);
                                this.tmpBias[3] = var21;
                                var20.m_positionTransform.transform(this.tmpBias);
                                this.tmpPositions[var17 * 3] += this.tmpBias[0];
                                this.tmpPositions[var17 * 3 + 1] += this.tmpBias[1];
                                this.tmpPositions[var17 * 3 + 2] += this.tmpBias[2];

                                for (var22 = 0; var22 < 3; ++var22) {
                                    var10000 = this.maxPos;
                                    if (this.maxPos[var22] <= this.tmpPositions[var17 * 3 + var22]) {
                                        var29 = this.tmpPositions;
                                        var10003 = var17 * 3 + var22;
                                    } else {
                                        var29 = this.maxPos;
                                        var10003 = var22;
                                    }

                                    var10000[var22] = var29[var10003];
                                    var10000 = this.minPos;
                                    if (this.minPos[var22] >= this.tmpPositions[var17 * 3 + var22]) {
                                        var29 = this.tmpPositions;
                                        var10003 = var17 * 3 + var22;
                                    } else {
                                        var29 = this.minPos;
                                        var10003 = var22;
                                    }

                                    var10000[var22] = var29[var10003];
                                }

                                if (var12 != null) {
                                    for (var22 = 0; var22 < 3; ++var22) {
                                        float var31;
                                        float var32;
                                        if (var4.getComponentType() == 2) {
                                            var10000 = this.tmpBias;
                                            var10001 = var22;
                                            var31 = (float) var10[var17 * 3 + var22] + 0.5F;
                                            var32 = 32767.5F;
                                        } else {
                                            var10000 = this.tmpBias;
                                            var10001 = var22;
                                            var31 = (float) var11[var17 * 3 + var22] + 0.5F;
                                            var32 = 127.5F;
                                        }

                                        var10000[var10001] = var31 / var32;
                                    }

                                    this.tmpBias[0] *= var21;
                                    this.tmpBias[1] *= var21;
                                    this.tmpBias[2] *= var21;
                                    this.tmpBias[3] = 0.0F;
                                    var20.m_normalTransform.transform(this.tmpBias);
                                    var12[var17 * 3] += (short) G3DUtils.round(this.tmpBias[0] * 32767.5F - 0.5F);
                                    var12[var17 * 3 + 1] += (short) G3DUtils.round(this.tmpBias[1] * 32767.5F - 0.5F);
                                    var12[var17 * 3 + 2] += (short) G3DUtils.round(this.tmpBias[2] * 32767.5F - 0.5F);
                                }
                            }
                        }

                        var13 -= var26;
                    }
                }
            }

            for (int axis = 0; axis < 3; ++axis) {
                tmpBias[axis] = (minPos[axis] + maxPos[axis]) / 2;

                float tmpScale = (maxPos[axis] - minPos[axis]) / 2;
                if(maxAxisSize < tmpScale) maxAxisSize = tmpScale;
            }

            float scale = maxAxisSize != 0.0F ? maxAxisSize / 32767.0F : 1.0F;

            for (int i = 0; i < vertexCount; i++) {
                positionsVals[i * 3 + 0] = (short) G3DUtils.round((tmpPositions[i * 3 + 0] - tmpBias[0]) / scale);
                positionsVals[i * 3 + 1] = (short) G3DUtils.round((tmpPositions[i * 3 + 1] - tmpBias[1]) / scale);
                positionsVals[i * 3 + 2] = (short) G3DUtils.round((tmpPositions[i * 3 + 2] - tmpBias[2]) / scale);
            }

            morphed.setPositions(positions, scale, tmpBias);
        }
    }
}
