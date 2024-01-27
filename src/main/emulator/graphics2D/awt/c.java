package emulator.graphics2D.awt;

import emulator.graphics2D.*;
import java.awt.geom.*;

/**
 * TransformAWT
 */
public final class c implements ITransform
{
    private AffineTransform transform;
    
    public c() {
        super();
        this.transform = new AffineTransform();
    }
    
    public c(final AffineTransform anAffineTransform4) {
        super();
        this.transform = anAffineTransform4;
    }
    
    public final AffineTransform method8() {
        return this.transform;
    }
    
    public final void transform(final ITransform transform) {
        this.transform.concatenate(((c)transform).method8());
    }
    
    public final void transform(final float[] array, final int n, final float[] array2, final int n2, final int n3) {
        this.transform.transform(array, n, array2, n2, n3);
    }

    public final ITransform newTransform(int var1, int var2, int var3, int var4, int var5, int var6) {
        var4 += a(var1, var2, var3, var6);
        var5 += b(var1, var2, var3, var6);
        AffineTransform var7;
        (var7 = new AffineTransform()).translate((double)var4, (double)var5);
        if(var3 == 6) {
            var7.translate(0.0D, (double)var1);
            var7.rotate(-1.5707963267948966D);
        } else {
            double var10001;
            double var10002;
            if(var3 == 4) {
                var7.rotate(-1.5707963267948966D);
                var10001 = -1.0D;
                var10002 = 1.0D;
            } else {
                if(var3 == 5) {
                    var7.translate((double)var2, 0.0D);
                    var7.rotate(1.5707963267948966D);
                    return new c(var7);
                }

                if(var3 == 7) {
                    var7.translate((double)var2, (double)var1);
                    var7.rotate(1.5707963267948966D);
                    var10001 = -1.0D;
                    var10002 = 1.0D;
                } else {
                    if(var3 == 3) {
                        var7.translate((double)var1, (double)var2);
                        var7.rotate(3.141592653589793D);
                        return new c(var7);
                    }

                    if(var3 == 2) {
                        var7.translate((double)var1, 0.0D);
                        var10001 = -1.0D;
                        var10002 = 1.0D;
                    } else {
                        if(var3 != 1) {
                            return new c(var7);
                        }

                        var7.translate(0.0D, (double)var2);
                        var10001 = 1.0D;
                        var10002 = -1.0D;
                    }
                }
            }

            var7.scale(var10001, var10002);
        }

        return new c(var7);
    }

    private static int a(int var0, int var1, int var2, int var3) {
        if(var2 <= 3) {
            if((var3 & 1) != 0) {
                return -(var0 >> 1);
            }

            if((var3 & 8) != 0) {
                return -var0;
            }
        } else {
            if((var3 & 1) != 0) {
                return -(var1 >> 1);
            }

            if((var3 & 8) != 0) {
                return -var1;
            }
        }

        return 0;
    }

    private static int b(int var0, int var1, int var2, int var3) {
        if(var2 <= 3) {
            if((var3 & 2) != 0) {
                return -(var1 >> 1);
            }

            if((var3 & 32) != 0) {
                return -var1;
            }
        } else {
            if((var3 & 2) != 0) {
                return -(var0 >> 1);
            }

            if((var3 & 32) != 0) {
                return -var0;
            }
        }

        return 0;
    }

}
