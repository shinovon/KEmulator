package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;

public class VertexArray extends Object3D {
    private int componentType;
    private int vertexCount;
    private int componentCount;
    private byte[] byteValues;
    private short[] shortValues;

    public VertexArray(int var1, int var2, int var3) {
        if ((var1 >= 1 || var1 <= '\uffff') && (var2 >= 2 || var2 <= 4) && (var3 >= 1 || var3 <= 2)) {
            this.vertexCount = var1;
            this.componentCount = var2;
            this.componentType = var3;
            if (this.componentType == 1) {
                this.byteValues = new byte[var1 * var2];
            } else {
                this.shortValues = new short[var1 * var2];
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    protected Object3D duplicateObject() {
        VertexArray var1 = (VertexArray) super.duplicateObject();
        if (this.componentType == 1) {
            var1.byteValues = (byte[]) this.byteValues.clone();
        } else {
            var1.shortValues = (short[]) this.shortValues.clone();
        }

        return var1;
    }

    public void set(int var1, int var2, short[] var3) {
        if (var3 == null) {
            throw new NullPointerException();
        } else if (this.componentType == 2 && var2 >= 0 && var3.length >= var2 * this.componentCount) {
            if (var1 >= 0 && var1 + var2 <= this.vertexCount) {
                System.arraycopy(var3, 0, this.shortValues, var1 * this.componentCount, var2 * this.componentCount);
            } else {
                throw new IndexOutOfBoundsException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void set(int var1, int var2, byte[] var3) {
        if (var3 == null) {
            throw new NullPointerException();
        } else if (this.componentType == 1 && var2 >= 0 && var3.length >= var2 * this.componentCount) {
            if (var1 >= 0 && var1 + var2 <= this.vertexCount) {
                System.arraycopy(var3, 0, this.byteValues, var1 * this.componentCount, var2 * this.componentCount);
            } else {
                throw new IndexOutOfBoundsException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public int getComponentCount() {
        return this.componentCount;
    }

    public int getComponentType() {
        return this.componentType;
    }

    public void get(int var1, int var2, short[] var3) {
        if (var3 == null) {
            throw new NullPointerException();
        } else if (this.componentType == 2 && var2 >= 0 && var3.length >= var2 * this.componentCount) {
            if (var1 >= 0 && var1 + var2 <= this.vertexCount) {
                System.arraycopy(this.shortValues, var1 * this.componentCount, var3, 0, var2 * this.componentCount);
            } else {
                throw new IndexOutOfBoundsException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void get(int var1, int var2, byte[] var3) {
        if (var3 == null) {
            throw new NullPointerException();
        } else if (this.componentType == 1 && var2 >= 0 && var3.length >= var2 * this.componentCount) {
            if (var1 >= 0 && var1 + var2 <= this.vertexCount) {
                System.arraycopy(this.byteValues, var1 * this.componentCount, var3, 0, var2 * this.componentCount);
            } else {
                throw new IndexOutOfBoundsException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public byte[] getByteValues() {
        return this.byteValues;
    }

    public short[] getShortValues() {
        return this.shortValues;
    }

    private boolean equals_(VertexArray var1) {
        return var1 != null && this.componentType == var1.componentType && this.componentCount == var1.componentCount && this.vertexCount == var1.vertexCount;
    }

    public void morph(VertexArray[] var1, VertexArray var2, float[] var3, float var4) {
        int var5;
        for (var5 = 0; var5 < var1.length; ++var5) {
            if (!this.equals_(var1[var5])) {
                throw new IllegalStateException();
            }
        }

        float var6;
        int var7;
        if (this.componentType == 1) {
            for (var5 = 0; var5 < this.byteValues.length; ++var5) {
                var6 = 0.0F;

                for (var7 = 0; var7 < var1.length; ++var7) {
                    var6 += (float) var1[var7].byteValues[var5] * var3[var7];
                }

                var6 += (float) var2.byteValues[var5] * var4;
                this.byteValues[var5] = (byte) G3DUtils.round(var6);
            }
        } else {
            for (var5 = 0; var5 < this.shortValues.length; ++var5) {
                var6 = 0.0F;

                for (var7 = 0; var7 < var1.length; ++var7) {
                    var6 += (float) var1[var7].shortValues[var5] * var3[var7];
                }

                var6 += (float) var2.shortValues[var5] * var4;
                this.shortValues[var5] = (short) G3DUtils.round(var6);
            }
        }

    }

    public void morphColors(VertexArray[] var1, VertexArray var2, float[] var3, float var4) {
        int var5;
        for (var5 = 0; var5 < var1.length; ++var5) {
            if (!this.equals_(var1[var5])) {
                throw new IllegalStateException();
            }
        }

        if (this.componentType == 1) {
            for (var5 = 0; var5 < this.byteValues.length; ++var5) {
                float var6 = 0.0F;

                for (int var7 = 0; var7 < var1.length; ++var7) {
                    var6 += (float) ((short) var1[var7].byteValues[var5] & 255) * var3[var7];
                }

                var6 += (float) (var2.byteValues[var5] & 255) * var4;
                this.byteValues[var5] = (byte) G3DUtils.limit((int) (var6 + 0.5F), 0, 255);
            }
        }

    }
}
