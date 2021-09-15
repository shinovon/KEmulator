package emulator.graphics2D.awt;

import emulator.graphics2D.*;
import java.awt.geom.*;

/**
 * TransformAWT
 */
public final class c implements ITransform
{
    private AffineTransform anAffineTransform4;
    
    public c() {
        super();
        this.anAffineTransform4 = new AffineTransform();
    }
    
    public c(final AffineTransform anAffineTransform4) {
        super();
        this.anAffineTransform4 = anAffineTransform4;
    }
    
    public final AffineTransform method8() {
        return this.anAffineTransform4;
    }
    
    public final void transform(final ITransform transform) {
    }
    
    public final void transform(final float[] array, final int n, final float[] array2, final int n2, final int n3) {
        this.anAffineTransform4.transform(array, n, array2, n2, n3);
    }
    
    public final ITransform newTransform(final int n, final int n2, final int n3, int n4, int n5, final int n6) {
        n4 += method9(n, n2, n3, n6);
        n5 += method10(n, n2, n3, n6);
        final AffineTransform affineTransform;
        (affineTransform = new AffineTransform()).translate(n4, n5);
        if (n3 == 6) {
            affineTransform.translate(0.0, n);
            affineTransform.rotate(-1.5707963267948966);
        }
        else {
            AffineTransform affineTransform2;
            double n7;
            double n8;
            if (n3 == 4) {
                affineTransform.rotate(-1.5707963267948966);
                affineTransform2 = affineTransform;
                n7 = -1.0;
                n8 = 1.0;
            }
            else {
                if (n3 == 5) {
                    affineTransform.translate(n2, 0.0);
                    affineTransform.rotate(1.5707963267948966);
                    return new c(affineTransform);
                }
                if (n3 == 7) {
                    affineTransform.translate(n2, n);
                    affineTransform.rotate(1.5707963267948966);
                    affineTransform2 = affineTransform;
                    n7 = -1.0;
                    n8 = 1.0;
                }
                else {
                    if (n3 == 3) {
                        affineTransform.translate(n, n2);
                        affineTransform.rotate(3.141592653589793);
                        return new c(affineTransform);
                    }
                    if (n3 == 2) {
                        affineTransform.translate(n, 0.0);
                        affineTransform2 = affineTransform;
                        n7 = -1.0;
                        n8 = 1.0;
                    }
                    else {
                        if (n3 != 1) {
                            return new c(affineTransform);
                        }
                        affineTransform.translate(0.0, n2);
                        affineTransform2 = affineTransform;
                        n7 = 1.0;
                        n8 = -1.0;
                    }
                }
            }
            affineTransform2.scale(n7, n8);
        }
        return new c(affineTransform);
    }
    
    private static int method9(final int n, final int n2, final int n3, final int n4) {
        if (n3 <= 3) {
            if ((n4 & 0x1) != 0x0) {
                return -(n >> 1);
            }
            if ((n4 & 0x8) != 0x0) {
                return -n;
            }
        }
        else {
            if ((n4 & 0x1) != 0x0) {
                return -(n2 >> 1);
            }
            if ((n4 & 0x8) != 0x0) {
                return -n2;
            }
        }
        return 0;
    }
    
    private static int method10(final int n, final int n2, final int n3, final int n4) {
        if (n3 <= 3) {
            if ((n4 & 0x2) != 0x0) {
                return -(n2 >> 1);
            }
            if ((n4 & 0x20) != 0x0) {
                return -n2;
            }
        }
        else {
            if ((n4 & 0x2) != 0x0) {
                return -(n >> 1);
            }
            if ((n4 & 0x20) != 0x0) {
                return -n;
            }
        }
        return 0;
    }
}
