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
                boolean var6 = false;
                Vector4f var7 = new Vector4f(ray[0], ray[1], ray[2], 1.0F);
                Vector4f var8 = new Vector4f(ray[3], ray[4], ray[5], 1.0F);
                Transform var9;
                (var9 = new Transform()).set(transform);
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

                for (int var20 = 0; var20 < this.submeshes.length; ++var20) {
                    if (this.appearances[var20] != null && this.submeshes[var20] != null) {
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

                        TriangleStripArray var22 = (TriangleStripArray) this.submeshes[var20];

                        for (int var23 = 0; var22.getIndices(var23, var16); ++var23) {
                            int var24 = vb.getVertexCount();
                            if (var16[0] >= var24 || var16[1] >= var24 || var16[2] >= var24) {
                                throw new IllegalStateException("Index overflow: (" + var16[0] + ", " + var16[1] + ", " + var16[2] + ") >=" + var24);
                            }

                            if (var16[0] < 0 || var16[1] < 0 || var16[2] < 0) {
                                throw new IllegalStateException("Index underflow");
                            }

                            vb.getVertex(var16[0], var10);
                            vb.getVertex(var16[1], var11);
                            vb.getVertex(var16[2], var12);
                            if (G3DUtils.intersectTriangle(var7, var8, var10, var11, var12, var14, var16[3] ^ var21) && ri.testDistance(var14.x)) {
                                if (vb.getNormalVertex(var16[0], var10)) {
                                    vb.getNormalVertex(var16[1], var11);
                                    vb.getNormalVertex(var16[2], var12);
                                    (var19 = new float[3])[0] = var10.x * (1.0F - (var14.y + var14.z)) + var11.x * var14.y + var12.x * var14.z;
                                    var19[1] = var10.y * (1.0F - (var14.y + var14.z)) + var11.y * var14.y + var12.y * var14.z;
                                    var19[2] = var10.z * (1.0F - (var14.y + var14.z)) + var11.z * var14.y + var12.z * var14.z;
                                }

                                for (int var25 = 0; var25 < var17.length; ++var25) {
                                    int var10001;
                                    float var10002;
                                    float[] var26;
                                    if (vb.getTexVertex(var16[0], var25, var10)) {
                                        vb.getTexVertex(var16[1], var25, var11);
                                        vb.getTexVertex(var16[2], var25, var12);
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

                                if (ri.endPick(var14.x, var18, var17, var20, this, var19)) {
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
