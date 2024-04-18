package emulator.graphics3D;

public final class Vector4f {
    public static final Vector4f X_AXIS = new Vector4f(1.0F, 0.0F, 0.0F, 0.0F);
    public static final Vector4f Y_AXIS = new Vector4f(0.0F, 1.0F, 0.0F, 0.0F);
    public static final Vector4f Z_AXIS = new Vector4f(0.0F, 0.0F, 1.0F, 0.0F);
    public static final Vector4f ORIGIN = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
    public float x;
    public float y;
    public float z;
    public float w;

    public Vector4f() {
    }

    public Vector4f(float var1, float var2, float var3, float var4) {
        this.x = var1;
        this.y = var2;
        this.z = var3;
        this.w = var4;
    }

    public Vector4f(Vector4f var1) {
        this.x = var1.x;
        this.y = var1.y;
        this.z = var1.z;
        this.w = var1.w;
    }

    public final void set(float var1, float var2, float var3, float var4) {
        this.x = var1;
        this.y = var2;
        this.z = var3;
        this.w = var4;
    }

    public final void set(Vector4f var1) {
        this.x = var1.x;
        this.y = var1.y;
        this.z = var1.z;
        this.w = var1.w;
    }

    public final float length() {
        return (float) Math.sqrt((double) (this.x * this.x + this.y * this.y + this.z * this.z));
    }

    public final void mul(float var1) {
        this.x *= var1;
        this.y *= var1;
        this.z *= var1;
        this.w *= var1;
    }

    public final void add(Vector4f var1) {
        this.x += var1.x;
        this.y += var1.y;
        this.z += var1.z;
        this.w += var1.w;
    }

    public final void sub(Vector4f var1) {
        this.x -= var1.x;
        this.y -= var1.y;
        this.z -= var1.z;
        this.w -= var1.w;
    }

    public final void sub(Vector4f var1, Vector4f var2) {
        this.x = var1.x - var2.x;
        this.y = var1.y - var2.y;
        this.z = var1.z - var2.z;
        this.w = var1.w - var2.w;
    }

    public final float dot(Vector4f var1) {
        return this.x * var1.x + this.y * var1.y + this.z * var1.z + this.w * var1.w;
    }

    public final void cross(Vector4f var1, Vector4f var2) {
        this.x = var1.y * var2.z - var1.z * var2.y;
        this.y = var1.z * var2.x - var1.x * var2.z;
        this.z = var1.x * var2.y - var1.y * var2.x;
        this.w = 0.0F;
    }

    public final boolean normalize() {
        float var1;
        if ((var1 = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w) < 1.0E-5F) {
            return false;
        } else {
            float var2 = 1.0F / (float) Math.sqrt((double) var1);
            this.x *= var2;
            this.y *= var2;
            this.z *= var2;
            this.w *= var2;
            return true;
        }
    }
}
