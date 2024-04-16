package emulator.graphics3D;

public final class b {
    public static final b ab607 = new b(1.0F, 0.0F, 0.0F, 0.0F);
    public static final b ab609 = new b(0.0F, 1.0F, 0.0F, 0.0F);
    public static final b ab611 = new b(0.0F, 0.0F, 1.0F, 0.0F);
    public static final b ab613 = new b(0.0F, 0.0F, 0.0F, 1.0F);
    public float aFloat608;
    public float aFloat610;
    public float aFloat612;
    public float aFloat614;

    public b() {
    }

    public b(float var1, float var2, float var3, float var4) {
        this.aFloat608 = var1;
        this.aFloat610 = var2;
        this.aFloat612 = var3;
        this.aFloat614 = var4;
    }

    public b(b var1) {
        this.aFloat608 = var1.aFloat608;
        this.aFloat610 = var1.aFloat610;
        this.aFloat612 = var1.aFloat612;
        this.aFloat614 = var1.aFloat614;
    }

    public final void method422(float var1, float var2, float var3, float var4) {
        this.aFloat608 = var1;
        this.aFloat610 = var2;
        this.aFloat612 = var3;
        this.aFloat614 = var4;
    }

    public final void method423(b var1) {
        this.aFloat608 = var1.aFloat608;
        this.aFloat610 = var1.aFloat610;
        this.aFloat612 = var1.aFloat612;
        this.aFloat614 = var1.aFloat614;
    }

    public final float method424() {
        return (float) Math.sqrt((double) (this.aFloat608 * this.aFloat608 + this.aFloat610 * this.aFloat610 + this.aFloat612 * this.aFloat612));
    }

    public final void method425(float var1) {
        this.aFloat608 *= var1;
        this.aFloat610 *= var1;
        this.aFloat612 *= var1;
        this.aFloat614 *= var1;
    }

    public final void method429(b var1) {
        this.aFloat608 += var1.aFloat608;
        this.aFloat610 += var1.aFloat610;
        this.aFloat612 += var1.aFloat612;
        this.aFloat614 += var1.aFloat614;
    }

    public final void method431(b var1) {
        this.aFloat608 -= var1.aFloat608;
        this.aFloat610 -= var1.aFloat610;
        this.aFloat612 -= var1.aFloat612;
        this.aFloat614 -= var1.aFloat614;
    }

    public final void method426(b var1, b var2) {
        this.aFloat608 = var1.aFloat608 - var2.aFloat608;
        this.aFloat610 = var1.aFloat610 - var2.aFloat610;
        this.aFloat612 = var1.aFloat612 - var2.aFloat612;
        this.aFloat614 = var1.aFloat614 - var2.aFloat614;
    }

    public final float method427(b var1) {
        return this.aFloat608 * var1.aFloat608 + this.aFloat610 * var1.aFloat610 + this.aFloat612 * var1.aFloat612 + this.aFloat614 * var1.aFloat614;
    }

    public final void method430(b var1, b var2) {
        this.aFloat608 = var1.aFloat610 * var2.aFloat612 - var1.aFloat612 * var2.aFloat610;
        this.aFloat610 = var1.aFloat612 * var2.aFloat608 - var1.aFloat608 * var2.aFloat612;
        this.aFloat612 = var1.aFloat608 * var2.aFloat610 - var1.aFloat610 * var2.aFloat608;
        this.aFloat614 = 0.0F;
    }

    public final boolean method428() {
        float var1;
        if ((var1 = this.aFloat608 * this.aFloat608 + this.aFloat610 * this.aFloat610 + this.aFloat612 * this.aFloat612 + this.aFloat614 * this.aFloat614) < 1.0E-5F) {
            return false;
        } else {
            float var2 = 1.0F / (float) Math.sqrt((double) var1);
            this.aFloat608 *= var2;
            this.aFloat610 *= var2;
            this.aFloat612 *= var2;
            this.aFloat614 *= var2;
            return true;
        }
    }
}
