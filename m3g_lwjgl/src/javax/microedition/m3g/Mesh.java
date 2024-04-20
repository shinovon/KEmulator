package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.Vector4f;
import emulator.graphics3D.lwjgl.Emulator3D;

public class Mesh extends Node {
    protected VertexBuffer m_vertices;
    private IndexBuffer[] anIndexBufferArray888;
    private Appearance[] appearances;

    public Mesh(VertexBuffer var1, IndexBuffer var2, Appearance var3) {
        if (var1 != null && var2 != null) {
            this.m_vertices = var1;
            this.anIndexBufferArray888 = new IndexBuffer[1];
            this.anIndexBufferArray888[0] = var2;
            this.addReference(this.m_vertices);
            this.addReference(this.anIndexBufferArray888[0]);
            this.appearances = new Appearance[1];
            if (var3 != null) {
                this.appearances[0] = var3;
                this.addReference(this.appearances[0]);
            }

        } else {
            throw new NullPointerException();
        }
    }

    public Mesh(VertexBuffer var1, IndexBuffer[] var2, Appearance[] var3) {
        if (var1 != null && var2 != null) {
            if (var2.length != 0 && (var3 == null || var3.length >= var2.length)) {
                this.m_vertices = var1;
                this.addReference(this.m_vertices);
                this.anIndexBufferArray888 = new IndexBuffer[var2.length];
                this.appearances = new Appearance[var2.length];

                for (int var4 = var2.length - 1; var4 >= 0; --var4) {
                    if (var2[var4] == null) {
                        throw new NullPointerException();
                    }

                    this.anIndexBufferArray888[var4] = var2[var4];
                    this.addReference(this.anIndexBufferArray888[var4]);
                    if (var3 != null) {
                        this.appearances[var4] = var3[var4];
                        this.addReference(this.appearances[var4]);
                    }
                }

            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new NullPointerException();
        }
    }

    protected Object3D duplicateObject() {
        Mesh var1;
        (var1 = (Mesh) super.duplicateObject()).anIndexBufferArray888 = (IndexBuffer[]) this.anIndexBufferArray888.clone();
        var1.appearances = (Appearance[]) this.appearances.clone();
        return var1;
    }

    public void setAppearance(int var1, Appearance var2) {
        if (var1 >= 0 && var1 < this.anIndexBufferArray888.length) {
            this.removeReference(this.appearances[var1]);
            this.appearances[var1] = var2;
            this.addReference(this.appearances[var1]);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public Appearance getAppearance(int var1) {
        if (var1 >= 0 && var1 < this.anIndexBufferArray888.length) {
            return this.appearances[var1];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public IndexBuffer getIndexBuffer(int var1) {
        if (var1 >= 0 && var1 < this.anIndexBufferArray888.length) {
            return this.anIndexBufferArray888[var1];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public VertexBuffer getVertexBuffer() {
        return this.m_vertices;
    }

    public int getSubmeshCount() {
        return this.anIndexBufferArray888.length;
    }

    protected boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4) {
        return this.rayIntersect(var1, var2, var3, var4, this.m_vertices);
    }

    protected boolean rayIntersect(int var1, float[] var2, RayIntersection var3, Transform var4, VertexBuffer var5) {
        if (var5 != null && this.appearances != null && this.anIndexBufferArray888 != null) {
            if (var5.getPositions((float[]) null) == null) {
                throw new IllegalStateException("No vertex positions");
            } else {
                boolean var6 = false;
                Vector4f var7 = new Vector4f(var2[0], var2[1], var2[2], 1.0F);
                Vector4f var8 = new Vector4f(var2[3], var2[4], var2[5], 1.0F);
                Transform var9;
                (var9 = new Transform()).set(var4);
//                var9.getImpl_().method445();
                var9.getImpl_().invert();
                var9.getImpl_().transform(var7);
                var9.getImpl_().transform(var8);
                var7.mul(1.0F / var7.w);
                var8.mul(1.0F / var8.w);
                var8.sub(var7);
                Vector4f var10 = new Vector4f();
                Vector4f var11 = new Vector4f();
                Vector4f var12 = new Vector4f();
                Vector4f var13 = new Vector4f();
                Vector4f var14 = new Vector4f();
                Transform var15 = new Transform();
                int[] var16 = new int[4];
                float[] var17 = new float[Emulator3D.NumTextureUnits];
                float[] var18 = new float[Emulator3D.NumTextureUnits];
                float[] var19 = null;

                for (int var20 = 0; var20 < this.anIndexBufferArray888.length; ++var20) {
                    if (this.appearances[var20] != null && this.anIndexBufferArray888[var20] != null) {
                        int var21;
                        if (this.appearances[var20].getPolygonMode() != null) {
                            label120:
                            {
                                var21 = this.appearances[var20].getPolygonMode().getWinding() != 168 ? 1 : 0;
                                int var10000;
                                switch (this.appearances[var20].getPolygonMode().getCulling()) {
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

                        TriangleStripArray var22 = (TriangleStripArray) this.anIndexBufferArray888[var20];

                        for (int var23 = 0; var22.getIndices(var23, var16); ++var23) {
                            int var24 = var5.getVertexCount();
                            if (var16[0] >= var24 || var16[1] >= var24 || var16[2] >= var24) {
                                throw new IllegalStateException("Index overflow: (" + var16[0] + ", " + var16[1] + ", " + var16[2] + ") >=" + var24);
                            }

                            if (var16[0] < 0 || var16[1] < 0 || var16[2] < 0) {
                                throw new IllegalStateException("Index underflow");
                            }

                            var5.getVertex(var16[0], var10);
                            var5.getVertex(var16[1], var11);
                            var5.getVertex(var16[2], var12);
                            if (G3DUtils.intersectTriangle(var7, var8, var10, var11, var12, var14, var16[3] ^ var21) && var3.testDistance(var14.x)) {
                                if (var5.getNormalVertex(var16[0], var10)) {
                                    var5.getNormalVertex(var16[1], var11);
                                    var5.getNormalVertex(var16[2], var12);
                                    (var19 = new float[3])[0] = var10.x * (1.0F - (var14.y + var14.z)) + var11.x * var14.y + var12.x * var14.z;
                                    var19[1] = var10.y * (1.0F - (var14.y + var14.z)) + var11.y * var14.y + var12.y * var14.z;
                                    var19[2] = var10.z * (1.0F - (var14.y + var14.z)) + var11.z * var14.y + var12.z * var14.z;
                                }

                                for (int var25 = 0; var25 < var17.length; ++var25) {
                                    int var10001;
                                    float var10002;
                                    float[] var26;
                                    if (var5.getTexVertex(var16[0], var25, var10)) {
                                        var5.getTexVertex(var16[1], var25, var11);
                                        var5.getTexVertex(var16[2], var25, var12);
                                        var13.x = var10.x * (1.0F - (var14.y + var14.z)) + var11.x * var14.y + var12.x * var14.z;
                                        var13.y = var10.y * (1.0F - (var14.y + var14.z)) + var11.y * var14.y + var12.y * var14.z;
                                        var13.z = 0.0F;
                                        var13.w = 1.0F;
                                        if (this.appearances[var20] != null && this.appearances[var20].getTexture(var25) != null) {
                                            this.appearances[var20].getTexture(var25).getCompositeTransform(var15);
                                            var15.getImpl_().transform(var13);
                                            var13.mul(1.0F / var13.w);
                                        }

                                        var18[var25] = var13.x;
                                        var26 = var17;
                                        var10001 = var25;
                                        var10002 = var13.y;
                                    } else {
                                        var18[var25] = 0.0F;
                                        var26 = var17;
                                        var10001 = var25;
                                        var10002 = 0.0F;
                                    }

                                    var26[var10001] = var10002;
                                }

                                if (var3.endPick(var14.x, var18, var17, var20, this, var14.x, var19)) {
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
