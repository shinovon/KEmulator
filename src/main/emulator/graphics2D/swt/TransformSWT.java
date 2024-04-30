package emulator.graphics2D.swt;

import emulator.graphics2D.ITransform;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Transform;

public final class TransformSWT implements ITransform {
    float[] a;

    public TransformSWT() {
        Transform var1 = new Transform((Device)null);
        this.a = new float[6];
        var1.getElements(this.a);
        var1.dispose();
    }

    public TransformSWT(Transform var1) {
        this.a = new float[6];
        var1.getElements(this.a);
    }

    public final Transform method298() {
        return new Transform((Device)null, this.a);
    }

    public final void transform(float[] arrf, int n, float[] arrf2, int n2, int n3) {
        System.arraycopy((Object)arrf, (int)n, (Object)arrf2, (int)n2, (int)(n3 << 1));
        Transform transform = new Transform(null, this.a);
        transform.transform(arrf2);
        transform.dispose();
    }

    public final void transform(ITransform t) {
        Transform t1 = new Transform(null, this.a);
        Transform t2 = ((TransformSWT)t).method298();
        t1.multiply(t2);
        t1.getElements(this.a);
        t1.dispose();
        t2.dispose();
    }

    public final ITransform newTransform(final int n, final int n2, final int n3, int n4, int n5, final int n6) {
        n4 += a(n, n2, n3, n6);
        n5 += b(n, n2, n3, n6);
        final Transform transform;
        (transform = new Transform((Device)null)).translate((float)n4, (float)n5);
        Label_0209: {
            if (n3 == 6) {
                transform.translate(0.0f, (float)n);
                transform.rotate(270f);
            }
            else {
                float n7;
                float n8;
                if (n3 == 4) {
                    transform.rotate(270f);
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
                            n7 = -1.0f;
                            n8 = 1.0f;
                        }
                        else {
                            if (n3 != 1) {
                                break Label_0209;
                            }
                            transform.translate(0.0f, (float)n2);
                            n7 = 1.0f;
                            n8 = -1.0f;
                        }
                    }
                }
                transform.scale(n7, n8);
            }
        }
        final TransformSWT a = new TransformSWT(transform);
        transform.dispose();
        return a;
    }

    private static int a(int var0, int var1, int var2, int var3) {
        if (var2 <= 3) {
            if ((var3 & 1) != 0) {
                return -(var0 >> 1);
            }

            if ((var3 & 8) != 0) {
                return -var0;
            }
        } else {
            if ((var3 & 1) != 0) {
                return -(var1 >> 1);
            }

            if ((var3 & 8) != 0) {
                return -var1;
            }
        }

        return 0;
    }

    private static int b(int var0, int var1, int var2, int var3) {
        if (var2 <= 3) {
            if ((var3 & 2) != 0) {
                return -(var1 >> 1);
            }

            if ((var3 & 32) != 0) {
                return -var1;
            }
        } else {
            if ((var3 & 2) != 0) {
                return -(var0 >> 1);
            }

            if ((var3 & 32) != 0) {
                return -var0;
            }
        }

        return 0;
    }

}
