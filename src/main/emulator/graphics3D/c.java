package emulator.graphics3D;

import emulator.graphics3D.G3DUtils;
import emulator.graphics3D.a;
import emulator.graphics3D.b;

public final class c {
  public float[] aFloatArray615 = new float[16];
  private static final float[] aFloatArray616 = new float[]{1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
  private float[] aFloatArray617 = new float[4];

  public c() {
    this.method432();
  }

  public final void method432() {
    System.arraycopy(aFloatArray616, 0, this.aFloatArray615, 0, 16);
  }

  public final void method433(float[] var1) {
    System.arraycopy(this.aFloatArray615, 0, var1, 0, 16);
  }

  public final void method441(float[] var1) {
    System.arraycopy(var1, 0, this.aFloatArray615, 0, 16);
  }

  public final void method434(c var1) {
    System.arraycopy(var1.aFloatArray615, 0, this.aFloatArray615, 0, 16);
  }

  public final void method442() {
    if(!G3DUtils.intelSSE_Invert4x4(this.aFloatArray615, 1.0E-10F)) {
      throw new ArithmeticException();
    }
  }

  public final void method445() {
    G3DUtils.intelSSE_Invert4x4(this.aFloatArray615, 0.0F);
  }

  public final void method447() {
    this.method435(1, 4);
    this.method435(2, 8);
    this.method435(3, 12);
    this.method435(7, 13);
    this.method435(11, 14);
    this.method435(6, 9);
  }

  private void method435(int var1, int var2) {
    float var3 = this.aFloatArray615[var1];
    this.aFloatArray615[var1] = this.aFloatArray615[var2];
    this.aFloatArray615[var2] = var3;
  }

  public final void method436(c var1, boolean var2) {
    float[] var3 = new float[16];
    float[] var4 = (var2?this:var1).aFloatArray615;
    float[] var5 = (var2?var1:this).aFloatArray615;

    for(int var6 = 0; var6 < 4; ++var6) {
      for(int var7 = 0; var7 < 4; ++var7) {
        int var8 = var7 << 2;
        var3[var8 + var6] += var4[0 + var6] * var5[var8 + 0];
        var3[var8 + var6] += var4[4 + var6] * var5[var8 + 1];
        var3[var8 + var6] += var4[8 + var6] * var5[var8 + 2];
        var3[var8 + var6] += var4[12 + var6] * var5[var8 + 3];
      }
    }

    System.arraycopy(var3, 0, this.aFloatArray615, 0, 16);
  }

  public final void method437(float var1, float var2, float var3, float var4) {
    a var5;
    (var5 = new a()).method407(var1, var2, var3, var4);
    this.method443(var5.aFloat603, var5.aFloat604, var5.aFloat605, var5.aFloat606);
  }

  public final void method443(float var1, float var2, float var3, float var4) {
    a var5;
    (var5 = new a(var1, var2, var3, var4)).method404();
    c var6;
    float[] var7 = (var6 = new c()).aFloatArray615;
    float var8 = var5.aFloat603 * var5.aFloat603;
    float var9 = var5.aFloat603 * var5.aFloat604;
    float var10 = var5.aFloat603 * var5.aFloat605;
    float var11 = var5.aFloat603 * var5.aFloat606;
    float var12 = var5.aFloat604 * var5.aFloat604;
    float var13 = var5.aFloat604 * var5.aFloat605;
    float var14 = var5.aFloat604 * var5.aFloat606;
    float var15 = var5.aFloat605 * var5.aFloat605;
    float var16 = var5.aFloat605 * var5.aFloat606;
    var7[0] = 1.0F - 2.0F * (var12 + var15);
    var7[1] = 2.0F * (var9 - var16);
    var7[2] = 2.0F * (var10 + var14);
    var7[4] = 2.0F * (var9 + var16);
    var7[5] = 1.0F - 2.0F * (var8 + var15);
    var7[6] = 2.0F * (var13 - var11);
    var7[8] = 2.0F * (var10 - var14);
    var7[9] = 2.0F * (var13 + var11);
    var7[10] = 1.0F - 2.0F * (var8 + var12);
    this.method436(var6, false);
  }

  public final void method438(float var1, float var2, float var3) {
    this.aFloatArray615[0] *= var1;
    this.aFloatArray615[1] *= var2;
    this.aFloatArray615[2] *= var3;
    this.aFloatArray615[4] *= var1;
    this.aFloatArray615[5] *= var2;
    this.aFloatArray615[6] *= var3;
    this.aFloatArray615[8] *= var1;
    this.aFloatArray615[9] *= var2;
    this.aFloatArray615[10] *= var3;
    this.aFloatArray615[12] *= var1;
    this.aFloatArray615[13] *= var2;
    this.aFloatArray615[14] *= var3;
  }

  public final void method444(float var1, float var2, float var3) {
    this.aFloatArray615[3] += this.aFloatArray615[0] * var1 + this.aFloatArray615[1] * var2 + this.aFloatArray615[2] * var3;
    this.aFloatArray615[7] += this.aFloatArray615[4] * var1 + this.aFloatArray615[5] * var2 + this.aFloatArray615[6] * var3;
    this.aFloatArray615[11] += this.aFloatArray615[8] * var1 + this.aFloatArray615[9] * var2 + this.aFloatArray615[10] * var3;
    this.aFloatArray615[15] += this.aFloatArray615[12] * var1 + this.aFloatArray615[13] * var2 + this.aFloatArray615[14] * var3;
  }

  private void method439(float[] var1, int var2) {
    int var3 = 0;

    for(int var4 = 0; var4 < 4; ++var4) {
      float var5 = (var5 = 0.0F + this.aFloatArray615[var3 + 0] * var1[var2 + 0]) + this.aFloatArray615[var3 + 1] * var1[var2 + 1] + this.aFloatArray615[var3 + 2] * var1[var2 + 2] + this.aFloatArray615[var3 + 3] * var1[var2 + 3];
      this.aFloatArray617[var4] = var5;
      var3 += 4;
    }

    System.arraycopy(this.aFloatArray617, 0, var1, var2, 4);
  }

  public final void method446(float[] var1) {
    for(int var2 = 0; var2 + 4 <= var1.length; var2 += 4) {
      this.method439(var1, var2);
    }

  }

  public final void method440(b var1) {
    float var2 = this.aFloatArray615[0] * var1.aFloat608 + this.aFloatArray615[1] * var1.aFloat610 + this.aFloatArray615[2] * var1.aFloat612 + this.aFloatArray615[3] * var1.aFloat614;
    float var3 = this.aFloatArray615[4] * var1.aFloat608 + this.aFloatArray615[5] * var1.aFloat610 + this.aFloatArray615[6] * var1.aFloat612 + this.aFloatArray615[7] * var1.aFloat614;
    float var4 = this.aFloatArray615[8] * var1.aFloat608 + this.aFloatArray615[9] * var1.aFloat610 + this.aFloatArray615[10] * var1.aFloat612 + this.aFloatArray615[11] * var1.aFloat614;
    float var5 = this.aFloatArray615[12] * var1.aFloat608 + this.aFloatArray615[13] * var1.aFloat610 + this.aFloatArray615[14] * var1.aFloat612 + this.aFloatArray615[15] * var1.aFloat614;
    var1.aFloat608 = var2;
    var1.aFloat610 = var3;
    var1.aFloat612 = var4;
    var1.aFloat614 = var5;
  }
}
