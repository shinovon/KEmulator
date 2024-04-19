package javax.microedition.m3g;

import emulator.graphics3D.lwjgl.Emulator3D;

public class RayIntersection {
    float[] aFloatArray1156 = new float[6];
    float[] normal;
    Node intersected;
    int submeshIndex;
    float distance;
    float[] textureS;
    float[] textureT;
    private float aFloat1162;
    private Node root;
    private float pickX;
    private float pickY;
    private Camera camera;

    public RayIntersection() {
        this.aFloatArray1156[5] = 1.0F;
        this.intersected = null;
        this.submeshIndex = 0;
        this.distance = 0.0F;
        this.normal = new float[3];
        this.normal[2] = 1.0F;
        this.textureS = new float[Emulator3D.NumTextureUnits];
        this.textureT = new float[Emulator3D.NumTextureUnits];
    }

    public Node getIntersected() {
        return this.intersected;
    }

    public void getRay(float[] var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else if (var1.length < 6) {
            throw new IllegalArgumentException();
        } else {
            var1[0] = this.aFloatArray1156[0];
            var1[1] = this.aFloatArray1156[1];
            var1[2] = this.aFloatArray1156[2];
            var1[3] = this.aFloatArray1156[3] - this.aFloatArray1156[0];
            var1[4] = this.aFloatArray1156[4] - this.aFloatArray1156[1];
            var1[5] = this.aFloatArray1156[5] - this.aFloatArray1156[2];
        }
    }

    public float getDistance() {
        return this.distance;
    }

    public int getSubmeshIndex() {
        return this.submeshIndex;
    }

    public float getTextureS(int var1) {
        if (var1 >= 0 && var1 < Emulator3D.NumTextureUnits) {
            return this.textureS[var1];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public float getTextureT(int var1) {
        if (var1 >= 0 && var1 < Emulator3D.NumTextureUnits) {
            return this.textureT[var1];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public float getNormalX() {
        return this.normal[0];
    }

    public float getNormalY() {
        return this.normal[1];
    }

    public float getNormalZ() {
        return this.normal[2];
    }

    protected Node getRoot() {
        return this.root;
    }

    protected Camera getCamera() {
        return this.camera;
    }

    protected float getPickX() {
        return this.pickX;
    }

    protected float getPickY() {
        return this.pickY;
    }

    protected void startPick(Node var1, float[] var2, float var3, float var4, Camera var5) {
        this.root = var1;
        this.aFloatArray1156 = var2;
        this.pickX = var3;
        this.pickY = var4;
        this.camera = var5;
        this.aFloat1162 = Float.MAX_VALUE;
    }

    protected boolean testDistance(float var1) {
        return var1 > 0.0F && var1 < this.aFloat1162;
    }

    protected boolean endPick(float var1, float[] var2, float[] var3, int var4, Node var5, float var6, float[] var7) {
        if (var1 > 0.0F && var1 < this.aFloat1162) {
            int var8;
            if (var2 != null && var3 != null) {
                for (var8 = 0; var8 < var2.length; ++var8) {
                    this.textureS[var8] = var2[var8];
                    this.textureT[var8] = var3[var8];
                }
            } else {
                for (var8 = 0; var8 < this.textureS.length; ++var8) {
                    this.textureS[var8] = 0.0F;
                    this.textureT[var8] = 0.0F;
                }
            }

            float[] var10;
            byte var10001;
            float var10002;
            label26:
            {
                this.submeshIndex = var4;
                this.distance = var6;
                this.intersected = var5;
                RayIntersection var10000;
                if (var7 != null) {
                    this.normal[0] = var7[0];
                    this.normal[1] = var7[1];
                    this.normal[2] = var7[2];
                    float var9;
                    if ((var9 = (float) Math.sqrt((double) (var7[0] * var7[0] + var7[1] * var7[1] + var7[2] * var7[2]))) > 1.0E-5F) {
                        this.normal[0] /= var9;
                        this.normal[1] /= var9;
                        var10 = this.normal;
                        var10001 = 2;
                        var10002 = this.normal[2] / var9;
                        break label26;
                    }

                    var10000 = this;
                } else {
                    var10000 = this;
                }

                var10000.normal[0] = 0.0F;
                this.normal[1] = 0.0F;
                var10 = this.normal;
                var10001 = 2;
                var10002 = 1.0F;
            }

            var10[var10001] = var10002;
            this.aFloat1162 = var1;
            return true;
        } else {
            return false;
        }
    }
}
