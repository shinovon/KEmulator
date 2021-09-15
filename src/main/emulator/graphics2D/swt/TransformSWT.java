package emulator.graphics2D.swt;

import emulator.graphics2D.*;
import org.eclipse.swt.graphics.*;

public final class TransformSWT implements ITransform
{
    float[] aFloatArray528;
    
    public TransformSWT() {
        super();
        final Transform transform = new Transform((Device)null);
        transform.getElements(this.aFloatArray528 = new float[6]);
        transform.dispose();
    }
    
    public TransformSWT(final Transform transform) {
        super();
        transform.getElements(this.aFloatArray528 = new float[6]);
    }
    
    public final Transform method298() {
        return new Transform((Device)null, this.aFloatArray528);
    }
    
    public final void transform(final float[] array, final int n, final float[] array2, final int n2, final int n3) {
        System.arraycopy(array, n, array2, n2, n3 << 1);
        final Transform transform;
        (transform = new Transform((Device)null, this.aFloatArray528)).transform(array2);
        transform.dispose();
    }
    
    public final void transform(final ITransform transform) {
        final Transform transform2 = new Transform((Device)null, this.aFloatArray528);
        final Transform method298 = ((TransformSWT)transform).method298();
        final float[] array = new float[6];
        method298.getElements(array);
        final float n = (float)Math.asin(array[1]);
        final float n2 = (float)(array[0] / Math.cos(n));
        final float n3 = (float)(array[3] / Math.cos(n));
        transform2.translate(array[4], array[5]);
        transform2.scale(n2, n3);
        transform2.rotate(n);
        transform2.getElements(this.aFloatArray528);
        transform2.dispose();
        method298.dispose();
    }
    
    public final ITransform newTransform(final int n, final int n2, final int n3, int n4, int n5, final int n6) {
        n4 += method9(n, n2, n3, n6);
        n5 += method10(n, n2, n3, n6);
        final Transform transform;
        (transform = new Transform((Device)null)).translate((float)n4, (float)n5);
        Label_0209: {
            if (n3 == 6) {
                transform.translate(0.0f, (float)n);
                transform.rotate(270.0f);
            }
            else {
                Transform transform2;
                float n7;
                float n8;
                if (n3 == 4) {
                    transform.rotate(270.0f);
                    transform2 = transform;
                    n7 = -1.0f;
                    n8 = 1.0f;
                }
                else {
                    if (n3 == 5) {
                        transform.translate((float)n2, 0.0f);
                        transform.rotate(90.0f);
                        break Label_0209;
                    }
                    if (n3 == 7) {
                        transform.translate((float)n2, (float)n);
                        transform.rotate(90.0f);
                        transform2 = transform;
                        n7 = -1.0f;
                        n8 = 1.0f;
                    }
                    else {
                        if (n3 == 3) {
                            transform.translate((float)n, (float)n2);
                            transform.rotate(180.0f);
                            break Label_0209;
                        }
                        if (n3 == 2) {
                            transform.translate((float)n, 0.0f);
                            transform2 = transform;
                            n7 = -1.0f;
                            n8 = 1.0f;
                        }
                        else {
                            if (n3 != 1) {
                                break Label_0209;
                            }
                            transform.translate(0.0f, (float)n2);
                            transform2 = transform;
                            n7 = 1.0f;
                            n8 = -1.0f;
                        }
                    }
                }
                transform2.scale(n7, n8);
            }
        }
        final TransformSWT b = new TransformSWT(transform);
        transform.dispose();
        return b;
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
