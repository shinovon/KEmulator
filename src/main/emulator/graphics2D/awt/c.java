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

    public final ITransform newTransform(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
        paramInt4 += a(paramInt1, paramInt2, paramInt3, paramInt6);
        paramInt5 += b(paramInt1, paramInt2, paramInt3, paramInt6);
        AffineTransform localAffineTransform;
        (localAffineTransform = new AffineTransform()).translate(paramInt4, paramInt5);
        if (paramInt3 == 6)
        {
            localAffineTransform.translate(0.0D, paramInt1);
            localAffineTransform.rotate(-1.5707963267948966D);
        }
        else
        {
            localAffineTransform.rotate(-1.5707963267948966D);
            localAffineTransform.translate(paramInt2, 0.0D);
            localAffineTransform.rotate(1.5707963267948966D);
            localAffineTransform.translate(paramInt2, paramInt1);
            localAffineTransform.rotate(1.5707963267948966D);
            localAffineTransform.translate(paramInt1, paramInt2);
            localAffineTransform.rotate(3.141592653589793D);
            localAffineTransform.translate(paramInt1, 0.0D);
            if (paramInt3 == 1)
            {
                localAffineTransform.translate(0.0D, paramInt2);
                localAffineTransform.scale(1.0D, paramInt3 == 2 ? 1.0D : paramInt3 == 7 ? 1.0D : paramInt3 == 4 ? 1.0D : -1.0D);
            }
        }
        return new c(localAffineTransform);
    }
    
    private static int a(final int n, final int n2, final int n3, final int n4) {
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
    
    private static int b(final int n, final int n2, final int n3, final int n4) {
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
