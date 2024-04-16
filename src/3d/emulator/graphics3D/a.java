package emulator.graphics3D;

import emulator.graphics3D.b;

public final class a {
    public float aFloat603;
    public float aFloat604;
    public float aFloat605;
    public float aFloat606;

    public a() {
    }

    public a(float var1, float var2, float var3, float var4) {
        this.aFloat603 = var1;
        this.aFloat604 = var2;
        this.aFloat605 = var3;
        this.aFloat606 = var4;
    }

    public a(float[] var1) {
        this.method405(var1);
    }

    public a(a var1) {
        this.method406(var1);
    }

    public final void method405(float[] var1) {
        if (var1.length != 4) {
            throw new Error("Invalid number of components for quaternion");
        } else {
            this.aFloat603 = var1[0];
            this.aFloat604 = var1[1];
            this.aFloat605 = var1[2];
            this.aFloat606 = var1[3];
        }
    }

    public final void method406(a var1) {
        this.aFloat603 = var1.aFloat603;
        this.aFloat604 = var1.aFloat604;
        this.aFloat605 = var1.aFloat605;
        this.aFloat606 = var1.aFloat606;
    }

    private void method414() {
        this.aFloat603 = this.aFloat604 = this.aFloat605 = 0.0F;
        this.aFloat606 = 1.0F;
    }

    public final void method404() {
        a var10000;
        float var1;
        float var10001;
        if ((var1 = this.aFloat603 * this.aFloat603 + this.aFloat604 * this.aFloat604 + this.aFloat605 * this.aFloat605 + this.aFloat606 * this.aFloat606) > 1.0E-5F) {
            float var2 = 1.0F / (float) Math.sqrt((double) var1);
            this.aFloat603 *= var2;
            this.aFloat604 *= var2;
            this.aFloat605 *= var2;
            var10000 = this;
            var10001 = this.aFloat606 * var2;
        } else {
            this.aFloat603 = this.aFloat604 = this.aFloat605 = 0.0F;
            var10000 = this;
            var10001 = 1.0F;
        }

        var10000.aFloat606 = var10001;
    }

    public final void method407(float var1, float var2, float var3, float var4) {
        b var5;
        if ((var5 = new b(var2, var3, var4, 0.0F)).method428()) {
            float var6;
            float var7 = (float) Math.sin((double) (var6 = (float) Math.toRadians((double) (0.5F * var1))));
            this.aFloat603 = var7 * var5.aFloat608;
            this.aFloat604 = var7 * var5.aFloat610;
            this.aFloat605 = var7 * var5.aFloat612;
            this.aFloat606 = (float) Math.cos((double) var6);
        } else {
            this.method414();
        }
    }

    public final void method415(float[] var1) {
        this.method404();
        float[] var10000;
        byte var10001;
        float var2;
        float var10002;
        if ((var2 = 1.0F - this.aFloat606 * this.aFloat606) > 1.0E-5F) {
            float var3 = (float) Math.sqrt((double) var2);
            var1[1] = this.aFloat603 / var3;
            var1[2] = this.aFloat604 / var3;
            var10000 = var1;
            var10001 = 3;
            var10002 = this.aFloat605 / var3;
        } else {
            var1[1] = var1[2] = 0.0F;
            var10000 = var1;
            var10001 = 3;
            var10002 = 1.0F;
        }

        var10000[var10001] = var10002;
        var1[0] = (float) Math.toDegrees(Math.acos((double) this.aFloat606) * 2.0D);
    }

    public final void method416(a var1) {
        a var2 = new a(this);
        this.aFloat606 = var2.aFloat606 * var1.aFloat606 - var2.aFloat603 * var1.aFloat603 - var2.aFloat604 * var1.aFloat604 - var2.aFloat605 * var1.aFloat605;
        this.aFloat603 = var2.aFloat606 * var1.aFloat603 + var2.aFloat603 * var1.aFloat606 + var2.aFloat604 * var1.aFloat605 - var2.aFloat605 * var1.aFloat604;
        this.aFloat604 = var2.aFloat606 * var1.aFloat604 - var2.aFloat603 * var1.aFloat605 + var2.aFloat604 * var1.aFloat606 + var2.aFloat605 * var1.aFloat603;
        this.aFloat605 = var2.aFloat606 * var1.aFloat605 + var2.aFloat603 * var1.aFloat604 - var2.aFloat604 * var1.aFloat603 + var2.aFloat605 * var1.aFloat606;
    }

    public final void method408(float var1) {
        this.aFloat603 *= var1;
        this.aFloat604 *= var1;
        this.aFloat605 *= var1;
        this.aFloat606 *= var1;
    }

    public final void method417(a var1) {
        this.aFloat603 += var1.aFloat603;
        this.aFloat604 += var1.aFloat604;
        this.aFloat605 += var1.aFloat605;
        this.aFloat606 += var1.aFloat606;
    }

    public final void method418(a var1) {
        this.aFloat603 -= var1.aFloat603;
        this.aFloat604 -= var1.aFloat604;
        this.aFloat605 -= var1.aFloat605;
        this.aFloat606 -= var1.aFloat606;
    }

    private void method420(a var1) {
        this.aFloat603 = -var1.aFloat603;
        this.aFloat604 = -var1.aFloat604;
        this.aFloat605 = -var1.aFloat605;
        this.aFloat606 = var1.aFloat606;
    }

    private float method409(a var1) {
        return this.aFloat603 * var1.aFloat603 + this.aFloat604 * var1.aFloat604 + this.aFloat605 * var1.aFloat605 + this.aFloat606 * var1.aFloat606;
    }

    private void method421(a var1) {
        float var2;
        if ((var2 = (float) Math.sqrt((double) (var1.aFloat603 * var1.aFloat603 + var1.aFloat604 * var1.aFloat604 + var1.aFloat605 * var1.aFloat605))) > 1.0E-5F) {
            float var3 = (float) (Math.atan2((double) var2, (double) this.aFloat606) / (double) var2);
            this.aFloat603 = var3 * var1.aFloat603;
            this.aFloat604 = var3 * var1.aFloat604;
            this.aFloat605 = var3 * var1.aFloat605;
        } else {
            this.aFloat603 = this.aFloat604 = this.aFloat605 = 0.0F;
        }

        this.aFloat606 = 0.0F;
    }

    public final void method419(a var1) {
        a var10000;
        float var10001;
        float var2;
        if ((var2 = (float) Math.sqrt((double) (var1.aFloat603 * var1.aFloat603 + var1.aFloat604 * var1.aFloat604 + var1.aFloat605 * var1.aFloat605))) > 1.0E-5F) {
            float var3 = (float) Math.sin((double) var2) / var2;
            this.aFloat603 = var3 * var1.aFloat603;
            this.aFloat604 = var3 * var1.aFloat604;
            this.aFloat605 = var3 * var1.aFloat605;
            var10000 = this;
            var10001 = (float) Math.cos((double) var2);
        } else {
            this.aFloat603 = this.aFloat604 = this.aFloat605 = 0.0F;
            var10000 = this;
            var10001 = 1.0F;
        }

        var10000.aFloat606 = var10001;
    }

    public final void method410(a var1, a var2) {
        this.method406(var1);
        this.method420(this);
        this.method416(var2);
        this.method421(this);
    }

    public final void method411(float var1, a var2, a var3) {
        float var4;
        float var7;
        float var8;
        if ((var4 = var2.method409(var3)) + 1.0F > 1.0E-5F) {
            float var10000;
            if (1.0F - var4 > 1.0E-5F) {
                float var5;
                float var6 = (float) Math.sin((double) (var5 = (float) Math.acos((double) var4)));
                var7 = (float) Math.sin((double) ((1.0F - var1) * var5)) / var6;
                var10000 = (float) Math.sin((double) (var1 * var5)) / var6;
            } else {
                var7 = 1.0F - var1;
                var10000 = var1;
            }

            var8 = var10000;
            this.aFloat603 = var7 * var2.aFloat603 + var8 * var3.aFloat603;
            this.aFloat604 = var7 * var2.aFloat604 + var8 * var3.aFloat604;
            this.aFloat605 = var7 * var2.aFloat605 + var8 * var3.aFloat605;
            this.aFloat606 = var7 * var2.aFloat606 + var8 * var3.aFloat606;
        } else {
            this.aFloat603 = -var2.aFloat604;
            this.aFloat604 = var2.aFloat603;
            this.aFloat605 = -var2.aFloat606;
            this.aFloat606 = var2.aFloat605;
            var7 = (float) Math.sin((double) (1.0F - var1) * 3.141592653589793D / 2.0D);
            var8 = (float) Math.sin((double) var1 * 3.141592653589793D / 2.0D);
            this.aFloat603 = var7 * var2.aFloat603 + var8 * this.aFloat603;
            this.aFloat604 = var7 * var2.aFloat604 + var8 * this.aFloat604;
            this.aFloat605 = var7 * var2.aFloat605 + var8 * this.aFloat605;
        }
    }

    public final void method412(float var1, a var2, a var3, a var4, a var5) {
        a var6 = new a();
        a var7 = new a();
        var6.method411(var1, var2, var5);
        var7.method411(var1, var3, var4);
        this.method411(2.0F * var1 * (1.0F - var1), var6, var7);
    }

    public final void method413(b var1, b var2, b var3) {
        if (var1.aFloat614 == 0.0F && var2.aFloat614 == 0.0F) {
            b var4 = new b(var1);
            b var5 = new b(var2);
            float var7;
            if (var3 != null) {
                b var6;
                (var6 = new b(var3)).method428();
                var4.method428();
                var7 = var4.method427(var6);
                var6.method425(var7);
                var4.method431(var6);
                var6.method423(var3);
                var6.method428();
                var5.method428();
                var7 = var5.method427(var6);
                var6.method425(var7);
                var5.method431(var6);
            }

            if (var4.method428() && var5.method428()) {
                float var11;
                if ((var11 = var4.method427(var5)) > 0.99999F) {
                    this.method414();
                } else {
                    a var12;
                    float var10001;
                    if (var11 < -0.99999F) {
                        if (var3 == null) {
                            var3 = new b();
                            var7 = Math.abs(var4.aFloat608);
                            float var8 = Math.abs(var4.aFloat610);
                            float var9 = Math.abs(var4.aFloat612);
                            b var10000;
                            float var10002;
                            float var10003;
                            if (var7 <= var8 && var7 <= var9) {
                                var10000 = var3;
                                var10001 = 1.0F;
                                var10002 = 0.0F;
                                var10003 = 0.0F;
                            } else if (var8 <= var7 && var8 <= var9) {
                                var10000 = var3;
                                var10001 = 0.0F;
                                var10002 = 1.0F;
                                var10003 = 0.0F;
                            } else {
                                var10000 = var3;
                                var10001 = 0.0F;
                                var10002 = 0.0F;
                                var10003 = 1.0F;
                            }

                            var10000.method422(var10001, var10002, var10003, 0.0F);
                            float var10 = var3.method427(var4);
                            var4.method425(var10);
                            var3.method431(var4);
                        }

                        var12 = this;
                        var10001 = 180.0F;
                    } else {
                        (var3 = new b()).method430(var4, var5);
                        var12 = this;
                        var10001 = (float) Math.toDegrees(Math.acos((double) var11));
                    }

                    var12.method407(var10001, var3.aFloat608, var3.aFloat610, var3.aFloat612);
                }
            } else {
                this.method414();
            }
        } else {
            throw new Error();
        }
    }
}
